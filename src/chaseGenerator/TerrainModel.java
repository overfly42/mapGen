package chaseGenerator;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration of a Single Terrain, e.g. Forest, or Hills
 * @author christian
 *
 */
public class TerrainModel {
	private String name;
	public Color color;
	public Map<String,Integer> adjectionProbability;
	private boolean choosen;
	
	public TerrainModel()
	{
		name = "default";
		color = Color.RED;
		adjectionProbability = new HashMap<>();
	}
	public String getName()
	{
		return name;
	}
	public boolean isChoosen()
	{
		return choosen;
	}
	public Color getColor()
	{
		return color;
	}
}
