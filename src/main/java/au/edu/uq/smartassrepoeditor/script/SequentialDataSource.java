/* This file is a part of SmartAss and contains the SequentialDataSource class that is a 
 * datasource that returns records from the data set sequentially. 
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
 * The SequentialDataSource class is a 
 * datasource that returns records from the data set sequentially.
 */
public class SequentialDataSource implements DataSource {
	
        DataReader reader;
        
	/**
	 * Creates new {@link SequentialDataSource}
	 */
	public SequentialDataSource(DataReader reader) {
		this.reader = reader;
	}

	@Override
	/**
	 *	Returns next data record 
	 */
	public DataArray getData() {
		DataArray dataArray = reader.readData();
		if( dataArray == null ) {
			reader.rewindDataStream();
			dataArray = reader.readData();
		}
		return dataArray;
	}

        @Override
        public void close() { 
            reader.close();
        }
}
