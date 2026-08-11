/*
 * File name:        BDD.java (package eas.miscellaneous.fsmToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  05.10.2012 (13:23:55)
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

package veryFastPDF.algorithms.bdd;

import java.awt.Cursor;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import mainServlet.WebLink;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.algorithms.JavaPDFCode;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.numberRep.NumberRepresentable;
import veryFastPDF.algorithms.numberRep.representations.ExcessQ;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.deprecated.JavaPDF;
import veryFastPDF.plugin.FancyJButton;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.script.exceptionHandling.LongOperationException;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;


/**
 * Klasse zur Erzeugung eines BDDs. Die Klasse kann temporär auch Zwischen-
 * schritte speichern, also kein echtes BDD. Nach wiederholtem Aufruf der
 * Methode simplifyOneStep (bis sie false zurückgibt), enthält die Klasse
 * ein BDD. Die Variablenreihenfolge ist in der Variable VAR_NAMES angegeben;
 * eine Änderung hier bewirkt jedoch keine verändert Berechnung - diese
 * muss über den übergebeben Array im Konstruktor gesteuert werden.
 * 
 * @author lko
 */
@SuppressWarnings("deprecation")
@Webproof(useInProductiveMode = true)
public class BDD extends RepresentableDefault {

    private static final String SIMPLIFY_ONE_STEP_METHOD_NAME = "Simplify stepwise";
    private static final String TRUTH_TABLE_JAVA_PDF_METHOD_NAME = "Truth table (JavaPDF)";
    private static final String TRUTH_TABLE_LATEX_METHOD_NAME = "Truth table (Latex)";
    private static final String METHOD_NAME_ANIMATE_BDD_GENERATION = "Animate BDD generation";

