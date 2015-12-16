/* This file is part of SmartAss and contains the AssignmentsConstructor class that represents the
 * form of assignments constructor application.
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
package au.edu.uq.smartassrepoeditor.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

import au.edu.uq.smartassrepoeditor.engine.Engine;
import au.edu.uq.smartassrepoeditor.repository.AbstractComboField;
import au.edu.uq.smartassrepoeditor.repository.RepositoryStorage;
import au.edu.uq.smartassrepoeditor.repository.data.Database;
import au.edu.uq.smartassrepoeditor.repository.data.ObjectArrayTableModel;
import au.edu.uq.smartassrepoeditor.repository.data.RowSetTableModel;
import au.edu.uq.smartassrepoeditor.templates.TexReader;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTAnyText;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTCall;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTDocument;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTMulti;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTMultiChoice;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTRepeat;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTTemplate;
import au.edu.uq.smartassrepoeditor.templates.texparser.ComplexNode;
import au.edu.uq.smartassrepoeditor.templates.texparser.SimpleNode;

/**
 * The AssignmentsConstructor class represents the
 * main form of assignments constructor application.
 */
public class AssignmentsConstructor extends JFrame {
	static String[] conn_str;
	RepositoryStorage storage;
	Database data;
	ConstructorTableModel ass_model;
	RowSetTableModel templates_model;
	ASTTemplate template_node;
	ASTDocument doc_node;

	String db_path, db_username, db_password, tex_header;

	SearchComboField search_template;
	SearchComboField search_keywords;
	JTable templates;
	JTable assigments;


	/**
	 * Table model for visual template editing
	 *
	 */

	class ConstructorTableModel extends ObjectArrayTableModel   {
		NumberCellEditor cc = new NumberCellEditor();

		public ConstructorTableModel() {
			headers = new String[] {"Assigments"};
		}

		public int getColumnCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object obj = super.getValueAt(rowIndex, columnIndex);
			if(obj instanceof TextConstruction && assigments.getRowHeight(rowIndex)!=32)
				assigments.setRowHeight(rowIndex, 32);
			return obj;
		}
		@Override
		protected Object getValueFromObject(Object obj, int columnIndex) {
			return obj;
		}

		@Override
		public void removeRow(int index) {
			AbstractTemplateConstruction obj = (AbstractTemplateConstruction) objects.get(index);
			super.removeRow(index);
			obj.onRemove();
		}

