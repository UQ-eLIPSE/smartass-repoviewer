/* This file is part of SmartAss and contains the DataSource class that is the 
 * simple base class for all kinds of datasources available in scripts.   
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
package au.edu.uq.smartassrepoeditor.script;

/**
 * The DataSource class is the simple base class for all kinds
 * of datasources available in scripts.   
 *
 */
public interface DataSource {

	/**
	 * This method returns the next portion of data following specific rules of the DataSource.
	 * For {@link DataSource} class that is an abstract class this is just an empty portion of data. 
	 * 
         * @return 
	 */
	public DataArray getData();

        /**
         * 
         */
        public void close();
}
