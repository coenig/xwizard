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

package veryFastPDF.algorithms.turing;

/**
 * @author Lukas König
 */
public class TraceElement {

    private Tape tapeContents;
    private Transition transition;
    private int num;
    
    public TraceElement(Tape tape, Transition transition, int number) {
        this.tapeContents = tape;
        this.transition = transition;
        this.num = number;
    }
    
    public Tape getTapeContents() {
        return this.tapeContents;
    }

    public Transition getTransition() {
        return this.transition;
    }
    
    @Override
    public String toString() {
        return tapeContents + " [" + transition + "]";
    }
    
    public int getNum() {
        return this.num;
    }
}
