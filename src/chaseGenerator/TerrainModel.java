package chaseGenerator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Configuration of a Single Terrain, e.g. Forest, or Hills
 * 
 * @author christian
 *
 */
@XmlRootElement
public class TerrainModel {
	private String name = null;
	public Map<String, Integer> adjectionProbability;
	private boolean choosen;
	private TerrainConfig terraConf = null;
	@XmlElement
	private int red = 0, blue = 0, green = 0;

	public TerrainModel() {
		if (name != null)
			System.out.println(name);
		name = "default";
		red = Color.RED.getRed();
		blue = Color.RED.getBlue();
		green = Color.RED.getGreen();
		adjectionProbability = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String str) {
		name = str;
	}

	public boolean isChoosen() {
		return choosen;
	}

	public void setChoosen(boolean b) {
		choosen = b;
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
		while (getWholePropability() != 100 && steps++ < 100) {
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

	public void setColor(Color c) {
		red = c.getRed();
		blue = c.getBlue();
		green = c.getGreen();
	}
}
