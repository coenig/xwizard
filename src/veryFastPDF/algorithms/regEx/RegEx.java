/*
 * File name:        RegEx.java (package veryFastPDF.algorithms.regEx)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  29.05.2015 (06:56:18)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import veryFastPDF.algorithms.regEx.math.Expression;
import veryFastPDF.algorithms.regEx.math.ExpressionOneVal;
import veryFastPDF.algorithms.regEx.math.ExpressionRaw;
import veryFastPDF.algorithms.regEx.math.ExpressionTerminal;
import veryFastPDF.algorithms.regEx.math.ExpressionTwoVal;

/**
 * Container for AbstractRegEx.
 * 
 * @author Lukas König
 */
public class RegEx {
    
    private AbstractRegEx regEx;

    public RegEx(Expression expressionPlain) {
        regEx = this.createRegExFromExpression(expressionPlain.getExpressionRaw()).regEx;
    }
    
    public ArrayList<String> deriveWords(int kleeneIterations) {
        HashSet<String> derivedWords = new HashSet<>();
        regEx.deriveWords(kleeneIterations, 20, derivedWords);
        
        ArrayList<String> words = new ArrayList<>(derivedWords);
        Collections.sort(words, new WordSorting());
        
        return words;
    }
    
    private RegEx createRegExFromExpression(ExpressionRaw exp) {
        if (exp.getClass().equals(ExpressionTerminal.class)) {
            ExpressionTerminal expTerm = (ExpressionTerminal) exp;
            
            if (expTerm.getCharacter().equals("O")) {
                return new RegEx(new RegExEmpty());
            } else {
                return new RegEx(new RegExCharacter(expTerm.getCharacter()));
            }
        } else if (exp.getClass().equals(ExpressionOneVal.class)) {
            ExpressionOneVal expOneVal = (ExpressionOneVal) exp;
            RegEx regExFromNextLevel = createRegExFromExpression(expOneVal.getExpressionRaw());
            regExFromNextLevel.kleene();
            return regExFromNextLevel;
        } else { // if (exp.getClass().equals(ExpressionTwoVal.class)) {
            ExpressionTwoVal expTwoVal = (ExpressionTwoVal) exp;
            RegEx regExFromNextLevel1 = createRegExFromExpression(expTwoVal.getExp1());
            RegEx regExFromNextLevel2 = createRegExFromExpression(expTwoVal.getExp2());
            if (expTwoVal.getOperatorInfix().equals("+")) {
                regExFromNextLevel1.union(regExFromNextLevel2);
                return regExFromNextLevel1;
            } else { // equals(".")
                regExFromNextLevel1.concat(regExFromNextLevel2);
                return regExFromNextLevel1;
            }
        }
    }
    
    public RegEx() {
        this((AbstractRegEx) null);
    }

    public RegEx(RegEx other) {
        this(other.regEx);
    }
    
    public RegEx(AbstractRegEx other) {
        if (other == null) {
            regEx = new RegExEmpty();
        } else {
            regEx = other.copy();
        }
    }

    public void concat(RegEx regExToConc) {
        this.concat(regExToConc.regEx);
    }

    public void concatFirst(RegEx regExToConc) {
        this.concat(regExToConc.regEx, true);
    }

    public void union(RegEx regExToConc) {
        this.union(regExToConc.regEx);
    }

    public void unionFirst(RegEx regExToConc) {
        this.union(regExToConc.regEx, true);
    }

    public void concat(AbstractRegEx regExToConc) {
        this.concat(regExToConc, false);
    }
    
    /**
     * Concatenates given regEx at the end of this.
     * 
     * @param regExToConc  Given regEx.
     * @param reverse  Concats from left to right.
     */
    public void concat(AbstractRegEx regExToConc, boolean reverse) {
        if (regEx.isEmpty()) {
            // Nothing to do.
        } else if (regExToConc.isEmpty()) {
            regEx = new RegExEmpty();
        } else if (regEx.isEmptyWord()) {
            regEx = regExToConc;
        } else if (regExToConc.isEmptyWord()) {
            // Nothing to do.
        } else {
            if (reverse) {
                regEx = new RegExConcat(regExToConc, regEx, null);
            } else {
                regEx = new RegExConcat(regEx, regExToConc, null);
            }
        }
        
        setFather();
    }
    
    public void union(AbstractRegEx regExToUnite) {
        this.union(regExToUnite, false);
    }
    
