/* This file is part of SmartAss and describes the DataStorage class that is the abstract ancestor 
 * for any specific data storage - a file, a record in the database etc. 
*  The purpose of {@link DataStorage} is to access data in the container and provide it
*  to the {@link DataReader} that then packs it to {@link DataArray} object
 * and give it to the {@link DataSource}. 

 * 
 * . This class together with other DataSource-related classes creates an infrastructure
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

import java.io.InputStream;
import java.io.Reader;


/**
 * The DataStorage class is the abstract ancestor for any specific data storage - 
 * a file, a record in the database etc. 
*  The purpose of {@link DataStorage} is to access data in the container and provide it
*  to the {@link DataReader} that then packs it to {@link DataArray} object
 * and give it to the {@link DataSource}. 
 */
public abstract class DataStorage {
	/**
	 * Returns the {@link InputStream} for data access
	 * 
	 * @param do_init		if true the {@link DataStorage} will (re)init data source that
	 * 						typically means that read position will be set to the start of the storage data
	 * @return				the input stream for data access		
	 */
	public abstract InputStream getInputStream (boolean do_init);
	
	
	/**
	 * Returns the {@link InputStream} for data access
	 */
	public InputStream getInputStream () {
		return getInputStream(false);
	}
	
	/**
	 * Returns the {@link Reader} for data access
	 * 
	 * @param do_init		if true the {@link DataStorage} will (re)init data source that
	 * 						typically means that read position will be set to the start of the storage data
	 * @return				the {@link Reader} for data access
	 */	
	public abstract Reader getReader(boolean do_init);
	
	/**
	 * Returns the {@link Reader} for data access
	 */
	public Reader getReader() {
		return getReader(false);
	}
	
	/**
	 * This method should be called to free all resources that can not be freed by the garbage collector.
	 *
	 */
	public void close() {}
}
