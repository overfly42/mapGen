package chaseGenerator.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import chaseGenerator.AbilitySG;
import chaseGenerator.TrapType;
import chaseGenerator.models.TerrainModel;

@XmlRootElement
public class FieldObject implements Serializable {
	private TerrainModel area;
	@XmlElement
	public int survival;
	@XmlElement
	public int perception;
	@XmlElement
	public List<String> objects;
	@XmlElement
	public boolean containsStreet;

	public FieldObject() {
		objects = new ArrayList<>();
	}

	public TerrainModel getArea() {
		return area;
	}

	public void setArea(TerrainModel area) {
		this.area = area;
	}

	public void addObject(String str) {
		if (objects.contains(str))
			return;
		objects.add(str);
	}

	public List<String> getObjects() {
		return objects;
	}

	public boolean hasObjects() {
		return objects.isEmpty() == false;
	}
}