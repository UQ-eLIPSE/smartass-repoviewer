/* This file is part of SmartAss and contains the TemplatesForm class that 
 * contains the form with the list of SmartAss templates
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
import javax.swing.JFileChooser;
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

import au.edu.uq.smartassrepoeditor.repository.data.AbstractDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.Database;
import au.edu.uq.smartassrepoeditor.repository.data.ItemDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.RowSetTableModel;
import au.edu.uq.smartassrepoeditor.repository.data.TemplatesItemModel;

/**
 * The TemplatesForm class contains the form with the list of SmartAss templates
 */
public class TemplatesForm extends AbstractDBTableForm implements ListSelectionListener {
	JTable table;
	JTextArea description = new JTextArea();
	RepositoryStorage storage;
	
	JFileChooser file_dialog;
	
	class EditRowAction extends AbstractAction {
		
		public EditRowAction() {
			super("Edit");
		}
		
        public void actionPerformed(ActionEvent e) {
        	if(selected_row!=-1) {
        		try {
        			//model = new AbstractDataModel(new Database("jdbc:mysql://localhost/smartassignments", "smartass", "SmartAss", new RepositoryStorage()));
	            	TemplateEditForm form = new TemplateEditForm(storage, 
	            			(TemplatesItemModel) model.getItem(table_model.getValue(selected_row, "id")));
	            	getParent().add(form);
	            	form.pack();
	               	form.setVisible(true);
	            	form.toFront();
        		} catch (NullPointerException exception) {
        			//exception.printStackTrace();
        			System.out.println("The error occured");
        		}
        	}
         }
	}

	class NewRowAction extends AbstractAction {
		
		public NewRowAction() {
			super("Add");
		}
		
        public void actionPerformed(ActionEvent e) {
        	TemplatesItemModel itemModel = (TemplatesItemModel) model.newItem();
        	
           	TemplateEditForm form = new TemplateEditForm(storage, itemModel);
           	/*getParent().add(form);
           	form.pack();
           	form.setVisible(true);
           	form.toFront();
           	*/
         }
	}
	
	class TemplateDeleteAction extends DeleteRowAction {
		
		@Override
		protected void doDeleteRow(int row, RowSetTableModel model) {
			String path;
			if(model.getValue(selected_row, "path").toString()==null) 
				path = "";
			else
				path = model.getValue(selected_row, "path").toString();

			storage.deleteFile(RepositoryStorage.SCOPE_TEMPLATE, 
					new String[]{path, model.getValue(selected_row, "name").toString()+".tex"});
			if(model.getValue(selected_row, "questions")!=null)
				storage.deleteFile(RepositoryStorage.SCOPE_TEMPLATE, 
						new String[]{path, model.getValue(selected_row, "questions").toString()});
			if(model.getValue(selected_row, "solutions")!=null) 
				storage.deleteFile(RepositoryStorage.SCOPE_TEMPLATE, 
						new String[]{path, model.getValue(selected_row, "solutions").toString()});
			if(model.getValue(selected_row, "shortanswers")!=null) 
				storage.deleteFile(RepositoryStorage.SCOPE_TEMPLATE, 
						new String[]{path, model.getValue(selected_row, "shortanswers").toString()});
			super.doDeleteRow(row, model);
		}
	}
	
	class ExportAction extends AbstractAction {
		
		public ExportAction() {
			super("Export");
		}
		
		public void actionPerformed(ActionEvent e) {
			if(file_dialog==null)
				file_dialog = new JFileChooser();
			if(file_dialog.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
				
            	TemplatesItemModel dm = (TemplatesItemModel) model.getItem(table_model.getValue(selected_row, "id"));
            	storage.exportFile(RepositoryStorage.SCOPE_TEMPLATE, 
            			new String[]{dm.getPath(), dm.getName() + ".tex"}, 
            			file_dialog.getSelectedFile().getAbsolutePath(), 
            			dm.metadataToString());

			}
			
		}
	}

	
	public TemplatesForm(RepositoryStorage storage, AbstractDataModel model) {
		super("Templates", model);
		
		this.storage = storage;
		
		EditRowAction ea =  new EditRowAction();
		NewRowAction na = new NewRowAction();
		DeleteRowAction da =  new TemplateDeleteAction();

		JPanel buttons = new JPanel();
		buttons.add(new JButton(na));
		buttons.add(new JButton(da));
		buttons.add(new JButton(ea));
		buttons.add(new JButton(new RefreshAction()));
		buttons.add(new JButton(new ExportAction()));
		add(buttons, BorderLayout.NORTH);

		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		table = createTable(table_model, new int[] {50, 100});
		table.getSelectionModel().addListSelectionListener(this);
		JScrollPane scrollPane = new JScrollPane(table);
		splitter.add(scrollPane);
		
		JPanel details = new JPanel();
		JLabel lb;
		details.setLayout(new BoxLayout(details, BoxLayout.PAGE_AXIS));
		details.add(lb = new JLabel("Description:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		JScrollPane sp = new JScrollPane(description);
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		details.add(sp);
		details.add(Box.createRigidArea(new Dimension(0,5)));
		
		splitter.add(details);

		splitter.setDividerLocation(200);
		add(splitter);
		setSize(300, 100);
		
		setRowDeleteAction(table, da);
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doEditRow");
		table.getActionMap().put("doEditRow", new EditRowAction());
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).
				put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "doNewRow");
		table.getActionMap().put("doNewRow", new NewRowAction());

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
    		} else
    			description.setText("");
    	}
   	}
}
