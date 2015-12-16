/* This file is part of SmartAss and contains the TemplateClassificationForm class that 
 * is the editor form for SmartAss  in the template Classification metadata
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

import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import au.edu.uq.smartassrepoeditor.repository.data.ClassificationsDataModel.ClassificationsLookupModel;

/**
 * The TemplateClassificationForm class is the editor form for SmartAss Classification in the template metadata
 */
public class TemplateClassificationForm extends LookupTableDialog {
	ClassificationsLookupModel sec_model;
	JTable sec_table;
	int sec_row;

	public TemplateClassificationForm(ClassificationsLookupModel model, ClassificationsLookupModel sec_model) {
		super(model);
		this.sec_model = sec_model;
		model.setReadonly(true);
		sec_model.setReadonly(true);
		createControls(sec_model);		
	}

	protected void createControls() {}
	
	protected void createControls(ClassificationsLookupModel sec_model) {
		JSplitPane spl = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		table = new JTable(model);
		table.getSelectionModel().addListSelectionListener(this);
		spl.add(sp = new JScrollPane(table));
		sp.setPreferredSize(new Dimension(200,150));
		sec_table = new JTable(sec_model);
		sec_table.getSelectionModel().addListSelectionListener(this);
		spl.add(sp = new JScrollPane(sec_table));
		sp.setPreferredSize(new Dimension(200,150));
		controls.add(spl);
		setSize(300, 200);
		pack();
	}

    public void valueChanged(ListSelectionEvent e) {
    	if(!e.getValueIsAdjusting()) {
    		if(e.getSource()==table.getSelectionModel()) {
    			super.valueChanged(e);
    			if(selected_row!=-1) { 
    				Object prim_id = model.getValue(selected_row, "id");
    	    		if(prim_id!=null)
    	    			sec_model.setMaster((Integer)prim_id);
    			} else {
    				sec_model.setMaster(-1);
    			}
    			sec_row = -1;
    		} else {
	    		sec_row = -1;
	    		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) 
	                if (((ListSelectionModel)e.getSource()).isSelectedIndex(i)) 
	                    sec_row = i;
    		}
   		}
    }
    
    public int getSecondaryRow() {
    	return sec_row;
    }
}
