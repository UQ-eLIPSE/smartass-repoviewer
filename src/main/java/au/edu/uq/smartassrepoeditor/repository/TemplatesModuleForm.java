/* This file is part of SmartAss and contains the TemplatesModuleForm class that 
 * is the editor form for SmartAss module metadata in the template
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The TemplatesModuleForm class is the editor form for SmartAss module metadata in the template
 */
public class TemplatesModuleForm extends LookupTableDialog {
	private JTextArea parameters;

	public TemplatesModuleForm(LookupTableModel model) {
		super(model);
	}
	
	public TemplatesModuleForm(LookupTableModel model, int module_id) {
		super(model);
		for(int i=0; i<model.getRowCount();i++) 
			if((Integer)model.getValue(i, "id")==module_id) {
				table.getSelectionModel().addSelectionInterval(i, i);
				break;
		}
	}
	
	@Override
	protected void createControls() {
		JLabel lb;
		controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
		controls.add(lb = new JLabel("Select module:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		super.createControls();
		sp.setAlignmentX(Component.LEFT_ALIGNMENT);
		controls.add(lb = new JLabel("Parameters:"));
		lb.setAlignmentX(Component.LEFT_ALIGNMENT);
		parameters = new JTextArea();
		JScrollPane sp1 = new JScrollPane(parameters);
		sp1.setPreferredSize(new Dimension(200, 100));
		controls.add(sp1);
		sp1.setAlignmentX(Component.LEFT_ALIGNMENT);
		
	}

	public void setParameters(String par) {
		parameters.setText(par);
	}
	
	public String getParameters() {
		return parameters.getText();
	}
}
