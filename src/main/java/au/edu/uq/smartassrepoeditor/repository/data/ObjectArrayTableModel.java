/* This file is part of SmartAss and contains the ObjectArrayTableModel class that
 * represents the table model that stores data in the array of Objects
 *
 * Copyright (C) 2008 The University of Queensland
 * SmartAss is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2, or
 * (at your option) any later version.
 * GNU program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with program;
 * see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package au.edu.uq.smartassrepoeditor.repository.data;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;;

/**
 * The ObjectArrayTableModel class represents the table model that stores data in the array of Objects
 */
public abstract class ObjectArrayTableModel extends AbstractTableModel {
	protected int column_count = 1;
	protected String[] headers = {};
    // Objects
    protected Vector<Object> objects = new Vector<Object>();

    //Don'1 fire update notifications
    private boolean batch_mode;

	public ObjectArrayTableModel() {
	}

	public int getColumnCount() {
		return column_count;
	}

	public int getRowCount() {
		return objects.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return getValueFromObject(objects.get(rowIndex), columnIndex);
	}

	@Override
	public String getColumnName(int column) {
		if(column<headers.length)
			return headers[column];
		return super.getColumnName(column);
	}

	abstract protected Object getValueFromObject(Object obj, int columnIndex) ;

	public void addRow(Object obj) {
		objects.add(obj);
		if(!isBatchMode())
			fireTableStructureChanged();
	}

	public void insertRow(Object obj, int index) {
		objects.add(index, obj);
		if(!isBatchMode())
			fireTableStructureChanged();
	}

	public void removeRow(int index) {
		objects.remove(index);
		if(!isBatchMode())
			fireTableStructureChanged();
	}

	public void clearAll() {
		objects.clear();
	}

	public Object getObject(int index) {
		if(index<0 || index>=objects.size())
			return null;
		return objects.get(index);
	}

	 boolean isBatchMode() {
		return batch_mode;
	}

	public void setBatchMode(boolean batch_mode) {
		this.batch_mode = batch_mode;
		if(!batch_mode)
			fireTableStructureChanged();
	}

	public void removeObject(Object obj) {
		objects.remove(obj);
	}

	/**
	 * Re-reads data (if there is some data source that descendant of {@link ObjectArrayTableModel}
	 * knows)
	 *
	 * Both refresh() and update() is empty in {@link ObjectArrayTableModel}
	 *
	 */
	public void refresh() {}

	/**
	 * Updates data (if there is some data source that descendant of {@link ObjectArrayTableModel}
	 * knows)
	 *
	 * Both refresh() and update() is empty in {@link ObjectArrayTableModel}
	 *
	 */
	public void update() {}

}
