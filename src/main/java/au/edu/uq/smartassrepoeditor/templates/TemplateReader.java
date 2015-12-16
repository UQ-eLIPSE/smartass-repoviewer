/* This file is part of SmartAss and describes the TemplateReader class that is the ancestor 
 * of all possible SmartAss template reader classes.
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
package au.edu.uq.smartassrepoeditor.templates;

import au.edu.uq.smartassrepoeditor.engine.*;

import java.io.InputStream;
import java.util.*;

/**
 * The TemplateReader class is the abstract ancestor for all possible SmartAss template reader classes.
 */
abstract public class TemplateReader {
    protected Engine engine;
    protected String template; //M.b. not String, but some class - container for template text?
    protected boolean executed;

    /**
     * Creates new TempalteReader object
     * 
     * @param engine	the SmartAss engine object is used by the TemplateReader to interact with other SmartAss
     * 					components and with the environment 
     */
    TemplateReader(Engine engine)
    {
     this.engine = engine;
    }
    
    /**
     * Reads the template content as the {@link String} parameter
     */
    public void loadTemplate(String template) throws TemplateParseException 
    {
        this.template = template;
    }
    
    /**
     * Loads the template content from the given {@link InputStream}. This function from {@link TemplateReader}
     * actually do nothing - override it in descendants.
     * 
     * @param template		{@link InputStream} to read the template content
     * @throws TemplateParseException
     */
    public void loadTemplate(InputStream template) throws TemplateParseException {
    	
    }
	
    /**
     * Override this function to execute the template. Each {@link TemplateReader} descendant can (and should)
     * implement its own version of this function that will understand some specific template description language
     * and create a set of execution results - "sections" that represents specific problem solution phase - a question,
     * a solution etc
     * 
     * @return	the {@link Map} of execution results in form of SectionName -> SectionContent 
     */
    abstract public Map<String, String> execute();
    
    /**
     * Return true if this template already have been executed or false otherwise 
     */
    public boolean isExecuted() {
		return executed;
	}
}
