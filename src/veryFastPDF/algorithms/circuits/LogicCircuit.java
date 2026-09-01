/*
 * File name:        LogicCircuit.java (package veryFastPDF.algorithms.circuits)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  17.11.2015 (10:31:53)
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

package veryFastPDF.algorithms.circuits;

import java.util.HashMap;
import java.util.Random;

import eas.math.geometry.Vector2D;
import eas.miscellaneous.StaticMethods;
import mainServlet.WebLink;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.plugin.VFPWindow;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;
import veryFastPDF.VFPVariables;

/**
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class LogicCircuit extends RepresentableDefault {

    private static final Object FLIFLOP_NAME_RS = "RSFF";
    private static final String FLIFLOP_NAME_JK = "JKFF";
    private static final long serialVersionUID = -73696259283737489L;
    private static final String SCRIPT_PREFIX = "circuit:";
    
    private String components = "#none:none#";

    private HashMap<String, String> allComps = new HashMap<>();
    private HashMap<String, Gate> allGates = new HashMap<>();
    private static final HashMap<String, String> COMP_MAP_EUR_STATIC = new HashMap<>();
    private HashMap<String, Vector2D> gateCoordinates;
    
    private boolean perpendicularEdges = true;
    private int seed = 0;
    
    @SuppressWarnings("unused")
    private boolean european = true;
    
    public LogicCircuit(Exercise exercise) {
        super(exercise);
    }
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {
                SCRIPT_PREFIX + "\n" + 
                        "a => n | x1.2;\n" + 
                        "b => x2.1 | a2.1;\n" + 
                        "u => a2.2 | x2.2;\n" + 
                        "n => a1.2;\n" + 
                        "x2 => a1.1 | x1.1;\n" + 
                        "a1 => o.2;\n" + 
                        "a2 => o.1;\n" + 
                        "o => U;\n" + 
                        "x1 => E;\n" + 
                        "--declarations--\n" + 
                        "e=#n#;\n" + 
                        "components=n:NOT,x1:XOR,x2:XOR,a1:AND,a2:AND,o:OR;\n" + 
                        "perpendicularEdges=false\n" + 
                        "--declarations-end--"
        };
    }

    @Override
    public boolean isAcceptableScript(String script) {
        return script != null && script.startsWith(SCRIPT_PREFIX);
    }
    
    static {
        COMP_MAP_EUR_STATIC.put("AND", "and gate, inputs={nn}");
        COMP_MAP_EUR_STATIC.put("AND3", "and gate, inputs={nnn}");
        COMP_MAP_EUR_STATIC.put("AND4", "and gate, inputs={nnnn}");
        COMP_MAP_EUR_STATIC.put("AND5", "and gate, inputs={nnnnn}");
        COMP_MAP_EUR_STATIC.put("AND6", "and gate, inputs={nnnnnn}");
        COMP_MAP_EUR_STATIC.put("AND7", "and gate, inputs={nnnnnnn}");
        COMP_MAP_EUR_STATIC.put("AND8", "and gate, inputs={nnnnnnnn}");
        COMP_MAP_EUR_STATIC.put("NAND", "nand gate, inputs={nn}");
        COMP_MAP_EUR_STATIC.put("NAND3", "nand gate, inputs={nnn}");
        COMP_MAP_EUR_STATIC.put("NAND4", "nand gate, inputs={nnnn}");
        COMP_MAP_EUR_STATIC.put("NAND5", "nand gate, inputs={nnnnn}");
        COMP_MAP_EUR_STATIC.put("NAND6", "nand gate, inputs={nnnnnn}");
        COMP_MAP_EUR_STATIC.put("NAND7", "nand gate, inputs={nnnnnnn}");
        COMP_MAP_EUR_STATIC.put("NAND8", "nand gate, inputs={nnnnnnnn}");
        COMP_MAP_EUR_STATIC.put("OR", "or gate, inputs={nn}");
        COMP_MAP_EUR_STATIC.put("OR3", "or gate, inputs={nnn}");
        COMP_MAP_EUR_STATIC.put("OR4", "or gate, inputs={nnnn}");
        COMP_MAP_EUR_STATIC.put("OR5", "or gate, inputs={nnnnn}");
        COMP_MAP_EUR_STATIC.put("OR6", "or gate, inputs={nnnnnn}");
        COMP_MAP_EUR_STATIC.put("OR7", "or gate, inputs={nnnnnnn}");
        COMP_MAP_EUR_STATIC.put("OR8", "or gate, inputs={nnnnnnnn}");
        COMP_MAP_EUR_STATIC.put("XOR", "xor gate, inputs={nn}");
        COMP_MAP_EUR_STATIC.put("XOR3", "xor gate, inputs={nnn}");
        COMP_MAP_EUR_STATIC.put("XOR4", "xor gate, inputs={nnnn}");
        COMP_MAP_EUR_STATIC.put("XOR5", "xor gate, inputs={nnnnn}");
        COMP_MAP_EUR_STATIC.put("XOR6", "xor gate, inputs={nnnnnn}");
        COMP_MAP_EUR_STATIC.put("XOR7", "xor gate, inputs={nnnnnnn}");
        COMP_MAP_EUR_STATIC.put("XOR8", "xor gate, inputs={nnnnnnnn}");
        COMP_MAP_EUR_STATIC.put("NOR", "nor gate, inputs={nn}");
        COMP_MAP_EUR_STATIC.put("NOR3", "nor gate, inputs={nnn}");
        COMP_MAP_EUR_STATIC.put("NOR4", "nor gate, inputs={nnnn}");
        COMP_MAP_EUR_STATIC.put("NOR5", "nor gate, inputs={nnnnn}");
        COMP_MAP_EUR_STATIC.put("NOR6", "nor gate, inputs={nnnnnn}");
        COMP_MAP_EUR_STATIC.put("NOR7", "nor gate, inputs={nnnnnnn}");
        COMP_MAP_EUR_STATIC.put("NOR8", "nor gate, inputs={nnnnnnnn}");
        COMP_MAP_EUR_STATIC.put("NOT", "not gate");
    }
    
    private static String getGateMapping(String gateName) {
        String[] split = null;
    
         try {
            split = StaticMethods.removeWhitespaces(gateName).split("-");
        } catch (Exception e) {
        }
        
        if (split == null || split.length < 2) {
            return COMP_MAP_EUR_STATIC.get(gateName);
        }
        
        return split[0].toLowerCase() + " gate, inputs={" + split[1] + "}";
    }
    
    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(script, father, 0);
        String code = this.getScriptWithoutPrepAndDeclAndPreamble();
        code = this.decollapseRules(code);

        for (String s : StaticMethods.removeWhitespaces(components).split(",")) {
            String[] split = s.split(":");
            allComps.put(split[0], split[1]);
        }
        
        allGates = new HashMap<>();
        
        for (String s : StaticMethods.removeWhitespaces(code).split(";")) {
            String[] ruleSplit = s.split("=>");
            String[] rightSplit = ruleSplit[1].split("\\.");
            String nameLeft = ruleSplit[0];
            String nameRight = rightSplit[0];
            int inputRight;
            
            if (rightSplit.length == 1) {
                inputRight = -1;
            } else {
                inputRight = Integer.parseInt(rightSplit[1]);
            }
            
            if (!allGates.containsKey(nameLeft)) {
                allGates.put(nameLeft, new Gate(nameLeft));
            }
            if (!allGates.containsKey(nameRight)) {
                allGates.put(nameRight, new Gate(nameRight));
            }
            
            Gate gl = allGates.get(nameLeft);
            Gate gr = allGates.get(nameRight);
            
            Input i = new Input(gr, inputRight);
            gl.getOutput().addConnection(i);
            gr.getInputList().put(inputRight, i);
            i.addConnection(gl.getOutput());
        }

        // Create graph.
        String tempDir = WebLink.getWORKING_DIRECTORY() == null 
                ? VFPWindow.getSINGLETON_INSTANCE().getWorkingDirectory().getAbsolutePath()
                : WebLink.getWORKING_DIRECTORY();
                
        GraphViz gv = new GraphViz(tempDir, this);
        gv.addln(GraphViz.startDigraph());
        gv.addln("rankdir=\"LR\";\n");
        gv.addln("graph [pad=\"1.75\", ranksep=\"1.25\", nodesep=\"1.25\"];");
        
        for (Gate g1 : this.allGates.values()) {
            for (Input i : g1.getOutput().getConnections()) {
                Gate g2 = i.getGate();
                gv.addln(g1.getName() + " -> " + g2.getName() + " [" + "shape=rectangle" + "];");
            }
        }
        
        gv.addln("}");
        
        gateCoordinates = gv.getNodePositions();
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        Random random = new Random(this.seed);
        String nodePositions = "";
        String edges = "";
        int cnt = 0;
        
        for (String name : this.gateCoordinates.keySet()) {
            Vector2D coord = this.gateCoordinates.get(name);
            String typeRaw = this.allComps.get(name);
            String type = getGateMapping(typeRaw);
            String latexName = name.length() > 1 ? name.charAt(0) + "_" + name.substring(1) : name;
            
            if (type == null) {
                type = "";
            }
            
            if (typeRaw != null && typeRaw.equals(FLIFLOP_NAME_JK)) {
                nodePositions += "\\JKFF("
                        + coord.x
                        + ","
                        + (coord.y - 0.5)
                        + "){" + name + "}{Q}";
            } else if (typeRaw != null && typeRaw.equals(FLIFLOP_NAME_RS)) {
                    nodePositions += "\\RSFF("
                            + coord.x
                            + ","
                            + (coord.y - 0.5)
                            + "){" + name + "}{Q}";
            } else {
                nodePositions += "\\node[" + "label={below:$" + latexName + "$}, "
                        + type 
                        + "] (" 
                        + name 
                        + ") at ("
                        + coord.x
                        + ","
                        + coord.y
                        + ") {};\n";
            }
        }

        for (Gate g1 : this.allGates.values()) {
            for (Input i : g1.getOutput().getConnections()) {
                Gate g2 = i.getGate();
                boolean typeRaw1IsFF = FLIFLOP_NAME_JK.equals(this.allComps.get(g1.getName()))
                        || FLIFLOP_NAME_RS.equals(this.allComps.get(g1.getName()));
                boolean typeRaw2IsFF = FLIFLOP_NAME_JK.equals(this.allComps.get(g2.getName()))
                        || FLIFLOP_NAME_RS.equals(this.allComps.get(g2.getName()));
                
                String rect = perpendicularEdges ? "|-" : "--";
            
                int getiNum = i.getiNum();
                String inputName = g2.getName() + "i" + (getiNum >= 0 ? getiNum : "");
                
                edges += "  \\coordinate [label=left:$~$] (" 
                        + inputName + ") at ($ ("
                        + g2.getName()
                        + (typeRaw2IsFF ? "" : ".input")
                        + (getiNum >= 0 ? " " + getiNum : "")
                        + ") - (0.3, 0)$);\n";

                Vector2D coord1 = this.gateCoordinates.get(g1.getName());
                Vector2D coord2 = this.gateCoordinates.get(g2.getName());
                
                // Backwards edge.
                if (coord1.x > coord2.x) {
                    cnt++;
                    String coordV;
                    String coordE = "\\coordinate [label=left:$~$] (be" + cnt + ") at ($(current bounding box.east)+(0.3, 0)$);";
                    
                    if (coord1.y >= coord2.y) {
                        coordV = "\\coordinate [label=left:$~$] (bv" + cnt + ") at ($(current bounding box.south)+(0,0.3)$);";
                    } else {
                        coordV = "\\coordinate [label=left:$~$] (bv" + cnt + ") at ($(current bounding box.north)+(0,0.3)$);";
                    }
                    
                    edges += coordE + "\n" + coordV + "\n";

                    edges += "  \\draw let \\p1 = (bv" + cnt 
                            + "), \\p2 = (" + g1.getName() 
                            + (typeRaw1IsFF ? " " : ".")
                            + "east), \\p3 = (be" + cnt + ") in (" 
                            + g1.getName() 
                            + (typeRaw1IsFF ? " " : ".")
                            + "east) -- (\\x3, \\y2) |- (\\x2, \\y1) -|" 
                            + " ("
                            + inputName
                            + ") " + rect
                            + " ("
                            + g2.getName()
                            + (typeRaw2IsFF ? "" : ".input")
                            + (getiNum >= 0 ? " " + getiNum : "")
                            + ");\n";
                } else {
                    int width = this.perpendicularEdges ? (random.nextInt(7) + 1) : 3;
                    
                    edges += "  \\draw (" 
                            + g1.getName() 
                            + (typeRaw1IsFF ? " " : ".")
                            + "east) -- ++(right:"
                            + width
                            + "mm) " + rect 
                            + " ("
                            + inputName
                            + ") " + rect
                            + " ("
                            + g2.getName()
                            + (typeRaw2IsFF ? "" : ".input")
                            + (getiNum >= 0 ? " " + getiNum : "")
                            + ");\n";
                }
            }
        }
        
        return new LaTeXPDF(
                LaTeXCommands.PREAMBLE_TIKZ_EUR
                    + nodePositions
                    + edges
                    + LaTeXCommands.POSTAMBLE_TIKZ, 
                pdfPath,
                this);
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }

    @Override
    public String getGermanName() {
        return "Schaltkreis";
    }

    @Override
    public String createScriptFromInstance() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    6, 
                    3, 
                    2, 
                    "http://www.dasinfobuch.de/links/Schaltnetze-Schaltwerke.html",
                    BASE_QA_ADDRESS + "?qa=357&qa_1=band-ii-kapitel-1",
                    english
                    );
        }

        return "";
    }
}
