/* This file is part of SmartAss and contains the AbstractDataModel class that
 * is the ancestor for all SmartAss data models
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.sql.RowSet;

import com.sun.rowset.JdbcRowSetImpl;

/**
 * The AbstractDataModel class is the ancestor for all SmartAss data models
 */
public abstract class AbstractDataModel {
	Database data;
	String[] fields = {};
	String table_name;

	Vector<RowSetTableModel> rowsetModels = new Vector<RowSetTableModel>();

	public AbstractDataModel(Database data) {
		this.data = data;
	}

	public ItemDataModel getItem(Object id) {
		PreparedStatement st = createStatement(composeSelectSql("id=?"));
		try {
			st.setObject(1, id);
			return getItem(st.executeQuery());
		} catch (SQLException e) {
			return null;
		}
	}

	public ItemDataModel getItem(String field_name, Object value) {
		PreparedStatement st = createStatement(composeSelectSql(field_name + "=?"));
		try {
			st.setObject(1, value);
			return getItem(st.executeQuery());
		} catch (SQLException e) {
			return null;
		}
	}

	public ItemDataModel getItem(String where_clause, Object[] params) {
		PreparedStatement st = createStatement(composeSelectSql(where_clause));
		try {
			if(params!=null)
				for(int i=0;i<params.length;i++)
					st.setObject(i+1, params[i]);
			return getItem(st.executeQuery());
		} catch (SQLException e) {
			return null;
		}
	}

	protected ItemDataModel getItem(ResultSet rs) throws SQLException {
		if(rs.first()) {
			ItemDataModel dm = newItem();
			dataToItemModel(rs, dm);
			return dm;
		} else
			return null;
	}

	abstract public ItemDataModel newItem();

	public void delete(ItemDataModel item) {}

	public void delete(Object id) {
		delete("id", id);
	}


	protected void delete(String id_name, Object id) {
		PreparedStatement st = createStatement(composeDeleteSql(id_name+"=?"));
		try {
			st.setObject(1, id);
			st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public RowSetTableModel getTableModel() {
		return getTableModel(false);
	}

	public RowSetTableModel getTableModel(boolean readonly) {
		return getTableModel(composeRowsetSql(null), null, readonly);
	}

	public RowSetTableModel getTableModel(String sql, Object[] params, boolean readonly) {
		return getTableModel(sql, params, null, readonly);
	}

	public RowSetTableModel getTableModel(String sql, Object[] params, String order_clause, boolean readonly) {
		try {
			if(order_clause!=null)
				sql = sql + " order by " + order_clause;
	    	RowSet rs = new JdbcRowSetImpl(data.getConnection());
	    	rs.setCommand(sql);
	    	if(params!=null)
	    		for(int i=0; i<params.length; i++)
	    			rs.setObject(i+1, params[i]);
	    	rs.execute();
	    	RowSetTableModel m = createTableModel(rs, readonly);
	    	addInterestedRowSetModel(m);
	    	return m;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void update(ItemDataModel dm) {
		PreparedStatement st;
		if(dm.is_new) //new item, do insert
			st = createStatement(composeInsertSql());
		else //already existing item, do update
			st = createStatement(composeUpdateSql());
		dataFromItemModel(st, dm);
		try {
			st.execute();
			afterExecuteUpdate(st, dm);
			dm.is_new = false;
		}catch (SQLException e) {
			e.printStackTrace();
		}

		//Refresh all registered RowSetTableModel objects
		for(int i=0;i<rowsetModels.size();i++)
			rowsetModels.get(i).refresh();
	}

/*	public void insert(ItemDataModel dm) {
		PreparedStatement st = createStatement(composeInsertSql());
		dataFromItemModel(st, dm);
		for(int i=0;i<rowsetModels.size();i++)
			rowsetModels.get(i).refresh();
		try {
			st.execute();
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	protected void afterExecuteUpdate(PreparedStatement st, ItemDataModel dm) {

	}

	public void addInterestedRowSetModel(RowSetTableModel rm) {
		rowsetModels.add(rm);
	}

	protected RowSetTableModel createTableModel(RowSet rs, boolean readonly) {return null;}

	protected void dataToItemModel(ResultSet rs, ItemDataModel dm) {
		try {
			if(rs.first())
				dm.readFromModel(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method has the empty implimentation, instead of abstract qualifier because
	 * in readonly model we need no transfer data from ItemDataModel object
	 *
	 * @param st
	 * @param dm
	 */
	protected void dataFromItemModel(PreparedStatement st, ItemDataModel dm) {}


	protected String composeFieldsList() {
		if(fields.length==0)
			return "*";
		return composeFieldsList(fields);
	}

	protected String composeFieldsList(String[] field_list) {
		String res = field_list[0];
		for(int i=1;i<field_list.length;i++)
			res = res +"," + field_list[i];
		return res;
	}

	protected String composeSelectSql() {
		return "select " + composeFieldsList() + " from " + table_name;
	}

	protected String composeSelectSql(String where_clause) {
		return composeSelectSql() + " where " + where_clause;
	}

	protected String composeUpdateSql() {
		return composeUpdateSql("id");
	}

	protected String composeUpdateSql(String id_name) {
		return composeUpdateSql(id_name, fields);
	}

	protected String composeUpdateSql(String id_name, String[] field_list) {
		int i = 0;
		if(field_list[0].equals(id_name))
			i++;
		String result = field_list[i++] + "=?";
		while(i<field_list.length) {
			if(!field_list[i].equals(id_name))
				result = result + "," + field_list[i] + "=?";
			i++;
		}
		return "update " + table_name + " set " + result + " where " + id_name + "=?";
	}

	protected String composeInsertSql() {
		return composeInsertSql(fields, "id");
	}

	protected String composeInsertSql(String[] field_list, String id_name) {
		int i = 0;
		if(field_list[0].equals(id_name))
			i++;
		String res = field_list[i];
		for(i=1;i<field_list.length;i++)
			if(!field_list[i].equals(id_name))
				res = res +"," + field_list[i];
		String s = "?";
		for(i=1;i<field_list.length;i++)
			if(!field_list[i].equals(id_name))
				s = s +",?";
		return "insert into " + table_name + " (" + res + ") values (" + s + ")";
	}

	protected String composeDeleteSql(String where_clause) {
		return "delete from " + table_name + " where " + where_clause;
	}

	protected String composeRowsetSql(String order_clause) {return null;}

	protected PreparedStatement	createStatement(String sql) {
		try {
			return data.getConnection().prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Database getDatabase() {
		return data;
	}

	public void setDatabase(Database data) {
		this.data = data;
	}

}
