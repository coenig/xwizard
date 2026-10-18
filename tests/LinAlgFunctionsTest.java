import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.algorithms.linearTrans.LinAlgScene;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Statement;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Dependency-free regression checks; run with target/classes and application jars on the classpath. */
public class LinAlgFunctionsTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        String a = "A = [1 2 3; 2 4 6; 1 1 1]\n";
        scalar(a, "det(A)", 0);
        scalar(a, "rank(A)", 2);
        scalar(a, "trace(A)", 6);
        scalar("A=[0 1;1 0]\n", "det(A)", -1);
        scalar("A=[2]\n", "det(A)", 2);
        scalar("A=[1e-20 0;0 2e-20]\n", "rank(A)", 2);
        scalar("A=[1e20 0;0 2e20]\n", "rank(A)", 2);
        scalar("A=[1e-20 0;0 2e-20]\n", "det(inv(A))", 5e39);
        scalar("A=[3 4]\n", "norm(A)", 5);
        scalar("A=[1 2;3 4]\n", "trace(A^T) + det(A) * rank(A)", 1);
        matrix(a, "rref(A)", new double[][] {{1, 0, -1}, {0, 1, 2}, {0, 0, 0}});
        matrix(a, "basis(A)", new double[][] {{1, 2}, {2, 4}, {1, 1}});
        matrix(a, "nullspace(A)", new double[][] {{1}, {-2}, {1}});
        matrix("A=[1 2;3 4]\n", "transpose(A)", new double[][] {{1, 3}, {2, 4}});
        matrix("A=[2 0;0 4]\n", "inv(A)", new double[][] {{0.5, 0}, {0, 0.25}});
        matrix("A=[0 0;0 0]\n", "nullspace(A)", new double[][] {{1, 0}, {0, 1}});

        Value empty = evaluate("A=[1 0;0 1]\n", "nullspace(A)");
        check(empty.getRowCount() == 2 && empty.getColumnCount() == 0, "empty nullspace basis is 2x0");
        Value transposed = evaluate("A=[1 0;0 1]\n", "transpose(nullspace(A))");
        check(transposed.getRowCount() == 0 && transposed.getColumnCount() == 2, "empty transpose is 0x2");
        matrix("A=[1 0;0 1]\n", "A * nullspace(A) * transpose(nullspace(A))",
                new double[][] {{0, 0}, {0, 0}});

        LinAlgScene span = LinAlgScene.parse("u=(1,2,3)\nv=2*u\nspan(u, v)\n");
        Statement last = span.getStatements().get(2);
        check(last.isDirective() && last.getFunction().equals("span"), "span directive");
        check(last.getValue().getColumnCount() == 1, "dependent vectors removed");
        check(span.toScript().contains("span(u, v)"), "span source preserved");
        LinAlgScene scene = LinAlgScene.parse(a + "det(A)\nrank(A)\nspan(A)\nx=rank(A)+1\nsvd(A)\neig(A)\n");
        check(scene.toScript().contains("x = rank(A)+1"), "assignment source preserved");
        check(scene.toScript().equals(LinAlgScene.parse(scene.toScript()).toScript()), "round trip");
        check(LinAlgScene.parse("1 2\n3 4").find("A").getValue().getRowCount() == 2, "legacy matrix");

        error("A=[1 2]\ndet(A)", "Line 2:", "square");
        error("A=[1 2]\ntrace(A)", "Line 2:", "square");
        error("A=[1]\nr=rank()", "Line 2:", "exactly one");
        error("A=[1]\nr=rank(A,A)", "Line 2:", "exactly one");
        error("A=[1]\nr=norm(3)", "Line 2:", "matrix or vector");
        error("rank(X)", "Line 1:", "Unknown name");
        error("span()", "Line 1:", "needs a matrix");
        error("span((1,2),(1,2,3))", "Line 1:", "equal dimension");
        error("span([1 2],[3 4])", "Line 1:", "column vectors");
        error("A=[1]\nx=span(A)", "Line 2:", "own line");
        error("A=[1]\nx=mystery(A)", "Line 2:", "Unknown function");
        error("A=[1 2;2 4]\ninv(A)", "Line 2:", "singular");
        error("A=[1e999]\nrank(A)", "Line 2:", "finite");
        error("A=[1]\nspan(A)+span(A)", "Line 2:", "Unexpected");

        Random random = new Random(42);
        for (int i = 0; i < 30; i++) {
            StringBuilder body = new StringBuilder("A=[");
            int rows = 1 + random.nextInt(4);
            int cols = 1 + random.nextInt(5);
            for (int r = 0; r < rows; r++) {
                body.append(r == 0 ? "" : ";");
                for (int c = 0; c < cols; c++) {
                    body.append(" ").append(random.nextInt(7) - 3);
                }
            }
            body.append("]\n");
            Value rank = evaluate(body.toString(), "rank(A)");
            Value nullspace = evaluate(body.toString(), "nullspace(A)");
            check(rank.getScalar() + nullspace.getColumnCount() == cols, "rank-nullity");
            Value residual = evaluate(body.toString(), "A * nullspace(A)");
            for (double[] row : residual.getRows()) {
                for (double entry : row) {
                    close(entry, 0);
                }
            }
            Value rref = evaluate(body.toString(), "rref(A)");
            matrix(body.toString(), "rref(rref(A))", rref.getRows());
        }

        LinAlg script = new LinAlg(null);
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        for (String help : new String[] {script.veryQuickHelpText(), script.veryQuickHelpText_G(),
                script.helpText(), script.helpText_G()}) {
            check(help != null && help.contains("linalg:"), "LinAlg help registered in both languages");
            check(help.contains("span(A)") && help.contains("showPlot=true"), "quick syntax and plotting help");
        }
        for (String help : new String[] {script.helpText(), script.helpText_G()}) {
            for (String function : new String[] {"det", "rank", "trace", "norm", "rref", "basis",
                    "nullspace", "transpose", "inv", "span", "svd", "eig"}) {
                check(help.contains(function + "(A)"), "documented function: " + function);
            }
            check(help.contains("showFormulas") && help.contains("--declarations--"), "declarations documented");
            check(!help.contains(veryFastPDF.HelpTexts.CONVERSION_BUTTONS_PLACEHOLDER),
                    "conversion button placeholder resolved");
        }
        mainServlet.WebLink.availablePDFTypes = availableTypes;
        script.createInstanceFromScript("linalg:\n" + a
                + "det(A)\nrank(A)\nspan(A)\nnullspace(A)\nx=norm((3,4))+rank(A)\n"
                + "Z=[0 0;0 0]\nspan(Z)\nnullspace([1 0;0 1])\nsvd(A)\neig(A)\n", null);
        String latex = script.toLaTeX();
        check(latex.contains("\\operatorname{det}(A) = 0"), "det rendered as operator");
        check(latex.contains("\\operatorname{rank}(A) = 2"), "rank rendered");
        check(latex.contains("\\operatorname{span}\\left\\{"), "span rendered as subspace");
        check(latex.contains("\\{\\mathbf{0}\\}"), "zero span rendered");
        check(latex.contains("2\\times 0"), "empty basis rendered with dimensions");
        check(!latex.contains("[1 0;0 1]"), "inline matrix rendered in math notation");
        check(script.createScriptFromInstance().contains("det(A)"), "serialization preserves calls");
        LinAlg steps = new LinAlg(null);
        steps.createInstanceFromScript("linalg:\nA=[1 -2;3 4]\ndet(A)\nd=det(A)\ntrace(A)\n"
                + "norm((-3,4))\nA=[5 0;0 6]\ndet(A)\nA=det(A)\n"
                + "B=[1 0 0;0 1 0;0 0 1]\ndet(B)\n"
                + "C=[0.00001 2;3 4]\ndet(C)\nnorm([1 2;3 4])\n", null);
        String stepLatex = steps.toLaTeX();
        check(stepLatex.contains("\\operatorname{det}(A) = 1 \\cdot 4 - \\left(-2\\right) \\cdot 3 = 10"),
                "2x2 determinant expanded with negative factor parentheses");
        check(stepLatex.contains("d = \\operatorname{det}(A) = 1 \\cdot 4"),
                "whole-call assignment expanded");
        check(stepLatex.contains("\\operatorname{trace}(A) = 1 + 4 = 5"), "trace expanded");
        check(stepLatex.contains("\\sqrt{\\left(-3\\right)^{2} + 4^{2}} = 5"), "vector norm expanded");
        check(stepLatex.contains("\\sqrt{1^{2} + 2^{2} + 3^{2} + 4^{2}}"), "small matrix norm expanded");
        check(stepLatex.contains("5 \\cdot 6 - 0 \\cdot 0 = 30"), "redefined input captured");
        check(stepLatex.contains("\\operatorname{det}(B) = 1$$"), "larger determinant remains concise");
        check(stepLatex.contains("0.00001 \\cdot 4"), "formula inputs retain precision");
        check(steps.createScriptFromInstance().contains("d = det(A)"), "expanded formulas do not rewrite source");
        check(!LinAlgScene.formatNumber(1e40, true).contains("922337"), "large numbers not saturated");
        if (args.length > 0) {
            Files.writeString(Path.of(args[0]), LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM
                    + latex + stepLatex + LaTeXCommands.POSTAMBLE_STANDARD);
        }
        System.out.println("Passed " + checks + " checks.");
    }

    private static Value evaluate(String body, String expression) {
        return LinAlgScene.parse(body + "result=" + expression).find("result").getValue();
    }

    private static void scalar(String body, String expression, double expected) {
        Value value = evaluate(body, expression);
        check(value.isScalar(), "scalar result: " + expression);
        close(value.getScalar(), expected);
    }

    private static void matrix(String body, String expression, double[][] expected) {
        Value value = evaluate(body, expression);
        check(!value.isScalar() && value.getRowCount() == expected.length, "matrix shape: " + expression);
        check(value.getColumnCount() == expected[0].length, "matrix columns: " + expression);
        for (int r = 0; r < expected.length; r++) {
            for (int c = 0; c < expected[r].length; c++) {
                close(value.getRows()[r][c], expected[r][c]);
            }
        }
    }

    private static void error(String body, String... messages) {
        try {
            LinAlgScene.parse(body);
            throw new AssertionError("Expected error: " + body);
        } catch (IllegalArgumentException e) {
            for (String message : messages) {
                check(e.getMessage().contains(message), e.getMessage() + " should contain " + message);
            }
        }
    }

    private static void close(double actual, double expected) {
        check(Double.isFinite(actual) && Math.abs(actual - expected) <= 1e-9 * Math.max(1, Math.abs(expected)),
                actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
