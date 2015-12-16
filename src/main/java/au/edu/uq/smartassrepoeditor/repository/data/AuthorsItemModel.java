/* This file is part of SmartAss and contains the AuthorsItemModel class that
 * is the model for the single author item
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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The AuthorsItemModel class is the model for the single author item
 */
public class AuthorsItemModel extends IntIdItemModel {
	String name;
	String description;

	public AuthorsItemModel(AbstractDataModel data) {
		super(data);

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void readFromModel(ResultSet rs) throws SQLException {
		super.readFromModel(rs);
    	id = rs.getInt("id");
    	name = rs.getString("name");
    	description = rs.getString("description");
	}

}
