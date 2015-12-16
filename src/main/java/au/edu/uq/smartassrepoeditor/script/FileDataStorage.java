/* This file is part of SmartAss and describes the FileDataStorage class that represents
 * a file-based data storage. 
*  The purpose of {@link FileDataStorage} is to access data stored in the file and provide it
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * The FileDataStorage class represents a file-based data storage. 
*  The purpose of {@link FileDataStorage} is to access data stored in the file and provide it
*  to the {@link DataReader} that then packs it to {@link DataArray} object
 * and give it to the {@link D
 */
public class FileDataStorage extends DataStorage {
	File file;
	InputStream is = null;
	Reader rd = null;
	
	/**
	 * Creates the {@link FileDataStorage} object for given {@link File}
	 * 
	 * @param file		the {@link File} where the data is stored 
	 */
	public FileDataStorage(File file) {
		 this.file = file;
	}

	@Override
	/**
	 * Returns the {@link InputStream} for data access
	 * 
	 * @param do_init		if true then (re)inits FileInputStream positioning it to file start
	 * @return				the input stream for data access		
	 */
	public InputStream getInputStream(boolean init_stream) {
		if(is==null || init_stream)
			try {
				if(is!=null)
					try {
						is.close();
					} catch (IOException e) {} 
				is = new FileInputStream(file);
			} catch(FileNotFoundException e) {
				is = null;
			}
		return is;
	}
	
	@Override
	/**
	 * Returns the {@link Reader} for data access
	 * 
	 * @param do_init		if true then (re)inits Reader positioning it to file start
	 * @return				the Reader for data access		
	 */
	public Reader getReader(boolean do_init) {
		if(rd==null || do_init)
			rd = new InputStreamReader(getInputStream(do_init));
		return rd;
	}

	@Override
	/**
	 * This method closes FileInputStream.
	 *
	 */
	public void close() {
		try {
			if(is!=null)
				is.close();
		} catch (IOException e) {} 
		super.close();
	}
}
