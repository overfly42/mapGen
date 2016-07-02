package chaseGenerator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import chaseGenerator.models.ObjectModel;
import chaseGenerator.models.TerrainModel;

@XmlRootElement
public class EnvData {
	@XmlElement
	public	List<TerrainModel> fields;
	@XmlElement
	public	List<ObjectModel> objects;
	public EnvData() {
		fields = new ArrayList<>();
		objects = new ArrayList<>();

	}

	public void update() {
		for (TerrainModel tm : fields)
			tm.update();
	}

	public TerrainModel getModel(String name) {
		for (TerrainModel tm : fields)
			if (tm.getName().equals(name))
				return tm;
		return null;
	}
}
