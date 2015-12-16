/* This file is part of SmartAss and contains the SettingsDialog class that 
 * is used to edit SmartAss repository-related settings
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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * the SettingsDialog class is used to edit SmartAss repository-related settings
 */
public class SettingsDialog extends JDialog {
	JTextField tex_path, pdf_path, files_path;
	final int MAX_FIELDS_WIDTH = 2000; 

	public SettingsDialog(Frame owner) {
		super(owner, "Settings", true);
		
		setSize(500,350);
		JPanel controls = new JPanel();
		JPanel buttons = new JPanel();
		
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(new JLabel("Path to templates storage root directory"));
		controls.add(tex_path = new JTextField());
		tex_path.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		controls.add(new JLabel("Path to pdfs storage root directory"));
		controls.add(pdf_path = new JTextField());
		pdf_path.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		controls.add(new JLabel("Path to files storage directory"));
		controls.add(files_path = new JTextField());
		files_path.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		
		buttons.add(new JButton(new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				save();
				close();
			}
		}));
		buttons.add(new JButton(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		}));
		
		add(controls);
		add(buttons, BorderLayout.SOUTH);
		load();
		pack();
	}

	private void close() {
	      dispose();
	}

	private void save() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass/repository");
		prefs.put("tex_path", tex_path.getText());
		prefs.put("pdf_path", pdf_path.getText());
		prefs.put("files_path", files_path.getText());
	}

	private void load() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass/repository");
		tex_path.setText(prefs.get("tex_path", ""));
		pdf_path.setText(prefs.get("pdf_path", ""));
		files_path.setText(prefs.get("files_path", ""));
	}
	
	
}
