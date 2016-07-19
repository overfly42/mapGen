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

import chaseGenerator.data.EnvData;
import chaseGenerator.gui.TerrainConfig;

/**
 * Configuration of a Single Terrain, e.g. Forest, or Hills The ActionListener
 * is for the SidePanel
 * 
 * @author christian
 *
 */
//@XmlRootElement
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
	private transient TerrainConfig terraConf = null;
//	@XmlTransient
	private transient EnvData env;

	private int areas = 0;
	@XmlElement
	private int red = 0, blue = 0, green = 0;

	public TerrainModel() {
		red = Color.RED.getRed();
		blue = Color.RED.getBlue();
		green = Color.RED.getGreen();
		adjectionProbability = new HashMap<>();
		env = null;
	}

	public Color getColor() {
		return new Color(red, green, blue);
	}

	public Color getInverseColor() {
		return new Color(255 - red, 255 - green, 255 - blue);
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
		if (i > 100) {
			int above = 0;
			// count elelemnts above 100
			for (String name : adjectionProbability.keySet())
				if (adjectionProbability.get(name) > 0 && !name.equals(s))
					above++;
			i = 100 - above;
		} else if (i < 0)
			i = 0;
		// set the new probabiltity
		adjectionProbability.put(s, i);
		// reduce or increase other values
		Object[] objects = adjectionProbability.keySet().toArray();// Elements
																	// to run
																	// over
		if (objects.length <= 1)
			return;
		// Add Elemements to modify (the just adjustet one not)
		String[] names = new String[objects.length - 1];
		int pos = 0;
		for (Object o : objects)
			if (o.equals(s))
				continue;
			else
				names[pos++] = (String) o;
		// Set / Caclulate parameter for endings
		int max = names.length;
		int step = getWholePropability() > 100 ? -1 : 1;
		int steps = 0;
		int maxStep = 100 * names.length;
		while (getWholePropability() != 100 && steps++ < maxStep) {
			pos = (pos + 1) % max;
			int val = getPropability(names[pos]) + step;
			if (val < 1 || val > 100) // Do not set an adjection propability to
										// zero automaticaly
				continue;
			adjectionProbability.put(names[pos], val);
		}
		// Take care for a bi directional connection
		// Get coutnerpart
		TerrainModel tm = env.getModel(s);
		if (tm.getPropability(getName()) == 0 && i > 0)
			tm.setPropability(getName(), 1);// the lowest non zero value
		else if (tm.getPropability(s) > 0 && i == 0)
			tm.setPropability(getName(), 0);// there is no connection between
											// this two
		update();
	}

	public void setEnviroment(EnvData ed) {
		env = ed;
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
			if (s.equals(adjecting) && adjectionProbability.get(s) > 0)
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
			if (p >= percent )
				return ap.name;
		}
		return null;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@XmlTransient
	public List<String> getAdjectingTerrains() {
		List<String> ls = new ArrayList<>();
		for (String s : adjectionProbability.keySet())
			if (adjectionProbability.get(s) > 0)
				ls.add(s);
		return ls;
	}
}
