/*
 * File name:        StateSetInputSymbolPair.java (package eas.fundamentalAlgorithms.fsm)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  13.05.2013 (21:37:13)
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

package veryFastPDF.algorithms.fsm;


/**
 * @author Lukas König
 */
public class StateSetInputSymbolPair {
    private StateSet states;
    private String inputSymbol;
    
    public StateSetInputSymbolPair(StateSet stateSet, String inputSymbol) {
        this.states = new StateSet(stateSet.getStates());
        this.inputSymbol = inputSymbol;
    }

    public StateSet getStatesSet() {
        return this.states;
    }
    
    public String getInputSymbol() {
        return this.inputSymbol;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.inputSymbol == null) ? 0 : this.inputSymbol.hashCode());
        result = prime * result
                + ((this.states == null) ? 0 : this.states.hashCode());
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
        StateSetInputSymbolPair other = (StateSetInputSymbolPair) obj;
        if (this.inputSymbol == null) {
            if (other.inputSymbol != null)
                return false;
        } else if (!this.inputSymbol.equals(other.inputSymbol))
            return false;
        if (this.states == null) {
            if (other.states != null)
                return false;
        } else if (!this.states.equals(other.states))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + this.states + ", " + this.inputSymbol + ")";
    }
}
