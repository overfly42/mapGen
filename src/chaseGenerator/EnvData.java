package chaseGenerator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EnvData {
	@XmlElement
	List<TerrainModel> fields;

	public EnvData() {
		fields = new ArrayList<>();

	}

}
