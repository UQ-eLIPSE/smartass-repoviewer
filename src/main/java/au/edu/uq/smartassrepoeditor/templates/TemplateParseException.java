/** @(#)TemplateParseException.java
 *
 * This file is a part of SmartAss and describes the TemplateParseException class 
 * that represents the exception that is fired when SmartAss parser can't parse template. 
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
 * The TemplateParseException class represents the exception 
 * that is fired when SmartAss parser can't parse template. 
 */
public class TemplateParseException extends Exception {

	public TemplateParseException() {
		super();
	}

	public TemplateParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public TemplateParseException(String message) {
		super(message);
	}

	public TemplateParseException(Throwable cause) {
		super(cause);
	}
}
