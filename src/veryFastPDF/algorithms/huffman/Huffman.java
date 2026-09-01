/*
 * File name:        Huffman.java (package eas.fundamentalAlgorithms.graphBased.huffman)
 * Author(s):        Lukas König, Internet
 * Java version:     7.0
 * Generation date:  15.11.2013 (07:13:32)
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

package veryFastPDF.algorithms.huffman;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JComponent;

import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;
import veryFastPDF.VFPVariables;

/**
 * From the internet.
 * 
 * @author http://pmarques.dei.uc.pt/programs/huffman/Huffman.java.html
 */
@Webproof(useInProductiveMode = true)
public class Huffman extends RepresentableDefault {

    private static final long serialVersionUID = -8163430534846828075L;
    private HuffmanNode treeRoot;
    private String representedText = null;
    private boolean classicView = false;
    
    public Huffman(Exercise exercise) {
        super(exercise);
        super.addIgnoredField("representedText");
        this.setAllowCollapsingRules(false);
    }
    
    public HuffmanNode getRoot() {
        return this.treeRoot;
    }

    public HashMap<Character, String> getAllCodes() {
        return this.getAllCodes(this.treeRoot, new HashMap<Character, String>(), "");
    }
    
    private HashMap<Character, String> getAllCodes(HuffmanNode tree, HashMap<Character, String> currentMap, String currentPath) {
        if (tree != null) {
            if (tree.getLeft() == null && tree.getRight() == null) {
                currentMap.put(tree.getContent(), currentPath);
            }
            if (tree.getLeft() != null) {
                currentMap.putAll(getAllCodes(tree.getLeft(), currentMap, currentPath + "0"));
            }
            if (tree.getRight() != null) {
                currentMap.putAll(getAllCodes(tree.getRight(), currentMap, currentPath + "1"));
            }
        }
        
        return currentMap;
    }
    
    @Override
    public void createInstanceFromScript(String code, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(code, father, 0);
        this.representedText = this.getScriptWithoutPrepAndDeclAndPreamble().replace("\n", "");

        int[] frequency = new int[256]; // Frequency table of each
                                        // letter
        TreeSet<HuffmanNode> trees = new TreeSet<HuffmanNode>(); // List
                                                                 // containing
                                                                 // all trees --
                                                                 // ORDERED!

        // Build the frequency table of each letter
        for (int i = 0; i < this.representedText.length(); i++) {
            char ch = this.representedText.charAt(i);
            ++frequency[ch];
        }

        // Build up the initial trees
        for (int i = 0; i < 256; i++) {
            if (frequency[i] > 0) {
                HuffmanNode n = new HuffmanNode((char) (i), frequency[i]);
                trees.add(n);
            }
        }

        // Huffman algoritm
        while (trees.size() > 1) {
            HuffmanNode tree1 = trees.first();
            trees.remove(tree1);
            HuffmanNode tree2 = trees.first();
            trees.remove(tree2);

            HuffmanNode merged = new HuffmanNode(tree1, tree2);
            trees.add(merged);
        }

        this.treeRoot = trees.first();
    }

    public List<String> generateHuffmanGraphViz() {
        this.leafNodesGraphvizFormat.clear();
        
        return this.generateHuffmanGraphViz(
                this.getRoot(), 
                new LinkedList<String>(), 
                this.getAllCodes());
    }

    private HashSet<String> leafNodesGraphvizFormat = new HashSet<>();
    
