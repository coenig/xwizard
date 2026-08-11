/*
 * File name:        Abstract2ValuedRegEx.java (package veryFastPDF.algorithms.regEx)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  29.05.2015 (11:00:12)
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

/**
 * @author Lukas König
 */
public abstract class Abstract2ValuedRegEx extends AbstractRegEx {

    private AbstractRegEx exp1;
    private AbstractRegEx exp2;
    private AbstractRegEx father;
    private boolean isPartOfUnion;
    private boolean isPartOfConcatenation;

    public AbstractRegEx getExp1() {
        return this.exp1;
    }
    
    public AbstractRegEx getExp2() {
        return this.exp2;
    }

    public AbstractRegEx getFather() {
        return this.father;
    }
    
    public Abstract2ValuedRegEx(AbstractRegEx exp1, AbstractRegEx exp2, AbstractRegEx father) {
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.setFather(father);
    }
    
    public void setFather(AbstractRegEx father) {
        isPartOfUnion = false;
        isPartOfConcatenation = false;
        this.father = father;
    
        if (father == null) {
            isPartOfUnion = true;
            isPartOfConcatenation = true;
        }
        
        if (father != null) {
            if (RegExUnion.class.equals(father.getClass())) {
                isPartOfUnion = true;
            }
            if (RegExConcat.class.equals(father.getClass())) {
                isPartOfConcatenation = true;
            }
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.exp1 == null) ? 0 : this.exp1.hashCode());
        result = prime * result
                + ((this.exp2 == null) ? 0 : this.exp2.hashCode());
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
        Abstract2ValuedRegEx other = (Abstract2ValuedRegEx) obj;
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
        return true;
    }

    @Override
    public String toString(boolean latex) {
        String symb = "+";
        if (RegExConcat.class.isAssignableFrom(this.getClass())) {
            if (latex) {
                symb = "\\cdot";
            } else {
                symb = "";
            }
        }
        
        if (symb.equals("+") && isPartOfUnion || symb.equals("\\cdot") && isPartOfConcatenation) {
            return getExp1().toString(latex) + " " + symb + " " + getExp2().toString(latex);
        }

        return "(" + getExp1().toString(latex) + " " + symb + " " + getExp2().toString(latex) + ")";
    }
}
