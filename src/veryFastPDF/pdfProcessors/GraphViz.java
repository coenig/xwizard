/*
 * File name:        GraphViz.java (package veryFastPDF.pdfProcessors)
 * Author(s):        Lukas König, Laszlo Szathmary (see below)
 * Java version:     8.0 (at generation time)
 * Generation date:  02.11.2014 (17:37:58)
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. See also original license below (LGPL). 
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

// GraphViz.java - a simple API to call dot from Java programs

/*$Id$*/
/*
 ******************************************************************************
 *                                                                            *
 *              (c) Copyright 2003 Laszlo Szathmary                           *
 *                                                                            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation; either version 2.1 of the License, or        *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public    *
 * License for more details.                                                  *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program; if not, write to the Free Software Foundation,    *
 * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                              *
 *                                                                            *
 ******************************************************************************
 */

package veryFastPDF.pdfProcessors;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

import eas.GlobalVariables;
import eas.math.geometry.Vector2D;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.ExternalFilePathsManager;
import eas.miscellaneous.convenience.FileLocationEstimator;
import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import veryFastPDF.VFPVariables;
import veryFastPDF.algorithms.plainDOT.Graphviz;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.ConvenienceMethods;

/**
 * <dl>
 * <dt>Purpose: GraphViz Java API
 * <dd>
 * 
 * <dt>Description:
 * <dd>With this Java class you can simply call dot from your Java programs
 * <dt>Example usage:
 * <dd>
 * 
 * <pre>
 * GraphViz gv = new GraphViz();
 * gv.addln(gv.start_graph());
 * gv.addln(&quot;A -&gt; B;&quot;);
 * gv.addln(&quot;A -&gt; C;&quot;);
 * gv.addln(gv.end_graph());
 * "syso"(gv.getDotSource());
 * 
 * String type = &quot;gif&quot;;
 * File out = new File(&quot;out.&quot; + type); // out.gif in this example
 * gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
 * </pre>
 * 
 * </dd>
 * 
 * </dl>
 * 
 * @version v0.4, 2011/02/05 (February) -- Patch of Keheliya Gallaba is added.
 *          Now you can specify the type of the output file: gif, dot, fig, pdf,
 *          ps, svg, png, etc.
 * @version v0.3, 2010/11/29 (November) -- Windows support + ability to read the
 *          graph from a text file
 * @version v0.2, 2010/07/22 (July) -- bug fix
 * @version v0.1, 2003/12/04 (December) -- first release
 * @author Laszlo Szathmary (<a
 *         href="jabba.laci@gmail.com">jabba.laci@gmail.com</a>)
 */
public class GraphViz extends PDFProcessor {

    public static final String edgeSymbolDirected = "->";
    public static final String edgeSymbolUnDirected = "--";

    public static String correctAnswer(String solCode) {
        return "dot:\n" 
                + "    digraph G {rankdir=RL;" 
                + "bgcolor=\"white:white\" penwidth=\"1px\" style=\"radial\"\n" 
                + (solCode == null ? "" : "a1 [label=<Code: " + solCode + "> fontcolor=\"black\" fillcolor=\"transparent\"]; \n") 
                + "        subgraph cluster1 {fillcolor=\"green:yellow\" style=\"filled\" \n" 
                + "            node [shape=ellipse style=\"radial\" penwidth=\"0px\"] \n" 
                + "                a0 [label=<Aufgabe abgeschlossen!> fontcolor=\"black\" fillcolor=\"transparent\"]; \n" 
                + "        }\n" 
                + "    }";
    }

    public static final String incorrectAnswer(String backURL) { 
        return "dot:\n" 
            + "    digraph G {rankdir=RL;"
            + "bgcolor=\"white:white\" penwidth=\"1px\" style=\"radial\"\n" 
            + (backURL == null ? "" : "a1 [label=\"Zur Aufgabe\", URL=\"" + backURL + "\", fontcolor=\"black\", fillcolor=\"transparent\"]; \n") 
            + "        subgraph cluster1 {fillcolor=\"red:orange\" style=\"filled\" \n" 
            + "            node [shape=ellipse style=\"radial\" penwidth=\"0px\"] \n" 
            + "                a0 [label=<Leider falsch.> fontcolor=\"black\" fillcolor=\"transparent\"]; \n" 
            + "        }\n"
            + "    }";
    }

    public static final String XWIZZ_WELCOME_MESSAGE = "dot:\n" + 
            "    digraph G {bgcolor=\"blue:black\" penwidth=\"1px\" style=\"radial\"\n" + 
            "        subgraph cluster1 {fillcolor=\"lightblue:white\" style=\"filled\" \n" + 
            "            node [shape=ellipse style=\"radial\"] \n" + 
            "                a0 [label=<" + VFPVariables.PROG_NAME_XWIZZ + "!> fontcolor=\"black\" fillcolor=\"transparent\"]; \n" + 
            "        }\n" + 
            "    }";
    
