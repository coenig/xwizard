/*
 * File name:        StatePair.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  10.05.2013 (22:00:11)
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

import java.util.Collection;
import java.util.HashSet;

/**
 * @author lko
 */
public class StatePair {
    private HashSet<String> twoStates;
    
    public StatePair(String state1, String state2) {
        super();
        this.twoStates = new HashSet<String>();
        this.twoStates.add(state1);
        this.twoStates.add(state2);
    }
    
    public Collection<String> getStates() {
        return this.twoStates;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.twoStates == null) ? 0 : this.twoStates.hashCode());
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
        StatePair other = (StatePair) obj;
        if (this.twoStates == null) {
            if (other.twoStates != null)
                return false;
        } else if (!this.twoStates.equals(other.twoStates))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return this.twoStates.toString();
    }
}
