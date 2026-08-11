/*
 * File name:        EdgeList.java (package eas.math.type2grammars.charty)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  06.03.2015 (19:01:47)
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

package veryFastPDF.algorithms.grammars.type2grammars.charty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import veryFastPDF.pdfProcessors.LaTeXPDF;

/**
 * @author Lukas König
 */
public class EdgeList extends ArrayList<Edge> {

    private static final long serialVersionUID = -4922714058821504273L;

    public EdgeList() {
        super();
    }

    public EdgeList(Collection<? extends Edge> c) {
        for (Edge e : c) {
            this.add(new Edge(e));
        }
    }

    public EdgeList(int initialCapacity) {
        super(initialCapacity);
    }
    
    @Override
    public String toString() {
        String s = "";
        
        List<Edge> edgeLists = this;
        boolean inUlineMode = false;
        
        for (Edge edge : edgeLists) {
            String lhs = LaTeXPDF.replaceSpecialChars(edge.lhs);
            
            if (!lhs.startsWith("\\") && lhs.length() > 1 && !lhs.equals("<>")) {
                if (edge.getFatherGrammar().isMultiLetterSymbolsHaveIndex()) {
                    lhs = lhs.charAt(0) + "_{" + lhs.substring(1) + "}";
                } else {
                    lhs = "\\mbox{" + lhs + "}";
//                    lhs = "\\ " + lhs + "\\ ";
                }
            }
            
            if (edge.isMarkedL()) {
                lhs = "\\overline{" + lhs + "}";
            }
            
            if (edge.isMarkedR() && !inUlineMode) {
                lhs = "\\underline{" + lhs;
                inUlineMode = true;
            } else if (!edge.isMarkedR() && inUlineMode) {
                lhs = "}" + lhs;
                inUlineMode = false;
            }
            
            s += lhs;
        }
        
        if (inUlineMode) {
            s += "}";
        }

        return s;
    }
}
