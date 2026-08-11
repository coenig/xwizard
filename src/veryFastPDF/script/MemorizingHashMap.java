/*
 * File name:        MemorizingHashMap.java (package mainServlet)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  30.08.2015 (12:05:34)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

package veryFastPDF.script;

import java.util.HashMap;

/**
 * Same as HashMap except it stores the last value put into it to return in all
 * cases where a non-existing key is requested.
 * 
 * @author Lukas König
 */
public class MemorizingHashMap<ValueType> extends HashMap<String, ValueType> {

    private static final long serialVersionUID = 174918756747619345L;

    private ValueType backupValue;
    
    /**
     * Standard constructor.
     * 
     * @param backupValue  The first backup value. It is replaced with the
     *                     next value at all calls of the put method.
     */
    public MemorizingHashMap(ValueType backupValue) {
        this.backupValue = backupValue;
    }
    
    @Override
    public ValueType put(String key, ValueType value) {
        ValueType returnValue = super.put(key, value);
        this.backupValue = value;
        return returnValue;
    }
    
    @Override
    public ValueType get(Object key) {
        ValueType script = super.get(key);
        
        if (script == null) {
            script = this.backupValue;
        }
        
        return script;
    }
    
    @Override
    public String toString() {
        return super.toString() + " -> " + this.backupValue;
    }
}
