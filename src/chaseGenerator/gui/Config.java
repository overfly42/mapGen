package chaseGenerator.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import chaseGenerator.data.EnvData;
import chaseGenerator.data.Field;
import chaseGenerator.models.TerrainModel;
import interfaces.UpdateListener;

public class Config extends JPanel {

	public class DataModel extends AbstractTableModel implements UpdateListener {
		private EnvData data;
		private Field field;

		public DataModel(EnvData ed, Field f) {
			data = ed;
			field = f;
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public int getRowCount() {
			if (data == null || data.fields == null)
				return 0;
			return data.fields.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return data.fields.get(row).getName();
			case 1:
				TerrainModel tm = data.fields.get(row);
				if (tm != null)
					return field.getPecentage(tm.getName());
			case 2:
				return field.getNumberOfFields((String) getValueAt(row, 0));
			}
			return ".";
		}

		@Override
		public void setValueAt(Object o, int row, int col) {
			field.setPercentage(data.fields.get(row).getName(), (int) o);
			fireTableDataChanged();
		}

		@Override
		public Class getColumnClass(int conl) {
			switch (conl) {
			case 0:
				return String.class;
			default:
				return Integer.class;
			}
		}

		@Override
		public boolean isCellEditable(int row, int col) {
			switch (col) {
			case 1:
				return true;
			default:
				return false;
			}
		}

		public String getColumnName(int col) {
			switch (col) {
			case 0:
				return "Name";
			case 1:
				return "Part of Field";
			case 2:
				return "Number of Fields";
			default:
				return "N/A";
			}
		}
	}

	// GUI
	JSpinner fieldCount;
	// Custom
	Field data;
	EnvData env;

	public Config(Field field, EnvData enviroment) {
		data = field;
		env = enviroment;
		initCompontents();
	}

	private void initCompontents() {
		this.setLayout(null);
		JLabel l1 = new JLabel();
		l1.setText("Fieldsize:");
		l1.setBounds(0, 0, 200, 20);
		this.add(l1);

		fieldCount = new JSpinner();
		fieldCount.setBounds(200, 0, 200, 20);
		fieldCount.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				changeFields();
			}
		});
		fieldCount.setModel(new SpinnerNumberModel(data.getFields(), 1, 100, 1));
		fieldCount.setValue(data.getFields());
		this.add(fieldCount);

		JLabel l2 = new JLabel();
		l2.setText("Overall Distribution");
		l2.setBounds(0, 20, 200, 20);
		this.add(l2);

		JScrollPane sp = new JScrollPane(new JTable(new DataModel(env, data)));
		sp.setBounds(200, 20, 300, 200);
		this.add(sp);

	}

	private void changeFields() {
		data.reCreateField((Integer) fieldCount.getValue());
	}

}
