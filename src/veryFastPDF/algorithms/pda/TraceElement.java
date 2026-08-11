/*
 * File name:        Trace.java (package eas.fundamentalAlgorithms.turingMachine)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  19.12.2013 (11:29:18)
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

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Lukas König
 */
public class TraceElement {

    private ArrayList<String> input;
    private int headPos;
    private LinkedList<String> keller;
    private Transition transition;
    
    public static TraceElement accepted(ArrayList<String> input, LinkedList<String> keller, int headPos) {
        return new TraceElement(
                input, 
                keller, 
                new Transition(new StateTapesymbolKellersymbol("", "", ""), (ArrayList<StateKellersymbols>) null) , 
                headPos);
    }

    public static TraceElement notAccepted(ArrayList<String> input, LinkedList<String> keller, int headPos) {
        return new TraceElement(
                input, 
                keller, 
                new Transition(null, new ArrayList<>()) , 
                headPos);
    }
    
    public static boolean isAccepted(TraceElement el) {
        return el.transition.to == null && el.headPos >= el.input.size() - 1;
    }

    public static boolean isNotAccepted(TraceElement el) {
        return el.transition.from == null || el.transition.to == null && el.headPos < el.input.size();
    }

    public TraceElement(
            ArrayList<String> input, 
            LinkedList<String> keller, 
            Transition transition,
            int headPos) {
        this.input = new ArrayList<String>(input);
        this.keller = new LinkedList<String>(keller);
        this.transition = transition;
        this.headPos = headPos;
    }

    public int getHeadPos() {
        return this.headPos;
    }
    
    public LinkedList<String> getKeller() {
        return this.keller;
    }
    
    public ArrayList<String> getInput() {
        return this.input;
    }

    public Transition getTransition() {
        return this.transition;
    }
    
    @Override
    public String toString() {
        return input + " || " + this.keller + " [" + transition + "]";
    }
}
