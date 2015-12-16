/* This file is part of SmartAss and contains the RSectionsTextNode class that is the container for
 * any TeX content.
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

import java.util.HashMap;

import au.edu.uq.smartassrepoeditor.templates.texparser.ASTAnyText;
import au.edu.uq.smartassrepoeditor.templates.texparser.ResultNode;

/**
 * The RSectionsTextNode class is the container for any TeX content in template execution results tree.
 */
public class RSectionsTextNode extends ResultNode {
	HashMap<String, String> sections = new HashMap<String, String>();

	public RSectionsTextNode() {
		super(null);
		ASTAnyText n = new ASTAnyText(0);
		n.setText("");
		master_copy = n;
		node_name = "UserChangedText";
	}

	public void setSection(String name, String text) {
		sections.put(name, text);
	}

	@Override
	public String getSection(String section) {
		String s = sections.get(section);
		if(s!=null)
			return s;
		else
			return "";
	}

}
