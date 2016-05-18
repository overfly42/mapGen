package chaseGenerator;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class SidePanel extends JPanel {
	EnvData enviroment;

	public void setEnviroment(EnvData ed) {
		enviroment = ed;
		this.removeAll();
		int size = enviroment.fields.size();
		this.setLayout(new GridLayout(size, 1));
		for (FieldType2 ft2 : enviroment.fields) {
			JCheckBox cb = new JCheckBox(ft2.getName());
			cb.setSelected(ft2.isChoosen());
			this.add(cb);
		}
	}
}
