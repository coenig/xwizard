/*
 * Datei:        StepperSimpleLukasZIP.java
 * Autor(en):    Lukas König
 * Java-Version: 6.0
 * Erstellt:     14.09.2010
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
import java.util.LinkedList;

/**
 * @author Lukas König
 *
 */
public class StepperSimpleLukasZIP extends RecursiveDirectoryStepper {
    public LinkedList<String> list = new LinkedList<String>();
    
    @Override
    protected boolean start() {
        return false;
    }
    
    @Override
    protected boolean doWithFile(File fileObject) {
        if (fileObject.getName().endsWith(".java") 
                || fileObject.getName().endsWith(".class")
                || fileObject.getName().endsWith(".properties")) {
            list.add(fileObject.getPath());
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    protected boolean doWithDirectory(File fileObject) {
        return true;
    }
    
    public LinkedList<String> getList() {
        return this.list;
    }
}