    /**
     * The dir. where temporary files will be created.
     */
     private String tempDirectory = null; // Windows

    /**
     * Where is your dot program located? It will be called externally.
     */
     private static String dotFile = null; // Windows

    /**
     * The source of the graph written in dot language.
     */
    private StringBuilder graph = new StringBuilder();
    
    private boolean writeProtected;

    private static FileLocationEstimator estimator = new FileLocationEstimator(
            ".*program.*", ".*graphviz.*", "bin", ".*dot.exe.*");
    
    /**
     * Constructor: creates a new GraphViz object that will contain a graph.
     * 
     * Note that this constructor will prompt for dot.exe location if it is
     * not already stored in tempDir.
     */
    public GraphViz(String tempDir, RepresentableAsPDF rep) {
        super(rep);
        
        if (tempDir == null) {
            writeProtected = true;
        } else {
            assignTempDir(tempDir);
            
            if (MainLink.isApplicationOriginDesktop()) {
                dotFile = ExternalFilePathsManager.retrieveExternalFilePath(ExternalFilePathsManager.PATH_TO_GRAPHVIZ_ID, true,
                        "Choose the path to 'dot.exe' which is the graphViz executable file.\n"
                        + "It is available in your '*program files*/graphViz/bin' folder after installation of the graphViz package in the 'install' folder.\n \n"
                        + "[You can get the newest version from www.graphviz.org]",
                        estimator,
                        WebLink.CONF_FILE,
                        WebLink.pathDOT_POS).getAbsolutePath();
            } else {
                try {
                    dotFile = WebLink.getDOTPath();
                } catch (Exception e) {
                    throw new RuntimeException("Configuration file '" + WebLink.CONF_FILE.getAbsolutePath() + "' does not contain a valid "
                            + "path to dot.exe.");
                }
            }
        }
    }

    public void assignTempDir(String tempDir) {
        this.tempDirectory = tempDir;

        if (tempDir == null) {
            return; // Don't assign temp dir.
        }
        
        File tempDirFile = new File(tempDir);
        
        if (!tempDirFile.exists()) {
            GlobalVariables.getParameters().logInfo("Directory " + tempDir + " does not exist. I will create it.");
            tempDirFile.mkdirs();
        }
    }

    /**
     * Returns the graph's source description in dot language.
     * 
     * @return Source of the graph in dot language.
     */
    @Override
    public String getSourceString() {
        return graph.toString();
    }
    
    @Override
    public String getCodePrefix() {
        return "dot:";
    }

    /**
     * Adds a string to the graph's source (without newline).
     */
    public void add(String line) {
        if (!this.writeProtected) {
            graph.append(line);
        }
    }

    /**
     * Adds a string to the graph's source (with newline).
     */
    @Override
    public void addln(String line) {
        if (!this.writeProtected) {
            graph.append(line + "\n");
        }
    }

    /**
     * Adds a newline to the graph's source.
     */
    public void addln() {
        if (!this.writeProtected) {
            addln("");
        }
    }

    /**
     * Returns the graph as an image in binary format.
     * 
     * @param dot_source
     *            Source of the graph to be drawn.
     * @param type
     *            Type of the output image to be produced, e.g.: gif, dot, fig,
     *            pdf, ps, svg, png.
     * @return A byte array containing the image of the graph.
     */
    public byte[] getGraph(String dot_source, String type) {
        File dot;
        byte[] img_stream = null;

        try {
            dot = writeDotSourceToFile(dot_source);
            if (dot != null) {
                img_stream = get_img_stream(dot, type);
                if (dot.delete() == false)
                    System.err.println("Warning: " + dot.getAbsolutePath()
                            + " could not be deleted!");
                return img_stream;
            }
            return null;
        } catch (java.io.IOException ioe) {
            throw new RuntimeException();
        }
    }

    /**
     * Returns the graph as an image in text format.
     * 
     * @param dot_source
     *            Source of the graph to be drawn.
     * @param type
     *            Type of the output image to be produced, e.g.: gif, dot, fig,
     *            pdf, ps, svg, png.
     * @return A String containing the image of the graph in SVG format.
     */
    public String getPlainGraphSVG(String dot_source) {
        File dot;

        try {
            dot = writeDotSourceToFile(dot_source);
            if (dot != null) {
                String text = getImgText(dot);
                return text;
            }
            return null;
        } catch (java.io.IOException ioe) {
            return null;
        }
    }

