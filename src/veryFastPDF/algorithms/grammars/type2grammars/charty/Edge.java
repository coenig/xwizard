/*
 * File name:        Edge.java (package veryFastPDF.algorithms.grammars.type2grammars)
 * Author(s):        Leo Woerteler, Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  08.03.2015 (08:33:26)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.pdfProcessors.GraphViz;

/**
 * An edge in the chart parser. (Formerly inner class in 
 * <code>ChartParser</code>.)
 *
 * @author Leo Woerteler, Lukas König
 */
public final class Edge implements Iterable<Edge> {

    /** Prime number used as factor in hash-code calculation. */
    private static final int HASH_CODE_PRIME = 31;

    /** Start position of the edge. */
    private final int start;
    /** End position of the edge. */
    private final int end;
    /** position of the dot. */
    final int dot;

    /** Left hand side. */
    protected final String lhs;
    /** Right hand side. */
    protected final String[] rhs;

    /** Ancestors of this edge. */
    private final List<Edge> children;

    private Grammar edgesFatherGrammar;
    private ChartParser edgesFatherChartParser;
    
    private boolean markedL;
    private boolean markedR;
    
    
    protected Grammar getFatherGrammar() {
        return this.edgesFatherGrammar;
    }
    
    public String getLhs() {
        return this.lhs;
    }
    
    /**
     * Constructor.
     *
     * @param s     start
     * @param e     end
     * @param d     dot position
     * @param lh    left hand side
     * @param rh    right hand side
     * @param kids  children of this edger
     */
    protected Edge(final int s, final int e, final int d, final String lh,
            final String[] rh, final List<Edge> kids, Grammar fatherGr, ChartParser fatherCh, boolean markedL, boolean markedR) {
        start = s;
        end = e;
        dot = d;
        lhs = lh;
        rhs = rh;
        children = kids;
        this.edgesFatherGrammar = fatherGr;
        this.setMarkedL(markedL);
        this.setMarkedR(markedR);
        this.edgesFatherChartParser = fatherCh;
    }

    /**
     * Combining constructor.
     *
     * @param a
     *            active edge
     * @param i
     *            inactive edge
     */
    protected Edge(final Edge a, final Edge i, Grammar fatherGr, ChartParser fatherCh, boolean markedL, boolean markedR) {
        this(a.getStart(), i.getEnd(), a.dot + 1, a.lhs, a.rhs.clone(),
                new ArrayList<Edge>(a.children), fatherGr, fatherCh, markedL, markedR);
        children.add(i);
    }

    protected Edge(Edge edge) {
        this.children = edge.children;
        this.dot = edge.dot;
        this.edgesFatherGrammar = edge.edgesFatherGrammar;
        this.end = edge.getEnd();
        this.lhs = edge.lhs;
        this.rhs = edge.rhs;
        this.start = edge.getStart();
        this.setMarkedL(edge.isMarkedL());
        this.setMarkedR(false);
    }

    /**
     * Checks whether this active edge matches the given inactive edge.
     *
     * @param inactive
     *            inactive edge
     * @return result of check
     */
    protected boolean matches(final Edge inactive) {
        return getEnd() == inactive.getStart() && rhs[dot].equals(inactive.lhs);
    }

    /**
     * Checks whether this edge is still active.
     *
     * @return {@code true}, if this edge is active, {@code false}
     *         otherwise.
     */
    protected boolean isActive() {
        return dot < rhs.length;
    }

