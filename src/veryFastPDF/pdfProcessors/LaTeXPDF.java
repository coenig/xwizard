/*
 * File name:        LaTeXPDF2.java (package veryFastPDF.pdfProcessors)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.11.2014 (17:37:58)
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.ExternalFilePathsManager;
import eas.miscellaneous.convenience.FileLocationEstimator;
import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import veryFastPDF.VFPVariables;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.script.ScriptConversionMethods;
import veryFastPDF.web.ConvenienceMethods;

/**
 * @author Lukas König
 */
public class LaTeXPDF extends PDFProcessor {
    
    private String latexCode = "";
    private String rawCode = "";
    
    public static final String PATH_TO_PDFTK_ID = ExternalFilePathsManager.registerFilepathID("PATH_TO_pdftk.exe");
    public static final String PATH_TO_PDF2SVG_ID = ExternalFilePathsManager.registerFilepathID("PATH_TO_pdf2svg.exe");

    protected static FileLocationEstimator estimator_pdf2svg = new FileLocationEstimator(
            ".*program.*", ".*pdf2svg.*", "pdf2svg.exe");
    
    private static FileLocationEstimator estimator_tk = new FileLocationEstimator(
            ".*program.*", ".*PDFtk.*", "bin", "pdftk.exe");
    
    private static FileLocationEstimator estimator = new FileLocationEstimator(
            ".*program.*", ".*miktex.*", ".*miktex.*", "bin", "x64", "pdflatex.exe");
    
    static {
        estimator.addAlternatePath(
                ".*program.*", ".*miktex.*", ".*miktex.*", "bin", "pdflatex.exe");
        estimator.addAlternatePath(
                ".*Users.*", ".*", ".*AppData.*", ".*Local.*", ".*Program.*", ".*MiKTeX.*", ".*miktex.*", ".*bin.*", ".*x64.*", "pdflatex.exe");
        estimator.addAlternatePath(
                ".*Users.*", ".*", ".*AppData.*", ".*Local.*", ".*Program.*", ".*MiKTeX.*", ".*miktex.*", ".*bin.*", "pdflatex.exe");
    }
    
    public LaTeXPDF(String code, String pdfPath, RepresentableAsPDF rep) {
        super(rep);

        latexCode = code;
        rawCode = code;
        
        if (ScriptConversionMethods.containsInscriptPreprocessorsForInternalUsage(code, LaTeX.getStaticInstance())) {
            GlobalVariables.getParameters().logWarning(
                    "The following script contained inscript tags at LaTeXPDF compilation time: " + StringUtils.abbreviateMiddle(code, " [...] ", 100));

            String code2 = inferPlaceholders(code);
            
            LaTeX latexRep = (LaTeX) RepresentableFactory.instanceFromScript(LaTeX.LATEX_PREAMBLE + code2, null);
            latexCode = latexRep.generatePDFscript(pdfPath).getSourceString();
        }
        
        if (latexCode != null) { // Here we finally remove the plain-text tags, if any.
            latexCode = latexCode.replace(RepresentableDefault.PLAIN_TEXT_BEGIN_TAG, "").replace(RepresentableDefault.PLAIN_TEXT_END_TAG, "");
        }
    }

    /**
     * In the end has the following effect: All regular inscript tags are
     * surrounded by plain-text tags, all internal-usage inscript tags are 
     * regular inscript tags.
     * 
     * @param code  The code to infer placeholders.
     * 
     * @return  The processed code with placeholders.
     */
    private static String inferPlaceholders(String code) {
        String begTagInternalPlace = RepresentableDefault.getStaticInstance().symbolToPlaceholder(RepresentableDefault.INSCR_BEG_TAG_FOR_INTERNAL_USAGE);
        String endTagInternalPlace = RepresentableDefault.getStaticInstance().symbolToPlaceholder(RepresentableDefault.INSCR_END_TAG_FOR_INTERNAL_USAGE);
        
        String[] symbs = new String[] {
                RepresentableDefault.INSCR_BEG_TAG_FOR_INTERNAL_USAGE,
                RepresentableDefault.INSCR_END_TAG_FOR_INTERNAL_USAGE,
                RepresentableDefault.INSCR_BEG_TAG,
                RepresentableDefault.INSCR_END_TAG,
                };

        String[] places = new String[] {
                begTagInternalPlace,
                endTagInternalPlace,
                RepresentableDefault.PLAIN_TEXT_BEGIN_TAG + symbs[2],
                symbs[3] + RepresentableDefault.PLAIN_TEXT_END_TAG,
                };
        String code2 = StringUtils.replaceEach(code, symbs, places);
        
        symbs = new String[] {
                begTagInternalPlace,
                endTagInternalPlace,
                };

        places = new String[] {
                RepresentableDefault.INSCR_BEG_TAG,
                RepresentableDefault.INSCR_END_TAG
                };
        String code3 = StringUtils.replaceEach(code2, symbs, places);
        
        return code3;
    }
    
