/* This file is part of SmartAss and contains the ItemEditForm class that 
 * is the ancestor for different single item-oriented data edit forms
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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import au.edu.uq.smartassrepoeditor.repository.data.ItemDataModel;

/**
 * The ItemEditForm class is the ancestor for different single item-oriented data edit forms
 */
public class ItemEditForm extends JInternalFrame {
	JPanel root, controls, buttons;
	ItemDataModel model;

	class OkAction extends AbstractAction {
		public OkAction(String title) {
			super(title);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(validateData()) {
				controlsToModel();
				if(updateData()) {
					setVisible(false);
					dispose();
				}
			}
		}
	}

	class CancelAction extends AbstractAction {
		public CancelAction(String title) {
			super(title);
		}
		
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	}


	public ItemEditForm(ItemDataModel model, String title) {
		super(title, true, true, true, true);
		this.model = model;
		
		root = new JPanel();
		controls = new JPanel();
		buttons = new JPanel();
		buttons.add(new JButton(new OkAction("OK")));
		buttons.add(new JButton(new CancelAction("Cancel")));
		System.out.println(">>>>>>>>>>> Getting here 2");
		createControls();
		System.out.println(">>>>>>>>>>> Getting here 3");
		root.setLayout(new BoxLayout(root, BoxLayout.PAGE_AXIS));
		root.add(controls);
		root.add(buttons);
		//buttons.setLayout(new BoxLayout(root, BoxLayout.LINE_AXIS));
		add(root);
		setSize(200, 100);
		
	}
	
	protected void createControls() {
		
	}
	
	protected void modelToControls() {
	}
	
	protected void controlsToModel() {
	}
	
	protected boolean validateData() {
		return true;
	}
	
	protected boolean updateData() {
		model.validate();
		model.update();
		return true;
	}
}
