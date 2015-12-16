/* This file is part of SmartAss and contains the TemplatesItemModel class that 
 * is the model for the template item
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
package au.edu.uq.smartassrepoeditor.repository.data;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;


import au.edu.uq.smartassrepoeditor.repository.RepositoryStorage;
import au.edu.uq.smartassrepoeditor.repository.data.TemplatesDataModel.TemplateUpdatesItemModel;

/**
 * The TemplatesItemModel class is the model for the template item
 */
public class TemplatesItemModel extends IntIdItemModel {
	//Stored data
	private String name = "";
	private String keywords = "";
	private String description = "";
	private String path = "";
	private String questions = "";
	private String solutions = "";
	private String shortanswers = "";
	private int author_id;

	//Data from dictionaries
	private String author_name;
	
	//auxilliary fields
	File tex_root; 

	
	//Detail tables
	TemplatesDataModel.TemplateClassificationsModel classifications;
	TemplatesDataModel.TemplateFilesModel files;
	TemplatesDataModel.TemplateModulesModel modules;
	TemplatesDataModel.TemplateUpdatesModel updates;
	
	//Auxiliary members
	RepositoryStorage storage;
	String old_name = null, old_path = null;
	File new_pdfs[] = new File[3];

	public TemplatesItemModel(TemplatesDataModel data) {
		super(data);
		this.storage = data.getDatabase().getStorage();
		classifications = data.createTemplateClassificationsModel(this);
		files = data.createTemplateFilesModel(this);
		modules = data.createTemplateModulesModel(this);
		updates = data.createTemplateUpdatesModel(this);
	}

	public String metadataToString() {
		String meta = "%%META\n";
		meta = meta + "%%AUTHOR " + getAuthorName() +"\n";
		meta = meta + "%%KEYWORDS " + getKeywords() +"\n";
		meta = meta + "%%DESCRIPTION " + getDescription().replace("\n", "\n%") +"\n";
		String cmstr = "";
		/*for(int i=0; i<classifications.getRowCount(); i++) 
			cmstr = cmstr + "%%CLASSIFICATION " + classifications.getValueAt(i, 1) + "\n";
		meta = meta + cmstr; 
		cmstr = "";*/
		for(int i=0; i<files.getRowCount(); i++) {
			FilesItemModel file = (FilesItemModel) files.getObject(i);
			cmstr = cmstr + "%%FILE " + file.getName();
			String desc = file.getDescription();
			if(desc!=null && desc.toString().length()>0)
				cmstr = cmstr + "\n%" + desc.toString().replace("\n", "\n%");
			cmstr = cmstr + "\n";
		}
		meta = meta + cmstr;
		
		cmstr = "";
		for(int i=0; i<modules.getRowCount(); i++) {
			cmstr = cmstr + "%%MODULE ";
			ModulesItemModel mod = (ModulesItemModel) modules.getObject(i); 
			if(mod.getModulePackage()!=null && mod.getModulePackage().length()>0)
				cmstr = cmstr + mod.getModulePackage() + ".";
			cmstr = cmstr + mod.getName() + "\n";
		}
		meta = meta + cmstr;
		
		cmstr = "";
		for(int i=0; i<updates.getRowCount(); i++) {
			TemplateUpdatesItemModel upd = (TemplateUpdatesItemModel) updates.getObject(i);
			cmstr = cmstr + "%%UPDATE " + upd.getUpdateDate() + ", " + upd.getAuthor().getName();
			if(upd.getComment()!=null && upd.getComment().toString().length()>0)
				cmstr = cmstr + "\n%" + upd.getComment().replace("\n", "\n%") +"\n";
		}
		meta = meta + cmstr;
		
		meta = meta + "%%META END\n";
		return meta;
	}
	
	// Getters and setters for model fields
	public int getAuthorId() {
		return author_id;
	}

	public void setAuthorId(int author_id) {
		this.author_id = author_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(old_name==null)
			old_name = this.name;
		this.name = name;
	}

	public String getQuestions() {
		return questions;
	}

	public void importQuestions(File new_questions) {
		new_pdfs[0] = new_questions;
	}

	public String getShortanswers() {
		return shortanswers;
	}

	public void importShortanswers(File new_shotanswers) {
		new_pdfs[1] = new_shotanswers;
	}

	public String getSolutions() {
		return solutions;
	}

	public void importSolutions(File new_solutions) {
		new_pdfs[2] = new_solutions;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		old_path = this.path;
		this.path = path;
	}

	public String getAuthorName() {
		return author_name;
	}
	
	
	@Override
	protected void readFromModel(ResultSet rs) throws SQLException {
		super.readFromModel(rs);
		id = rs.getInt("id");
		name = rs.getString("name");
		description = rs.getString("description");
		keywords = rs.getString("keywords");
		path = rs.getString("path");
		questions = rs.getString("questions");
		solutions = rs.getString("solutions");
		shortanswers = rs.getString("shortanswers");
		author_id = rs.getInt("author_id");
		AuthorsItemModel author = (AuthorsItemModel)data.getDatabase().getAuthorsModel().getItem(author_id); 
		if(author!=null)
			author_name = author.getName(); 
		
		modules = ((TemplatesDataModel)data).createTemplateModulesModel(this);
		classifications = ((TemplatesDataModel)data).createTemplateClassificationsModel(this);
		files = ((TemplatesDataModel)data).createTemplateFilesModel(this);
		updates = ((TemplatesDataModel)data).createTemplateUpdatesModel(this);
	}
	
	public TemplatesDataModel.TemplateModulesModel getModules() {
		return modules;
	}

	public TemplatesDataModel.TemplateClassificationsModel getClassifications() {
		return classifications;
	}

	public TemplatesDataModel.TemplateFilesModel getFiles() {
		return files;
	}
	
	public TemplatesDataModel.TemplateUpdatesModel getUpdates() {
		return updates;
	}
	
	@Override
	public void update() {
		questions = processPdfFile(new_pdfs[0], questions, "questions");
		solutions = processPdfFile(new_pdfs[1], solutions, "soluitons");
		shortanswers = processPdfFile(new_pdfs[2], shortanswers, "shortanswers");

		super.update();
	}

	private String processPdfFile(File new_file, String filename, String suffix) {
		if(new_file!=null) {
			if(filename.length()>0)
				storage.deleteFile(RepositoryStorage.SCOPE_PDF, 
						new String[] {path, filename});
			storage.importFile(RepositoryStorage.SCOPE_PDF, new_file.getAbsolutePath(), 
					new String[] {path, name + "_" + suffix + ".pdf"}, true);
		} else if(filename.length()>0 && (old_name!=null || old_path!=null)) 
			//the file is present in the repository and template name or template path is changed
		{
			if(old_name==null)
				old_name = name;
			if(old_path==null)
				old_path = path;
			storage.moveFile(RepositoryStorage.SCOPE_PDF, 
					new String[] {old_path, old_name + "_" + suffix + ".pdf"},
					new String[] {path, name + "_" + suffix + ".pdf"}, true);
		} 
		if(new_file==null && filename.length()==0)
			return "";
		return name + "_" + suffix + ".pdf";
	}
}
