/* This file is part of SmartAss and contains the RepositoryStorage class that 
 * is used to store template-related files such as the template itself, graphics or example PDFs
 * in the repository. 
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
package au.edu.uq.smartassrepoeditor.repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter; 			// @TODO: Replace with java.lang.StringBuilder;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The RepositoryStorage class is used to store template-related files 
 * such as the template itself, graphics or example PDFs
 * in the repository.
 */
public class RepositoryStorage {

	/** Class logger. */
	private static final Logger LOG = LoggerFactory.getLogger( RepositoryStorage.class );

	private static final char NEWLINE = '\n';
	
	public static final int SCOPE_TEMPLATE = 0;
	public static final int SCOPE_PDF = 1;
	public static final int SCOPE_FILES = 2;
	
	public static final int OK = 0;
	public static final int ERROR_INVALID_SRC = 1;
	public static final int ERROR_INVALID_DST = 10;
	public static final int ERROR_INVALID_DST_DIR = 11;
	public static final int ERROR_FILE_COPY = 20;
	

	File[] path = new File[3];
	
	
	public RepositoryStorage() {
		Preferences prefs = Preferences.userRoot().node("au/edu/uq/smartass/repository");
		path[SCOPE_TEMPLATE] = new File(prefs.get("tex_path", "tex"));
		path[SCOPE_PDF] = new File(prefs.get("pdf_path", "pdf"));
		path[SCOPE_FILES] = new File (prefs.get("files_path", "files"));
	}

	public int importFile(int scope, String src, String dst, boolean create_dir) {
		File fsrc = new File(src);
		File fdst = new File(path[scope], dst);

		return processFile(scope, fsrc, fdst, create_dir, false);
	}
	

	public Vector<String> readMetadata(String src) {
		File fsrc = new File(src);
		Vector<String> meta = new Vector<String>();
		
		try {
        	BufferedReader in = new BufferedReader(new FileReader(src));
            StringWriter tmp = new StringWriter();
            
            String s;
            while((s=in.readLine())!=null && !(s.contains("%%META"))) 
            	tmp.write(s + "\n");
            while((s=in.readLine())!=null && !(s.contains("%%META END")))
            	meta.add(s); 
            while((s=in.readLine())!=null)
            	tmp.write(s + "\n");
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
		} catch (IOException e) {
        	e.printStackTrace();
		}
		
		return meta;
	}
	
	/**
	 * Imports a template file (any file).
	 * Copy a file from source location to destination. Removes line(s) containing <code>%%META</code>.
	 * 
	 * <p>Uses the platform's default 'charset'.
	 *
	 * @param 	scope 		.
	 * @param 	src 		.
	 * @param 	dst 		.
	 * @param 	create_dir 	UNUSED
	 * @return 			int?
	 * @deprecated 			Not being used?
	 */
	@Deprecated
	public int importTemplate(int scope, String src, String[] dst, boolean create_dir) {
		File fdst = new File(new File(path[scope], dst[0]), dst[1]);    			// path[]:File

		LOG.info("importTemplate()[ source=>{}, target =>{}]", src, fdst.getAbsolutePath());

		try {
			BufferedReader reader = new BufferedReader(new FileReader(src));
			StringBuilder builder = new StringBuilder();

			String line;
			while ( (line = reader.readLine()) != null && ! line.contains("%%META") ) 
					builder.append(line).append(NEWLINE);
			while ( (line = reader.readLine()) != null && ! line.contains("%%META END") );
		        while ( (line = reader.readLine()) != null )
					builder.append(line).append(NEWLINE);

			copyStream(
					new ByteArrayInputStream(builder.toString().getBytes()),  // use platform default 'charset'
					new FileOutputStream(fdst)
				);
			return OK;

		} catch (IOException e) {
			LOG.info("importTemplate() - {} => {}", e.getClass().getName(), e.getMessage());
			return ERROR_FILE_COPY;
		}
	}
	
	public int importFile(int scope, String src, String[] dst, boolean create_dir) {
		File fsrc = new File(src);
		File fdst = new File(new File(path[scope], dst[0]), dst[1]);

		return processFile(scope, fsrc, fdst, create_dir, false);
	}
	
	public int moveFile(int scope, String src, String dst, boolean create_dir) {
		return processFile(scope, new File(path[scope], src), new File(path[scope], dst), create_dir, true);
	}

	public int moveFile(int scope, String src[], String dst[], boolean create_dir) {
		return processFile(scope, 
				new File(new File(path[scope], src[0]), src[1]), 
				new File(new File(path[scope], dst[0]), dst[1]), 
				create_dir, true);
	}
	
	public boolean deleteFile(int scope, String file_path) {
		return (new File(path[scope], file_path)).delete();
	}

	public boolean deleteFile(int scope, String[] file_path) {
		return (new File(new File(path[scope], file_path[0]), file_path[1])).delete();
	}
	
	/**
	 *
	 * @param
	 * @param
	 * @param
	 * @param
	 * @return 		<code>true</code> always!
	 */
	public boolean exportFile(int scope, String[] file_path, String dst_path, String metadata) {
		try {
			InputStream meta_in = new ByteArrayInputStream(metadata.getBytes());
			InputStream in = new FileInputStream(
					new File(new File(path[SCOPE_TEMPLATE], file_path[0]), file_path[1])
				);
			OutputStream out = new FileOutputStream(dst_path);
			
			copyStream(meta_in, out);
			copyStream(in, out);

			meta_in.close();
	        	in.close();
	        	out.close();

	        } catch(IOException e) {
			LOG.info("exportFile(): Exception caught [{}]: => {}", e.getClass().getName(), e.getMessage());

		} finally {
			return true;
		}
	}

	private int processFile(int scope, File fsrc, File fdst, boolean create_dir, boolean delete_src) {
		if(!isSrcFileCorrect(fsrc))
			return ERROR_INVALID_SRC;
		
		if(!fdst.getParentFile().exists())
			if(create_dir)
				createDirectory(fdst.getParentFile());
			else
				return ERROR_INVALID_DST_DIR;
		
		if(!copyFile(fsrc, fdst, delete_src))
			return ERROR_FILE_COPY;
		return OK;
	}
	
	public boolean checkFileExists(int scope, String filename) {
		File f = new File(path[scope], filename);
		return f.exists();
	}
	

	/////////////////////
	//Service functions
	////////////////////
	
	private boolean copyFile(File src, File dst, boolean delete_src) {
		try {
			dst.delete();
	        InputStream in = new FileInputStream(src);
	        OutputStream out = new FileOutputStream(dst);
	        
	        copyStream(in, out);
	        try {
	        	in.close();
	        	out.close();
	        } catch(IOException e) {
	        	e.printStackTrace();
	        }
	        if(delete_src)
	        	src.delete();
	        return true;
	    
		} catch (FileNotFoundException e) {
        	e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Copy an input stream to an output stream.
	 *
	 * @param 	in 	source input stream.
	 * @param 	out 	target output stream.
	 */
	private void copyStream(InputStream in, OutputStream out) {
		byte[] buf = new byte[1024];
		int len;
		try {
			while ((len = in.read(buf)) > 0) { out.write(buf, 0, len); }
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isSrcFileCorrect(File file) {
		return file!=null && file.exists() && !file.isDirectory();
	}
	
	protected void createDirectory(File dir) {
		if(dir.getParentFile()!=null && !dir.getParentFile().exists())
			createDirectory(dir.getParentFile());
		dir.mkdir();
	}
}
