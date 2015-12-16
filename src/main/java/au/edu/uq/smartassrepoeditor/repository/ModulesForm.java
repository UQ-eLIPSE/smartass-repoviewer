/* This file is part of SmartAss and contains the ModulesForm class that 
 * contains SmartAss modules list
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.edu.uq.smartassrepoeditor.repository.data.ModulesDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.ModulesItemModel;

/**
 * The ModulesForm class contains the SmartAss modules list
 */
public class ModulesForm extends AbstractDBTableForm  implements ListSelectionListener {
	JTable table;
	JTextArea description = new JTextArea();
	JTextArea parameters = new JTextArea();
	
	
	class EditRowAction extends AbstractAction {
		
		public EditRowAction() {
			super("Edit");
		}
		
        public void actionPerformed(ActionEvent e) {
        	if(selected_row!=-1) {
            	ModuleEditForm form = 
            		new ModuleEditForm((ModulesItemModel) model.getItem(table_model.getValue(selected_row, "id")));
            	getParent().add(form);
            	form.pack();
               	form.setVisible(true);
            	form.toFront();
        	}
         }
	}

	class NewRowAction extends AbstractAction {
		public NewRowAction() {
			super("New");
		}
		
        public void actionPerformed(ActionEvent e) {
           	ModuleEditForm form = new ModuleEditForm((ModulesItemModel) model.newItem());
           	getParent().add(form);
           	form.pack();
           	form.setVisible(true);
           	form.toFront();
         }
	}

	
	public ModulesForm(ModulesDataModel model) {
		super("Modules", model);
		
		EditRowAction ea =  new EditRowAction();
		NewRowAction na = new NewRowAction();
		DeleteRowAction da =  new DeleteRowAction();

		JPanel buttons = new JPanel();
		buttons.add(new JButton(na));
		buttons.add(new JButton(da));
		buttons.add(new JButton(ea));
		buttons.add(new JButton(new RefreshAction()));
		add(buttons, BorderLayout.NORTH);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		table = createTable(table_model, new int[] {50, 100});
		table.getSelectionModel().addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(table);
		splitter.add(scrollPane);
		
		JPanel details = new JPanel();
		JLabel lb;
		details.setLayout(new BoxLayout(details, BoxLayout.PAGE_AXIS));
		details.add(lb = new JLabel("Parameters:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		parameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane = new JScrollPane(parameters);
		scrollPane.setPreferredSize(new Dimension(200,25));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.add(scrollPane);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		details.add(lb = new JLabel("Description:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		description.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane = new JScrollPane(description);
		scrollPane.setPreferredSize(new Dimension(200, 25));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.add(scrollPane);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		
		splitter.add(details);

		splitter.setDividerLocation(200);
		getContentPane().add(splitter);
		setSize(300, 100);
		
		setRowDeleteAction(table, da);
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doEditRow");
		table.getActionMap().put("doEditRow", ea);
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "doNewRow");
		table.getActionMap().put("doNewRow", na);

	}

	/**
	 *  Implementation of {@link ListSelectionListener}    
	 */
	public void valueChanged(ListSelectionEvent e) {
    	if(!e.getValueIsAdjusting()) {
    		selected_row = -1;
    		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) 
                if (((ListSelectionModel)e.getSource()).isSelectedIndex(i)) 
                    selected_row = i;
    		
    		if(selected_row!=-1) {
    			Object o = table_model.getValue(selected_row, "description");
    			if(o==null)
    				description.setText("");
    			else
    				description.setText(o.toString());
    			o = table_model.getValue(selected_row, "parameters");
    			if(o==null)
    				description.setText("");
    			else
    				parameters.setText(o.toString());
    		} else {
				description.setText("");
				description.setText("");
    		}
    	}
   	}
	
}
