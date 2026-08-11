/*
 * Datei: AutomatenFilter.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         03.12.2009
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

package eas.startSetup;

import java.io.File;
import java.io.FilenameFilter;

import eas.miscellaneous.system.FileNamePostfixFilter;

/**
 * @author Lukas König
 */
public class AutomatenFilter implements FilenameFilter {

    /**
     * @param arg0  Die Datei.
     * @param arg1  Ein Text.
     * 
     * @return  Ob die Datei akzeptiert wird.
     */
    @Override
    public boolean accept(final File arg0, final String arg1) {
        return new FileNamePostfixFilter("gra").accept(arg0, arg1)
            || new FileNamePostfixFilter("tra").accept(arg0, arg1)
            || new FileNamePostfixFilter("con").accept(arg0, arg1);
    }
}
