package chaseGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

public class FieldPanel extends JPanel implements ActionListener {

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
		initData();

		ToolTipManager.sharedInstance().registerComponent(this);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		this.setToolTipText("Hallo WElt");

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
				if (i == myPos.x && n == myPos.y)
					g.setColor(Color.CYAN);
				else if (i == yourPos.x && n == yourPos.y)
					g.setColor(Color.WHITE);
				g.fillRect(xOff + pxw * i, yOff + pxh * n, pxw, pxh);
				g.setColor(Color.BLACK);
				g.drawRect(xOff + pxw * i, yOff + pxh * n, pxw, pxh);
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
		// str += "<br>" + "Überlebenskunst(mod): " + fo.survival;
		// str += "<br>" + "Wahrnehmung(mod): " + fo.perception;
		// str += "<br>" + "Norden (SG " + fo.nextField[NORTH][0].sg + "): " +
		// fo.nextField[NORTH][0].type.toString();
		// str += "<br>" + " (SG " + fo.nextField[NORTH][1].sg + "): " +
		// fo.nextField[NORTH][1].type.toString();
		// str += "<br>" + "Osten (SG " + fo.nextField[EAST][0].sg + "): " +
		// fo.nextField[EAST][0].type.toString();
		// str += "<br>" + " (SG " + fo.nextField[EAST][1].sg + "): " +
		// fo.nextField[EAST][1].type.toString();
		// str += "<br>" + "Süden (SG " + fo.nextField[SOUTH][0].sg + "): " +
		// fo.nextField[SOUTH][0].type.toString();
		// str += "<br>" + " (SG " + fo.nextField[SOUTH][1].sg + "): " +
		// fo.nextField[SOUTH][1].type.toString();
		// str += "<br>" + "Westen (SG " + fo.nextField[WEST][0].sg + "): " +
		// fo.nextField[WEST][0].type.toString();
		// str += "<br>" + " (SG " + fo.nextField[WEST][1].sg + "): " +
		// fo.nextField[WEST][1].type.toString();
		// if (fo.getTrap() != null)
		// str += "<br>" + "Falle: " + fo.getTrap().toString();
		// "<html>X: " + xPos + "<br> Y: " + yPos+"</html>";
		return "<html>" + str + "</html>";

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		generateTerrain();
	}

	public void click(MouseEvent e) {
		int x = (e.getX() - xOff) / pxw;
		int y = (e.getY() - yOff) / pxh;
		// myPoint pos;
		if (e.getButton() == MouseEvent.BUTTON1) {
			// pos = myPos;
			List<TerrainModel> ltm = getAllowedTerrains(x, y);
			System.out.println("Allowed is : ");
			for (TerrainModel tm : ltm)
				System.out.println("\t" + tm.getName());
		} else {
			// pos = yourPos;
			fillSingleTerrainManuall(x, y, e.getX(), e.getY());
		}

		// pos.x = x;
		// pos.y = y;

		repaint();
	}

	private void generateTerrain() {
		initData();
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

	private void initData() {
		data.reCreateField(30);
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

		while (n > 0) {
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

	private void fillSingleTerrainManuall(int x, int y, int mouseX, int mouseY) {
		JPopupMenu jpm = new JPopupMenu();
		List<TerrainModel> ltm = getAllowedTerrains(x, y);
		for (TerrainModel tm : ltm) {

			JMenuItem jmi = new JMenuItem(tm.getName());
			jmi.addActionListener(new ActionListener() {
				TerrainModel save = tm;

				@Override
				public void actionPerformed(ActionEvent e) {
					data.get(x, y).setArea(save);
					System.out.println(save.getName());
					repaint();
				}
			});
			jpm.add(jmi);
		}

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
		for (TerrainModel tm : res) {
			for (int i = 0; i < 4; i++) {
				TerrainModel tmp = data.get(X[i], Y[i]).getArea();
				if (tmp == null)
					continue;
				if (!tmp.isAdjectableTo(tm.getName()) && !tmp.isDestination())
					del.add(tm);
			}
		}
		res.removeAll(del);
		return res;
	}
	// private void placeDestination() {
	//// int halfField = data.getFields() / 2;
	//// int x = halfField + r.nextInt(halfField);
	//// int y = halfField + r.nextInt(halfField);
	//// data.get(x, y).setArea(FieldType.Ziel);
	// }
	//
	// private void placeBoarder() {
	//// for (int i = 0; i < data.getFields(); i++)
	//// data.get(i, 0).setArea(FieldType.Wald);
	// }
	//
	// private void placeRiver() {
	//// int maxRiver = data.getFields();
	//// int numRiver = 0;
	//// // find Start
	//// int x;
	//// int y;
	//// boolean startfound = false;
	//// do {
	//// x = r.nextInt(data.getFields());
	//// y = r.nextInt(data.getFields());
	//// FieldType ft = data.get(x, y).getArea();
	//// startfound = ft == null;
	//// } while (!startfound);
	//// data.get(x, y).setArea(FieldType.Fluss);
	//// int[] mod = { -1, 0, +1 };
	//// int trys = 0;
	//// while (numRiver < maxRiver && trys < 10) {
	//// int modx = mod[r.nextInt(3)];
	//// int mody = modx == 0 ? mod[r.nextInt(3)] : 0;
	//// if (x + modx < 0 || x + modx >= data.getFields() || y + mody < 0 || y +
	// mody >= data.getFields()) {
	//// trys++;
	//// continue;
	//// }
	//// if (data.get(x + modx, y + mody).getArea() != null) {
	//// trys++;
	//// continue;
	//// }
	//// x += modx;
	//// y += mody;
	//// data.get(x, y).setArea(FieldType.Fluss);
	//// numRiver++;
	//// }
	// }
	//
	// private void fillEmptyTerrain() {
	////
	//// // fill up to 10% random
	//// int max = (data.getFields() * data.getFields()) / 10;
	//// for (int i = 0; i < max; i++) {
	//// int x = r.nextInt(data.getFields());
	//// int y = r.nextInt(data.getFields());
	//// if (data.get(x, y).getArea() == null)
	//// data.get(x, y).setArea(getRandomTerrain());
	//// }
	////
	//// // fill rest by rules
	//// for (int i = 0; i < data.getFields(); i++)
	//// for (int n = 0; n < data.getFields(); n++) {
	//// if (data.get(i, n).getArea() != null)
	//// continue;
	//// FieldType ft;
	//// int trys = 0;
	//// do {
	//// ft = getRandomTerrain();
	////
	//// } while (!checkAreaType(ft, i, n) && trys++ < 10);
	//// if (trys >= 10)
	//// ft = FieldType.LEER;
	//// data.get(i, n).setArea(ft);
	//// }
	// }
	//
	// private boolean checkAreaType(FieldType ft, int x, int y) {
	//// FieldType north = x == 0 ? FieldType.LEER : data.get(x - 1,
	// y).getArea();
	//// FieldType east = y == data.getFields() - 1 ? FieldType.LEER :
	// data.get(x, y + 1).getArea();
	//// FieldType south = x == data.getFields() - 1 ? FieldType.LEER :
	// data.get(x + 1, y).getArea();
	//// FieldType west = y == 0 ? FieldType.LEER : data.get(x, y -
	// 1).getArea();
	////
	//// return checkPair(ft, west) && checkPair(ft, south) && checkPair(ft,
	// east) && checkPair(ft, north);
	// }
	//
	// private boolean checkPair(FieldType a, FieldType b) {
	// // Wald neben Unterholz
	// // Wald neben Wald
	// // Wald neben Ziel
	// // Unterholz neben Sand
	// // Unterholz neben Schutt
	// // Unterholz neben Wasser
	// // Unterholz neben Unterholz
	// // Schlamm neben Sand
	// // Schlamm neben Schutt
	// // Schlamm neben Unterholz
	// // Schlamm neben Wasser
	// // Schlamm neben Schlamm
	// // Schutt neben Unterholz
	// // Schutt neben Schlamm
	// // Schutt neben Schutt
	// // Sand neben Sand
	// // Sand neben Schlamm
	// // Sand neben Unterholz
	// // Sand neben Wasser
	// if (a == FieldType.LEER || b == FieldType.LEER || a == null || b == null)
	// return true;
	// switch (a) {
	// case Fluss:
	// switch (b) {
	// case Fluss:
	// case Unterholz:
	// case Schlamm:
	// case Sand:
	// return true;
	// default:
	// return false;
	// }
	// case Sand:
	// switch (b) {
	// case Fluss:
	// case Sand:
	// case Schlamm:
	// case Unterholz:
	// return true;
	// case Schutt:
	// case Wald:
	// case Ziel:
	// default:
	// return false;
	// }
	// case Schlamm:
	// switch (b) {
	// case Fluss:
	// case Sand:
	// case Schlamm:
	// case Unterholz:
	// case Schutt:
	// return true;
	// case Wald:
	// case Ziel:
	// default:
	// return false;
	// }
	// case Schutt:
	// switch (b) {
	// case Unterholz:
	// case Schutt:
	// case Schlamm:
	// return true;
	// case Fluss:
	// case LEER:
	// case Sand:
	// case Wald:
	// case Ziel:
	// default:
	// return false;
	// }
	// case Unterholz:
	// switch (b) {
	// case Unterholz:
	// case Schlamm:
	// case Schutt:
	// case Fluss:
	// return true;
	// case Sand:
	// case Wald:
	// case Ziel:
	// default:
	// return false;
	// }
	// case Wald:
	// switch (b) {
	// case Wald:
	// case Ziel:
	// case Unterholz:
	// return true;
	// case Fluss:
	// case Sand:
	// case Schlamm:
	// case Schutt:
	// default:
	// return false;
	// }
	// case Ziel:
	// switch (b) {
	// case Wald:
	// return false;
	// case Fluss:
	// case LEER:
	// case Sand:
	// case Schlamm:
	// case Schutt:
	// case Unterholz:
	// case Ziel:
	// default:
	// return true;
	// }
	// case LEER:
	// default:
	// }
	// return true;
	// }
	//
	// private FieldType getRandomTerrain() {
	// // 35% Wald
	// // 40% Unterholz
	// // 10% Schlamm
	// // 10% Sand
	// // 5% Schutt
	// int type = r.nextInt(100);
	//
	// if (type < 36)
	// return FieldType.Wald;
	//
	// if (type < 76)
	// return FieldType.Unterholz;
	//
	// if (type < 86)
	// return FieldType.Schlamm;
	//
	// if (type < 96)
	// return FieldType.Sand;
	//
	// if (type < 100)
	// return FieldType.Schutt;
	// return FieldType.LEER;
	// }
	//
	// private void fillRemainingWhitheSpace() {
	// for (int x = 0; x < data.getFields(); x++)
	// for (int y = 0; y < data.getFields(); y++)
	// if (data.get(x, y).getArea() == FieldType.LEER) {
	// FieldType north = x == 0 ? FieldType.LEER : data.get(x - 1, y).getArea();
	// FieldType east = y == data.getFields() - 1 ? FieldType.LEER : data.get(x,
	// y + 1).getArea();
	// FieldType south = x == data.getFields() - 1 ? FieldType.LEER : data.get(x
	// + 1, y).getArea();
	// FieldType west = y == 0 ? FieldType.LEER : data.get(x, y - 1).getArea();
	// FieldType placeType = FieldType.Unterholz;
	//
	// for (FieldType ft : colorCode.keySet()) {
	// if (ft == FieldType.LEER || ft == FieldType.Ziel)
	// continue;
	// if (checkPair(ft, north) && checkPair(ft, east) && checkPair(ft, west) &&
	// checkPair(ft, west)
	// && checkPair(ft, south)) {
	// placeType = ft;
	// break;
	// }
	// }
	// data.get(x, y).setArea(placeType);
	//
	// }
	// }

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

	// private void setAbilitySkills() {
	// int max = 5;
	// AbilityType[] at = new AbilityType[max];
	// at[0] = AbilityType.Akrobatik;
	// at[1] = AbilityType.Heimlichkeit;
	// at[2] = AbilityType.Klettern;
	// at[3] = AbilityType.Entfesselungskunst;
	// at[4] = AbilityType.Schwimmen;
	// FieldType local;
	// for (int i = 0; i < data.getFields(); i++)
	// for (int n = 0; n < data.getFields(); n++) {
	// local = data.get(i, n).getArea();
	// // North
	// for (int a = 0; a < 4; a++) {
	// if (local == FieldType.Fluss)
	// data.get(i, n).nextField[a][0] = new AbilitySG(AbilityType.Schwimmen, 10
	// + r.nextInt(15));
	// else if (local == FieldType.Unterholz || local == FieldType.Schlamm)
	// data.get(i, n).nextField[a][0] = new
	// AbilitySG(AbilityType.Entfesselungskunst,
	// 10 + r.nextInt(15));
	// else
	// data.get(i, n).nextField[a][0] = new AbilitySG(at[r.nextInt(3)], 10 +
	// r.nextInt(15));
	//
	// AbilityType atype;
	// do {
	// atype = at[r.nextInt(4)];
	// } while (atype == data.get(i, n).nextField[a][0].type);
	// data.get(i, n).nextField[a][1] = new AbilitySG(atype, 10 +
	// r.nextInt(10));
	// }
	// // for (int j = 0; j < 4; j++)
	// // for (int m = 0; m < 2; m++) {
	// // data.get(i, n).nextField[j][m] = new
	// // AbilitySG(at[r.nextInt(max)], 10 + r.nextInt(15));
	// // }
	// }
	// }

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
