/* This file is part of SmartAss and contains the LineNumberDataSource class that is a 
 * datasource that returns single record basing on its position is the data set. 
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
 * The LineNumberDataSource class is a datasource that returns single record 
 * basing on its position is the data set.
 */
public class LineNumberDataSource implements DataSource {
	DataArray dataArray;

	/**
	 * Creates {@link LineNumberDataSource} and reads record at <code>lineno</code> position
	 * 
	 * @param reader	data reader
	 * @param lineno	the "line number" - the number of data record in the data set
	 */
        @SuppressWarnings("empty-statement")
	public LineNumberDataSource(DataReader reader, int lineno) {
		if (lineno > 0) {
			int n = 0;
			while ( (dataArray = reader.readData()) != null && ++n < lineno ) {};
		}
		if (dataArray==null) dataArray = new DataArray();
		
		reader.close();
	}
	
	@Override
	public DataArray getData() { return dataArray; }

        @Override
        public void close() {}

}
