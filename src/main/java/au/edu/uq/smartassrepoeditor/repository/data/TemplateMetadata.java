/* This file is part of SmartAss and contains the TemplateMetadata class that
 * is used for different metadata-related tasks such as import metadata form the text representation
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JOptionPane;

/**
 * The TemplateMetadata class
 * is used for different metadata-related tasks such as import metadata form the text representation
 */
public class TemplateMetadata {
	Database database;
	String name = "";
	String author = "";
	String keywords = "";
	String description = "";

	CheckArrayTableModel classifications = new CheckArrayTableModel(new String[]{"Classification"}) {
		@Override
		protected boolean processNoItem(Object obj) {
			String str = (String) obj;
			if(JOptionPane.showConfirmDialog(null,
					"Classification \"" + str + "\" does not found in repository.\nAdd this classification?" +
							"\n(changes will be written to database immidiately, so they will not be undone if you chose to not import this template)",
					null, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
			{
				String [] spl = str.split("/");
				int parent_id=0;
				String name;
				if(spl.length==2) {
					ClassificationItemModel parent = (ClassificationItemModel) database.getClassificationsModel().getItem("name=? and parent_id=?",
							new Object[] {spl[0].trim(), 0});
					if(parent==null) {
						parent = (ClassificationItemModel) database.getClassificationsModel().newItem();
						parent.setName(spl[0].trim());
						parent.update();
					}
					parent_id = parent.getId();
					name = spl[1];
				} else
					name = spl[0];
				ClassificationItemModel cl = (ClassificationItemModel) database.getClassificationsModel().newItem(parent_id);
				cl.setName(name.trim());
				cl.update();
				items.put(obj, cl);
				return true;
			}
			return false;
		}
	};
	CheckArrayTableModel files = new CheckArrayTableModel(new String[]{"File", "Description"}) {
		@Override
		protected boolean processNoItem(Object obj) {
			String[] str = (String[]) obj;
			JOptionPane.showMessageDialog(null,
					"File \"" + str[0] + "\" does not found in repository.\nImport this file before your can add it to \"Files\" section of template metadata!");
			return false;
		}

	};
	CheckArrayTableModel modules = new CheckArrayTableModel(new String[]{"Module"}) {
		@Override
		protected boolean processNoItem(Object obj) {
			String str = (String) obj;
			if(JOptionPane.showConfirmDialog(null,
					"The module \"" + str + "\" does not found in the repository.\nDo you want add this module?" +
							"\n(changes will be written to database immidiately, so they will not be undone if you chose to not import this template)",
					null, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
			{
				String[] spl = database.getModulesModel().splitFullName(str);
				ModulesItemModel mod = (ModulesItemModel) database.getModulesModel().newItem();
				mod.setName(spl[1]);
				mod.setModulePackage(spl[0]);
				mod.update();
				items.put(obj, mod);
				return true;
			}
			return false;
		}
	};
	CheckArrayTableModel updates = new CheckArrayTableModel(new String[]{"Upd. date", "Author", "Comment"}) {
		@Override
		protected Object prepareObjectToFill(Object obj) {
			AuthorsItemModel auth = (AuthorsItemModel) items.get(obj);
			if(auth!=null) {
				String[] str = (String[]) obj;
				TemplatesDataModel.TemplateUpdatesItemModel upd = database.getTemplatesModel().getUpdates().newItem();
				upd.setAuthor(auth);
				try {
					upd.setUpdateDate(new java.sql.Date( (new SimpleDateFormat("yyyy-dd-MM")).parse(str[0]).getTime() ) );
				} catch (ParseException e) {
					return null;
				}
				upd.setComment(str[2]);
				return upd;
			}
			return null;
		}

		@Override
		protected boolean processNoItem(Object obj) {
			String str[] = (String[]) obj;
			if(JOptionPane.showConfirmDialog(null,
					"Author \"" + str[1] + "\" does not found in repository.\nDo yoou want add this author?" +
							"\n(changes will be written to database immidiately, so they will not be undone if you chose to not import this template)",
					null, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
			{
				AuthorsItemModel auth = (AuthorsItemModel) database.getAuthorsModel().newItem();
				auth.setName(str[1]);
				auth.update();
				items.put(obj, auth);
				return true;
			}
			return false;
		}
};

	AuthorsItemModel author_item;

	boolean importAuthor;
	boolean importKeywords;
	boolean importDescription;

	public class CheckArrayTableModel extends StringArrayTableModel {
		protected Vector<Boolean> checks;
		protected HashMap<Object, ItemDataModel> items;

		public CheckArrayTableModel(String[] headers) {
			column_count = headers.length + 1;
			this.headers = new String[headers.length+1];
			this.headers[0] = "Import";
			for(int i=0;i<headers.length;i++)
				this.headers[i+1] = headers[i];
			checks = new Vector<Boolean>();
			items = new HashMap<Object, ItemDataModel>();
		}

		@Override
		public void addRow(Object obj) {
			checks.add(false);
			super.addRow(obj);
		}

		public void setItem(Object key, ItemDataModel item) {
			int pos = objects.indexOf(key);
			if(pos>=0) {
				items.put(key, item);
				checks.set(pos, true);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(columnIndex==0)
				return checks.get(rowIndex);
/*				if(checks.get(columnIndex))
					return "+";
				else
					return "-";*/
			else
				return super.getValueAt(rowIndex, columnIndex-1);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if(columnIndex==0)
				return Boolean.class;
			return super.getColumnClass(columnIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex==0)
				return true;
			return super.isCellEditable(rowIndex, columnIndex);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex==0) {
				boolean check = (Boolean) aValue;
				if(check) {
					Object oi = items.get(getObject(rowIndex));
					if(oi==null && !processNoItem(getObject(rowIndex)))
						return;
				}
				checks.set(rowIndex, check);
			}
			super.setValueAt(aValue, rowIndex, columnIndex);
		}

		public void fillObjectArray(ObjectArrayTableModel objs, boolean clear) {
			if(clear)
				objs.clearAll();
			for(int i=0; i<objects.size();i++)
				 if(checks.get(i)) {
					 Object oi = prepareObjectToFill(objects.get(i));
					 if(oi!=null)
						 objs.addRow(oi);
				 }
		}

		protected Object prepareObjectToFill(Object obj) {
			 return items.get(obj);
		}

		protected boolean processNoItem(Object obj) {
			return false;
		}
	}

	public TemplateMetadata(Vector<String> metadata, String filename, Database database) {
		name = filename;
		this.database = database;
		parseMetadata(metadata);
	}

	protected void parseMetadata(Vector<String> meta) {
		int state=0;
		String[] sfile = {};
		String[] supdate = {};
		for(String s : meta) {
			if(s.indexOf("%%")==0) {
				switch(state) {
				case 2:
					files.addRow(sfile);
					FilesItemModel file = (FilesItemModel) database.getFilesModel().getItem("name", sfile[0]);
					if(file!=null)
						files.setItem(sfile, file);
					break;
				case 3:
					updates.addRow(supdate);
					AuthorsItemModel auth = (AuthorsItemModel) database.getAuthorsModel().getItem("name", supdate[1]);
					if(auth!=null)
						updates.setItem(supdate, auth);
				}
				state = 0;
			}

			switch(state) {
			case 0:
				if(s.contains("%%AUTHOR")) {
					author = s.replace("%%AUTHOR", "").trim();
					author_item = (AuthorsItemModel) database.getAuthorsModel().getItem("name", author);
				} else if(s.contains("%%KEYWORDS")) {
					keywords = s.replace("%%KEYWORDS", "").trim();
				} else if(s.contains("%%DESCRIPTION")) {
					description = s.replace("%%DESCRIPTION", "");
					state = 1;
				} else if(s.contains("%%CLASSIFICATION")) {
					classifications.addRow(s = s.replace("%%CLASSIFICATION", "").trim());
					String [] spl = s.split("/");
					ClassificationItemModel cit[] = new ClassificationItemModel[2];
					cit[0] = (ClassificationItemModel) database.getClassificationsModel().getItem("name=? and parent_id=?",
							new Object[] {spl[0], 0});
					if(cit[0]!=null)
						if(spl.length==1)
							classifications.setItem(s, cit[0]);
						else {
							cit[1] = (ClassificationItemModel) database.getClassificationsModel().getItem("name=? and parent_id=?",
									new Object[] {spl[1], cit[0].getId()});
							if(cit[1]!=null)
								classifications.setItem(s, cit[1]);
						}
				} else if(s.contains("%%FILE")) {
					sfile = new String[]{s.replace("%%FILE", "").trim(), ""};
					state = 2;
				} else if(s.contains("%%MODULE")) {
					modules.addRow(s = s.replace("%%MODULE", "").trim());
					ModulesItemModel mod = (ModulesItemModel) database.getModulesModel().getItemByFullName(s);
					if(mod!=null)
						modules.setItem(s, mod);
				} else if(s.contains("%%UPDATE")) {
					String[] spl = s.replace("%%UPDATE", "").split(",", 2);
					if(spl.length==1)
						supdate = new String[]{spl[0], "", ""};
					else
						supdate = new String[]{spl[0].trim(), spl[1].trim(), ""};
					state = 3;
				}
				break;
			case 1:
				description = description + "\n" + s.replace("%", "");
				break;
			case 2:
				sfile[1] = sfile[1] + "\n" + s.replace("%", "");
				break;
			case 3:
				supdate[2] = supdate[2] + "\n" + s.replace("%", "");
			}
		}
		switch(state) {
		case 2:
			files.addRow(sfile);
			FilesItemModel file = (FilesItemModel) database.getFilesModel().getItem("name", sfile[0]);
			if(file!=null)
				files.setItem(sfile, file);
			break;
		case 3:
			updates.addRow(supdate);
			AuthorsItemModel auth = (AuthorsItemModel) database.getAuthorsModel().getItem("name", supdate[1]);
			if(auth!=null)
				updates.setItem(supdate, auth);
		}
	}

	public boolean processNoAuthor() {
		if(JOptionPane.showConfirmDialog(null,
				"Author \"" + author + "\" does not found in repository.\nDo yoou want add this author?" +
						"\n(changes will be written to database immidiately, so they will not be undone if you chose to not import this template)",
				null, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
		{
			AuthorsItemModel auth = (AuthorsItemModel) database.getAuthorsModel().newItem();
			auth.setName(author);
			auth.update();
			author_item = auth;
			return true;
		}
		return false;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
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
		this.name = name;
	}

	public CheckArrayTableModel getClassifications() {
		return classifications;
	}

	public CheckArrayTableModel getFiles() {
		return files;
	}

	public CheckArrayTableModel getModules() {
		return modules;
	}

	public AuthorsItemModel getAuthorItem() {
		return author_item;
	}

	public void setAuthorItem(AuthorsItemModel author_item) {
		this.author_item = author_item;
	}

	public boolean getImportAuthor() {
		return importAuthor;
	}

	public void setImportAuthor(boolean importAuthor) {
		this.importAuthor = importAuthor;
	}

	public boolean getImportDescription() {
		return importDescription;
	}

	public void setImportDescription(boolean importDescription) {
		this.importDescription = importDescription;
	}

	public boolean getImportKeywords() {
		return importKeywords;
	}

	public void setImportKeywords(boolean importKeywords) {
		this.importKeywords = importKeywords;
	}

	public CheckArrayTableModel getUpdates() {
		return updates;
	}

}


/*			    		PreparedStatement sql = model.connection.prepareStatement("select id from authors where name=?");
	sql.setString(1, s);
	ResultSet rs = sql.executeQuery();
	if(rs.first()) {
		author.id = rs.getObject(1);
		author.setText(s);
	} else {
		if(JOptionPane.showConfirmDialog(getMainThis(),
				"Author " + s + " does not exists in repository!\nAdd this author?",
				null, JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
			PreparedStatement sqlu = model.connection.prepareStatement("insert into authors where name=?");
		}

	}
}catch (SQLException e) {
	e.printStackTrace();
}*/
