/*
 * File name:        Tester.java (package veryFastPDF.script.testing)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  18.03.2017 (09:00:34)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

package veryFastPDF.script.testing;

import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * @author hq0976
 */
@Webproof(useInProductiveMode = true)
public class Tester extends RepresentableDefault {

    private static final long serialVersionUID = -8324856455327689606L;

    public Tester() {
        super(null);
    }

    @Override
    public String[] getExampleScripts() {
        return TestScripts.TEST_STRINGS;
    }

    @Override
    public boolean isAcceptableScript(String script) {
        return false;
    }

    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {

    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }

    @Override
    public String getGermanName() {
        return null;
    }

    @Override
    public String createScriptFromInstance() {
        return null;
    }
}
