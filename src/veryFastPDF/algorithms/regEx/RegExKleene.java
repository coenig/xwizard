/*
 * File name:        RegExpKleene.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  11.05.2013 (09:52:20)
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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author lko
 */
public class RegExKleene extends AbstractRegEx {

    private AbstractRegEx exp;
    
    public RegExKleene(AbstractRegEx expression) {
        this.exp = expression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.exp == null) ? 0 : this.exp.hashCode());
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
        RegExKleene other = (RegExKleene) obj;
        if (this.exp == null) {
            if (other.exp != null)
                return false;
        } else if (!this.exp.equals(other.exp))
            return false;
        return true;
    }

    public AbstractRegEx getExp() {
        return this.exp;
    }
    
    @Override
    public String toString(boolean latex) {
        String star = "*";
        if (latex) {
            star = "^\\star";
        }
        
        if (RegExCharacter.class.isAssignableFrom(exp.getClass())
                || RegExEmpty.class.isAssignableFrom(exp.getClass())
                || RegExLambda.class.isAssignableFrom(exp.getClass())) {
            return exp.toString(latex) + star;
        }
        return "(" + exp.toString(latex) + ")" + star;
    }

    @Override
    public AbstractRegEx copy() {
        return new RegExKleene(this.exp.copy());
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isEmptyWord() {
        return this.exp.isEmpty() || this.exp.isEmptyWord();
    }
    
    @Override
    public void simplify() {
        // (A + O*)* oder (O* + A) => A*
    }

    @Override
    public void deriveWords(int kleeneIterations, int maxWords, HashSet<String> currentWords) {
        HashSet<String> wordsSet = new HashSet<>();
        this.exp.deriveWords(kleeneIterations - 1, maxWords, wordsSet);
        
        LinkedList<String> words = new LinkedList<>(wordsSet);
        Collections.sort(words, new WordSorting());
        
        HashSet<String> originalWords = new HashSet<>(words);
        words.add("");
        
        for (int i = 0; i <= kleeneIterations; i++) {
            HashSet<String> newWords = new HashSet<>();

            for (String word1 : words) {
                for (String word2 : originalWords) {
                    newWords.add(word1 + word2);
                }
            }
            
            words.addAll(newWords);
        }
        
        AbstractRegEx.addAllWords(words, currentWords, maxWords);
    }
}
