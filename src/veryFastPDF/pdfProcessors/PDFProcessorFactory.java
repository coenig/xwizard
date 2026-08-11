/*
 * File name:        PDFProcessorFactory.java (package veryFastPDF.pdfProcessors)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  23.07.2015 (14:09:53)
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

package veryFastPDF.pdfProcessors;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import veryFastPDF.pdfProcessors.deprecated.GNUPlotPDF;
import veryFastPDF.pdfProcessors.deprecated.JavaPDF;

/**
 * @author Lukas König
 */
@SuppressWarnings("deprecation")
public class PDFProcessorFactory {

    private static HashMap<Class<? extends PDFProcessor>, PDFProcessor> availableProcessors;

    public static Collection<PDFProcessor> allWebPDFProcessors() {
        Collection<PDFProcessor> possiblePDFProcessorClasses = new HashSet<>();
        possiblePDFProcessorClasses.add(PDFProcessorFactory.getPrematureInstanceOf(GraphViz.class));
        possiblePDFProcessorClasses.add(PDFProcessorFactory.getPrematureInstanceOf(LaTeXPDF.class));
        return possiblePDFProcessorClasses;
    }

    public static PDFProcessor getPrematureInstanceOf(Class<? extends PDFProcessor> pdfClass) {
        if (availableProcessors == null) {
            availableProcessors = new HashMap<>();
            availableProcessors.put(GNUPlotPDF.class, new GNUPlotPDF(null));
            availableProcessors.put(GraphViz.class, new GraphViz(null, null));
            availableProcessors.put(JavaPDF.class, new JavaPDF(null));
            availableProcessors.put(LaTeXPDF.class, new LaTeXPDF(null, null, null));
        }
        
        return availableProcessors.get(pdfClass);
    }
}