    /**
     * Checks whether this edge spans over the entire input sequence.
     *
     * @return {@code true}, if this edge is overspanning, {@code false}
     */
    protected boolean isOverspanning() {
        return getStart() == 0 && getEnd() == this.edgesFatherChartParser.getTokens().length;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Edge)) {
            return false;
        }
        final Edge o = (Edge) obj;
        return getStart() == o.getStart() && getEnd() == o.getEnd() && dot == o.dot
                && lhs.equals(o.lhs) && Arrays.equals(rhs, o.rhs)
                && children.equals(o.children);
    }

    @Override
    public int hashCode() {
        int res = 0;
        for (final int i : new int[] { getStart(), getEnd(), dot, lhs.hashCode(),
                Arrays.hashCode(rhs), children.hashCode() }) {
            res = HASH_CODE_PRIME * res + i;
        }
        return res;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %s, %s)", getStart(), getEnd(), dot, lhs,
                Arrays.toString(rhs));
    }

    /**
     * Creates a representation of this subtree in bracketing notation.
     *
     * @return representation
     */
    protected String toLaTeX() {
        final StringBuilder sb = new StringBuilder();
        toLaTeX(sb, Collections.singletonList(this));
        return sb.toString().trim();
    }

    /**
     * Recursive {@link #toLaTeX()} helper.
     *
     * @param sb
     *            string builder for efficiency
     * @param kids
     *            list of children
     */
    private void toLaTeX(final StringBuilder sb, final List<Edge> kids) {
        for (final Edge c : kids) {
            sb.append(" [.").append(c.lhs);
            if (!c.children.isEmpty()) {
                toLaTeX(sb, c.children);
            } else {
                for (final String term : c.rhs) {
                    sb.append(" ").append(term);
                }
            }
            sb.append(" ]");
        }
    }

    /**
     * Checks whether the edge has real children.
     * 
     * @return Whether the edge has real children or just text as children.
     */
    protected boolean hasRealChildren() {
        return !children.isEmpty();
    }

    @Override
    public Iterator<Edge> iterator() {
        return children.iterator();
    }

    protected String toString(String id, int level2, boolean useIndexForMultiSymbolWords) {
        String s = "";
        int level = level2;
        
        if (!this.hasRealChildren()) {
            level = Integer.MAX_VALUE;
        }
        
        HashSet<String> sameLevel = ParseTree.sameLevelNodes.get(level);
        
        if (sameLevel == null) {
            sameLevel = new HashSet<>();
            ParseTree.sameLevelNodes.put(level, sameLevel);
        }
        sameLevel.add("a" + id);
        
        String style = "";
        
        if (!this.hasRealChildren()) {
            style = "shape=rectangle,";
            
            if ("<>".equals(lhs.toString())) {
                style = "shape=point,";
            }
        }
        
        
        String sString = this.lhs;

        if (sString.equals(ChartParser.currentStartSymbol)) {
            style = "style=\"filled\"";
        }
        
        // Special symbols.
        sString = GraphViz.replaceSpecialChars(sString);

        if (!sString.startsWith("&") || !sString.endsWith(";")) {
            if (useIndexForMultiSymbolWords && sString.length() > 1 && sString.charAt(1) != '\'') {
                sString = sString.charAt(0) + "<SUB>" + sString.substring(1) + "</SUB>";
            }
        }
        
        s += "a" + id + " [" + style + "label=<" + sString + ">];\n";

        int i = 0;
        
        for (Edge child : this) {
            s += "a" + id + " -> a" + (id + "b" + i) + ";\n";
            s += child.toString(id + "b" + i, level + 1, useIndexForMultiSymbolWords);
            i++;
        }

        return s;
    }
    
    protected EdgeListList getDerivation(EdgeListList soFar) {
        List<Edge> lastDeriv = soFar.getLast();
        EdgeList newDeriv = new EdgeList(lastDeriv);
        
        for (int i = 0; i < newDeriv.size(); i++) {
            Edge e = newDeriv.get(i);
            
            if (!e.children.isEmpty()) {
                newDeriv.remove(i);
                boolean isRightSideMarked = lastDeriv.get(i).isMarkedR();
                Edge leftSideEdge = new Edge(lastDeriv.get(i));
                leftSideEdge.setMarkedL(true);
                leftSideEdge.setMarkedR(isRightSideMarked);
                
                lastDeriv.set(i, leftSideEdge);

                for (int j = e.children.size() - 1; j >= 0; j--) {
                    Edge edge = new Edge(e.children.get(j));
                    edge.setMarkedR(true);
                    newDeriv.add(i, edge);
                }
                
                soFar.add(newDeriv);
                return e.getDerivation(soFar);
            }
        }
        
        return soFar;
    }

    /**
     * @return Returns the marked.
     */
    protected boolean isMarkedL() {
        return markedL;
    }

    /**
     * @param marked The marked to set.
     */
    protected void setMarkedL(boolean marked) {
        this.markedL = marked;
    }

    /**
     * @return Returns the markedR.
     */
    protected boolean isMarkedR() {
        return markedR;
    }

    /**
     * @param markedR The markedR to set.
     */
    protected void setMarkedR(boolean markedR) {
        this.markedR = markedR;
    }

    /**
     * @return Returns the start.
     */
    protected int getStart() {
        return start;
    }

    /**
     * @return Returns the end.
     */
    protected int getEnd() {
        return end;
    }
}
