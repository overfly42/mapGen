package chaseGenerator.data;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Field {
	@XmlElement
	private FieldObject[][] data;
	@XmlElement
	private int fields = 25;
	@XmlElement
	private Map<String, Integer> overall;

	public Field() {
		reCreateField(fields);
		overall = new HashMap<>();
	}

	public void reCreateField(int val) {
		fields = val;
		data = new FieldObject[val][val];
		for (int i = 0; i < val; i++)
			for (int n = 0; n < val; n++)
				data[i][n] = new FieldObject();
	}

	public FieldObject get(int x, int y) {
		return data[x][y];
	}

	public int getFields() {
		return fields;
	}

	public void setFieldAt(FieldObject fo, int x, int y) {
		data[x][y] = fo;
	}

	public int getPecentage(String key) {
		try {
			return overall.get(key);
		} catch (Exception e) {
			System.out.println("Catch an Exception");
		}
		return 0;
	}

	public void setPercentage(String key, int val) {
		if (val < 0)
			val = 0;
		else if (val > 100)
			val = 100;
		overall.put(key, val);
		int step = getOverallPercentage() > 100 ? -1 : 1;
		int stepCount = 0;
		int max = overall.size() * 100;

		String[] keys = new String[1];
		keys = overall.keySet().toArray(keys);
		int pos = 0;
		while (stepCount++ < max && getOverallPercentage() != 100) {
			pos = (pos + 1) % keys.length;
			if (keys[pos].equals(key))
				continue;
			int value = overall.get(keys[pos]) + step;
			value = Math.max(value, 0);
			value = Math.min(value, 100);
			overall.put(keys[pos], value);
		}
	}

	private int getOverallPercentage() {
		int i = 0;
		for (String s : overall.keySet())
			i += overall.get(s);
		return i;

	}

	public int getNumberOfFields(String key) {
		try {
			int maxFields = fields * fields;
			int percentage = overall.get(key);
			if (percentage == 0)
				return 0;

			return (maxFields * percentage) / 100;
		} catch (Exception e) {
			return 0;
		}
	}
}
