package chaseGenerator.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import chaseGenerator.data.EnvData;
import chaseGenerator.models.ObjectModel;
import chaseGenerator.models.TerrainModel;

public class SidePanel extends JPanel {
	EnvData enviroment;

	/**
	 * With setting the enviroment, all elements will be shown. Old elements
	 * will be removed prio to adding them again
	 * 
	 * @param ed
	 */
	public void setEnviroment(EnvData ed) {
		enviroment = ed;
		this.removeAll();
		int size = Math.max(enviroment.fields.size(), enviroment.objects.size());
		this.setLayout(new GridLayout(size, 1));
		// for (TerrainModel ft2 : enviroment.fields) {
		JComponent jc;
		for (int i = 0; i < size; i++) {
			if (enviroment.fields.size() > i) {
				TerrainModel ft2 = enviroment.fields.get(i);
				JCheckBox cb = new JCheckBox(ft2.getName());
				cb.setSelected(ft2.isChoosen());
				cb.addActionListener(ft2);
				jc = cb;
			} else
				jc = new JLabel("");
			this.add(jc);
			if (enviroment.objects.size() > i) {
				ObjectModel om = enviroment.objects.get(i);
				JCheckBox cb = new JCheckBox(om.getName());
				cb.setSelected(om.isChoosen());
				cb.addActionListener(om);
				jc = cb;
			} else
				jc = new JLabel("");
			this.add(jc);
		}
	}
}
