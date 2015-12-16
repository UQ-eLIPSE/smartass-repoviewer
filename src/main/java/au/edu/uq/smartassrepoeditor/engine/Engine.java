/* This file is part of SmartAss and describes class Engine - the core of SmartAss.
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
package au.edu.uq.smartassrepoeditor.engine;

import au.edu.uq.smartassrepoeditor.script.DataSource;
import au.edu.uq.smartassrepoeditor.templates.TemplateNotFoundException;
import au.edu.uq.smartassrepoeditor.templates.TemplateParseException;
import au.edu.uq.smartassrepoeditor.templates.TemplateReader;
import au.edu.uq.smartassrepoeditor.templates.TexReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Engine {

    /** Class logger. */
    private static final Logger LOG;


    /** */
    private static final Preferences PREFERENCES;


    /**
     * Manager for QuestionModule services.
     */
    private static final QuestionModuleManager QUESTIONMODULEMANAGER;


    /** Singleton Instance. */
    private static final Engine ENGINE;


    static {
        LOG = LoggerFactory.getLogger( Engine.class );
        PREFERENCES = Preferences.userRoot().node( "au/edu/uq/smartass" );
        QUESTIONMODULEMANAGER = new QuestionModuleManager();
        ENGINE = new Engine();
    }


    /** */
    private HashMap<String, DataSource> datasources = new HashMap<>();


    /**
     * Singleton method to retieve only Engine instance.
     * @return
     */
    public static Engine getInstance() { return ENGINE; }


	/**
	 * Default Constructor
	 */
	private Engine() {
		LOG.info( "::Engine() initialisation:");

		LOG.info( "::Engine()[ preferences=>{}] ", PREFERENCES.toString() );
        try {
            for (String key : PREFERENCES.keys())
                    LOG.info( "::Engine()[ Preference: {} => {} ]", key, PREFERENCES.get(key, "...") );
        } catch (BackingStoreException e) {
                LOG.info( "::Engine()[ Preference: ERROR => {} ]", e.getMessage() );
        }

        LOG.info( "::Engine()[ System.properties => {}] ", "..." );
		for (Map.Entry<Object,Object> entry : System.getProperties().entrySet())
				LOG.debug( "::Engine()[ system property : {} => {} ]", entry.getKey(), entry.getValue() );
	}

    /**
     * Lookup Preference setting using empty default value.
     *
     * @param key Preference lookup key.
     * @return
     */
	public String getPreference(String key) { return getPreference(key, ""); }

    /**
     * Lookup Preference setting using empty default value.
     *
     * @param key Preference lookup key.
     * @param def Default value to use if no key/value pair.
     * @return
     */
	public String getPreference(String key, String def) { return PREFERENCES.get(key, def); }


	/**
	 * This metod creates and returns specific template module by type
	 * @param type
	 */
	public TemplateReader getTemplateReader(String type)
	{
		return new TexReader(this);
		//At this moment we just create PlainTextReader, whatever value "type" parameter contains
	}

	/**
     * Lookup QuestionModule by name. The QuestionModule should be a preregistered service within the QuestionModuleManager.
	 *
     * @param module_name
     * @return
	 */
	public QuestionModule getQuestionModule(String module_name) {
        return getQuestionModule(module_name, new String[0] );
    }

	/**
	 * Lookup QuestionModule by name. The QuestionModule should be a preregistered service within the QuestionModuleManager.
	 *
	 * @param module_name lookup key.
	 * @param params array of string value initialization parameters.
	 * @return 	QuestionModule 	A <code>QuestionModule</code> identifed by module_name.
	 */
	public QuestionModule getQuestionModule(final String module_name, final String[] params) {
        LOG.info( "::getQuestionModule( {}, {} )", module_name, Arrays.toString(params) );
        return QUESTIONMODULEMANAGER.retrieveSimpleQuestionModule(module_name).initialise(params);
	}


	/** This method takes the template type and body (not the template file name) as its arguments
	  * This is useful to isolate template processing from file system specifics and so on.
      *
      * @param template
      * @param type
	  * @return the result of template processing
      * @throws au.edu.uq.smartass.templates.TemplateParseException
	  */
        public Map<String, String> processTemplate(InputStream template, String type) throws TemplateParseException {
		TemplateReader tr = getTemplateReader(type);

		//----------- it seems that we need no this in engine... ------------
		//Because QuestionModule descendants can change TeX representation of Ops
		//we set them to default before load and execute template
		//to ensure that some
		//MathsOp.clearAllTex();
		tr.loadTemplate(template);
		return tr.execute();
	}

	public Map<String, String> processFile(String file_name) throws TemplateNotFoundException, TemplateParseException
	{
		InputStream stream = getTemplateStream(file_name);
		if(stream!=null)
			return processTemplate(stream, file_name.substring(file_name.length()-3,file_name.length()));
		return null;
	}

	public InputStream getTemplateStream(String name) throws TemplateNotFoundException {

        String[] templateRoots = PREFERENCES.get("templates_root", ".").split(";");
		LOG.info( "::getTemplateStream()[ name=>{}, templateRoots=>{} ] ", name, Arrays.toString(templateRoots) );

                File template;
		try {
                        for (String templateRoot : templateRoots) {
                                template = new File(templateRoot, name);
                                if (template.exists()) return new FileInputStream(template);
                        }

			//not found, check for legacy mode
			template = new File(name);
			if (name.equals(template.getName())) //yes, we have filename without path
				 // look for file in subdirectories of each templateRoots directory
				for(int i=0;i<templateRoots.length;i++) {
					File d = new File(templateRoots[i]);
					if(d.exists()) {
						InputStream stream = getTemplateStream(d, name);
						if(stream!=null)
							return stream;
					}
				}
		} catch(FileNotFoundException e) {
			throw new TemplateNotFoundException(name);
		}
		throw new TemplateNotFoundException(name);
	}

	private InputStream getTemplateStream(File dir, String name) {
		try {
			File f;
			File list[] = dir.listFiles();
			if(list==null) return null;

			for(int i=list.length; --i>=0;) {
				f = new File(list[i], name);
				if(f.exists()) return new FileInputStream(f);
				InputStream stream = getTemplateStream(list[i], name);
				if(stream!=null) return stream;
            }
		} catch (FileNotFoundException e) {
            LOG.info( "::getTemplateStream()[ {} ]", e.getMessage() );
        }
		return null;
	}

	public void addDataSource(String name, DataSource ds) {
		DataSource oldds = datasources.get(name);
		if (oldds != null) oldds.close();
		datasources.put(name, ds);
	}

	public DataSource getDataSource(String name) {
		return datasources.get(name);
	}

	public void close() {
		clearDataSources();
	}

	public void clearDataSources() {
		for (DataSource ds : datasources.values()) ds.close();
		datasources.clear();
	}
}
