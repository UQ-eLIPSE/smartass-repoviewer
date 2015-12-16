/* This file is part of SmartAss and contains the IntIdDataModel class that
 * is the ancestor for all models with unique integer id field
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

/**
 *  The IntIdDataModel class is the ancestor for all models with unique integer id field
 */
abstract public class IntIdDataModel extends AbstractDataModel {

	public IntIdDataModel(Database data) {
		super(data);
	}

	@Override
	protected void afterExecuteUpdate(PreparedStatement st, ItemDataModel dm) {
		if(dm.getIsNew()) {
			try {
				ResultSet ks = st.getGeneratedKeys();
				ks.next();
				((IntIdItemModel)dm).setId(ks.getInt(1));
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
