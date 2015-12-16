/* This file is part of SmartAss and contains the ModuleEditForm class that 
 * is the editor form for SmartAss module metadata
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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import au.edu.uq.smartassrepoeditor.repository.data.DataValidationException;
import au.edu.uq.smartassrepoeditor.repository.data.ModulesItemModel;

/**
 * The ModuleEditForm class is the edit forms for SmartAss module metadata
 */
public class ModuleEditForm extends ItemEditForm {
	ModulesItemModel model;
	
	JTextField name;
	JTextField module_package; 
	JTextArea description;
	JTextArea parameters;

	public ModuleEditForm(ModulesItemModel model) {
		super(model, "Edit module");
		this.model = model;
		modelToControls();
	}

	protected void createControls() {
		name = new JTextField();
		module_package = new JTextField(); 
		description = new JTextArea();
		parameters = new JTextArea();
		
		JLabel lb;
		controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
		controls.add(lb = new JLabel("Module name:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(name);
		controls.add(lb = new JLabel("Package:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(module_package);
		controls.add(lb = new JLabel("Parameters:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		parameters.setAlignmentX(Component.LEFT_ALIGNMENT);
		JScrollPane sp = new JScrollPane(parameters);
		sp.setPreferredSize(new Dimension(200, 100));
		controls.add(sp);
		controls.add(lb = new JLabel("Description"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		description.setAlignmentX(Component.LEFT_ALIGNMENT);
		sp = new JScrollPane(description);
		sp.setPreferredSize(new Dimension(200, 100));
		controls.add(sp);
	}
	
	protected void modelToControls() {
		name.setText(model.getName());
		module_package.setText(model.getModulePackage());
		description.setText(model.getDescription());
		parameters.setText(model.getParameters());
	}
	
	protected void controlsToModel() {
		model.setName(name.getText());
		model.setModulePackage(module_package.getText());
		model.setDescription(description.getText());
		model.setParameters(parameters.getText());
	}
	
	@Override
	protected boolean updateData() {
		try {
			return super.updateData();
		} catch (DataValidationException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Data validation error", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}
}
