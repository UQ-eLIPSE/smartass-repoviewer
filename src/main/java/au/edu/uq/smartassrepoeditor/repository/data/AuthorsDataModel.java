/* This file is part of SmartAss and contains the AuthorsDataModel class that
 * is the model for authors table
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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.RowSet;

/**
 * The AuthorsDataModel class is the model for authors table
 */
public class AuthorsDataModel extends DictionaryDataModel {

	public AuthorsDataModel(Database data) {
		super(data);
		fields = new String[] {"name", "description",  "id"};
		table_name = "authors";
	}

    @Override
	public RowSetTableModel createTableModel(RowSet rs, boolean readonly)  {
    	RowSetTableModel m = new DictionaryRowSetTableModel(rs, readonly);
    	m.setVisibleColumns(new String[]{"name", "description"});
		m.setColumnTitles(new String[] {null, "Name", "Description"});

    	return m;
	}

    @Override
    protected String composeRowsetSql(String order_clause) {
    	return "select * from authors order by name";
    }

    @Override
    public ItemDataModel newItem() {
    	return new AuthorsItemModel(this);
    }

    @Override
    protected void dataFromItemModel(PreparedStatement st, ItemDataModel dm) {
    	AuthorsItemModel am = (AuthorsItemModel) dm;
    	try {
    		if(!am.getIsNew())
    			st.setInt(3, am.getId()); //id parameter is last in query because  it is a part of where clause
	    						 //in update sql
	    	st.setString(1, am.getName());
	    	st.setString(2, am.getDescription());
    	} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}
