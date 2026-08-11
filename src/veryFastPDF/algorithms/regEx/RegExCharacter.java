/*
 * File name:        RegExpCharacter.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  11.05.2013 (09:48:05)
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
public class RegExCharacter extends AbstractRegEx {

    private String character;
    
    public RegExCharacter(String character) {
        this.character = character;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.character == null) ? 0 : this.character.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegExCharacter other = (RegExCharacter) obj;
        if (this.character == null) {
            if (other.character != null)
                return false;
        } else if (!this.character.equals(other.character))
            return false;
        return true;
    }

    @Override
    public String toString(boolean latex) {
        return character;
    }

    @Override
    public AbstractRegEx copy() {
        return new RegExCharacter(this.character);
    }

    @Override
    public boolean isEmpty() {
        return false;
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
        words.add(this.character);
        AbstractRegEx.addAllWords(words, currentWords, maxWords);
    }
}
