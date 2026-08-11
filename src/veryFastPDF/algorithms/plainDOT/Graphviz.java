/*
 * File name:        PlainDot.java (package eas.fundamentalAlgorithms.graphBased.plainDOT)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  03.12.2013 (15:56:11)
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

package veryFastPDF.algorithms.plainDOT;

import java.util.HashMap;

import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class Graphviz extends RepresentableDefault {
    
    public static final String END_DIGRAPH = "}";
    public static final String BEGIN_DIGRAPH = "digraph G {";
    public static final String GRAPHVIZ_DOT_PREAMBLE = "dot:";

    private String code = null;

    public Graphviz(Exercise exercise) {
        super(exercise);
        this.addIgnoredField("code");
        this.setAllowCollapsingRules(false);
    }

    public static final String SIMPLE_EXAMPLE = "\n" + BEGIN_DIGRAPH + "\n" 
            + "a -> b -> c;\n"
            + "b -> d;\n" 
            + "a [shape=polygon,sides=5,peripheries=3,color=lightblue,style=filled];\n"
            + "c [shape=polygon,sides=4,skew=.4,label=\"hello world\"]\n" 
            + "d [shape=invtriangle];\n"
            + "e [shape=polygon,sides=4,distortion=.7];\n" 
            + END_DIGRAPH;

    private static final long serialVersionUID = -9196223756496324176L;

    public static String BIG_EXAMPLE = BEGIN_DIGRAPH + " bgcolor=\"yellow:red\" \n"
            + "  subgraph cluster1 {fillcolor=\"blue:green\" style=\"filled\" \n"
            + "        node [shape=diamond fillcolor=\"gold:brown\" style=\"radial\" gradientangle=180] \n"
            + "        a0 [label=< \n"
            + " <TABLE border=\"10\" cellspacing=\"10\" cellpadding=\"10\" style=\"rounded\" bgcolor=\"/rdylgn11/1:/rdylgn11/11\" gradientangle=\"315\"> \n"
            + "    <TR><TD border=\"3\"  bgcolor=\"/rdylgn11/1:/rdylgn11/2\">00</TD> \n"
            + "    <TD border=\"3\"  bgcolor=\"/rdylgn11/2:/rdylgn11/3\">01</TD> \n"
            + "    <TD border=\"3\"  bgcolor=\"/rdylgn11/3:/rdylgn11/4\">02</TD> \n"
            + "    <TD border=\"3\"  bgcolor=\"/rdylgn11/4:/rdylgn11/5\">03</TD> \n"
            + "    </TR> \n" + " \n"
            + "    <TR><TD border=\"3\" bgcolor=\"/rdylgn11/1:/rdylgn11/6\" gradientangle=\"270\">10</TD> \n"
            + "    <TD border=\"3\" rowspan=\"2\"  bgcolor=\"/rdylgn11/3:/rdylgn11/9\" gradientangle=\"270\">11</TD> \n"
            + "    <TD border=\"3\"  bgcolor=\"/rdylgn11/3:/rdylgn11/8\" gradientangle=\"270\">12</TD> \n"
            + "    <TD border=\"3\"  bgcolor=\"/rdylgn11/4:/rdylgn11/9\" gradientangle=\"270\">13</TD> \n"
            + "    </TR> \n" + " \n"
            + "    <TR><TD border=\"3\"  bgcolor=\"/rdylgn11/6:/rdylgn11/9\" gradientangle=\"270\">20</TD> \n"
            + "    <TD border=\"3\" colspan=\"2\"  bgcolor=\"/rdylgn11/9:/rdylgn11/11\">22</TD> \n"
            + "    </TR> \n" + " \n"
            + "    <TR><TD border=\"3\" style=\"radial\" bgcolor=\"/rdylgn11/1:/rdylgn11/8\">30</TD> \n"
            + "    <TD border=\"3\" style=\"radial\" bgcolor=\"/rdylgn11/1:/rdylgn11/8\" gradientangle=\"45\">31</TD> \n"
            + "    <TD border=\"3\" style=\"radial\" bgcolor=\"/rdylgn11/1:/rdylgn11/8\" gradientangle=\"90\" >32</TD> \n"
            + "    <TD border=\"3\" style=\"radial\" bgcolor=\"/rdylgn11/1:/rdylgn11/8\" gradientangle=\"180\">33</TD> \n"
            + "    </TR> \n" + "</TABLE>>]; \n" + "} \n" + END_DIGRAPH;
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {GRAPHVIZ_DOT_PREAMBLE + "\n" + SIMPLE_EXAMPLE, GRAPHVIZ_DOT_PREAMBLE + "\n" + BIG_EXAMPLE, GraphViz.XWIZZ_WELCOME_MESSAGE};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").split("\n")[0].toLowerCase().startsWith(GRAPHVIZ_DOT_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
        this.code = this.getScriptWithoutPrepAndDeclAndPreamble();
    }

    @Override
    public GraphViz generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        GraphViz gv = new GraphViz(pdfPath, this);
        gv.addln(code);
        return gv;
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }

    @Override
    public HashMap<String, String> getMetaProperties() {
        HashMap<String, String> metaProps = new HashMap<>();
        String currentScript = this.getRawScript();
        int scriptLength = -1;
        
        if (currentScript != null) {
            scriptLength = currentScript.length();
        }
        
        metaProps.put("ScriptLength", scriptLength + "");
        
        return metaProps;
    }

    @Override
    public String getGermanName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String createScriptFromInstance() {
        return null;
    }
}
