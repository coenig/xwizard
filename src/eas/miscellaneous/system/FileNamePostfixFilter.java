/*
 * Datei:          DateiFilter.java
 * Autor(en):      Lukas König
 * Java-Version:   1.4
 * Erstellt (vor): 27.04.2007
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

package eas.miscellaneous.system;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

/**
 * Einfache Filterklasse.
 *
 * @author Lukas König
 */
public class FileNamePostfixFilter extends FileFilter 
                         implements FilenameFilter, Serializable {

    private static final long serialVersionUID = -4258261394889297489L;

    /**
     * Die Endung des Dateinamens, die akzeptiert wird.
     */
    private String endung;

    /**
     * Ob Verzeichnisse zugelassen sind.
     */
    private boolean verzZulassen;
    
    /**
     * Die Endung, die der Dateiname haben soll.
     *
     * @param end  Die Endung (nach dem Punkt).
     */
    public FileNamePostfixFilter(final String end) {
        this.endung = end;
        this.verzZulassen = false;
    }
    
    /**
     * Entscheidet, ob eine Datei akzeptiert wird.
     *
     * @param arg0  Das File-Argument.
     * @param arg1  Das String-Argument.
     *
     * @return  <code>true</code>, gdw. die Datei akzeptiert werden soll.
     */
    @Override
    public boolean accept(final File arg0,
                          final String arg1) {
        if (this.verzZulassen && arg0.isDirectory()) {
            return true;
        }
        
        if (arg1.length() < this.endung.length()) {
            return false;
        }

        String test = arg1.substring(arg1.length()
                                         - this.endung.length(),
                                     arg1.length());

        return test.equals(this.endung);
    }

    /**
     * @param arg0  Datei.
     * 
     * @return  Ob die Datei akzeptiert wird.
     */
    @Override
    public boolean accept(final File arg0) {
        return this.accept(arg0, arg0.getName());
    }

    /**
     * @return Die Beschreibung.
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * @param wert  Ob Verzeichnisse zugelassen sind.
     */
    public void setVerzZulassen(final boolean wert) {
        this.verzZulassen = wert;
    }
}
