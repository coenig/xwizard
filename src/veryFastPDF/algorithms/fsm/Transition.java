/*
 * File name:        Transition.java (package eas.miscellaneous.fsmToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  02.10.2012 (18:10:01)
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

import veryFastPDF.pdfProcessors.GraphViz;

/**
 * @author lko
 */
public class Transition implements Comparable<Transition> {

    private boolean directed;
    private String source;
    private String destination;
    private String label;

    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public Transition(boolean directed, String sourceNode, String destinationNode, String label) {
        super();
        this.directed = directed;
        this.source = sourceNode;
        this.destination = destinationNode;
        this.label = label;
    }

    public boolean isDirected() {
        return this.directed;
    }

    public String getSource() {
        return this.source;
    }

    public String getDestination() {
        return this.destination;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    @Override
    public String toString() {
        return this.toString("");
    }
    
    public String toString(String thickness) {
        String edgeSymbol = GraphViz.edgeSymbolDirected;
        
        if (!this.directed) {
            edgeSymbol = GraphViz.edgeSymbolUnDirected;
        }
        
        return source + " " + edgeSymbol + " " + destination + " [label=\"" + label + "\"" + thickness + "];";
    }

    @Override
    public int compareTo(Transition o) {
        return this.getLabel().compareTo(o.getLabel());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((this.destination == null) ? 0 : this.destination.hashCode());
        result = prime * result
                + ((this.label == null) ? 0 : this.label.hashCode());
        result = prime * result
                + ((this.source == null) ? 0 : this.source.hashCode());
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
        Transition other = (Transition) obj;
        if (this.destination == null) {
            if (other.destination != null)
                return false;
        } else if (!this.destination.equals(other.destination))
            return false;
        if (this.label == null) {
            if (other.label != null)
                return false;
        } else if (!this.label.equals(other.label))
            return false;
        if (this.source == null) {
            if (other.source != null)
                return false;
        } else if (!this.source.equals(other.source))
            return false;
        return true;
    }
}
