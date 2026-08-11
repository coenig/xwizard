/*
 * File name:        Typ0Rule.java (package eas.grammars)
 * Author(s):        Lukas König
 * Java version:     6.0
 * Generation date:  19.01.2012 (20:05:33)
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

package veryFastPDF.algorithms.grammars;

import java.util.Collection;



/**
 * @author Lukas König
 */
public class Rule {

    private Word leftSide;
    private Word rightSide;
    
    public Rule(Rule other) {
        this.leftSide = new Word(other.leftSide);
        this.rightSide = new Word(other.rightSide);
    }
    
    public Rule(Word left, Word right) {
        this.leftSide = left;
        this.rightSide = right;
    }
    
    public Rule(String codeLine2, Collection<String> nonterminals) {
        String codeLine = codeLine2;
        
        // The following is in case of an URL parameter swallowed a + sign. Not really nice...
        if (codeLine.trim().startsWith("=>")) {
            codeLine = "+" + codeLine;
        }
        if (codeLine.trim().endsWith("=>")) {
            codeLine = codeLine + "+";
        }
        
        String[] cl = codeLine.replace(";", "").replace(" ", "").replace("\n", "").split("=>");
        String lSide = cl[0];
        String rSide = cl[1];
        this.leftSide = new Word(lSide, nonterminals);
        this.rightSide = new Word(rSide, nonterminals);
    }
    
    /**
     * @return  Left-hand side of the rule to use for whatever
     *          you like, however, DON'T mutate it!!
     */
    public Word getImmutableLeftSide() {
        return this.leftSide;
    }
    
    /**
     * @return  Right-hand side of the rule to use for whatever
     *          you like, however, DON'T mutate it!!
     */
    public Word getImmutableRightSide() {
        return this.rightSide;
    }
    
    @Override
    public String toString() {
        String s = "";
        
        s += leftSide.toString().replace("[", "").replace("]", "");
        s += " => ";

        if (rightSide.getWordLength() == 0) {
            s += "epsilon";
        } else {
            s += rightSide.toString().replace("[", "").replace("]", "");
        }
        
        return s;
    }

    public String toStringLatex() {
        String s = "";
        
        s += "&" + leftSide.toString().replace("[", "").replace("]", "").replace(": ", "");
        s += " \\rightarrow ";

        if (rightSide.getWordLength() > 0) {
            s += rightSide.toString().replace("[", "").replace("]", "").replace(": ", "");
        } else {
            s += "\\epsilon";
        }
        
        return s;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.leftSide == null) ? 0 : this.leftSide.hashCode());
        result = prime * result
                + ((this.rightSide == null) ? 0 : this.rightSide.hashCode());
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
        Rule other = (Rule) obj;
        if (this.leftSide == null) {
            if (other.leftSide != null)
                return false;
        } else if (!this.leftSide.equals(other.leftSide))
            return false;
        if (this.rightSide == null) {
            if (other.rightSide != null)
                return false;
        } else if (!this.rightSide.equals(other.rightSide))
            return false;
        return true;
    }
}
