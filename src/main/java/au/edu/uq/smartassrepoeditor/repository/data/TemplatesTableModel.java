package au.edu.uq.smartassrepoeditor.repository.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.*;


public class TemplatesTableModel extends RowSetTableModel {

	public TemplatesTableModel(RowSet rowset) {
		super(rowset, true);
	}
	
	public TemplatesTableModel(RowSet rowset, boolean readonly) {
		super(rowset, true);
	}
    
    public void deleteRow(int row) {
    	try {
        	Connection conn = rowSet.getStatement().getConnection();
    		int id = (Integer)getValue(row, "id");
    		PreparedStatement sql = conn.prepareStatement("delete from templates where id=?");
    		sql.setInt(1, id);
    		sql.execute();
    		sql = conn.prepareStatement("delete from templates_modules where template_id=?");
    		sql.setInt(1, id);
    		sql.execute();
    		sql = conn.prepareStatement("delete from templates_files where template_id=?");
    		sql.setInt(1, id);
    		sql.execute();
    		sql = conn.prepareStatement("delete from updates where template_id=?");
    		sql.setInt(1, id);
    		sql.execute();
    		sql = conn.prepareStatement("delete from templates_classifications where template_id=?");
    		sql.setInt(1, id);
    		sql.execute();
    		rowSet.execute();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }    

}
