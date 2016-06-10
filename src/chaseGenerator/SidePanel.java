package chaseGenerator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class SidePanel extends JPanel {
	EnvData enviroment;

	public void setEnviroment(EnvData ed) {
		enviroment = ed;
		this.removeAll();
		int size = enviroment.fields.size();
		this.setLayout(new GridLayout(size, 1));
		for (TerrainModel ft2 : enviroment.fields) {
			JCheckBox cb = new JCheckBox(ft2.getName());
			cb.setSelected(ft2.isChoosen());
			cb.addActionListener(ft2);
			this.add(cb);
		}
	}
}
