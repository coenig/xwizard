/*
 * File name:        StateTapeSymbolKellerSymbol.java (package eas.fundamentalAlgorithms.graphBased.pushDown)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  10.01.2014 (14:27:50)
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

package veryFastPDF.algorithms.pda;


/**
 * @author Lukas König
 */
public class StateTapesymbolKellersymbol {
    protected String state;
    protected String tapeSymbol;
    protected String kellerSymbol;

    public StateTapesymbolKellersymbol(
            String state, 
            String tapeSymbol, 
            String kellerSymbol) {
        super();
        this.state = state;
        this.tapeSymbol = tapeSymbol;
        this.kellerSymbol = kellerSymbol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.kellerSymbol == null) ? 0 : this.kellerSymbol
                        .hashCode());
        result = prime * result
                + ((this.state == null) ? 0 : this.state.hashCode());
        result = prime * result
                + ((this.tapeSymbol == null) ? 0 : this.tapeSymbol.hashCode());
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
        StateTapesymbolKellersymbol other = (StateTapesymbolKellersymbol) obj;
        if (this.kellerSymbol == null) {
            if (other.kellerSymbol != null)
                return false;
        } else if (!this.kellerSymbol.equals(other.kellerSymbol))
            return false;
        if (this.state == null) {
            if (other.state != null)
                return false;
        } else if (!this.state.equals(other.state))
            return false;
        if (this.tapeSymbol == null) {
            if (other.tapeSymbol != null)
                return false;
        } else if (!this.tapeSymbol.equals(other.tapeSymbol))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + this.state + ", " + this.tapeSymbol + ", " + this.kellerSymbol + ")";
    }
}
