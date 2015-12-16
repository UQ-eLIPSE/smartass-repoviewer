/* This file is part of SmartAss and contains the TemplateEditForm class that 
 * is the editor form for SmartAss  template metadata
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
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import au.edu.uq.smartassrepoeditor.repository.data.AuthorsItemModel;
import au.edu.uq.smartassrepoeditor.repository.data.Database;
import au.edu.uq.smartassrepoeditor.repository.data.TemplateMetadata;
import au.edu.uq.smartassrepoeditor.repository.data.TemplatesDataModel;
import au.edu.uq.smartassrepoeditor.repository.data.TemplatesItemModel;
import au.edu.uq.smartassrepoeditor.repository.data.ClassificationsDataModel.ClassificationsLookupModel;

/**
 * The TemplateEditForm class is the editor form for SmartAss template metadata
 */
public class TemplateEditForm extends ItemEditForm {
	Database database;
	TemplatesItemModel model;
	RepositoryStorage storage;
	boolean is_new;
	
	JTextField name;
	JTextField keywords; 
	JTextArea description;
	AbstractComboField path;
	PdfFilePanel questions; 
	PdfFilePanel solutions; 
	PdfFilePanel shortanswers;
	LookupComboField author;
	JTable classifications, files, modules, updates;
	
	AbstractComboField original_path;
	JPanel original_panel;
	JFileChooser file_dialog;
	JFileChooser dir_dialog;
	
	String tex_root, pdf_root, files_root;
	File original_file;
	boolean template_name_edited;

	class PdfFilePanel extends JPanel {
		JLabel filename, newfilepath;
		JButton add, view;
		File file = null;
		String title;
		
		public PdfFilePanel(String file_name, String title) {
			this.title = title; 
			view = new JButton(new AbstractAction("View") {
				public void actionPerformed(ActionEvent e) {
				}
			});
			add = new JButton(new AbstractAction("Add") {
				public void actionPerformed(ActionEvent e) {
					File f = doSelectFile();
					if(f!=null) { 
						newfilepath.setText("File to import: " + f.getAbsolutePath());
						file = f;
					}
				}
			});
			filename = new JLabel();
			filename.setAlignmentX(Component.LEFT_ALIGNMENT);
			setText(file_name);

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			newfilepath = new JLabel("");
			newfilepath.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(filename);
			JPanel pn = new JPanel();
			pn.setAlignmentX(Component.LEFT_ALIGNMENT);
			pn.add(view);
			pn.add(add);
			add(pn);
			add(newfilepath);
		}
		
		public File getFile() {
			return file;
		}
		
		public void setText(String text) {
			if(text.length()>0)
				filename.setText(title + text);
			else
				filename.setText(title + "No file");
		}
	
	}
	
