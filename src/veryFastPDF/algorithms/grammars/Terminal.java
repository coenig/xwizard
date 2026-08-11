/*
 * File name:        Terminal.java (package eas.grammars)
 * Author(s):        Lukas König
 * Java version:     6.0
 * Generation date:  19.01.2012 (20:06:47)
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

/**
 * Keep this class immutable if possible.
 * 
 * @author Lukas König
 */
public class Terminal implements Symbol {

    private String symbString;
    
    public Terminal(Terminal other) {
        this.symbString = other.symbString;
    }
    
    public Terminal(String symbol) {
        this.symbString = symbol;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.symbString == null) ? 0 : this.symbString.hashCode());
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
        Terminal other = (Terminal) obj;
        if (this.symbString == null) {
            if (other.symbString != null)
                return false;
        } else if (!this.symbString.equals(other.symbString))
            return false;
        return true;
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
    
    @Override
    public String toString() {
        return this.getSymbolAsString();
    }

    @Override
    public String getSymbolAsString() {
        return this.symbString;
    }
}
