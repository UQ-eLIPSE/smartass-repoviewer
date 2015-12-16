/* This file is part of SmartAss and contains the ModulesDataModel class that
 * is the model for modules table
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

import au.edu.uq.smartassrepoeditor.repository.ModulesTableModel;

/**
 * The ModulesDataModel class is the model for modules table
 */
public class ModulesDataModel extends DictionaryDataModel {

	public ModulesDataModel(Database data) {
		super(data);

		fields = new String[] {"name", "package", "parameters", "description", "id"};
		table_name = "modules";
	}

	@Override
	public ItemDataModel newItem() {
		return new ModulesItemModel(this);
	}

	@Override
	protected void dataFromItemModel(PreparedStatement st, ItemDataModel dm) {
		ModulesItemModel md = (ModulesItemModel) dm;
		try {
			st.setString(1, md.getName());
			st.setString(2, md.getModulePackage());
			st.setString(3, md.getParameters());
			st.setString(4, md.getDescription());
			if(!md.is_new)
				st.setInt(5, md.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected RowSetTableModel createTableModel(RowSet rs, boolean readonly) {
    	RowSetTableModel m = new ModulesTableModel(rs, readonly);
    	m.setVisibleColumns(new String[]{"name", "package"});
		m.setColumnTitles(new String[] {null, "Name", "Package"});
		return m;
	}

	@Override
	protected String composeRowsetSql(String order_clause) {
		return "select * from modules order by name";
	}

	static public String[] splitFullName(String fullname) {
		int pos = fullname.lastIndexOf(".");
		if(pos==-1)
			return new String[]{"", fullname};
		return  new String[]{fullname.substring(0, pos), fullname.substring(pos+1)};

		}

	ModulesItemModel getItemByFullName(String fullname) {
		return (ModulesItemModel) getItem("package=? and name=?", splitFullName(fullname));
	}
}
