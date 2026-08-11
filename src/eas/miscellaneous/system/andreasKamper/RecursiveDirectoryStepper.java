/*
 * Datei:        RecursiveDirectoryStepper.java
 * Autor(en):    Andreas Kamper
 * Java-Version: 6.0
 * Erstellt:     10.03.2009
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
 * - to Share -- to copy, distribute and transmit the work
 * - to Remix -- to adapt the work
 * 
 * Under the following conditions:
 * - Attribution -- You must attribute the work in the manner specified by the 
 *   author or licensor (but not in any way that suggests that they endorse 
 *   you or your use of the work).
 * - Noncommercial -- You may not use this work for commercial purposes.
 * - Share Alike -- If you alter, transform, or build upon this work, you may 
 *   distribute the resulting work only under the same or a similar license to 
 *   this one. 
 * 
 * + Detailed license conditions (Germany):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/de/
 * + Detailed license conditions (unported):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en
 * 
 * This header must be placed in the beginning of any version of this file.
 */

package eas.miscellaneous.system.andreasKamper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Created on 13.10.2005 Die Klasse durchsteigt ein Verzeichnis rekursiv runter
 * und gibt die enthaltenen Objekte aus. Die Methoden doWithFile() und
 * doWithDirectory() können von unterklassen überschrieben werden um anderes
 * Verhalten zu erzeugen.
 *  
 */

public abstract class RecursiveDirectoryStepper {

	protected String sourceDirectory = "";

	public boolean goThroughDirectory(String dir) {

		File fileObject = new File(dir);
		if (!fileObject.exists()) return false;
		if (!fileObject.isDirectory()) { return doWithFile(fileObject); }
		if (!doWithDirectory(fileObject)) return false;
		String[] contents = fileObject.list();
		for (int i = 0; i < contents.length; i++) {
			goThroughDirectory(dir + "/" + contents[i]);

		}
		return true;
	}

	
	protected abstract boolean start();

	protected abstract boolean doWithFile(File fileObject);

	protected abstract boolean doWithDirectory(File fileObject);

	protected boolean copyDirectory(File newDirectory) {

		
		if (newDirectory.exists()) {
			if (!newDirectory.isDirectory()||!newDirectory.canWrite())
				return false;
			else
				return true;
		}
		newDirectory.mkdir();
		return true;
	}

	protected boolean copyFile(File fileObject,File destination) {

		try {
			InputStream fis = new FileInputStream(fileObject);
			OutputStream fos = new FileOutputStream(destination);
			byte buffer[] = new byte[0xffff];
			int nbytes;
			while ((nbytes = fis.read(buffer)) != -1)
				fos.write(buffer, 0, nbytes);
			fis.close();
			fos.close();
		}

		catch (IOException e) {
			System.err.println(e);
			return false;
		}
		return true;
	}
	
	
}