    /**
     * United given regEx at the end of this.
     * 
     * @param regExToUnite  Given regEx.
     * @param reverse  Unites from left to right.
     */
    public void union(AbstractRegEx regExToUnite, boolean reverse) {
        if (regEx.isEmpty()) {
            regEx = regExToUnite;
        } else if (regExToUnite.isEmpty()) {
            // Nothing to do.
        } else if (this.containsUnion(regExToUnite)) {
            // Nothing to do.
        } else if (new RegEx(regExToUnite).containsUnion(this.regEx)) {
            regEx = regExToUnite;
        } else {
            if (reverse) {
                regEx = new RegExUnion(regExToUnite, regEx, null);
            } else {
                regEx = new RegExUnion(regEx, regExToUnite, null);
            }
        }
        
        setFather();
    }

    /**
     * Sets the fathers of the two regular expressions underlying this
     * - if any - to this.
     */
    public void setFather() {
        if (Abstract2ValuedRegEx.class.isAssignableFrom(regEx.getClass())) {
            Abstract2ValuedRegEx ru = (Abstract2ValuedRegEx) regEx;
            try {((Abstract2ValuedRegEx) ru.getExp1()).setFather(regEx);} catch (Exception e) {}
            try {((Abstract2ValuedRegEx) ru.getExp2()).setFather(regEx);} catch (Exception e) {}
        }
    }
    
    public void kleene() {
        if (RegExKleene.class.isAssignableFrom(regEx.getClass())) {
            // Nothing to do.
        } else if (regEx.isEmptyOrEmptyWord()) {
            regEx = new RegExLambda();
        } else {
            if (RegExUnion.class.isAssignableFrom(regEx.getClass())) { // (A + O*)* => A*
                RegExUnion union = (RegExUnion) regEx;
                AbstractRegEx regEx1 = union.getExp1();
                AbstractRegEx regEx2 = union.getExp2();
                if (regEx1.isEmptyOrEmptyWord()) {
                    regEx = new RegExKleene(regEx2);
                    return;
                }
                if (regEx2.isEmptyOrEmptyWord()) {
                    regEx = new RegExKleene(regEx1);
                    return;
                }
            }
            
            regEx = new RegExKleene(regEx);
        }
    }

    private boolean containsUnion(AbstractRegEx regExToTest) {
        if (this.regEx.equals(regExToTest)) {
            return true;
        }
        
        if (RegExUnion.class.equals(this.regEx.getClass())) {
            RegExUnion rUn = (RegExUnion) this.regEx;
            RegEx e1 = new RegEx(rUn.getExp1());
            RegEx e2 = new RegEx(rUn.getExp2());
            return e1.containsUnion(regExToTest) || e2.containsUnion(regExToTest);
        }
        
        if (RegExKleene.class.equals(regEx.getClass())) {
            RegExKleene rKl = (RegExKleene) this.regEx;
            RegEx e = new RegEx(rKl.getExp());
            return e.containsUnion(regExToTest);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.regEx == null) ? 0 : this.regEx.hashCode());
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
        RegEx other = (RegEx) obj;
        if (this.regEx == null) {
            if (other.regEx != null)
                return false;
        } else if (!this.regEx.equals(other.regEx))
            return false;
        return true;
    }

    public String toStringLatex() {
        return regEx.toString(true);
    }
    
    @Override
    public String toString() {
        return regEx.toString(false);
    }

    /*
    FROM: https://github.com/izuzak/noam/blob/master/src/noam.re.js
    All those which DON'T have an "OK" are not (yet) implemented within the VFP.
    
    The current set of patterns that are checked are (small alphabet
    letters represent any regular expression):
      OK # (a) => a (sequence of 1 element)
      OK # (a) => a (choices with 1 element)
      OK # $* => $
      OK # (a*)* => a*
      OK # $+a* => a*
      OK # $a => a
      OK # (a+(b+c)) => a+b+c
      OK # ab(cd) => abcd
      OK # a+b+a => b+a
      OK # a*a* => a*
      OK # (a+$)* => a*
      OK # a*aa* => aa*
      OK # (a()) => ()
      OK # ()* => ()
      # (a+b*)* => (a+b)*
      # a+b+a* => b+a*
      # (aa+a)* => a*
      # (ab+ac) => a(b+c)
      # (ab+cb) => (a+c)b
      # a*($+b(a+b)*) => (a+b)*
      # ($+(a+b)*a)b* => (a+b)*
      UNDESIRED # (a*b*c*)* => (a*+b*+c*)*
    If none of these "simple" patterns can be applied, the simplification
    process tries to apply patterns based on language subset (via
    transformations to fsms):
      INFEASIBLE # L1+L2 => L2, if L1 is subset of L2
      INFEASIBLE # (L1+L2)* => L2, if L1* is subset of L2*
      INFEASIBLE # L1*L2* => L2, if L1* is subset of L2*
      INFEASIBLE # $+L => L, if L contains $
      INFEASIBLE # (L1+$)(L2)* => (L2)* if L1 is subset of L2
    The tree transformation process is stopped after no transformation
    can be applied to the tree.
     */

    public void simplify() {
        this.regEx.simplify();
    }
}
