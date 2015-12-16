/** @(#)TemplateNotFoundException.java
 *
 * This file is a part of SmartAss and describes the TemplateNotFoundException class 
 * that represents the exception that is fired when engine can't find requested template file. 
 *   
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
 *
 */

package au.edu.uq.smartassrepoeditor.templates;

/**
 * The TemplateNotFoundException class represents the exception that is 
 * fired when engine can't find requested template file.
 */
public class TemplateNotFoundException extends Exception {
	final static String msq_prefix = "Error! Template not found: "; 

	public TemplateNotFoundException(String message) {
		super(msq_prefix + message + "\n");
	}

	public TemplateNotFoundException(Throwable cause) {
		super(cause);
	}

	public TemplateNotFoundException(String message, Throwable cause) {
		super(msq_prefix + message + "\n", cause);
	}

}
