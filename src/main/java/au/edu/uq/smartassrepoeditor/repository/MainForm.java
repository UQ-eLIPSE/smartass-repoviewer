/* This file is part of SmartAss and contains the MainForm class that 
 * represents the main form of the repository editor
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import au.edu.uq.smartassrepoeditor.repository.data.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The MainForm class represents the main form of the repository editor
 */
public class MainForm extends JFrame {
	static String[] conn_str;
	JDesktopPane desktop;
    Connection conn;
    Database data;
	RepositoryStorage storage;
	HashMap<String, JInternalFrame> forms = new HashMap<String, JInternalFrame>();

    abstract class EditDictionaryAction extends AbstractAction implements InternalFrameListener {
    	String form_name;
    	
        public EditDictionaryAction(String text) {
            super(text);
            putValue(SHORT_DESCRIPTION, "Edit " + text);
        }

        public void actionPerformed(ActionEvent e) {
        	JInternalFrame form = findForm(form_name);
        	if(form==null) {
        		form = createInitForm();
        	    form.pack();
        		desktop.add(form);
        		forms.put(form_name, form);
        		form.addInternalFrameListener(this);
        		form.setName(form_name);
        	}
        	form.setVisible(true);
        	form.toFront();
        	try {
				form.setSelected(true);
			} catch (PropertyVetoException e1) {
				e1.printStackTrace();
			}
        }
        
        protected JInternalFrame findForm(String form_name) {
        	return forms.get(form_name);
        }
        
        abstract protected JInternalFrame createInitForm();
        
        public void internalFrameActivated(InternalFrameEvent e) {}
        public void internalFrameClosing(InternalFrameEvent e) {}
        public void internalFrameDeactivated(InternalFrameEvent e) {}
        public void internalFrameDeiconified(InternalFrameEvent e) {}
        public void internalFrameIconified(InternalFrameEvent e) {}
        public void internalFrameOpened(InternalFrameEvent e) {}
        
        public void internalFrameClosed(InternalFrameEvent e) {
        	forms.remove(((JComponent)e.getSource()).getName());
        }
    }
    
    class AuthorsDictionaryAction extends EditDictionaryAction {
        
    	public AuthorsDictionaryAction() {
            super("Authors");
        	form_name = "authors";
        }

        protected JInternalFrame createInitForm() {
        	return new DBTableForm(data.getAuthorsModel(), "Authors");
        }
    }

    class ClassificationsDictionaryAction extends EditDictionaryAction {
        
    	public ClassificationsDictionaryAction() {
            super("Classifications");
        	form_name = "classifications";
        }

        protected JInternalFrame createInitForm() {
           	return new ClassificationsForm(data.getClassificationsModel());
        }
    }

    class FilesDictionaryAction extends EditDictionaryAction {
        
    	public FilesDictionaryAction() {
            super("Files");
        	form_name = "files";
        }

        protected JInternalFrame createInitForm() {
           	return new FilesForm(storage, data.getFilesModel());
        }
    }

    class ModulesDictionaryAction extends EditDictionaryAction {
        
    	public ModulesDictionaryAction() {
            super("Modules");
        	form_name = "modules";
        }

        protected JInternalFrame createInitForm() {
           	return new ModulesForm(data.getModulesModel());
        }
    }

    class TemplatesAction extends EditDictionaryAction {
        
    	public TemplatesAction() {
            super("Show templates");
        	form_name = "files";
        }

        protected JInternalFrame createInitForm() {
           	return new TemplatesForm(storage, data.getTemplatesModel());
        }
    }

	
	/**
	 * @throws HeadlessException
	 */
	public MainForm() throws HeadlessException {
		super();
		
		storage = new RepositoryStorage();
		data = new Database(conn_str[0],conn_str[1],conn_str[2], storage);
		
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	      System.out.println("Error setting native LAF: " + e);
	    }
		setTitle("SmartAss repository editor");
	    Container content = getContentPane();
	    desktop = new JDesktopPane();
	    content.add(desktop, BorderLayout.CENTER);
	    setSize(1000, 700);
	    
	    try {
	    	Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
		try {
//		    conn = DriverManager.getConnection("jdbc:mysql://localhost/smartass_repository?user=root&password=");
		    conn = DriverManager.getConnection(conn_str[0],conn_str[1],conn_str[2]);
		} catch (SQLException ex) {
		    System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}

        createMenu();
        
        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		//super.windowClosed(e);
        		dispose();
        	}
        });
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem; 

		menu = new JMenu("Dictionaries");
	    menu.setMnemonic(KeyEvent.VK_D);
	    menu.getAccessibleContext().setAccessibleDescription(
	              "Edit dictionaries");
	    menuBar.add(menu);
        
        menu.add(menuItem = new JMenuItem(new AuthorsDictionaryAction()));
        menu.add(menuItem = new JMenuItem(new ClassificationsDictionaryAction()));
        menu.add(menuItem = new JMenuItem(new FilesDictionaryAction()));
        menu.add(menuItem = new JMenuItem(new ModulesDictionaryAction()));
        menu.add(new AbstractAction("Settings") {
        	public void actionPerformed(ActionEvent e) {
        		SettingsDialog settings = new SettingsDialog(getMainThis());
        		settings.setVisible(true);
        	}
        });
        
        JMenu templates = new JMenu("Templates");
        menuBar.add(templates);
        templates.add(new TemplatesAction());
        
        //menuBar.add(new JMenuItem(new TemplatesAction()));
        
        setJMenuBar(menuBar);
	}
	
	protected MainForm getMainThis() {
		return this;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		conn_str=new String[3];
		//conn_str[0] = args[0]; 
		//conn_str[1]=args[1]; 
		//if(args.length>2)
			//conn_str[2]=args[2];
		
		conn_str[0] = "jdbc:mysql://localhost/smartassignments";
		conn_str[1] = "smartass";
		conn_str[2] = "SmartAss";
		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new MainForm();
                frame.setVisible(true);
            }
        });
	}

}
