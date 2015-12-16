/* This file is part of SmartAss and contains the ImportMetadataDialog class that 
 * is used to import the template metadata to the repository
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import au.edu.uq.smartassrepoeditor.repository.data.Database;
import au.edu.uq.smartassrepoeditor.repository.data.TemplateMetadata;
import au.edu.uq.smartassrepoeditor.repository.data.TemplatesItemModel;

/**
 * The ImportMetadataDialog class is used to import the template metadata to the repository
 */
public class ImportMetadataDialog extends JDialog {
	Database database;
	TemplatesItemModel template;
	TemplateMetadata metadata;

	JTable classifications;
	JTable files;
	JTable modules;
	JTextField author;
	JTextField keywords;
	JTextArea description;
	JCheckBox keywords_cb;
	JCheckBox description_cb;
	JCheckBox author_cb;
	
	boolean is_ok = false;
	
	
	class CheckTable extends JTable {
		public CheckTable(AbstractTableModel model) {
			super(model);
			getColumnModel().getColumn(0).setPreferredWidth(50);
			getColumnModel().getColumn(0).setMaxWidth(50);
			for(int i=1;i<getColumnCount();i++)
				getColumnModel().getColumn(i).setPreferredWidth(200);
		}
	}
	
	public ImportMetadataDialog(Database database, TemplatesItemModel template, String filename) {
		this.template = template;
		this.database = database;
		setTitle("Metadata import");
		setModal(true);
		
		metadata = new TemplateMetadata(database.getStorage().readMetadata(filename), new File(filename).getName(), database);
		
		JPanel buttons = new JPanel();
		JPanel controls = new JPanel();

		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		JPanel tp = new JPanel();
		tp.setLayout(new BoxLayout(tp, BoxLayout.PAGE_AXIS));

		tp.add(author_cb = new JCheckBox("Author:"));
		author_cb.setAlignmentX(Component.LEFT_ALIGNMENT);
		author_cb.setSelected(metadata.getAuthorItem()!=null);
		author_cb.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange()==ItemEvent.SELECTED) {
						if(metadata.getAuthorItem()==null)
							author_cb.setSelected(metadata.processNoAuthor());
					}
				}	
		});
		author = new JTextField(metadata.getAuthor());
		author.setAlignmentX(Component.LEFT_ALIGNMENT);
		author.setMaximumSize(new Dimension(1000, 24));
		tp.add(author);
		
		tp.add(keywords_cb = new JCheckBox("Keywords:"));
		keywords_cb.setAlignmentX(Component.LEFT_ALIGNMENT);
		keywords_cb.setSelected(true);
		keywords = new JTextField(metadata.getKeywords());
		keywords.setAlignmentX(Component.LEFT_ALIGNMENT);
		keywords.setMaximumSize(new Dimension(1000, 24));
		tp.add(keywords);
		
		tp.add(description_cb = new JCheckBox("Description:"));
		description_cb.setAlignmentX(Component.LEFT_ALIGNMENT);
		description_cb.setSelected(true);
		description = new JTextArea(metadata.getDescription());
		description.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.add(description);
		tabs.add("Common", tp);
		
		classifications = new CheckTable(metadata.getClassifications());
		tabs.add("Classifications", new JScrollPane(classifications));
		files = new CheckTable(metadata.getFiles());
		tabs.add("Files", new JScrollPane(files));
		modules = new CheckTable(metadata.getModules());
		tabs.add("Modules", new JScrollPane(modules));
		JTable updates = new JTable(metadata.getUpdates());
		tabs.add("Updates", new JScrollPane(updates));

		tabs.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(tabs);
		JPanel tbpanel = new JPanel(new BorderLayout());

		
		buttons.add(new JButton(new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				is_ok = true;
				setVisible(false);
			}
		}));
		buttons.add(new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}));
		
		add(controls);
		add(buttons, BorderLayout.SOUTH);
		pack();
	}

	public TemplateMetadata execute() {
		setVisible(true);
		if(is_ok) {
			metadata.setImportAuthor(author_cb.isSelected());
			metadata.setImportDescription(description_cb.isSelected());
			metadata.setImportKeywords(keywords_cb.isSelected());
			return metadata;
		}
		dispose();
		return null;
	}
	
}
