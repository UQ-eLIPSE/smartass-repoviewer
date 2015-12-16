/* This file is part of SmartAss and contains the RowSetTableModel class that
 * represents the table model that retrieves data from the JDBC RowSet
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

import java.sql.*;
import java.util.*;
import javax.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 * The RowSetTableModel class
 * represents the table model that retrieves data from the JDBC RowSet
 *
 * Based on Jan Stola code from Sun java tutorial, but extensively extended...
 */
public class RowSetTableModel extends AbstractTableModel implements RowSetListener {
//    static int STATE_BROWSE = 0;
//    static int STATE_EDIT = 1;
//    static int STATE_INSERT = 2;

    // RowSet with the data
    protected RowSet rowSet;
    // Number of rows
    private int rowCount;
    // Columns visible in the table model
    private String[] visibleColumns;
    // Readonly columns
    private String[] readonlyColumns;
    //headers
    private String[] columns_titles;
    //Is current row inserted?
    private boolean isRowInserted = false;
    //Is rowset model readonly?
    protected boolean readonly = false;
    //Has model additional empty row at table end?
    protected boolean hasInsertRow = true;
    //edit state (e.g., STATE_BROWSE, STATE_EDIT or STATE_INSERT)
//    protected int state = STATE_BROWSE;
    protected int row_edited;



    ///////////////////////
    // Constructors
    //////////////////////

    public RowSetTableModel(RowSet rowset) {
    	super();
		setRowSet(rowset);
	}

    public RowSetTableModel(RowSet rowset, boolean readonly) {
    	super();
		setRowSet(rowset);
    	setReadonly(readonly);
	}

	////////////////////////
	// Metadata access
	///////////////////////

    /**
     * Returns number of rows in the table.
     *
     * @return number of rows in the table.
     */
    public int getRowCount() {
    	if(readonly || !hasInsertRow) //Can't insert if readonly model or hasInsertRow==false...
    		return rowCount;
        return rowCount+1; //The row after last real rowSet row is used to add a new row into the table
    }

