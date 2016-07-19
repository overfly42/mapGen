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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

		public String toString() {
			return "[X: " + x + " Y: " + y + "]";
		}
	}

	/**
	 * This Element of a tree contains a reference to its parrent and to its
	 * children
	 * 
	 * @author christian
	 *
	 */
	private class TreeElement {
		private List<TreeElement> children;
		private TreeElement parrent;
		private String elementName;

		/**
		 * leaf or node of the tree
		 * 
		 * @param parrent
		 *            could be null, but only in the root
		 * @param name
		 *            name of this element, got from the Terrain Model
		 *            represented by this element
		 */
		public TreeElement(TreeElement parrent, String name) {
			this.parrent = parrent;
			elementName = name;
			children = new ArrayList<>();
		}

		/**
		 * Use this Constructor for the creation of the root element
		 * 
		 * @param ed
		 */
		public TreeElement(EnvData ed, String name) {
			children = new ArrayList<>();
			elementName = name;
			TerrainModel tm = ed.getModel(name);
			children.add(new TreeElement(this, tm.getName()));
			addChildren(ed, tm);

		}

		/**
		 * Should only be called by the constructor to generate the
		 * 
		 * @param ed
		 * @param tm
		 */
		public void addChildren(EnvData ed, TerrainModel tm) {
			if (!tm.isChoosen())
				return;
			for (TerrainModel tm2 : ed.fields) {
				// This element don't need to be inserted in the tree
				if (tm2.getName().equals(tm.getName()))
					continue;
				if (tm.getPropability(tm2.getName()) == 0)
					continue;
				if (!containsUp(tm2.getName())) {
					TreeElement te = new TreeElement(this, tm2.getName());
					children.add(te);
					te.addChildren(ed, tm2);
				}
			}
		}

		/**
		 * searches from this node upwards
		 * 
		 * @param s
		 *            name of element to search for
		 * @return true if this element or one of its parents is equal to s
		 */
		public boolean containsUp(String s) {
			if (s.equals(elementName))
				return true;
			if (parrent != null && parrent.containsUp(s))
				return true;
			return false;
		}

		/**
		 * searches from this node downwards
		 * 
		 * @param s
		 * @return
		 */
		public int cotainsDown(String s) {
			int level = 0;
			for (TreeElement te : children) {
				if (te.elementName.equals(s))
					return 1;
			}
			for (TreeElement te : children) {
				int lvl = te.cotainsDown(s);
				level = Math.max(lvl, level);

			}
			return level + 1;
		}

		/**
		 * calculates the level above and below this element including this one
		 * 
		 * @param direction
		 *            if true searches its parents, otherwise the children
		 * @return amount of level above or below
		 */
		public int getLevel(boolean direction) {
			int level = 0;
			if (direction) {
				if (parrent != null)
					level += parrent.getLevel(direction);
			} else {
				for (TreeElement te : children)
					level = Math.max(te.getLevel(direction), level);
			}
			return level + 1;
		}

		/**
		 * returns the level wich contains the count of elements given as
		 * argument -1 if there is no such level
		 */
		public int getLevel(int count) {
			if (count < 0)
				return -1;
			int max = getLevel(false);
			for (int i = 0; i < max; i++)
				if (getElementsInDeep(i).size() >= count)
					return i;
			return -1;
		}

		/**
		 * 
		 * @param level
		 * @return
		 */
		public Set<String> getElementsInDeep(int level) {
			Set<String> elements = new HashSet<>();
			// if (level == 0)
			elements.add(elementName);
			// else {
			if (level > 0)
				for (TreeElement te : children)
					elements.addAll(te.getElementsInDeep(level - 1));
			// }
			return elements;
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

	public void generateRandomTerrain() {
		// Prepair field
		data.reCreateField(data.getFields());
		// prepair data
		Map<String, TreeElement> allowedTerrainTrees = getAllowedTerrainTrees();
		Map<String, Integer> effectiveTreeDeepth = getEffectivTreeDeepth(allowedTerrainTrees);
		// Place fields
		placeBorder();
		placeRandomSpecial();
		fillEmptyTerrains(effectiveTreeDeepth, allowedTerrainTrees);
		// no comment
		repaint();
	}

	public void generateConfigTerrain() {
		placeBorder();
		repaint();
	}

	private void fillEmptyTerrains(Map<String, Integer> effectiveTreeDeep, Map<String, TreeElement> terrainTrees) {
		Random r = new Random();
		// border is allready set, becouse of this start at 1
		for (int x = 1; x < data.getFields() - 1; x++)
			for (int y = 1; y < data.getFields() - 1; y++) {
				if (data.get(x, y).getArea() != null) // if there is an terrain
														// do nothing
					continue;
				// get an Terrain Element in x or y direction
				TerrainModel base = null;
				int tmpX = x;
				int tmpY = y;
				if (r.nextBoolean())
					tmpX -= 1;
				else
					tmpY -= 1;
				base = data.get(tmpX, tmpY).getArea();// is border or allready
														// set
				if (base == null) // if it is null, there is an error
				{
					base = envoirment.getRandomTerrain();
//					System.out.println("Random terrain is " + base.getName());
				}
				String nextArea = null;
				List<TerrainModel> allowedTerrains = null;
				int max = 10;
				int run = 0;
				do {
					nextArea = base.getAreaNameOf(r.nextInt(100));
					allowedTerrains = getAllowedTerrains(x, y);
				} while (run++ < max// dont run forever
						// Conditions to run further
						// Element  has  to be  choosen
						&& !envoirment.getModel(nextArea).isChoosen()
						
						// Element  must be allowed
						&& !contains(allowedTerrains, nextArea));
				if (run == max + 1)
					continue;
				data.get(x, y).setArea(envoirment.getModel(nextArea));
//				System.out.println(run + " Next is " + nextArea + " " + envoirment.getModel(nextArea).getAreas());

			}
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
			if (areaType.getArea() == null || om.isAllowedTo(areaType.getArea().getName()))

			{
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
		if (areaType.hasObjects())

		{
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
				TerrainModel tmp;
				tmp = getArea(i, x, y);
				if (tmp == null)
					continue;
				if (!tmp.isAdjectableTo(tm.getName()) && !tmp.isDestination())
					del.add(tm);
			}
		}
		res.removeAll(del);
		return res;
	}

	/**
	 * Gets a List of all allowed Terrains for this field, effectiveDeep and mst
	 * should have corosponding keys
	 * 
	 * @param x
	 *            center x coordinate
	 * @param y
	 *            center y coordinate
	 * @param effectiveDeep
	 *            map for effective deeps of the searched trees
	 * @param mst
	 *            map of trees with connections resolved
	 * @return
	 */
	private List<TerrainModel> getAllowedTerrains(int x, int y, Map<String, Integer> effectiveDeep,
			Map<String, TreeElement> mst) {
		List<TerrainModel> ltm = new ArrayList<>();// return value
		for (String key : effectiveDeep.keySet()) {// effective
			int deep = effectiveDeep.get(key);
			TreeElement te = mst.get(key);
			boolean checkOK = true;
			for (int i = 1; i <= deep; i++) {// distance == 0 is this field
				Set<String> ss = getElementsInDistance(x, y, i);
				if (ss.isEmpty()) // In case of an empty region all is posible
					continue;
				for (String s : ss)// Check every field returned that it does
									// not need more fields between the checked
									// field(x,y) and itself
					if (i < te.cotainsDown(s))
						checkOK = false;
			}
			if (checkOK)
				ltm.add(envoirment.getModel(key));
		}
		return ltm;
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
	}

	private TerrainModel getArea(int dir, int x, int y) {
		switch (dir) {
		case NORTH:
			if (y == 0)
				return null;
			return data.get(x, y - 1).getArea();
		case EAST:
			if (x > data.getFields() - 2)
				return null;
			return data.get(x + 1, y).getArea();
		case SOUTH:
			if (y > data.getFields() - 2)
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

	private void placeSpecial(TerrainModel tm, int count) {
		Random r = new Random();
		for (int i = 0; i < count; i++) {
			// get a position within the field
			int x;
			int y;
			int counter = 0;
			do {
				x = r.nextInt(data.getFields() - 2) + 1;
				y = r.nextInt(data.getFields() - 2) + 1;
			} while (counter++ < 100 && data.get(x, y).getArea() != null);
			createArea(x, y, data.getFields() / 4, tm);
		}
	}

	/**
	 * Sets the explicit given number of terrains
	 */
	private void placeRandomSpecial() {
		for (TerrainModel tm : envoirment.fields) {
			int size = tm.getAreas();
			placeSpecial(tm, size);
		}
	}

	private void placeConfigSpecial() {

	}

	private void createArea(int x, int y, int max, TerrainModel tm) {
		if (max <= 0)
			return;
		max--;
		data.get(x, y).setArea(tm);
		int i = 0;
		int d = r.nextInt(4);
		if (y > 1)
			createArea(x, y - 1, max--, tm);
		if (y < data.getFields() - 1)
			createArea(x, y + 1, max--, tm);
		if (x < data.getFields() - 1)
			createArea(x + 1, y, max--, tm);
		if (x > 1)
			createArea(x - 1, y, max--, tm);
		if (data.get(x, y).getArea() != null)
			return;

	}

	private boolean contains(List<TerrainModel> ltm, TerrainModel tm) {
		if (ltm.contains(tm))
			return true;
		for (TerrainModel t : ltm) {
			System.out.println(tm.getName() + " " + t.getName());
			if (t.getName().equals(tm.getName()))
				return true;
		}
		return false;
	}

	private boolean contains(List<TerrainModel> ltm, String terrainName) {
		for (TerrainModel tm : ltm)
			if (tm.getName().equals(terrainName))
				return true;
		return false;
	}

	private Map<String, TreeElement> getAllowedTerrainTrees() {
		Map<String, TreeElement> mst = new HashMap<>();
		for (TerrainModel tm : envoirment.fields) {
			if (tm.isChoosen()) {
				TreeElement te = new TreeElement(envoirment, tm.getName());
				mst.put(tm.getName(), te);
			}
		}
		return mst;
	}

	/**
	 * Calculates a Map with the effective Search deepth for every tree element
	 * of the map mst
	 * 
	 * @param mst
	 * @return
	 */
	private Map<String, Integer> getEffectivTreeDeepth(Map<String, TreeElement> mst) {
		Map<String, Integer> value = new HashMap<>();
		for (String s : mst.keySet()) {
			TreeElement te = mst.get(s);
			int lvl = mst.get(s).getLevel(envoirment.fields.size());
			if (lvl == -1)
				lvl = mst.get(s).getLevel(false);
			value.put(s, lvl);
		}
		return value;
	}

	/**
	 * Returns all Elements in a distance of to from x,y, cityblock metrik
	 * 
	 * @param x
	 * @param y
	 * @param to
	 * @return
	 */
	private Set<String> getElementsInDistance(int x, int y, int to) {
		Set<String> ss = new HashSet<>();
		for (int i = 0; i <= to; i++) {
			int nx = to - i;// Movement along x axis
			int ny = i;// movement along y axis
			TerrainModel tm = null;
			// Prevent an index out of bounds exception
			// Prevent NullPointerException
			if (x + nx < data.getFields() && y + ny < data.getFields())
				tm = data.get(x + nx, y + ny).getArea();
			if (tm != null)
				ss.add(tm.getName());
			if (x + nx < data.getFields() && y - ny >= 0)
				tm = data.get(x + nx, y - ny).getArea();
			if (tm != null)
				ss.add(tm.getName());
			if (x - nx >= 0 && y + ny < data.getFields())
				tm = data.get(x - nx, y + ny).getArea();
			if (tm != null)
				ss.add(tm.getName());
			if (x - nx >= 0 && y - ny >= 0)
				tm = data.get(x - nx, y - ny).getArea();
			if (tm != null)
				ss.add(tm.getName());

		}
		// for (int i = -to; i <= to; i++) {
		// int nx = x + i;
		// int ny1 = y + (to - Math.abs(i));
		// int ny2 = y - (to - Math.abs(i));
		// for (int n = 0; n < to - Math.abs(i); n++)
		// System.out.print(".");
		// for (int n = 0; n <= 2*Math.abs(i); n++)
		// System.out.print("-");
		// for (int n = 0; n < to - Math.abs(i); n++)
		// System.out.print(".");
		// System.out.println();
		// if (0 > nx || nx > data.getFields())
		// continue;
		// if (0 <= ny1 && ny1 < data.getFields()) {
		// TerrainModel tm = data.get(nx, ny1).getArea();
		// if (tm != null)
		// ss.add(tm.getName());
		// }
		// if (0 <= ny2 && ny2 < data.getFields()) {
		// TerrainModel tm = data.get(nx, ny2).getArea();
		// if (tm != null)
		// ss.add(tm.getName());
		// }
		// }
		return ss;
	}

	private Set<String> getSorounndingElements(int x, int y) {
		Set<String> soroundings = new HashSet();
		for (int i = 0; i < 4; i++) {
			TerrainModel tm = getFieldIn(x, y, i);
			if (tm == null)
				continue;
			soroundings.add(tm.getName());
		}
		return soroundings;
	}

	/**
	 * Returns a Field in the given direction, or null if there is no more
	 * 
	 * @param direction
	 * @return
	 */
	private TerrainModel getFieldIn(int x, int y, int direction) {
		switch (direction) {
		case NORTH:
			if (y <= 0)
				break;
			return data.get(x, y - 1).getArea();
		case SOUTH:
			if (y >= data.getFields() - 1)
				break;
			return data.get(x, y + 1).getArea();
		case EAST:
			if (x >= data.getFields() - 1)
				break;
			return data.get(x + 1, y).getArea();
		case WEST:
			if (x <= 0)
				break;
			return data.get(x - 1, y).getArea();
		default:
			return null;
		}
		return null;
	}
}
