package au.edu.uq.smartassrepoeditor.engine;


import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
  * Class QuestionModuleManager.
  */
public class QuestionModuleManager {

	/** Class logger. */
	private static final Logger LOG = LoggerFactory.getLogger( QuestionModuleManager.class );

    /** Class Preferences. */
    private static final Preferences preferences = Preferences.userRoot().node("au/edu/uq/smartass");

    /**
      *
      */
    private final File MODULE_PATH;
    {
            String pluginPath = preferences.get("plugin_path", ".");
            MODULE_PATH = new File(pluginPath);
            if (!MODULE_PATH.isDirectory()) MODULE_PATH.mkdirs();
    }

    /**
      * Default Question Module Instance
      */
    public static final QuestionModule MISSING_QUESTION_MODULE
            = (final String section) -> "ERROR: Missing Question Module!";

    /**
      * Map Canonical class name to Question Module Object loaded as a Service.
      */
    Map<String,QuestionModule> questionModules = new HashMap<>();

    /**
      * Simple name lookup of Question Module Canonical Class Name.
      */
    Map<String,String> questionModuleSimpleLookup = new HashMap<>();


    /**
      *
      * @param key
      * @return
      */
    public QuestionModule retrieveSimpleQuestionModule(final String key) {
            LOG.info( "::retrieveSimpleQuestionModule( {} )", key );
            if (!questionModuleSimpleLookup.containsKey(key)) scanModuleDirectory();

            return retrieveQuestionModule(questionModuleSimpleLookup.get(key));
    }
    /**
      *
     * @param key
     * @return
      */
    public QuestionModule retrieveCanonicalQuestionModule(final String key) {
            LOG.info( "::retrieveCanonicalQuestionModule( {} )", key);
            if (!questionModules.containsKey(key)) scanModuleDirectory();

            return retrieveQuestionModule(key);
    }


    /**
      *
      */
    private QuestionModule retrieveQuestionModule(final String key) {
        QuestionModule questionModule = questionModules.get(key);

        if (null == questionModule) {
            LOG.info( "::retrieveQuestionModule( {} )[ QuestionModule NOT found!", key );
            return MISSING_QUESTION_MODULE;
        }

        try {
            Constructor<? extends QuestionModule> constructor = null;
            constructor = questionModule.getClass().getDeclaredConstructor(null);
            return constructor.newInstance(null);
        } catch (NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException ex) {
            LOG.info(
                    "::retrieveQuestionModule( {} )[ No Default Constructor for QuestionModule! ]\n-----> {}",
                    key, ex.getMessage() );
            return MISSING_QUESTION_MODULE;
        }

    }

    /**
      *
      */
    private void scanModuleDirectory() {
        LOG.info( "::scanModuleDirectory( )" );
        Set<URL> pluginURLs = new HashSet<>();
        for (File lib : MODULE_PATH.listFiles(
                (File pathname) ->
                        pathname.isFile() &&
                        pathname.getName().toLowerCase().endsWith(".jar")
        )) {
            try {
                URL pluginURL = lib.toURI().toURL();
                pluginURLs.add( pluginURL );
                LOG.info( "::scanModuleDirectory()[ addURL: {} ]", pluginURL );
            } catch (MalformedURLException ex) {
                // should never be thrown since URL is formed from valid file.
                LOG.error( "::scanModuleDirectory() - {}", ex.getMessage() );
            }
        }

        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(
                    pluginURLs.toArray(new URL[0]),
                    this.getClass().getClassLoader()
                );

            ServiceLoader<QuestionModule> questionModuleLoader
                    = ServiceLoader.load(QuestionModule.class, classLoader);
            LOG.info("::scanModuleDirectory()[ {} ]", questionModuleLoader.toString());
            for (QuestionModule qm : questionModuleLoader) {
                String simple = qm.getClass().getSimpleName();
                String canonical = qm.getClass().getCanonicalName();
                LOG.info("::scanModuleDirectory()[ {} => {} ]", simple, canonical);
                questionModules.put(canonical, qm);
                questionModuleSimpleLookup.put(simple, canonical);
            }
        } catch (Exception ex) {
            LOG.error( "::scanModuleDirectory()[ ERROR => {} ]", ex.getMessage() );
        }
    }

}
