/* This file is part of SmartAss and contains the InteractiveApp class that represents the
 * main form of the application for the interactive assignment edit and (re)execucion.
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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import au.edu.uq.smartassrepoeditor.engine.Engine;
import au.edu.uq.smartassrepoeditor.templates.TexReader;
import au.edu.uq.smartassrepoeditor.templates.texparser.ComplexNode;
import au.edu.uq.smartassrepoeditor.templates.texparser.RCall;
import au.edu.uq.smartassrepoeditor.templates.texparser.RComplexNode;
import au.edu.uq.smartassrepoeditor.templates.texparser.RTemplate;
import au.edu.uq.smartassrepoeditor.templates.texparser.ResultNode;
import au.edu.uq.smartassrepoeditor.templates.texparser.SimpleNode;
import au.edu.uq.smartassrepoeditor.templates.texparser.UnparsedTexNode;

/**
 * The InteractiveApp class that represents the
 * main form of the application for the interactive assignment edit and (re)execucion.
 */
public class InteractiveApp extends JFrame implements TreeSelectionListener {
	static String params[] = null;
	static final String ALL_IN_ONE_NAME = "ALL-IN-ONE";
	static final String DVI_NAME = "DVI";
	static final String PDF_NAME = "PDF";

	Engine engine = Engine.getInstance();
	TexReader tr;

	JTree tree;
	DefaultTreeModel tree_model;
	JTree code_tree;
	DefaultTreeModel code_tree_model;
	ResultRootNode root = new ResultRootNode(null, "empty");
	ResultTreeNode selected;
	CodeTreeNode code_root;
	CodeTreeNode code_selected;
	JPanel code_text_pane;
	CodePanel code_text;
	JTabbedPane tree_pane;
	JPanel text_pane;

	Action open, exec, save, exec_node, dvi_section;

	//SmartAss and jDvi settings
	String templates_root = "";
	String output_path = "";
	String tex_path = "";
	// jDvi has a lot more properties but these are mandatory ones
	String jdvi_font_path = "";
	String jdvi_font_nameformat = "";
	String jdvi_resolution = "";

	JSplitPane split;
	JTabbedPane tabs;
	int last_tab;
	String template_name;
	String[] predef_sections = {"QUESTION", "SOLUTION", "SHORTANSWER"};
	File template_file;
	boolean debug_mode = true; //if true, application will be a bit more verbose
	Component code_pane;
	HashMap<String, Component> dvi_tabs = new HashMap<String, Component>();
	JButton bt_dvi_section, bt_dvi_all, bt_dvi_allinone;
	JCheckBox pdvi;
	boolean persist_dvi = false;
	boolean do_pdf = false;
	HashSet<File> files_to_cleanup = new HashSet<File>();

 	/**
 	 * The AbstractAction class is the action to open the file with the assignment
 	 *
 	 */
	class OpenAction extends AbstractAction {
		JFrame frame;

