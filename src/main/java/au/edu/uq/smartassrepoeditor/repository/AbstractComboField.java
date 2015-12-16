/* This file is part of SmartAss and contains the AbstractComboField class that 
 * is one of the repository editor visual controls
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
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * The AbstractComboField class is a one of the repository editor visual controls
 */
public class AbstractComboField extends JPanel {
    private JTextField text = new JTextField();
    protected JButton button = new JButton();
    
    public AbstractComboField()
    {
        super( new BorderLayout() );
        
        // Setup button
        button.setText( " ... " );
        button.setOpaque( false );
        button.setFocusPainted( false );
        button.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
        Color bkgd = text.getBackground();
        button.setBackground( bkgd );
        this.setBackground( bkgd );
        
        // Steal the text field's border
        this.setBorder( text.getBorder() );
        text.setBorder( null );
        
        add( text, BorderLayout.CENTER );
        add( button, BorderLayout.EAST );
    }
    
    public void addActionListener(ActionListener listener) {
    	button.addActionListener(listener);
	}
    
    public void setText(String text) {
		this.text.setText(text);
	}
    
    public String getText() {
		return text.getText();
	}
}

