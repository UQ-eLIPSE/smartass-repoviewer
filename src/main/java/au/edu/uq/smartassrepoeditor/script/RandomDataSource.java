/* This file is part of SmartAss and contains the RandomDataSource class that is a 
 * datasource that returns a random record from the dataArrays set. 
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

import java.util.ArrayList;
import java.util.List;


/**
 * RandomDataSource class is a datasource that returns a random record from th
 */
public class RandomDataSource implements DataSource {
	
        List<DataArray> dataArrays = new ArrayList<>(); 
	
        boolean isUnique = false;
	
	/**
	 * Creates the {@link RandomDataSource} object and initializes its dataArrays from the dataArrays reader 
	 * 
	 * @param reader	the dataArrays reader
	 */
	public RandomDataSource(DataReader reader) {
		initData(reader);
	}
	
	/**
	 * Creates the {@link RandomDataSource} object and initializes its dataArrays from the dataArrays reader.
	 * The dataArrays records will be treated as isUnique if isUnique is true e.g. each record will be returned 
         * to datasource caller only once. 
	 * 
	 * @param reader	the dataArrays reader
	 * @param isUnique	
	 */
	public RandomDataSource(DataReader reader, boolean isUnique) {
		initData(reader);
		this.isUnique = isUnique;
	}
	
	/**
	 * Initializes the dataArrays stream
	 */
	private void initData(DataReader reader) {
		DataArray dataArray;
		reader.rewindDataStream();
		while ( (dataArray = reader.readData()) != null )
			dataArrays.add(dataArray);
                reader.close();
	}
	
	@Override
	/**
	 * Returns next random data record
	 */
	public DataArray getData() {
		int idx = (int)(Math.random() * dataArrays.size());
		DataArray dataArray = dataArrays.get(idx);
		if (isUnique) dataArrays.remove(idx);
		return dataArray;
	}

        @Override
        public void close() {}

}
