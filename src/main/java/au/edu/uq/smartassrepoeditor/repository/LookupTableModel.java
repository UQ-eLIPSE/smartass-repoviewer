/* This file is part of SmartAss and contains the LookupTableModel class that 
 * is the ancestor for any table-oriented data models (as in Model-View-Controller)
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
package au.edu.uq.smartassrepoeditor.repository;

import javax.sql.RowSet;

import au.edu.uq.smartassrepoeditor.repository.data.RowSetTableModel;

/**
 * The LookupTableModel class is the ancestor for any table-oriented data models (as in Model-View-Controller)
 */
public class LookupTableModel extends RowSetTableModel {
	String idfield;

	public LookupTableModel(RowSet rowset) {
		super(rowset, true);
		idfield = "id";
		setVisibleColumns(new String[]{"name"});
		setColumnTitles(new String[]{"ID", "Name"});
	}

	public LookupTableModel(RowSet rowset, String idfield, String[] visible_columns) {
		super(rowset, true);
		this.idfield = idfield;
		setVisibleColumns(visible_columns);
		setColumnTitles(new String[]{"ID", "Name"});
	}

	public LookupTableModel(RowSet rowset, String idfield, String[] visible_columns, String[] column_titles) {
		super(rowset, true);
		this.idfield = idfield;
		setVisibleColumns(visible_columns);
		setColumnTitles(column_titles);
	}
	
	public Object getId(int row) {
		return getValue(row, idfield);
	}

	//Override this to make any non-trivial data representation 
	public String getText(int row) {
		return getValue(row, "name").toString();
	}
}
