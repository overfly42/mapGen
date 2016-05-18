package chaseGenerator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Field {
	@XmlElement
	private FieldObject[][] data;
	@XmlElement
	private int fields = 4;
	public void reCreateField(int val)
	{
		fields = val;
		data = new FieldObject[val][val];
		for (int i = 0; i < val; i++)
			for (int n = 0; n < val; n++)
				data[i][n] = new FieldObject();
	}
	public FieldObject get(int x, int y)
	{
		return data[x][y];
	}
	public int getFields(){
		return fields;
	}

}
