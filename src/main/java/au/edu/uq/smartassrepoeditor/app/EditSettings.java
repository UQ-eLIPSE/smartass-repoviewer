/* This file is part of SmartAss and describes the EditSettings class, the user friendly application
 * for setting up environment for SmartAss.
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

import javax.swing.*;
import java.awt.event.*;

/**
 * The EditSettings class is the user friendly application
 * for setting up environment for SmartAss.
 */
public class EditSettings extends JFrame {

	public EditSettings() {
		super("SmartAss Settings");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,350);

		SettingsPanel pn = new SettingsPanel(this);
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


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EditSettings es = new EditSettings();
		es.setVisible(true);
	}

}
