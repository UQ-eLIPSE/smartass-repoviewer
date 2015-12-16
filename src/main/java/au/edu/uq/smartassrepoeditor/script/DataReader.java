/* This file is part of SmartAss and contains the DataReader class that is the base class for all kinds
 * of data readers available for scripts. DataReader descendants together with other 
 * DataSource-related classes creates an infrastructure to provide problem-solution generation classes 
 * with external data sources.
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
 * Class {@link DataReader} - base class for all kinds of data readers available for scripts.
 * The purpose of {@link DataReader} is to decode data it reads from {@link DataStorage}, pack it in {@link DataArray} object
 * and give it to {@link DataSource}. So the place of {@link DataReader} in the middle level between top-level 
 * {@link DataSource} objects that user manipulate in script and low-level {@link DataStorage} objects that 
 * undestand a particular way the data is stored - e.g. file, database etc.      
 *
 */
public abstract class DataReader {
	DataStorage storage;
	
	/**
	 * Creates the DataReader with the given {@link DataStorage} object
	 * 
	 * @param storage	{@link DataStorage} object that containes data this {@link DataReader} will decode
	 */
	public DataReader(DataStorage storage) {
		this.storage = storage;
	}

	/**
	 * Reads from the storage, decodes and returns the data to the caller (typically {@link DataSource}
	 *  
	 * @return	{@link DataArray} object with the parsed data
	 */
	public abstract DataArray readData();
	
	/**
	 * Sets the data stream to its starting postition.
	 *
	 */
	public abstract void rewindDataStream(); 
	
	/**
	 * This method should be called to free all resources that can not be freed by garbage collector.
	 *
	 */
	public void close() {
		storage.close();
	}
}
