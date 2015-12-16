/* This file is a part of SmartAss and describes the SimpleScript class that is the SmartAss script processor.
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
package au.edu.uq.smartassrepoeditor.script;

import au.edu.uq.smartassrepoeditor.engine.Engine;
import au.edu.uq.smartassrepoeditor.engine.QuestionModule;
import au.edu.uq.smartassrepoeditor.script.ssparser.ParseException;
import au.edu.uq.smartassrepoeditor.script.ssparser.SimpleScriptParser;

import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SimpleScript class that is the SmartAss script processor that realizes a really simple scripting
 * language with a minimal statements set such as variable of given module type creation etc. 
 */
public class SimpleScript extends Script {

	/** Class logger. */
	private static final Logger LOG = LoggerFactory.getLogger( SimpleScript.class );


	/**
	 * Lookup for QuestionModules based on name.
     *
     * @todo May be the best place for this - engine? But put this here for a while...
	 */
	Map<String,QuestionModule> vars = new HashMap<>();


	public SimpleScript(Engine engine) {
		super(engine);
	}
	
	/**
	 * Executes the line of the scripting language.
	 * 
	 * @param line		the line of the scripting language.
	 * @return			the execution results
         * 
         * @todo: Simplify, refactor method - too long.
	 */
	protected String executeLine(SimpleScriptParser.ScriptLine line) {
		if(line instanceof SimpleScriptParser.VarCreation) {
			SimpleScriptParser.VarCreation vc = (SimpleScriptParser.VarCreation) line;
			Vector<String> params = new Vector<>();
			HashMap<String, DataArray> dsdata = new HashMap<>();
			for(Iterator<SimpleScriptParser.VarArg> it=vc.args.iterator();it.hasNext();) {
				SimpleScriptParser.VarArg arg = it.next();
				if(arg instanceof SimpleScriptParser.StrArg)
					params.add(((SimpleScriptParser.StrArg)arg).str);
				else if(arg instanceof SimpleScriptParser.DSArg) {
					SimpleScriptParser.DSArg dsarg = (SimpleScriptParser.DSArg) arg;
					DataArray d = dsdata.get(dsarg.dsname);
					if(d==null) {
						DataSource ds = engine.getDataSource(dsarg.dsname);
						if(ds==null)
							d = new DataArray();
						else
							d = ds.getData();
						dsdata.put(dsarg.dsname, d);
					}
					if(dsarg.fieldno>0)
						params.add(d.getField(dsarg.fieldno));
					else {
                                            params.addAll(Arrays.asList(d.getData()));
					}
				}
			}
			createVar(vc.moduleName, vc.varName, params.toArray(new String[]{}));	
		} else if(line instanceof SimpleScriptParser.SectionAccess) {
			SimpleScriptParser.SectionAccess sa = (SimpleScriptParser.SectionAccess) line;
			return callMethod(sa.varName, sa.sectionName);	
		} else if(line instanceof SimpleScriptParser.DSCreation) {
			SimpleScriptParser.DSCreation dsc = (SimpleScriptParser.DSCreation) line;
			DataSource ds = null;
			DataReader dr;
			if(dsc.type.equals("INFILE"))
				dr = new CsvDataReader(new FileDataStorage(new File(dsc.path)));
			else
				return "";
			switch(dsc.kind) {
			case SimpleScriptParser.DSCreation.RANDOM:
				ds = new RandomDataSource(dr); break;
			case SimpleScriptParser.DSCreation.RANDOM_UNIQUE:
				ds = new RandomDataSource(dr, true); break;
			case SimpleScriptParser.DSCreation.SEQUENTIAL:
				ds = new SequentialDataSource(dr); break;
			case SimpleScriptParser.DSCreation.BYLINENO:
				ds = new LineNumberDataSource(dr, dsc.num); break;
			case SimpleScriptParser.DSCreation.BYKEY:
				ds = new KeyDataSource(dr, dsc.num, dsc.key);
			}
			if(ds!=null)
				engine.addDataSource(dsc.name, ds);
		}
 
		return "";
	}
	
	/**
	 * Adds to lookup a 'variable' that is an instance of QuestionModule identified by class name.
     * Note that if the 'variable' name already exists, a new instance is not added to the Map.
	 * 
	 * @param type		the module class name
	 * @param name		the name for the variable
	 * @param params	module constructor parameters
	 */
	private void createVar(String type, String name, String[] params) {
		LOG.info( "::createVar()[ type=>{}, name=>{}, params=>{} ]", type, name, Arrays.toString(params) );
		if ( ! vars.containsKey(name) ) vars.put(name, engine.getQuestionModule(type, params));
	}

	/**
	 * Executes the block of the scripting language.	 
	 */
	public String executeBlock(String block) {
		Vector<SimpleScriptParser.ScriptLine> lines;
		try {
			lines = (new SimpleScriptParser(new StringReader(block))).parse();
			String s = ""; //Script: " + Integer.toString(lines.size()) + " lines";
			for(int i=0; i<lines.size();i++)
				s = s + executeLine(lines.get(i));
			return s;
		} catch(ParseException e) {
			return e.getMessage();
		} catch(Error e) {
			return e.getMessage();
		}
	}

	/**
	 * Takes a raw String with parameters for QuestionModule initialization
	 * and decodes it to array of Strings
	 * The format of parameters is <i>param,param, ..., param</i> 
	 * where param has a form of <br><br>
	 * 		<i>"any chars inside \", including comma"</i> <br> 
	 * or <br>
	 * 		<i>any chars but comma</i> <br><br>  
	 * though for readability reasons it is recommended to quote
	 * all strings except numbers or names (without separators inside)    
	 * 
	 * @param param		raw String with parameters for QuestionModule initialization
	 * @return			String array with decoded parameters
	 */
	String[] decodeParams(String param) {
		Vector<String> res = new Vector<String>();
		
		Pattern pat = Pattern.compile("^\\s*\".*?[^\\\\]\"\\s*,");          
		Pattern pat0 = Pattern.compile("^\\s*\".*?[^\\\\]\"\\s*\\z");          
		Pattern pat1 = Pattern.compile("^.*?,");
		Matcher matcher = null;
		boolean found = true;
		while(found) {
			if(found = (matcher = pat.matcher(param)).find())
				res.add(clearParam(matcher.group().substring(0, matcher.group().length()-1).trim()));
			else if(found = (matcher = pat0.matcher(param)).find())
				res.add(clearParam(matcher.group().substring(0, matcher.group().length()-1).trim()));
			else if(found = (matcher = pat1.matcher(param)).find())
				res.add(clearParam(matcher.group().substring(0, matcher.group().length()-1).trim()));
			if(found)	
				param = param.substring(matcher.group().length());
		}
		if(param.length()>0) 
			res.add(clearParam(param.trim()));
		
		String[] params = new String[res.size()];
		for(int i=0; i<res.size(); i++) 
			params[i] = res.get(i);
		return params;
	}

	private String clearParam(String param) {
		if(param.length()==0) return param;

		param = param.replaceAll("\\\\\"", "\"");
		if(param.charAt(0)=='"') param = param.substring(1);
		if(param.charAt(param.length()-1)=='"') param = param.substring(0, param.length()-1);
		return param;
	}
	
	private String callMethod(String name, String method) {
		QuestionModule var = (QuestionModule) vars.get(name);
		if (null == var) return "[variable \""+name+"\" NOT found!]";
                return var.getSection(method.toLowerCase());
	}
}
