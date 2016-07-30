package chaseGenerator.gui;

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

import chaseGenerator.data.EnvData;
import chaseGenerator.models.TerrainModel;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JSpinner;

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

	private JCheckBox chckbxBorder;

	public TerrainConfig(TerrainModel terrainData, EnvData others, MainFrame mf) {
		model = terrainData;
		model.setTerrainConfig(this);
		otherTerrains = others;
		this.mf = mf;
		this.setPreferredSize(new Dimension(500, 500));

		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.LEFT);
		lblName.setBounds(15, 23, 40, 15);

		JLabel lblColor = new JLabel("Color");
		lblColor.setBounds(15, 46, 37, 15);
		lblColor.setHorizontalAlignment(SwingConstants.LEFT);

		lblC = new JLabel("          ");
		lblC.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				chooseColor();
			}
		});

		JLabel lblStreet = new JLabel("Street");
		lblStreet.setBounds(15, 73, 45, 15);

		JCheckBox chckbxPossible = new JCheckBox("possible");
		chckbxPossible.setBounds(140, 71, 85, 23);
		chckbxPossible.setSelected(model.isStreet());
		chckbxPossible.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.setStreet(((JCheckBox) arg0.getSource()).isSelected());

			}
		});

		JCheckBox chckbxCrossing = new JCheckBox("crossing");
		chckbxCrossing.setBounds(140, 94, 85, 23);
		chckbxCrossing.setSelected(model.isStreetCrossing());
		chckbxCrossing.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.setStreetCrossing(((JCheckBox) arg0.getSource()).isSelected());

			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setLocation(15, 213);

		tableModel = new Model(model);
		table = new JTable();
		table.setModel(tableModel);
		scrollPane.setViewportView(table);
		scrollPane.setSize(new Dimension(340, 113));

		lblC.setOpaque(true);
		lblC.setBackground(model.getColor());
		lblC.setBounds(140, 46, 160, 15);

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
		textField.setBounds(140, 21, 114, 19);
		add(textField);
		textField.setColumns(10);

		JButton btnDeleteThisTerrain = new JButton("Delete this Terrain");
		btnDeleteThisTerrain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete();
			}
		});
		btnDeleteThisTerrain.setBounds(15, 338, 340, 25);
		add(btnDeleteThisTerrain);

		JLabel lblSpecial = new JLabel("Special:");
		lblSpecial.setBounds(15, 150, 100, 23);

		chckbxBorder = new JCheckBox("Border");
		chckbxBorder.setBounds(140, 150, 129, 23);
		chckbxBorder.setSelected(model.isBorder());
		chckbxBorder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				borderChanged(((JCheckBox) arg0.getSource()).isSelected());

			}
		});
		add(chckbxBorder);

		JLabel lblNoOfAreas = new JLabel("No. of Areas");
		lblNoOfAreas.setBounds(15, 168, 105, 15);
		add(lblNoOfAreas);

		JLabel lblMeansAny = new JLabel("0 means any");
		lblMeansAny.setBounds(15, 186, 105, 15);
		add(lblMeansAny);

		JSpinner spinner = new JSpinner();
		spinner.setBounds(140, 181, 114, 20);
		spinner.setValue(model.getAreas());
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int i = (int) ((JSpinner) e.getSource()).getValue();
				if (i < 0) {
					i = 0;
					((JSpinner) e.getSource()).setValue(i);
				}
				setOccurences(i);

			}
		});
		add(spinner);

		setLayout(null);
		add(lblName);
		add(lblColor);
		add(lblC);
		add(lblStreet);
		add(chckbxPossible);
		add(chckbxCrossing);
		add(scrollPane);
		add(lblSpecial);
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
		mf.changeTerrainTabName(textField.getText());
	}

	private void delete() {
		mf.deleteTab();
	}

	private void borderChanged(boolean b) {
		System.out.println("This is " + (b ? "" : "not ") + "the new border");
		for (TerrainModel tm : otherTerrains.fields) {
			if (tm.getName().equals(model.getName()))
				continue;
			tm.setBorder(false);
		}
		model.setBorder(b);
	}

	public void unsetBorder() {
		chckbxBorder.setSelected(false);
	}

	private void setOccurences(int n) {
		model.setAreas(n);
	}

}
