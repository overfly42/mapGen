package chaseGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class FieldObject {
	private FieldType area;
	private TrapType trap;
	@XmlElement
	int survival;
	@XmlElement
	int perception;
	@XmlElementWrapper
	AbilitySG[][] nextField = new AbilitySG[4][2];

	public FieldType getArea() {
		return area;
	}

	public void setArea(FieldType area) {
		this.area = area;
	}

	public void setTrap(TrapType tt) {
		trap = tt;
	}

	public TrapType getTrap() {
		return trap;
	}
}