    private static final String SCRIPT_PREAMBLE = "bdd:";
    private static final long serialVersionUID = -4344122123034909213L;
    private BDDNode tree;
    private String scriptcode = null;
    private final String[] STD_VAR_NAMES = new String[] {
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    
    private String[] varNames;
    
    private int simplifySteps = -1;
    
    private void setStdVarNames() {
        this.varNames = new String[STD_VAR_NAMES.length];
        for (int i = 0; i < this.varNames.length; i++) {
            this.varNames[i] = STD_VAR_NAMES[i];
        }
    }
            
    /**
     * Konstruktor, der die Funktionswerte der darzustellenden Funktion erhält.
     * Die Werte werden in der Reihenfolge übergeben, wie sie an den Blättern
     * des unvereinfachten Entscheidungsbaums vorkommen (0 -> 0 -> 0 ganz links,
     * 1 -> 1 -> 1 ganz rechts). Die Anzahl der Werte muss eine Zweierpotens
     * sein und die Werte selbst müssen 0 oder 1 sein.
     * 
     * @param values  Die Werte.
     */
    public BDD(String code, Exercise exercise) {
        super(exercise);
        super.addIgnoredField("scriptcode");
        super.addIgnoredField("firstTime");
        super.addIgnoredField("marked");
        super.addIgnoredField("dontSimplifyToFinalOnGVGeneration");
        this.setAllowCollapsingRules(false);
        createInstanceFromScript(code, null);
        this.setStdVarNames();
    }
    
    public BDD(Exercise exercise) {
        this("", exercise);
    }
    
    private void setFathers(BDDNode subTree) {
        if (subTree == null) {
            return;
        }
        
        if (subTree.getLeft() != null) {
            subTree.getLeft().addFather(subTree);
            this.setFathers(subTree.getLeft());
        }
        
        if (subTree.getRight() != null) {
            subTree.getRight().addFather(subTree);
            this.setFathers(subTree.getRight());
        }
    }

//    @Override
//    public String toString() {
//        return this.tree.getGraphVizInstructions().toString().replace(", ", "\n");
//    }
    
    private LinkedList<BDDNode> getAllNodesFromLevel(int level, BDDNode startNode) {
        LinkedList<BDDNode> nodes = new LinkedList<BDDNode>();
        
        if (startNode == null) {
            return nodes;
        }
        
        if (level == 1) {
            nodes.add(startNode);
        }
        
        nodes.addAll(getAllNodesFromLevel(level - 1, startNode.getLeft()));
        nodes.addAll(getAllNodesFromLevel(level - 1, startNode.getRight()));
        
        return nodes;
    }
    
    private void mergeNodes(BDDNode node1, BDDNode node2) {
        
        // node1 und node2 sind identisch. Wähle o.B.d.A. node1.
        for (BDDNode father : node2.getFathers()) {
            if (father.getLeft() == node2) {
                father.setLeft(node1);
            } 
            if (father.getRight() == node2) {
                father.setRight(node1);
            }
            
            node1.addFather(father);
            
//            if (node2.getLeft() != null) {
//                node2.getLeft().removeFather(node2);
//            }
//            if (node2.getRight() != null) {
//                node2.getRight().removeFather(node2);
//            }
//            node2.removeAllFathers();
//            node2.setLeft(null);
//            node2.setRight(null);
            
            if (node1.getLeft() != null) {
                node1.getLeft().removeFather(node2);
            }
            if (node1.getRight() != null) {
                node1.getRight().removeFather(node2);
            }
        }
    }
    
    /**
     * Löscht einen Knoten, dessen Kinder äquivalent sind.
     * 
     * @param node  Der zu löschende Knoten.
     */
    private void deleteNode(BDDNode node) {

        if (node == this.tree) {
            // Obersten Knoten löschen.
            node.getLeft().removeFather(node);
            this.tree = node.getLeft();
        } else {
            // Der Väter linkes oder rechtes Kind neu setzen auf (o.B.d.A.) linkes Kind.
            for (BDDNode father : node.getFathers()) {
                if (node == father.getLeft()) {
                    father.setLeft(node.getLeft());
                } 
                if (node == father.getRight()) {
                    father.setRight(node.getLeft());
                }
                
                // Kinds Väter neu setzen.
                node.getLeft().removeFather(node);
                node.getLeft().addFather(father);
            }
        }
    }
    
    private boolean firstTime = true;

    public boolean isFirstTime() {
        return this.firstTime;
    }

    private boolean marked = false;
    
    public boolean isMarked() {
        return marked;
    }

    /**
     * Vollführt oder markiert einen Vereinfachungsschritt des BDDs, falls 
     * möglich. Die Methode markiert beim ersten Aufruf die nächste 
     * Vereinfachung. Beim zweiten wird diese durchgeführt. Beim dritten
     * wird die nächste Vereinfachung markiert usw.
     * 
     * @return  Ob es noch Vereinfachungen gibt.
     */
    public boolean simplifyOneStep() {
        this.tree.deMarkAll();
        this.marked = false;
        
        for (int level = tree.getDepth(); level >= 1; level--) {
            LinkedList<BDDNode> nodesOnLevel = this.getAllNodesFromLevel(level, this.tree);
            
            for (BDDNode node1 : nodesOnLevel) {
                for (BDDNode node2 : nodesOnLevel) {
                    if (node1 != node2 && node1.identical(node2)) {
                        if (firstTime) {
                            node1.mark();
                            node2.mark();
                            this.marked = true;
                            firstTime = false;
                            return true;
                        } else {
                            mergeNodes(node1, node2);
                            firstTime = true;
                            return true;
                        }
                    }
                }
                
                if (node1.getLeft() != null && node1.getRight() != null) {
                    if (node1.getLeft().identical(node1.getRight())) {
                        if (firstTime) {
                            node1.mark();
                            this.marked = true;
                            firstTime = false;
                            return true;
                        } else {
                            deleteNode(node1);
                            firstTime = true;
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }

    private int[] getValues(String scriptText) {
        String codeCleared = clearCodeAndEstablishVarOrder(scriptText);

        String onesAndZeros = "";
        int[] onesAndZerosNum;
        for (int i = 0; i < codeCleared.length(); i++) {
            if (codeCleared.charAt(i) == '1' || codeCleared.charAt(i) == '0') {
                onesAndZeros += codeCleared.charAt(i);
            }
        }
        
        int length = 1;
        while (length < onesAndZeros.length()) {
            length *= 2;
        }
        
        onesAndZerosNum = new int[length];
        for (int i = 0; i < onesAndZeros.length(); i++) {
            if (onesAndZeros.charAt(i) == '1') {
                onesAndZerosNum[i] = 1;
            }
            if (onesAndZeros.charAt(i) == '0') {
                onesAndZerosNum[i] = 0;
            }
        }
        
        return onesAndZerosNum;
    }

    public int simplifyToFinalOrForNumSteps(int numSteps) {
        GeneralDialog.resetLongTimeOperationID("$BDD-GENERATION$");
        
        int i = 0;
        
        if (numSteps != 0) { // -1 means: simplify to end.
            do {
                if (!GeneralDialog.continueLongOperation("$BDD-GENERATION$")) {
                    throw new LongOperationException();
                }
                
                if (!this.isFirstTime() || i == 0) {
                    i++;
                }
            } while (this.simplifyOneStep() && (numSteps < 0 || numSteps > i));
            
            this.firstTime = true;
            this.simplifyOneStep();
        }
        
        return i;
    }
    
    private void createPDFFromCurrentState(String datnam, int i, String tempDir) {
        this.dontSimplifyToFinalOnGVGeneration = true;
        GraphViz gv = this.generatePDFscript(tempDir);
        this.dontSimplifyToFinalOnGVGeneration = false;
        
        String realName = datnam;
        realName += "-" + i;
        
        try {
            gv.storeAsPDF(realName, tempDir);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int extractPDFBDDGeneration(
            String datnam, 
            String tempDir) {
        this.createInstanceFromScript(this.scriptcode, null);
        int i = 0;

        do {
            // Ausgeben: erster Durchlauf oder alle markierten.
            if (!this.isFirstTime() || i == 0) {
                createPDFFromCurrentState(datnam, i, tempDir);
                i++;
            }
        } while (this.simplifyOneStep());

        // Ausgeben: letzter Durchlauf.
        createPDFFromCurrentState(datnam, i, tempDir);
        return i;
    }
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {
                SCRIPT_PREAMBLE + " a,b,c,d,e: 01101001100101101001011001101001",
                SCRIPT_PREAMBLE + " a,b,c,d,e: 01101"};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").split("\n")[0].toLowerCase().startsWith(SCRIPT_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
        String code = getScriptWithoutPrepAndDeclAndPreamble();

        this.setStdVarNames();
        this.firstTime = true;
        this.marked = false;
        this.scriptcode = code;
        
        int[] values = this.getValues(code);
        
        for (int i = 1; i <= values.length * 2; i *= 2) {
            if (i == values.length) {
                break;
            }
            if (i > values.length) {
                throw new RuntimeException("Anzahl an Werten ist keine Zweierpotenz.");
            }
        }
        
        int index = 0;
        LinkedList<BDDNode> currenttrees = new LinkedList<BDDNode>();
        for (int i : values) {
            if (i != 0 && i != 1) {
                throw new RuntimeException("Nur 0 und 1 als Werte erlaubt.");
            }
            
            currenttrees.add(new BDDNode(null, null, "" + i, index));
            index++;
        }
        
        int level = (int) (Math.log10(values.length) / Math.log10(2)) - 1;
        
        LinkedList<BDDNode> zwischtrees = new LinkedList<BDDNode>();
        while (currenttrees.size() > 1) {
            index = 0;
            
            zwischtrees = new LinkedList<BDDNode>();
            for (int i = 0; i < currenttrees.size(); i += 2) {
                zwischtrees.add(new BDDNode(currenttrees.get(i), currenttrees.get(i + 1), varNames[level], index));
                index++;
            }
            currenttrees = zwischtrees;
            
            level--;
        }
        
        this.tree = currenttrees.getFirst();
        
        this.setFathers(this.tree);
    }

    private String clearCodeAndEstablishVarOrder(String code) {
        String codeCleared = StaticMethods.removeWhitespaces(remDecl(code));
        String varOrder = "";
        
        if (codeCleared.split(":").length > 2) {
            varOrder = codeCleared.split(":")[codeCleared.split(":").length - 2];
            String[] varOrderSplit = varOrder.split(",");
            for (int i = 0; i < varOrderSplit.length; i++) {
                if (i < this.varNames.length) {
                    this.varNames[i] = varOrderSplit[i];
                }
            }
        }
        
        codeCleared = codeCleared.split(":")[codeCleared.split(":").length - 1];
        return codeCleared;
    }

    private boolean dontSimplifyToFinalOnGVGeneration = false;
    
    @Override
    public GraphViz generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);
        
        GraphViz gv;
        gv = new GraphViz(pdfPath, this);

        if (!dontSimplifyToFinalOnGVGeneration) {
            try {
                this.simplifyToFinalOrForNumSteps(this.simplifySteps);
            } catch (Exception e) {
                gv.add(gv.safetyCodeInCaseOfLargeCodeOrLongOperation(gv.getSourceString().length()));
                return gv;
            }
        }
        
        gv.addln(GraphViz.startDigraph());

        List<String> instructions = this.tree.getGraphVizInstructions();
        
        LinkedList<String> inst0 = new LinkedList<String>();
        LinkedList<String> inst1 = new LinkedList<String>();
        LinkedList<String> inst01 = new LinkedList<String>();
        
        for (String line : instructions) {
            if (line.contains("[label=\" 0 \"]")) {
                inst0.add(line);
            } else if (line.contains("[label=\" 1 \"]")) {
                inst1.add(line);
            } else if (line.contains("[label=\" 0/1 \"]")) {
                inst01.add(line);
            } else {
                gv.addln(line);
            }
        }
   
        // Sortiere 0- und 1-Kanten.
        for (String line : inst0) {gv.addln(line);}
        for (String line : inst1) {gv.addln(line);}
        for (String line : inst01) {gv.addln(line);}
        
        gv.addln(GraphViz.endGraph());

        return gv;
    }
    
    public String generateExercise(String pdfPath, String filename) {
        if (super.getFather() != null) {
            super.getFather().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
        
        String output = "";
        int i = 0;
        
        while (StaticMethods.deleteDAT(pdfPath + "/" + filename + "-" + i + ".pdf")) {
//            StaticMethods.deleteDAT(pdfPath + "/" + filename + "-" + i + ".java");
            i++;
        }
        
        int steps = this.extractPDFBDDGeneration(
                filename, 
                pdfPath);
        
        output += this.scriptcode + "\n\nBDD generated in " + steps + " steps.\n\n" + "Stored in your working directory: "
            + pdfPath + File.separator + filename + "-*i*.pdf";
        
        if (GeneralDialog.yesNoAnswer(
                "Show all BDD steps?", 
                "Do you want to open all " + steps + " generated steps as PDFs?")) {
            for (int j = 0; j <= steps; j++) {
                super.getFather().runSumatra(filename + "-" + j + ".pdf");
                try {Thread.sleep(500);} catch (InterruptedException e) {}
            }
        }
        
        if (super.getFather() != null) {
            super.getFather().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        return output;
    }

    @ConversionMethod(plainText = false)
    public String getLaTeXTruthTableCompleteScript() {
        String prefix = LaTeX.LATEX_PREAMBLE + LaTeXCommands.PREAMBLE_CROP_PAGE;
        String postamble = LaTeXCommands.POSTAMBLE_STANDARD;
        return prefix + this.getLaTeXPlainTruthTabular() + postamble + this.getUpperClassDeclarationsBlockOnly();
    }
    
    @ConversionMethod(plainText = false)
    public String getJavaPDFTruthTableCompleteScript() {
        String codeCleared = this.scriptcode.split(":")[this.scriptcode.split(":").length - 1];
        int[] values = this.getValues(codeCleared);
        String tabular = JavaPDF.JAVA_PDF_PREFIX + "\n";
        int numVars = intLogBase2(values.length);
        
        for (int i = 0; i < numVars; i++) {
            tabular += "" + this.varNames[i] + " ";
        }
        tabular += "  f\n";

        ExcessQ eq = new ExcessQ(null);
        String pars = ExcessQ.PAR_NAME_Q + "=" + 0 + ","
                + NumberRepresentable.PAR_NAME_LENGTH + "=" + numVars + ","
                + ExcessQ.PAR_NAME_RADIX + "=" + 2 + ",";
        
        for (int i = 0; i < values.length; i++) {
            eq.setFromParameters(pars + NumberRepresentable.PAR_NAME_VALUE + "=" + i);
            String bin = eq.getCode();
            
            for (int j = 0; j < bin.length(); j++) {
                tabular += bin.charAt(j) + " ";
            }
            
            tabular += " " + values[i];
            tabular += "\n";
        }
        
        return tabular;
    }
    
    private int intLogBase2(int num) {
        int p = 0;
        int n = num;
        while (n > 1) {
            n /= 2;
            p++;
        }
        return p;
    }
    
    public String getLaTeXPlainTruthTabular() {
        String codeCleared = this.scriptcode.split(":")[this.scriptcode.split(":").length - 1];
        int[] values = this.getValues(codeCleared);
        String tabular = "\n\\begin{tabular}{";
        int numVars = intLogBase2(values.length);
        
        for (int i = 0; i < numVars; i++) {
            tabular += "|c";
        }
        tabular += "||c|}\n\\hline\n";

        for (int i = 0; i < numVars; i++) {
            tabular += "$" + this.varNames[i] + "$ & ";
        }
        tabular += "$f_{BDD}$ \\\\\n\\hline\\hline\n";

        ExcessQ eq = new ExcessQ(null);
        String pars = ExcessQ.PAR_NAME_Q + "=" + 0 + ","
                + NumberRepresentable.PAR_NAME_LENGTH + "=" + numVars + ","
                + ExcessQ.PAR_NAME_RADIX + "=" + 2 + ",";

        for (int i = 0; i < values.length; i++) {
            eq.setFromParameters(pars + NumberRepresentable.PAR_NAME_VALUE + "=" + i);

            String bin = eq.getCode();
            
            for (int j = 0; j < bin.length(); j++) {
                tabular += bin.charAt(j) + " & ";
            }
            
            tabular += values[i];
            tabular += "\\\\\n\\hline\n";
        }
        
        tabular += "\\end{tabular}";
        
        return tabular;
    }
    
    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }

    @Override
    public JComponent getAdditionalInfo() {
        JPanel panel = new JPanel(new MigLayout("wrap 1"));
        
        FancyJButton buttGenerateExercise = new FancyJButton(
                "Generate all steps...", 
                () -> this.generateExercise(
                        super.getFather().getWorkingDirectory().getAbsolutePath(), 
                        WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME)));
        panel.add(buttGenerateExercise);
        panel.add(super.getAdditionalInfo());

        return panel;
    }

    @ConversionMethod(plainText = false)
    public String animateGen() {
        String loopStr = "\n" + RepresentableDefault.createStdAnimation(
                "simp", "simp", "max");
        return createCompleteAnimationScript(loopStr);
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();
        
        String tableLatexName = TRUTH_TABLE_LATEX_METHOD_NAME;
        String tableJavaName = TRUTH_TABLE_JAVA_PDF_METHOD_NAME;
        String simplifyOneStep = SIMPLIFY_ONE_STEP_METHOD_NAME;
        String tableLatexName_G = "Wahrheitstabelle (Latex)";
        String tableJavaName_G = "Wahrheitstabelle (JavaPDF)";
        String simplifyOneStep_G = "Vereinfache schrittweise";
        String maxStepsName = "max";
        String animateBDDGenName = METHOD_NAME_ANIMATE_BDD_GENERATION;
        String animateBDDGenName_G = "Animiere BDD-Erzeugung";

        try {
            MethodWrapper mwh8 = new MethodWrapper(
                    this.getClass().getMethod("animateGen"),
                    BDD.class, // Target script class. Important to set correctly!
                    this,
                    "Create animation of the BDD generation.",
                    "Erstelle eine Animation aus dem BDD-Erzeugungs-Prozess.",
                    animateBDDGenName,
                    animateBDDGenName_G);
            mwh8.setDisplayLevel(5);
            
            MethodWrapper mwh0 = new MethodWrapper(
                    this.getClass().getMethod("maxSteps"),
                    this, 
                    maxStepsName);
            
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("getLaTeXTruthTableCompleteScript"),
                    LaTeX.class, // Target script class. Important to set correctly!
                    this,
                    "Show the truth table for this BDD in Latex mode",
                    "Zeige die Wahrheitstabelle des BDDs im Latex-Modus",
                    tableLatexName,
                    tableLatexName_G);
            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("getJavaPDFTruthTableCompleteScript"),
                    JavaPDFCode.class, // Target script class. Important to set correctly!
                    this,
                    "Show the truth table for this BDD in plain JavaPDF mode",
                    "Zeige die Wahrheitstabelle des BDDs im JavaPDF-Modus",
                    tableJavaName,
                    tableJavaName_G);
            MethodWrapper mw3 = new MethodWrapper(
                    this.getClass().getMethod("simplifyOneStepMore"),
                    BDD.class, // Target script class. Important to set correctly!
                    this,
                    "Performs one step more of the BDD creation algorithm",
                    "Führt einen weiteren Vereinfachungsschritt des BDD-Algorithmus aus",
                    simplifyOneStep,
                    simplifyOneStep_G);
            
            methods.put(maxStepsName, mwh0);
            methods.put(tableLatexName, mw1);
            methods.put(tableJavaName, mw2);
            methods.put(simplifyOneStep, mw3);
            methods.put(animateBDDGenName, mwh8);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        return methods;
    }

    @Override
    public String getGermanName() {
        return "BDD";
    }
    
    @ConversionMethod(plainText = false)
    public String simplifyOneStepMore() {
        BDD copy = (BDD) RepresentableFactory.copyRep(this, false);
        int maxSteps = copy.simplifyToFinalOrForNumSteps(Integer.MAX_VALUE);
        copy.simplifySteps = (copy.simplifySteps + 1) % (maxSteps + 1);
        String createScriptFromInstance = copy.createScriptFromInstance();
        return createScriptFromInstance;
    }

    private int getMaxSteps() {
        BDD copy = new BDD(null);
        copy.createInstanceFromScript(this.getScriptWithoutPrepAndDeclAndPreamble(), null);
        int maxSteps = copy.simplifyToFinalOrForNumSteps(Integer.MAX_VALUE);
        return maxSteps;
    }
    
    @ConversionMethod
    public String maxSteps() {
        return getMaxSteps() + "";
    }
    
    @Override
    public String createScriptFromInstance() {
        return this.addDeclarationsToScript();
    }

    @Override
    public HashMap<String, String> getMetaProperties() {
        HashMap<String, String> metaPropertiesSuper = super.getMetaProperties();
        String rawTruthValues = this.clearCodeAndEstablishVarOrder(this.getRawScript());
        
        BDD copy = new BDD(this.getRawScript(), this.getExercise());
        int beginNumNodes = copy.tree.getNumNodes();
        int beginDepth = copy.tree.getDepth();
        
        copy.simplifyToFinalOrForNumSteps(this.simplifySteps);
        int currentNumSteps = this.simplifySteps;
        int currentNumNodes = copy.tree.getNumNodes();
        int currentDepth = copy.tree.getDepth();
        
        copy = new BDD(this.getRawScript(), this.getExercise());
        int endNumSteps = copy.simplifyToFinalOrForNumSteps(Integer.MAX_VALUE);
        int endNumNodes = copy.tree.getNumNodes();
        int endDepth = copy.tree.getDepth();

        metaPropertiesSuper.put("BeginDepth", "" + beginDepth);           // OK
        metaPropertiesSuper.put("BeginNumNodes", "" + beginNumNodes);     // OK
        metaPropertiesSuper.put("CurrentDepth", "" + currentDepth);       // OK
        metaPropertiesSuper.put("CurrentNumSteps", "" + currentNumSteps); // OK
        metaPropertiesSuper.put("CurrentNumNodes", "" + currentNumNodes); // OK
        metaPropertiesSuper.put("EndDepth", "" + endDepth);               // OK
        metaPropertiesSuper.put("EndNumSteps", "" + endNumSteps);         // OK
        metaPropertiesSuper.put("EndNumNodes", "" + endNumNodes);         // OK
        metaPropertiesSuper.put("RawTruthValues", "" + rawTruthValues);   // OK
        
        return metaPropertiesSuper;
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    6, 
                    3, 
                    2, 
                    "http://www.dasinfobuch.de/links/Binary-Decision-Diagram.html",
                    "http://info2.aifb.kit.edu/qa/index.php?qa=359&qa_1=band-ii-kapitel-3",
                    english
                    );
        }

        return "";
    }
    
    @Override
    public HashMap<String, String> getMethodNameAbbreviations() {
        HashMap<String, String> methodNameAbbreviations = super.getMethodNameAbbreviations();
        methodNameAbbreviations.put("simp", SIMPLIFY_ONE_STEP_METHOD_NAME);
        methodNameAbbreviations.put("truthTableLatex", TRUTH_TABLE_LATEX_METHOD_NAME);
        methodNameAbbreviations.put("truthTableJava", TRUTH_TABLE_JAVA_PDF_METHOD_NAME);
        methodNameAbbreviations.put("animate", METHOD_NAME_ANIMATE_BDD_GENERATION);
        return methodNameAbbreviations;
    }
}
