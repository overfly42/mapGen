package chaseGenerator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class BaseModel implements ActionListener{
	private boolean choosen;
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String str) {
		name = str;
	}

	public boolean isChoosen() {
		return choosen;
	}

	public void setChoosen(boolean b) {
		choosen = b;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		setChoosen(((JCheckBox) e.getSource()).isSelected());

	}
}