		@Override
		public void removeObject(Object obj) {
			super.removeObject(obj);
			((AbstractTemplateConstruction)obj).onRemove();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return getObject(rowIndex) instanceof RepeatConstruction
				|| getObject(rowIndex) instanceof MultyConstruction
				|| getObject(rowIndex) instanceof TextConstruction;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			AbstractTemplateConstruction obj = (AbstractTemplateConstruction) getObject(rowIndex);
			if(obj instanceof TextConstruction)
				((ASTAnyText)obj.getNode()).setText((String)aValue + "\n");
			else if(obj instanceof RepeatConstruction)
				((ASTRepeat)obj.getNode()).setRepeatsNum(Integer.parseInt((String)aValue));
			else if(obj instanceof MultyConstruction)
				((ASTMulti)obj.getNode()).setChoicesCount(Integer.parseInt((String)aValue));
		}

	}

	///////////////////////////////////
	//Objects for TableModel
	//////////////////////////////////
	class AbstractTemplateConstruction {
		SimpleNode node;
		AbstractTemplateConstruction parent;
		Vector<AbstractTemplateConstruction> children;
		int level;

		public AbstractTemplateConstruction(SimpleNode node, AbstractTemplateConstruction parent) {
			this.node = node;
			this.parent = parent;
			children = new Vector<AbstractTemplateConstruction>();
			if(parent!=null) {
				parent.addChild(this);
				level = parent.getLevel()+1;
			}
		}

		public int getRowHeight() {
			return 16;
		}


		public AbstractTemplateConstruction getParent() {
			return parent;
		}

		public boolean canParent() {
			return false;
		}

		public void addChild(AbstractTemplateConstruction child) {
			children.add(child);
		}

		public SimpleNode getNode() {
			return node;
		}

		public void onRemove() {
			node.removeSelf();
			for(Object o : children)
				ass_model.removeObject(o);
		}

		protected int getLevel() {
			return level;
		}

	}

	class CallTemplateConstruction extends AbstractTemplateConstruction {
		public CallTemplateConstruction(ASTCall node, AbstractTemplateConstruction parent) {
			super(node, parent);
		}

		@Override
		public String toString() {
			return ((ASTCall)node).getFilename();
		}
	}

	class RepeatConstruction extends AbstractTemplateConstruction {
		RepeatEndConstruction end;

		public RepeatConstruction(ASTRepeat node, AbstractTemplateConstruction parent) {
			super(node, parent);
		}

		@Override
		public String toString() {
			return "REPEAT " + ((ASTRepeat)node).getRepeatsNum();
		}

		@Override
		public boolean canParent() {
			return true;
		}

		@Override
		public void addChild(AbstractTemplateConstruction child) {
			super.addChild(child);
		}

		@Override
		public void onRemove() {
			super.onRemove();
			ass_model.removeObject(end);
		}

		public void setEnd(RepeatEndConstruction end) {
			this.end = end;
		}
	}

	class RepeatEndConstruction extends AbstractTemplateConstruction {
		RepeatConstruction begin;
		boolean in_remove;

		public RepeatEndConstruction(ASTRepeat node, RepeatConstruction begin) {
			super(node, begin.getParent());
			begin.setEnd(this);
			this.begin = begin;
		}

		@Override
		public String toString() {
			return "END REPEAT";
		}

		@Override
		public void onRemove() {
			if(!in_remove) {
				in_remove = true;
				ass_model.removeObject(begin);
				super.onRemove();
			}
		}
	}

	class TextConstruction extends AbstractTemplateConstruction {
		public TextConstruction(ASTAnyText node, AbstractTemplateConstruction parent) {
			super(node, parent);
		}

		@Override
		public String toString() {
			return ((ASTAnyText)node).getCode();
		}
	}

	class MultyConstruction extends AbstractTemplateConstruction {
		Vector<MultyChoiceConstruction> choices;
		MultyEndConstruction end;

		public MultyConstruction(ASTMulti node, AbstractTemplateConstruction parent) {
			super(node, parent);
			choices = new Vector<MultyChoiceConstruction>();
		}

		@Override
		public String toString() {
			if(((ASTMulti)node).getChoicesCount()>1)
				return "MULTY " + ((ASTMulti)node).getChoicesCount();
			return "MULTY";

		}

		@Override
		public boolean canParent() {
			return true;
		}

		@Override
		public void addChild(AbstractTemplateConstruction child) {
			if(child instanceof MultyEndConstruction)
				end = (MultyEndConstruction) child;
			else if (child instanceof MultyChoiceConstruction)
				choices.add((MultyChoiceConstruction) child);
			else
				super.addChild(child);
		}

		@Override
		public void onRemove() {
			super.onRemove();
			for(Object o : choices)
				ass_model.removeObject(o);
			ass_model.removeObject(end);
		}

		protected void addChoice(MultyChoiceConstruction choice) {
			choices.add(choice);
		}

		protected void setEnd(MultyEndConstruction end) {
			this.end = end;
		}
	}

	class MultyChoiceConstruction extends AbstractTemplateConstruction {
		MultyConstruction multy;

		public MultyChoiceConstruction(ASTMultiChoice node, MultyConstruction multy) {
			super(node, multy);
			this.multy = multy;
			multy.addChoice(this);
		}

		@Override
		public String toString() {
			return "CHOICE";
		}
	}

	class MultyEndConstruction extends AbstractTemplateConstruction {
		MultyConstruction multy;
		boolean in_remove;

		public MultyEndConstruction(ASTMulti node, MultyConstruction multy) {
			super(node, multy.getParent());
			this.multy = multy;
			multy.setEnd(this);
		}

		@Override
		public String toString() {
			return "END MULTY";
		}

		@Override
		public void onRemove() {
			if(!in_remove) {
				in_remove = true;
				ass_model.removeObject(multy);
				super.onRemove();
			}
		}
	}


	//////////////////////
	//Renderers/editors
	/////////////////////

	class RendererPanel extends JPanel {
		JPanel spacer = new JPanel();
		Component control;

		public RendererPanel() {
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setLevel(0);
			add(spacer);
		}

		public void setText(String txt) {

		}

		public void setBackground(Color bg) {
			super.setBackground(bg);
			if(control!=null)
				control.setBackground(bg);
		}

		public void setLevel(int level) {
			spacer.setPreferredSize(new Dimension(10*level, getPreferredSize().height));
			spacer.setMaximumSize(new Dimension(10*level, getPreferredSize().height));
		}
	}

	class TextRendererPanel extends RendererPanel {
		JTextArea text = new JTextArea();
		public TextRendererPanel() {
			control = text;
			add(text);
		}

		@Override
		public void setText(String txt) {
			text.setText(txt);
		}
	}

	class OtherRendererPanel extends RendererPanel {
		JLabel label = new JLabel();
		public OtherRendererPanel() {
			control = label;
			add(label);
		}

		@Override
		public void setText(String txt) {
			label.setText(txt);
		}
	}

	public class AssigmentCellRenderer implements TableCellRenderer {
		RendererPanel label_pn;
		RendererPanel text_pn;

		public AssigmentCellRenderer() {
			label_pn = new OtherRendererPanel();
			text_pn = new TextRendererPanel();
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			RendererPanel pn;
			if(value instanceof TextConstruction)
				pn = text_pn;
			else
				pn = label_pn;
			pn.setText(value.toString());
			if(isSelected)
				pn.setBackground(table.getSelectionBackground());
			else
				pn.setBackground(table.getBackground());
			pn.setLevel(((AbstractTemplateConstruction)value).getLevel());
			return pn;
		}
	}

	/**
	 *
	 */
	public class NumberCellEditor extends AbstractCellEditor implements TableCellEditor {
		JPanel pn;
		JLabel label;
		JTextField edit;

		JTextPane ml_edit;
		JScrollPane sp;
		JTextComponent current;


		public NumberCellEditor() {
			pn = new JPanel();
			pn.setLayout(new BoxLayout(pn, BoxLayout.LINE_AXIS));
			pn.add(label = new JLabel());
			pn.add(edit = new JTextField());

			ml_edit = new JTextPane();
			sp = new JScrollPane(ml_edit);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if(value instanceof RepeatConstruction) {
				label.setText("REPEAT ");
				edit.setText(Integer.toString(((ASTRepeat)((RepeatConstruction)value).getNode()).getRepeatsNum()));
				current = edit;
			} else if(value instanceof MultyConstruction) {
				label.setText("MULTI ");
				edit.setText(Integer.toString(((ASTMulti)((MultyConstruction)value).getNode()).getChoicesCount()));
				current = edit;
			}  else if(value instanceof TextConstruction) {
				//label.setText("");
				String text = ((ASTAnyText)((TextConstruction)value).getNode()).getCode();
				text = text.substring(0,text.length()-1);
				ml_edit.setText(text);
				current = ml_edit;
				return sp;
			}

			return pn;
		}

		public Object getCellEditorValue() {
			return current.getText();
		}
	}

	class SearchComboField extends AbstractComboField {
		public SearchComboField() {
			button.setText(" ? ");
		}
	}

	public AssignmentsConstructor() {
		super("Assigments constructor");

		loadSettings();

		template_node = new ASTTemplate(0);
		doc_node = new ASTDocument(1);
		ASTAnyText header_node = new ASTAnyText(0);
		header_node.setText(tex_header);
		template_node.jjtAddChild(header_node, 0);
		template_node.jjtAddChild(doc_node, 1);

		RepositoryStorage storage = new RepositoryStorage();
		data = new Database(db_path, db_username, db_password, storage);

		JSplitPane spl = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		JPanel pn = new JPanel();
		pn.setLayout(new BorderLayout());

		JPanel search_panel = new JPanel();
		search_panel.setLayout(new BoxLayout(search_panel, BoxLayout.PAGE_AXIS));
		search_panel.add(new JLabel("Search template name:"));
		search_panel.add(search_template = new SearchComboField());
		search_template.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				templates.setModel(templates_model = composeTempaltesModel(search_template.getText(), search_keywords.getText()));
			}
		});
		search_panel.add(new JLabel("Search keywords:"));
		search_panel.add(search_keywords = new SearchComboField());
		search_keywords.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				templates.setModel(templates_model = composeTempaltesModel(search_template.getText(), search_keywords.getText()));
			}
		});
		pn.add(search_panel, BorderLayout.NORTH);


		templates_model = composeTempaltesModel(search_template.getText(), search_keywords.getText());
		pn.add(new JScrollPane(templates = new JTable(templates_model)));
		pn.setPreferredSize(new Dimension(200, 400));
		spl.add(pn);

		JPanel rpan = new JPanel(new BorderLayout());
		pn = new JPanel();
		pn.setLayout(new BoxLayout(pn, BoxLayout.PAGE_AXIS));
		pn.setPreferredSize(new Dimension(100, 400));
		JButton bt = new JButton(new AbstractAction("Add >>") {
			public void actionPerformed(ActionEvent e) {
				int row = templates.getSelectedRow();
				if(row!=-1) {
					int ass_row = assigments.getSelectedRow();
					AbstractTemplateConstruction parent = findParent(ass_row);
					ComplexNode parent_node = findParentNode(parent);
					ASTCall call = new ASTCall(parent_node.jjtGetNumChildren());
					call.setFilename((String) templates_model.getValue(row, "name") + ".tex");
					insertNode(ass_row, call, parent_node);
					addConstruction(ass_row, new CallTemplateConstruction(call, parent));
				}
			}
		});
		pn.add(bt);
		pn.add(Box.createRigidArea(new Dimension(0,10)));
		bt = new JButton(new AbstractAction("Text") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				AbstractTemplateConstruction parent = findParent(row);
				ComplexNode parent_node = findParentNode(parent);
				ASTAnyText node = new ASTAnyText(parent_node.jjtGetNumChildren());
				node.setText("\n"); //Attention! Empty text nodes is ommited by template code tree containers!
				insertNode(row, node, parent_node);
				addConstruction(row, new TextConstruction(node, parent));
			}
		});
		bt.setPreferredSize(new Dimension(200, 300));
		pn.add(bt);
		bt = new JButton(new AbstractAction("REPEAT") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				AbstractTemplateConstruction parent = findParent(row);
				ComplexNode parent_node = findParentNode(parent);
				ASTRepeat node = new ASTRepeat(parent_node.jjtGetNumChildren());
				node.setRepeatsNum(10);
				insertNode(row, node, parent_node);
				RepeatConstruction rc = new RepeatConstruction(node, parent);
				RepeatEndConstruction re = new RepeatEndConstruction(node, rc);
				if(row==-1) {
					ass_model.addRow(rc);
					ass_model.addRow(re);
					assigments.setRowSelectionInterval(ass_model.getRowCount()-1, ass_model.getRowCount()-1);
				} else {
					ass_model.insertRow(rc, row+1);
					ass_model.insertRow(re, row+2);
					assigments.setRowSelectionInterval(row+1, row+1);
				}
			}
		});
		pn.add(bt);
		bt = new JButton(new AbstractAction("MULTI") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				AbstractTemplateConstruction parent = findParent(row);
				ComplexNode parent_node = findParentNode(parent);
				ASTMulti node = new ASTMulti(parent_node.jjtGetNumChildren());
				node.setChoicesCount(1);
				insertNode(row, node, parent_node);
				ASTMultiChoice cnode = new ASTMultiChoice(0);
				node.jjtAddChild(cnode, 0);
				cnode.jjtSetParent(node);
				MultyConstruction c = new MultyConstruction(node, parent);
				MultyEndConstruction ce = new MultyEndConstruction(node, c);
				MultyChoiceConstruction cc = new MultyChoiceConstruction(cnode, c);
				if(row==-1) {
					ass_model.addRow(c);
					ass_model.addRow(cc);
					ass_model.addRow(ce);
					assigments.setRowSelectionInterval(ass_model.getRowCount()-1, ass_model.getRowCount()-1);
				} else {
					ass_model.insertRow(c, row+1);
					ass_model.insertRow(cc, row+2);
					ass_model.insertRow(ce, row+3);
					assigments.setRowSelectionInterval(row+1, row+1);
				}
			}
		});
		pn.add(bt);
		bt = new JButton(new AbstractAction("CHOICE") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				AbstractTemplateConstruction parent = findParent(row);
				if(parent instanceof MultyConstruction) {
					ComplexNode parent_node = findParentNode(parent);
					ASTMultiChoice node = new ASTMultiChoice(parent_node.jjtGetNumChildren());
					insertNode(row, node, parent_node);
					addConstruction(row, new MultyChoiceConstruction(node, (MultyConstruction) parent));
				}
			}
		});
		pn.add(bt);
		pn.add(Box.createRigidArea(new Dimension(0,10)));
		/*bt = new JButton(new AbstractAction("Up") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				if(row>0 && (AbstractTemplateConstruction)) {
					AbstractTemplateConstruction o =  (AbstractTemplateConstruction) ass_model.getObject(row);
					ass_model.removeRow(row);
					ass_model.insertRow(o, row-1);
					assigments.setRowSelectionInterval(row-1, row-1);
				}
			}
		});
		pn.add(bt);
		bt = new JButton(new AbstractAction("Down") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				if(row!=-1 && row<ass_model.getRowCount()-1) {
					Object o = ass_model.getObject(row);
					ass_model.removeRow(row);
					ass_model.insertRow(o, row+1);
					assigments.setRowSelectionInterval(row+1, row+1);
				}
			}
		});
		pn.add(bt);*/
		pn.add(Box.createRigidArea(new Dimension(0,10)));
		bt = new JButton(new AbstractAction("Delete <<") {
			public void actionPerformed(ActionEvent e) {
				int row = assigments.getSelectedRow();
				if(row!=-1)
					ass_model.removeRow(row);
			}
		});
		pn.add(bt);
		pn.add(Box.createRigidArea(new Dimension(0,10)));
		bt = new JButton(new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				//fc.setCurrentDirectory(new File(output_path));
				//fc.setSelectedFile(new File(template_name));
				if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
					try {
						FileWriter file_writer = new FileWriter(fc.getSelectedFile().getPath());
						BufferedWriter out = new BufferedWriter(file_writer);
						out.write(template_node.getCode());
						out.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		pn.add(bt);
		bt = new JButton(new AbstractAction("Open") {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
					try {
						Engine engine = Engine.getInstance();
						TexReader tr = new TexReader(engine);
						InputStream in = new FileInputStream(fc.getSelectedFile());
						tr.loadTemplate(in);
						template_node = (ASTTemplate) tr.getRootNode();
						in.close();
						analyseTemplate();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		pn.add(bt);
		bt = new JButton(new AbstractAction("Settings") {
			public void actionPerformed(ActionEvent e) {
        		AssignmentsConstructorSettingsDialog settings = new AssignmentsConstructorSettingsDialog(null);
        		settings.setVisible(true);
			}
		});
		pn.add(bt);
		rpan.add(pn, BorderLayout.WEST);

		pn = new JPanel();
		pn.setLayout(new BorderLayout());
		pn.add(new JScrollPane(assigments = new JTable(ass_model = new ConstructorTableModel())));
		rpan.add(pn);
		//ass_model.setRowHeights();
		NumberCellEditor cc = new NumberCellEditor();
		assigments.setDefaultEditor(Object.class, cc);
		assigments.setDefaultRenderer(Object.class, new AssigmentCellRenderer());
	//	assigments.setDefaultEditor(RepeatConstruction.class, cc);
		//assigments.setDefaultEditor(MultyConstruction.class, cc);

		spl.add(rpan);
		add(spl);

		setPreferredSize(new Dimension(800, 600));
		pack();

        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		dispose();
        	}
        });

 /*       for(int i=0; i<10; i++)
        	ass_model.addRow(new CallTemplateConstruction(null));*/
	}

	protected void analyseTemplate() {
		ass_model.clearAll();
		doc_node = template_node.getDocument();
		analyseNode(doc_node, null);
	}

	protected void analyseNode(SimpleNode node, AbstractTemplateConstruction parent) {

		for(int i=0; i<node.jjtGetNumChildren();i++) {
			SimpleNode child = (SimpleNode) node.jjtGetChild(i);
			if(child instanceof ASTAnyText)
				ass_model.addRow(new TextConstruction((ASTAnyText)child, parent));
			else if(child instanceof ASTCall)
				ass_model.addRow(new CallTemplateConstruction((ASTCall)child, parent));
			else if(child instanceof ASTRepeat) {
				RepeatConstruction repeat = new RepeatConstruction((ASTRepeat) child, parent);
				ass_model.addRow(repeat);
				analyseNode(child, repeat);
				ass_model.addRow(new RepeatEndConstruction((ASTRepeat)child, repeat));
			} else if(child instanceof ASTMulti) {
				MultyConstruction multi = new MultyConstruction((ASTMulti) child, parent);
				ass_model.addRow(multi);
				analyseNode(child, multi);
				ass_model.addRow(new MultyEndConstruction((ASTMulti)child, multi));
			} else if(child instanceof ASTMultiChoice) {
				MultyChoiceConstruction choice = new MultyChoiceConstruction((ASTMultiChoice)child, (MultyConstruction)parent);
				int m=1;
				while(child.jjtGetNumChildren()>0) {
					if(child.jjtGetChild(0)!=null)
						node.insertNode((SimpleNode)child.jjtGetChild(0), i+(m++));
					child.removeNode(0);
				}
				ass_model.addRow(choice);
			}
		}

	}

	protected AbstractTemplateConstruction findParent(int row) {
		AbstractTemplateConstruction parent = (AbstractTemplateConstruction) ass_model.getObject(row);
		if(parent==null)
			return null;
		if(parent.canParent())
			return parent;
		else
			return parent.getParent();
	}

	protected ComplexNode findParentNode(AbstractTemplateConstruction parent) {
		if(parent==null)
			return doc_node;
		else
			return (ComplexNode) parent.getNode();
	}

	protected void addConstruction(int row, AbstractTemplateConstruction constr) {
		if(row==-1) {
			ass_model.addRow(constr);
			assigments.setRowSelectionInterval(ass_model.getRowCount()-1, ass_model.getRowCount()-1);
		} else {
			ass_model.insertRow(constr, assigments.getSelectedRow()+1);
			assigments.setRowSelectionInterval(row+1, row+1);
		}
	}

	protected void insertNode(int row, SimpleNode node, SimpleNode parent_node) {
		if(row==-1)
			parent_node.jjtAddChild(node, parent_node.jjtGetNumChildren());
		else {
			SimpleNode sel_node = ((AbstractTemplateConstruction)ass_model.getObject(row)).getNode();
			if(parent_node==sel_node)
				parent_node.insertNode(node, 0);
			else
				for(int i=0;i<parent_node.jjtGetNumChildren();i++)
					if(sel_node==parent_node.jjtGetChild(i)) {
						parent_node.insertNode(node, i+1);
						break;
					}
		}
		node.jjtSetParent(parent_node);
	}

	private RowSetTableModel composeTempaltesModel(String templ_filter, String keyword_filter) {
		String sql = "select * from templates ";
		Vector<String> params = new Vector<String>();
		templ_filter = templ_filter.trim();
		keyword_filter = keyword_filter.trim();
		if(!templ_filter.equals("") || !keyword_filter.equals("")) {
			sql = sql + " where ";
			if(!templ_filter.equals("")) {
				sql = sql + " name like ? ";
				if(keyword_filter.length()!=0)
					sql = sql + " and ";
				params.add("%" + templ_filter + "%");
			}
			if(keyword_filter.length()!=0) {
				String[] keys = keyword_filter.split("[\\ \\,]");
				String clause = "(";
				boolean need_and = false;
				for(int i=0;i<keys.length;i++) {
					if(keys[i].length()>0) {
						clause = clause + (need_and?" and ":"") + " keywords like ?";
						params.add("%" + keys[i] + "%");
						need_and = true;
					}
				}
				if(clause.length()>1)
					sql = sql + clause + ")";
			}

		}
		RowSetTableModel model = data.getTemplatesModel().getTableModel(sql, params.toArray(), true);
		model.setVisibleColumns(new String[]{"name"});
		model.setColumnTitles(new String[] {null, "Name", "Keywords", null, "Path to template file", null,
				null, null, null, "Author"});
		return model;
	}

	private void loadSettings() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass/repository");
		db_path = prefs.get("repository_db_path", "");
		db_username = prefs.get("repository_db_user", "");
		db_password = prefs.get("repository_db_password", "");
		prefs = Preferences.userRoot().node("au/edu/uq/smartass/app");
		tex_header = prefs.get("assigments_constructor_tex_header", "");

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		conn_str=new String[3];
		conn_str[0] = args[0];
		conn_str[1]=args[1];
		if(args.length>2)
			conn_str[2]=args[2];
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		AssignmentsConstructor form = new AssignmentsConstructor();
                form.setVisible(true);
            }
        });

	}

}
