/*
 * File name:        ScriptNode.java (package veryFastPDF.script)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  19.03.2017 (21:42:23)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

package veryFastPDF.script;

import java.util.LinkedList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import veryFastPDF.algorithms.plainDOT.Graphviz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.plugin.VFPWindow;

/**
 * @author hq0976
 *
 */
public class ScriptTree {

    private LinkedList<ScriptTree> children = new LinkedList<>();
    private ScriptTree father;
    private Integer beg, end;
    private String idName;

    public String getIdName() {
        return idName;
    }

    private String script;
    private boolean debug = false;
    
    public void activateDebug() {
        this.debug = true;
    }
    
    public ScriptTree() {
        this(null);
    }
    
    private ScriptTree(ScriptTree father) {
        this.father = father;
    }
    
    public LinkedList<ScriptTree> getChildren() {
        return this.children;
    }

    public ScriptTree getFather() {
        return this.father;
    }

    public int getBeg() {
        return beg;
    }

    public int getEnd() {
        return end;
    }

    private static final String PREP_TREE_INFO = WebLink.createDOTExceptionNode("Error in prep tree");
    
    private static String errorNodes(int beg, int end) {
        return PREP_TREE_INFO + "dgsezusi [label=\"" + beg + "/" + end + " ??\" color=\"red\"];";
    }

    public void addScript(String scriptIdentifier, String script, int beg, int end) {
        addScriptPrivate(scriptIdentifier, script, beg, end);
    }
        
    private void addScriptPrivate(String scriptIdentifier, String script, int beg, int end) {
        if (this.beg == null || this.end == null) {
            this.beg = beg;
            this.end = end;
        }
        
        if (this.beg == beg && this.end == end) {
            idName = scriptIdentifier;
            this.script = script.replace("\n", "\n").replace("\n", " ");
        } else if (this.beg > beg || this.end < end) {
            String errorNodes = errorNodes(beg, end);
            WebLink.EXCEPTION_EXPLANATION = this.getGraphvizTree(errorNodes);
            throw new RuntimeException("Node range [" + this.beg + "-" + this.end + "] too narrow for requested range [" + beg + "-" + end + "].");
        } else {
            int i;
            for (i = 0; i < this.children.size(); i++) {
                ScriptTree n = this.children.get(i);
                if (n.beg <= beg && n.end >= end) { // Go down in child.
                    n.addScript(scriptIdentifier, script, beg, end);
                    return;
                } else if (n.beg > end) { // Fit in gap.
                    if (i == 0 || this.children.get(i - 1).end < beg) {
                        break;
                    }
                } else if (beg <= n.beg && beg <= n.end && end >= n.beg && end >= n.end) { // New node is father of (several) child(ren).
                    int storeBeg = i;
                    int storeEnd;
                    while (i < this.children.size() && end >= this.children.get(i).end) {
                        i++;
                    }
                    storeEnd = i - 1;

                    ScriptTree nNew = new ScriptTree(this);
                    nNew.addScript(scriptIdentifier, script, beg, end);
                    
                    // Remove from direct children.
                    for (int j = storeBeg; j <= storeEnd; j++) {
                        nNew.children.add(this.children.get(storeBeg));
                        this.children.remove(storeBeg);
                    }

                    this.children.add(storeBeg, nNew);
                    
                    return;
                }
            }
            
            if (i > 0 && this.children.get(i - 1).end >= beg) {
                String errorNodes = errorNodes(beg, end);
                WebLink.EXCEPTION_EXPLANATION = this.getGraphvizTree(errorNodes);
                throw new RuntimeException("Cannot insert '" + script + "' interval [" + beg + "-" + end + "] into node tree " + this.getGraphvizTree(null) + ".");
            }
            
            ScriptTree n = new ScriptTree(this);
            n.addScript(scriptIdentifier, script, beg, end);
            this.children.add(i, n);
        }
        
        if (debug) {
            storeAsPDF();
        }
    }

