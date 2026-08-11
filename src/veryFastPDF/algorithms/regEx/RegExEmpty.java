/*
 * File name:        RegExpEmpty.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  11.05.2013 (09:46:29)
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

package veryFastPDF.algorithms.regEx;

import java.util.HashSet;

/**
 * @author lko
 */
public class RegExEmpty extends AbstractRegEx {

    @Override
    public String toString(boolean latex) {
        if (latex) {
            return "\\emptyset";
        } else {
            return "O";
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        return RegExEmpty.class.equals(obj.getClass());
    }

    @Override
    public AbstractRegEx copy() {
        return new RegExEmpty();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @Override
    public boolean isEmptyWord() {
        return false;
    }
    
    @Override
    public void simplify() {
        
    }

    @Override
    public void deriveWords(int kleeneIterations, int maxWords, HashSet<String> currentWords) {
        HashSet<String> words = new HashSet<>();
        AbstractRegEx.addAllWords(words, currentWords, maxWords);
    }
}
