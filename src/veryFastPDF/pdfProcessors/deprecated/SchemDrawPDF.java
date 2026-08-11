/*
 * File name:        SchemDrawPDF.java (package veryFastPDF.pdfProcessors)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  17.11.2015 (10:23)
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

import java.io.File;

import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.ExternalFilePathsManager;
import eas.miscellaneous.convenience.FileLocationEstimator;
import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import veryFastPDF.VFPVariables;
import veryFastPDF.algorithms.circuits.LogicCircuit;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.web.ConvenienceMethods;

/**
 * @author Lukas König
 */
@Deprecated
public class SchemDrawPDF extends PDFProcessor {

    private String pythonCode = "";
    
    private static FileLocationEstimator estimator = new FileLocationEstimator(
            ".*Anaconda.*", "pythonw.exe");
    
    static {
        estimator.addAlternatePath(
                ".*program.*", ".*Anaconda.*", "pythonw.exe");
    }
    
    public SchemDrawPDF(String code) {
        super(null);
        pythonCode = code;
    }
    
    @Override
    public void storeAsPDF(String datNam, String tempDir) {
        // Create required PDF files.
        this.applyPreprocessors(tempDir);
        
        StaticMethods.saveTextToFile(
                tempDir, 
                datNam + ".py", 
                pythonCode 
                    + "\n"
                    + "d.save('" + tempDir.replace("\\", "/") + "/" + datNam + ".pdf')");
        
        File pythonPath;
        if (MainLink.isApplicationOriginDesktop()) { // Desktop mode.
            pythonPath = ExternalFilePathsManager.retrieveExternalFilePath(
                    ExternalFilePathsManager.PATH_TO_PYTHON_ID, 
                    true,
                    "Choose the file path of the Python processor (usually python.exe).",
                    estimator,
                    null,
                    null);
        } else { // Web mode.
            pythonPath = new File(WebLink.getPythonPath());
        }
        
        String command = "\"" + pythonPath.getAbsolutePath() + "\" \"" + tempDir + "/" + datNam + ".py\"";

        ConvenienceMethods.execCommand(command, true, false);
    }
    
    @Override
    public String getSourceString() {
        return pythonCode;
    }

    @Override
    public void addln(String s) {
        this.pythonCode += s + "\n";
    }

    @Override
    public int getCodeSizeToBeConsideredLarge() {
        return 15_000;
    }

    @Override
    public String getSVGCode(final String fileName, String workingDir, String script, int hash) { // TODO
        // Create required PDF files.
        this.applyPreprocessors(workingDir);

        String oldScript = this.pythonCode;
        this.pythonCode = script;
        storeAsPDF(fileName, workingDir);
        this.pythonCode = oldScript;

        String fileNamePath = new File(workingDir).getPath() + "\\" + fileName;
        
        String command = "\"" + new File(WebLink.getPDF2SVGPath()).getPath() // Note the " which are required on server only.
                + "\" \"" + fileNamePath + ".pdf\" \"" + fileNamePath + ".svg\"";

        ConvenienceMethods.execCommand(command, true, false);

        return StaticMethods.readTextFromFile(new File(fileNamePath + ".svg"), null);
    }

    @Override
    public Class<? extends RepresentableAsPDF> getPlainRepresentableClass() {
        return LogicCircuit.class;
    }

    @Override
    public String safetyCodeInCaseOfLargeCodeOrLongOperation(int codeSize) {
        String latexString = LaTeXCommands.PREAMBLE_CROP_PAGE 
                + "\\begin{tabular}{c}"
                + "Sorry, " + this.getClass().getSimpleName()
                + " code too large (" + codeSize
                + " characters) or operation too long; \\\\ "
                + GraphViz.INFO_ABOUT_TOO_LONG_SCRIPTS_AND_OPS + ":\\\\ "
                + "\\url{" + VFPVariables.LINK_TO_VFP + "}"
                + "\\end{tabular}"
                + LaTeXCommands.POSTAMBLE_STANDARD;
        return latexString;
    }

    @Override
    public String getCodePrefix() {
        return "schemdraw:";
    }
    
    @Override
    public String getPreparedSourceString() {
        return this.getSourceString();
    }
}
