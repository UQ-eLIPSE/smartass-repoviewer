/* This file is part of SmartAss and based on original file from Tim Hoffmann
 * (see copiright notice below)
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

//$ This file is part of the jDvi distribution.
//$ it is provided under the Gnu Public license (GPL).
//$ You should have received a copy of the GPL together
//$ with this file (see file GPL in folder doc).
//$ If not, write to the
//$ Free Software Foundation,
//$ 59 Temple Place - Suite 330,
//$ Boston, MA 02111-1307, USA.
//$
//$ jDvi is distributed in the hope that it will be useful,
//$ but WITHOUT ANY WARRANTY; without even the implied
//$ warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
//$ PURPOSE.
//$ See the GNU General Public License for more details.
//$
//$
//$ Feel free to modify and redistribute jDvi provided this
//$ copyright notice stays...
//$ (c) 2000 Tim Hoffmann


package au.edu.uq.smartassrepoeditor.app;

import jdvi.*;
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

/**
 * The main class <code>JDvi</code> of the jDvi TeX dvi file viewer.
 *
 * This is free software (see the <a href="GPL">GPL</a>).
 * @author <a href="mailto:timh@sfb288.math.tu-berlin.de">Tim Hoffmann</a>
 * @version $Revision: 1.2 $
 */
public class DviView extends Panel implements JDviContext {

    JDviViewerPanel dvi;
    URL scribbleURL;
    Properties properties;
    FileDialog dialog;
    long modificationTime = 0 ;
    File dviFile;

  public DviView() throws java.io.FileNotFoundException, java.io.IOException {
	//properties = new Properties(System.getProperties());
    properties = new Properties();
	//
	// load the properties either from a file given by the SystemProperty
	// "jdvi.propertiesFile" or from jdviProp in the user's home.
	//
	File propFile = new
	    File(System.getProperty("jdvi.propertiesFile",
				    System.getProperty("user.home")+
				    System.getProperty("file.separator")+
				    "jdviProp"));
	if(propFile.exists()) {
	    System.out.println("loading properties from "+propFile);
	    InputStream stream = new FileInputStream(propFile);
	    properties.load(stream);
	    stream.close();
	}

	setBackground(Color.white);

	setLayout(new BorderLayout());

	dvi = new JDviViewerPanel(this);
	add("Center", dvi);
    }

    //
    // JDviContext stuff
    //

    public Image getImage(URL url) {
	Image img=null;
	try {
	    URLConnection connect = url.openConnection();
	    Object oo = connect.getContent();
	    if(oo instanceof ImageProducer) {
		System.out.println("image producer "+oo);
		img = createImage((ImageProducer) oo);
	    }
	    else if(oo instanceof Image) {
		img= (Image) oo;
	    }
	} catch (java.net.MalformedURLException ex ) {
	    img = null;
	    System.err.println("Image loading: "+ex);
	}
	catch (java.io.IOException ex2 ) {
	    img = null;
	    System.err.println("Image loading: "+ex2);
	}
	finally {
	    //System.out.println("image done in context");
	    return img;
	}
    }

    public AudioClip getAudioClip(URL url) {
	return null;
    }
    public  void  showDocument(URL url) {
	String cmd[] = new String[2];
	cmd[0] = System.getProperty("jdvi.browser",getProperties().getProperty("jdvi.browser","netscape"));
	cmd[1] = url.toExternalForm();
	try {
	    Runtime.getRuntime().exec(cmd);
	} catch (java.io.IOException e) {System.out.println(e);
	}
	//System.out.println("Extenal urls are not implemented.");
    }

    public  void  showDocument(URL url,String s) {
	try {
	    showDocument(new URL(url,s));
	} catch (java.net.MalformedURLException e) {
	}
    }

    public URL getCodeBase() {
	try {
	return new URL("file:"+System.getProperty("user.dir")+System.getProperty("file.separator"));
	} catch (java.net.MalformedURLException e) {
	    return null;
	}
    }