    /**
     * Writes the graph's image in a file.
     * 
     * @param img
     *            A byte array containing the image of the graph.
     * @param file
     *            Name of the file to where we want to write.
     * @return Success: 1, Failure: -1
     */
    public int writeGraphToFile(byte[] img, String file) {
        File to = new File(file);
        return writeGraphToFile(img, to);
    }

    /**
     * Writes the graph's image in a file.
     * 
     * @param img  A byte array containing the image of the graph.
     * @param to   A File object to where we want to write.
     * @return Success: 1, Failure: -1
     */
    public int writeGraphToFile(byte[] img, File to) {
//        GlobalVariables.getParameters().logError("Graphviz: " + to);

        try {
            FileOutputStream fos = new FileOutputStream(to);
            fos.write(img);
            fos.close();
        } catch (java.io.IOException ioe) {
            throw new RuntimeException("Graph not written: " + ioe);
        }
        return 1;
    }

    /**
     * It will call the external dot program, and return the image in binary
     * format.
     * 
     * @param dot   Source of the graph (in dot language).
     * @param type  Type of the output image to be produced, e.g.: gif, dot, fig,
     *              pdf, ps, svg, png.
     * @return The image of the graph in .gif format.
     */
    private byte[] get_img_stream(File dot, String type) {
        File img;
        byte[] img_stream = null;

        try {
            img = File.createTempFile("graph_", "." + type, new File(tempDirectory));
            Runtime rt = Runtime.getRuntime();

            // patch by Mike Chenault
            String[] args = {dotFile, "-T" + type, dot.getAbsolutePath(), "-o", img.getAbsolutePath()};
            Process p = rt.exec(args);

            p.waitFor();

            FileInputStream in = new FileInputStream(img.getAbsolutePath());
            img_stream = new byte[in.available()];
            in.read(img_stream);
            // Close it if we need to
            if (in != null)
                in.close();

            if (img.delete() == false)
                System.err.println("Warning: " + img.getAbsolutePath()
                        + " could not be deleted!");
        } catch (java.io.IOException ioe) {
            System.err
                    .println("Error:    in I/O processing of tempfile in dir "
                            + tempDirectory + "\n");
            System.err.println("       or in calling external command");
            ioe.printStackTrace();
        } catch (java.lang.InterruptedException ie) {
            System.err
                    .println("Error: the execution of the external program was interrupted");
            ie.printStackTrace();
        }

        return img_stream;
    }

    /**
     * It will call the external dot program, and return the image in plain text
     * format (SVG image format only).
     * 
     * @param dot   Source of the graph (in dot language).
     * @return The image of the graph in text format.
     */
    private String getImgText(File dot) {
        File img;

        try {
            img = File.createTempFile("graph_", "." + "svg", new File(tempDirectory));
            Runtime rt = Runtime.getRuntime();

            // patch by Mike Chenault
            String[] args = { dotFile, "-T" + "svg", dot.getAbsolutePath(), "-o",
                    img.getAbsolutePath() };
            Process p = rt.exec(args);

            p.waitFor();

            String text = StaticMethods.readTextFromFile(img, null);
            return text;
        } catch (java.io.IOException ioe) {
            System.err
                    .println("Error:    in I/O processing of tempfile in dir "
                            + tempDirectory + "\n");
            System.err.println("       or in calling external command");
            ioe.printStackTrace();
        } catch (java.lang.InterruptedException ie) {
            System.err
                    .println("Error: the execution of the external program was interrupted");
            ie.printStackTrace();
        }

        return null;
    }
    
    /**
     * Writes the source of the graph in a file, and returns the written file as
     * a File object.
     * 
     * @param str
     *            Source of the graph (in dot language).
     * @return The file (as a File object) that contains the source of the
     *         graph.
     */
    private File writeDotSourceToFile(String str) throws java.io.IOException {
        File temp;
        try {
            temp = File.createTempFile("graph_", ".dot.tmp", new File(tempDirectory));
            FileWriter fout = new FileWriter(temp);
            fout.write(str);
            fout.close();
        } catch (Exception e) {
            throw new RuntimeException("Error: I/O error while writing the dot source to temp file -- " + e.getMessage());
        }
        return temp;
    }

    /**
     * Returns a string that is used to start a digraph.
     * 
     * @return A string to open a digraph.
     */
    public static String startDigraph() {
        return "digraph G {";
    }
    
    /**
     * Returns a string that is used to start a graph.
     * 
     * @return A string to open a graph.
     */
    public static String startGraph() {
        return "graph G {";
    }

    /**
     * Returns a string that is used to start a graph.
     * 
     * @return A string to open a graph.
     */
    public static String startStrictGraph() {
        return "strict graph {";
    }

    /**
     * Returns a string that is used to end a graph.
     * 
     * @return A string to close a graph.
     */
    public static String endGraph() {
        return "}";
    }

