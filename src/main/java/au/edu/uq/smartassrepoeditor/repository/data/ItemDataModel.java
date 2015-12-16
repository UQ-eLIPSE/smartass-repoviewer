/* This file is part of SmartAss and contains the ItemDataModel class that
 * is the ancestor for all item data models of SmartAss repository
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
 * The ItemDataModel class is the ancestor for all item data models of SmartAss repository
 */
public class ItemDataModel {
	AbstractDataModel data;
	protected boolean is_new = true;

	public ItemDataModel(AbstractDataModel data) {
		this.data = data;
	}

	public void validate() {};

	public void update() {
		data.update(this);
	}

	protected void readFromModel(ResultSet rs) throws SQLException {
		is_new = false;
	}

	public boolean getIsNew() {
		return is_new;
	}

	public AbstractDataModel getDataModel () {
		return data;
	}

}
