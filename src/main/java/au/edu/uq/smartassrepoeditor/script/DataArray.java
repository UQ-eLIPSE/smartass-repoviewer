/* This file is part of SmartAss and describes the DataArray class that represents a single data record. 
 * This class together with other DataSource-related classes creates an infrastructure
 * to provide problem-solution generation classes with some external data.

 * 
 * Copyright (C) 2006 Department of Mathematics, The University of Queensland
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
package au.edu.uq.smartassrepoeditor.script;

/**
 * The DataArray class represents a single data record. 
 * This class together with other DataSource-related classes creates an infrastructure
 * to provide problem-solution generation classes with some external data.
 */
public class DataArray {
	String[] data;
	
	/**
	 * Creates an empty data record
	 */
	public DataArray() {
		data = new String[]{};
	}
	
	/**
	 * Creates the data record from the {@link String} array
	 */
	public DataArray(String[] data) {
		this.data = data; 
	}
	
	/**
	 * Returns data recors as the {@link String} array
	 */
	public String[] getData() {
		return data;
	}
	
	/**
	 * Returns single record field by its position in the record
	 */
	public String getField(int field_position) {
		if(data==null || field_position>data.length || field_position<=0)
			return "";
		else
			return data[field_position-1];
	}

}
