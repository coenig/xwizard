/*
 * File name:        RedBlackTree.java (package eas.math.fundamentalAlgorithms.graphBased.algorithms.trees)
 * Author(s):        Marlon Braun
 * Java version:     8.0 (at generation time)
 * Generation date:  25.08.2014 (15:21:44)
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

package veryFastPDF.algorithms.searchTree.redblacktree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.searchTree.AbstractTreeRepresentable;
import veryFastPDF.algorithms.searchTree.DirtyReflectionHacks;
import veryFastPDF.algorithms.searchTree.tree234.Tree234;
import veryFastPDF.algorithms.searchTree.tree234.Tree234Impl;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.web.Webproof;

/**
 * A class for drawing Red-Black-Trees in Lukas' framework.
 * 
 * @author marlon.braun
 */
@Webproof(useInProductiveMode = true)
public class RedBlackTree extends AbstractTreeRepresentable {

    /**
     * Serial version
     */
    private static final long serialVersionUID = -5104362528859096140L;

    /**
     * An identifier for specifying red nodes in the script.
     */
    public static final String RED = "r";

    /**
     * An identifier for specifiying black nodes in the script. By default all
     * nodes are black, so adding this prefix is not necessary.
     */
    public static final String BLACK = "b";

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
    public static final String TREE_PREFIX = "redblack:";

    /**
     * Standard constructor
     */
    public RedBlackTree(Exercise exercise) {
        super(TREE_PREFIX, exercise);
        this.setAllowCollapsingRules(true);
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
    public String[] getExampleScripts() {
        String[] common = super.getExampleScripts();
        String[] ret = new String[common.length + 1];
        for (int i = 0; i < common.length; i++) {
            ret[i] = common[i];
        }
        ret[common.length] = prefix
                + "\nd => b|f;\nb => a|r:c;\nf => r:e|h;\n\n "
                + DECL_BEG_TAG + "\ntype=string\n"
                + DECL_END_TAG;
        return ret;
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        String tree234Name = "2-3-4 tree";
        String tree234Name_G = "2-3-4-Baum";

        MethodWrapper mw1;
        try {
            mw1 = new MethodWrapper(this.getClass().getMethod("to234Tree"),
                    Tree234.class, // Target script class. Important to set
                                   // correctly!
                    this, // Object to invoke method on (usually this).
                    "Transforms this red-black tree into an equivalent 2-3-4 tree",
                    "Konvertiert diesen Rot-Schwarz-Baum in einen äquivalenten 2-3-4-Baum",
                    tree234Name,
                    tree234Name_G);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        } // Tooltip
        mw1.setMethodDescription("Transforms this red-black tree into an equivalent 2-3-4 tree.");

        methods.put(tree234Name, mw1);

        return methods;
    }

    /**
     * A method for obtaining the script of an equivalent red-black tree of this
     * 2-3-4-tree.
     * 
     * @return The script representation of this 2-3-4-tree as red-black-tree.
     */
    @ConversionMethod(plainText = false)
    public String to234Tree() {

        @SuppressWarnings("rawtypes")
        Tree234Impl tree234 = ((RedBlackBST) tree).get234tree();
        String prefix = (new Tree234(this.getExercise())).prefix;

        return prefix + tree234.getScript() + "\n\n" + DECL_BEG_TAG
                + "\ntype=" + type + "\n" + DECL_END_TAG;
    }

    /**
     * A red black tree that treats input parameters as Strings.
     */
    private RedBlackBST<String, String> redBlackTreeString;

    /**
     * A red black tree that treats input parameters as numbers.
     */
    private RedBlackBST<Integer, Integer> redBlackTreeInteger;

    /**
     * Creates a PDF output of a red black tree that is equivalent to this 2-3-4
     * tree
     * 
     * @param pdfPath
     *            The path to the base folder.
     * @param filename
     *            The filename to which the tree is written.
     * @return A result message.
     */
    public String transformToRedBlack(String pdfPath, String filename) {

        @SuppressWarnings("rawtypes")
        Tree234Impl rbTree;
        if (redBlackTreeInteger != null) {
            rbTree = redBlackTreeInteger.get234tree();
        } else {
            rbTree = redBlackTreeString.get234tree();
        }
        StaticMethods.deleteDAT(pdfPath + "/" + filename + ".pdf");

        GraphViz gv = new GraphViz(pdfPath, this);
        gv.addln(rbTree.getDOT());
        gv.storeAsPDF(filename, pdfPath);

        return "Generated Red-black tree from this 2-3-4 tree. Stored in your working directory: "
                + pdfPath + "\\" + filename + ".pdf";
    }

    @Override
    protected void initializeTreeByType() {
        switch (type.toLowerCase()) {
        case "integer":
            tree = new RedBlackBST<Integer, Integer>();
            break;
        case "real":
            tree = new RedBlackBST<Double, Double>();
            break;
        case "string":
        default:
            tree = new RedBlackBST<String, String>();
        }
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void createTreeFromCode(String code) {

        // Obtain all nodes (keys) and their children (values)
        HashMap<String, List<String>> nodes = getParentChildren(code);
        HashMap<String, List<String>> formattedNodes = new HashMap<>();

        // Format keys, so all black nodes are prefixed
        for (Map.Entry<String, List<String>> entry : nodes.entrySet()) {
            entry.getValue().replaceAll(s -> formatKey(s));
            formattedNodes.put(formatKey(entry.getKey()), entry.getValue());
        }

        // Find the root
        String root = getRoot(formattedNodes);

        // Build all the nodes of the tree
        LinkedList<RedBlackBST.Node> treeNodes = new LinkedList<>();

        RedBlackBST.Node rootNode = DirtyReflectionHacks.newRedBlackNode(
                getKeys(RedBlackBST.getKey(root))[0],
                RedBlackBST.getColor(root));

        this.initializeTreeByType();
        DirtyReflectionHacks.setRoot(tree, rootNode);
        treeNodes.push(rootNode);

        while (!treeNodes.isEmpty()) {
            RedBlackBST.Node node = treeNodes.poll();
            List<String> children = formattedNodes.get(node.toString());
            if (children != null) {
                for (String c : children) {
                    RedBlackBST.Node child = DirtyReflectionHacks
                            .newRedBlackNode(getKeys(RedBlackBST.getKey(c))[0],
                                    RedBlackBST.getColor(c));
                    if (child.compareTo(node) < 0) {
                        DirtyReflectionHacks.setLeftChild(node, child);
                    } else {
                        DirtyReflectionHacks.setRightChild(node, child);
                    }
                    treeNodes.push(child);
                }
            }
        }
    }

    /**
     * A convenience method for prefix keys with {@value #BLACK} and
     * {@value #RED}.
     * 
     * @param key
     *            The key that is formatted
     * @return The formatted key
     */
    private String formatKey(String key) {
        boolean color = RedBlackBST.getColor(key);
        String formattedKey = color == RedBlackBST.RED ? RED : BLACK;
        formattedKey += ":" + RedBlackBST.getKey(key);
        return formattedKey;
    }
    
    @Override
    public String getGermanName() {
        return "Rot-Schwarz-Baum";
    }

    @Override
    public String createScriptFromInstance() {
        // TODO Auto-generated method stub
        return null;
    }
}
