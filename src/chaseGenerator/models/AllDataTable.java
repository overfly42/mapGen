package chaseGenerator.models;


import javax.swing.table.AbstractTableModel;

import chaseGenerator.data.EnvData;
import interfaces.UpdateListener;

public class AllDataTable extends AbstractTableModel implements UpdateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5866307797502154325L;

	private EnvData enviroment;

	public AllDataTable(EnvData env) {
		enviroment = env;
	}

	@Override
	public int getColumnCount() {

		return enviroment.fields.size() + 1;
	}

	@Override
	public int getRowCount() {

		return enviroment.fields.size();
	}

	@Override
	public Object getValueAt(int row, int col) {

		if (col == 0)
			return enviroment.fields.get(row).getName();
		return getTerrainModel(row, col).getPropability(getValueAt(row, 0).toString());

	}

	@Override
	public void setValueAt(Object o, int row, int col) {
		if (col == 0)
			return;
		getTerrainModel(row, col).setPropability(getValueAt(row, 0).toString(), (int) o);
		fireTableDataChanged();
	}

	private TerrainModel getTerrainModel(int row, int col) {
		return enviroment.getModel(getColumnName(col));
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		switch (col) {
		case 0:
			return false;
		default:
			return true;
		}
	}

	@Override
	public Class getColumnClass(int col) {
		switch (col) {
		case 0:
			return String.class;
		default:
			return Integer.class;

		}
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0)
			return "from\\to";
		col--;
		return enviroment.fields.get(col).getName();
	}

}
