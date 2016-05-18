package chaseGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class AbilitySG {
	public AbilitySG(AbilityType at, int sg) {
		type = at;
		this.sg = sg;
	}

	public AbilitySG() {
	}

	@XmlElement
	AbilityType type;
	@XmlElement
	int sg;
}