/* This file is part of SmartAss and contains the ClassificationItemModel class that
 * is the model for the classification item
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
 * The ClassificationItemModel class is the model for the classification item
 */
public class ClassificationItemModel extends IntIdItemModel {
	int parent_id;
	String name;
	String description;

	public ClassificationItemModel(AbstractDataModel data) {
		this(data, 0);
	}

	public ClassificationItemModel(AbstractDataModel data, int parent_id) {
		super(data);
		this.parent_id = parent_id;
	}

	@Override
	protected void readFromModel(ResultSet rs) throws SQLException {
		super.readFromModel(rs);
		name = rs.getString("name");
		description = rs.getString("description");
		parent_id = rs.getInt("parent_id");
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

	public int getParentId() {
		return parent_id;
	}

	public ClassificationItemModel getParent() {
		return (ClassificationItemModel) data.getItem(new Integer(parent_id));
	}

/*	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}*/


}
