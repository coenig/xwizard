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
        check(marked.getDynamicMethods().get("Select multiplication").getParameterCount() == 0
                && marked.getDynamicMethods().get("Animate multiplication").getParameterCount() == 0,
                "single marked multiplication needs no marker parameter");
        check(!html(marked).contains("#openModal" + "Select multiplication".hashCode())
                && html(marked).contains("type=\"submit\""), "single multiplication uses direct buttons");
        marked.selectOnlyMultiplication();
        check(marked.createScriptFromInstance().contains("operation=product"), "single marker inferred");
        LinAlg multiple = parse("A=[1 2;3 4]\nB=A * @first A\nC=B * @second A\nD=2 * @scale A");
        check(multiple.getDynamicMethods().get("Select multiplication").getParameterCount() == 1
                && multiple.getDynamicMethods().get("Animate multiplication").getParameterCount() == 1,
                "multiple marked products require marker parameter");
        check(html(multiple).contains("#openModal" + "Select multiplication".hashCode()),
                "multiple products retain selection dialog");
        try {
            multiple.selectOnlyMultiplication();
            throw new AssertionError("Ambiguous selection accepted");
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().contains("exactly one"), "ambiguous inference reports error");
        }
        LinAlg eligible = parse("A=[1]\nB=A * @\"quoted product\" A\nC=2 * @scale A\nD=A + @sum A");
        eligible.selectOnlyMultiplication();
        check(eligible.getDynamicMethods().get("Select multiplication").getParameterCount() == 0,
                "non-product and scalar markers do not cause ambiguity");
        LinAlg inferredAnimation = new LinAlg(null);
        inferredAnimation.createInstanceFromScript(
                ScriptConversionMethods.removeComments(eligible.animateOnlyMultiplication()), null);
        check(inferredAnimation.getAnimate().size() == 2, "inferred animation covers initial and arithmetic frames");
        var explicit = (LinAlg) ScriptConversionMethods.evaluateChain(marked.createScriptFromInstance(),
                "this.selectOperation[product]", null);
        check(explicit.createScriptFromInstance().contains("operation=product"),
                "explicit marker chains remain compatible with single-operation controls");
        var inferred = (LinAlg) ScriptConversionMethods.evaluateChain(marked.createScriptFromInstance(),
                "this.selectOnlyMultiplication", null);
        check(inferred.createScriptFromInstance().contains("operation=product"),
                "inferred selection is chain callable");
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