    /**
     * Read a DOT graph from a text file.
     * 
     * @param input
     *            Input text file containing the DOT graph source.
     */
    public void readSource(String input) {
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fis = new FileInputStream(input);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            dis.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        this.graph = sb;
    }

    @Override
    public String toString() {
        return this.graph.toString();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void storeAsPDF(final String datNam, String tempDir) {
        // Create preprocessor files on PDF processor level (not recommended anymore).
        this.applyPreprocessors(tempDir);

        String type = "pdf";
        writeGraphToFile(datNam, tempDir, type);
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public String getSVGCode(final String fileName, String workingDir, String script, int hash) {
        // Create preprocessor files on PDF processor level (not recommended anymore).
        this.applyPreprocessors(workingDir);

        storeAsPDF(fileName, workingDir);

        String pathToStoreSVG = new File(workingDir).getPath() + File.separator;
        if (this.createSVGFileFromAnimationInstructions(pathToStoreSVG, true, hash)) {
            return StaticMethods.readTextFromFile(
                    new File(pathToStoreSVG + WebLink.fileName(RepresentableDefault.THIS_NAME) + ".svg"), 
                    GlobalVariables.getParameters());
        } else {
            return WebLink.setSVGWidth(this.getPlainGraphSVG(script));
        }
    }

    private void writeGraphToFile(final String datNam, String tempDir, String type) {
        File out = new File(tempDir + "/" + datNam + "." + type);
        this.writeGraphToFile(this.getGraph(this.getSourceString(), type), out);
    }
    
    public void resetDotSource() {
        this.graph = new StringBuilder();
    }

    @Override
    public int getCodeSizeToBeConsideredLarge() {
        return 30_000;
    }

    @Override
    public Class<? extends RepresentableAsPDF> getPlainRepresentableClass() {
        return Graphviz.class;
    }

    public static final String INFO_ABOUT_TOO_LONG_SCRIPTS_AND_OPS = 
            "Download " + VFPVariables.PROG_NAME_XWIZZ + " desktop version ('" 
            + VFPVariables.PROG_NAME_PDF_GEN 
            + "') for more powerful calculations";
    
    @Override
    public String safetyCodeInCaseOfLargeCodeOrLongOperation(int codeSize) {
        return GraphViz.startDigraph() + "\nrankdir=LR;\n" 
                + "\"" + "a1" + "\" [label=<<U>" + INFO_ABOUT_TOO_LONG_SCRIPTS_AND_OPS + "</U>> URL=\"" + VFPVariables.LINK_TO_VFP + "\" shape=\"none\"];\n" 
                + "\"Sorry, " + this.getClass().getSimpleName() + " code too large (" + codeSize + " characters) or operation too long\"; \n"
                + GraphViz.endGraph();
    }

    public String safetyCodeForExceptionCase(String ex) {
        return GraphViz.startDigraph() + "\nrankdir=LR;\n" 
                + "\"" + "a1" + "\" [label=\"Exception: " + ex + "\"];\n" 
                + GraphViz.endGraph();
    }

    public void setWriteProtected(boolean protect) {
        this.writeProtected = protect;
    }
    
    public static String minimalGraph() {
        return startDigraph() + "\n" + endGraph();
    }

    /**
     * Call this method MANUALLY to replace special characters according to
     * graphviz rules. Insert new specials if desired.
     * 
     * @param sString  The String to replace special characters in.
     * @return  The cleaned-up string.
     */
    public static String replaceSpecialChars(String sString) {
        String ssString = ConvenienceMethods.replaceSpecialCharsHTML_G(
                sString.replace("XplusX", "&#043;")
                .replace("XsemX", "&#059;")
                .replace("XkaX", "&#040;")
                .replace("XkzX", "&#041;"));
        return ssString;
    }
    
    /**
     * Retrieves a map of the node positions as calculated by the graphviz run.
     * A temp file is created (and never deleted).
     * 
     * @return  A map NAME -> COORDINATE of node positions.
     */
    public HashMap<String, Vector2D> getNodePositions() {
        HashMap<String, Vector2D> coordinates = new HashMap<>();
        String datNam = "temp-gv-output";
        this.writeGraphToFile(datNam, this.tempDirectory, "plain");
        LinkedList<String> lines = StaticMethods.readTextArrayFromFile(this.tempDirectory, datNam + ".plain", null);
        
        for (String line : lines) {
            if (line.startsWith("node")) {
                String[] split = line.split(" ");
                String name = split[1];
                Vector2D coord = new Vector2D(Double.parseDouble(split[2]), Double.parseDouble(split[3]));
                coordinates.put(name, coord);
            }
        }
        
        return coordinates;
    }
    
    @Override
    public String getPreparedSourceString() {
        return this.getSourceString();
    }
}