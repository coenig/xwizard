/*
 * File name:        RegExpAddition.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  11.05.2013 (09:58:22)
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
public class RegExUnion extends Abstract2ValuedRegEx {

    public RegExUnion(AbstractRegEx exp1, AbstractRegEx exp2, AbstractRegEx father) {
        super(exp1, exp2, father);
    }

    @Override
    public AbstractRegEx copy() {
        return new RegExUnion(getExp1().copy(), getExp2().copy(), this.getFather());
    }

    @Override
    public boolean isEmpty() {
        return this.getExp1().isEmpty() && this.getExp2().isEmpty();
    }

    @Override
    public boolean isEmptyWord() {
        return (this.getExp1().isEmptyWord() || this.getExp1().isEmpty()) 
                && (this.getExp2().isEmptyWord() || this.getExp2().isEmpty())
                && (this.getExp1().isEmptyWord() || this.getExp2().isEmptyWord());
    }
    
    @Override
    public void simplify() {
        
    }

    @Override
    public void deriveWords(int kleeneIterations, int maxWords, HashSet<String> currentWords) {
        HashSet<String> words = new HashSet<>();
        HashSet<String> words1 = new HashSet<>();
        HashSet<String> words2 = new HashSet<>();
        this.getExp1().deriveWords(kleeneIterations, maxWords, words1);
        this.getExp2().deriveWords(kleeneIterations, maxWords, words2);
        
        words.addAll(words1);
        words.addAll(words2);
        
        AbstractRegEx.addAllWords(words, currentWords, maxWords);
    }
}