		public OpenAction(JFrame frame) {
			super("Open");
			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(templates_root));
			if(fc.showOpenDialog(frame)==JFileChooser.APPROVE_OPTION) {
				File f = fc.getSelectedFile();
				loadFile(f);
			}
		}

	}

	/**
	 * The SaveAction class is the ancestor for any content save actions
	 */
	class SaveAction extends AbstractAction {
		protected JFrame frame;

		public SaveAction(String name, JFrame frame) {
			super(name);
			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			if(tr==null) return;
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(output_path));
			fc.setSelectedFile(new File(template_name));
			if(fc.showSaveDialog(frame)==JFileChooser.APPROVE_OPTION) {
				saveFile(fc.getSelectedFile().getPath());
			}
		}

		protected void saveFile(String file_name) {

		}
	}

	/**
	 * The SaveTexAction class is the save TeX file action
	 */
	class SaveTexAction extends SaveAction {

		public SaveTexAction(JFrame frame) {
			super("Save Tex", frame);
		}

		protected void saveFile(String file_name) {
			if(file_name.lastIndexOf(".tex")>0)
				file_name = file_name.substring(0,file_name.lastIndexOf(".tex") );
			Vector<String> names = tr.getSectionNames();
			for(Iterator<String> it=names.iterator(); it.hasNext(); ) {
				String name = it.next();
				//Skip DEF section - don't need it saved
				if(!name.equals("DEF")) {
					writeFile(new File(file_name + "-" + name + "S.tex"), tr.getSection(name));
				}
			}
		}
	}

	/**
	 * The SaveDviAction class is the save DVI file action
	 */
	class SaveDviAction extends SaveAction {

		public SaveDviAction(JFrame frame) {
			super("Save DVI", frame);
		}

		protected void saveFile(String file_name) {
			if(file_name.lastIndexOf(".tex")>0)
				file_name = file_name.substring(0,file_name.lastIndexOf(".tex") );
			else if(file_name.lastIndexOf(".dvi")>0)
				file_name = file_name.substring(0,file_name.lastIndexOf(".dvi") );
			Vector<String> names = tr.getSectionNames();
			for(Iterator<String> it=names.iterator(); it.hasNext(); ) {
				String name = it.next();
				//Skip DEF section - don't need it saved
				if(!name.equals("DEF")) {
					texToDvi(new File(file_name + "-" + name + "S.tex"), tr.getSection(name), false);
				}
			}
		}
	}

	/**
	 * The SavePdfAction class is the save PDF file action
	 */
	class SavePdfAction extends SaveAction {

		public SavePdfAction(JFrame frame) {
			super("Save PDF", frame);
		}

		protected void saveFile(String file_name) {
			if(file_name.lastIndexOf(".tex")>0)
				file_name = file_name.substring(0,file_name.lastIndexOf(".tex") );
			else if(file_name.lastIndexOf(".pdf")>0)
				file_name = file_name.substring(0,file_name.lastIndexOf(".pdf") );
			Vector<String> names = tr.getSectionNames();
			for(Iterator<String> it=names.iterator(); it.hasNext(); ) {
				String name = it.next();
				//Skip DEF section - don't need it saved
				if(!name.equals("DEF")) {
					texToDvi(new File(file_name + "-" + name + "S.tex"), tr.getSection(name), true);
				}
			}
		}
	}


	/**
	 * The ExecuteAction class is the template (re)execution action
	 */
	class ExecuteAction extends AbstractAction {
		JFrame frame;

		public ExecuteAction(JFrame frame) {
			super("Execute all");
			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			if(tr!=null)
				execute();
			else
				JOptionPane.showMessageDialog(frame, "File not open - nothing to execute", "Message", JOptionPane.INFORMATION_MESSAGE);

		}
	}

	/**
	 * The EditSettingsAction class executes the settings editor
	 */
	class EditSettingsAction extends AbstractAction {
		JFrame frame;

		public EditSettingsAction(JFrame frame) {
			super("Settings");
			this.frame = frame;
		}

		public void actionPerformed(ActionEvent e) {
			SettingsDialog es = new SettingsDialog(frame);
			es.setModal(true);
			es.setVisible(true);

			initContext();
			if(tr!=null)
				loadFile(template_file);
		}
	}

	/**
	 * The ReExecuteNodeAction class re-executes the single assignment node code
	 */
	class ReExecuteNodeAction extends AbstractAction {
		public ReExecuteNodeAction() {
			super("Execute node");
		}

		public void actionPerformed(ActionEvent e) {
			if(selected!=null) {
				if(selected==root)
					execute();
				else {
				ResultNode r = selected.getResult().getMasterCopy().execute(new HashSet<String>(tr.getSectionNames()), tr.getScript());
				selected.getResult().replaceSelf(r);
				while(selected.getChildCount()>0)
					tree_model.removeNodeFromParent((DefaultMutableTreeNode)selected.getChildAt(0));
				selected.removeAllChildren();
				selected.setUserObject(r);
				fillTree(r, selected);
				setTabs();
				}
			}
		}
	}

	/**
	 * The SectionToDviAction class created DVI based on the TeX section content
	 */
	class SectionToDviAction extends AbstractAction {
		public SectionToDviAction() {
			super("DVI sec.");
		}

		public void actionPerformed(ActionEvent e) {
			String secn = tabs.getTitleAt(tabs.getSelectedIndex());
			if(secn.indexOf(" DVI")>=0)
				secn = secn.replaceFirst(" DVI", "");
			if(secn.indexOf(" PDF")>=0)
				secn = secn.replaceFirst(" PDF", "");
			Component dvi = dvi_tabs.get(secn);
			if(dvi==null)
				dvi = composeDvi(secn, selected.getResult());
			tabs.setSelectedComponent(dvi);
		}
	}

	/**
	 * The AllToDviAction class converts all TeX sections to DVI
	 */
	class AllToDviAction extends AbstractAction {
		public AllToDviAction() {
			super("DVI all");
		}

		public void actionPerformed(ActionEvent e) {
			String secn;
			for(int i=0;i<tabs.indexOfComponent(code_pane);i++) {
				secn = tabs.getTitleAt(i);
				Component dvi = dvi_tabs.get(secn);
				if(dvi==null)
					composeDvi(secn, selected.getResult());
			}
			tabs.setSelectedIndex(tabs.indexOfComponent(code_pane)+1);
		}
	}

	/**
	 * The AllToDviAction class converts all TeX sections to DVI and put them to the single DVI document
	 */
	class AllInOneToDviAction extends AbstractAction {
		public AllInOneToDviAction() {
			super("DVI all-in-one");
		}

		public void actionPerformed(ActionEvent e) {
			Component dvi = dvi_tabs.get(ALL_IN_ONE_NAME);
			if(dvi==null) {
				composeAllInOneDvi(selected.getResult());
			}
			tabs.setSelectedIndex(tabs.indexOfComponent(code_pane)+1);
		}
	}


	class SimpleTextPanel extends JPanel {
		JEditorPane text;

		public SimpleTextPanel() {
			setLayout(new BorderLayout());
			text = new JEditorPane();
			text.setEditable(false);
			add(new JScrollPane(text));
		}

		public void setText(String t) {
			text.setText(t);
			text.setCaretPosition(0);
		}

	}

	/**
	 * Class TextPanel - container for EditorPane
	 * whith template code or tex result of template execution
	 *
	 * @author nik
	 *
	 */
	class TextPanel extends SimpleTextPanel {
		JButton edit, save;
		boolean changed;

		/**
		 * Creates inner controls of TextPanel
		 * (buttons and JEditorPane)
		 *
		 */
		public TextPanel() {
			text.getDocument().addDocumentListener(new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					setChanged(true);
				}

				public void insertUpdate(DocumentEvent e) {
					setChanged(true);
				}

				public void removeUpdate(DocumentEvent e) {
					setChanged(true);
				}
			});

			JPanel buttons = new JPanel();
			buttons.add(edit = new JButton("Edit"));
			edit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					text.setEditable(true);
				}
			});
			buttons.add(save = new JButton("Save changes"));
			save.setEnabled(false);
			save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveText();
				}
			});
			add(buttons, BorderLayout.NORTH);
		}

		public void setText(String t) {
			super.setText(t);
			setChanged(false);
		}

		public void setChanged(boolean yes) {
			changed = yes;
			save.setEnabled(yes);
		}

		protected void saveText() {

		}
	}

	class TexPanel extends TextPanel {
		@Override
		protected void saveText() {
			ResultNode this_node = selected.getResult();
			RSectionsTextNode n = new RSectionsTextNode();

			for(Iterator<String> it=tr.getSectionNames().iterator();it.hasNext();) {
				String s = it.next();
				if(s.equals(tabs.getTitleAt(tabs.getSelectedIndex())))
					n.setSection(s, text.getText());
				else
					n.setSection(s, this_node.getSection(s));
			}
			this_node.replaceSelf(n);
			tree.collapsePath(new TreePath(selected.getPath()));
			selected.removeAllChildren();
			selected.setUserObject(n);
			tree.repaint();
			setTabs();
		}
	}

	class CodePanel extends TextPanel {
		@Override
		protected void saveText() {
			String code;
			SimpleNode this_node = (SimpleNode) code_selected.getNode();
			if(this_node==tr.getRootNode())
				code = text.getText();
			else {
				UnparsedTexNode n = new UnparsedTexNode(text.getText());
				this_node.replaceSelf(n);
				code = tr.getRootNode().getCode();
			}
			try {
				FileOutputStream st = new FileOutputStream(template_file);
				st.write(code.getBytes());
				st.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			loadFile(template_file);
		}
	}


	class ResultTreeNode extends DefaultMutableTreeNode {

		public ResultTreeNode(ResultNode result) {
			super(result);
		}

		public ResultNode getResult() {
			return (ResultNode) getUserObject();
		}

	}

	class ResultRootNode extends ResultTreeNode {
		String caption;

		public ResultRootNode(ResultNode result, String caption) {
			super(result);
			setCaption(caption);
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		@Override
		public String toString() {
			return caption;
		}
	}

	class CodeTreeNode extends DefaultMutableTreeNode {
		String caption;

		public CodeTreeNode(SimpleNode node) {
			super(node);
		}

		public CodeTreeNode(SimpleNode node, String caption) {
			super(node);
			this.caption = caption;
		}

		public SimpleNode getNode() {
			return (SimpleNode) getUserObject();
		}

		@Override
		public String toString() {
			if(caption!=null)
				return caption;
			else
				return super.toString();
		}
	}


	class SettingsDialog extends JDialog {

		public SettingsDialog(JFrame owner) {
			super(owner, "SmartAss Settings");
		    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setSize(500,350);

			SettingsPanel pn = new SettingsPanel(owner);
			add(pn);
			pn.setCloseAction(new ActionListener() {
				   public void actionPerformed(ActionEvent e)
				   {
				      doClose();
				   }
				});
		}

		private void doClose() {
		      dispose();
		}
	}

	public InteractiveApp() throws HeadlessException {
		super("Interactive assigment generator");

		initContext();

		Container content = getContentPane();
		setSize(900, 600);

		JPanel buttons = new JPanel();
		buttons.add(new JButton(open = new OpenAction(this)));
		buttons.add(new JButton(exec = new ExecuteAction(this)));
		buttons.add(new JButton(save = new SaveTexAction(this)));
		buttons.add(new JButton(new SaveDviAction(this)));
		buttons.add(new JButton(new SavePdfAction(this)));
		buttons.add(new JButton(new EditSettingsAction(this)));
		exec.setEnabled(false);
		save.setEnabled(false);

		tree_pane = new JTabbedPane();
		tree_model = new DefaultTreeModel(root);
		tree = new JTree(tree_model);
		tree.setPreferredSize(new Dimension(150, 600));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		tree_pane.add("Result", new JScrollPane(tree));

		code_tree_model = new DefaultTreeModel(code_root);
		code_tree = new JTree(code_tree_model);
		code_tree.setPreferredSize(new Dimension(150, 600));
		code_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		code_tree.addTreeSelectionListener(this);
		tree_pane.add("Code", new JScrollPane(code_tree));

		tree_pane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				onNodeChanged((JTree)((JScrollPane)tree_pane.getSelectedComponent()).getViewport().getComponent(0));
			}
		});

		code_text = new CodePanel();


		tabs = new JTabbedPane();
		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				dvi_section.setEnabled(tabs.getSelectedComponent()!=code_pane);
				if(do_pdf && tabs.getSelectedComponent() instanceof PdfView)
					((PdfView) tabs.getSelectedComponent()).refresh();

			}
		});
		JPanel sub_buttons = new JPanel();
		sub_buttons.add(new JButton(exec_node = new ReExecuteNodeAction()));
		sub_buttons.add(bt_dvi_section = new JButton(dvi_section = new SectionToDviAction()));
		sub_buttons.add(bt_dvi_all = new JButton(new AllToDviAction()));
		sub_buttons.add(bt_dvi_allinone = new JButton(new AllInOneToDviAction()));

		ButtonGroup dvipdf = new ButtonGroup();
		JRadioButton rb = new JRadioButton(DVI_NAME);
		//rb.setActionCommand(DVI_NAME);
		rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_pdf = false;
				bt_dvi_section.setText(DVI_NAME + " sec.");
				bt_dvi_all.setText(DVI_NAME + " all");
				bt_dvi_allinone.setText(DVI_NAME + " all-in-one");
				pdvi.setText("Persistent " + DVI_NAME + "s");
				setTabs();
			}
		});
		dvipdf.add(rb);
		rb.setSelected(true);
		sub_buttons.add(rb);
		rb = new JRadioButton(PDF_NAME);
		rb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				do_pdf = true;
				bt_dvi_section.setText(PDF_NAME + " sec.");
				bt_dvi_all.setText(PDF_NAME + " all");
				bt_dvi_allinone.setText(PDF_NAME + " all-in-one");
				pdvi.setText("Persistent " + PDF_NAME +"s");
				setTabs();
			}
		});
		dvipdf.add(rb);
		sub_buttons.add(rb);

		pdvi = new JCheckBox("Persistent DVIs");
		pdvi.setSelected(persist_dvi);
		pdvi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				persist_dvi = e.getStateChange()== ItemEvent.SELECTED;
			}
		});
		sub_buttons.add(pdvi);
		exec_node.setEnabled(false);
		text_pane = new JPanel(new BorderLayout());
		text_pane.add(sub_buttons, BorderLayout.NORTH);
		text_pane.add(tabs);

		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(tree_pane);
		split.add(text_pane);
		content.add(split);
		content.add(buttons, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		cleanupTemp();
        		dispose();
        		engine.close();
        	}
        });

        if(params.length>0) {
        	File f = new File(params[0]);
        	if(f.exists()) {
        		loadFile(f);
        	}
        }
	}

    protected void initContext() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass");
		output_path = prefs.get("output_path", ".").split(";")[0];
		templates_root = prefs.get("templates_root", ".").split(";")[0];
		tex_path = prefs.get("tex_path", "");
		jdvi_font_nameformat = prefs.get("jdvi_font_nameformat", "");
		jdvi_font_path = prefs.get("jdvi_font_path", "");
		jdvi_resolution = prefs.get("jdvi_resolution", "");
	}

	protected void loadFile(File f) {
		try {
			//this will be result if we fail during parsing or executing file
			code_tree_model.setRoot(new DefaultMutableTreeNode("empty"));
			tree_model.setRoot(new DefaultMutableTreeNode("empty"));
			//no save or re-execute allowed until we succesfully load and parse file
			save.setEnabled(false);
			exec.setEnabled(false);
			exec_node.setEnabled(false);
			//clear old generated stuff
			tabs.removeAll();

			template_file = f;
			template_name = f.getName();
			if(template_name.lastIndexOf(".tex")==template_name.length()-4)
				template_name = template_name.substring(0, template_name.lastIndexOf(".tex"));
			tr = (TexReader) engine.getTemplateReader("tex");
			tr.loadTemplate(new FileInputStream(f));
			code_root = new CodeTreeNode(tr.getRootNode(), template_name);
			code_tree_model.setRoot(code_root);
			fillCodeTree(tr.getRootNode(), code_root);
			code_tree.setSelectionRow(0);
			exec.setEnabled(true);
			execute();
		} catch (Exception ex) {
			reportError(ex);
		}
	}

	protected void execute() {
		try {
			tr.execute();
			engine.clearDataSources();
		} catch(Exception e) {
			reportError(e);
		}
		root = new ResultRootNode(tr.getResultNode(), template_name);
		tree_model.setRoot(root);
		fillTree(tr.getResultNode(), root);
		tree.setSelectionRow(0);
		save.setEnabled(true);
	}

	void fillCodeTree(SimpleNode n, DefaultMutableTreeNode tn) {
		for(int i=0; i<n.jjtGetNumChildren();i++) {
			SimpleNode nn = (SimpleNode) n.jjtGetChild(i);
			if(nn!=null) {
				CodeTreeNode tn1 = new CodeTreeNode(nn);
				code_tree_model.insertNodeInto(tn1, tn, tn.getChildCount());
				if(n instanceof ComplexNode) {
					fillCodeTree(nn, tn1);
				}
			}
		}
	}

	void fillTree(ResultNode r, DefaultMutableTreeNode tn) {
		if(!(r instanceof RComplexNode))
			return;
		Iterator<ResultNode> it = ((RComplexNode)r).iterateResultNodes();
		if(it==null)
			return;
		while(it.hasNext()) {
			r = it.next();
			if(r instanceof RComplexNode || r instanceof RCall) {
				ResultTreeNode tn1;
				tn1 = new ResultTreeNode(r);
				tree_model.insertNodeInto(tn1, tn, tn.getChildCount());
//				tn.add(tn1);
				fillTree(r, tn1);
			}
		}
	}

	protected void	setTabs() {
		dvi_tabs.clear();
		if(!(selected.getUserObject() instanceof ResultNode)) {
			tabs.removeAll();
			return;
		}

		ResultNode r = selected.getResult();
		last_tab = tabs.getSelectedIndex();
		if(last_tab==-1)
			last_tab = 0;
		Vector<String> dvitorestore = new Vector<String>();
		if(persist_dvi)
			for(int i=tabs.indexOfComponent(code_pane)+1;i<tabs.getComponentCount();i++)
				dvitorestore.add(tabs.getTitleAt(i));
		tabs.removeAll();

		SimpleTextPanel text;
		//predefined sections go first
		Set<String> secs = new HashSet<String>(tr.getSectionNames());
		secs.remove("DEF");
		for(int i=0;i<predef_sections.length;i++) {
			tabs.add(predef_sections[i], text = new TexPanel());
			text.setText(r.getSection(predef_sections[i]));
			secs.remove(predef_sections[i]);
		}
		for(Iterator<String> it=secs.iterator(); it.hasNext();) {
			String s = it.next();
			String t = r.getSection(s);
			tabs.add(s, new JScrollPane(text = new TexPanel()));
			text.setText(t);
		}
		code_pane = tabs.add("Code", text = new SimpleTextPanel());
		text.setText(r.getMasterCopy().getCode());

		if(persist_dvi) {
			String secn;
			for(Iterator<String> it=dvitorestore.iterator();it.hasNext();) {
				if((secn = it.next().replaceFirst(" DVI", "").replaceFirst(" PDF", "")).equals(ALL_IN_ONE_NAME))
					composeAllInOneDvi(r);
				else
					composeDvi(secn, r);
			}
		}

		if(last_tab<tabs.getTabCount())
			tabs.setSelectedIndex(last_tab);
		else
			tabs.setSelectedIndex(last_tab = 0);
	}

	private Component composeDvi(String secn, ResultNode r) {
		if(r!=tr.getResultNode())
			return composeDvi(secn, ((RTemplate)tr.getResultNode()).wrapWithHeaders(r.getSection(secn)), r.hashCode());
		else
			return composeDvi(secn, r.getSection(secn), r.hashCode());
	}

	private Component composeAllInOneDvi(ResultNode r) {
		String tex ="";
		if(r==tr.getResultNode())
			r = ((RTemplate)r).getDocument();
		for(int i=0;i<tabs.indexOfComponent(code_pane);i++)
			tex = tex + "\\\\\\\\SECTION " + tabs.getTitleAt(i) + "\\\\\\\\" +
				r.getSection(tabs.getTitleAt(i));
		tex = ((RTemplate)tr.getResultNode()).wrapWithHeaders(tex);
		return composeDvi(ALL_IN_ONE_NAME, tex, r.hashCode());
	}

	private Component composeDvi(String secn, String tex, int node_hash) {
		//Compose TeX file for dvi creation
		File file = new File(System.getProperty("java.io.tmpdir"),
				template_name + node_hash + "-" + secn + ".tex");
		//shedule temporary file to cleanup on program exit
		files_to_cleanup.add(file);
		//Create dvi
		texToDvi(file, tex, do_pdf);
		if(do_pdf)
			file = new File(System.getProperty("java.io.tmpdir"),
					template_name + node_hash + "-" + secn + ".pdf");
		else
			file = new File(System.getProperty("java.io.tmpdir"),
					template_name + node_hash + "-" + secn + ".dvi");
		files_to_cleanup.add(file);
		//auxiliary files created by latex
		files_to_cleanup.add(new File(System.getProperty("java.io.tmpdir"),
				template_name + node_hash + "-" + secn + ".log"));
		files_to_cleanup.add(new File(System.getProperty("java.io.tmpdir"),
				template_name + node_hash + "-" + secn + ".aux"));

		//Store this in SmartAss preferences, put into jdvi properties before create it
		System.setProperty("jdvi.font.path", jdvi_font_path);
		System.setProperty("jdvi.font.nameformat", jdvi_font_nameformat);
		System.setProperty("jdvi.resolution", jdvi_resolution);

		//Show dvi in tab
		try {
			if(do_pdf) {
				PdfView pdf = new PdfView(file);
				tabs.add(secn + " " + PDF_NAME , pdf);
				dvi_tabs.put(secn, pdf);
				return pdf;
			} else {
				DviView dvi = new DviView();
				tabs.add(secn + " " + DVI_NAME , dvi);
				if(file.exists()) {
					dvi.dviFile = file;
					dvi.dvi.loadDocument(file.toURI().toURL(), "");
					dvi.repaint();
					dvi.dvi.gotoPage(0);
				}
				dvi_tabs.put(secn, dvi);
				return dvi;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	void writeFile(File out_file, String text) {
		try {
			FileWriter file_writer = new FileWriter(out_file);
			BufferedWriter out = new BufferedWriter(file_writer);
			out.write(text);
			out.close();
		} catch (Exception e) {
			reportError(e);
		}
	}

	private void texToDvi(File file, String tex, boolean do_pdf) {
		writeFile(file, tex);
		try {
			String line;
			BufferedReader input;
			Process p;

			if(do_pdf)
				p = Runtime.getRuntime().exec("pdflatex -interaction=batchmode \""
						+ file.getAbsolutePath() + "\"",
					new String[] {"TEXINPUTS=" + tex_path},
					file.getParentFile());
			else
				p = Runtime.getRuntime().exec("latex -interaction=batchmode \""
						+ file.getAbsolutePath() + "\"",
					new String[] {"TEXINPUTS=" + tex_path},
					file.getParentFile()); //.:/home/nik/workspace/SmartAss/test/output:/usr/share/texmf-texlive/tex/latex//
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			      while ((line = input.readLine()) != null) {
			        System.out.println(line);
			      }
			input.close();

			//p = Runtime.getRuntime().exec("kdvi " + file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-4) + ".dvi");

		} catch(Exception e) {
			e.printStackTrace();
		};
	}

	private void reportError(Exception e) {
		String s = e.getMessage();
		if(s==null)
			s = e.toString();
		if(debug_mode)
			e.printStackTrace();
		JOptionPane.showMessageDialog(this, s, "Error!", JOptionPane.ERROR_MESSAGE);
	}

	void cleanupTemp() {
		for(Iterator<File> it=files_to_cleanup.iterator();it.hasNext();)
			it.next().delete();
	}

	private void onNodeChanged(JTree sender) {
		if(sender==tree) {
			if(tree.getLastSelectedPathComponent() instanceof ResultTreeNode) {
				selected = (ResultTreeNode) tree.getLastSelectedPathComponent();
				if (selected == null)
					return;
				setTabs();
				exec_node.setEnabled(true);
			}
			split.setBottomComponent(text_pane);
		} else if(sender==code_tree && code_tree.getLastSelectedPathComponent() instanceof CodeTreeNode) {
			code_selected = (CodeTreeNode) code_tree.getLastSelectedPathComponent();
			code_text.setText(code_selected.getNode().getCode());
			split.setBottomComponent(code_text);
		}
	}

	public void valueChanged(TreeSelectionEvent e) {
		onNodeChanged((JTree)e.getSource());
	}

	public static void main(String s[]) throws IOException
	{
		params = s;
//		printLicense();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	        		InteractiveApp app = new InteractiveApp();
	                app.setVisible(true);
	            }
	        });
		//app.executeUI(s);
		return;
	}


}
