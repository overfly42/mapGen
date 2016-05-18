package chaseGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JPanel;
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
	private Map<FieldType, Color> colorCode;
	private myPoint myPos;
	private myPoint yourPos;
	private EnvData evoirment;

	private class myPoint {
		int x = 7;
		int y = 0;
	}

	public FieldPanel() {

		this.setPreferredSize(new Dimension(500, 500));
		this.setOpaque(true);
		r = new Random();
		colorCode = new HashMap<>();
		initColorCode();
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
		if(ed == null)
			evoirment = new EnvData();
		evoirment = ed;
	}

	private void initColorCode() {
		colorCode.put(FieldType.Unterholz, Color.BLACK);
		colorCode.put(FieldType.Fluss, Color.BLUE);
		colorCode.put(FieldType.Sand, Color.YELLOW);
		colorCode.put(FieldType.Schlamm, Color.ORANGE);
		colorCode.put(FieldType.Schutt, Color.GRAY);
		colorCode.put(FieldType.Wald, Color.GREEN);
		colorCode.put(FieldType.Ziel, Color.PINK);
		colorCode.put(FieldType.LEER, Color.WHITE);

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
				FieldType ft = data.get(i, n).getArea();
				if (ft == null)
					ft = FieldType.LEER;
				if (i == myPos.x && n == myPos.y)
					g.setColor(Color.CYAN);
				else if (i == yourPos.x && n == yourPos.y)
					g.setColor(Color.WHITE);
				else
					g.setColor(colorCode.get(ft));
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
		str += "<br>" + "Gelände: " + fo.getArea().toString();
		str += "<br>" + "Überlebenskunst(mod): " + fo.survival;
		str += "<br>" + "Wahrnehmung(mod): " + fo.perception;
		str += "<br>" + "Norden (SG " + fo.nextField[NORTH][0].sg + "): " + fo.nextField[NORTH][0].type.toString();
		str += "<br>" + "       (SG " + fo.nextField[NORTH][1].sg + "): " + fo.nextField[NORTH][1].type.toString();
		str += "<br>" + "Osten (SG " + fo.nextField[EAST][0].sg + "): " + fo.nextField[EAST][0].type.toString();
		str += "<br>" + "       (SG " + fo.nextField[EAST][1].sg + "): " + fo.nextField[EAST][1].type.toString();
		str += "<br>" + "Süden (SG " + fo.nextField[SOUTH][0].sg + "): " + fo.nextField[SOUTH][0].type.toString();
		str += "<br>" + "       (SG " + fo.nextField[SOUTH][1].sg + "): " + fo.nextField[SOUTH][1].type.toString();
		str += "<br>" + "Westen (SG " + fo.nextField[WEST][0].sg + "): " + fo.nextField[WEST][0].type.toString();
		str += "<br>" + "       (SG " + fo.nextField[WEST][1].sg + "): " + fo.nextField[WEST][1].type.toString();
		if (fo.getTrap() != null)
			str += "<br>" + "Falle: " + fo.getTrap().toString();
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
		myPoint pos;
		if (e.getButton() == MouseEvent.BUTTON1)
			pos = myPos;
		else
			pos = yourPos;

		pos.x = x;
		pos.y = y;
		System.out.println("CLick" + x + " " + y);
		repaint();
	}

	private void generateTerrain() {
		initData();
		placeDestination();
		placeBoarder();
		placeRiver();
		fillEmptyTerrain();
		fillRemainingWhitheSpace();
		setSurvilvalSklills();
		setPerceptionSkills();
		setAbilitySkills();
		setTraps();
		repaint();
	}

	private void initData() {
		data.reCreateField(30);
	}

	private void placeDestination() {
		int halfField = data.getFields() / 2;
		int x = halfField + r.nextInt(halfField);
		int y = halfField + r.nextInt(halfField);
		data.get(x, y).setArea(FieldType.Ziel);
	}

	private void placeBoarder() {
		for (int i = 0; i < data.getFields(); i++)
			data.get(i, 0).setArea(FieldType.Wald);
	}

	private void placeRiver() {
		int maxRiver = data.getFields();
		int numRiver = 0;
		// find Start
		int x;
		int y;
		boolean startfound = false;
		do {
			x = r.nextInt(data.getFields());
			y = r.nextInt(data.getFields());
			FieldType ft = data.get(x, y).getArea();
			startfound = ft == null;
		} while (!startfound);
		data.get(x, y).setArea(FieldType.Fluss);
		int[] mod = { -1, 0, +1 };
		int trys = 0;
		while (numRiver < maxRiver && trys < 10) {
			int modx = mod[r.nextInt(3)];
			int mody = modx == 0 ? mod[r.nextInt(3)] : 0;
			if (x + modx < 0 || x + modx >= data.getFields() || y + mody < 0 || y + mody >= data.getFields()) {
				trys++;
				continue;
			}
			if (data.get(x + modx, y + mody).getArea() != null) {
				trys++;
				continue;
			}
			x += modx;
			y += mody;
			data.get(x, y).setArea(FieldType.Fluss);
			numRiver++;
		}
	}

	private void fillEmptyTerrain() {

		// fill up to 10% random
		int max = (data.getFields() * data.getFields()) / 10;
		for (int i = 0; i < max; i++) {
			int x = r.nextInt(data.getFields());
			int y = r.nextInt(data.getFields());
			if (data.get(x, y).getArea() == null)
				data.get(x, y).setArea(getRandomTerrain());
		}

		// fill rest by rules
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++) {
				if (data.get(i, n).getArea() != null)
					continue;
				FieldType ft;
				int trys = 0;
				do {
					ft = getRandomTerrain();

				} while (!checkAreaType(ft, i, n) && trys++ < 10);
				if (trys >= 10)
					ft = FieldType.LEER;
				data.get(i, n).setArea(ft);
			}
	}

	private boolean checkAreaType(FieldType ft, int x, int y) {
		FieldType north = x == 0 ? FieldType.LEER : data.get(x - 1, y).getArea();
		FieldType east = y == data.getFields() - 1 ? FieldType.LEER : data.get(x, y + 1).getArea();
		FieldType south = x == data.getFields() - 1 ? FieldType.LEER : data.get(x + 1, y).getArea();
		FieldType west = y == 0 ? FieldType.LEER : data.get(x, y - 1).getArea();

		return checkPair(ft, west) && checkPair(ft, south) && checkPair(ft, east) && checkPair(ft, north);
	}

	private boolean checkPair(FieldType a, FieldType b) {
		// Wald neben Unterholz
		// Wald neben Wald
		// Wald neben Ziel
		// Unterholz neben Sand
		// Unterholz neben Schutt
		// Unterholz neben Wasser
		// Unterholz neben Unterholz
		// Schlamm neben Sand
		// Schlamm neben Schutt
		// Schlamm neben Unterholz
		// Schlamm neben Wasser
		// Schlamm neben Schlamm
		// Schutt neben Unterholz
		// Schutt neben Schlamm
		// Schutt neben Schutt
		// Sand neben Sand
		// Sand neben Schlamm
		// Sand neben Unterholz
		// Sand neben Wasser
		if (a == FieldType.LEER || b == FieldType.LEER || a == null || b == null)
			return true;
		switch (a) {
		case Fluss:
			switch (b) {
			case Fluss:
			case Unterholz:
			case Schlamm:
			case Sand:
				return true;
			default:
				return false;
			}
		case Sand:
			switch (b) {
			case Fluss:
			case Sand:
			case Schlamm:
			case Unterholz:
				return true;
			case Schutt:
			case Wald:
			case Ziel:
			default:
				return false;
			}
		case Schlamm:
			switch (b) {
			case Fluss:
			case Sand:
			case Schlamm:
			case Unterholz:
			case Schutt:
				return true;
			case Wald:
			case Ziel:
			default:
				return false;
			}
		case Schutt:
			switch (b) {
			case Unterholz:
			case Schutt:
			case Schlamm:
				return true;
			case Fluss:
			case LEER:
			case Sand:
			case Wald:
			case Ziel:
			default:
				return false;
			}
		case Unterholz:
			switch (b) {
			case Unterholz:
			case Schlamm:
			case Schutt:
			case Fluss:
				return true;
			case Sand:
			case Wald:
			case Ziel:
			default:
				return false;
			}
		case Wald:
			switch (b) {
			case Wald:
			case Ziel:
			case Unterholz:
				return true;
			case Fluss:
			case Sand:
			case Schlamm:
			case Schutt:
			default:
				return false;
			}
		case Ziel:
			switch (b) {
			case Wald:
				return false;
			case Fluss:
			case LEER:
			case Sand:
			case Schlamm:
			case Schutt:
			case Unterholz:
			case Ziel:
			default:
				return true;
			}
		case LEER:
		default:
		}
		return true;
	}

	private FieldType getRandomTerrain() {
		// 35% Wald
		// 40% Unterholz
		// 10% Schlamm
		// 10% Sand
		// 5% Schutt
		int type = r.nextInt(100);

		if (type < 36)
			return FieldType.Wald;

		if (type < 76)
			return FieldType.Unterholz;

		if (type < 86)
			return FieldType.Schlamm;

		if (type < 96)
			return FieldType.Sand;

		if (type < 100)
			return FieldType.Schutt;
		return FieldType.LEER;
	}

	private void fillRemainingWhitheSpace() {
		for (int x = 0; x < data.getFields(); x++)
			for (int y = 0; y < data.getFields(); y++)
				if (data.get(x, y).getArea() == FieldType.LEER) {
					FieldType north = x == 0 ? FieldType.LEER : data.get(x - 1, y).getArea();
					FieldType east = y == data.getFields() - 1 ? FieldType.LEER : data.get(x, y + 1).getArea();
					FieldType south = x == data.getFields() - 1 ? FieldType.LEER : data.get(x + 1, y).getArea();
					FieldType west = y == 0 ? FieldType.LEER : data.get(x, y - 1).getArea();
					FieldType placeType = FieldType.Unterholz;

					for (FieldType ft : colorCode.keySet()) {
						if (ft == FieldType.LEER || ft == FieldType.Ziel)
							continue;
						if (checkPair(ft, north) && checkPair(ft, east) && checkPair(ft, west) && checkPair(ft, west)
								&& checkPair(ft, south)) {
							placeType = ft;
							break;
						}
					}
					data.get(x, y).setArea(placeType);

				}
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

	private void setAbilitySkills() {
		int max = 5;
		AbilityType[] at = new AbilityType[max];
		at[0] = AbilityType.Akrobatik;
		at[1] = AbilityType.Heimlichkeit;
		at[2] = AbilityType.Klettern;
		at[3] = AbilityType.Entfesselungskunst;
		at[4] = AbilityType.Schwimmen;
		FieldType local;
		for (int i = 0; i < data.getFields(); i++)
			for (int n = 0; n < data.getFields(); n++) {
				local = data.get(i, n).getArea();
				// North
				for (int a = 0; a < 4; a++) {
					if (local == FieldType.Fluss)
						data.get(i, n).nextField[a][0] = new AbilitySG(AbilityType.Schwimmen, 10 + r.nextInt(15));
					else if (local == FieldType.Unterholz || local == FieldType.Schlamm)
						data.get(i, n).nextField[a][0] = new AbilitySG(AbilityType.Entfesselungskunst,
								10 + r.nextInt(15));
					else
						data.get(i, n).nextField[a][0] = new AbilitySG(at[r.nextInt(3)], 10 + r.nextInt(15));

					AbilityType atype;
					do {
						atype = at[r.nextInt(4)];
					} while (atype == data.get(i, n).nextField[a][0].type);
					data.get(i, n).nextField[a][1] = new AbilitySG(atype, 10 + r.nextInt(10));
				}
				// for (int j = 0; j < 4; j++)
				// for (int m = 0; m < 2; m++) {
				// data.get(i, n).nextField[j][m] = new
				// AbilitySG(at[r.nextInt(max)], 10 + r.nextInt(15));
				// }
			}
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
				if (data.get(i, n).getArea() == FieldType.Ziel)
					continue;
				if (data.get(i, n).getArea() == FieldType.Fluss)
					continue;
				if (r.nextInt(10) != 0)
					continue;
				data.get(i, n).setTrap(tt[r.nextInt(max)]);
			}
	}

	private FieldType getArea(int dir, int x, int y) {
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
