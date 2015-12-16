/* This file is part of SmartAss and contains the ClassificationsDataModel class that
 * is the model for the classifications table
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

import com.sun.rowset.JdbcRowSetImpl;

import au.edu.uq.smartassrepoeditor.repository.LookupTableModel;

/**
 * The ClassificationsDataModel class is the model for the classifications table
 */
public class ClassificationsDataModel extends DictionaryDataModel {

	public class ClassificationsLookupModel extends LookupTableModel {
		public ClassificationsLookupModel(RowSet rs) {
			super(rs);
		}

		public void setMaster(int parent_id) {
			try {
				rowSet.setInt(1, parent_id);
				refresh();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public ClassificationsDataModel(Database data) {
		super(data);
		fields = new String[] {"parent_id", "name", "description", "id"};
		table_name = "classifications";
	}

	@Override
	public ItemDataModel newItem() {
		return new ClassificationItemModel(this);
	}

	public ClassificationItemModel newItem(int parent_id) {
		return new ClassificationItemModel(this, parent_id);
	}

	@Override
	protected DetailRowSetTableModel createTableModel(RowSet rs, boolean readonly) {
		DetailRowSetTableModel m;
		m = new DetailRowSetTableModel(rs, "parent_id") {
			@Override
			protected void checkRowValid() throws DataValidationException {
				try {
					String name = rowSet.getString("name");
					if(name==null || name.length()==0)
							throw new DataValidationException("Name can not be empty!");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void beforeSetValue(String columnName, Object newValue) {
				if(columnName.equals("name")) {
					int id = -1;
					try {
						id = rowSet.getInt("id");
					} catch (SQLException e) {
						e.printStackTrace();
					}
					IntIdItemModel it = (IntIdItemModel) getItem("name=? and parent_id=?", new Object[]{newValue, master_id});
					if(it!=null && it.getId()!=id)
						throw new DataValidationException("The item with name \"" + newValue + "\" already exists in the dictionary!");
				}
			}
		};
		m.setColumnTitles(new String[] {null, null, "Name", "Description"});
	    m.setVisibleColumns(new String[]{"name", "description"});
		return m;
	}

	@Override
	protected String composeRowsetSql(String order_clause) {
		return "select * from classifications where parent_id=? order by name";
	}

	@Override
	public RowSetTableModel getTableModel(String sql, Object[] params, String order_clause, boolean readonly) {
		return super.getTableModel(sql, new Object[]{new Integer(-1)}, order_clause, readonly);
	}

	public LookupTableModel getLookupModel() {
    	RowSet rs;
		try {
			rs = new JdbcRowSetImpl(data.getConnection());
			rs.setCommand(composeRowsetSql(null));
			rs.setInt(1, -1);
			rs.execute();
			return new ClassificationsLookupModel(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void dataFromItemModel(PreparedStatement st, ItemDataModel dm) {
		ClassificationItemModel cl = (ClassificationItemModel) dm;
		try {
			st.setInt(1	, cl.getParentId());
			st.setString(2, cl.getName());
			st.setString(3, cl.getDescription());
			if(!cl.getIsNew())
				st.setInt(4, cl.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
