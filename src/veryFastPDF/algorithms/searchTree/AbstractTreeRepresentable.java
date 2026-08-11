/*
 * File name:        AbstractTree.java (package veryFastPDF.algorithms.tree234)
 * Author(s):        bwpc
 * Java version:     8.0 (at generation time)
 * Generation date:  17.06.2015 (16:06:41)
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

package veryFastPDF.algorithms.searchTree;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.plugin.FancyJButton;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;

/**
 * An abstract representation of search trees that encapsulates functionalities
 * that are required in 2-3-4- and red-black-trees.
 * 
 * @author marlonso
 */
public abstract class AbstractTreeRepresentable extends RepresentableDefault {

    /**
     * Serial version
     */
    private static final long serialVersionUID = -5635585779369920719L;

    /**
     * Constant for using strings as keys in tree.
     */
    public static final String STRING = "string";

    /**
     * Constant for using integers as keys in tree.
     */
    public static final String INTEGER = "integer";

    /**
     * Constant for using doubles as keys in tree.
     */
    public static final String REAL = "real";

    /**
     * The binary search tree in which the underlying data is stored. Since the
     * tree can either store integers or strings we cannot set its generic type
     * during declaration.
     */
    @SuppressWarnings("rawtypes")
    protected AbstractSearchTree tree;

    /**
     * The prefix for identifying, whether the user supplied code starts the
     * definition of a tree. In this mode, we use plain tree syntax by
     * specifying nodes and edges directly.
     */
    public final String prefix;

    /**
     * A prefix for switching into the simplified construction mode for trees.
     */
    public static final String SIMPLE = "simple-";

    /**
     * The prefix for identifying, whether the user supplied code starts the
     * definition of a tree. In this mode, the tree is given as a sequence of
     * values to either insert or delete.
     */
    public final String simplePrefix;

    /**
     * A constructor that sets the prefix of the implementing class.
     * 
     * @param prefix
     *            The prefix for this particular tree to identify the script by
     *            the FSM generator
     */
    public AbstractTreeRepresentable(String prefix, Exercise exercise) {
        super(exercise);
        this.prefix = prefix;
        this.simplePrefix = SIMPLE + prefix;
    }

    /**
     * Initializes the abstract search tree using the type specified
     * {@link AbstractTreeRepresentable#type}.
     */
    protected abstract void initializeTreeByType();

    /**
     * Creates a tree from script code.
     * 
     * @param code
     *            The pure scriptcode without any declarations or identification
     *            prefixes.
     */
    protected abstract void createTreeFromCode(String code);

    /**
     * A method for obtaining the class type of the keys that are stored in this
     * tree as string representation.
     * 
     * @return The class of the keys stored in this tree as string
     *         representation.
     */
    protected abstract String getType();

    /**
     * A method for setting the class type of the keys stored in this tree.
     * 
     * @param type
     */
    protected abstract void setType(String type);

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").startsWith(prefix) || code.startsWith(simplePrefix);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
//        String code = RepresentableDefault
//                .getScriptWithoutDeclarations(codeRaw).trim();

        String code = getScriptWithoutPrepAndDecl();
        
