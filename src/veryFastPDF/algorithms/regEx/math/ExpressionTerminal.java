/*
 * File name:        TerminalExpression.java (package eas.math)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.06.2015 (16:08:23)
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

package veryFastPDF.algorithms.regEx.math;

/**
 * @author Lukas König
 */
public class ExpressionTerminal extends ExpressionRaw {

    private String character;

    public ExpressionTerminal(String character) {
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
        ExpressionTerminal other = (ExpressionTerminal) obj;
        if (this.character == null) {
            if (other.character != null)
                return false;
        } else if (!this.character.equals(other.character))
            return false;
        return true;
    }
    
    @Override
    public String toString(boolean latex) {
        String lineBreak = Expression.manageLineBreak1(latex);
        Expression.manageLineBreak2(this.character);
        return this.character + lineBreak;
    }

    @Override
    public String toStringPostfix() {
        return this.toString(false);
    }
    
    public String getCharacter() {
        return this.character;
    }
}
