/*
 * File name:        TwoThreeFourTree.java (package eas.math.fundamentalAlgorithms.graphBased.algorithms.twothreefourtree)
 * Author(s):        Marlon Braun
 * Java version:     8.0 (at generation time)
 * Generation date:  15.10.2014 (14:30:58)
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

package veryFastPDF.algorithms.searchTree.tree234;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import veryFastPDF.algorithms.searchTree.AbstractTreeRepresentable;
import veryFastPDF.algorithms.searchTree.DirtyReflectionHacks;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackBST;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackTree;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.web.Webproof;

/**
 * This class is an implementation for generating and displaying 2-3-4 trees in
 * the UI. The implementation is based on the layout of {@link RedBlackTree}.
 * 
 * @author marlon.braun
 */
@Webproof(useInProductiveMode = true)
public class Tree234 extends AbstractTreeRepresentable {

    /**
     * Serial version
     */
    private static final long serialVersionUID = -1962461996234688296L;

    /**
     * The type of keys used in creating the tree. Either <code>string</code>,
     * <code>integer</code> or <code>real</code>. This variable should be
     * located in {@link AbstractTreeRepresentable}, however Lukas declaration
     * mechanism does not work for class variables in parent classes.
     */
    private String type = "string";
    
    /**
     * The prefix of this tree implementation. 
     */
    public static final String TREE_PREFIX = "tree234:";

    public Tree234(Exercise exercise) {
        super(TREE_PREFIX, exercise);
        this.setAllowCollapsingRules(true);
    }

    @Override
    public GraphViz generatePDFscript(String pdfPath) {

        String graphVizString = "";

        if (tree != null) {
            graphVizString = tree.getDOT();
        } else {
            GraphViz gv = new GraphViz(pdfPath, this);
            gv.add(GraphViz.minimalGraph());
            return gv;
        }

        GraphViz gv = new GraphViz(pdfPath, this);
        gv.addln(graphVizString);
        return gv;
    }

    @Override
    public String[] getExampleScripts() {
        String[] common = super.getExampleScripts();
        String[] ret = new String[common.length + 2];
        for (int i = 0; i < common.length; i++) {
            ret[i] = common[i];
        }
        ret[common.length] = prefix + "\n[2,4,6] => [1]|[3]|[5]|[7,8]; \n\n "
                + DECL_BEG_TAG + "\ntype=integer\n"
                + DECL_END_TAG;
        ret[common.length + 1] = prefix
                + "\n[b,d,f] => [a]|[c]|[e]|[g,h];\n\n "
                + DECL_BEG_TAG + "\ntype=string\n"
                + DECL_END_TAG;
        return ret;
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();
        String redBlackName = "Red-black tree";
        String redBlackName_G = "Rot-Schwarz-Baum";

        MethodWrapper mw1;
        try {
            mw1 = new MethodWrapper(this.getClass().getMethod("toRedBlack"),
                    RedBlackTree.class, // Target script class. Important to set
                                        // correctly!
                    this, // Object to invoke method on (usually this).
                    "Transforms this 2-3-4 tree into an equivalent left-leaning red-black tree.",
                    "Konvertiert diesen 2-3-4-Baum in einen äquivalenten links-geneigten Rot-Schwarz-Baum",
                    redBlackName,
                    redBlackName_G);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        } // Tooltip
        mw1.setMethodDescription("Transforms this 2-3-4 tree into an equivalent left-leaning red-black tree.");

        methods.put(redBlackName, mw1);

        return methods;
    }

    /**
     * A method for obtaining the script of an equivalent red-black tree of this
     * 2-3-4-tree.
     * 
     * @return The script representation of this 2-3-4-tree as red-black-tree.
     */
    @ConversionMethod(plainText = false)
    public String toRedBlack() {

        @SuppressWarnings("rawtypes")
        RedBlackBST rbTree = ((Tree234Impl) tree).getRedBlackTree();
        String prefix = (new RedBlackTree(this.getExercise())).prefix;

        return prefix + rbTree.getScript() + "\n\n" + DECL_BEG_TAG
                + "\ntype=" + type + "\n" + DECL_END_TAG;
    }

    @Override
    protected void initializeTreeByType() {
        switch (type.toLowerCase()) {
        case "integer":
            tree = new Tree234Impl<Integer, Integer>();
            break;
        case "real":
            tree = new Tree234Impl<Double, Double>();
            break;
        case "string":
        default:
            tree = new Tree234Impl<String, String>();
        }
    }

    @Override
    protected String getType() {
        return type;
    }

    @Override
    protected void setType(String type) {
        this.type = type.trim().toLowerCase();
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void createTreeFromCode(String code) {
        // Obtain all nodes (keys) and their children (values)
        HashMap<String, List<String>> nodes = getParentChildren(code);
        // Find the root
        String root = getRoot(nodes);

        // Build all the nodes of the tree
        LinkedList<Tree234Impl.Node> treeNodes = new LinkedList<>();

        String[] rootKeys = Tree234Impl.getKeysFromScriptRepresentation(root);
        Tree234Impl.Node rootNode = DirtyReflectionHacks
                .new234Node(getKeys(rootKeys));

        initializeTreeByType();
        DirtyReflectionHacks.setRoot(tree, rootNode);
        treeNodes.push(rootNode);

        while (!treeNodes.isEmpty()) {
            Tree234Impl.Node node = treeNodes.poll();
            List<String> children = nodes.get(node.scriptRepresentation());
            if (children != null) {
                for (String child : children) {
                    String[] keys = Tree234Impl
                            .getKeysFromScriptRepresentation(child);
                    Tree234Impl.Node newChild = DirtyReflectionHacks
                            .new234Node(node, getKeys(keys));
                    DirtyReflectionHacks.addChild(node, newChild);
                    treeNodes.push(newChild);
                }
            }
        }
    }
    
    @Override
    public String getGermanName() {
        return "2-3-4-Baum";
    }

    @Override
    public String createScriptFromInstance() {
        // TODO Auto-generated method stub
        return null;
    }
}
