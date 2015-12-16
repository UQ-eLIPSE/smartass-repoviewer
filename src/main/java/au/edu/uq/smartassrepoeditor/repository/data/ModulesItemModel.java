/* This file is part of SmartAss and contains the ModulesItemModel class that
 * is the model for the module item
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
 * The ModulesItemModel class is the model for the module item
 */
public class ModulesItemModel extends IntIdItemModel {
	protected String name;
	protected String module_package;
	protected String parameters;
	protected String description;


	public ModulesItemModel(ModulesDataModel data) {
		super(data);

		is_new = true;
		id = 0; //???
		name = "";
		module_package = "";
		parameters = "";
		description = "";
	}

	@Override
	protected void readFromModel(ResultSet rs) throws SQLException {
		super.readFromModel(rs);
		name = rs.getString("name");
		module_package = rs.getString("package");
		parameters = rs.getString("parameters");
		description = rs.getString("description");
	}

	// Gettets and setters for model fields
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getModulePackage() {
		return module_package;
	}

	public void setModulePackage(String module_package) {
		this.module_package = module_package;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	private void checkItemExists(String new_name, String new_package) {
		ModulesItemModel item = (ModulesItemModel) data.getItem("name=? and package=?", new String[]{new_name, new_package});
		if(item!=null && item.getId()!=id)
			throw new DataValidationException("Module \"" + new_name + "\" from package \"" + new_package +
					"\" already exists in repository");
	}

	@Override
	public void validate() {
		super.validate();
		checkItemExists(name, module_package);
	}
}
