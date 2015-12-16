/* This file is part of SmartAss and contains the StringArrayTableModel class that
 * represents the table model that stores data in the array of String
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

/**
 * The StringArrayTableModel class
 * represents the table model that stores data in the array of String
 */
public class StringArrayTableModel extends ObjectArrayTableModel {

	@Override
	protected Object getValueFromObject(Object obj, int columnIndex) {
		if (obj instanceof String) {
			if(columnIndex==0)
				return (String) obj;
		} else if(obj instanceof String[]) {
			String[] arr = (String[]) obj;
			if(columnIndex<arr.length)
				return arr[columnIndex];
		}
		return "";
	}

}
