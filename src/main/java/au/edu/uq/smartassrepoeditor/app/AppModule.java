/* This file is part of SmartAss and describes class AppModule for simple commandline user interface.
 * Copyright (C) 2006 Andriy Kvyatkovskyy, The University of Queensland
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

import au.edu.uq.smartassrepoeditor.engine.*;
import au.edu.uq.smartassrepoeditor.templates.TemplateNotFoundException;
import au.edu.uq.smartassrepoeditor.templates.TemplateParseException;

import java.io.*;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class AppModule {

	protected Engine engine = Engine.getInstance();

	protected boolean doPdf = false;

	/**
	 * Runs some kind of user interface... command line will be good for a start
	 *
	 * @param       params  parameters list (params[0] is an input file name
	 */
	void executeUI(String params[])  throws IOException
	{
		for(int i=0;i<params.length;i++) {
			System.out.println(i);
			if(params[i].startsWith("--"))
				processParam(params[i]);
		}

		for(int i=0;i<params.length;i++)
			if(!params[i].startsWith("--")) {
				processTemplate(params[i]);
			}
	}

	protected void processParam(String par){
		par = par.substring(2, par.length());
		String[] pp = par.split("=",2);
		if(pp[0].equals("save-options-to")) {
			Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass");
			FileOutputStream fs = null;
			try {
				if(pp[1].equals(""))
					fs = new FileOutputStream("stdout");
				else
					fs = new FileOutputStream(pp[1]);
			} catch(FileNotFoundException e) {
				System.out.println("Error during save options!");
			}
			try {
				prefs.exportSubtree(fs);
			} catch(BackingStoreException e) {
				System.out.println("Error during retrieve options from java storage!");
			} catch(IOException e) {
				System.out.println("Error during save options!");
			}
		} else if(pp[0].equals("load-options-from")) {
			FileInputStream fs = null;
			try {
				if(pp[1].equals(""))
					fs = new FileInputStream("stdin");
				else
					fs = new FileInputStream(pp[1]);
			} catch(FileNotFoundException e) {
				System.out.println("Error during loading options!");
			}
			try {
				Preferences.importPreferences(fs);
			} catch(IOException e) {
				System.out.println("Error during loading options!");
			} catch(InvalidPreferencesFormatException e) {
				System.out.println("Import file has incorrect format!");
			}
		} else if(pp[0].equals("pdf")) {
			doPdf = true;
		}
	}

	protected void processTemplate(String template){
		Map<String, String> outs;
		try {
			outs = engine.processFile(template);
				System.out.println("outs num:"+outs.size());
				if(outs.size()==1)
					writeFile("output", (String) outs.get("TOP"));
				else {
					Object sec_names[] = outs.keySet().toArray();
					for(int i=0; i<sec_names.length; i++) {
						if(sec_names[i].equals("DEF"))
							//Skip DEF section - don't need it saved
							//But m.b. there is a better place for this - perhaps in Engine?
							continue;
						String fileName=template.substring(0, template.lastIndexOf("."));
						writeFile(fileName+"-"+sec_names[i]+"S", outs.get(sec_names[i]));
					}
				}
		} catch (TemplateNotFoundException e) {
			System.out.println("Template '" + template +"' is not found!");
		} catch (TemplateParseException e) {
			System.out.println("Error parsing '" + template +"'template!\n" +
					e.getMessage());
		}
		engine.clearDataSources();
	}


	/**
	 * Writes a string to a file, this function is used by executeUI() to save
	 *
	 * @param       filename	output file name
	 * @param       text		string to write
	 */
	void writeFile(String filename, String text) {
		try {
			String output_path = engine.getPreference("output_path");
			if(output_path.length()==0)
				output_path = ".";
			File out_file = new File(output_path, filename+".tex");
			FileWriter file_writer = new FileWriter(out_file);
			BufferedWriter out = new BufferedWriter(file_writer);
			out.write(text);
			out.close();
			if(doPdf) {
				Process p = Runtime.getRuntime().exec(
						new String[]{engine.getPreference("latex"), out_file.getCanonicalPath()},
						null, new File(output_path));
				InputStream s = p.getInputStream();
				byte[] exec_out = new byte[1000];
				s.read(exec_out);
				System.out.println((new String(exec_out)).trim());
			}
		} catch (Exception e) {
			System.err.println("Error: "+e.getMessage());
            e.printStackTrace();
		}
	}


	/**
	 * Application entry point
	 *
	 * @param       s		command line parameters
	 */
	public static void main(String s[]) throws IOException
	{
		printLicense();
		AppModule app = new AppModule();
		app.executeUI(s);
		return;
	}

	private static void printLicense() {
		System.out.print(
			"\nCopyright (C) 2006 The University of Queensland\n" +
			"It is free software; you can redistribute it and/or modify it under the terms of the\n" +
			"GNU General Public License as published by the Free Software Foundation; either version 2, or\n" +
			"(at your option) any later version.\n" +
			"GNU program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;\n" +
			"without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.\n" +
			"See the GNU General Public License for more details.\n" +
			"You should have received a copy of the GNU General Public License along with program;\n" +
			"see the file COPYING. If not, write to the\n" +
			"Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA\n\n");
		}
}
