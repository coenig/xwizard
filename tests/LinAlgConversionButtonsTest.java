import java.util.Collections;
import java.util.HashMap;

import mainServlet.WebLink;
import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.ScriptConversionMethods;

/** Context-sensitive conversion controls without removing chain-callable methods. */
public final class LinAlgConversionButtonsTest {
    private static int checks;

    public static void main(String[] args) {
        LinAlg mlp = new LinAlg(null);
        mlp.createInstanceFromScript(mlp.getExampleScripts()[16], null);
        visible(mlp, true, "MLP step", "Next MLP neuron", "Animate MLP training");
        visible(mlp, false, "Select multiplication", "Animate multiplication", "Multiplication step",
                "Next multiplication step", "Multiply", "Multiply with scalar", "Change basis",
                "Toggle plots", "Toggle formulas");
        String html = html(mlp);
        check(html.contains("Animate MLP training") && !html.contains("Animate multiplication"),
                "web HTML contains only applicable animation buttons");
        mlp.mlpStep(mlp.getMlpTrace().getStepCount());
        visible(mlp, false, "Next MLP neuron");
        visible(mlp, true, "MLP step", "Animate MLP training");
        mlp.mlpStep(0);
        visible(mlp, true, "Next MLP neuron");

        LinAlg plain = parse("A=[1 2;3 4]\nv=(1,2)");
        visible(plain, false, "MLP step", "Next MLP neuron", "Animate MLP training",
                "Select multiplication", "Animate multiplication", "Multiplication step",
                "Next multiplication step", "Toggle formulas");
        visible(plain, true, "Multiply with scalar", "Multiply", "Change basis", "Toggle plots");
        plain.togglePlot();
        visible(plain, true, "Toggle formulas");
        LinAlg scalar = parse("x=2");
        visible(scalar, false, "Multiply with scalar", "Toggle plots", "Toggle formulas");

        LinAlg marked = parse("A=[1 2;3 4]\nB=[2 0;0 2]\nC=A * @product B");
        visible(marked, true, "Select multiplication", "Animate multiplication");
        visible(marked, false, "Multiplication step", "Next multiplication step");
        marked.selectOperation("product");
        visible(marked, true, "Multiplication step", "Next multiplication step", "Animate multiplication");
        visible(marked, false, "MLP step", "Toggle plots", "Toggle formulas", "Multiply", "Change basis");
        marked.multiplicationStep(Integer.parseInt(marked.multiplicationSteps()));
        visible(marked, false, "Next multiplication step");
        visible(marked, true, "Multiplication step", "Animate multiplication");
        LinAlg otherMarkers = parse("A=[1 2;3 4]\nB=A + @sum A\nC=2 * @scale A\nD=A ^T @transpose");
        visible(otherMarkers, false, "Select multiplication", "Animate multiplication");
        LinAlg finalProduct = parse("A=[1]\nB=A * @p A\n--declarations--\noperation=p;\nstep=-1;\n--declarations-end--");
        visible(finalProduct, false, "Next multiplication step");
        visible(finalProduct, true, "Multiplication step");
        LinAlg mixed = new LinAlg(null);
        mixed.createInstanceFromScript(mlp.getExampleScripts()[16].replace("--declarations--",
                "product=W1 * @product x\n--declarations--"), null);
        visible(mixed, true, "Animate MLP training", "Select multiplication", "Animate multiplication");
        mixed.selectOperation("product");
        visible(mixed, true, "Multiplication step");
        visible(mixed, false, "MLP step", "Animate MLP training");

        RepresentableDefault.removeKnownStuff();
        LinAlg animated = new LinAlg(null);
        mlp.createInstanceFromScript(mlp.getExampleScripts()[16], null);
        animated.createInstanceFromScript(ScriptConversionMethods.removeComments(mlp.animateMlp()), null);
        check(animated.getAnimate().size() == 23, "hidden buttons do not break MLP animation chains");
        check(plain.getDynamicMethods().containsKey("mlpSteps")
                && plain.getDynamicMethods().containsKey("multiplicationSteps"),
                "script conversion methods remain registered");
        System.out.println("Passed " + checks + " conversion button checks.");
    }

    private static LinAlg parse(String body) {
        LinAlg rep = new LinAlg(null);
        rep.createInstanceFromScript("linalg:\n" + body, null);
        return rep;
    }

    private static String html(LinAlg rep) {
        return WebLink.getDynamicMethodButtonsHTMLString(rep, rep.createScriptFromInstance(), true,
                Collections.singletonList(LinAlg.class), ".*", ".*", ".*", new HashMap<>());
    }

    private static void visible(LinAlg rep, boolean expected, String... names) {
        HashMap<String, MethodWrapper> methods = rep.getDynamicMethods();
        for (String name : names) {
            check(methods.get(name).isMethodButtonVisible() == expected, name + " visibility");
        }
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
