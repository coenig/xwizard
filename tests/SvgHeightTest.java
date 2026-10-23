import veryFastPDF.algorithms.bdd.BDD;
import veryFastPDF.algorithms.fsm.FSM;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.script.RepresentableDefault;

/** Shared web output height declarations and script roundtrips. */
public final class SvgHeightTest {
    private static int checks;

    public static void main(String[] args) {
        for (RepresentableDefault rep : new RepresentableDefault[] {
                new LinAlg(null), new FSM((veryFastPDF.script.Exercise) null), new BDD(null), new LaTeX(null)}) {
            String example = rep instanceof LaTeX
                    ? "latex:\n\\documentclass{article}\\begin{document}Test\\end{document}"
                    : rep.getExampleScripts()[0];
            rep.createInstanceFromScript(example + declarations("800"), null);
            check(rep.getSvgHeight() == 800, "height applies to " + rep.getClass().getSimpleName());
            String roundtrip = rep instanceof LaTeX ? rep.addDeclarationsToScript() : rep.createScriptFromInstance();
            check(roundtrip.contains("svgHeight=800;"), "height survives script generation: " + rep.getClass().getSimpleName());
            rep.createInstanceFromScript(roundtrip, null);
            check(rep.getSvgHeight() == 800, "height survives roundtrip");
            check(rep.createCompleteAnimationScript("").contains("svgHeight=800;"),
                    "height survives animation generation");
            check(rep.getUpperClassDeclarationsBlockOnly().contains("svgHeight=800;"),
                    "shared declarations retain height");
            rep.createInstanceFromScript(example + declarations("0"), null);
            check(rep.getSvgHeight() == 0, "zero selects stylesheet default");
            rep.createInstanceFromScript(example + declarations("650"), null);
            rep.createInstanceFromScript(example, null);
            check(rep.getSvgHeight() == 0, "height resets when declaration removed");
            for (String invalid : new String[] {"-1", "800px", "1.5", "no", "2147483648"}) {
                try {
                    rep.createInstanceFromScript(example + declarations(invalid), null);
                    throw new AssertionError("Accepted invalid height " + invalid);
                } catch (IllegalArgumentException expected) {
                    check(expected.getMessage().contains("svgHeight"), "invalid height explicitly reported");
                }
            }
        }
        LinAlg mlp = new LinAlg(null);
        mlp.createInstanceFromScript(mlp.getExampleScripts()[16], null);
        check(mlp.getSvgHeight() == 800, "MLP example requests taller web output");
        check(mlp.animateMlp().contains("svgHeight=800;"), "MLP animation preserves height");
        System.out.println("Passed " + checks + " SVG height checks.");
    }

    private static String declarations(String height) {
        return "\n--declarations--\nsvgHeight=" + height + ";\n--declarations-end--";
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
