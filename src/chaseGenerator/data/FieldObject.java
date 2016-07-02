package chaseGenerator.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import chaseGenerator.AbilitySG;
import chaseGenerator.TrapType;
import chaseGenerator.models.TerrainModel;

@XmlRootElement
public
class FieldObject {
	private TerrainModel area;
	private TrapType trap;
	@XmlElement
	public int survival;
	@XmlElement
	public	int perception;
	@XmlElementWrapper
	public	AbilitySG[][] nextField = new AbilitySG[4][2];

	public TerrainModel getArea() {
		return area;
	}

	public void setArea(TerrainModel area) {
		this.area = area;
	}

	public void setTrap(TrapType tt) {
		trap = tt;
	}

	public TrapType getTrap() {
		return trap;
	}
}