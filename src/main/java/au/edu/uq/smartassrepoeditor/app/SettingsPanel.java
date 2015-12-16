/* This file is part of SmartAss and describes the SettingsPanel class, the JPanel descendant that
 * contains SmartAss application related settings editors.
 * Copyright (C) 2006 Department of Mathematics, The University of Queensland
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
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * The SettingsPanel class is the JPanel descendant that
 * contains SmartAss application related settings editors
 */
public class SettingsPanel extends JPanel {
	JTextField edPathToModules;
	JTextField edPathToTemplates;
	JTextField edPathToOutput;
	JTextField edLatexCommand;
	JTextField edSections;
	JTextField edOpFile;
	JTextField edTexSearchPath;

	JTextField edJDviFontPath;
	JTextField edJDviFontNameFormat;
	JTextField edJDviResolution;

	JFrame frame;
	JButton close_button;

	final int MAX_FIELDS_WIDTH = 2000;

	public SettingsPanel(JFrame frame) {
		this.frame = frame;
		createControls();
	}

	public void setCloseAction(ActionListener ac) {
		close_button.addActionListener(ac);
	}

	private void createControls() {
		setLayout(new BorderLayout());

		JPanel pn = new JPanel();

		pn.setMinimumSize(new Dimension(300,300));
		pn.setLayout(new BoxLayout(pn, BoxLayout.Y_AXIS));
        pn.add(new JLabel("Path to maths modules"));
		pn.add(edPathToModules = new JTextField());
		JLabel lb = new JLabel("<html><b>Attention!</b> Path to maths modules should be a path to directory listed in Java CLASSPATH, NOT one with modules .class files!</html>");
		lb.setFont(new Font("SanSerif", Font.ITALIC, 12));
		pn.add(lb);
		edPathToModules.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Path to TeX templates"));
		pn.add(edPathToTemplates = new JTextField());
		edPathToTemplates.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Path for output files"));
		pn.add(edPathToOutput = new JTextField());
		edPathToOutput.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));

		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Path to folder with input TeX files"));
		pn.add(edTexSearchPath = new JTextField());
		edTexSearchPath.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		lb = new JLabel("<html><b>Attention!</b> If this feald doesn't empty it has to contain path to default LaTeX directory! (see LaTeX manual for you system)</html>");
		lb.setFont(new Font("SanSerif", Font.ITALIC, 12));
		pn.add(lb);

		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("LaTeX run command"));
		pn.add(edLatexCommand = new JTextField());
		edLatexCommand.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Default sections"));
		pn.add(edSections = new JTextField());
		edSections.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
//		pn.add(Box.createRigidArea(new Dimension(0,5)));
//		pn.add(new JLabel("Default Ops representation file"));
//		pn.add(edOpFile = new JTextField());
//		edOpFile.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("SmartAss", pn);

		pn = new JPanel();
		pn.setLayout(new BoxLayout(pn, BoxLayout.Y_AXIS));
		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Font path"));
		pn.add(edJDviFontPath = new JTextField());
		edJDviFontPath.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Font name format"));
		pn.add(edJDviFontNameFormat = new JTextField());
		edJDviFontNameFormat.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		pn.add(Box.createRigidArea(new Dimension(0,5)));
		pn.add(new JLabel("Resolution"));
		pn.add(edJDviResolution = new JTextField());
		edJDviResolution.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));

		tabs.add("JDvi", pn);

		add(tabs);

		JPanel pb = new JPanel();
		JButton bt;
		add(pb, BorderLayout.EAST);
		pb.setLayout(new BoxLayout(pb, BoxLayout.Y_AXIS));
		pb.add(bt = new JButton("Save"));
		bt.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e)
			   {
			      doSave();
			   }
			});
		pb.add(Box.createRigidArea(new Dimension(0,20)));
		pb.add(bt = new JButton("Save As..."));
		bt.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e)
			   {
			      doSaveAs();
			   }
			});
		pb.add(Box.createRigidArea(new Dimension(0,20)));
		pb.add(bt = new JButton("Refresh"));
		bt.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e)
			   {
			      doRefresh();
			   }
			});
		pb.add(Box.createRigidArea(new Dimension(0,20)));
		pb.add(bt = new JButton("Load From..."));
		bt.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e)
			   {
			      doLoadFrom();
			      doRefresh();
			   }
			});
		pb.add(Box.createRigidArea(new Dimension(0,20)));
		pb.add(close_button = new JButton("Close"));

		doRefresh();
	}

	private void doSave() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass");
		prefs.put("modules_root", edPathToModules.getText());
		prefs.put("templates_root", edPathToTemplates.getText());
		prefs.put("output_path", edPathToOutput.getText());
		prefs.put("latex", edLatexCommand.getText());
		prefs.put("sections", edSections.getText());
		prefs.put("tex_path", edTexSearchPath.getText());

		prefs.put("jdvi_font_path", edJDviFontPath.getText());
		prefs.put("jdvi_font_nameformat", edJDviFontNameFormat.getText());
		prefs.put("jdvi_resolution", edJDviResolution.getText());
	}

	private void doSaveAs() {
		FileDialog fd = new FileDialog(frame, "Save settings", FileDialog.SAVE);
		fd.setVisible(true);
		String fname = fd.getFile();
		if(fname!=null) {
			Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass");
			try {
				FileOutputStream fw = new FileOutputStream(fname);
				prefs.exportSubtree(fw);
			} catch(FileNotFoundException e) {
				System.out.println("Error creating output file!");
			} catch(IOException e) {
				System.out.println("Error writing data!");
			} catch(BackingStoreException e) {
				System.out.println("Error reading settings!");
			}
		}
	}

	private void doRefresh() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass");
		edPathToModules.setText(prefs.get("modules_root", "."));
		edPathToTemplates.setText(prefs.get("templates_root", "."));
		edPathToOutput.setText(prefs.get("output_path", "."));
		edLatexCommand.setText(prefs.get("latex", ""));
		edSections.setText(prefs.get("sections", ""));
		edTexSearchPath.setText(prefs.get("tex_path", ""));

		edJDviFontPath.setText(prefs.get("jdvi_font_path", ""));
		edJDviFontNameFormat.setText(prefs.get("jdvi_font_nameformat", ""));
		edJDviResolution.setText(prefs.get("jdvi_resolution", ""));
	}

	private void doLoadFrom() {
		FileDialog fd = new FileDialog(frame, "Load settings", FileDialog.LOAD);
		fd.setVisible(true);
		String fname = fd.getFile();
		if(fname!=null) {
			try {
				FileInputStream fr = new FileInputStream(fname);
				Preferences.importPreferences(fr);
			} catch(FileNotFoundException e) {
				System.out.println("Error creating output file!");
			} catch(IOException e) {
				System.out.println("Error writing data!");
			} catch (InvalidPreferencesFormatException e) {
				System.out.println("Invalid settings format!");
			}
		}
	}
}
