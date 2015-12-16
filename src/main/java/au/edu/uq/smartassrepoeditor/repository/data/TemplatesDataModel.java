/* This file is part of SmartAss and contains the TemplatesDataModel class that
 * is the model for templates table
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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.RowSet;

/**
 * The TemplatesDataModel class is the model for templates table
 */
public class TemplatesDataModel extends IntIdDataModel {

	TemplateUpdatesDataModel updates;

	public class TemplateModulesModel extends ObjectArrayTableModel {
		public TemplateModulesModel() {
			column_count = 2;
			headers = new String[]{"Name", "Package"};
		}

		@Override
		protected Object getValueFromObject(Object obj, int columnIndex) {
			ModulesItemModel mi = (ModulesItemModel) obj;
			switch(columnIndex) {
			case 0:
				return mi.getName();
			case 1:
				return mi.getModulePackage();
			}
			return "";
		}
	}

	public class TemplateFilesModel extends ObjectArrayTableModel {
		public TemplateFilesModel() {
			column_count = 2;
			headers = new String[]{"Name", "Description"};
		}

		@Override
		protected Object getValueFromObject(Object obj, int columnIndex) {
			FilesItemModel mi = (FilesItemModel) obj;
			switch(columnIndex) {
			case 0:
				return mi.getName();
			case 1:
				return mi.getDescription();
			}
			return "";
		}
	}

	public class TemplateClassificationsModel extends ObjectArrayTableModel {
		public TemplateClassificationsModel() {
			headers = new String[]{"Name", "Description"};
		}

		@Override
		protected Object getValueFromObject(Object obj, int columnIndex) {
			if(obj==null)
				return "";
			ClassificationItemModel mi = (ClassificationItemModel) obj;
			if(mi.getParentId()==0)
				return mi.getName();
			return mi.getParent().getName() + "/" + mi.getName();
		}
	}

	public class TemplateUpdatesDataModel extends IntIdDataModel {
		public TemplateUpdatesDataModel(Database data) {
			super(data);
			fields = new String[] {"template_id", "author_id", "update_date", "comment", "id"};
			table_name = "updates";
		}

		@Override
		public TemplateUpdatesItemModel newItem() {
			return new TemplateUpdatesItemModel(this);
		}

