/* This file is part of SmartAss and contains the AssignmentsConstructorSettingsDialog class
 * that represents the settings form that is used to edit assignment constructor related properties.
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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * The AssignmentsConstructorSettingsDialog class
 * represents the settings form that is used to edit assignment constructor related properties.
 */
public class AssignmentsConstructorSettingsDialog extends JDialog {
	final int MAX_FIELDS_WIDTH = 2000;
	JTextField db_path;
	JTextField db_user_name;
	JTextField db_password;
	JTextPane tex_header;

	public AssignmentsConstructorSettingsDialog(JFrame owner) {
		super(owner, "Assigment constructor settings", true);

		setSize(500,400);
		JPanel controls = new JPanel();
		JPanel buttons = new JPanel();

		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		controls.add(new JLabel("Database connection string"));
		controls.add(db_path = new JTextField());
		db_path.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		controls.add(new JLabel("Database connection username"));
		controls.add(db_user_name = new JTextField());
		db_user_name.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		controls.add(new JLabel("Database connection password"));
		controls.add(db_password = new JTextField());
		db_password.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 20));
		controls.add(new JLabel("Assigment TeX header"));
		controls.add(tex_header = new JTextPane());
		tex_header.setMaximumSize(new Dimension(MAX_FIELDS_WIDTH, 120));


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
		prefs.put("repository_db_path", db_path.getText());
		prefs.put("repository_db_user", db_user_name.getText());
		prefs.put("repository_db_password", db_password.getText());
		prefs = Preferences.userRoot().node("au/edu/uq/smartass/app");
		prefs.put("assigments_constructor_tex_header", tex_header.getText());
	}

	private void load() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass/repository");
		db_path.setText(prefs.get("repository_db_path", ""));
		db_user_name.setText(prefs.get("repository_db_user", ""));
		db_password.setText(prefs.get("repository_db_password", ""));
		prefs = Preferences.userRoot().node("au/edu/uq/smartass/app");
		tex_header.setText(prefs.get("assigments_constructor_tex_header", ""));
	}


}
