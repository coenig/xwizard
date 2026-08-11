/*
 * File name:        BigDecimalSpecial.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  20.02.2016 (10:34:33)
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

package veryFastPDF.algorithms.numberRep;

import java.math.BigDecimal;

/**
 * @author Lukas König
 */
public class BigDecimalSpecial extends BigDecimal {

    private static final long serialVersionUID = 5388759298982934566L;
    private boolean isInfinity = false;
    private boolean isNaN = false;
    
    public BigDecimalSpecial(String val) {
        super(val);
    }

    public BigDecimalSpecial(BigDecimal val) {
        super(val.unscaledValue(), val.scale());
    }

    public boolean isInfinity() {
        return this.isInfinity;
    }

    public void setInfinity(boolean isInfinity) {
        this.isInfinity = isInfinity;
    }

    public boolean isNaN() {
        return this.isNaN;
    }

    public void setNaN(boolean isNaN) {
        this.isNaN = isNaN;
    }
}
