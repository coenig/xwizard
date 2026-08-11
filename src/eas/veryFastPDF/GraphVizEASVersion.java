/*
 * File name:        GraphViz.java (package eas.veryFastPDF.pdfProcessors)
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

package eas.veryFastPDF;

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
import eas.startSetup.SingleParameter;

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
public class GraphVizEASVersion {

    public static final String edgeSymbolDirected = "->";
    public static final String edgeSymbolUnDirected = "--";
    
    /**
     * The directory where temporary files will be created.
     */
     public static String GraphVizTEMPDIR = null; // Windows

    public static String getGraphVizTEMPDIR() {
        return GraphVizTEMPDIR;
    }

    /**
     * Where is your dot program located? It will be called externally.
     */
     public static String dotFile = null;

    /**
     * The source of the graph written in dot language.
     */
     public StringBuilder graph = new StringBuilder();
    
     public static final FileLocationEstimator ESTIMATOR_DOT_PATH = new FileLocationEstimator(
            ".*program.*", ".*graphviz.*", "bin", ".*dot.exe.*");
    
    /**
     * Constructor: creates a new GraphViz object that will contain a graph.
     * 
     * Note that this constructor will prompt for dot.exe location if it is
     * not already stored in tempDir.
     */
    public GraphVizEASVersion() {
        dotFile = ExternalFilePathsManager.retrieveDOTPath(false).getAbsolutePath();
    }

    /**
     * Note that we don't need to call this manually in a regular EAS run as the 
     * temp dir should be set automatically by the ParCollection. Just set this 
     * Class as the corresponding {@link SingleParameter}'s listener parameter. 
     * See the {@code GraphVizTEMPDIR} parameter in {@link EnvironmentEA#getParameters()} 
     * as an example for this. 
     */
    public static void setGraphVizTEMPDIR(String tempDir) {
        GraphVizTEMPDIR = tempDir;

        if (tempDir == null) {
            return; // Don't check temp dir if null (should never happen in automatic assignment).
        }
        
        File tempDirFile = new File(GraphVizTEMPDIR);
        
        if (!tempDirFile.exists()) {
            GlobalVariables.getParameters().logInfo("<" + GraphVizEASVersion.class.getSimpleName() + "> Directory '" + tempDir + "' does not exist. I will create it.");
            tempDirFile.mkdirs();
        }
    }

    /**
     * Returns the graph's source description in dot language.
     * 
     * @return Source of the graph in dot language.
     */
    public String getSourceString() {
        return graph.toString();
    }
    
    public String getCodePrefix() {
        return "dot:";
    }

    /**
     * Adds a string to the graph's source (without newline).
     */
    public void add(String line) {
        graph.append(line);
    }

    /**
     * Adds a string to the graph's source (with newline).
     */
    public void addln(String line) {
        graph.append(line + "\n");
    }

    /**
     * Adds a newline to the graph's source.
     */
    public void addln() {
        addln("");
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
    @SuppressWarnings("null")
    private byte[] get_img_stream(File dot, String type) {
        File img;
        byte[] img_stream = null;

        try {
            img = File.createTempFile("graph_", "." + type, new File(GraphVizTEMPDIR));
            Runtime rt = Runtime.getRuntime();

            // patch by Mike Chenault
            String[] args = { dotFile, "-T" + type, dot.getAbsolutePath(), "-o",
                    img.getAbsolutePath() };
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
                            + GraphVizTEMPDIR + "\n");
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
            img = File.createTempFile("graph_", "." + "svg", new File(GraphVizTEMPDIR));
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
                            + GraphVizTEMPDIR + "\n");
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
    @SuppressWarnings("unused")
    private File writeDotSourceToFile(String str) throws java.io.IOException {
        File temp;
        try {
            temp = File.createTempFile("graph_", ".dot.tmp", new File(GraphVizTEMPDIR));
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
    
    public void storeAsPDF(final String datNam, String tempDir) {
        // Create preprocessor files on PDF processor level (not recommended anymore).
        String type = "pdf";
        writeGraphToFile(datNam, tempDir, type);
    }

    public void writeGraphToFile(final String datNam, String tempDir,
            String type) {
        File out = new File(tempDir + "/" + datNam + "." + type);
        this.writeGraphToFile(this.getGraph(this.getSourceString(), type), out);
    }
    
    public void resetDotSource() {
        this.graph = new StringBuilder();
    }

    public int getCodeSizeToBeConsideredLarge() {
        return 30_000;
    }
    
    public static String minimalGraph() {
        return startDigraph() + "\n" + endGraph();
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
        this.writeGraphToFile(datNam, GraphVizTEMPDIR, "plain");
        LinkedList<String> lines = StaticMethods.readTextArrayFromFile(GraphVizTEMPDIR, datNam + ".plain", null);
        
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
}