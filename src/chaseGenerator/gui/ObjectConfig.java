package chaseGenerator.gui;

import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import chaseGenerator.ObjectType;
import chaseGenerator.data.EnvData;
import chaseGenerator.models.ObjectModel;

public class ObjectConfig extends JPanel {
	private ObjectModel data;
	private EnvData enviroment;
	private MainFrame main;

	private JTextField nameField;
	private JComboBox<ObjectType> objectTypeBox;
	private JTextArea descArea;

	public ObjectConfig(ObjectModel om, EnvData env, MainFrame mf) {
		data = om;
		enviroment = env;
		main = mf;
		this.setLayout(null);// Use Absolute Positions
		///////////////////////////////////////////////////
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(5, 15, 75, 25);
		this.add(lblName);

		nameField = new JTextField(data.getName());
		nameField.setBounds(80, 15, 300, 25);
		nameField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				nameChange();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				nameChange();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				nameChange();
			}
		});
		this.add(nameField);
		///////////////////////////////////////////////////
		JLabel lblNameTypeBox = new JLabel("Type:");
		lblNameTypeBox.setBounds(5, 40, 75, 25);
		this.add(lblNameTypeBox);

		objectTypeBox = new JComboBox<>();
		objectTypeBox.addItem(ObjectType.Item);
		objectTypeBox.addItem(ObjectType.Location);
		objectTypeBox.addItem(ObjectType.Trap);
		objectTypeBox.addItem(ObjectType.Person);
		if (data.getType() != null)
			objectTypeBox.setSelectedItem(data.getType());
		objectTypeBox.setBounds(80, 40, 300, 25);
		objectTypeBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				typeChange();

			}
		});
		this.add(objectTypeBox);
		///////////////////////////////////////////////////
		JLabel lblDesc = new JLabel("Description:");
		lblDesc.setBounds(5, 65, 75, 25);
		this.add(lblDesc);

		descArea = new JTextArea(data.getDescription());
		descArea.setBounds(80, 65, 300, 150);
		descArea.getDocument().addDocumentListener(new DocumentListener() {


			@Override
			public void removeUpdate(DocumentEvent arg0) {
				descChange();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				descChange();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				descChange();
			}
		});
		descArea.setOpaque(true);
		ScrollPane sp = new ScrollPane();
		sp.add(descArea);
		sp.setBounds(80, 65, 300, 150);
		this.add(sp);
		///////////////////////////////////////////////////
	}

	private void nameChange() {
		data.setName(nameField.getText());
		main.changeObjectTabName(nameField.getText());
	}

	private void typeChange() {
		data.setType((ObjectType) objectTypeBox.getSelectedItem());
	}

	private void descChange() {
		data.setDescription(descArea.getText());
	}
}
