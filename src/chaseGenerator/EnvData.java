package chaseGenerator;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EnvData {
	@XmlElement
	List<FieldType2> fields;

	public EnvData() {
		fields = new ArrayList<>();
//		FieldType2 f = new FieldType2("Wald", false);
//		f.addNeighbour("Unterholz", 20);
//		f.addNeighbour("Wald", 100);
//		fields.add(f);
//		fields.add(new FieldType2("Unterholz", true));
//		fields.add(new FieldType2("Sand", true));
		// Unterholz, Sand, Wald, Schlamm, Schutt, Fluss, Ziel, LEER
	}

}
