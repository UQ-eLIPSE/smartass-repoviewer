/* This file is part of SmartAss and contains the AbstractDBTableForm class that 
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.edu.uq.smartassrepoeditor.repository.data.AbstractDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.RowSetTableModel;

/**
 * The AbstractDBTableForm class that is a one of the repository editor visual controls
 */
public class AbstractDBTableForm extends JInternalFrame   implements ListSelectionListener {
	int selected_row = -1;
	AbstractDataModel model;
	RowSetTableModel table_model;
	JTable table;

	
	class DeleteRowAction extends AbstractAction {
		public DeleteRowAction() {
			super("Delete");
		}
		
        public void actionPerformed(ActionEvent e) {
        	if(checkAllowDelete(selected_row))
        		doDeleteRow(selected_row, table_model);
        }
        
        protected void doDeleteRow(int row, RowSetTableModel model) {
        	model.deleteRow(row);
		}
        
        protected boolean checkAllowDelete(int row) {
			return row!=-1 && JOptionPane.showConfirmDialog(null,
					"Delete this record?",
					"Confirm operation", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;
		}
	}

	class RefreshAction extends AbstractAction {
		public RefreshAction() {
			super("Refresh");
		}
		
        public void actionPerformed(ActionEvent e) {
        	table_model.refresh();
        }
	}


	public AbstractDBTableForm(String title, AbstractDataModel model) {
		super(title, true, true, true, true);
		
		this.model = model;
		table_model = model.getTableModel();
	}
	
	protected JTable createTable(RowSetTableModel model, int[] column_width) {
		JTable table = new JTable(model);
		//table.setFillsViewportHeight(true);
		if(column_width!=null)
			for(int i=0; i<table.getColumnModel().getColumnCount() && i<column_width.length;i++)
				table.getColumnModel().getColumn(i).setPreferredWidth(50);
		return table;
	}
	
	protected void setRowDeleteAction(JTable table, AbstractAction action) {
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK), "doDeleteRow");
		table.getActionMap().put("doDeleteRow", action);
	}

	public void valueChanged(ListSelectionEvent e) {
    	if(!e.getValueIsAdjusting()) {
    		selected_row = -1;
    		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) 
                if (((ListSelectionModel)e.getSource()).isSelectedIndex(i)) 
                    selected_row = i;
    		}
   		}
}
