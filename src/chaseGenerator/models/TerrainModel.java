package chaseGenerator.models;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import chaseGenerator.gui.TerrainConfig;

/**
 * Configuration of a Single Terrain, e.g. Forest, or Hills The ActionListener
 * is for the SidePanel
 * 
 * @author christian
 *
 */
@XmlRootElement
public class TerrainModel extends BaseModel {

	private class AjectionPropability implements Comparable<AjectionPropability> {
		public String name;
		public int propability;

		public AjectionPropability(String n, int p) {
			name = n;
			propability = p;
		}

		@Override
		public int compareTo(AjectionPropability o) {

			return o.propability - propability;
		}
	}

	public Map<String, Integer> adjectionProbability;

	private boolean destination;

	private boolean border;
	private TerrainConfig terraConf = null;

	private int areas = 0;
	@XmlElement
	private int red = 0, blue = 0, green = 0;

	public TerrainModel() {
		red = Color.RED.getRed();
		blue = Color.RED.getBlue();
		green = Color.RED.getGreen();
		adjectionProbability = new HashMap<>();
	}


	public Color getColor() {
		return new Color(red, green, blue);
	}

	@XmlTransient
	public void setTerrainConfig(TerrainConfig tc) {
		terraConf = tc;
	}

	public void update() {
		terraConf.upate();
	}

	public void setPropability(String s, int i) {
		// Set borders
		if (i > 100)
			i = 100;
		else if (i < 0)
			i = 0;
		adjectionProbability.put(s, i);
		Object[] objects = adjectionProbability.keySet().toArray();
		if (objects.length <= 1)
			return;
		String[] names = new String[objects.length - 1];
		int pos = 0;
		for (Object o : objects)
			if (o.equals(s))
				continue;
			else
				names[pos++] = (String) o;
		int max = names.length;
		int step = getWholePropability() > 100 ? -1 : 1;
		int steps = 0;
		int maxStep = 100 * names.length;
		while (getWholePropability() != 100 && steps++ < maxStep) {
			pos = (pos + 1) % max;
			int val = getPropability(names[pos]) + step;
			if (val < 0 || val > 100)
				continue;
			adjectionProbability.put(names[pos], val);
		}
		update();
	}

	private int getWholePropability() {
		int propability = 0;
		for (String n : adjectionProbability.keySet())
			propability += adjectionProbability.get(n);
		return propability;
	}

	public int getPropability(String s) {
		Integer i = adjectionProbability.get(s);
		if (i == null)
			return 0;
		return i;
	}

	public int getAreas() {
		return areas;
	}

	public void setAreas(int a) {
		areas = a;
	}

	@XmlTransient
	public void setColor(Color c) {
		red = c.getRed();
		blue = c.getBlue();
		green = c.getGreen();
	}

	public boolean isBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
		if (terraConf != null && !border)
			terraConf.unsetBorder();
	}

	public boolean isDestination() {
		return destination;
	}

	public boolean isAdjectableTo(String s) {
		for (String adjecting : adjectionProbability.keySet())
			if (s.equals(adjecting)&&adjectionProbability.get(s)>0)
				return true;
		return false;
	}

	public void setDestination(boolean destination) {

		this.destination = destination;
		if (terraConf != null && !destination)
			terraConf.unsetDestination();
	}



	/**
	 * 
	 * @param percent
	 *            value between 0 and 99 including to select next area
	 * @return name of Area is selected
	 */
	public String getAreaNameOf(int percent) {
		int p = 0;
		List<AjectionPropability> elemtnes = new ArrayList<>();
		for (String s : adjectionProbability.keySet()) {
			elemtnes.add(new AjectionPropability(s, adjectionProbability.get(s)));

		}
		Collections.sort(elemtnes);
		for (AjectionPropability ap : elemtnes) {
			p += ap.propability;
			if (p >= percent)
				return ap.name;
		}
		return null;
	}
}
