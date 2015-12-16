/* This file is part of SmartAss and contains the FilesForm class that 
 * is the visual editor for Files dictionary
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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;

import au.edu.uq.smartassrepoeditor.repository.data.FilesDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.FilesDataModel.FilesRowSetModel;

/**
 * The FilesForm class is the visual editor for Files dictionary
 */
public class FilesForm extends AbstractDBTableForm  implements ListSelectionListener {
	JTable table;
	RepositoryStorage storage;
	JFileChooser file_dialog;

	class FileAddAction extends AbstractAction {
		public FileAddAction() {
			super("Add");
		}
		
		public void actionPerformed(ActionEvent e) {
			if(file_dialog==null)
				file_dialog = new JFileChooser();
			if(file_dialog.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
				File original_file = file_dialog.getSelectedFile();
				if(!storage.checkFileExists(RepositoryStorage.SCOPE_FILES, original_file.getName())) {
					storage.importFile(RepositoryStorage.SCOPE_FILES, original_file.getAbsolutePath(), original_file.getName(), false);
					table_model.setValue(original_file.getName(), table_model.getRowCount()+1, "name");
				} else 
					JOptionPane.showMessageDialog(null, "File \""+original_file.getName()+"\" already exists in repository", "Attention", JOptionPane.INFORMATION_MESSAGE);
			}		
		}
	}
	
	public FilesForm(RepositoryStorage storage, FilesDataModel model) {
		super("Files", model);
		this.storage = storage;
		table_model.setHasInsertRow(false);
		((FilesRowSetModel)table_model).setStorage(storage);
		
		JPanel buttons = new JPanel();
		FileAddAction aa = new FileAddAction();
		buttons.add(new JButton(aa));
		DeleteRowAction da = new DeleteRowAction(); 
		buttons.add(new JButton(da));
		add(buttons, BorderLayout.NORTH);
		buttons.add(new JButton(new RefreshAction()));
		
		table = createTable(table_model, new int[]{50, 100});
		add(new JScrollPane(table));
		setSize(300, 200);
		
		table.getSelectionModel().addListSelectionListener(this);
		setRowDeleteAction(table, da);
	}

}
