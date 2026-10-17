package veryFastPDF.algorithms.linearTrans;

import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * Placeholder for linear transformations (ML). So far, it ignores the script
 * content and always converts to a single LaTeX page saying "hello world".
 * Only the default conversion methods are available.
 */
@Webproof(useInProductiveMode = true)
public class LinearTrans extends RepresentableDefault {

    private static final long serialVersionUID = 4120758296435718323L;

    public static final String SCRIPT_PREAMBLE = "lintrans:";

    public LinearTrans(Exercise exercise) {
        super(exercise);
        this.setAllowCollapsingRules(false);
    }

    @Override
    public String[] getExampleScripts() {
        return new String[] {SCRIPT_PREAMBLE};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").trim().toLowerCase().startsWith(SCRIPT_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(script, father, 0);
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        return new LaTeXPDF(
                LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
                + "hello world"
                + LaTeXCommands.POSTAMBLE_STANDARD,
                pdfPath,
                this);
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }

    @Override
    public String getGermanName() {
        return "Lineare Transformation";
    }

    @Override
    public String createScriptFromInstance() {
        return SCRIPT_PREAMBLE + "\n" + this.generateCompleteDeclarationsBlock();
    }
}