    public static String message(String messageText) {
        return LaTeX.LATEX_PREAMBLE 
                + LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
                + messageText
                + LaTeXCommands.POSTAMBLE_STANDARD;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void storeAsPDF(String datNam, String tempDir) {
        // Create preprocessor files on PDF processor level (not recommended anymore).
        this.applyPreprocessors(tempDir);
        
        StaticMethods.saveTextToFile(
                tempDir, 
                datNam + ".tex", 
                latexCode);
        
        File latexPDFPath;
        if (MainLink.isApplicationOriginDesktop()) { // Desktop mode.
            latexPDFPath = ExternalFilePathsManager.retrieveExternalFilePath(
                    ExternalFilePathsManager.PATH_TO_PDFLATEX_ID, 
                    true,
                    "Choose the file path of the LaTeXCode pdf processor file (usually pdflatex.exe).",
                    estimator,
                    WebLink.CONF_FILE,
                    WebLink.pathLATEX_POS);
        } else { // Web mode.
            latexPDFPath = new File(WebLink.getLATEXPath());
        }

        String[] command = {
            latexPDFPath.getAbsolutePath(),
            "-interaction",
            "nonstopmode",
            "-output-directory",
            tempDir,
            new File(tempDir, datNam + ".tex").getAbsolutePath()
        };

//        GlobalVariables.getParameters().logError("Latex: " + command);

        ConvenienceMethods.execCommand(command, true);
        
//        String fullPath = tempDir + "/" + datNam + ".pdf";
//        
//        BrissCMD.autoCrop(new String[] {
//                "-s", 
//                fullPath, 
//                "-d", 
//                fullPath});
    }
    
    @Override
    public String getSourceString() {
        return rawCode;
    }

    @Override
    public String getPreparedSourceString() {
        return latexCode;
    }
    
    @Override
    public String getCodePrefix() {
        return LaTeX.LATEX_PREAMBLE;
    }

    @Override
    public void addln(String s) {
        this.latexCode += s + "\n";
    }

    @Override
    public int getCodeSizeToBeConsideredLarge() {
        return 50_000;
    }

    @Override
    public String getSVGCode(final String fileName, String workingDir, String script, int hash) {
        String oldScript = this.latexCode;
        this.latexCode = script;
        storeAsPDF(fileName, workingDir);
        this.latexCode = oldScript;

        splitPDFToSinglePages(fileName, workingDir);

        String pathToStoreSVG = new File(workingDir).getPath() + File.separator;
        createSVGFileFromAnimationInstructions(pathToStoreSVG, false, hash);

        return StaticMethods.readTextFromFile(new File(pathToStoreSVG + fileName + ".svg"), null);
    }

    private void splitPDFToSinglePages(String fileName, String workingDir) {
        File splitterPath;
        if (MainLink.isApplicationOriginDesktop()) { // Desktop mode.
            splitterPath = ExternalFilePathsManager.retrieveExternalFilePath(
                    PATH_TO_PDFTK_ID, 
                    true,
                    "Choose the executable file of the PDFTK program used to split PDFs into single pages (usually pdftk.exe).",
                    estimator_tk,
                    WebLink.CONF_FILE,
                    WebLink.pathPdftk_POS);
        } else { // Web mode.
            splitterPath = new File(WebLink.getPDFTKPath());
        }

        String[] command = {
            splitterPath.getAbsolutePath(),
            new File(workingDir, fileName + ".pdf").getAbsolutePath(),
            "burst",
            "output",
            new File(workingDir, WebLink.fileName("page%d") + ".pdf").getAbsolutePath()
        };

        ConvenienceMethods.execCommand(command, true);
    }

    @Override
    public Class<? extends RepresentableAsPDF> getPlainRepresentableClass() {
        return LaTeX.class;
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

    /**
     * Call this method MANUALLY to replace special characters according to
     * latex rules. Insert new specials if desired.
     * 
     * @param sString  The String to replace special characters in.
     * @return  The cleaned-up string.
     */
    public static String replaceSpecialChars(String sString) {
        String ssString = sString
                .replace("ä", "\\\"a")
                .replace("ö", "\\\"o")
                .replace("ü", "\\\"u")
                .replace("XplusX", "+")
                .replace("XsemX", ";")
                .replace("XkaX", "(")
                .replace("XkzX", ")");
        return ssString;
    }

    public static HashSet<String> getNamesOfPDFPages() {
        HashSet<String> names = new HashSet<>();
        
        for (int i = 1; i < 100; i++) {
            String num = i + "";
            String basename = "page" + num;
            String filename = WebLink.fileName(basename) + ".pdf";
            Path p = Paths.get(WebLink.getWORKING_DIRECTORY() + "/" + filename);
            if (p.toFile().exists()) {
                names.add(basename);
            }
        }
        
        return names;
    }
}
