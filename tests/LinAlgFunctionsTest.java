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
        var exampleOptions = mainServlet.Wizz.class.getDeclaredMethod("exampleSelectOptions",
                String.class, veryFastPDF.script.RepresentableAsPDF.class, int.class, boolean.class);
        exampleOptions.setAccessible(true);
        for (boolean english : new boolean[] {true, false}) {
            String options = (String) exampleOptions.invoke(null, "LinAlg", script,
                    script.getExampleScripts().length, english);
            java.util.Set<String> names = new java.util.HashSet<>();
            for (int i = 0; i < script.getExampleScripts().length; i++) {
                String name = script.getExampleScriptName(i, english);
                check(name != null && !name.isBlank() && !name.matches("(Example|Beispiel) \\d+"),
                        "descriptive example label");
                check(names.add(name), "unique example label");
                check(options.contains("<option value=\"" + i + "\">"
                        + org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(name) + "</option>"),
                        "dropdown preserves index and localized label");
            }
        }
        LinAlg customLabel = new LinAlg(null) {
            @Override
            public String getExampleScriptName(int index, boolean english) {
                return "<example> & \"test\"";
            }
        };
        String escapedOptions = (String) exampleOptions.invoke(null, "LinAlg", customLabel, 1, true);
        check(escapedOptions.contains("&lt;example&gt; &amp; &quot;test&quot;"), "example labels HTML escaped");
        check(script.getExampleScriptName(9, true).equals("Example 10"), "default English example label");
        check(script.getExampleScriptName(9, false).equals("Beispiel 10"), "default German example label");
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
        check(latex.contains("\\operatorname{det}(A) = 1 \\cdot " + minor(new double[][] {{4, 6}, {1, 1}})),
                "det rendered as operator with 3x3 expansion");
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
                + "D=[1 2 3;4 5 6;7 8 10]\ndet(D)\nd=det(D)\n"
                + "E=[-1 2 3;4 -5 6;7 8 -10]\ndet(E)\n"
                + "F=[1 0 0 0;0 1 0 0;0 0 1 0;0 0 0 1]\ndet(F)\n"
                + "G=[1 2 3 4 5;6 7 8 9 10;11 12 13 14 15;16 17 18 19 20;21 22 23 24 25]\ndet(G)\n"
                + "H=[7]\ndet(H)\n"
                + "C=[0.00001 2;3 4]\ndet(C)\nnorm([1 2;3 4])\n", null);
        String stepLatex = steps.toLaTeX();
        String two = "1 \\cdot " + minor(new double[][] {{4}})
                + " - \\left(-2\\right) \\cdot " + minor(new double[][] {{3}}) + " = 10";
        check(stepLatex.contains("\\operatorname{det}(A) = " + two),
                "2x2 determinant expanded with negative factor parentheses");
        check(stepLatex.contains("d = \\operatorname{det}(A) = " + two),
                "whole-call assignment expanded");
        check(stepLatex.contains("\\operatorname{trace}(A) = 1 + 4 = 5"), "trace expanded");
        check(stepLatex.contains("\\sqrt{\\left(-3\\right)^{2} + 4^{2}} = 5"), "vector norm expanded");
        check(stepLatex.contains("\\sqrt{1^{2} + 2^{2} + 3^{2} + 4^{2}}"), "small matrix norm expanded");
        check(stepLatex.contains("5 \\cdot " + minor(new double[][] {{6}})
                + " - 0 \\cdot " + minor(new double[][] {{0}}) + " = 30"), "redefined input captured");
        String cofactor = "1 \\cdot " + minor(new double[][] {{5, 6}, {8, 10}})
                + " - 2 \\cdot " + minor(new double[][] {{4, 6}, {7, 10}})
                + " + 3 \\cdot " + minor(new double[][] {{4, 5}, {7, 8}}) + " = -3";
        check(stepLatex.contains("\\operatorname{det}(D) = " + cofactor), "3x3 cofactor expansion");
        check(stepLatex.contains("d = \\operatorname{det}(D) = " + cofactor), "3x3 assignment expansion");
        check(stepLatex.contains("\\left(-1\\right) \\cdot " + minor(new double[][] {{-5, 6}, {8, -10}})),
                "negative 3x3 factors parenthesized");
        check(stepLatex.contains("\\operatorname{det}(F) = 1 \\cdot "
                + minor(new double[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}})), "4x4 expansion");
        check(stepLatex.contains(" - 0 \\cdot "
                + minor(new double[][] {{0, 1, 0}, {0, 0, 1}, {0, 0, 0}}) + " = 1"),
                "fourth cofactor sign and minor");
        check(stepLatex.contains(" + 5 \\cdot " + minor(new double[][] {
                {6, 7, 8, 9}, {11, 12, 13, 14}, {16, 17, 18, 19}, {21, 22, 23, 24}})), "5x5 expansion");
        check(stepLatex.contains("\\operatorname{det}(H) = 7$$"), "1x1 base case");
        check(stepLatex.contains("0.00001 \\cdot " + minor(new double[][] {{4}})), "formula inputs retain precision");
        check(steps.createScriptFromInstance().contains("d = det(A)"), "expanded formulas do not rewrite source");
        check(!LinAlgScene.formatNumber(1e40, true).contains("922337"), "large numbers not saturated");
        LinAlg explained = new LinAlg(null);
        StringBuilder explainedBody = new StringBuilder("A=[0 2;1 3]\nu=(1,2)\nv=(2,4)\n");
        for (String function : new String[] {"det", "rank", "trace", "norm", "rref", "basis",
                "nullspace", "inv", "transpose", "span", "svd", "eig"}) {
            String upper = Character.toUpperCase(function.charAt(0)) + function.substring(1);
            LinAlgScene upperScene = LinAlgScene.parse("A=[0 2;1 3]\n" + upper + "(A)");
            Statement call = upperScene.getStatements().get(1);
            check(!call.getExplanations().isEmpty(), "explanation for " + upper);
            check(call.getExplanations().get(0).function.equals(function), "canonical function");
            LinAlgScene lowerScene = LinAlgScene.parse("A=[0 2;1 3]\n" + function + "(A)");
            check(lowerScene.getStatements().get(1).getExplanations().isEmpty(), "lowercase concise");
            Value upperValue = call.getValue();
            Value lowerValue = lowerScene.getStatements().get(1).getValue();
            check(upperValue.isScalar() == lowerValue.isScalar(), "same result type");
            for (int r = 0; r < upperValue.getRowCount(); r++) {
                for (int c = 0; c < upperValue.getColumnCount(); c++) {
                    close(upperValue.getRows()[r][c], lowerValue.getRows()[r][c]);
                }
            }
            explainedBody.append(upper).append("(A)\n");
        }
        explainedBody.append("Span(u,v)\nx=Det(A)+Rank(A)\nA=Inv(A)\n");
        explained.createInstanceFromScript("linalg:\n" + explainedBody, null);
        String explanations = explained.toLaTeX();
        check(explanations.contains("Gaussian elimination"), "det algorithm text");
        check(!explanations.contains("\\det\\left("), "capitalized Det omits Laplace minors");
        check(explanations.contains("\\operatorname{Det}(A) = -2$$"), "Det shows direct final result");
        LinAlg detAssignment = new LinAlg(null);
        detAssignment.createInstanceFromScript("linalg:\nA=[1 2;3 4]\nd=Det(A)", null);
        check(detAssignment.toLaTeX().contains("d = \\operatorname{Det}(A) = -2$$"),
                "assigned Det omits Laplace expansion");
        check(!detAssignment.toLaTeX().contains("\\det\\left("), "assigned Det has no minors");
        check(explanations.contains("Gauss--Jordan"), "row reduction algorithm text");
        check(explanations.contains("\\leftrightarrow"), "actual row swap");
        check(explanations.contains("\\gets"), "actual row updates");
        check(explanations.contains("ORIGINAL"), "original column selection explained");
        check(explanations.contains("free variables"), "nullspace explained");
        check(explanations.contains("One-sided Jacobi"), "SVD algorithm explained");
        check(explanations.contains("QR iteration"), "eigen algorithm explained");
        check(explained.createScriptFromInstance().contains("x = Det(A)+Rank(A)"), "uppercase source preserved");
        LinAlgScene nested = LinAlgScene.parse("A=[1 0;0 2]\nx=Det(Transpose(A))+Rank(A)");
        check(nested.find("x").getExplanations().size() == 3, "nested explanation order captured");
        LinAlgScene nestedDisplay = LinAlgScene.parse("A=[1 0;0 2]\nsvd(Transpose(A))");
        check(nestedDisplay.getStatements().get(1).getExplanations().size() == 1,
                "nested explanation inside display-only call");
        LinAlgScene reductions = LinAlgScene.parse("A=[0 2;1 3]\nRref(A)\nInv(A)\nDet(A)");
        var reductionSteps = reductions.getStatements().get(1).getExplanations().get(0).steps;
        double[][] finalReduction = reductionSteps.get(reductionSteps.size() - 1).rows;
        close(finalReduction[0][0], 1);
        close(finalReduction[0][1], 0);
        close(finalReduction[1][0], 0);
        close(finalReduction[1][1], 1);
        var inverseSteps = reductions.getStatements().get(2).getExplanations().get(0).steps;
        LinAlg inverseDisplay = new LinAlg(null);
        inverseDisplay.createInstanceFromScript("linalg:\nA=[0 2;1 3]\nInv(A)", null);
        String inverseLatex = inverseDisplay.toLaTeX();
        check(inverseLatex.split(java.util.regex.Pattern.quote("\\begin{array}{cc|cc}"), -1).length - 1
                == inverseSteps.size(), "every inverse step has a divider between equal halves");
        check(inverseLatex.contains("\\end{array}\\right)"), "augmented array retains parentheses");
        check(inverseLatex.contains("\\operatorname{Inv}(A) = \\begin{pmatrix}"),
                "final inverse remains an ordinary matrix");
        double[][] finalInverse = inverseSteps.get(inverseSteps.size() - 1).rows;
        close(finalInverse[0][0], 1);
        close(finalInverse[1][1], 1);
        close(finalInverse[0][2], -1.5);
        close(finalInverse[0][3], 1);
        close(finalInverse[1][2], 0.5);
        close(finalInverse[1][3], 0);
        check(reductionSteps.get(0).rows[0][1] == 2, "step snapshots are not mutated");
        scalar("A=[0 2;1 3]\n", "Det(A)+Rank(A)", 0);
        error("A=[1]\nx=Span(A)", "Line 2:", "own line");
        LinAlgScene zeroRank = LinAlgScene.parse("A=[0 0;0 0]\nRank(A)");
        check(zeroRank.getStatements().get(1).getValue().getScalar() == 0, "zero rank explained");
        String gradientBody = "f(x,y)=x^2+2*x*y+3*y^2\np=(1,2)\n";
        matrix(gradientBody, "grad(f,p)", new double[][] {{6}, {14}});
        matrix(gradientBody, "Grad(f,p)", new double[][] {{6}, {14}});
        matrix(gradientBody + "A=[1 0;0 2]\n", "A*grad(f,p)", new double[][] {{6}, {28}});
        matrix("f(x,y,z)=x*y+z^2\n", "grad(f,(2,3,4))", new double[][] {{3}, {2}, {8}});
        matrix("f(x)=sin(x)+cos(x)+exp(x)+log(x)\n", "grad(f,[1])",
                new double[][] {{Math.cos(1) - Math.sin(1) + Math.exp(1) + 1}});
        matrix("f(x,y)=x/y\n", "grad(f,(6,2))", new double[][] {{0.5}, {-1.5}});
        matrix("f(x)=-x^2\n", "grad(f,[3])", new double[][] {{-6}});
        matrix("f(x)=x^-2\n", "grad(f,[2])", new double[][] {{-0.25}});
        matrix("f(x)=x^0\n", "grad(f,[0])", new double[][] {{0}});
        matrix("f(x)=x^1\n", "grad(f,[0])", new double[][] {{1}});
        matrix("f(x)=x^2^3\n", "grad(f,[2])", new double[][] {{1024}});
        matrix("f(x)=sin(x^2)\n", "grad(f,[2])", new double[][] {{4 * Math.cos(4)}});
        matrix("f(x,y)=x^y\n", "grad(f,(2,3))", new double[][] {{12}, {8 * Math.log(2)}});
        matrix("f(x)=7\n", "grad(f,[0])", new double[][] {{0}});
        String vectorBody = "F(x,y)=(x*y,sin(x),x^2+y^2)\np=(2,3)\n";
        matrix(vectorBody, "jacobian(F,p)", new double[][] {{3, 2}, {Math.cos(2), 0}, {4, 6}});
        matrix(gradientBody, "jacobian(f,p)", new double[][] {{6, 14}});
        matrix(gradientBody, "hessian(f,p)", new double[][] {{2, 2}, {2, 6}});
        matrix(gradientBody, "Hessian(f,p)", new double[][] {{2, 2}, {2, 6}});
        scalar(gradientBody, "laplacian(f,p)", 8);
        scalar(gradientBody, "trace(hessian(f,p))", 8);
        matrix("f(x,y)=x^3*y+sin(x*y)\n", "hessian(f,(1,2))",
                new double[][] {{12 - 4 * Math.sin(2), 3 + Math.cos(2) - 2 * Math.sin(2)},
                        {3 + Math.cos(2) - 2 * Math.sin(2), -Math.sin(2)}});
        matrix("f(x)=log(x)+exp(x)\n", "hessian(f,[2])",
                new double[][] {{-0.25 + Math.exp(2)}});
        matrix("f(x,y)=7\n", "hessian(f,(0,0))", new double[][] {{0, 0}, {0, 0}});
        scalar("F(x,y)=(x*y,x^2+y^2)\n", "divergence(F,(2,3))", 9);
        scalar("F(x,y)=(x*y,x^2+y^2)\n", "trace(jacobian(F,(2,3)))", 9);
        matrix("F(x,y,z)=(-y,x,0)\n", "curl(F,(1,2,3))", new double[][] {{0}, {0}, {2}});
        matrix("F(x,y,z)=(y*z,x*z,x*y)\n", "curl(F,(1,2,3))", new double[][] {{0}, {0}, {0}});
        matrix("F(x,y,z)=(x*y,y*z,z*x)\n", "curl(F,(2,3,4))", new double[][] {{-3}, {-4}, {-2}});
        scalar("f(x)=x^2\n", "divergence(f,[3])", 6);
        matrix("f(x)=x^3\n", "jacobian(f,[2])", new double[][] {{12}});
        var oneVariable = new veryFastPDF.algorithms.linearTrans.ScalarFunction(java.util.List.of("x"), "x^3");
        check(oneVariable.derivativeLaTeX("hessian").contains("pmatrix"), "symbolic 1x1 Hessian is a matrix");
        check(oneVariable.derivativeLaTeX("jacobian").contains("pmatrix"), "symbolic 1x1 Jacobian is a matrix");
        check(!oneVariable.derivativeLaTeX("laplacian").contains("pmatrix"), "symbolic Laplacian is scalar");
        matrix("F(x,y)=((x+y)*x,exp(y))\n", "jacobian(F,(2,0))",
                new double[][] {{4, 2}, {0, 1}});
        matrix("F(x,y)=((x+y),cos(y))\n", "jacobian(F,(2,0))",
                new double[][] {{1, 1}, {0, 0}});
        matrix("f(x,y)=(x+y)*x\n", "hessian(f,(2,0))", new double[][] {{2, 1}, {1, 0}});
        matrix("f(x)=x^2\n", "grad(f,[0])", new double[][] {{0}});
        matrix("f(x)=x^2\n", "hessian(f,[0])", new double[][] {{2}});
        error("F(x,y)=(x,y)\nhessian(F,(1,2))", "Line 2:", "scalar-valued");
        error("F(x,y)=(x,y)\ngrad(F,(1,2))", "Line 2:", "scalar-valued");
        error("F(x,y)=(x,y)\nlaplacian(F,(1,2))", "Line 2:", "scalar-valued");
        error("F(x,y)=(x,y,x)\ndivergence(F,(1,2))", "Line 2:", "one component");
        error("f(x,y)=x+y\ndivergence(f,(1,2))", "Line 2:", "one component");
        error("F(x,y)=(x,y)\ncurl(F,(1,2))", "Line 2:", "three-component");
        error("F(x,y,z)=(x,y)\ncurl(F,(1,2,3))", "Line 2:", "three-component");
        error("F(x,y)=(x,)\njacobian(F,(1,2))", "Line 1:", "scalar expression");
        error("F(x,y)=(x,y)+1", "Line 1:", "Unexpected");
        error("F(x,y)=(log(x),y)\njacobian(F,(-1,2))", "Line 2:", "undefined");
        error("f(x,y)=x+y\nhessian(f,[1])", "Line 2:", "expected 2");
        error("F(x,y)=(x,y)\njacobian(F,[1 2])", "Line 2:", "column vector");
        error("hessian(x)=x", "Line 1:", "Reserved");
        error("f(x)=x\njacobian(unknown,[1])", "Line 2:", "Unknown");
        LinAlg calculus = new LinAlg(null);
        String calculusBody = "f(x,y)=x^2+2*x*y+3*y^2\np=(1,2)\nF(x,y)=(x*y,sin(x))\n"
                + "G(x,y,z)=(-y,x,0)\nHessian(f,p)\nJacobian(F,p)\nLaplacian(f,p)\n"
                + "Divergence(F,p)\nCurl(G,(1,2,3))\nJ=jacobian(F,p)\nH=hessian(f,p)\n";
        calculus.createInstanceFromScript("linalg:\n" + calculusBody, null);
        String calculusLatex = calculus.toLaTeX();
        check(calculusLatex.contains("Rows correspond to output"), "Jacobian orientation explained");
        check(calculusLatex.contains("symmetric"), "Hessian symmetry explained");
        check(calculusLatex.contains("trace of the Hessian"), "Laplacian explained");
        check(calculusLatex.contains("trace of the Jacobian"), "divergence explained");
        check(calculusLatex.contains("\\nabla\\times F"), "curl explained");
        check(calculusLatex.contains("\\operatorname{hessian}(f)"), "symbolic Hessian rendered");
        check(calculusLatex.contains("\\operatorname{jacobian}(F)"), "symbolic Jacobian rendered");
        String mlBody = "A=[0 2;1 3]\nf(x,y)=x^2+y^2\nF(x,y)=(x*y,x+y)\n"
                + "G(x,y,z)=(-y,x,0)\np=(1,2)\n";
        String[][] mlCalls = {
                {"Det(A)", "volume scaling"}, {"Rank(A)", "redundant features"},
                {"Rref(A)", "linear constraints"}, {"Basis(A)", "not necessarily orthogonal"},
                {"Span(A)", "representational limits"}, {"Nullspace(A)", "unidentifiable parameters"},
                {"Inv(A)", "solve linear systems"}, {"Trace(A)", "total variance"},
                {"Norm(A)", "gradient clipping"}, {"Transpose(A)", "linear layers"},
                {"Svd(A)", "centered data"}, {"Eig(A)", "PCA directions"},
                {"Grad(f,p)", "gradient descent"}, {"Jacobian(F,p)", "backpropagation"},
                {"Hessian(f,p)", "loss curvature"}, {"Laplacian(f,p)", "not this operator"},
                {"Divergence(F,p)", "log-density changes"}, {"Curl(G,(1,2,3))", "local rotation"}
        };
        for (String[] entry : mlCalls) {
            LinAlg upper = new LinAlg(null);
            upper.createInstanceFromScript("linalg:\n" + mlBody + entry[0], null);
            String upperLatex = upper.toLaTeX();
            check(upperLatex.contains("\\textbf{ML use:} ") && upperLatex.contains(entry[1]),
                    "function-specific ML use for " + entry[0]);
            check(upperLatex.indexOf("\\textbf{ML use:}") < upperLatex.lastIndexOf("$$"),
                    "ML use precedes result for " + entry[0]);
            LinAlg lower = new LinAlg(null);
            String lowerCall = Character.toLowerCase(entry[0].charAt(0)) + entry[0].substring(1);
            lower.createInstanceFromScript("linalg:\n" + mlBody + lowerCall, null);
            check(!lower.toLaTeX().contains("\\textbf{ML use:}"), "lowercase omits ML use for " + lowerCall);
        }
        LinAlg nestedMl = new LinAlg(null);
        nestedMl.createInstanceFromScript("linalg:\n" + mlBody + "d=Det(Transpose(A))+Rank(A)", null);
        check(nestedMl.toLaTeX().split(java.util.regex.Pattern.quote("\\textbf{ML use:}"), -1).length - 1 == 3,
                "nested and assigned calls each retain ML use");
        for (String function : new String[] {"jacobian", "hessian", "laplacian", "divergence", "curl"}) {
            String upper = Character.toUpperCase(function.charAt(0)) + function.substring(1);
            String body = function.matches("hessian|laplacian") ? gradientBody
                    : function.equals("curl") ? "f(x,y,z)=(-y,x,0)\np=(1,2,3)\n"
                    : "f(x,y)=(x*y,x+y)\np=(1,2)\n";
            Value low = evaluate(body, function + "(f,p)");
            Value high = evaluate(body, upper + "(f,p)");
            check(low.isScalar() == high.isScalar(), "calculus capitalized result type");
            for (int r = 0; r < low.getRowCount(); r++) {
                for (int c = 0; c < low.getColumnCount(); c++) {
                    close(low.getRows()[r][c], high.getRows()[r][c]);
                }
            }
        }
        LinAlgScene calculusScene = LinAlgScene.parse(calculusBody);
        check(calculusScene.toScript().equals(LinAlgScene.parse(calculusScene.toScript()).toScript()),
                "vector functions and calculus source roundtrip");
        java.io.ByteArrayOutputStream calculusBytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(calculusBytes)) {
            output.writeObject(calculusScene);
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(calculusBytes.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) input.readObject();
            check(restored.toScript().equals(calculusScene.toScript()), "calculus scene serialization");
            check(restored.getStatements().get(5).getExplanations().get(0).scalarFunction
                    .derivativeLaTeX("jacobian").contains("pmatrix"), "vector derivative trees serialized");
        }
        matrix("f(z,x)=z^2*x\n", "hessian(f,(2,3))", new double[][] {{6, 4}, {4, 0}});
        scalar("f(w,x,y,z)=w^2+x^2+y^2+z^2\n", "laplacian(f,(1,2,3,4))", 8);
        matrix("F(x)=(x,x^2,x^3)\n", "jacobian(F,[2])", new double[][] {{1}, {4}, {12}});
        scalar("F(a,b,c,d)=(a,b,c,d)\n", "divergence(F,(1,2,3,4))", 4);
        error("f(x,x)=x", "Line 1:", "distinct");
        error("f(x)=z+x", "Line 1:", "Unknown scalar");
        error("f(x)=sqrt(x)", "Line 1:", "Unknown scalar");
        error("f(x)=log(x)\ngrad(f,[-1])", "Line 2:", "undefined");
        error("f(x)=1/x\ngrad(f,[0])", "Line 2:", "undefined");
        error("f(x,y)=x+y\ngrad(f,[1])", "Line 2:", "expected 2");
        error("grad(missing,(1,2))", "Line 1:", "Unknown scalar function");
        error("f(x)=x\ngrad(f,2)", "Line 2:", "column vector");
        error("f(x,y)=x+y\ngrad(f,[1 2])", "Line 2:", "column vector");
        error("grad(x)=x", "Line 1:", "Reserved");
        LinAlg gradients = new LinAlg(null);
        gradients.createInstanceFromScript("linalg:\n" + gradientBody + "g=grad(f,p)\nGrad(f,p)\n", null);
        String gradientLatex = gradients.toLaTeX();
        check(gradientLatex.contains("\\nabla f"), "symbolic gradient displayed");
        check(gradientLatex.contains("chain rules"), "differentiation explained");
        LinAlg namedGradient = new LinAlg(null);
        namedGradient.createInstanceFromScript("linalg:\nh(t)=t^3\nGrad(h,[2])", null);
        check(namedGradient.toLaTeX().contains("\\nabla h"), "actual scalar function name in explanation");
        check(gradientLatex.contains("6") && gradientLatex.contains("14"), "numeric gradient rendered");
        String gradientScript = gradients.createScriptFromInstance();
        check(gradientScript.contains("f(x,y)=x^2+2*x*y+3*y^2"), "function source preserved");
        check(gradientScript.contains("g = grad(f,p)"), "gradient source preserved");
        LinAlgScene gradientScene = LinAlgScene.parse(gradientBody + "g=grad(f,p)");
        close(gradientScene.find("g").getValue().getOrigin()[0], 1);
        close(gradientScene.find("g").getValue().getOrigin()[1], 2);
        String plot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(gradientScene);
        check(plot.contains("(1,2) -- (7,16)"), "2D gradient arrow anchored at point");
        LinAlgScene gradient3D = LinAlgScene.parse("f(x,y,z)=x*y+z^2\ng=grad(f,(2,3,4))");
        check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(gradient3D).contains("(2,3,4) -- (5,5,12)"),
                "3D gradient arrow anchored at point");
        LinAlgScene curlPlot = LinAlgScene.parse("F(x,y,z)=(-y,x,0)\nc=curl(F,(1,2,3))");
        check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(curlPlot).contains("(1,2,3) -- (1,2,5)"),
                "curl vector anchored at evaluation point");
        check(gradientScene.toScript().equals(LinAlgScene.parse(gradientScene.toScript()).toScript()),
                "gradient scene roundtrip");
        java.io.ByteArrayOutputStream serialized = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(serialized)) {
            output.writeObject(LinAlgScene.parse(gradientBody + "g=Grad(f,p)"));
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(serialized.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) input.readObject();
            check(restored.find("g").getExplanations().get(0).scalarFunction.gradientLaTeX().contains("pmatrix"),
                    "symbolic gradients survive scene serialization");
            close(restored.find("g").getValue().getOrigin()[1], 2);
        }
        matrix("f(x)=x^2\ng=grad(f,[3])\nf(x)=x^3\n", "g", new double[][] {{6}});
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        for (String help : new String[] {gradients.helpText(), gradients.helpText_G()}) {
            check(help.contains("grad(f, p)") && help.contains("Grad(f, p)"), "gradient web help");
            for (String operator : new String[] {"jacobian", "hessian", "laplacian", "divergence", "curl"}) {
                check(help.contains(operator + "("), "calculus web help: " + operator);
            }
        }
        mainServlet.WebLink.availablePDFTypes = availableTypes;
        for (String example : gradients.getExampleScripts()) {
            LinAlg exampleScript = new LinAlg(null);
            exampleScript.createInstanceFromScript(example, null);
            check(!exampleScript.toLaTeX().isBlank(), "all built-in examples still render");
        }
        if (args.length > 0) {
            Files.writeString(Path.of(args[0]), LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM
                    .replace("\\begin{document}", "\\usepackage{tikz}\n\\begin{document}")
                    + latex + stepLatex + explanations + gradientLatex
                    + calculusLatex
                    + "\\begin{center}" + plot + "\\end{center}"
                    + "\\begin{center}" + veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(gradient3D)
                    + "\\end{center}" + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("gradient-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM.replace(
                            "\\begin{document}", "\\usepackage{tikz}\n\\begin{document}")
                            + gradientLatex + "\\begin{center}" + plot + "\\end{center}"
                            + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("calculus-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM + calculusLatex
                            + LaTeXCommands.POSTAMBLE_STANDARD);
        }
        System.out.println("Passed " + checks + " checks.");
    }

    private static String minor(double[][] rows) {
        StringBuilder result = new StringBuilder("\\det\\left(\\begin{pmatrix}\n");
        for (double[] row : rows) {
            for (int c = 0; c < row.length; c++) {
                result.append(c == 0 ? "" : " & ").append(LinAlgScene.formatNumber(row[c], false));
            }
            result.append(" \\\\\n");
        }
        return result.append("\\end{pmatrix}\\right)").toString();
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
