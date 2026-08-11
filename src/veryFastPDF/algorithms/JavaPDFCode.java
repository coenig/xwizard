/*
 * File name:        JavaPDFCode.java (package eas.math.veryFastPDF.algorithms)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.02.2015 (20:45:08)
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

package veryFastPDF.algorithms;

import java.util.HashMap;

import javax.swing.JComponent;

import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.deprecated.JavaPDF;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;

/**
 * @author Lukas König
 */
@SuppressWarnings("deprecation")
public class JavaPDFCode extends RepresentableDefault {

    public JavaPDFCode(Exercise exercise) {
        super(exercise);
    }

    private static final long serialVersionUID = 2715152073979798683L;

    @Override
    public String[] getExampleScripts() {
        return new String[] {
//                JavaPDF.JAVA_PDF_PREFIX + "\nHello World!",
//                JavaPDF.JAVA_PDF_PREFIX + "\nSome other text..."
                };
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return code.replace(" ", "").replace("\n", "").startsWith(JavaPDF.JAVA_PDF_PREFIX);
    }

    @Override
    public void createInstanceFromScript(String code, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(code, father, 0);
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        return new JavaPDF(this.getRawScript());
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return new JavaPDF("").getClass();
    }

    @Override
    public JComponent getAdditionalInfo() {
        return null;
    }

    @Override
    public HashMap<String, String> getMetaProperties() {
        return new HashMap<>();
    }
    
    @Override
    public String getGermanName() {
        return "Java-PDF-Code";
    }

    @Override
    public String createScriptFromInstance() {
        // TODO Auto-generated method stub
        return null;
    }
}
