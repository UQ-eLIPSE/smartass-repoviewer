/* This file is part of SmartAss and contains the ClassificationsForm class that 
 * is the visual editor for Classifications dictionary
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.edu.uq.smartassrepoeditor.repository.data.AbstractDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.DetailRowSetTableModel;
import au.edu.uq.smartassrepoeditor.repository.data.RowSetTableModel;

/**
 * The ClassificationsForm class is the visual editor for Classifications dictionary
 */
public class ClassificationsForm extends AbstractDBTableForm implements ListSelectionListener {
	JTable prim_table, sec_table;
	DetailRowSetTableModel prim_model;
	DetailRowSetTableModel sec_model;
	int prim_row = -1;
	
	
	class DeletePrimAction extends DeleteRowAction {
        public void actionPerformed(ActionEvent e) {
        	if(checkAllowDelete(prim_row))
        		doDeleteRow(prim_row, prim_model);
        }
        
        @Override
        protected void doDeleteRow(int row, RowSetTableModel model) {
        	sec_model.onMasterDeleted();
        	super.doDeleteRow(row, model);
        }
	}


	public ClassificationsForm(AbstractDataModel model) {
		super("Classifications", model);
		prim_model = (DetailRowSetTableModel) model.getTableModel();
		sec_model = (DetailRowSetTableModel) table_model;
		prim_model.setMaster(0);
		sec_model.setMaster(-1);
		sec_model.setHasInsertRow(false);
		
		DeletePrimAction dpa = new DeletePrimAction();
		JPanel buttons = new JPanel();
		buttons.add(new JButton(dpa));
		buttons.add(new JButton(new RefreshAction()));
		add(buttons, BorderLayout.NORTH);

		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		prim_table = createTable(prim_model, new int[]{50, 100});
		JScrollPane scrollPane = new JScrollPane(prim_table);
		prim_table.getSelectionModel().addListSelectionListener(this);
		splitter.add(scrollPane);
		
		JPanel tbpanel = new JPanel(new BorderLayout());
		JPanel sbuttons = new JPanel();
		DeleteRowAction dsa = new DeleteRowAction();
		sbuttons.add(new JButton(dsa));
		sbuttons.add(new JButton(new RefreshAction()));
		tbpanel.add(sbuttons, BorderLayout.NORTH);
		
		sec_table = createTable(sec_model, new int[]{50, 100});
		scrollPane = new JScrollPane(sec_table);
		scrollPane.setPreferredSize(new Dimension(200, 100));
		tbpanel.add(scrollPane);
		splitter.add(tbpanel);

		splitter.setDividerLocation(200);
		add(splitter);
		setSize(300, 100);

		prim_table.getSelectionModel().addListSelectionListener(this);
		setRowDeleteAction(prim_table, dpa);
		sec_table.getSelectionModel().addListSelectionListener(this);
		setRowDeleteAction(sec_table, dsa);
	}
	
	private void detailSetMasterRow() {
		if(prim_row!=-1) { 
			Object prim_id = prim_model.getValue(prim_row, "id");
    		if(prim_id!=null) { 
    			sec_model.setMaster(prim_id);
    			sec_model.setReadonly(false);
    			sec_model.setHasInsertRow(true);
    			return;
    		} //else we in empty string for new record in master table
		}
		sec_model.setHasInsertRow(false);
		sec_model.setMaster(-1);
	}

    @Override
	public void valueChanged(ListSelectionEvent e) {
    	if(!e.getValueIsAdjusting()) {
    		int sel = -1;
    		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) 
                if (((ListSelectionModel)e.getSource()).isSelectedIndex(i)) 
                    sel = i;

    		if(e.getSource()==prim_table.getSelectionModel()) {
    				prim_row = sel;
    				detailSetMasterRow();
	    		} else 
	    			selected_row = sel;
   		}
    }
}
