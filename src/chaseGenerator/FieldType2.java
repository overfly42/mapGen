package chaseGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FieldType2 {
	private String name;
	private boolean choosen;
	@XmlElement
	private List<String> neighbours;
	@XmlElement
	private Map<String, Integer> probability;
	@XmlElement
	private int max;

	public FieldType2() {
		neighbours = new ArrayList<>();
		probability = new HashMap<>();
		max = 0;
	}

	public FieldType2(String s, boolean b) {
		this();
		name = s;
		choosen = b;
	}

	public boolean isChoosen() {
		return choosen;
	}

	public void setChoosen(boolean choosen) {
		this.choosen = choosen;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasNeighbour(String s) {
		return neighbours.contains(s);
	}

	public void addNeighbour(String s, int probability) {
		if (!neighbours.contains(s))
			neighbours.add(s);
		this.probability.put(s, probability);
	}

	public int getAllProbability() {
		int val = 0;
		for (String s : probability.keySet())
			val += probability.get(s);
		return val;
	}
}
