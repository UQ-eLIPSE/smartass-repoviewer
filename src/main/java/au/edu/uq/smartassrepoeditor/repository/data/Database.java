/* This file is the part of SmartAss and contains the Database class that
 * is the access point to the repository database for all data aware classes
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import au.edu.uq.smartassrepoeditor.repository.RepositoryStorage;

/**
 * The Database class is the access point to the repository database for all data aware classes
 */
public class Database {
    Connection conn;

    private AuthorsDataModel authors;
    private ModulesDataModel modules;
    private FilesDataModel files;
    private ClassificationsDataModel classifications;
    private TemplatesDataModel templates;

    private RepositoryStorage storage;


    public Database(String conn_str, String username, String passwd, RepositoryStorage storage) {
    	this.storage = storage;
	    try {
	    	Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
		try {
//		    conn = DriverManager.getConnection("jdbc:mysql://localhost/smartass_repository?user=root&password=");
		    conn = DriverManager.getConnection(conn_str, username, passwd);
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}

	}

    public AuthorsDataModel getAuthorsModel() {
    	if(authors==null)
    		authors = new AuthorsDataModel(this);
		return authors;
	}

    public ModulesDataModel getModulesModel() {
    	if(modules==null)
    		modules = new ModulesDataModel(this);
		return modules;
	}

    public FilesDataModel getFilesModel() {
    	if(files==null)
    		files = new FilesDataModel(this);
		return files;
	}

    public ClassificationsDataModel getClassificationsModel() {
    	if(classifications==null)
    		classifications = new ClassificationsDataModel(this);
		return classifications;
	}

    public TemplatesDataModel getTemplatesModel() {
    	if(templates==null)
    		templates = new TemplatesDataModel(this);
		return templates;
	}

    public Connection getConnection() {
    	return conn;
    }

	public RepositoryStorage getStorage() {
		return storage;
	}

}
