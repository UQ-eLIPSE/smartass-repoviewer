/* This file is part of SmartAss and contains the LookupTableDialog class that 
 * is the ancestor for different table-oriented lookup dialogs
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
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * The LookupTableDialog class is the ancestor for different table-oriented lookup dialogs
 */
public class LookupTableDialog extends JDialog  implements ListSelectionListener{
	protected JPanel root, controls, buttons;
	protected JTable table;
	protected JScrollPane sp;

	LookupTableModel model;
	int selected_row = -1;
	
	class OkAction extends AbstractAction {
		public OkAction(String title) {
			super(title);
		}
		
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	}

	class CancelAction extends AbstractAction {
		public CancelAction(String title) {
			super(title);
		}
		
		public void actionPerformed(ActionEvent e) {
			selected_row = -1;
			setVisible(false);
			dispose();
		}
	}

	public LookupTableDialog(LookupTableModel model) {
		this.model = model;

		root = new JPanel(new BorderLayout());
		controls = new JPanel();
		buttons = new JPanel();
		buttons.add(new JButton(new OkAction("OK")));
		buttons.add(new JButton(new CancelAction("Cancel")));

		createControls();
		
		root.setLayout( new BorderLayout());
		root.add(controls,  BorderLayout.CENTER);
		root.add(buttons,  BorderLayout.SOUTH);
		add(root);
		setSize(300, 200);
		pack();
		setModal(true);
	}
	
	protected void createControls() {
		table = new JTable(model);
//		table.setFillsViewportHeight(true);
		table.getSelectionModel().addListSelectionListener(this);
		controls.add(sp = new JScrollPane(table));
	}
	
	public int executeLookup() {
		setVisible(true);
		return selected_row;
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
