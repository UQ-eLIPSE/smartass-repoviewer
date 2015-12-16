/* This file is part of SmartAss and contains the ClassificationItemModel class that
 * is the ancestor for all item models with unique integer id field
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
 * The ClassificationItemModel class is the ancestor for all item models with unique integer id field
 *
 */
public class IntIdItemModel extends ItemDataModel {
	protected int id;

	public IntIdItemModel(AbstractDataModel data) {
		super(data);
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	@Override
	protected void readFromModel(ResultSet rs) throws SQLException {
		super.readFromModel(rs);
		id = rs.getInt("id");
	}

}
