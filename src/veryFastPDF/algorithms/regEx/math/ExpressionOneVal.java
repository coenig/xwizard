/*
 * File name:        ExpressionOneVal.java (package eas.math)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.06.2015 (16:09:05)
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
public class ExpressionOneVal extends ExpressionRaw {
    private ExpressionRaw expression;
    private String operatorPostfix;
    private Expression envelope;
    
    public ExpressionOneVal(ExpressionRaw e, String operator, Expression father) {
        this.expression = e;
        this.operatorPostfix = operator;
        this.envelope = father;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.expression == null) ? 0 : this.expression.hashCode());
        result = prime
                * result
                + ((this.operatorPostfix == null) ? 0 : this.operatorPostfix
                        .hashCode());
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
        ExpressionOneVal other = (ExpressionOneVal) obj;
        if (this.expression == null) {
            if (other.expression != null)
                return false;
        } else if (!this.expression.equals(other.expression))
            return false;
        if (this.operatorPostfix == null) {
            if (other.operatorPostfix != null)
                return false;
        } else if (!this.operatorPostfix.equals(other.operatorPostfix))
            return false;
        return true;
    }

    @Override
    public String toString(boolean latex) {
        String latexLineBreak = "";

        if (Expression.OMIT_UNNECESSARY_BRACKETS_IN_OUTPUT
                && (expression.getClass().equals(ExpressionTerminal.class)
                        || expression.getClass().equals(ExpressionOneVal.class))) {
            String shortVersion = expression.toString(latex) + operatorPostfix + latexLineBreak;
            
            Expression.manageLineBreak2(shortVersion);
            return shortVersion;
        }
        
        String longVersion = envelope.getOpBrack() + expression.toString(latex) + envelope.getClBrack() + operatorPostfix + latexLineBreak;
        Expression.manageLineBreak2(longVersion);
        return longVersion;
    }

    @Override
    public String toStringPostfix() {
        return expression.toStringPostfix() + " " + operatorPostfix;
    }
    
    public ExpressionRaw getExpressionRaw() {
        return this.expression;
    }
}