    /**
     * Adjusts all begins and ends starting at <code>oldEnd</code> or behind by
     * <code>delta</code>. Note that sub-script nodes may be shifted to the end
     * of the area of the higher-level script if its area shrinks from right to 
     * left, as no information is stored which parts of the area (which the 
     * sub-script area is part of) have been changed.</BR>
     * </BR>
     * For example: In <code>@{d@{x}@d}@</code>,  <code>x</code> will eventually 
     * end up being being shifted to the end of the <code>dxd</code> area, 
     * resulting in <code>x</code> being assigned the position <code>2-2</code>
     * (instead of <code>2-2</code>).</BR>
     * </BR>
     * Therefore, the script tree cannot replace the script, but it should only
     * be used for looking up the inscript preprocessor hierarchy defined by the 
     * script.
     * 
     * 
     * @param oldEnd  The index, left of which no adjustments are necessary 
     *                (except if an area shrinks over some of its sub-areas.
     * @param delta   The delta value to add to all parts right of 
     *                <code>oldEnd</code>.
     */
    public void shiftEnd(int oldEnd, int delta) {
        shiftEnd(oldEnd, delta, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (debug) {
            storeAsPDF();
        }
    }
    
    public void storeAsPDF() {
        if (MainLink.isApplicationOriginDesktop()) {
            String workingDir = VFPWindow.getSINGLETON_INSTANCE().getWorkingDirectory().getAbsolutePath();
            RepresentableAsPDF rep = RepresentableFactory.instanceFromScript(this.getGraphvizTree(null), null);
            PDFProcessor pdfProcessor = ScriptConversionMethods.getPDFProcessorFrom(rep, workingDir);
            pdfProcessor.storeAsPDF(
                    WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME), 
                    workingDir);
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
        }
    }
    
    /**
     * Does what is explained in @{link {@link #shiftEnd(int, int)}.
     * 
     * @param oldEnd  The index, left of which no adjustments are necessary 
     *                (except if an area shrinks over some of its sub-areas.
     * @param delta   The delta value to add to all parts right of 
     *                <code>oldEnd</code>.
     * @param min     The minimum given from the higher-level father.
     * @param max     The maximum given from the higher-level father.
     */
    private void shiftEnd(int oldEnd, int delta, int min, int max) {
        if (delta == 0) {
            return;
        }
        
        int myLength = end - beg;
        
        if (this.beg >= oldEnd) {
            this.beg += delta;
        }
        
        if (this.end >= oldEnd) {
            this.end += delta;
        }
        
        if (this.end > max) {
            this.end = max;
            this.beg = this.end - myLength; 
        }
        
        if (this.beg < min) {
            this.beg = min;
        }
        
        if (this.beg > this.end) {
            this.beg = this.end + 1;
        }
        
        for (ScriptTree s : this.children) {
            s.shiftEnd(oldEnd, delta, this.beg, this.end);
        }
    }

    @Override
    public String toString() {
        return // this.idName + "-" + 
                this.beg + this.children.toString() + this.end;
    }
    
    
    public String getGraphvizTree(String error) {
        String s = Graphviz.GRAPHVIZ_DOT_PREAMBLE + "\n" 
                + Graphviz.BEGIN_DIGRAPH
                + (error == null ? "" : error)
                + gvInner()
                + Graphviz.END_DIGRAPH;
        
        return s;
    }

    private String makeSafe(String s) {
        return ScriptConversionMethods.encryptScript(s + "").replace("scrypt:", "s");
    }
    
    private String gvInner() {
        String s = "";
        String processedIDName = this.idName == null ? "this" : this.idName;
        String safeNameFrom = makeSafe(processedIDName);
        String add = this.father == null ? " [" + this.beg + "/" + this.end + "]" :  "='" + StringEscapeUtils.escapeHtml4(StringUtils.abbreviateMiddle(this.script, "...", 30)).replace("@", "&#64;") + "'";
        s += safeNameFrom + " [label=<" + "" + (processedIDName + add) + ">];\n";
        
        for (int i = 0; i < this.children.size(); i++) {
            ScriptTree child = this.children.get(i);
            String safeNameTo = makeSafe(child.idName);
            s += safeNameFrom + "->" + safeNameTo + " [label=\"" + child.beg + "/" + child.end + "\"];\n";
            s += child.gvInner();
        }
        
        return s;
    }
    
    public ScriptTree findRoot() {
        if (this.father == null) {
            return this;
        }
        
        return this.father.findRoot();
    }
    
    public LinkedList<ScriptTree> traverseDepthFirst() {
        return traverseDepthFirst(new LinkedList<>());
    }
    
    private LinkedList<ScriptTree> traverseDepthFirst(LinkedList<ScriptTree> soFar) {
        this.children.forEach(c -> c.traverseDepthFirst(soFar));
        soFar.add(this);
        return soFar;
    }
    
    public static void main(String[] args) {
        ScriptTree root = new ScriptTree(null);
        root.addScript("a", "test", 0, 100);
        root.addScript("b", "test", 10, 20);
        root.addScript("c", "test", 30, 40);
        root.addScript("d", "test", 50, 60);
        root.addScript("e", "test", 1, 7);
        root.addScript("f", "test", 33, 36);
        root.addScript("g", "test", 68, 86);
        root.addScript("h", "test", 23, 66);
        root.addScript("i", "test", 37, 39);
        System.out.println(root);
        root.shiftEnd(25, 4);
        System.out.println(root);
    }
}
