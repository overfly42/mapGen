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
	public void update()
	{
		for(TerrainModel tm : fields)
			tm.update();
	}
	public TerrainModel getModel(String name)
	{
		for(TerrainModel tm : fields)
			if(tm.getName().equals(name))
				return tm;
		return null;
	}

}
