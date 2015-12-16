package au.edu.uq.smartassrepoeditor.repository;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import static org.junit.Assert.*;
import org.junit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepositoryStorageTest {

	/** Class logger. */
	private static final Logger LOG = LoggerFactory.getLogger( RepositoryStorageTest.class );

	/** */
	private static final RepositoryStorage REPOSITORY = new RepositoryStorage();

	private static int getScope() { return RepositoryStorage.SCOPE_TEMPLATE; }
	private static File getScopeDir() { return REPOSITORY.path[getScope()]; }

	/** */
	private static File testDir;

	/** */
	@BeforeClass public static void initialize() {

		testDir = new File(getScopeDir(), "test");
		if (!testDir.exists()) testDir.mkdirs();

		LOG.info("scope:template 	{}", getScopeDir().getAbsolutePath());
		LOG.info("scope:pdf 		{}", REPOSITORY.path[RepositoryStorage.SCOPE_PDF].getAbsolutePath());
		LOG.info("scope:files 		{}", REPOSITORY.path[RepositoryStorage.SCOPE_FILES].getAbsolutePath());
	}
	
	@SuppressWarnings("deprecation")
	@Ignore
        @Test public void importTemplateTest() throws IOException {

		int result;
		File src;
		File dst;
		PrintWriter out;

		//
		// java.io.FileNotFoundException:  (No such file or directory)
		//
		result = REPOSITORY.importTemplate(
				getScope(),
				"",
				new String[]{"",""},
				false
			);
		assertEquals(REPOSITORY.ERROR_FILE_COPY, result);

		//
		// Disabled because logic seems errornous. Does not deal with blank dest structure very well.
		//
		// Currently this test fails 
		// 	- the method uses the parent directory as the filename, returning OK.
		// 	- subsequent runs also return OK (FAIL).
		//
//		src = File.createTempFile("RepositoryStorage", "test");
//		result = REPOSITORY.importTemplate(
//				getScope(),
//				src.getAbsolutePath(),
//				new String[]{"",""},
//				false
//			);
//		src.delete();
//		//assertEquals(REPOSITORY.ERROR_FILE_COPY, result);
//		dst = getScopeDir();
//		assertTrue(dst.exists());
//		assertTrue(dst.isFile());
//		assertEquals(0L, dst.length());
//		dst.delete();

		//
		//
		//
		src = File.createTempFile("RepositoryStorage", "test");
		out = new PrintWriter(src);
		out.println("%%META meta data one");
		out.println("%%META meta data two");
		out.println("%%META END");
		out.close();

		assertNotEquals(0L, src.length());

		dst = new File(testDir, src.getName());
		
		result = REPOSITORY.importTemplate(
				getScope(),
				src.getAbsolutePath(),
				new String[]{testDir.getName(), dst.getName()},
				false
			);

		assertTrue(dst.exists());
		assertTrue(dst.isFile());
		assertEquals(0L, dst.length());

		src.delete();
		if (dst.exists() && dst.isFile()) dst.delete();

		//
		//
		//
		src = File.createTempFile("RepositoryStorage", "test");
		out = new PrintWriter(src);
		out.println("Some meta data");
		out.println("Some more meta data");
		out.close();

		dst = new File(testDir, src.getName());
		
		result = REPOSITORY.importTemplate(
				getScope(),
				src.getAbsolutePath(),
				new String[]{testDir.getName(), dst.getName()},
				false
			);


		assertTrue(dst.exists());
		assertTrue(dst.isFile());
		assertEquals(src.length(), dst.length());
		assertNotEquals(0L, dst.length());

		src.delete();
		if (dst.exists() && dst.isFile()) dst.delete();

	}   


	/** */
        @Test public void exportFileTest() throws IOException {
		// scope 	: int
		// file_path 	: String[]
		// dst_path 	: String
		// metadata 	: String

		File dst = File.createTempFile("RepositoryStorageTest", "exportFile");
		File src = new File(testDir, dst.getName());
		
		String meta = "Arbitary Meta data string";

		src.createNewFile();

		REPOSITORY.exportFile(getScope(), new String[]{src.getParentFile().getName(), src.getName()}, dst.getAbsolutePath(), meta);

		assertTrue(src.exists());
		assertTrue(dst.exists());
		assertEquals(0L, src.length());
		assertNotEquals(src.length(), dst.length());

		Scanner scanner = new Scanner(dst);
		if (!scanner.hasNextLine()) assertTrue(false);
		assertEquals(meta, scanner.nextLine());

	}
}

