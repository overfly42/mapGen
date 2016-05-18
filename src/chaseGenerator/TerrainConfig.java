package chaseGenerator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TerrainConfig extends JPanel {

	private class Model extends DefaultTableModel {
		private TerrainModel tm;

		public Model(TerrainModel m) {
			super();
			tm = m;
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return 2;

		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return "N/A";
			case 1:
				return -1;
			default:
				return null;
			}
		}

		public String getColumnName(int col) {
			return col + "";

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
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1499349776862563165L;

	private TerrainModel model;
	private EnvData otherTerrains;
	private JTable table;
	private JTextField textField;

	public TerrainConfig(TerrainModel terrainData, EnvData others) {
		model = terrainData;
		otherTerrains = others;

		JLabel lblName = new JLabel("Name");
		lblName.setHorizontalAlignment(SwingConstants.LEFT);
		lblName.setBounds(0, 23, 40, 15);

		JLabel lblColor = new JLabel("Color");
		lblColor.setBounds(0, 46, 37, 15);
		lblColor.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel lblH = new JLabel("          ");
		lblH.addMouseListener(new MouseAdapter() {
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

		table = new JTable();
		table.setModel(new Model(model));
		scrollPane.setViewportView(table);
		scrollPane.setSize(new Dimension(340, 113));
		
		lblH.setOpaque(true);
		lblH.setBackground(model.getColor());
		lblH.setBounds(60, 46, 160, 15);
		setLayout(null);
		add(lblName);
		add(lblColor);
		add(lblH);
		add(lblStreet);
		add(chckbxPossible);
		add(chckbxCrossing);
		add(scrollPane);
		
		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				nameChange(arg0);
			}
		});
		textField.setBounds(58, 21, 114, 19);
		add(textField);
		textField.setColumns(10);
	}
	private void chooseColor()
	{
		System.out.println("Choosing Color");
		JColorChooser jcc = new JColorChooser(model.getColor());
		jcc.showDialog(this, "Please Choose a Color for this Terrain", model.getColor());
		
	}
	private void nameChange(KeyEvent ke)
	{
		System.out.println("Got Key");
	}
	
}