    private List<String> generateHuffmanGraphViz(HuffmanNode currentNode, List<String> currentInstructions, HashMap<Character, String> map) {
        if (currentNode == null) {
            return currentInstructions;
        }  
        
        if (currentNode.getRight() == null && currentNode.getLeft() == null) {
            // Leaf nodes.
            currentInstructions.add(0, currentNode.shortToString()
                    + "[shape=record, label=\"{{" 
                    + currentNode.getContent() 
                    + "|" + currentNode.getValue() 
                    + "}|" + map.get(currentNode.getContent()) 
                    + "}\"]");
            leafNodesGraphvizFormat.add(currentNode.shortToString());
        } else {
            currentInstructions.add(0, currentNode.shortToString() + " [label=\"" + currentNode.getValue() + "\"];");
            currentInstructions.add(0, "node [shape = circle];");
        }
        
        String zero = "  0  ";
        String one = "  1  ";
        
        if (currentNode.getLeft() != null) {
            currentInstructions.add(
                    currentNode.shortToString() 
                        + " " 
                        + GraphViz.edgeSymbolDirected 
                        + " " 
                        + currentNode.getLeft().shortToString()
                        + "[label=\"" + zero + "\"]");
            
            generateHuffmanGraphViz(currentNode.getLeft(), currentInstructions, map);
        }
        
        if (currentNode.getRight() != null) {
            currentInstructions.add(
                    currentNode.shortToString() 
                        + " " 
                        + GraphViz.edgeSymbolDirected 
                        + " " 
                        + currentNode.getRight().shortToString()
                        + "[label=\"" + one + "\"]");
            generateHuffmanGraphViz(currentNode.getRight(), currentInstructions, map);
        }

        return currentInstructions;
    }

    public void createInstanceFromFrequencies(
            char[] characters,
            int[] frequencies) {
        String text = "";

        for (int i = 0; i < frequencies.length; i++) {
            for (int j = 0; j < frequencies[i]; j++) {
                text += characters[i];
            }
        }

        this.createInstanceFromScript(text, null);
    }
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {
                "huff:bauer-sucht-brauer",
                "huff:huffman-duffman"};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").split("\n")[0].toLowerCase().startsWith("huff:");
    }

    private boolean showBaseText = true;
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        GraphViz gv = createGraphvizTree(pdfPath);
        
        if (showBaseText) {
            LaTeXPDF latex = new LaTeXPDF(
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
                    + "\\bigbreak Distribution based on: "
                    + "\\begin{center}\\bf\\fbox{" + this.representedText + "}\\end{center}"
                    + "\n" + INSCR_BEG_TAG_FOR_INTERNAL_USAGE + "-1|"
                    + gv.getPlainPDFScript()
                    + INSCR_END_TAG_FOR_INTERNAL_USAGE + "\n"
                    + LaTeXCommands.POSTAMBLE_STANDARD, 
                    pdfPath,
                    this);
            
            return latex;
        }
        
        return gv;
    }

    public GraphViz createGraphvizTree(String pdfPath) {
        List<String> instructions = this.generateHuffmanGraphViz();
        
        GraphViz gv = new GraphViz(pdfPath, this);

        gv.addln(GraphViz.startDigraph());
        
        if (!this.classicView) {
            gv.addln("rankdir=BT;");
        }
        
        for (String s : instructions) {
            gv.addln(s);
        }
        
        if (!this.classicView) {
            gv.addln("{rank=same; " 
                    + this.leafNodesGraphvizFormat.toString().replace(", ", " ").replace("[", "").replace("]", "") 
                    + "};");
        }

        gv.add(GraphViz.endGraph());
        return gv;
    }

    @Override
    public JComponent getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }

    @Override
    public String getGermanName() {
        return "Huffman-Code";
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    7, 
                    4, 
                    2, 
                    "http://www.dasinfobuch.de/links/Kodierung-Verschl%C3%BCsselung.html",
                    BASE_QA_ADDRESS + "?qa=361&qa_1=band-ii-kapitel-4",
                    english
                    );
        }

        return "";
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        String className = this.getClass().getSimpleName();
        HashMap<String, String> metaProperties = super.getMetaProperties();
        String representedText2 = this.representedText == null ? "" : this.representedText;
        int depth = this.treeRoot == null ? 0 : this.treeRoot.depth();
        
        metaProperties.put(className + "_baseText", representedText2);
        metaProperties.put(className + "_length", representedText2.length() + "");
        metaProperties.put(className + "_uniqueCharacters", representedText2.chars().distinct().count() + "");
        metaProperties.put(className + "_classicView", this.classicView + "");
        metaProperties.put(className + "_treeDepth", depth + "");
        
        return metaProperties;
    }

    @Override
    public String createScriptFromInstance() {
        // TODO Auto-generated method stub
        return null;
    }
}