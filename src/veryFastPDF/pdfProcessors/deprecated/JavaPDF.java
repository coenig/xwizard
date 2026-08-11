/*
 * File name:        JavaPDF.java (package eas.math.veryFastPDF.pdfProcessors)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.02.2015 (20:26:56)
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

import veryFastPDF.algorithms.JavaPDFCode;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.UnsupportedOutputFormatException;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * @author Lukas König
 */
@Deprecated
public class JavaPDF extends PDFProcessor {

    public static final String JAVA_PDF_PREFIX = "javaPDF:";

    private String code;
    
    public JavaPDF(String code) {
        super(null);
        this.code = code;
    }
    
    @Override
    public void storeAsPDF(String datNam, String tempDir) {
        try {
            String[] lines = this.code.split("\n");
            Document document = new Document();
            
            document.setPageSize(new Rectangle(300, 20 * lines.length + 40));
            document.setMargins(10, 10, 10, 10);
            PdfWriter.getInstance(document, new FileOutputStream(tempDir + "/" + datNam + ".pdf"));
            document.open();
            
            for (String s : lines) {
                document.add(new Paragraph(s));
            }
            
            document.close();
        } catch (FileNotFoundException | DocumentException e) {
            throw new RuntimeException("PDF creation failed (" + this.getClass().getSimpleName() + ")");
        }
    }

    @Override
    public String getSourceString() {
        return code;
    }

    @Override
    public String getCodePrefix() {
        return JAVA_PDF_PREFIX;
    }

    @Override
    public void addln(String s) {
        this.code += s + "\n";
    }

    @Override
    public int getCodeSizeToBeConsideredLarge() {
        return 10_000;
    }

    @Override
    public String getSVGCode(final String fileName, String workingDir, String script, int hash)
            throws UnsupportedOutputFormatException {
        throw new UnsupportedOutputFormatException();
    }

    @Override
    public Class<? extends RepresentableAsPDF> getPlainRepresentableClass() {
        return JavaPDFCode.class;
    }

    @Override
    public String safetyCodeInCaseOfLargeCodeOrLongOperation(int codeSize) {
        return "Sorry, " + this.getClass().getSimpleName()
                + " code too large (" + this.getSourceString().length()
                + " characters) or operation too long";
    }
    
    @Override
    public String getPreparedSourceString() {
        return this.getSourceString();
    }
}
