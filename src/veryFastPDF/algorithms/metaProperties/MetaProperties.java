/*
 * File name:        MetaProperties.java (package veryFastPDF.algorithms.metaProperties)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  27.02.2016 (10:17:42)
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

package veryFastPDF.algorithms.metaProperties;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import veryFastPDF.VFPVariables;
import veryFastPDF.algorithms.JavaPDFCode;
import veryFastPDF.algorithms.gnuPlot.GNUPlotCode;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.plainDOT.Graphviz;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.deprecated.JavaPDF;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;

/**
 * @author Lukas König
 */
@SuppressWarnings("deprecation")
@Webproof(useInProductiveMode = true)
public class MetaProperties extends RepresentableDefault {

    private static final long serialVersionUID = 5363258334182590814L;

    private boolean english = true;
    private boolean showMethods = true;
    private String additionalGraphvizCommand = "rankdir=TD";
    private String ignoreObjects = GNUPlotCode.class.getSimpleName() + "," + JavaPDF.class.getSimpleName();
    
    public MetaProperties(Exercise exercise) {
        super(exercise);
        this.setAllowCollapsingRules(false);
    }

    public static final String SCRIPT_PREAMBLE = "properties:";
    
    private static String EXAMPLE_ENGLISH;
    private static String EXAMPLE_GERMAN;
    
    @Override
    public String[] getExampleScripts() {
        if (EXAMPLE_ENGLISH == null || EXAMPLE_GERMAN == null) {
            MetaProperties prop = new MetaProperties(null);
            EXAMPLE_ENGLISH = prop.createScriptFromInstance();
            prop.english = false;
            EXAMPLE_GERMAN = prop.createScriptFromInstance();
        }
        
        return new String[] {EXAMPLE_ENGLISH, EXAMPLE_GERMAN};
    }

    @Override
    public boolean isAcceptableScript(String script) {
        return script.trim().startsWith(SCRIPT_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {
        super.applyDeclarationsAndPreprocessors(script, father, 0);
        String plainScript = super.getScriptWithoutPrepAndDeclAndPreamble();
        GlobalVariables.getParameters().logDebug("Meta properties script received:\n-B-O-\n" + plainScript + "-E-O-");
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }

    @Override
    public String getGermanName() {
        return VFPVariables.PROG_NAME_XWIZZ + "-Eigenschaften";
    }

    @Override
    public String createScriptFromInstance() {
        return SCRIPT_PREAMBLE + "\n"
                + this.generateCompleteDeclarationsBlock();
    }
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);
        
        String[] ignore = ignoreObjects.split(",");

        if (ignoreObjects.isEmpty()) {
            ignore = new String[0];
        }
        
        GraphViz gv = new GraphViz(pdfPath, this);
        String title = this.english
                ? "Object Types and their Connections in XWizard"
                : "Objekttypen und ihre Verbindungen untereinander im XWizard";
        
        gv.addln(GraphViz.startDigraph());
//        gv.addln("D [label=\"All conversions\" penwidth=3 shape=\"rectangle\" style=filled, fillcolor=\"#FCD975\"];");
        gv.addln("labelloc=\"t\";");
        
        if (!additionalGraphvizCommand.isEmpty()) {
            gv.addln(additionalGraphvizCommand + ";\n");
        }
        
        gv.addln("label=\"" + title + "\";");
        
        HashSet<String> lines = new HashSet<>();
        for (Class<? extends RepresentableAsPDF> repClass : RepresentableFactory.getAvailableTypes()) {
            try {
                grab(lines, repClass, new HashSet<>());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        for (String line : lines) {
            boolean toIgnore = false;
            
            for (String s : ignore) {
                if (line.contains(s)) {
                    toIgnore = true;
                    break;
                }
            }
            
            if (!toIgnore) {
                gv.addln(line);
            }
        }
        
        gv.addln("MARB -> " + Graphviz.class.getSimpleName() + " [style=dotted];"); // TODO: Make MARB instance of RepresentableDefault
        
        gv.addln(GraphViz.endGraph());
        
        return gv;
    }
    
    private HashMap<Class<? extends RepresentableAsPDF>, String> classNames = new HashMap<>();
    
    private boolean grab(
            HashSet<String> soFar, 
            Class<? extends RepresentableAsPDF> start, 
            HashSet<Class<? extends RepresentableAsPDF>> visited) {
        if (!classNames.containsKey(start)) {
            classNames.put(start, StaticMethods.removeWhitespaces(start.getSimpleName()));
        }
        
        String startName = classNames.get(start);
        String germanName = RepresentableFactory.getGermanNameByClass(start);
        
        if (germanName == null) {
            germanName = start.getSimpleName();
        }
        
        String startDisplay = this.english
                ? start.getSimpleName()
                : ConvenienceMethods.replaceSpecialCharsHTML_G(germanName);
                
        RepresentableAsPDF rep = RepresentableFactory.getRepByClass(start);
        
        String shape = "ellipse";
        
        if (isPlainGeneratorScript(start)) {
            shape = "diamond";
        }
        
        soFar.add(startName + " [label=\"" + startDisplay + "\" shape=\"" + shape + "\"];");
        
        for (String mwName : rep.getDynamicMethods().keySet()) {
            MethodWrapper mw = rep.getDynamicMethods().get(mwName);
            Collection<Class<? extends RepresentableAsPDF>> targetClasses = mw.getClassesOfTargetScript();
            
            for (Class<? extends RepresentableAsPDF> target : targetClasses) {
                if (mw.isReturnValueScript() && target != null) {
                    if (!classNames.containsKey(target)) {
                        classNames.put(target, StaticMethods.removeWhitespaces(target.getSimpleName()));
                    }
        
                    String targetName = classNames.get(target);
                    String style = " [style=bold";
                    
                    if (isPlainGeneratorScript(target)) {
                        style = " [style=dotted";
                        soFar.add("PDF [shape=polygon peripheries=2 sides=8];");
                        soFar.add(targetName + " -> " + "PDF" + " [style=dashed];");
                    } else {
                        if (this.showMethods) {
                            String methodStringDisplay = this.english
                                    ? "method"
                                    : "Methode";
                            
                            String methodName = this.english
                                    ? mw.getDisplayNameWithDots()
                                    : ConvenienceMethods.replaceSpecialCharsHTML_G(mw.getDisplayNameWithDots_G());
                            
                            style += " label=\"" + methodStringDisplay + ": '" + methodName + "'\"";
                        }
                    }
                    
                    style += "]";
                    
                    if (!startName.equals(targetName)) {
                        soFar.add(startName + " -> " + targetName + style + ";");
                    }
                    
                    if (!visited.contains(start)) {
                        HashSet<Class<? extends RepresentableAsPDF>> visNew = new HashSet<>(visited);
                        visNew.add(start);
                        grab(soFar, target, visNew);
                    }
                }
            }
        }
        
        return true;
    }

    private boolean isPlainGeneratorScript(
            Class<? extends RepresentableAsPDF> start) {
        return start.equals(Graphviz.class)
                || start.equals(LaTeX.class)
                || start.equals(GNUPlotCode.class)
                || start.equals(JavaPDFCode.class);
    }
}
