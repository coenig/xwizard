/*
 * File name:        GNUPlot.java (package eas.math.fundamentalAlgorithms.graphBased.pdfProcessors)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  27.10.2014 (20:08:29)
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

package veryFastPDF.pdfProcessors.deprecated;

import eas.miscellaneous.gnuplot.JGnuplot;
import eas.miscellaneous.gnuplot.JGnuplot.Plot;
import veryFastPDF.algorithms.gnuPlot.GNUPlotCode;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.UnsupportedOutputFormatException;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * @author Lukas König
 */
@Deprecated
public class GNUPlotPDF extends PDFProcessor {

    public static final String GNUPLOT_PREFIX = "gnuplot:";
    private String gnuplotCode;

    public GNUPlotPDF(String code) {
        super(null);
        gnuplotCode = code;
    }

    @Override
    public void storeAsPDF(String datNam, String tempDir) {
        // Create required PDF files.
        this.applyPreprocessors(tempDir);

        String pdfSettings = "set terminal pdf\n"
                           + "set output '" + tempDir + "/" + datNam + ".pdf'\n";
        JGnuplot plot = new JGnuplot();
        
        plot.execute(new Plot("test"), pdfSettings + this.gnuplotCode.substring(this.getCodePrefix().length()));
    }

    @Override
    public String getSourceString() {
        return this.gnuplotCode.substring(this.getCodePrefix().length());
    }

    @Override
    public String getCodePrefix() {
        return GNUPLOT_PREFIX;
    }

    @Override
    public void addln(String s) {
        this.gnuplotCode += "\n";
    }

    @Override
    public int getCodeSizeToBeConsideredLarge() {
        return 10_000;
    }
    
    public void setGnuplotCode(String gnuplotCode) {
        this.gnuplotCode = gnuplotCode;
    }

    @Override
    public String getSVGCode(final String fileName, String workingDir, String script, int hash)
            throws UnsupportedOutputFormatException {
        throw new UnsupportedOutputFormatException();
    }

    @Override
    public Class<? extends RepresentableAsPDF> getPlainRepresentableClass() {
        return GNUPlotCode.class;
    }

    @Override
    public String safetyCodeInCaseOfLargeCodeOrLongOperation(int codeSize) {
        throw new RuntimeException("No safety code provided by " + this.getClass().getSimpleName());
    }
    
    @Override
    public String getPreparedSourceString() {
        return this.getSourceString();
    }
}
