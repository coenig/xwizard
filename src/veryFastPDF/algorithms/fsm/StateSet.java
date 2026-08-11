/*
 * File name:        StateSet.java (package eas.fundamentalAlgorithms.fsm)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  18.05.2013 (22:01:27)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author lko
 */
public class StateSet implements Comparable<StateSet> {

    private ArrayList<String> states;
    
    public StateSet() {
        this.states = new ArrayList<String>();
    }
    
    public StateSet(StateSet states) {
        this(states.getStates());
    }
    
    public StateSet(Collection<String> states) {
        this.states = new ArrayList<String>(states);
        this.sort();
    }
    
    private void sort() {
        Collections.sort(states);
    }
    
    public ArrayList<String> getStates() {
        return this.states;
    }

    public void add(String state) {
        states.add(state);
        this.sort();
    }

    public void addAll(Collection<String> states) {
        states.addAll(states);
        this.sort();
    }
    
    @Override
    public int compareTo(StateSet o) {
        if (this.states.size() > 0 && o.states.size() == 0) {
            return -1;
        } else if (o.states.size() > 0 && this.states.size() == 0) {
            return 1;
        }
        
        int i = ((Integer) this.states.size()).compareTo(o.states.size());

        if (i == 0) {
            return this.states.toString().replace("[", "").replace("]", "")
                    .compareTo(o.states.toString().replace("[", "").replace("]", ""));
        }
        
        return i;
    }

    @Override
    public String toString() {
        return this.states.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        StateSet other = (StateSet) obj;
        if (this.states == null) {
            if (other.states != null)
                return false;
        } else if (!this.states.equals(other.states))
            return false;
        return true;
    }
}
