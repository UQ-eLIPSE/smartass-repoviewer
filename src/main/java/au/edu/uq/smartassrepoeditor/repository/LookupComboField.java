/* This file is part of SmartAss and contains the LookupComboField class that 
 * is one of the repository editor visual controls
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
package au.edu.uq.smartassrepoeditor.repository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The LookupComboField class is one of the repository editor visual controls
 */
public class LookupComboField extends AbstractComboField {
	LookupTableModel lookup_model;
	Object id;
	
	public LookupComboField(LookupTableModel look_model) {
		super();
		
		lookup_model = look_model;
		addActionListener(new ActionListener() {
            public void actionPerformed( ActionEvent e )
            {
            	LookupTableDialog ld = new LookupTableDialog(lookup_model);
            	int row = ld.executeLookup();
            	if(row!=-1)
            		setData(lookup_model.getId(row), lookup_model.getText(row));
            }
		});
	}
	
	public void setData(Object id, String text) {
		setId(id);
		setText(text);
	}
	
	public void setId(Object new_id) {
		id = new_id;
	}
	
	public Object getId() {
		return id;
	}

}