        // First check if in simple mode and generate if so
        if (inSimpleMode(code)) {
            // TODO: Reimplement the simple mode for allowing deletion of
            // elements.
            String input = code.trim().substring(simplePrefix.length()).trim();
            String[] tokens = input.split("\\s+");
            initializeTreeByType();
            Comparable<? extends Comparable<?>>[] keys = getKeys(tokens);
            tree.insertAll(keys, keys);
        } else {
            createTreeFromCode(remDecl(this.decollapseLeftPrio()));
        }
    }

    @Override
    public GraphViz generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        GraphViz gv = new GraphViz(pdfPath, this);

        if (tree != null) {
            gv.addln(tree.getDOT());
        } else {
            gv.add(GraphViz.minimalGraph());
            return gv;
        }

        return gv;
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }

    @Override
    public String[] getExampleScripts() {
        return new String[] {
                simplePrefix + "1 2 3 4 5 6 7 8\n\n " + DECL_BEG_TAG
                        + "\ntype=integer\n" + DECL_END_TAG,
                simplePrefix + "a b c d e f g h \n\n " + DECL_BEG_TAG
                        + "\ntype=string\n" + DECL_END_TAG,
                simplePrefix + "1.0 1.5 2.0 2.5 3.0 3.5 4\n\n"
                        + DECL_BEG_TAG + "\ntype=real\n"
                        + DECL_END_TAG };
    }

    /**
     * A method for checking whether the graph description is given in simple
     * mode, which is the case if the code starts with {@link #simplePrefix}.
     * Leading whitespaces are trimmed.
     * 
     * @param originalCode
     *            The code description of the tree.
     * @return <code>true</code> if <code>originalCode</code> starts with
     *         {@link AbstractTreeRepresentable#simplePrefix}
     */
    protected boolean inSimpleMode(String originalCode) {
        if (originalCode != null
                && originalCode.trim().startsWith(simplePrefix))
            return true;
        return false;
    }

    /**
     * A method that wraps the insertion of keys in the UI by button.
     * 
     * @param insert
     *            The key to insert.
     * @return The resulting tree as script.
     */
    @SuppressWarnings("unchecked")
    @ConversionMethod(plainText = false)
    public String insert(String insert) {
        Comparable<? extends Comparable<?>> key = getKeys(insert)[0];
        if (tree == null)
            initializeTreeByType();
        tree.insert(key, key);
        return toScriptCode();
    }

    /**
     * A method that wraps the deletion of keys in the UI by button.
     * 
     * @param delete
     *            The key to delete.
     * @return The resulting tree as script.
     */
    @SuppressWarnings("unchecked")
    @ConversionMethod(plainText = false)
    public String delete(String delete) {
        Comparable<? extends Comparable<?>> key = getKeys(delete)[0];
        if (tree == null)
            return "";
        else
            tree.delete(key);
        return toScriptCode();
    }

    /**
     * Generates a tree instance containing a user supplied number of random
     * integers in the range [1,100].
     * 
     * @return The coded representation of an input tree containing randomized
     *         values.
     */
    @ConversionMethod(plainText = false)
    public String randomTree(int numOfNodes) {
        // We could let the user define these values
        int num = numOfNodes;
        int start = 1;
        int end = 100;

        // In case some idiotic user chose a value that is out of bounds we
        // simply reset it to a random value.
        if (num < 0 || num > end - start + 2) {
            num = (int) Math.floor(Math.random() * (end - start + 1));
            GlobalVariables
                    .getParameters()
                    .logDebug(
                            String.format(
                                    "User supplied number %s out of range. Reset to %s.",
                                    numOfNodes, num));
        }

        // Create an array having values from 'start' to 'end'
        List<Integer> chooseFrom = new ArrayList<>();
        for (int i = start; i < end - start + 2; i++) {
            chooseFrom.add(i);
        }

        String script = simplePrefix;
        for (int i = 0; i < num; i++) {
            int index = (int) Math.floor(Math.random() * chooseFrom.size());
            script += " " + chooseFrom.get(index);
            chooseFrom.remove(index);
        }

        // Setting the script like this is not so nice, however this is the only
        // simple way to add declarations.
        this.setRawScript(script);
        setType("integer");

        return this.addDeclarationsToScript();
    }

    /**
     * The script representation of this tree.
     * 
     * @return The script representation of the current tree.
     */
    @ConversionMethod(plainText = false)
    public String toScriptCode() {
        if (tree == null)
            throw new RuntimeException(
                    "Your current tree definition does not represent a valid tree.");
        else {
            return prefix + tree.getScript() + "\n\n" + DECL_BEG_TAG
                    + "type=" + getType() + ";\n\n" + DECL_END_TAG;
        }
    }

    /**
     * Creates a PDF after each insertion step.
     * 
     * TODO: Reimplement this method for also allowing deletion of elements.
     * 
     * @param pdfPath
     *            The path to the base folder.
     * @param baseFilename
     *            The base file name.
     * @return A result message.
     */
    public String constructionSteps(String pdfPath, String baseFilename) {
        // Save current script since it's overwritten during instance creation.
        String script = this.getRawScript();
        String output = "";

        try {
            if (super.getFather() != null) {
                super.getFather().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            }

            String[] tokens = this.getRawScript().trim()
                    .substring(simplePrefix.length()).split("\\s+");
            String base = simplePrefix;

            for (int i = 0; i < tokens.length; i++) {
                // Construct string
                base += " " + tokens[i];
                // Construct final file path
                String filename = baseFilename + "-" + i;
                // Delete old file if it exists
                // StaticMethods.deleteDAT(file);
                StaticMethods.deleteDAT(pdfPath + "/" + filename + ".pdf");

                // Construct tree and save it to file system
                createInstanceFromScript(base, null);
                GraphViz gv = generatePDFscript(pdfPath);
                gv.storeAsPDF(filename, pdfPath);
            }

            output += this.getRawScript() + "\n\nGenerated " + tokens.length
                    + " trees.\n\n" + "Stored in your working directory: "
                    + pdfPath + "\\" + baseFilename + "-*i*.pdf";

        } catch (RuntimeException e) {
            output = e.getMessage();
        } finally {
            if (super.getFather() != null) {
                super.getFather().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            // Reset scriptcode to original
            this.setRawScript(script);
        }
        return output;
    }

    @Override
    public JComponent getAdditionalInfo() {

        JPanel panel = new JPanel(new MigLayout("insets 0, wrap 1"));

        if (inSimpleMode(this.getRawScript())) {
            FancyJButton constSteps = new FancyJButton(
                    "Generate construction steps",
                    () -> this.constructionSteps(super.getFather()
                            .getWorkingDirectory().getAbsolutePath(), this
                            .getClass().getSimpleName()));
            panel.add(constSteps);
        }

        panel.add(super.getAdditionalInfo());

        return panel;
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        String randName = "Random tree";
        String insertName = "Insert";
        String deleteName = "Delete";
        String convertName = "Script conversion";
        String randName_G = "Zufälliger Baum";
        String insertName_G = "Einfügen";
        String deleteName_G = "Löschen";
        String convertName_G = "Skript-Konversion";

        try {
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("randomTree", Integer.TYPE), // Name
                                                                           // and
                                                                           // parameters
                                                                           // of
                                                                           // the
                                                                           // method.
                    this.getClass(), // Target script class. Important to set
                                     // correctly!
                    this, // Object to invoke method on (usually this).
                    "Generates a tree instance containing a user supplied number of random values",
                    "Erzeugt eine Baum-Instanz von durch den Nutzer angegebener Größe mit zufälligen Werten",
                    randName, randName_G); // Tooltip
            mw1.setMethodDescription("Creating an 2-3-4 tree using random integers in the interval [0,100].) How many keys?");

            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("toScriptCode"),
                    this.getClass(),
                    this,
                    "Converts the tree definition given by insertion and deletion into a proper script definition",
                    "Konvertiert die einfache Skript-Definition in eine vollständige Skript-Definition",
                    convertName, convertName_G);

            MethodWrapper mw3 = new MethodWrapper(this.getClass().getMethod(
                    "insert", String.class), this.getClass(), this,
                    "Insert a key into this tree.", "Füge Schlüssel ein",
                    insertName, insertName_G);

            MethodWrapper mw4 = new MethodWrapper(this.getClass().getMethod(
                    "delete", String.class), this.getClass(), this,
                    "Delete a key in this tree.", "Lösche Schlüssel",
                    deleteName, deleteName_G);

            if (!inSimpleMode(this.getRawScript())) {
                mw2.setMethodButtonEnabled(false);
                mw2.setTooltip(mw2.getTooltip()
                        + " (not available: tree is already given as script.)");
            }

            methods.put(randName, mw1);
            methods.put(insertName, mw3);
            methods.put(deleteName, mw4);
            methods.put(convertName, mw2);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return methods;
    }

    /**
     * A simple convenience method for removing the prefix of the script
     * definition. The method automatically detects, whether the script is given
     * in simple or script mode. If the script does not has any prefix, it
     * remains unchanged.
     * 
     * @param rawScript
     *            The original script with prefix and optionally declarations
     *            and leading whitespaces.
     * @return The <code>script</code> without the prefix.
     */
    protected String removePrefix(String rawScript) {
        String trimmed = rawScript.trim();
        if (trimmed.startsWith(simplePrefix))
            return trimmed.substring(simplePrefix.length());
        if (trimmed.startsWith(prefix))
            return trimmed.substring(prefix.length());
        return rawScript;
    }

    /**
     * Obtain the actual script of the underlying tree without prefix and
     * declarations with decollapsed rules. Additionally removes all white
     * spaces from the script.
     * 
     * @param rawScript
     *            The raw script including (optionally) prefix and declarations.
     * @return The processed script without prefixes, declarations and
     *         whitespaces.
     */
    public String getProcessedScript(String rawScript) {
        return removePrefix(StaticMethods.removeWhitespaces(this
                .decollapseRules(rawScript)));
    }

    /**
     * Auto-transforms keys to the format given by the type in the tree's
     * declaration.
     * 
     * @param keys
     *            Keys as String representation.
     * @return The keys as array in their native class format.
     */
    public Comparable<? extends Comparable<?>>[] getKeys(String... keys) {
        switch (getType()) {
        case "integer":
            List<Integer> integers = new ArrayList<>();
            for (String key : keys) {
                try {
                    integers.add(Integer.valueOf(key));
                } catch (NumberFormatException e) {
                    // Ignore keys that cannot be resolved
                    GlobalVariables.getParameters().logDebug(
                            String.format("Ignored key '%s'", key));
                }
            }
            return integers.toArray(new Integer[integers.size()]);
        case "real":
            List<Double> doubles = new ArrayList<>();
            for (String key : keys) {
                try {
                    doubles.add(Double.valueOf(key));
                } catch (NumberFormatException e) {
                    // Ignore keys that cannot be resolved
                    GlobalVariables.getParameters().logDebug(
                            String.format("Ignored key '%s'", key));
                }
            }
            return doubles.toArray(new Double[doubles.size()]);
        case "string":
        default:
            return keys;
        }
    }

    /**
     * A method for obtaining a map, where keys represent parents and values
     * children. Keys are given by their String representation in script mode.
     * Children are also given by their String representation in script mode,
     * however they are all contained in a HashSet.
     * 
     * @param rawCode
     *            The raw tree definition that can still contain any
     *            declarations, whitespaces, prefixes whatsoever.
     * @return A HashMap that stores parents as keys and children as values.
     */
    protected HashMap<String, List<String>> getParentChildren(String rawCode) {
        // System.out.println(getProcessedScript(rawCode));
        String[] lines = getProcessedScript(rawCode).split(";");

        // Obtain all nodes (keys) and their children (values)
        HashMap<String, List<String>> nodes = new HashMap<>(lines.length);
        for (String line : lines) {
            String[] split = line.trim().split("=>");
            // Remove white spaces
            String key = split[0];
            // Check if node has children
            if (split.length > 1) {
                String child = split[1];
                // If node exists, retain existing children
                List<String> children = nodes.get(key);
                if (children != null) {
                    // If a child was multiple times defined, we simply
                    // overwrite it for convenience and robustness
                    children.add(child);
                } else {
                    children = new ArrayList<>();
                    children.add(child);
                    nodes.put(key, children);
                }
                // Sanity check
                if (children.contains(key))
                    throw new RuntimeException(
                            String.format(
                                    "Parent %s is its own child. For reference see assignment: %s",
                                    key, line));
            }
            // Case if no children. Only add if not present.
            if (!nodes.containsKey(key)) {
                nodes.put(key, new ArrayList<>());
            }
        }
        return nodes;
    }

    /**
     * Obtains the root from a parent children map. See
     * {@link #getParentChildren(String)}. Sanity checks are also performed to
     * ensure that the node was correctly set.
     * 
     * @param nodes
     *            A Map where parents are keys and children values.
     * @return The node according to the map.
     */
    protected String getRoot(Map<String, List<String>> nodes) {
        // Find the root
        String root = null;
        for (String key : nodes.keySet()) {
            boolean isRoot = true;
            for (List<String> children : nodes.values()) {
                if (children.contains(key)) {
                    isRoot = false;
                    break;
                }
            }
            if (isRoot) {
                if (root == null)
                    root = key;
                else
                    throw new RuntimeException(String.format(
                            "Multiple roots defined: %s and %s", root, key));
            }
        }
        // Root sanity check
        if (root == null)
            throw new RuntimeException(
                    "There is no root in your tree. Possible cycle in tree definition detected.");

        return root;
    }
}