	class SelectionKeeper implements ListSelectionListener {
		int current_row = -1;
		public void valueChanged(ListSelectionEvent e) {
	    	if(!e.getValueIsAdjusting()) {
	    		current_row = -1;
	    		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) 
	                if (((ListSelectionModel)e.getSource()).isSelectedIndex(i)) 
	                    current_row = i;
	   		}
		}
	}
	
	SelectionKeeper class_sel, files_sel, modules_sel, updates_sel;

	public TemplateEditForm(RepositoryStorage storage, TemplatesItemModel model) {
		super(model, "Edit template");
		System.out.println(">>>>>>>>>>>>>> Model: " + model.getDataModel().toString());
		database = model.getDataModel().getDatabase();
		System.out.println(">>>>>>>>>>>>> Model 1");
		this.storage = storage;
		this.is_new = model.getIsNew();
		System.out.println(">>>>>>>>>>>>> Model 2");
		//createControls();
		if(is_new) 
			setTitle("Add template");
		System.out.println(">>>>>>>>>>>>> Model 3");
		if (original_panel == null)
			System.out.println(">>>>>>>>>>>>> Original panel is null");
		//original_panel.setVisible(is_new);
		System.out.println(">>>>>>>>>>>>> Model 3.5");
		loadPrefs();
		System.out.println(">>>>>>>>>>>>> Model 4");
		//modelToControls();
		System.out.println(">>>>>>>>>>>>> Model 5");
	}
	
	protected void createControls() {
		this.model = ((TemplatesItemModel) super.model);
		
		JLabel lb;
		System.out.println(":: Getting here 1");
		original_panel = new JPanel();
		original_panel.setLayout(new BoxLayout(original_panel, BoxLayout.Y_AXIS));
		original_panel.add(lb = new JLabel("Path to template to be added:"));
		System.out.println(":: Getting here 2");
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		original_path = new AbstractComboField();
		original_path.setAlignmentX(Component.LEFT_ALIGNMENT);
		original_path.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(file_dialog==null)
					file_dialog = new JFileChooser();
				if(file_dialog.showOpenDialog(getMainThis())==JFileChooser.APPROVE_OPTION) {
					original_file = file_dialog.getSelectedFile();
					original_path.setText(original_file.getAbsolutePath());
					if(!template_name_edited) {
						String s = original_file.getName();
						if(s.indexOf(".tex")==s.length()-4)
							s = s.replaceFirst(".tex", "");
						name.setText(s);
						
						ImportMetadataDialog impd = new ImportMetadataDialog(database, model, original_file.getAbsolutePath());
						TemplateMetadata meta = impd.execute();
						if(meta==null)
							return;
						if(meta.getImportAuthor()) {					
							author.setText(meta.getAuthorItem().getName());
							author.setId(meta.getAuthorItem().getId());
						}
						if(meta.getImportKeywords())
							keywords.setText(meta.getKeywords());
						if(meta.getImportDescription())
							description.setText(meta.getDescription());
						meta.getClassifications().fillObjectArray(model.getClassifications(), false);
						meta.getModules().fillObjectArray(model.getModules(), false);
						meta.getFiles().fillObjectArray(model.getFiles(), false);
						meta.getUpdates().fillObjectArray(model.getUpdates(), false);
					}
				}		
			}
		});
		original_panel.add(original_path);
		original_panel.setVisible(false);
		
		System.out.println(":: Getting here 3");
		
		name = new JTextField();
		description = new JTextArea();
		keywords = new JTextField(); 
		path = new AbstractComboField();

		questions = new PdfFilePanel(model.getQuestions(), "Questions example: ");
		questions.setAlignmentX(Component.LEFT_ALIGNMENT);
		solutions = new PdfFilePanel(model.getSolutions(), "Solutions example: ");
		solutions.setAlignmentX(Component.LEFT_ALIGNMENT);
		shortanswers = new PdfFilePanel(model.getShortanswers(), "Shortanswers example: ");
		shortanswers.setAlignmentX(Component.LEFT_ALIGNMENT);

		System.out.println(":: Getting here 4");
		System.out.println(model.getDataModel().getDatabase().getAuthorsModel().getLookupModel().toString());
		author = new LookupComboField(model.getDataModel().getDatabase().getAuthorsModel().getLookupModel());
		System.out.println(":: Getting here 4.1");
		classifications = new JTable(model.getClassifications());
		files = new JTable(model.getFiles());
		System.out.println(":: Getting here 4.25");
		modules = new JTable(model.getModules());
		updates = new JTable(model.getUpdates());
		System.out.println(":: Getting here 4.5");
		classifications.getSelectionModel().addListSelectionListener(class_sel = new SelectionKeeper());
		files.getSelectionModel().addListSelectionListener(files_sel = new SelectionKeeper());
		System.out.println(":: Getting here 4.75");
		modules.getSelectionModel().addListSelectionListener(modules_sel = new SelectionKeeper());
		updates.getSelectionModel().addListSelectionListener(updates_sel = new SelectionKeeper());
		System.out.println(":: Getting here 5");
		JScrollPane sp;
		controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
		
		controls.add(original_panel);

		controls.add(lb = new JLabel("Templpate name:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		name.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(name);
		System.out.println(":: Getting here 6");
		controls.add(lb = new JLabel("Template path:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(path);
		path.setAlignmentX(Component.LEFT_ALIGNMENT);
		path.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(dir_dialog==null) {
					dir_dialog = new JFileChooser() {
						@Override
						public void changeToParentDirectory() {
							if(getCurrentDirectory().getParent().contains(tex_root))
								super.changeToParentDirectory();
						}
						
						@Override
						public void setCurrentDirectory(File dir) {
							if(dir==null || dir.getAbsolutePath().contains(tex_root))
								super.setCurrentDirectory(dir);
						}
					};
					dir_dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					dir_dialog.setCurrentDirectory(new File(tex_root));
				}
				if(dir_dialog.showOpenDialog(getMainThis())==JFileChooser.APPROVE_OPTION) {
					String s = dir_dialog.getSelectedFile().getAbsolutePath().replace(tex_root, "");
					if(s.length()>0)
						s = s.substring(1);
					path.setText(s);
				}
			}
		});
		
		controls.add(lb = new JLabel("Keywords:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(keywords);
		keywords.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		controls.add(lb = new JLabel("Author:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(author);
		author.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		controls.add(lb = new JLabel("Description"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(sp = new JScrollPane(description));
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp.setPreferredSize(new Dimension(200, 50));
		
		controls.add(questions);
		questions.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(solutions);
		solutions.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(shortanswers);
		shortanswers.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
		tabs.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(tabs);
		JButton bt;
		JPanel tp = new JPanel();
		JPanel tbpanel = new JPanel(new BorderLayout());
		
		tp.add(lb = new JLabel("Classifications:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				ClassificationsLookupModel lm = (ClassificationsLookupModel) database.getClassificationsModel().getLookupModel();
				ClassificationsLookupModel sm = (ClassificationsLookupModel) database.getClassificationsModel().getLookupModel();
				lm.setMaster(0);

				TemplateClassificationForm ld = new TemplateClassificationForm(lm, sm);
	            int row = ld.executeLookup();
	            if(row!=-1) {
		           	int sec_row = ld.getSecondaryRow(); 
		           	if(sec_row!=-1)
		           		model.getClassifications().addRow(database.getClassificationsModel().getItem(sm.getId(sec_row)));
		           	else
		           		model.getClassifications().addRow(database.getClassificationsModel().getItem(lm.getId(row)));
	            }
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Delete") {
			public void actionPerformed(ActionEvent e) {
				model.getClassifications().removeRow(class_sel.current_row);
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.setAlignmentX(Component.LEFT_ALIGNMENT);
		tbpanel.add(tp, BorderLayout.NORTH);
		tbpanel.add(sp = new JScrollPane(classifications));
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp.setPreferredSize(new Dimension(100,100));
		tabs.add("Classifications", tbpanel);
		
		tp = new JPanel();
		tbpanel = new JPanel(new BorderLayout());
		tp.add(lb = new JLabel("Files:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				LookupTableModel lm = database.getFilesModel().getLookupModel();
            	LookupTableDialog ld = new LookupTableDialog(lm);
            	int row = ld.executeLookup();
            	if(row!=-1)
            		model.getFiles().addRow(database.getFilesModel().getItem(lm.getId(row)));
            	
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Delete") {
			public void actionPerformed(ActionEvent e) {
				model.getFiles().removeRow(files_sel.current_row);
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.setAlignmentX(Component.LEFT_ALIGNMENT);
		tbpanel.add(tp, BorderLayout.NORTH);
		tbpanel.add(sp = new JScrollPane(files));
		tabs.add("Files", tbpanel);
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp.setPreferredSize(new Dimension(100,80));
		
		tp = new JPanel();
		tbpanel = new JPanel(new BorderLayout());
		tp.add(lb = new JLabel("Modules:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				LookupTableModel lm = database.getModulesModel().getLookupModel();
            	TemplatesModuleForm ld = new TemplatesModuleForm(lm);
            	int row = ld.executeLookup();
            	if(row!=-1) {
            		model.getModules().addRow(database.getModulesModel().getItem(lm.getValue(row, "id")));
            	}
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Delete") {
			public void actionPerformed(ActionEvent e) {
				model.getModules().removeRow(modules_sel.current_row);
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Edit") {
			public void actionPerformed(ActionEvent e) {
				if(modules_sel.current_row!=-1) {
/*					LookupTableModel lm = model.createModuleLookupModel();
	            	TemplatesModuleForm ld = new TemplatesModuleForm(lm, (Integer)model.getModulesList().getValue(modules_sel.current_row, "module_id"));
	            	ld.setParameters(model.getModulesList().getValue(modules_sel.current_row, "parameters").toString());
	            	int row = ld.executeLookup();
	            	if(row!=-1) {
	            		model.getModulesList().update(modules_sel.current_row, 
	            				(Integer)lm.getValue(row, "id"), ld.getParameters());
	            	}*/
				}
			}
		}));
		tp.setAlignmentX(Component.LEFT_ALIGNMENT);
		tbpanel.add(tp, BorderLayout.NORTH);
		tbpanel.add(sp = new JScrollPane(modules));
		tabs.add("Modules", tbpanel);
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp.setPreferredSize(new Dimension(100,80));
		
		tp = new JPanel();
		tbpanel = new JPanel(new BorderLayout());
		tp.add(lb = new JLabel("Updates:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Add") {
			public void actionPerformed(ActionEvent e) {
				LookupTableModel lm = database.getAuthorsModel().getLookupModel();
				TemplatesUpdateForm ld = new TemplatesUpdateForm(lm);
            	int row = ld.executeLookup();
            	if(row!=-1) {
            		TemplatesDataModel.TemplateUpdatesItemModel upd = ((TemplatesDataModel)model.getDataModel()).getUpdates().newItem();
            		upd.setAuthor((AuthorsItemModel)database.getAuthorsModel().getItem(lm.getValue(row, "id")));
            		upd.setComment(ld.getComment());
            		upd.setUpdateDate(ld.getDate());
            		model.getUpdates().addRow(upd);
            	}
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.add(bt = new JButton(new AbstractAction("Delete") {
			public void actionPerformed(ActionEvent e) {
				model.getUpdates().removeRow(updates_sel.current_row);
			}
		}));
		tp.add(bt = new JButton(new AbstractAction("Edit") {
			public void actionPerformed(ActionEvent e) {
				if(updates_sel.current_row!=-1) {
					TemplatesDataModel.TemplateUpdatesItemModel upd = 
						(TemplatesDataModel.TemplateUpdatesItemModel) model.getUpdates().getObject(updates_sel.current_row);
					LookupTableModel lm = database.getAuthorsModel().getLookupModel();
	            	TemplatesUpdateForm ld = new TemplatesUpdateForm(lm, upd.getAuthor().getId());
	            	ld.setComment(upd.getComment());
	            	ld.setDate(upd.getUpdateDate());
	            	int row = ld.executeLookup();
	            	if(row!=-1) {
	            		upd.setAuthor((AuthorsItemModel)database.getAuthorsModel().getItem(lm.getValue(row, "id")));
	            		upd.setComment(ld.getComment());
	            		upd.setUpdateDate(ld.getDate());
	            	}
				}
			}
		}));
		bt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		tp.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(tp);
		controls.add(sp = new JScrollPane(updates));
		tbpanel.add(tp, BorderLayout.NORTH);
		tbpanel.add(sp = new JScrollPane(updates));
		tabs.add("Updates", tbpanel);
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp.setPreferredSize(new Dimension(100,80));
		
	}
	
	protected void modelToControls() {
		name.setText(model.getName());
		description.setText(model.getDescription());
		keywords.setText(model.getKeywords());
		path.setText(model.getPath());
		questions.setText(model.getQuestions());
		solutions.setText(model.getSolutions());
		shortanswers.setText(model.getShortanswers());
		author.setData(model.getAuthorId(), model.getAuthorName());
	}
	
	protected void controlsToModel() {
		model.setName(name.getText());
		model.setDescription(description.getText());
		model.setKeywords(keywords.getText());
		if(questions.getFile()!=null)
			model.importQuestions(questions.getFile());
		if(solutions.getFile()!=null)
			model.importSolutions(solutions.getFile());
		if(shortanswers.getFile()!=null)
			model.importShortanswers(shortanswers.getFile());
		model.setPath(path.getText());
		model.setAuthorId((Integer)author.getId());
	}

	protected boolean validateData() {
		if(name.getText().length()==0) {
			JOptionPane.showMessageDialog(getMainThis(),
				    "Template name can't be empty!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//Move files in repository storage
		String msg_move;
		int res;
		if(is_new) {
			//Template to add into repository
			//create_dir==false, so we can ask late if user wants to create directory
			res = storage.importFile(RepositoryStorage.SCOPE_TEMPLATE, original_file.getAbsolutePath(), 
					new String[]{path.getText(), name.getText()+".tex"}, false);
			switch(res) {
			case RepositoryStorage.ERROR_INVALID_SRC:
				JOptionPane.showMessageDialog(getMainThis(),
					    "Can not add template! Incorrect path to original file!",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
				return false;
			case RepositoryStorage.ERROR_INVALID_DST_DIR:
				if(JOptionPane.showConfirmDialog(getMainThis(),
						"Directrory " + path.getText() + " does not exists!\nCreate directory?",
						null, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
					//create directory
					res = storage.importFile(RepositoryStorage.SCOPE_TEMPLATE, original_file.getAbsolutePath(), 
							new String[]{path.getText(), name.getText()+".tex"}, true);
			}
			msg_move = "add";
		} else { //Template is already in repository, check if we need move or rename it
			msg_move = "move";
			if(!model.getName().equals(name.getText()) || !model.getPath().equals(path.getText())) {
				res = storage.moveFile(RepositoryStorage.SCOPE_TEMPLATE, 
						new String[] {model.getPath(), model.getName()+".tex"}, 
						new String[]{path.getText(), name.getText()+".tex"}, false);
				if(res==RepositoryStorage.ERROR_INVALID_DST_DIR)
					storage.moveFile(RepositoryStorage.SCOPE_TEMPLATE, 
							new String[] {model.getPath(), model.getName()+".tex"}, 
							new String[]{path.getText(), name.getText()+".tex"}, true);
			} else
				res = RepositoryStorage.OK;
		}
		
		if(res!=RepositoryStorage.OK) {
			JOptionPane.showMessageDialog(getMainThis(),
			    "Can not " + msg_move + " template to \"" + (new File(new File(path.getText()), name.getText())).getPath()  + "\"! File copy error!",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}
	
	protected File doSelectFile() {
		if(file_dialog==null)
			file_dialog = new JFileChooser();
		if(file_dialog.showOpenDialog(getMainThis())==JFileChooser.APPROVE_OPTION) {
			return file_dialog.getSelectedFile();
		}
		return null;
	}
	
	protected TemplateEditForm getMainThis() {
		return this;
	}
	
	protected void loadPrefs() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass/repository");
		tex_root = prefs.get("tex_path", "");
		pdf_root = prefs.get("pdf_path", "");
		files_root = prefs.get("files_path", "");
	}
}
