/*
 * File name:        LaTeX2.java (package eas.math.fundamentalAlgorithms.graphBased.algorithms.latex)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.11.2014 (17:34:31)
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

package veryFastPDF.algorithms.latex;

import java.util.HashMap;

import eas.GlobalVariables;
import mainServlet.WebLink;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * Note that latex is called "webproof" only since there is no way of making
 * it really webproof. Even tiny latex code can compile for a long time if it
 * contains loops, for example. Therefore, using latex, watchdog has to make
 * sure that the server doesn't freeze.
 * 
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class LaTeX extends RepresentableDefault {

    public static final String LATEX_PREAMBLE = "latex:";

    private static final long serialVersionUID = 1429912356570528818L;

    public static final String NO_SERIOUS_ERROR_STRING = "Some error occurred during pdf generation, but if a chain could not be evaluated, it's probably nothing. Here's the error: ";

    private boolean formulaMode = false;

    private static LaTeX staticInstance = new LaTeX(null);
    
    public LaTeX(Exercise exercise) {
        super(exercise);
        this.setAllowCollapsingRules(false);
    }
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {
                LATEX_PREAMBLE + "%varm|gra%\\huge\n"
                + "A random finite state machine:\n\\par\n"
                + "x=@{fsm:}@.randD[4, false, 3]\n\\par\n"
                + "The determinized version:\n\\par\n"
                + "y=@{x}@.det\n\\par\n"
                + "The minimized version:\n\\par\n"
                + "@{y}@.min"
        };
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").startsWith(LATEX_PREAMBLE);
    }

    public static String subscript(double scale, String script) {
        return
        RepresentableDefault.INSCR_BEG_TAG_FOR_INTERNAL_USAGE
        + scale
        + "|"
        + script
        + RepresentableDefault.INSCR_END_TAG_FOR_INTERNAL_USAGE;
    }
    
    @Override
    public void createInstanceFromScript(String scriptRaw, RepresentableAsPDF father) {
        this.formulaMode = false;
        this.applyDeclarationsAndPreprocessors(scriptRaw, father, 0);
        this.setScriptForPreprocessorExtraction(
                addPrePostAmbles(
                        this.getScriptWithoutPrepAndDeclAndPreamble()));
    }
    
    private String addPrePostAmbles(String plainLatexDocumentCode) {
        try {
            if (plainLatexDocumentCode.length() > 5) {
                String conv = plainLatexDocumentCode.substring(1, plainLatexDocumentCode.indexOf("%", 1));
                return LaTeXCommands.embedLatexCode(plainLatexDocumentCode, conv);
            }
        } catch (Exception e) {
        }
        
        return plainLatexDocumentCode;
    }

    private static String includeGraphics(String filename2, double scale) {
        String filename = WebLink.getWORKING_DIRECTORY().replace("\\", "/") + "/" + filename2;
        filename = WebLink.fileName(filename);
        String before = lineBeforeSubscript(filename2);
        String after = ""; // Don't put a % here, it makes stuff weird.
        
        if (scale < 0) {
            return before + "\\includegraphics[width=" + (-scale) + "\\textwidth]{" + filename + "}" + after;
        }
        
        return before + "\\includegraphics[scale=" + scale + "]{" + filename + "}" + after;
    }

    public static String lineBeforeSubscript(String filename2) {
        return "%uu\r\n%vv Here comes the inscript graphic for " + filename2 + ":\r\n";
    }

    @Override
    protected String placeholderForInscript(String filename, String preprocessorScript, double scale, boolean allowRegularScripts) {
        String placeholderForInscript = super.placeholderForInscript(filename, preprocessorScript, scale, allowRegularScripts);
        
        if (placeholderForInscript == null) {
            return includeGraphics(filename, scale);
        } else {
            return placeholderForInscript;
        }
    }
    
    private static int paperWidthForTriangularTables = LaTeXCommands.TRIANGLE_TABLE_STANDARD_PAGE_WIDTH;
    private static int paperHeightForTriangularTables = LaTeXCommands.TRIANGLE_TABLE_STANDARD_PAGE_HEIGHT;
    
    public static void setPaperHeightForTriangularTables(
            int paperHeightForTriangularTables) {
        LaTeX.paperHeightForTriangularTables = paperHeightForTriangularTables;
    }
    
    public static void setPaperWidthForTriangularTables(
            int paperWidthForTriangularTables) {
        LaTeX.paperWidthForTriangularTables = paperWidthForTriangularTables;
    }
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        try {
            super.generatePDFscript(pdfPath);
        } catch (Exception e) {
            GlobalVariables.getParameters().logDebug(NO_SERIOUS_ERROR_STRING + e.toString());
        }

        String preamble = "";
        String postamble = "";
        String code2 = this.getScriptWithoutPrepAndDeclAndPreamble();
        
        if (this.formulaMode) {
            preamble = LaTeXCommands.PREAMBLE_CROP_PAGE + "$";
            postamble = "$" + LaTeXCommands.POSTAMBLE_STANDARD;
            code2 = code2.replace("\n", "");
        } else if (code2.contains("dreieckstabelle")) {
            code2 = code2.replace("\\begin{document}", 
                    LaTeXCommands.DREIECKS_TABELLE(
                            paperWidthForTriangularTables, 
                            paperHeightForTriangularTables) + "\\begin{document}");
        }

        paperWidthForTriangularTables = LaTeXCommands.TRIANGLE_TABLE_STANDARD_PAGE_WIDTH;  // Reset immediately to avoid side effects.
        paperHeightForTriangularTables = LaTeXCommands.TRIANGLE_TABLE_STANDARD_PAGE_HEIGHT;
        
        return new LaTeXPDF(preamble + code2 + postamble, pdfPath, this);
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }

    public static String createFormulaMode(String formula, RepresentableDefault caller) {
        return LATEX_PREAMBLE + "\n" + formula + "\n" + LaTeXCommands.formulaModeOptions(caller);
    }

    public static String createAlignEnv(String formula, RepresentableDefault caller) { // TODO
        String script = LATEX_PREAMBLE 
                + LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
                + "\\begin{align*}"
                + formula
                + "\\end{align*}\n"
                + LaTeXCommands.POSTAMBLE_STANDARD
                + caller.getUpperClassDeclarationsBlockOnly();
        return script;
    }

    @Override
    public String getGermanName() {
        return this.getClass().getSimpleName();
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        String className = this.getClass().getSimpleName();
        HashMap<String, String> metaProperties = super.getMetaProperties();
        
        metaProperties.put(className + "_formulaMode", this.formulaMode + "");
        
        return metaProperties;
    }

    @Override
    public String createScriptFromInstance() {
        return null;
    }
    
    public static String center(String code) {
        return "\n\\begin{center}\n" + code + "\n\\end{center}";
    }
    
    @Override
    public boolean removePlaintextTagsAfterPreprocessorApplication() {
        return false;
    }

    public static LaTeX getStaticInstance() {
        return staticInstance;
    }
    
//    
//    @Override
//    public void managePlainTextPreprocessor(String filename, DummyRepresentable dummy) {
//        // Don't call super method. There, all other script types can be handled.
//        putInPlaintextMethodresults(
//                LaTeX.lineBeforeSubscript(filename), 
//                dummy.getCurrentScript().replace(RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS, ""));
//    }
}
