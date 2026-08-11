/*
 * File name:        ExpressionTwoVal.java (package eas.math)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.06.2015 (16:09:14)
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
public class ExpressionTwoVal extends ExpressionRaw {
    
    private ExpressionRaw exp1;
    private ExpressionRaw exp2;
    private String operatorInfix;
    private Expression envelope;
    
    /*
     * If true, the operators "around" this expression are stronger than "operatorInfix".
     * This means that brackets have to be printed in any case.
     */
    private boolean weak; 
    
    public ExpressionTwoVal(
            ExpressionRaw e1, 
            ExpressionRaw e2, 
            String operator,
            boolean weakOpInContext,
            Expression father) {
        this.exp1 = e1;
        this.exp2 = e2;
        this.operatorInfix = operator;
        this.weak = weakOpInContext;
        this.envelope = father;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.exp1 == null) ? 0 : this.exp1.hashCode());
        result = prime * result
                + ((this.exp2 == null) ? 0 : this.exp2.hashCode());
        result = prime
                * result
                + ((this.operatorInfix == null) ? 0 : this.operatorInfix
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
        ExpressionTwoVal other = (ExpressionTwoVal) obj;
        if (this.exp1 == null) {
            if (other.exp1 != null)
                return false;
        } else if (!this.exp1.equals(other.exp1))
            return false;
        if (this.exp2 == null) {
            if (other.exp2 != null)
                return false;
        } else if (!this.exp2.equals(other.exp2))
            return false;
        if (this.operatorInfix == null) {
            if (other.operatorInfix != null)
                return false;
        } else if (!this.operatorInfix.equals(other.operatorInfix))
            return false;
        return true;
    }

    @Override
    public String toString(boolean latex) {
        String latexLineBreak = "";

        if (Expression.OMIT_UNNECESSARY_BRACKETS_IN_OUTPUT && !weak) {
            String shortVersion = exp1.toString(latex) + operatorInfix + exp2.toString(latex) + latexLineBreak;
            Expression.manageLineBreak2(shortVersion);
            return shortVersion;
        }
        
        String longVersion = envelope.getOpBrack() + exp1.toString(latex) + operatorInfix + exp2.toString(latex) + envelope.getClBrack() + latexLineBreak;
        Expression.manageLineBreak2(longVersion);
        return longVersion;
    }

    @Override
    public String toStringPostfix() {
        return exp1.toStringPostfix() + " " + exp2.toStringPostfix() + " " + operatorInfix;
    }
    
    public ExpressionRaw getExp1() {
        return this.exp1;
    }
    
    public ExpressionRaw getExp2() {
        return this.exp2;
    }
    
    public String getOperatorInfix() {
        return this.operatorInfix;
    }
}