    /**
     * Returns number of columns in the table.
     *
     * @return number of columns in the table.
     */
    public int getColumnCount() {
        int columnCount = 0;
        try {
            if (visibleColumns != null) {
                columnCount = visibleColumns.length;
            } else if (rowSet != null) {
                columnCount = rowSet.getMetaData().getColumnCount();
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return columnCount;
    }

    /**
     * Returns label of the specified column (can be set for example
     * via <code>select column as columnLabel ...</code>).
     *
     * @return name of the specified column.
     */
    public String getColumnName(int column) {
        String name = super.getColumnName(column);
        if (visibleColumns != null) {
            name = visibleColumns[column].toUpperCase();
            column = findColumnIndex(visibleColumns[column]);
        }
        if (column != -1) {
        	if(columns_titles==null || columns_titles.length<=column || columns_titles[column]==null)
	            try {
	                name = rowSet.getMetaData().getColumnLabel(column+1);
	            } catch (SQLException sqlex) {
	                sqlex.printStackTrace();
	            }
	        else
	        	name = columns_titles[column];
        }
        return name;
    }

    public boolean isCellEditable(int row, int col) {
    	if(rowSet==null || readonly /*|| rowSet.isReadOnly()*/)
    		return false;

    	if(readonlyColumns==null)
    		return true;
    	else {
    		String column_name = getColumnName(col);
    		for(int i=0;i<readonlyColumns.length;i++)
    			if(readonlyColumns[i].equals(column_name))
    				return false;
    		return true;
    	}
    }


	////////////////////////
	// Data access and manipulation
	///////////////////////

    /**
     * Returns value at the specified row and column. Only visible columns are used.
     *
     * @param rowIndex		row index (zero-based)
     * @param columnIndex	column index in <b>visible columns list</b>
     *
     * @return value at the specified row and column.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        try {
        	//The way to insert new row into the table
        	if(rowIndex==rowCount)
        		return null;

        	rowSet.absolute(rowIndex+1);
            if (visibleColumns != null) {
                columnIndex = findColumnIndex(visibleColumns[columnIndex]);
            }
            if (columnIndex != -1) {
                value = rowSet.getObject(columnIndex+1);
            }
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return value;
    }

    /**
     * Returns value of the specified (by name) column at the specified row.
     *
     * @param rowIndex		row index
     * @param columnName	column name
     *
     * @return value at the specified row and column.
     */
    public Object getValue(int rowIndex, String columnName) {
        try {
        	//return null if row does not exist

        	if(rowIndex!=rowCount && rowSet.absolute(rowIndex+1))
        		return rowSet.getObject(columnName);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return null;
    }

    /**
     * Returns value of the specified column at the specified row.
     *
     * @param rowIndex		row index
     * @param columnIndex	column index (in the rowset, not the table wisible index)
     *
     * @return value at the specified row and column.
     */
    public Object getValue(int rowIndex, int columnIndex) {
        try {
        	//return null if row does not exist
        	if(rowIndex!=rowCount && rowSet.absolute(rowIndex+1))
        		return rowSet.getObject(columnIndex);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        return null;
    }

    public void setValue(Object value, int row, String columnName) {
    	if(readonly)
    		return;

    	if(!checkValueValid(columnName, value))
        	return;
    	try {
            if( !rowSet.absolute(row+1) ) {
//            	Can't find row in table? Let's treat this as an attempt to insert a new one!
            	rowSet.moveToInsertRow();
            	isRowInserted = true;
            }
            try {
            	beforeSetValue(columnName, value);
        	} catch(DataValidationException e) {
    			JOptionPane.showMessageDialog(null, e.getMessage(), "Incorrect data", JOptionPane.INFORMATION_MESSAGE);
        		isRowInserted = false;
        		return;
        	}

        	rowSet.updateObject(columnName, value);

        	afterSetValue(columnName, value);
            updateRow();
        }
        catch( SQLException e ) {
        	e.printStackTrace();
        }

    }

    public void setValue(Object value, int row, int column) {
        try {
        	setValue(value, row, rowSet.getMetaData().getColumnName(column));
        } catch( SQLException e ) {
            	e.printStackTrace();
        }

/*    	if(readonly)
    		return;
        try {
            if( !rowSet.absolute(row+1) ) {
//            	Can't find row in table? Let's treat this as an attempt to insert a new one!
            	rowSet.moveToInsertRow();
            	isRowInserted = true;
            }
            rowSet.updateObject(column, value);
            updateRow();
        }
        catch( SQLException e ) {
        	e.printStackTrace();
        }*/

    }


    public void deleteRow(int row) {
    	if(!checkAllowDelete())
    		return;
    	beforeDelete();
    	if(!readonly && row>=0)
	    	try {
	    		if(rowSet.absolute(row+1))
	    			rowSet.deleteRow();
	    		afterDelete();
	        } catch (SQLException sqlex) {
	            sqlex.printStackTrace();
	        }
	}

    protected void updateRow() throws SQLException {
    	try {
    		checkRowValid();
    	} catch(DataValidationException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Incorrect data", JOptionPane.INFORMATION_MESSAGE);
    		isRowInserted = false;
    		return;
    	}
    	if(rowInserted()) {
    		beforeInsert();
    		rowSet.insertRow();
    		afterInsert();
    	} else {
    		beforeUpdate();
            rowSet.updateRow();
            afterUpdate();
    	}
    	isRowInserted = false;
    }

    public void refresh() {
    	try {
    		rowSet.execute();
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public boolean rowInserted() {
		return isRowInserted;
	}

    /**
     * Sets new value at the specified row and column.
     * Called if the user changes a cell value in the table.
     * "column" parameter is a column number in visible columns list.
     *
     * @param value		new value
     * @param row		row index
     * @param column	column index in visible columns list
     *
     */
    public void setValueAt(Object value, int row, int column) {
    	setValue(value, row, findColumnIndex(visibleColumns[column])+1);
    }

    // Helper method used when <code>visibleColumns</code> property is set.
    // Transforms columnName into index of the column in the model.
    private Map columnToIndexMap = new HashMap();
    private int findColumnIndex(String columnName) {
        columnName = columnName.toUpperCase();
        Integer index = (Integer)columnToIndexMap.get(columnName);
        if (index != null) return index.intValue();
        int columnIndex = -1;
        if (rowSet != null) {
            try {
                ResultSetMetaData metaData = rowSet.getMetaData();
                int columns = metaData.getColumnCount();
                for (int i=1; i<=columns; i++) {
                    if (metaData.getColumnLabel(i).toUpperCase().equals(columnName)) {
                        columnIndex = i-1;
                        break;
                    }
                }
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
            }
        }
        return columnIndex;
    }

	//////////////////
	// Setters and getters for model properties
	/////////////////

    /**
     * Getter for the <code>rowSet</code> property.
     *
     * @return row set used by this model.
     */
    public RowSet getRowSet() {
        return rowSet;
    }

    /**
     * Setter for the <code>rowSet</code> property.
     *
     * @param rowSet row set with the data.
     */
    public void setRowSet(RowSet rowSet) {
        if (this.rowSet != null) {
            this.rowSet.removeRowSetListener(this);
        }
        this.rowSet = rowSet;
        columnToIndexMap.clear();
        rowSet.addRowSetListener(this);
        updateRowCount();
        fireTableStructureChanged();
    }

    /**
     * Getter for the <code>visibleColumns</code> property.
     *
     * @return columns visible in the table model or <code>null</code>
     * if all columns of the row set should be visible.
     */
    public String[] getVisibleColumns() {
        return visibleColumns;
    }

    /**
     * Setter for the <code>visibleColumns</code> property.
     *
     * @param columns visible in the table model or <code>null</code>
     * if all columns of the row set should be visible.
     */
    public void setVisibleColumns(String[] visibleColumns) {
        this.visibleColumns = visibleColumns;
        columnToIndexMap.clear();
        fireTableStructureChanged();
    }

    /**
     * Setter for the <code>readonlyColumns</code> property.
     *
     * @param columns that is readonly in the table model or <code>null</code>
     * if all columns of the row set should be editable.
     */
    public void setReadonlyColumns(String[] readonlyColumns) {
        this.readonlyColumns = readonlyColumns;
        fireTableStructureChanged();
    }

    public void setColumnTitles(String[] titles) {
    	columns_titles = titles;
        fireTableStructureChanged();
    }

    public void setReadonly(boolean readonly) {
    	this.readonly = readonly;
    	rowSetChanged(null);
    }

    public boolean getReadonly() { return readonly;}

	public void setHasInsertRow(boolean hasInsertRow) {
		this.hasInsertRow = hasInsertRow;
	}

	public boolean getHasInsertRow() {
		return hasInsertRow;
	}



    /**
     *  Helper method that updates <code>rowCount</code> field.
     */
    private void updateRowCount() {
    	isRowInserted = false;
        int rowCount = 0;
        try {
            rowSet.beforeFirst();
            while (rowSet.next()) rowCount++;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
        this.rowCount = rowCount;
    }


    ///////////////////
    // Events
    //////////////////

    protected void checkRowValid() {
	}

    protected void beforeUpdate() {

    }

    protected void afterUpdate() {

	}

    protected void  beforeInsert() {

	}

    protected void afterInsert() {

	}

    protected boolean checkAllowDelete() {
		return true;
	}

    protected void  beforeDelete() {

	}

    protected void afterDelete() {

	}

    protected boolean checkValueValid(String columnName, Object newValue) {
		return true;
	}

    protected void beforeSetValue(String columnName, Object newValue) {

	}

    protected void afterSetValue(String columnName, Object newValue) {

	}

	//////////////////
    // Implementation of RowSetListener
	//////////////////
    public void rowSetChanged(RowSetEvent event) {
        updateRowCount();
        fireTableStructureChanged();
    }

    public void rowChanged(RowSetEvent event) {
        updateRowCount();
        fireTableDataChanged();
    }

    public void cursorMoved(RowSetEvent event) {}

	public boolean isHasInsertRow() {
		return hasInsertRow;
	}

}
