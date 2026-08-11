/*
 * File name:        RegExp.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  11.05.2013 (09:43:53)
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

import java.util.Collection;
import java.util.HashSet;

/**
 * @author lko
 */
public abstract class AbstractRegEx {
    public abstract String toString(boolean latex);
    @Override public abstract int hashCode();
    @Override public abstract boolean equals(Object obj);
    public abstract AbstractRegEx copy();
    public abstract boolean isEmpty(); // Only for empty set.
    public abstract boolean isEmptyWord(); // For empty word lambda.
    public abstract void simplify();
    
    public boolean isEmptyOrEmptyWord() {
        return this.isEmpty() || this.isEmptyWord();
    }
    
    public abstract void deriveWords(
            int kleeneIterations, 
            int maxWords, 
            HashSet<String> currentWords);
    
    public static void addAllWords(Collection<String> words, Collection<String> toWords, int maxNum) {
        for (String w : words) {
            if (toWords.size() >= maxNum) {
                return;
            }
            
            toWords.add(w);
        }
    }
}
