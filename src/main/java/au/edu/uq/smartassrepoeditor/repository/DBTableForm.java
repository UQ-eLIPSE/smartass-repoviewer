/* This file is part of SmartAss and contains the DBTableForm class that 
 * is the ancestor for different table-oriented data edit forms
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
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.edu.uq.smartassrepoeditor.repository.data.AbstractDataModel;


/**
 * The DBTableForm class is the ancestor for different table-oriented data edit forms
 */
public class DBTableForm extends AbstractDBTableForm  implements ListSelectionListener {
	JTable table;
	
	public DBTableForm(AbstractDataModel model, String title)
			throws HeadlessException {
		super(title, model);
		
		DeleteRowAction da = new DeleteRowAction();
		JPanel buttons = new JPanel();
		buttons.add(new JButton(da));
		add(buttons, BorderLayout.NORTH);
		buttons.add(new JButton(new RefreshAction()));
		
		table = createTable(table_model, new int[]{50, 100});
		add(new JScrollPane(table));
		setSize(300, 200);
		
		table.getSelectionModel().addListSelectionListener(this);
		setRowDeleteAction(table, da);
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
