package chaseGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Deprecated
public class AbilitySG {
	public AbilitySG(AbilityType at, int sg) {
		type = at;
		this.sg = sg;
	}

	public AbilitySG() {
	}

	@XmlElement
	public
	AbilityType type;
	@XmlElement
	public
	int sg;
}