/* This file is part of SmartAss and contains the DetailRowSetTableModel class that
 * is the ancestor for any model class for the detail rowsets in the master-detail relationships
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

import java.sql.SQLException;

import javax.sql.RowSet;

/**
 * The DetailRowSetTableModel class
 * is the ancestor for any model class for the detail rowsets in the master-detail relationships
 */
public class DetailRowSetTableModel extends RowSetTableModel {
	String master_name;
	Object master_id;

	public DetailRowSetTableModel(RowSet rowset, String masterName) {
		super(rowset);
		this.master_name = masterName;
	}

	public void setMaster(Object masterId) {
		try {
			master_id = masterId;
			getRowSet().setObject(1, masterId);
			getRowSet().execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    protected void updateRow() throws SQLException {
    	if(rowInserted())
    		rowSet.updateObject(master_name, master_id);
    	super.updateRow();
    }

    public void onMasterDeleted() {
    	try {
    		while(rowSet.absolute(1))
    			rowSet.deleteRow();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
