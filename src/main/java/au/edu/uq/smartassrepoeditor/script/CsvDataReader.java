/* This file is part of SmartAss and describes the CsvDataReader class that reads the data as comma 
 * separated values. This class together with other DataSource-related classes creates an infrastructure
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

import java.io.IOException;

import au.edu.uq.smartassrepoeditor.auxiliary.CsvReader;


/**
 * The CsvDataReader class reads the data as comma separated values. 
 * This class together with other DataSource-related classes creates an infrastructure
 * to provide problem-solution generation classes with some external data.
 */
public class CsvDataReader extends DataReader {
	CsvReader reader; 
	
	/**
	 * Creates the new CvsDataReader object 
	 * 
	 * @param storage		{@link DataStorage} - the data container on some ddata storage
	 * 						such as a file or BLOB stored in the database  
	 */
	public CsvDataReader(DataStorage storage) {
		super(storage);
		rewindDataStream();
	}
	
	/**
	 * Creates the new CvsDataReader object 
	 * 
	 * @param storage		{@link DataStorage} - the data container on some ddata storage
	 * 						such as a file or BLOB stored in the database  
	 */
	public CsvDataReader(DataStorage storage, String delimiter) {
		super(storage);
		createReader();
	}

	@Override
	/**
	 * Reads next data record from the storage
	 */
	public DataArray readData() {
		String s[];
		try {
			if(reader!=null && reader.readRecord()) {
				s = reader.getValues();
				return new DataArray(s);
			} else
				return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	/**
	 * Rewinds data stream positioning reader to the first record of the storage
	 */
	public void rewindDataStream() {
		createReader();
	}
	
	/**
	 * Creates new data reader
	 */
	private void createReader() {
		try {
			if(reader!=null)
				reader.close();
			reader = new CsvReader(storage.getReader(true));
		} catch (Exception e) {
			reader = null;
		}
	}
	
	@Override
	/**
	 * Closes data reader. The reader can use some limited system resources such as file descriptors
	 * so this is a good practice to call close() after using CvsDataReader class  
	 */
	public void close() {
		if(reader!=null)
			reader.close();
		super.close();
	}
}
