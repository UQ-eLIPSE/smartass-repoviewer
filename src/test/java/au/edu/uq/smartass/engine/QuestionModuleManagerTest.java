package au.edu.uq.smartassrepoeditor.engine;


import java.util.prefs.Preferences;

import static org.junit.Assert.*;

import org.junit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
  *
  */
public class QuestionModuleManagerTest {

	/** Class logger. */
	private static final Logger LOG = LoggerFactory.getLogger( QuestionModuleManagerTest.class );

        /** */
        QuestionModuleManager moduleManager = new QuestionModuleManager();

        /**
          *
          */
	@BeforeClass public static void initialize() {
                Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass");
                prefs.put("plugin_path", "/Users/uqpwalle/proj/smartass/smartass-plugin/build/libs/");

		LOG.info( "::initialize() [ {} ] ", prefs.toString() );

                try {
                        for (String key : prefs.keys()) {
                                LOG.info( "::initialize() [ Preferences: {} => {} ] ", key, prefs.get(key, "...") );
                        }
                } catch (Exception ex) {
                        fail("Caused Exception!");
                }
        }

        /**
          *
          */
        @Test public void initialTest() {
                assertNotNull(moduleManager);
        }
        
        /**
          *
          */
        @Ignore
        @Test public void retrieveSimpleQuestionModuleTest() {
                // Missing Question Module
                assertEquals( QuestionModuleManager.MISSING_QUESTION_MODULE, moduleManager.retrieveSimpleQuestionModule("NoneModule") );

                // TestQuestion
// TODO: How to package and deploy TestQuesion plugin?
//                assertEquals( "SECTION: Simple Lookup", moduleManager.retrieveSimpleQuestionModule("TestQuestion").getSection("Simple Lookup"));
//                assertEquals( "SECTION: Canonical Lookup", moduleManager.retrieveCanonicalQuestionModule("au.edu.uq.smartass.question.TestQuestion").getSection("Canonical Lookup"));

        }

        /**
          *
          */
        @Test public void retrieveQuestionModule2Test() {
                assertTrue(true);
        }



}
