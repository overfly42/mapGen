package chaseGenerator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;

public class TerrainConfig extends JPanel {

	private class Model extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8956798810392186233L;
		private TerrainModel tm;

		public Model(TerrainModel m) {
			super();
			tm = m;
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return otherTerrains.fields.size();

		}

		public Object getValueAt(int row, int col) {

			int i = 0;
			for (TerrainModel t : otherTerrains.fields) {
				if (i < row) {
					i++;
					continue;
				}
				switch (col) {
				case 0:
					return t.getName();
				case 1:

					return tm.getPropability(t.getName());
				default:
					return null;
				}
			}
			return null;
		}

		public void setValueAt(Object o, int row, int col) {
			if (col != 1)
				return;
			String s = (String) getValueAt(row, 0);// Get the name
			tm.setPropability(s, (int) o);
		}

		public boolean isCellEditable(int row, int column) {
			if (column == 1)
				return true;
			return false;
		}

		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Name";
			case 1:
				return "Propability";
			default:
				return "N/A";
			}

		}

		public Class getColumnClass(int col) {
			switch (col) {
			case 0:
				return String.class;
			case 1:
				return Integer.class;
			default:
				return Object.class;
			}
		}

		public void update() {
			fireTableDataChanged();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1499349776862563165L;

	private TerrainModel model;
	private Model tableModel;
	private EnvData otherTerrains;
	private JTable table;
	private JTextField textField;
	private MainFrame mf;
	private JLabel lblC;

	public TerrainConfig(TerrainModel terrainData, EnvData others, MainFrame mf) {
		model = terrainData;
		model.setTerrainConfig(this);
		otherTerrains = others;
		this.mf = mf;

		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.LEFT);
		lblName.setBounds(0, 23, 40, 15);

		JLabel lblColor = new JLabel("Color");
		lblColor.setBounds(0, 46, 37, 15);
		lblColor.setHorizontalAlignment(SwingConstants.LEFT);

		lblC = new JLabel("          ");
		lblC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				chooseColor();
			}
		});

		JLabel lblStreet = new JLabel("Street");
		lblStreet.setBounds(0, 69, 45, 15);

		JCheckBox chckbxPossible = new JCheckBox("possible");
		chckbxPossible.setBounds(60, 69, 85, 23);

		JCheckBox chckbxCrossing = new JCheckBox("crossing");
		chckbxCrossing.setBounds(60, 92, 85, 23);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setLocation(0, 122);

		tableModel = new Model(model);
		table = new JTable();
		table.setModel(tableModel);
		scrollPane.setViewportView(table);
		scrollPane.setSize(new Dimension(340, 113));

		lblC.setOpaque(true);
		lblC.setBackground(model.getColor());
		lblC.setBounds(60, 46, 160, 15);
		setLayout(null);
		add(lblName);
		add(lblColor);
		add(lblC);
		add(lblStreet);
		add(chckbxPossible);
		add(chckbxCrossing);
		add(scrollPane);

		textField = new JTextField(model.getName());
		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				nameChange();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				nameChange();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				nameChange();
			}
		});
		textField.setBounds(58, 21, 114, 19);
		add(textField);
		textField.setColumns(10);

		JButton btnDeleteThisTerrain = new JButton("Delete this Terrain");
		btnDeleteThisTerrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete();
			}
		});
		btnDeleteThisTerrain.setBounds(0, 241, 340, 25);
		add(btnDeleteThisTerrain);
	}

	public void upate() {
		tableModel.update();
	}

	private void chooseColor() {
		System.out.println("Choosing Color");
		JColorChooser jcc = new JColorChooser(model.getColor());
		Color showDialog = JColorChooser.showDialog(this, "Please Choose a Color for this Terrain", model.getColor());
		if (showDialog != null)
			model.setColor(showDialog);
		lblC.setBackground(model.getColor());
	}

	private void nameChange() {
		model.setName(textField.getText());
		mf.changeTabName(textField.getText());
	}

	private void delete() {
		mf.deleteTab();
	}

}
