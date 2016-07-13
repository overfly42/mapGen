package chaseGenerator.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import chaseGenerator.TrapType;
import chaseGenerator.data.EnvData;
import chaseGenerator.data.Field;
import chaseGenerator.data.FieldObject;
import chaseGenerator.models.ObjectModel;
import chaseGenerator.models.TerrainModel;

public class FieldPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498728393060340188L;

	private static final int NORTH = 0;
	private static final int EAST = 1;
	private static final int SOUTH = 2;
	private static final int WEST = 3;

	private int w, h, pxw, pxh, xOff, yOff;
	private Field data;
	private Random r;
	private myPoint myPos;
	private myPoint yourPos;
	private EnvData envoirment;
	private SidePanel sp;

	private class myPoint {
		int x = 7;
		int y = 0;

		public myPoint() {
		};

		public myPoint(int X, int Y) {
			x = X;
			y = Y;
		}
	}

	public FieldPanel() {

		this.setPreferredSize(new Dimension(500, 500));
		this.setOpaque(true);
		r = new Random();
		data = new Field();
		myPos = new myPoint();
		yourPos = new myPoint();

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);

	}

	public Field getData() {
		return data;
	}

	public void setData(Field d) {
		data = d;
		repaint();
	}

	public void setEnv(EnvData ed) {
		if (ed == null)
			envoirment = new EnvData();
		envoirment = ed;
	}

	@Override
	public void paint(Graphics g) {

		w = this.getWidth();
		h = this.getHeight();
		pxw = w / data.getFields();
		pxh = h / data.getFields();
		xOff = (w - pxw * data.getFields()) / 2;
		yOff = (h - pxh * data.getFields()) / 2;
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++) {
				TerrainModel ft = data.get(i, n).getArea();
				if (ft == null)
					g.setColor(Color.WHITE);
				else {
					Color c = ft.getColor();
					if (c != null)
						g.setColor(c);
				}
				// choose color for hunter and hunted
				if (i == myPos.x && n == myPos.y)
					g.setColor(Color.CYAN);
				else if (i == yourPos.x && n == yourPos.y)
					g.setColor(Color.WHITE);
				// Paint actual Area
				g.fillRect(xOff + pxw * i, yOff + pxh * n, pxw, pxh);
				g.setColor(Color.BLACK);
				g.drawRect(xOff + pxw * i, yOff + pxh * n, pxw, pxh);
				// Paint indication if Objects exist
				if (data.get(i, n).hasObjects()) {
					g.setColor(ft.getInverseColor());
					g.drawLine(xOff + pxw * i, yOff + pxh * n, xOff + pxw * (i + 1), yOff + pxh * (n + 1));
					g.drawLine(xOff + pxw * (i + 1), yOff + pxh * n, xOff + pxw * i, yOff + pxh * (n + 1));
				}
			}
	}

	public String getToolTipText(MouseEvent me) {
		int x = me.getX() - xOff;
		int y = me.getY() - yOff;
		int xPos = x / pxw;
		int yPos = y / pxh;
		xPos = Math.min(xPos, data.getFields() - 1);
		yPos = Math.min(yPos, data.getFields() - 1);
		String str;
		str = "Pos: x: " + (xPos + 1) + " Y: " + (yPos + 1);
		if (xPos < 0 || yPos < 0 || data.get(xPos, yPos) == null)
			return str;
		FieldObject fo = data.get(xPos, yPos);
		if (fo == null)
			return str;
		if (fo.getArea() == null)
			return str;
		str += "<br>" + "Gelände: " + fo.getArea().getName();
		for (String s : fo.objects) {
			str += "<br>" + s;
		}
		return "<html>" + str + "</html>";

	}

	public void click(MouseEvent e) {
		final int x = (e.getX() - xOff) / pxw;
		final int y = (e.getY() - yOff) / pxh;
		if (e.getButton() == MouseEvent.BUTTON1) {
			JPopupMenu jpm = new JPopupMenu();
			JMenuItem jmi;
			jmi = new JMenuItem("hunted");
			myPoint p = new myPoint(x, y);
			jmi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					yourPos = new myPoint(x, y);
					repaint();

				}
			});
			jpm.add(jmi);
			jmi = new JMenuItem("hunter");
			jmi.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					myPos = new myPoint(x, y);
					repaint();

				}
			});
			jpm.add(jmi);
			jpm.show(this, e.getX(), e.getY());
		} else {
			fillSingleTerrainManuall(x, y, e.getX(), e.getY());
		}

		repaint();
	}

	/**
	 * Creates a randomly generated field Elements are choosen with respect
	 * config from the terrain view
	 */
	public void generateRandomTerrain() {
		data.reCreateField(data.getFields());
		placeBorder();
		placeDestination();
		placeSpecial();
		fillEmptyTerrain();
		cleanUnallowedFields();
		// fillEmptyTerrain();
		// cleanUnallowedFields(checkField());
		setSurvilvalSklills();
		setPerceptionSkills();
		// setAbilitySkills();
		setTraps();
		repaint();
	}

	/**
	 * Creates a nearly randomy created field Number of Elements of each type a
	 * determined by the config view Also this method takes care to not void the
	 * contraints from the terrain view
	 */
	public void generateConfigTerrain() {
		data.reCreateField(data.getFields());
		placeBorder();
		placeSpecial();
		// Get allready used elements
		Map<String, Integer> used = new HashMap<>();
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++) {
				TerrainModel tm = data.get(i, n).getArea();
				if (tm == null)
					continue;
				Integer val = used.get(tm.getName());
				if (val == null)
					val = 0;
				val++;
				used.put(tm.getName(), val);
			}
		// Generate a List of all usable Terrains
		List<TerrainModel> ltm = new ArrayList<>();
		for (TerrainModel tm : envoirment.fields) {
			String s = tm.getName();// TerrainModel to Insert
			int amount = data.getNumberOfFields(s);// Number of Areas of this
													// type
			Integer allreadyInserted = used.get(s);// Get Number of allready
													// inserted elements
			if (allreadyInserted == null) // Care for valid data
				allreadyInserted = 0;
			amount = Math.max(0, amount - allreadyInserted);// reduce the number
															// of Elements to
															// insert, but be
															// positive
			for (int i = 0; i < amount; i++)
				ltm.add(tm);

		}

		fillEmptyTerrain(ltm);
		repaint();
	}

	private void placeDestination() {
		TerrainModel tm = null;
		for (TerrainModel t : envoirment.fields) {
			if (t.isChoosen() && t.isDestination())
				tm = t;
		}
		if (tm == null)
			return;
		int size = data.getFields();
		FieldObject fo = new FieldObject();
		int n = Math.max(1, tm.getAreas());
		int trys = 0;
		while (n > 0 && trys++ < 100) {
			int x = r.nextInt(size / 2) + size / 4;
			int y = r.nextInt(size / 2) + size / 4;
			System.out.println("Setting dest to " + x + " " + y);
			fo.setArea(tm);
			if (data.get(x, y).getArea() != null)
				continue;
			data.setFieldAt(fo, x, y);
			n--;
		}

	}

	private void placeBorder() {
		TerrainModel tm = null;
		for (TerrainModel t : envoirment.fields)
			if (t.isBorder())
				tm = t;
		if (tm == null)
			return;
		int max = data.getFields();
		for (int i = 0; i < max; i++) {
			FieldObject fo = new FieldObject();
			fo.setArea(tm);
			data.setFieldAt(fo, max - 1, i);
			data.setFieldAt(fo, 0, i);
			data.setFieldAt(fo, i, 0);
			data.setFieldAt(fo, i, max - 1);
		}
	}

	private void placeSpecial() {
		List<TerrainModel> ltm = new ArrayList<>();
		for (TerrainModel tm : envoirment.fields)
			if (tm.getAreas() > 0 && !tm.isDestination())
				ltm.add(tm);
		int size = data.getFields();
		for (TerrainModel tm : ltm) {
			int max = tm.getAreas();
			int n = 0;
			while (n++ < max) {
				int x = r.nextInt(size - 2) + 1;// Do not choose the border
				int y = r.nextInt(size - 2) + 1;
				if (data.get(x, y).getArea() != null)
					continue;
				insertSpecialRegion(tm, tm, x, y);
			}
		}
	}

	/**
	 * inserts a region of the given Terrain including its borders
	 * 
	 * @param tm
	 * @param x
	 * @param y
	 */
	private void insertSpecialRegion(TerrainModel org, TerrainModel tm, int x, int y) {
		if (!tm.isChoosen())
			return;
		data.get(x, y).setArea(tm);
		if (org != tm)
			return;
		int X[] = new int[4];
		int Y[] = new int[4];
		// Manhatten Metrik
		X[0] = x;
		X[1] = x;
		X[2] = x - 1;
		X[3] = x + 1;
		Y[0] = y + 1;
		Y[1] = y - 1;
		Y[2] = y;
		Y[3] = y;
		for (int i = 0; i < 4; i++) {
			if (data.get(X[i], Y[i]).getArea() != null)
				continue;
			String nextTerrain = org.getAreaNameOf(r.nextInt(100));// 100 %

			if (nextTerrain == null)
				continue;
			insertSpecialRegion(org, envoirment.getModel(nextTerrain), X[i], Y[i]);
		}
	}

	/**
	 * 
	 * @return a List of Fields that do not pass the surronings check
	 */
	private List<myPoint> checkField() {
		List<myPoint> l = new ArrayList<>();
		int max = data.getFields() - 1;
		for (int x = 1; x < max; x++)
			for (int y = 1; y < max; y++)
				if (!checkField(x, y))
					l.add(new myPoint(x, y));
		return l;
	}

	private boolean checkField(int x, int y) {
		TerrainModel tm = data.get(x, y).getArea();
		if (tm == null || tm.isDestination())
			return true;
		TerrainModel[] adjected = new TerrainModel[4];
		adjected[0] = data.get(x + 1, y).getArea();
		adjected[1] = data.get(x - 1, y).getArea();
		adjected[2] = data.get(x, y + 1).getArea();
		adjected[3] = data.get(x, y - 1).getArea();
		for (TerrainModel adj : adjected) {
			if (adj == null || adj.isDestination())
				continue;
			if (adj.isAdjectableTo(tm.getName()) && tm.isAdjectableTo(adj.getName()))
				continue;
			return false;
		}
		return true;
	}

	private void cleanUnallowedFields() {
		List<myPoint> lmp = checkField();
		for (myPoint mp : lmp) {
			List<TerrainModel> at = getAllowedTerrains(mp.x, mp.y);
			if (at.size() > 0)
				data.get(mp.x, mp.y).setArea(at.get(r.nextInt(at.size())));
			else
				data.get(mp.x, mp.y).setArea(null);
		}
	}

	private void fillEmptyTerrain() {

		int max = data.getFields() - 1;
		for (int x = 1; x < max; x++) {
			for (int y = 1; y < max; y++)
				if (data.get(x, y).getArea() == null) {
					List<TerrainModel> allowed = getAllowedTerrains(x, y);

					if (allowed.isEmpty())
						continue;
					TerrainModel tm = allowed.get(r.nextInt(allowed.size()));

					data.get(x, y).setArea(tm);
				}
		}
	}

	private void fillEmptyTerrain(List<TerrainModel> ltm) {
		Random r = new Random();
		boolean mapNotFilled = true;
		int runs = 0;
		int maxRuns = data.getFields() * data.getFields() * 10;
		while(mapNotFilled&&runs++<maxRuns)
		{
			//find empty Field
		}

		// // Walk through the whole field
		// for (int i = 0; i < data.getFields(); i++)
		// for (int n = 0; n < data.getFields(); n++) {
		// if (ltm.size() == 0)
		// return;
		// if (data.get(i, n).getArea() != null)
		// continue;
		// List<TerrainModel> allowedTerrains = getAllowedTerrains(i, n);
		// int runs = 0;
		// TerrainModel tmFound = null;
		// int rand = 0;
		// while (tmFound == null && runs++ < 100) {
		// rand = r.nextInt(ltm.size());
		// if (allowedTerrains.contains(ltm.get(rand)))
		// tmFound = ltm.get(rand);
		// }
		// if (runs == 100)
		// System.out.println("Didn't find a match");
		// if (tmFound == null)
		// continue;
		// data.get(i, n).setArea(tmFound);
		// ltm.remove(rand);
		// }
		// System.out.println("Left " + ltm.size() + " entrys in list");

	}
	/**
	 * Gets a random field from the map, or zero if no one fits the parameter
	 * @param emtpy true if the field should be empty, or false if it should have a terrain
	 * @return
	 */
	private myPoint getRandomField(boolean emtpy)
	{
		return null;
	}

	private void fillSingleTerrainManuall(final int x, final int y, int mouseX, int mouseY) {
		JPopupMenu jpm = new JPopupMenu();
		List<TerrainModel> ltm = getAllowedTerrains(x, y);
		JMenu allowedTypes = new JMenu("Allowed Terrain types");
		for (final TerrainModel tm : ltm) {
			JMenuItem jmi = new JMenuItem(tm.getName());
			jmi.addActionListener(new ActionListener() {
				TerrainModel save = tm;

				@Override
				public void actionPerformed(ActionEvent e) {
					data.get(x, y).setArea(save);
					repaint();
				}
			});
			allowedTypes.add(jmi);
		}
		JMenu allTypes = new JMenu("All Terrain types");
		for (final TerrainModel tm : envoirment.fields) {
			JMenuItem jmi = new JMenuItem(tm.getName());
			jmi.addActionListener(new ActionListener() {
				TerrainModel save = tm;

				@Override
				public void actionPerformed(ActionEvent e) {
					reorganize(x, y, save);
					repaint();
				}
			});
			allTypes.add(jmi);

		}
		JMenu allowedObjects = new JMenu("Allowed Objects");
		final FieldObject areaType = data.get(x, y);
		for (final ObjectModel om : envoirment.objects)
			if (om.isAllowedTo(areaType.getArea().getName())) {
				JMenuItem jmi = new JMenuItem(om.getName());
				jmi.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						areaType.addObject(om.getName());
						repaint();
					}
				});
				allowedObjects.add(jmi);
			}
		JMenu activeObjects = null;
		if (areaType.hasObjects()) {
			activeObjects = new JMenu("Remove Objects");
			for (final String s : areaType.getObjects()) {
				JMenuItem jmi = new JMenuItem("remove " + s);
				jmi.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						areaType.getObjects().remove(s);
						repaint();
					}
				});
				activeObjects.add(jmi);
			}
		}
		jpm.add(allowedTypes);
		jpm.add(allTypes);
		jpm.addSeparator();
		jpm.add(allowedObjects);
		if (activeObjects != null)
			jpm.add(activeObjects);
		jpm.show(this, mouseX, mouseY);
	}

	/**
	 * Sets this filed to the given terrain model, and reorganizes all
	 * surrouning fields
	 * 
	 * @param x
	 * @param y
	 * @param tm
	 */
	private void reorganize(int x, int y, TerrainModel tm) {
		System.out.println("Reorganizing");
		data.get(x, y).setArea(tm);
		List<myPoint> points = checkField();
		System.out.println("Number of Points not allowed " + points.size());
		for (myPoint mp : points) {
			System.out.println(mp.x + " " + mp.y);
			if (mp.x == x && mp.y == y)
				continue;
			data.get(mp.x, mp.y).setArea(null);
		}
		fillEmptyTerrain();
	}

	/**
	 * Caclulates all Terrais that are allowed (at the moment) for this
	 * coordinates
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private List<TerrainModel> getAllowedTerrains(int x, int y) {
		List<TerrainModel> res = new ArrayList<>();
		if (x < 1 || y < 1 || x > data.getFields() - 1 || y > data.getFields() - 1)
			return res;
		// Adding all possible Terrais to a list
		for (TerrainModel tm : envoirment.fields) {
			if (tm.isDestination())
				continue;
			if (tm.getAreas() > 0)
				continue;
			if (tm.isChoosen())
				res.add(tm);
		}
		// Remove Terrains not allowd by surrondings
		List<TerrainModel> del = new ArrayList<>();

		for (TerrainModel tm : res) {
			for (int i = 0; i < 4; i++) {
				TerrainModel tmp = getArea(i, x, y);
				if (tmp == null)
					continue;
				if (!tmp.isAdjectableTo(tm.getName()) && !tmp.isDestination())
					del.add(tm);
			}
		}
		res.removeAll(del);
		return res;
	}

	private void setSurvilvalSklills() {
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++)
				data.get(i, n).survival = r.nextInt(10) + 5;
	}

	private void setPerceptionSkills() {
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++)
				data.get(i, n).perception = r.nextInt(10) + 5;
	}

	private void setTraps() {
		int max = 4;
		TrapType[] tt = new TrapType[max];
		tt[0] = TrapType.Loch;
		tt[1] = TrapType.Speere;
		tt[2] = TrapType.Schlinge;
		tt[3] = TrapType.Leer;
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++) {
				// if (data.get(i, n).getArea() == FieldType.Ziel)
				// continue;
				// if (data.get(i, n).getArea() == FieldType.Fluss)
				// continue;
				if (r.nextInt(10) != 0)
					continue;
				data.get(i, n).setTrap(tt[r.nextInt(max)]);
			}
	}

	private TerrainModel getArea(int dir, int x, int y) {
		switch (dir) {
		case NORTH:
			if (y == 0)
				return null;
			return data.get(x, y - 1).getArea();
		case EAST:
			if (x > data.getFields())
				return null;
			return data.get(x + 1, y).getArea();
		case SOUTH:
			if (y > data.getFields())
				return null;
			return data.get(x, y + 1).getArea();
		case WEST:
			if (y == 0)
				return null;
			return data.get(x - 1, y).getArea();
		default:
			return null;
		}
	}
}