		@Override
		protected void dataFromItemModel(PreparedStatement st, ItemDataModel dm) {
			TemplateUpdatesItemModel m = (TemplateUpdatesItemModel) dm;
			super.dataFromItemModel(st, dm);
			try {
				st.setInt(1, m.getTemplateId());
				st.setInt(2, m.getAuthor().getId());
				st.setDate(3, m.getUpdateDate());
				st.setString(4, m.getComment());
				if(!m.getIsNew())
					st.setInt(5, m.getId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public class TemplateUpdatesItemModel extends IntIdItemModel {
		private int template_id;
		private AuthorsItemModel author;
		private Date update_date;
		private String comment;

		public TemplateUpdatesItemModel(TemplateUpdatesDataModel data) {
			super(data);
		}

		@Override
		protected void readFromModel(ResultSet rs) throws SQLException {
			super.readFromModel(rs);
			template_id = rs.getInt("template_id");
			author = (AuthorsItemModel) data.getDatabase().getAuthorsModel().getItem(rs.getInt("author_id"));
			update_date = rs.getDate("update_date");
			comment = rs.getString("comment");
		}

		public AuthorsItemModel getAuthor() {
			return author;
		}

		public void setAuthor(AuthorsItemModel author) {
			this.author = author;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public int getTemplateId() {
			return template_id;
		}

		public void setTemplateId(int template_id) {
			this.template_id = template_id;
		}

		public Date getUpdateDate() {
			return update_date;
		}

		public void setUpdateDate(Date update_date) {
			this.update_date = update_date;
		}
	}

	public class TemplateUpdatesModel extends ObjectArrayTableModel {
		public TemplateUpdatesModel() {
			column_count = 2;
		}

		@Override
		protected Object getValueFromObject(Object obj, int columnIndex) {
			TemplateUpdatesItemModel im = (TemplateUpdatesItemModel) obj;
			switch(columnIndex) {
			case 0:
				return im.getAuthor().getName();
			case 1:
				return im.getUpdateDate();
			case 3:
				return im.getComment();
			}
			return "";
		}

	}

	public TemplatesDataModel(Database data) {
		super(data);
		fields = new String[] {"name", "keywords", "description", "path", "author_id", "questions", "solutions", "shortanswers", "id" };
		table_name = "templates";
		updates = new TemplateUpdatesDataModel(data);
	}

	@Override
	public ItemDataModel newItem() {
		return new TemplatesItemModel(this);
	}

	@Override
	protected String composeRowsetSql(String order_clause) {
		return "select t.*, a.name author_name from templates t left join authors a on t.author_id=a.id";
	}

	@Override
	protected RowSetTableModel createTableModel(RowSet rs, boolean readonly) {
    	RowSetTableModel m = new TemplatesTableModel(rs, readonly);
    	m.setVisibleColumns(new String[]{"name", "path", "keywords", "author_name"});
		m.setColumnTitles(new String[] {null, "Name", "Keywords", null, "Path to template file", null,
				null, null, null, "Author"});
		return m;
	}

	@Override
	public void delete(ItemDataModel item) {
		delete(((TemplatesItemModel) item).getId());
	}

	@Override
	protected void dataFromItemModel(PreparedStatement st, ItemDataModel dm) {
		TemplatesItemModel tm = (TemplatesItemModel) dm;
		try {
			st.setString(1, tm.getName());
			st.setString(2, tm.getKeywords());
			st.setString(3, tm.getDescription());
			st.setString(4, tm.getPath());
			st.setInt(5, tm.getAuthorId());
			st.setString(6, tm.getQuestions());
			st.setString(7, tm.getSolutions());
			st.setString(8, tm.getShortanswers());
			if(!tm.getIsNew())
				st.setInt(9, tm.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ItemDataModel dm) {
		super.update(dm);
		TemplatesItemModel im = (TemplatesItemModel) dm;
		saveTemplateClassificationsModel(im.getClassifications(), im);
		saveTemplateFilesModel(im.getFiles(), im);
		saveTemplateModulesModel(im.getModules(), im);
		saveTemplateUpdatesModel(im.getUpdates(), im);
	}

	public TemplateModulesModel createTemplateModulesModel(TemplatesItemModel item) {
		return (TemplateModulesModel) createTemplateDetailModel(item, new TemplateModulesModel(),
				"select * from templates_modules where template_id=?", data.getModulesModel(), "module_id");
	}

	public void saveTemplateModulesModel(TemplateModulesModel mm, TemplatesItemModel item) {
		saveTemplateDetailesModel(mm, item, "delete from templates_modules where template_id=?",
		"insert into templates_modules (template_id, module_id) values (?, ?)");
	}


	public TemplateFilesModel createTemplateFilesModel(TemplatesItemModel item) {
		return (TemplateFilesModel) createTemplateDetailModel(item, new TemplateFilesModel(),
				"select * from templates_files where template_id=?", data.getFilesModel(), "file_id");
	}

	public void saveTemplateFilesModel(TemplateFilesModel mm, TemplatesItemModel item) {
		saveTemplateDetailesModel(mm, item, "delete from templates_files where template_id=?",
		"insert into templates_files (template_id, file_id) values (?, ?)");
	}

	public TemplateClassificationsModel createTemplateClassificationsModel(TemplatesItemModel item) {
		return (TemplateClassificationsModel) createTemplateDetailModel(item, new TemplateClassificationsModel(),
				"select * from templates_classifications where template_id=?", data.getClassificationsModel(), "class_id");
	}

	public void saveTemplateClassificationsModel(TemplateClassificationsModel mm, TemplatesItemModel item) {
		saveTemplateDetailesModel(mm, item, "delete from templates_classifications where template_id=?",
				"insert into templates_classifications (template_id, class_id) values (?, ?)");
	}

	public TemplateUpdatesModel createTemplateUpdatesModel(TemplatesItemModel item) {
		return (TemplateUpdatesModel) createTemplateDetailModel(item, new TemplateUpdatesModel(),
				"select * from updates where template_id=?", updates, "id");
	}

	public void saveTemplateUpdatesModel(TemplateUpdatesModel mm, TemplatesItemModel item) {
		try {
			PreparedStatement st = data.getConnection().prepareStatement("delete from updates where template_id=?");
			st.setInt(1, item.getId());
			st.execute();
			st = data.getConnection().prepareStatement("insert into updates (template_id, author_id, comment, update_date, id) " +
					"values (?, ?, ?, ?, ?)");
			st.setInt(1, item.getId());
			for(int i=0;i<mm.getRowCount();i++) {
				TemplateUpdatesItemModel mi = (TemplateUpdatesItemModel) mm.getObject(i);
				st.setInt(2, mi.getAuthor().getId());
				st.setString(3, mi.getComment());
				st.setDate(4, mi.getUpdateDate());
				if(mi.getIsNew())
					st.setObject(5, null);
				else
					st.setInt(5, mi.getId());
				st.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected ObjectArrayTableModel createTemplateDetailModel(TemplatesItemModel item, ObjectArrayTableModel mm,
			String sql, AbstractDataModel dataModel, String objectIdName) {
		mm.setBatchMode(true);
		ResultSet rs = getDetailStatement(sql, item);
		try {
			if(rs.first())
				while(!rs.isAfterLast()) {
					mm.addRow(dataModel.getItem(rs.getObject(objectIdName)));
					rs.next();
				}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		mm.setBatchMode(false);
		return mm;
	}

	public void saveTemplateDetailesModel(ObjectArrayTableModel mm, TemplatesItemModel item,
			String delete_sql, String insert_sql) {
		try {
			PreparedStatement st = data.getConnection().prepareStatement(delete_sql);
			st.setInt(1, item.getId());
			st.execute();
			st = data.getConnection().prepareStatement(insert_sql);
			st.setInt(1, item.getId());
			for(int i=0;i<mm.getRowCount();i++) {
				st.setInt(2, ((IntIdItemModel)mm.getObject(i)).getId());
				st.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ResultSet getDetailStatement(String sql, TemplatesItemModel item) {
		try {
			PreparedStatement st = data.getConnection().prepareStatement(sql);
			st.setInt(1, item.getId());
			return st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TemplateUpdatesDataModel getUpdates() {
		return updates;
	}

}
