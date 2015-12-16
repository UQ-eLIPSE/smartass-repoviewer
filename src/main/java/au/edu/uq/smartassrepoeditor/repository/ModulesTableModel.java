/* This file is part of SmartAss and contains the ModulesTableModel class that 
 * is the data model (as in Model-View-Controller) for the SmartAss modules list
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

import java.sql.SQLException;

import javax.sql.RowSet;

import au.edu.uq.smartassrepoeditor.repository.data.ModulesItemModel;
import au.edu.uq.smartassrepoeditor.repository.data.RowSetTableModel;

/**
 * The ModulesTableModel class is the data model (as in Model-View-Controller) for the SmartAss modules list
 */
public class ModulesTableModel extends RowSetTableModel {

	public ModulesTableModel(RowSet rowset) {
		super(rowset, true);
	}

	public ModulesTableModel(RowSet rowset, boolean readonly) {
		super(rowset, true);
	}
	
    protected void updateRow() throws SQLException {
    	if(rowInserted() && rowSet.getObject("package")==null)
    		rowSet.updateString("package", "");
    	super.updateRow();
    }
    
    public void deleteRow(int row) {
    	readonly = false;
    	super.deleteRow(row);
    	readonly = true;
    }    
}
