/* This file is part of SmartAss and describes the TexReader class - the default SmartAss 
 * template processor.
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
import au.edu.uq.smartassrepoeditor.script.*;
import au.edu.uq.smartassrepoeditor.templates.texparser.ASTTemplate;
import au.edu.uq.smartassrepoeditor.templates.texparser.ParseException;
import au.edu.uq.smartassrepoeditor.templates.texparser.ResultNode;
import au.edu.uq.smartassrepoeditor.templates.texparser.TexParser;
import au.edu.uq.smartassrepoeditor.templates.texparser.SimpleNode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector; 	// @TODO: use List or Set


/**
 * The TexReader class is the default SmartAss template processor that understands a set of 
 * statements such as REPEAT, CALL, etc - see SmartAss user guide for complete list of 
 * template language statements.
 */
public class TexReader extends TemplateReader {
	protected boolean slave;
	protected Script script;
	protected String top;
	protected String bottom;
	protected HashMap<String, String> sections = new HashMap<String, String>();
	//protected HashMap<String, String> outs;// = new HashMap<String, String>();

	Set<String> predefined_names = new HashSet<String>();
	Set<String> names;
    
    protected TexParser parser;
    protected SimpleNode root;
    protected ResultNode result;
    
    @Override
    /**
     * Reads the template content as the {@link String} parameter
     */
    public void loadTemplate(String template) throws TemplateParseException {
    	loadTemplate(new ByteArrayInputStream(template.getBytes()));
    }
    
    /**
     * Loads and parses the template content from the given {@link InputStream}. 
     * 
     * @param template		{@link InputStream} to read the template content
     * @throws TemplateParseException
     */
    public void loadTemplate(InputStream template) throws ParseException {
		parser = new TexParser(template);
		parser.setEngine(engine);
		root = parser.parse();
		if(slave)
			root = ((ASTTemplate)root).getDocument();
    }
	
    /**
     * Executes the template creating a set of execution results - "sections" that represents 
     * specific problem solution phases such as a question, a solution etc
     * 
     * @return	the {@link Map} of execution results in form of SectionName -> SectionContent 
     */
    public Map<String, String> execute() {
    	names = new HashSet<String>(predefined_names); 
    	script = new SimpleScript(engine);
    	result = root.execute(names, script);
    	
    	Map<String, String> outs = new HashMap<String, String>();
	for (String name : names) outs.put(name, result.getSection(name));
    	
    	executed = true;
    	return outs;
    }
    
    /**
     * Returns the root node of the parsed template statement nodes tree
     */
    public SimpleNode getRootNode() {
    	return root;
    }
    
    /**
     * Returns the root node of the template execution result nodes tree
     */
    public ResultNode getResultNode() {
		return result;
	}
    
    public void setResultNode(ResultNode resultNode) {
    	result = resultNode;
    }
    
    /**
     * Creates new TexReader object
     * 
     * @param engine	the SmartAss engine object is used by the TemplateReader to interact with other SmartAss
     * 					components and with the environment 
     */
    public TexReader(Engine engine) {super(engine) ;}
	
    /**
     * Creates new TexReader object that can be the "slave" - the special {@link TexReader} mode
     * when it assumes that its execution results will be inserted into the some higher level caller
     * execution results
     * 
     * @param engine	the SmartAss engine object is used by the TemplateReader to interact with other SmartAss
     * 					components and with the environment 
     */
    public TexReader(Engine engine, boolean slave) {
    	super(engine);
    	this.slave = slave;
    }
    
    /**
     * Returns the content of the section with the given name
     * 
     * @param sec_name		the section name
     * @return				the content of the section
     */
    public String getSection(String sec_name) {
        return result.getSection(sec_name);
    }
    
    /**
     * Returns the list of section names
     */
    public Vector<String> getSectionNames() {
        return new Vector<String>(names);
    }
    
    /**
     * Sets predefined section names - the minimal set of the section that 
     * should appear in the execution results even if they don't exist in
     * the template code
     */
    public void setPredefinedNames(Set<String> names) {
    	predefined_names.clear();
    	predefined_names.addAll(new HashSet<String>(names));
	}

    /**
     * Sets predefined section names - the minimal set of the section that 
     * should appear in the execution results even if they don't exist in
     * the template code
     */
    public void setPredefinedNames(String[] names) {
    	predefined_names.clear();
		for(int i=0;i<names.length;i++)
			predefined_names.add(names[i]);
	}

    public Script getScript() {
		return script;
	}
}