    public URL getDocumentBase() {
	return getCodeBase();
    }

    public  void  inform(String s) {
	System.out.println("Inform: "+s);
    }
    /**
     * This method is no longer part of the JDviContext interface.
     */
    public Properties getProperties() {
	return properties;
    }

    public String getProperty(String name) {
	    return System.getProperty(name,properties.getProperty(name));
    }

    public String getProperty(String name, String defRes) {
	    return System.getProperty(name,properties.getProperty(name,defRes));
    }

    /**
     * If the property "jdvi.scribbleFile.save" is non null,
     * writeScribbleFile() opens a file dialog to ask where to save
     * the scribble file.
     */
    public void writeScribbleFile() {
	if(getProperty("jdvi.scribbleFile.save") == null) return;
	String file = null;
	if(scribbleURL.getProtocol().equals("file")) {
	    file =  scribbleURL.getFile();
	//dialog.setDirectory(file);
	dialog.setFile(file);
	}
	dialog.setVisible(true); 		// deprecated: show();
	if(dialog.getFile()!= null) {
	    file = dialog.getDirectory()+dialog.getFile();
	    System.out.println("Choosen:"+dialog.getDirectory()+dialog.getFile());
	    dialog.dispose();
	    try {
		FileOutputStream ostream = new FileOutputStream(file);
		//System.out.println(scribbleURL.toString());
		//URLConnection c = scribbleURL.openConnection();
		//c.setDoOutput(true);

		//OutputStream ostream = c.getOutputStream();
		ObjectOutputStream q = new ObjectOutputStream(ostream);

		System.out.print("Writing scribble file "+file);
		for(int i = 0; i<dvi.getNumberOfPages();i++) {
		    q.writeObject(dvi.page.scribble[i]);
		    System.out.print(".");
		}
		System.out.println(" ");
		q.flush();
		ostream.close();
	    } catch(java.io.FileNotFoundException e) {System.err.println(e);}
	    catch(java.io.IOException e2) {System.err.println(e2);}
	} else { // file = null
	    System.out.println("Nothing saved!");
	}
    }
    /**
     * if the property "jdvi.font.cache" is non null, writeFontCache()
     * writes a serialized zipped version of the font array of the JDviPanel
     * dvi.page.document.font into a file with name equal to the value
     * of above property. This can be read by JDvi ot the JDviApplet
     * to speed up font loading.
     */
    public void writeFontCache() {
	String fontCache = getProperty("jdvi.font.cache");
	if(fontCache!= null)
	    try {
		FileOutputStream ostream = new FileOutputStream(fontCache);
		DeflaterOutputStream dstream =
		    new DeflaterOutputStream(ostream);
		ObjectOutputStream q = new ObjectOutputStream(dstream);
		System.out.print("Writing font cache file "+fontCache);
		q.writeObject(dvi.page.document.font);
		q.flush();
		dstream.close();
	    } catch(java.io.FileNotFoundException e) {System.err.println(e);}
	    catch(java.io.IOException e2) {System.err.println(e2);}
    }
    /**
     * 	Read a scribble file written by writeScribbleFile()
     */
    public void readScribbleFile() {
	try {
	    InputStream istream = scribbleURL.openStream();
	    ObjectInputStream q = new ObjectInputStream(istream);

	    System.out.println("Reading scribble file "+scribbleURL);
	    for(int i = 0; i<dvi.getNumberOfPages();i++) {
		//for(int i = dvi.getNumberOfPages()-1; i>=0; i--) {
		dvi.page.scribble[i]  = (JDviScribble)q.readObject();
	    }
	    istream.close();
	} catch(java.io.FileNotFoundException e) {System.err.println(e);}
	catch(java.io.IOException e2) {System.err.println(e2);}
	catch(java.lang.ClassNotFoundException e3) {System.err.println(e3);}
    }
}
