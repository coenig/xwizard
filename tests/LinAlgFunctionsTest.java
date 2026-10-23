import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.algorithms.linearTrans.LinAlgScene;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Statement;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;
import veryFastPDF.algorithms.linearTrans.SingularValueDecomposition;
import veryFastPDF.algorithms.linearTrans.EigenDecomposition;

/** Dependency-free regression checks; run with target/classes and application jars on the classpath. */
public class LinAlgFunctionsTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        verifyOperationMarkers();
        verifyStatistics(args);
        verifyProbabilityMetrics(args);
        verifyDistributions(args);
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
        matrix("A=[4 1;2 3]\n", "eigenvalues(A)", new double[][] {{5}, {2}});
        matrix("A=[1 0;0 3]\n", "Eigenvalues(A)", new double[][] {{3}, {1}});
        matrix("A=[1 1;0 1]\n", "eigenvalues(A)", new double[][] {{1}, {1}});
        matrix("A=[1 1;0 1]\n", "Eigenvectors(A)", new double[][] {{1}, {0}});
        matrix("A=[-2]\n", "eigenvalues(A)", new double[][] {{-2}});
        matrix("A=[-2]\n", "eigenvectors(A)", new double[][] {{1}});
        for (String literal : new String[] {"[4 1;2 3]", "[2 1;1 2]", "[0 0;0 0]",
                "[2 0 0;0 2 0;0 0 -1]", "[4 1 0;2 3 0;0 0 1]"}) {
            Value eigenMatrix = evaluate("", literal);
            String body = "A=" + literal + "\n";
            Value values = evaluate(body, "eigenvalues(A)");
            Value vectors = evaluate(body, "eigenvectors(A)");
            Value explainedVectors = evaluate(body, "Eigenvectors(A)");
            check(vectors.getColumnCount() == values.getRowCount(), "diagonalizable eigenpair counts");
            for (int c = 0; c < vectors.getColumnCount(); c++) {
                double norm = 0;
                for (int r = 0; r < vectors.getRowCount(); r++) {
                    double av = 0;
                    for (int j = 0; j < vectors.getRowCount(); j++) {
                        av += eigenMatrix.getRows()[r][j] * vectors.getRows()[j][c];
                    }
                    close(av, values.getRows()[c][0] * vectors.getRows()[r][c]);
                    close(explainedVectors.getRows()[r][c], vectors.getRows()[r][c]);
                    norm += vectors.getRows()[r][c] * vectors.getRows()[r][c];
                }
                close(norm, 1);
            }
            check(LinAlgScene.parse(body + "x=rank(eigenvectors(A))+norm(eigenvalues(A))")
                    .find("x").getValue().isScalar(), "eigen results reusable in nested expressions");
        }
        error("A=[1 2]\neigenvalues(A)", "Line 2:", "square");
        error("A=[1 2]\neigenvectors(A)", "Line 2:", "square");
        error("eigenvalues(2)", "Line 1:", "matrix or vector");
        error("eigenvalues()", "Line 1:", "exactly one");
        error("eigenvectors([1],[1])", "Line 1:", "exactly one");
        error("A=[0 -1;1 0]\neigenvalues(A)", "Line 2:", "non-real");
        error("A=[0 -1;1 0]\neigenvectors(A)", "Line 2:", "non-real");
        error("A=[1e999]\neigenvalues(A)", "Line 2:", "finite");
        error("eigenvalues(x)=x", "Line 1:", "Reserved");
        error("eigenvectors(x)=x", "Line 1:", "Reserved");
        error("evd(x)=x", "Line 1:", "Reserved");
        error("A=[1]\nx=evd(A)", "Line 2:", "own line");
        LinAlg eigenScript = new LinAlg(null);
        String eigenBody = "A=[4 1;2 3]\nvalues=eigenvalues(A)\nvectors=eigenvectors(A)\n"
                + "Eigenvalues(A)\nEigenvectors(A)\nevd(A)\nEvd(A)\nevd(Transpose(A))";
        eigenScript.createInstanceFromScript("linalg:\n" + eigenBody, null);
        String eigenLatex = eigenScript.toLaTeX();
        check(eigenLatex.contains("\\operatorname{eigenvalues}(A)")
                && eigenLatex.contains("\\operatorname{eigenvectors}(A)"), "standalone eigen calls formatted");
        check(eigenLatex.contains("$Av=\\lambda v$") && eigenLatex.contains("algebraic multiplicity")
                && eigenLatex.contains("fewer columns"), "plain eigen function explanations");
        check(eigenScript.createScriptFromInstance().contains("evd(A)")
                && eigenScript.createScriptFromInstance().contains("values = eigenvalues(A)"),
                "eigen source expressions preserved");
        check(LinAlgScene.parse(eigenBody).toScript().equals(
                LinAlgScene.parse(LinAlgScene.parse(eigenBody).toScript()).toScript()), "eigen calls roundtrip");
        for (String function : new String[] {"eig", "Eig"}) {
            LinAlg oldScript = new LinAlg(null), newScript = new LinAlg(null);
            oldScript.createInstanceFromScript("linalg:\nA=[4 1;2 3]\n" + function + "(A)", null);
            newScript.createInstanceFromScript("linalg:\nA=[4 1;2 3]\n"
                    + (function.equals("Eig") ? "Evd" : "evd") + "(A)", null);
            check(oldScript.toLaTeX().replace("\\textbf{eig:}", "\\textbf{evd:}")
                    .equals(newScript.toLaTeX()), "legacy eig alias retains behavior");
        }
        LinAlg defectiveEvd = new LinAlg(null);
        defectiveEvd.createInstanceFromScript("linalg:\nA=[1 1;0 1]\nevd(A)", null);
        check(defectiveEvd.toLaTeX().contains("defective"), "EVD still reports defective matrices");
        matrix("A=[2 1 0;0 2 0;0 0 3]\n", "eigenvalues(A)", new double[][] {{3}, {2}, {2}});
        Value defectiveVectors = evaluate("A=[2 1 0;0 2 0;0 0 3]\n", "eigenvectors(A)");
        check(defectiveVectors.getRowCount() == 3 && defectiveVectors.getColumnCount() == 2,
                "mixed defective spectrum yields independent eigenspace columns");
        close(Math.abs(defectiveVectors.getRows()[2][0]), 1);
        close(Math.abs(defectiveVectors.getRows()[0][1]), 1);
        close(defectiveVectors.getRows()[1][1], 0);
        java.io.ByteArrayOutputStream eigenBytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(eigenBytes)) {
            output.writeObject(LinAlgScene.parse(eigenBody));
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(eigenBytes.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) input.readObject();
            check(restored.toScript().equals(LinAlgScene.parse(eigenBody).toScript()),
                    "eigen calls survive scene serialization");
            close(restored.find("values").getValue().getRows()[0][0], 5);
        }

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
        String evolutionBody = "A=[0 2;1 3]\nf(x,y)=sin(x^2)+x*y+y^2\np=(1,2)\n"
                + "F(x,y)=(x*y,sin(x))\nG(x,y,z)=(-y,x,0)\n";
        String[][] evolutionCases = {
                {"Det(A)", "(-1)^{1}\\cdot"}, {"Rank(A)", "number of pivots"},
                {"Rref(A)", "\\gets"}, {"Inv(A)", "cc|cc"}, {"Basis(A)", "original column"},
                {"Span(A)", "original column"}, {"Nullspace([1 2])", "General solution"},
                {"Trace(A)", "s_{2}=s_{1}+a_{2,2}=0+3=3"},
                {"Norm((3,4))", "h_{2}=\\operatorname{hypot}(3,4)=5"},
                {"Transpose(A)", "\\text{Input row }2"},
                {"Svd(A)", "Jacobi sweep"}, {"Evd(A)", "B=A-\\lambda I"},
                {"Eigenvalues(A)", "\\text{Block }"}, {"Eigenvectors(A)", "\\text{Eigenspace SVD sweep }"},
                {"Grad(f,p)", "sin and chain rule"}, {"Jacobian(F,p)", "product rule"},
                {"Hessian(f,p)", "Substitute the point, entry }(2,2)"},
                {"Laplacian(f,p)", "Accumulate term }2"}, {"Divergence(F,p)", "Accumulate term }2"},
                {"Curl(G,(1,2,3))", "Curl component }3"}
        };
        StringBuilder evolutionLatex = new StringBuilder();
        for (String[] evolution : evolutionCases) {
            LinAlg upper = new LinAlg(null), lower = new LinAlg(null);
            upper.createInstanceFromScript("linalg:\n" + evolutionBody + evolution[0], null);
            String upperLatex = upper.toLaTeX();
            check(upperLatex.contains(evolution[1]),
                    "worked algorithm evolution for " + evolution[0]);
            String lowerCall = Character.toLowerCase(evolution[0].charAt(0)) + evolution[0].substring(1);
            lower.createInstanceFromScript("linalg:\n" + evolutionBody + lowerCall, null);
            check(!lower.toLaTeX().contains(evolution[1]), "lowercase remains concise: " + lowerCall);
            evolutionLatex.append(upperLatex);
        }
        String[] eigenTraceInputs = {"[2 1;1 2]", "[1 2 3;4 5 6;7 8 10]",
                "[1 0;0 2]", "[1 1;0 1]", "[0 -1;1 0]"};
        for (String literal : eigenTraceInputs) verifyEigenEvolution(literal);
        LinAlg symmetricEvolution = new LinAlg(null);
        symmetricEvolution.createInstanceFromScript("linalg:\nA=[2 1;1 2]\nEvd(A)", null);
        check(symmetricEvolution.toLaTeX().contains("D\\gets J^TDJ")
                && symmetricEvolution.toLaTeX().contains("Q\\gets QJ"), "symmetric EVD actual rotations shown");
        LinAlg qrEvolution = new LinAlg(null);
        qrEvolution.createInstanceFromScript("linalg:\nA=[1 2 3;4 5 6;7 8 10]\nEvd(A)", null);
        check(qrEvolution.toLaTeX().contains("\\text{QR iteration }1")
                && qrEvolution.toLaTeX().contains("T_{k+1}=R_kQ_k"), "general EVD QR evolution shown");
        check(qrEvolution.toLaTeX().contains("after }10\\text{ QR iterations")
                && !qrEvolution.toLaTeX().contains("\\text{QR iteration }9:"),
                "long QR trace labels selected iterations and retains final spectral matrix");
        check(qrEvolution.toLaTeX().contains("Scale each eigenvector column")
                && qrEvolution.toLaTeX().contains("Multiply by the inverse"), "EVD reconstruction worked out");
        LinAlg chainEvolution = new LinAlg(null);
        chainEvolution.createInstanceFromScript("linalg:\nh(t)=sin(t^2)+exp(t)/t+log(t)+t^t\nGrad(h,2)", null);
        check(chainEvolution.toLaTeX().contains("quotient rule")
                && chainEvolution.toLaTeX().contains("power and chain rule")
                && chainEvolution.toLaTeX().contains("log and chain rule")
                && chainEvolution.toLaTeX().contains("exp and chain rule"),
                "differentiation traces quotient, general power and unary chain rules");
        LinAlg emptyEvolution = new LinAlg(null);
        emptyEvolution.createInstanceFromScript("linalg:\nA=[1 0;0 1]\nTranspose(nullspace(A))", null);
        check(emptyEvolution.toLaTeX().contains("No entries to move")
                && emptyEvolution.toLaTeX().contains("0\\times 2"), "empty transpose dimensions explained");
        LinAlg failedEvolution = new LinAlg(null);
        failedEvolution.createInstanceFromScript("linalg:\nA=[0 -1;1 0]\nEvd(A)", null);
        check(failedEvolution.toLaTeX().contains("\\text{Block }1")
                && failedEvolution.toLaTeX().contains("non-real"), "failed EVD retains block calculation");
        LinAlg nestedEvolution = new LinAlg(null);
        nestedEvolution.createInstanceFromScript("linalg:\nA=[1 0;0 3]\nx=Norm(Eigenvalues(A))\nA=[7]", null);
        check(nestedEvolution.toLaTeX().contains("symmetric") || nestedEvolution.toLaTeX().contains("Symmetric"),
                "nested eigen explanation captured before redefinition");
        check(nestedEvolution.toLaTeX().contains("\\operatorname{hypot}(0,3)"), "nested norm uses eigenvalues snapshot");
        java.io.ByteArrayOutputStream evolutionBytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(evolutionBytes)) {
            output.writeObject(nestedEvolution.getScene());
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(evolutionBytes.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) input.readObject();
            check(restored.find("x").getExplanations().get(0).steps.stream()
                    .anyMatch(s -> s.operation.contains("Sort diagonal")), "numerical trace survives serialization");
        }
        LinAlg nullspaceSteps = new LinAlg(null);
        nullspaceSteps.createInstanceFromScript("linalg:\nA=[1 2 3;2 4 6;1 1 1]\nN=Nullspace(A)", null);
        String nullspaceLatex = nullspaceSteps.toLaTeX();
        check(nullspaceLatex.contains("\\ker(A)=\\{x\\mid Ax=0\\}") && nullspaceLatex.contains("Rank--nullity"),
                "kernel meaning and rank-nullity explained");
        check(nullspaceLatex.contains("x_{3}=t_{1}") && nullspaceLatex.contains("x_{1}=1\\,t_{1}")
                && nullspaceLatex.contains("x_{2}=\\left(-2\\right)\\,t_{1}"), "actual nullspace pivot equations");
        check(nullspaceLatex.contains("Set $t_{1}=1$") && nullspaceLatex.contains("x=t_{1}v_{1}=Nt"),
                "basis construction and general solution");
        check(nullspaceLatex.contains("AN=0") && nullspaceLatex.contains("A(\\theta+Nt)=A\\theta"),
                "kernel identity and ML interpretation");
        LinAlg trivialKernel = new LinAlg(null);
        trivialKernel.createInstanceFromScript("linalg:\nA=[1 0;0 1]\nNullspace(A)", null);
        check(trivialKernel.toLaTeX().contains("empty basis")
                && trivialKernel.toLaTeX().contains("\\ker(A)=\\{\\mathbf{0}\\}")
                && trivialKernel.toLaTeX().contains("2\\times 0"), "trivial kernel explained accurately");
        LinAlg multipleFree = new LinAlg(null);
        multipleFree.createInstanceFromScript("linalg:\nA=[0 1 2 3;0 0 0 0]\nNullspace(A)", null);
        String multipleFreeLatex = multipleFree.toLaTeX();
        check(multipleFreeLatex.contains("x_{1}=t_{1}") && multipleFreeLatex.contains("x_{3}=t_{2}")
                && multipleFreeLatex.contains("x_{4}=t_{3}"), "nonleading pivots and multiple free variables");
        check(multipleFreeLatex.contains("x_{2}=\\left(-2\\right)\\,t_{2} + \\left(-3\\right)\\,t_{3}"),
                "multiple-free pivot equation");
        LinAlg zeroKernel = new LinAlg(null);
        zeroKernel.createInstanceFromScript("linalg:\nA=[0 0;0 0]\nNullspace(A)", null);
        check(zeroKernel.toLaTeX().contains("x=t_{1}v_{1}+t_{2}v_{2}=Nt")
                && !zeroKernel.toLaTeX().contains("Solve the pivot equations"), "zero matrix all coordinates free");
        LinAlg lowerKernel = new LinAlg(null);
        lowerKernel.createInstanceFromScript("linalg:\nA=[1 2 3]\nnullspace(A)", null);
        check(!lowerKernel.toLaTeX().contains("Rank--nullity"), "lowercase kernel remains concise");
        for (String matrix : new String[] {"[1 2.5 -3;0 100 4]", "[1 0;2.5 100;-3 4]",
                "[1 2 3;4 5 7;2 8 9]", "[2 0;0 3]", "[1 2;2 4;3 6]", "[0 0;0 0]", "[5]"}) {
            verifySvdTrace(matrix);
        }
        LinAlg detailedSvd = new LinAlg(null);
        detailedSvd.createInstanceFromScript("linalg:\nA=[1 2.5 -3;0 100 4]\nSvd(A)", null);
        String svdLatex = detailedSvd.toLaTeX();
        check(svdLatex.contains("Jacobi sweep 1") && svdLatex.contains("(\\alpha,\\beta,\\gamma)"),
                "capitalized SVD renders actual rotations");
        check(svdLatex.contains("W_0=") && svdLatex.contains("\\qquad Q=")
                && svdLatex.contains("\\|w_1\\|_2"), "SVD working matrices and column lengths rendered");
        check(svdLatex.contains("$V$ is $3\\times 2$") && svdLatex.contains("$V^*=V^T$ is $2\\times 3$"),
                "wide example distinguishes V from V transpose");
        check(svdLatex.contains("A^T=P\\Sigma Q^T") && svdLatex.contains("full SVD"),
                "wide transpose mapping and thin/full distinction explained");
        check(svdLatex.contains("$V^*=\\overline{V}^{\\,T}$")
                && svdLatex.contains("also written $V^H$"), "conjugate transpose notation explained");
        check(svdLatex.contains("$C^T=\\binom{1}{i}$") && svdLatex.contains("$C^*=\\binom{1}{-i}$"),
                "complex example distinguishes transpose and conjugate transpose");
        check(svdLatex.contains("only real entries") && svdLatex.contains("takes inner products"),
                "real equivalence and SVD coordinate interpretation explained");
        check(svdLatex.contains("$V^*V=I$") && svdLatex.contains("not mean $V^*$ is a two-sided inverse"),
                "thin orthonormality not confused with square inverse");
        LinAlg conciseSvd = new LinAlg(null);
        conciseSvd.createInstanceFromScript("linalg:\nA=[1 2.5 -3;0 100 4]\nsvd(A)", null);
        check(!conciseSvd.toLaTeX().contains("Jacobi sweep"), "lowercase SVD remains concise");
        LinAlg diagonalSvd = new LinAlg(null);
        diagonalSvd.createInstanceFromScript("linalg:\nA=[0 0;0 0]\nSvd(A)", null);
        check(diagonalSvd.toLaTeX().contains("no rotations are needed")
                && diagonalSvd.toLaTeX().contains("instead of dividing by zero"), "zero SVD explanation");
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
        check(script.getExampleScriptName(16, true).equals("Example 17"), "default English example label");
        check(script.getExampleScriptName(16, false).equals("Beispiel 17"), "default German example label");
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        for (String help : new String[] {script.veryQuickHelpText(), script.veryQuickHelpText_G(),
                script.helpText(), script.helpText_G()}) {
            check(help != null && help.contains("linalg:"), "LinAlg help registered in both languages");
            check(help.contains("span(A)") && help.contains("showPlot=true"), "quick syntax and plotting help");
        }
        for (String help : new String[] {script.helpText(), script.helpText_G()}) {
            for (String function : new String[] {"det", "rank", "trace", "norm", "rref", "basis",
                    "nullspace", "transpose", "inv", "span", "svd", "evd", "eigenvalues", "eigenvectors"}) {
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
                "nullspace", "inv", "transpose", "span", "svd", "evd", "eig", "eigenvalues", "eigenvectors"}) {
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
        var jacobianMapping = new veryFastPDF.algorithms.linearTrans.ScalarFunction(
                java.util.List.of("x", "y"), "(x*y,sin(x),x+y)");
        var jacobianSteps = jacobianMapping.differentiationSteps("jacobian", new double[] {1, 2});
        check(jacobianSteps.contains("\\text{Input / column }1:\\quad u_{1}=x")
                && jacobianSteps.contains("\\text{Input / column }2:\\quad u_{2}=y"),
                "Jacobian labels declared inputs as columns");
        check(jacobianSteps.stream().anyMatch(step -> step.startsWith(
                "\\text{Output / row }3:\\quad F_{3}=")), "rectangular Jacobian labels each output as a row");
        check(jacobianSteps.stream().anyMatch(step -> step.contains(
                "J_{2,1}=\\frac{\\partial F_{2}}{\\partial x}=")),
                "Jacobian entry identifies output component and input variable");
        LinAlg jacobianExplanation = new LinAlg(null);
        jacobianExplanation.createInstanceFromScript(
                "linalg:\nF(x,y)=(x*y,sin(x),x+y)\nJacobian(F,(1,2))", null);
        check(jacobianExplanation.toLaTeX().contains("A row is not an output value")
                && jacobianExplanation.toLaTeX().contains("\\delta F\\approx J(p)\\,\\delta u"),
                "Jacobian explanation distinguishes values from sensitivities");
        var laplacianTrace = new veryFastPDF.algorithms.linearTrans.ScalarFunction(
                java.util.List.of("x", "y"), "x^2+2*x*y+3*y^2");
        var accumulation = laplacianTrace.differentiationSteps("laplacian", new double[] {1, 2});
        check(accumulation.contains("\\text{Initialize running total:}\\quad S_0=0"),
                "Laplacian initializes running total");
        check(accumulation.contains("\\text{Accumulate term }1:\\quad S_{1}=S_{0}+"
                + "\\frac{\\partial^2 f}{\\partial x^2}=\\left(0\\right)+\\left(2\\right)=2"),
                "first Laplacian term shows previous total and derivative");
        check(accumulation.contains("\\text{Accumulate term }2:\\quad S_{2}=S_{1}+"
                + "\\frac{\\partial^2 f}{\\partial y^2}=\\left(2\\right)+\\left(6\\right)=8"),
                "second Laplacian term makes two plus six explicit");
        var divergenceTrace = new veryFastPDF.algorithms.linearTrans.ScalarFunction(
                java.util.List.of("x", "y"), "(2*x,3*y)");
        check(divergenceTrace.differentiationSteps("divergence", new double[] {1, 2}).contains(
                "\\text{Accumulate term }2:\\quad S_{2}=S_{1}+"
                + "\\frac{\\partial F_{2}}{\\partial y}=\\left(2\\right)+\\left(3\\right)=5"),
                "shared divergence accumulation labels correct first partial");
        var negativeTrace = new veryFastPDF.algorithms.linearTrans.ScalarFunction(
                java.util.List.of("x", "y"), "x^2-3*y^2");
        check(negativeTrace.differentiationSteps("laplacian", new double[] {1, 2}).stream()
                .anyMatch(step -> step.contains("\\left(2\\right)+\\left(-6\\right)=-4")),
                "negative accumulated terms parenthesized");
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
        check(calculusLatex.contains("row $i$ contains the sensitivities")
                && calculusLatex.contains("Column $j$ varies input"), "Jacobian orientation explained");
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
                {"Svd(A)", "centered data"}, {"Evd(A)", "PCA directions"},
                {"Eigenvalues(A)", "variance"}, {"Eigenvectors(A)", "PCA directions"},
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
        error("f(x,y)=x+y\ngrad(f,2)", "Line 2:", "column vector");
        String scalarPointBody = "f(x)=sin(x^2)+2*x\np=(2)\n";
        double scalarPointDerivative = 4 * Math.cos(4) + 2;
        matrix(scalarPointBody, "grad(f,p)", new double[][] {{scalarPointDerivative}});
        matrix(scalarPointBody, "Grad(f,p)", new double[][] {{scalarPointDerivative}});
        scalar(scalarPointBody, "p", 2);
        scalar("", "3*(2+1)", 9);
        for (String operator : new String[] {"grad", "jacobian", "hessian", "laplacian", "divergence"}) {
            for (String spelling : new String[] {operator,
                    Character.toUpperCase(operator.charAt(0)) + operator.substring(1)}) {
                Value vectorPoint = evaluate(scalarPointBody, spelling + "(f,[2])");
                for (String point : new String[] {"p", "2", "(2)", "1+1"}) {
                    Value scalarPoint = evaluate(scalarPointBody, spelling + "(f," + point + ")");
                    check(scalarPoint.isScalar() == vectorPoint.isScalar(), "scalar point retains operator result type");
                    check(scalarPoint.getRowCount() == vectorPoint.getRowCount()
                            && scalarPoint.getColumnCount() == vectorPoint.getColumnCount(),
                            "scalar point retains operator result shape");
                    close(scalarPoint.getRows()[0][0], vectorPoint.getRows()[0][0]);
                }
            }
        }
        matrix("F(x)=(x,x^2)\n", "Jacobian(F,2)", new double[][] {{1}, {4}});
        error("f(x)=log(x)\ngrad(f,-1)", "Line 2:", "undefined");
        LinAlg scalarPointScript = new LinAlg(null);
        scalarPointScript.createInstanceFromScript("linalg:\n" + scalarPointBody
                + "g=grad(f,p)\nGrad(f,p)\n" + LinAlg.DECL_BEG_TAG
                + "\nshowPlot=true\n" + LinAlg.DECL_END_TAG, null);
        check(scalarPointScript.toLaTeX().contains("Local function curve"), "user scalar point example plots");
        check(scalarPointScript.createScriptFromInstance().contains("p = (2)"),
                "scalar point source preserved");
        close(scalarPointScript.getScene().find("g").getValue().getOrigin()[0], 2);
        check(scalarPointScript.getScene().getStatements().get(3).getExplanations().get(0).input
                .getColumnCount() == 1, "capitalized scalar point explanation uses column point");
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
        check(plot.contains("Local contour map") && plot.contains("unit gradient direction"),
                "gradient shows function contours with explicitly scaled direction");
        check(plot.contains("x=2.5cm,y=2.5cm"), "equal input-axis scales preserve gradient direction");
        check(plot.contains("x\\in[-1,3]") && plot.contains("y\\in[0,4]"),
                "contour domain centered at evaluation point");
        LinAlgScene curveScene = LinAlgScene.parse("f(t)=t^2\ng=grad(f,[1])\nh=g\nGrad(f,[1])");
        String curvePlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(curveScene);
        check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.hasPlottableContent(curveScene),
                "1D gradient alone is plottable");
        check(curvePlot.contains("Local function curve") && curvePlot.contains("slope $f'(p)=2"),
                "1D curve with tangent slope");
        check(curvePlot.split("Local function curve", -1).length - 1 == 1,
                "aliases and repeated gradient point deduplicated");
        String standalonePlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("f(x,y)=x^2+y^2\nGrad(f,(0,0))"));
        check(standalonePlot.contains("Local contour map"), "standalone capitalized gradient plots function");
        String domainPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("f(x)=log(x)\ng=grad(f,[1])"));
        check(domainPlot.contains("Undefined/non-finite samples omitted:"),
                "out-of-domain samples explicitly reported");
        check(!domainPlot.contains("NaN") && !domainPlot.contains("Infinity"), "invalid samples not emitted");
        String constantPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("f(x,y)=7\ng=grad(f,(1,2))"));
        check(constantPlot.contains("Local contour map") && !constantPlot.contains("NaN"),
                "constant function and zero gradient plot safely");
        LinAlgScene transformedGradient = LinAlgScene.parse(gradientBody + "A=[1 0;0 2]\nw=A*grad(f,p)");
        check(!veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(transformedGradient)
                .contains("Local contour map"), "transformed vectors do not claim original gradient context");
        LinAlg curveScript = new LinAlg(null);
        curveScript.createInstanceFromScript("linalg:\nf(t)=t^2\ng=grad(f,[1])\n" + LinAlg.DECL_BEG_TAG
                + "\nshowPlot=true\n" + LinAlg.DECL_END_TAG, null);
        check(curveScript.toLaTeX().contains("Local function curve"), "1D function plot wired into script output");
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
            check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(restored).contains("Local contour map"),
                    "function plot context survives serialization");
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
            Files.writeString(Path.of(args[0]), LaTeXCommands.PREAMBLE_STANDARD
                    .replace("\\begin{document}", "\\usepackage{tikz}\n"
                            + "\\usepackage[paperwidth=54cm,paperheight=64cm,margin=2cm]{geometry}\n"
                            + "\\begin{document}")
                    + latex + stepLatex + explanations + gradientLatex + svdLatex + nullspaceLatex + eigenLatex
                    + multipleFreeLatex + trivialKernel.toLaTeX() + zeroKernel.toLaTeX()
                    + calculusLatex
                    + curvePlot + domainPlot + constantPlot + scalarPointScript.toLaTeX()
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
            Files.writeString(Path.of(args[0]).resolveSibling("curve-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM.replace(
                            "\\begin{document}", "\\usepackage{tikz}\n\\begin{document}")
                            + curvePlot + domainPlot + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("svd-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM + svdLatex
                            + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("eigen-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM + eigenLatex
                            + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("evolution-demo.tex"),
                    LaTeXCommands.PREAMBLE_STANDARD.replace("\\begin{document}",
                            "\\usepackage[paperwidth=34cm,paperheight=44cm,margin=2cm]{geometry}\n"
                            + "\\begin{document}")
                            + evolutionLatex + symmetricEvolution.toLaTeX() + qrEvolution.toLaTeX()
                            + failedEvolution.toLaTeX() + chainEvolution.toLaTeX()
                            + LaTeXCommands.POSTAMBLE_STANDARD);
        }
        System.out.println("Passed " + checks + " checks.");
    }

    private static void verifyDistributions(String[] args) throws Exception {
        for (String name : new String[] {"binomial", "Binomial", "BINOMIAL"}) {
            scalar("", name + "(10,0.3,3)", 0.266827932);
            scalar("", name + "(0,0.3,0)", 1);
            scalar("", name + "(0,0.3,1)", 0);
            scalar("", name + "(4,0,0)", 1);
            scalar("", name + "(4,0,1)", 0);
            scalar("", name + "(4,1,4)", 1);
            scalar("", name + "(4,1,3)", 0);
            scalar("", name + "(4,0.5,5)", 0);
            for (String arguments : new String[] {"-1,0.5,1", "1.5,0.5,1", "2,0.5,-1",
                    "2,0.5,0.5", "2,-0.1,1", "2,1.1,1", "[2],0.5,1", "2,[0.5],1",
                    "2,0.5,[1]", "1e309,0.5,1", "2,1e309,1", "2,0.5,1e309",
                    "9007199254740992,0.5,1", "2,0.5,9007199254740992", "", "2,0.5", "2,0.5,1,1"}) {
                error(name + "(" + arguments + ")", "Line 1:");
            }
            error(name + "(x)=x", "Reserved");
        }
        for (String name : new String[] {"poisson", "Poisson", "POISSON"}) {
            scalar("", name + "(4,2)", Math.exp(-4) * 8);
            scalar("", name + "(0,0)", 1);
            scalar("", name + "(0,1)", 0);
            scalar("", name + "(4,0)", Math.exp(-4));
            scalar("", name + "(1000,1000)", 0.0126146113487215);
            for (String arguments : new String[] {"-1,1", "1,-1", "1,0.5", "[1],1", "1,[1]",
                    "1e309,1", "1,1e309", "9007199254740992,1", "1,9007199254740992", "", "1", "1,2,3"}) {
                error(name + "(" + arguments + ")", "Line 1:");
            }
            error(name + "(x)=x", "Reserved");
        }
        for (int n = 1; n <= 35; n++) {
            double p = 0.37;
            double expected = Math.pow(1 - p, n);
            double total = 0;
            for (int k = 0; k <= n; k++) {
                double mass = evaluate("", "binomial(" + n + "," + p + "," + k + ")").getScalar();
                close(mass / expected, 1);
                total += mass;
                expected *= (n - k) * p / ((k + 1) * (1 - p));
            }
            close(total, 1);
        }
        for (double lambda : new double[] {0.01, 0.5, 4, 20, 100}) {
            double expected = Math.exp(-lambda), total = 0;
            for (int k = 0; k < 200; k++) {
                double mass = evaluate("", "poisson(" + lambda + "," + k + ")").getScalar();
                close(mass / expected, 1);
                total += mass;
                expected *= lambda / (k + 1);
                if (expected < Double.MIN_NORMAL) break;
            }
            close(total, 1);
        }
        close(evaluate("", "binomial(1000000,0.5,500000)").getScalar(), 0.00079788436133175);
        close(evaluate("", "binomial(1000000,0.000001,1)").getScalar(), 0.36787962511127);
        close(evaluate("", "binomial(1000000000000000,1e-15,1)").getScalar() / Math.exp(-1), 1);
        close(evaluate("", "binomial(1,1e-200,1)").getScalar() / 1e-200, 1);
        close(evaluate("", "poisson(1e-200,1)").getScalar() / 1e-200, 1);
        error("binomial(10000,0.5,0)", "underflow");
        error("poisson(1000,0)", "underflow");
        scalar("", "binomial(2,precision(1,1),1) + poisson(0,0)", 1.5);
        matrix("", "poisson(0,0)*[1 0;0 2]", new double[][] {{1, 0}, {0, 2}});
        LinAlgScene scene = LinAlgScene.parse("p=0.3\nb=binomial(10,p,3)\nBinomial(10,p,3)\n"
                + "p=0.5\nb2=binomial(10,p,3)\nnested=norm(poisson(4,2)*[1 2 3 4])\n"
                + "span(poisson(2,1)*[1 0;0 1])");
        String plot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(scene);
        check(plot.split("Binomial PMF", -1).length - 1 == 2, "identical calls deduplicated, redefinitions preserved");
        check(plot.split("Poisson PMF", -1).length - 1 == 2, "nested assigned and display-only calls plot");
        check(plot.contains("Discrete probability masses") && plot.contains("not renormalized")
                && plot.contains("Probability outside window"), "PMF and truncated tails labeled");
        check(plot.contains("\\draw[red,thick]"), "requested outcome highlighted");
        String fullPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("binomial(10,0.3,3)"));
        var stems = java.util.regex.Pattern.compile(
                "\\\\draw\\[(?:red|blue!70!black),thick\\] \\(([-+0-9.]+),0\\) -- "
                + "\\(([-+0-9.]+),([-+0-9.]+)\\);").matcher(fullPlot);
        int outcome = 0;
        double plottedTotal = 0;
        while (stems.find()) {
            double mass = evaluate("", "binomial(10,0.3," + outcome + ")").getScalar();
            double plottedMass = Double.parseDouble(stems.group(3)) / 3 * 0.266827932 * 1.15;
            check(Math.abs(mass - plottedMass) < 0.000006, "plotted stem height matches actual probability mass");
            close(Double.parseDouble(stems.group(1)), Double.parseDouble(stems.group(2)));
            plottedTotal += plottedMass;
            outcome++;
        }
        check(outcome == 11 && Math.abs(plottedTotal - 1) < 0.00003,
                "full binomial plot contains all outcomes and total mass one");
        check(fullPlot.contains("\\draw[red,thick] (-1.12,0)"), "red stem placed at requested k");
        check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.hasPlottableContent(LinAlgScene.parse("poisson(4,2)")),
                "standalone scalar distribution call plottable");
        check(plot.equals(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse(scene.toScript()))), "distribution source roundtrip");
        var bytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(bytes)) {
            output.writeObject(scene);
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            check(plot.equals(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz((LinAlgScene) input.readObject())),
                    "distribution requests survive serialization");
        }
        StringBuilder edgePlots = new StringBuilder();
        for (String expression : new String[] {"binomial(0,0.5,0)", "binomial(4,0,2)", "binomial(4,1,4)",
                "binomial(10,0.3,12)", "poisson(0,0)", "poisson(4,100)", "poisson(1000000,1000000)",
                "binomial(1000000,0.5,500000)", "binomial(100,0.00001,0)",
                "poisson(9007199254740991,9007199254740991)"}) {
            String edge = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(LinAlgScene.parse(expression));
            check(!edge.contains("NaN") && !edge.contains("Infinity"), "edge distribution plot finite");
            int stemCount = edge.split("\\\\draw\\[(?:red|blue!70!black),thick\\]", -1).length - 1;
            check(stemCount > 0 && stemCount <= 101, "bounded discrete plot workload");
            edgePlots.append("\\begin{center}").append(edge).append("\\end{center}\n");
            LinAlg explainedEdge = new LinAlg(null);
            explainedEdge.createInstanceFromScript("linalg:\n"
                    + Character.toUpperCase(expression.charAt(0)) + expression.substring(1), null);
            check(!explainedEdge.toLaTeX().contains("NaN") && !explainedEdge.toLaTeX().contains("Infinity"),
                    "capitalized boundary and large-parameter explanations finite");
        }
        check(edgePlots.toString().contains("underflow") && edgePlots.toString().contains("outside the plotted window"),
                "underflowed plotted tails and off-window k disclosed");
        LinAlg script = new LinAlg(null);
        script.createInstanceFromScript(script.getExampleScripts()[11], null);
        String latex = script.toLaTeX();
        for (String text : new String[] {"\\binom{10}{3}", "\\frac{10!}{3!\\,7!}", "\\ell=C-D_1-D_2-N",
                "\\ell=-s(k)-D(k,\\lambda)-N", "e^{\\ell}", "ML use", "Binomial PMF", "Poisson PMF",
                "\\operatorname{binomial}", "\\operatorname{poisson}"}) {
            check(latex.contains(text), "distribution explanation: " + text);
        }
        LinAlg concise = new LinAlg(null);
        concise.createInstanceFromScript("linalg:\nbinomial(10,0.3,3)\npoisson(4,2)", null);
        check(!concise.toLaTeX().contains("ML use") && !concise.toLaTeX().contains("PMF:"),
                "lowercase distribution output concise, plots opt-in");
        LinAlg tiny = new LinAlg(null);
        tiny.createInstanceFromScript("linalg:\np=binomial(1,1e-200,1)", null);
        check(tiny.toLaTeX().contains("10^{-200}"), "tiny distribution result not rounded to zero");
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        try {
            for (String help : new String[] {script.veryQuickHelpText(), script.veryQuickHelpText_G(),
                    script.helpText(), script.helpText_G()}) {
                check(help.contains("binomial(n, p, k)") && help.contains("poisson(lambda, k)"),
                        "distribution help bilingual");
            }
        } finally {
            mainServlet.WebLink.availablePDFTypes = availableTypes;
        }
        if (args.length > 0) {
            Files.writeString(Path.of(args[0]).resolveSibling("distribution-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM.replace("\\begin{document}",
                            "\\usepackage{tikz}\n\\begin{document}") + latex + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("distribution-edge-demo.tex"),
                    LaTeXCommands.PREAMBLE_STANDARD.replace("\\begin{document}",
                            "\\usepackage{tikz}\n\\usepackage[paperwidth=24cm,paperheight=32cm,margin=2cm]{geometry}\n"
                            + "\\begin{document}") + edgePlots + LaTeXCommands.POSTAMBLE_STANDARD);
        }
    }

    private static void verifyProbabilityMetrics(String[] args) throws Exception {
        for (String function : new String[] {"precision", "Precision", "PRECISION", "recall", "Recall", "RECALL"}) {
            scalar("", function + "(80,20)", 0.8);
            scalar("", function + "(0,1)", 0);
            scalar("", function + "(1,0)", 1);
            scalar("", function + "(0.5,1.5)", 0.25);
            scalar("", function + "(1e308,1e308)", 0.5);
            scalar("", function + "(1e-300,1e-300)", 0.5);
            close(evaluate("", function + "(1e-200,1)").getScalar() / 1e-200, 1);
            for (String arguments : new String[] {"0,0", "-1,2", "2,-1", "[1],2", "2,[1]",
                    "1e309,1", "1,1e309", "", "1", "1,2,3"}) {
                error(function + "(" + arguments + ")", "Line 1:", function.toLowerCase());
            }
            error(function + "(x)=x", "Line 1:", "Reserved");
        }
        for (String function : new String[] {"bayes", "Bayes", "BAYES"}) {
            scalar("", function + "(0.01,0.9,0.0585)", 2.0 / 13);
            scalar("", function + "(0,0.9,0.5)", 0);
            scalar("", function + "(0.5,0,0.2)", 0);
            scalar("", function + "(1,0.5,0.5)", 1);
            scalar("", function + "(0.5,1,0.5)", 1);
            scalar("", function + "(0.5,0.5,0.75)", 1.0 / 3);
            close(evaluate("", function + "(1e-200,1e-200,1e-200)").getScalar() / 1e-200, 1);
            for (String arguments : new String[] {"0.5,0.9,0", "0,0,0", "0.5,0.9,-0.1",
                    "1.1,0.9,0.5", "0.5,1.1,0.5", "0.5,0.9,1.1", "-0.1,0.9,0.5",
                    "0.5,-0.1,0.5", "0.5,0.9,0.4", "0.5,0.9,0.96", "1,0.9,1",
                    "0.5,0.9,1e309", "1e309,0.9,0.5", "0.5,1e309,0.5",
                    "[0.5],0.9,0.5", "0.5,[0.9],0.5", "0.5,0.9,[0.5]", "", "0.5", "0.5,0.9",
                    "0.5,0.9,0.5,1"}) {
                error(function + "(" + arguments + ")", "Line 1:", "bayes");
            }
            error(function + "(x)=x", "Line 1:", "Reserved");
        }
        error("precision(1e-300,1e300)", "Line 1:", "representable");
        error("bayes(1e-200,1e-200,0.5)", "Line 1:", "representable");
        scalar("", "recall(80,10)", 8.0 / 9);
        scalar("", "precision(80,20) + recall(80,20)", 1.6);
        scalar("", "bayes(precision(1,9),recall(9,1),0.18)", 0.5);
        matrix("", "Precision(80,20)*[1 0;0 1]", new double[][] {{0.8, 0}, {0, 0.8}});
        LinAlgScene scene = LinAlgScene.parse("tp=80\nfp=20\np=Precision(tp,fp)\nfp=999\n"
                + "r=Recall(80,10)\nb=Bayes(0.01,0.9,0.0585)\n"
                + "nested=Bayes(Precision(1,9),Recall(9,1),0.18)\n");
        close(scene.find("p").getExplanations().get(0).arguments.get(1).getScalar(), 20);
        check(scene.find("nested").getExplanations().size() == 3, "nested metrics explanations retained");
        check(!veryFastPDF.algorithms.linearTrans.LinAlgPlot.hasPlottableContent(scene),
                "probabilities do not masquerade as observation histograms");
        check(scene.toScript().equals(LinAlgScene.parse(scene.toScript()).toScript()),
                "probability metric source roundtrip");
        var bytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(bytes)) {
            output.writeObject(scene);
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) input.readObject();
            close(restored.find("p").getExplanations().get(0).arguments.get(1).getScalar(), 20);
            check(restored.find("nested").hasProbabilityCalculation(), "probability rendering context serialized");
            check(restored.toScript().equals(scene.toScript()), "probability scene persistence");
        }
        LinAlg script = new LinAlg(null);
        script.createInstanceFromScript(script.getExampleScripts()[10], null);
        String latex = script.toLaTeX();
        for (String text : new String[] {"TP+FP=80+20=100", "TP+FN=80+10=90",
                "P(H\\cap E)=P(H)P(E\\mid H)=0.01\\cdot 0.9=0.009",
                "\\frac{0.009}{0.0585}\\approx 0.1538", "ML use", "base rate",
                "\\operatorname{precision}", "\\operatorname{recall}", "\\operatorname{bayes}"}) {
            check(latex.contains(text), "worked probability output: " + text);
        }
        LinAlg snapshot = new LinAlg(null);
        snapshot.createInstanceFromScript("linalg:\ntp=80\nfp=20\nPrecision(tp,fp)\nfp=999\n", null);
        check(snapshot.toLaTeX().contains("TP+FP=80+20=100"), "render captured input not redefined input");
        LinAlg concise = new LinAlg(null);
        concise.createInstanceFromScript("linalg:\nprecision(80,20)\nrecall(80,10)\nbayes(0.01,0.9,0.0585)", null);
        check(!concise.toLaTeX().contains("ML use") && !concise.toLaTeX().contains("TP+FP"),
                "lowercase metrics concise");
        LinAlg tiny = new LinAlg(null);
        tiny.createInstanceFromScript("linalg:\nx=bayes(1e-200,1e-200,1e-200)", null);
        check(tiny.toLaTeX().contains("= 1\\cdot 10^{-200}"), "tiny posterior not displayed as zero");
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        try {
            for (String help : new String[] {script.veryQuickHelpText(), script.veryQuickHelpText_G(),
                    script.helpText(), script.helpText_G()}) {
                check(help.contains("precision(tp, fp)") && help.contains("recall(tp, fn)")
                        && help.contains("bayes(prior, likelihood, evidence)"), "metrics help bilingual");
            }
        } finally {
            mainServlet.WebLink.availablePDFTypes = availableTypes;
        }
        if (args.length > 0) {
            Files.writeString(Path.of(args[0]).resolveSibling("probability-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM + latex
                            + LaTeXCommands.POSTAMBLE_STANDARD);
        }
    }

    private static void verifyStatistics(String[] args) throws Exception {
        String body = "data=[2 4 4 4 5 5 7 9]\n";
        String[] names = {"mean", "variance", "std"};
        double[] expected = {5, 4, 2};
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            scalar(body, name + "(data)", expected[i]);
            scalar(body, name + "(data^T)", expected[i]);
            scalar(body, Character.toUpperCase(name.charAt(0)) + name.substring(1) + "(data)", expected[i]);
            scalar("data=[3]\n", name + "(data)", i == 0 ? 3 : 0);
            scalar("data=(-4,-4,-4,-4)\n", name + "(data)", i == 0 ? -4 : 0);
            for (String invalid : new String[] {name + "(2)", name + "([1 2;3 4])",
                    name + "(nullspace([1]))", name + "()", name + "([1],[2])"}) {
                error(invalid, "Line 1:");
            }
            scalar(name + "=[1 2]\n", name + "(" + name + ")", i == 0 ? 1.5 : i == 1 ? 0.25 : 0.5);
            error(name + "(x)=x", "Line 1:");
        }
        scalar("", "variance([0 2])", 1);
        scalar("", "mean([-2 -1 0 1 2])", 0);
        scalar("", "mean([1e308 1e308])", 1e308);
        scalar("", "std([-1e308 1e308])", 1e308);
        scalar("", "mean([-1e308 1e308])", 0);
        scalar("", "mean([1e16 1 -1e16])", 1.0 / 3);
        scalar("", "variance([1000000000001 1000000000002 1000000000003])", 2.0 / 3);
        scalar("", "variance([10000000000000000 10000000000000002 10000000000000004])", 8.0 / 3);
        close(evaluate("", "std([-1e-200 1e-200])").getScalar() / 1e-200, 1);
        close(evaluate("", "variance([-1e-150 1e-150])").getScalar() / 1e-300, 1);
        error("variance([-1e308 1e308])", "Line 1:", "outside");
        error("variance([-1e-200 1e-200])", "Line 1:", "outside");
        error("mean([1e-323 0 0 0 0 0 0 0])", "Line 1:", "outside");
        error("std([1e309])", "Line 1:");
        error("mean(inv([1e-309]))", "Line 1:");
        scalar("A=[1 0;0 3]\n", "mean(eigenvalues(A)) + std([0 2])", 3);
        matrix(body + "A=[1 0;0 2]\n", "std(data)*A", new double[][] {{2, 0}, {0, 4}});
        LinAlgScene snapshot = LinAlgScene.parse(body + "s=std(data)\nm=mean(data)\nVariance(data)\nstd(data^T)\n"
                + "norm(mean(data)*[1 2 3 4])\nspan(mean([3 3 3 3])*[1 0;0 1])\n"
                + "data=[0 0 0 0]\ns2=std(data)\n");
        check(snapshot.find("s").getValue().isScalar(), "statistics return reusable scalars");
        check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.hasPlottableContent(snapshot),
                "statistics scalars contribute plot inputs");
        String plot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(snapshot);
        check(plot.split("Empirical distribution", -1).length - 1 == 3,
                "deduplicated data, nested display-only calls and redefinition snapshots");
        check(plot.contains("Relative frequency per bin") && plot.contains("not an assumed normal distribution")
                && plot.contains("Bin masses sum to one"), "histogram interpretation explicit");
        check(plot.contains("Red: mean $\\mu=5") && plot.contains("$\\sigma=2"), "actual mean/std markers");
        String cappedData = java.util.stream.IntStream.range(0, 169).mapToObj(Integer::toString)
                .collect(java.util.stream.Collectors.joining(" ", "[", "]"));
        String[] histogramData = {"[0 1 1 2]", "[0 0.25 0.5 0.75 1]", "[4]", cappedData};
        int[][] expectedCounts = {{1, 3}, {2, 1, 2}, {1},
                {14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 15}};
        int[] sampleCounts = {4, 5, 1, 169};
        for (int i = 0; i < histogramData.length; i++) {
            String histogram = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                    LinAlgScene.parse("mean(" + histogramData[i] + ")"));
            var bars = java.util.regex.Pattern.compile(
                    "\\\\filldraw\\[fill=blue!35,draw=blue!70!black\\] "
                    + "\\(([-+0-9.]+),0\\) rectangle \\(([-+0-9.]+),([-+0-9.]+)\\);").matcher(histogram);
            int count = 0;
            double totalHeight = 0;
            while (bars.find()) {
                check(Double.parseDouble(bars.group(1)) < Double.parseDouble(bars.group(2)),
                        "nonzero equal-width bins");
                double height = Double.parseDouble(bars.group(3));
                check(Math.abs(height / 3 - (double) expectedCounts[i][count] / sampleCounts[i]) < 0.00002,
                        "actual bin probability mass with inclusive last endpoint");
                totalHeight += height;
                count++;
            }
            check(count == expectedCounts[i].length, "deterministic bin count capped at twelve");
            check(Math.abs(totalHeight / 3 - 1) < 0.0002, "histogram masses sum to one within coordinate rounding");
        }
        check(snapshot.toScript().equals(LinAlgScene.parse(snapshot.toScript()).toScript()),
                "statistics source roundtrip");
        check(plot.equals(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse(snapshot.toScript()))), "plot source roundtrip");
        var bytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(bytes)) {
            output.writeObject(snapshot);
        }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) input.readObject();
            check(plot.equals(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(restored)),
                    "statistical input snapshots survive serialization");
        }
        StringBuilder extremePlots = new StringBuilder();
        for (String literal : new String[] {"[3]", "[0 0 0 0]", "[-1e308 1e308 -1e308 1e308]",
                "[1e-200 -1e-200 1e-200 -1e-200]", "[1e308 1e308 1e308 1e308]"}) {
            String extreme = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                    LinAlgScene.parse("std(" + literal + ")"));
            check(extreme.contains("Empirical distribution"), "constant/singleton/extreme data plot");
            check(!extreme.contains("NaN") && !extreme.contains("Infinity"), "finite histogram coordinates");
            extremePlots.append("\\begin{center}").append(extreme).append("\\end{center}\n");
        }
        LinAlg script = new LinAlg(null);
        script.createInstanceFromScript(script.getExampleScripts()[9], null);
        String latex = script.toLaTeX();
        for (String step : new String[] {"S_{8}", "d_{8}^2", "D_{8}", "\\sigma=\\sqrt{4}=2",
                "ML use", "training data", "n-1", "Empirical distribution", "\\operatorname{mean}"}) {
            check(latex.contains(step), "statistics explanatory output: " + step);
        }
        LinAlg concise = new LinAlg(null);
        concise.createInstanceFromScript("linalg:\n" + body + "mean(data)\nvariance(data)\nstd(data)", null);
        check(!concise.toLaTeX().contains("ML use") && !concise.toLaTeX().contains("Empirical distribution"),
                "lowercase output concise and plots opt-in");
        LinAlg tiny = new LinAlg(null);
        tiny.createInstanceFromScript("linalg:\ns=std([-1e-200 1e-200])", null);
        check(tiny.toLaTeX().contains("= 1\\cdot 10^{-200}"), "tiny scalar result not falsely displayed as zero");
        LinAlg nested = new LinAlg(null);
        nested.createInstanceFromScript("linalg:\n" + body + "x=Std(Mean(data)*[1 2])", null);
        check(nested.getScene().find("x").getExplanations().size() == 2, "nested capitalized traces retained");
        close(nested.getScene().find("x").getValue().getScalar(), 2.5);
        LinAlg plotsOnly = new LinAlg(null);
        plotsOnly.createInstanceFromScript("linalg:\nstd([2 4 6 8])\n" + LinAlg.DECL_BEG_TAG
                + "\nshowPlot=true;\nshowFormulas=false;\n" + LinAlg.DECL_END_TAG, null);
        check(plotsOnly.toLaTeX().contains("Empirical distribution"),
                "plot-only rendering works: " + plotsOnly.toLaTeX());
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        try {
            for (String help : new String[] {script.veryQuickHelpText(), script.veryQuickHelpText_G(),
                    script.helpText(), script.helpText_G()}) {
                check(help.contains("mean(data)") && help.contains("variance(data)") && help.contains("std(data)"),
                        "statistics help in both languages");
            }
            check(script.helpText().contains("population variance") && script.helpText_G().contains("Populationsvarianz"),
                    "population convention documented bilingually");
        } finally {
            mainServlet.WebLink.availablePDFTypes = availableTypes;
        }
        if (args.length > 0) {
            String preamble = LaTeXCommands.PREAMBLE_STANDARD.replace("\\begin{document}",
                    "\\usepackage{tikz}\n\\usepackage[paperwidth=24cm,paperheight=32cm,margin=2cm]{geometry}\n"
                    + "\\begin{document}");
            Files.writeString(Path.of(args[0]).resolveSibling("statistics-demo.tex"),
                    preamble + latex + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("statistics-script-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM.replace("\\begin{document}",
                            "\\usepackage{tikz}\n\\begin{document}")
                            + latex + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("statistics-extreme-demo.tex"),
                    preamble + extremePlots + LaTeXCommands.POSTAMBLE_STANDARD);
        }
    }

    private static void verifyEigenEvolution(String literal) {
        Value value = evaluate("", literal);
        java.util.List<LinAlgScene.Step> trace = new java.util.ArrayList<>();
        EigenDecomposition traced = EigenDecomposition.tryCompute(value.toMatrix(), trace);
        EigenDecomposition concise = EigenDecomposition.tryCompute(value.toMatrix());
        check(!trace.isEmpty(), "EVD captures numerical steps including failure path");
        check(traced.isAvailable() == concise.isAvailable(), "tracing preserves EVD availability");
        if (traced.isAvailable()) {
            for (int r = 0; r < value.getRowCount(); r++) {
                for (int c = 0; c < value.getColumnCount(); c++) {
                    close(traced.getQ()[r][c], concise.getQ()[r][c]);
                    close(traced.getLambda()[r][c], concise.getLambda()[r][c]);
                    close(traced.getQInverse()[r][c], concise.getQInverse()[r][c]);
                }
            }
        } else {
            check(traced.getFailureReason().equals(concise.getFailureReason()), "failure reason preserved with trace");
        }
        double[][] current = value.getRows();
        double[][] rotated = null;
        for (int i = 0; i < trace.size(); i++) {
            LinAlgScene.Step step = trace.get(i);
            if (step.operation.contains("\\text{QR iteration }")) {
                double[][] q = step.rows, r = trace.get(i + 1).rows, next = trace.get(i + 2).rows;
                for (int row = 0; row < q.length; row++) {
                    for (int col = 0; col < q.length; col++) {
                        double qr = 0, rq = 0;
                        for (int k = 0; k < q.length; k++) {
                            qr += q[row][k] * r[k][col];
                            rq += r[row][k] * q[k][col];
                        }
                        if (step.operation.matches(".*iteration \\}[1-8]:.*")) close(qr, current[row][col]);
                        close(rq, next[row][col]);
                    }
                }
                current = next;
            }
            if (step.operation.contains("D\\gets J^TDJ")) rotated = step.rows;
            if (step.operation.equals("Q\\gets QJ")) {
                double[][] q = step.rows;
                for (int row = 0; row < q.length; row++) {
                    for (int col = 0; col < q.length; col++) {
                        double dq = 0;
                        for (int j = 0; j < q.length; j++) {
                            for (int k = 0; k < q.length; k++) {
                                dq += q[j][row] * value.getRows()[j][k] * q[k][col];
                            }
                        }
                        close(dq, rotated[row][col]);
                    }
                }
            }
        }
        if (literal.equals("[1 0;0 2]")) {
            check(trace.stream().noneMatch(s -> s.operation.contains("D\\gets J^TDJ")),
                    "diagonal input does not fabricate rotations");
        }
        if (literal.equals("[1 1;0 1]")) {
            check(trace.stream().anyMatch(s -> s.operation.contains("B=A-\\lambda I")),
                    "defective EVD traces eigenspace before failure");
        }
    }

    private static void verifySvdTrace(String literal) {
        Value input = evaluate("", literal);
        double[][] a = input.getRows();
        int m = a.length, n = a[0].length, k = Math.min(m, n);
        SingularValueDecomposition traced = new SingularValueDecomposition(input.toMatrix(), true);
        SingularValueDecomposition concise = new SingularValueDecomposition(input.toMatrix());
        check(concise.getSweeps().isEmpty(), "unrequested SVD trace not collected");
        check(traced.getU().length == m && traced.getU()[0].length == k, "thin U shape");
        check(traced.getV().length == n && traced.getV()[0].length == k, "thin V shape");
        check(traced.getVTransposed().length == k && traced.getVTransposed()[0].length == n,
                "thin V transpose shape");
        double[][] w = new double[Math.max(m, n)][k];
        double[][] q = new double[k][k];
        for (int r = 0; r < w.length; r++) {
            for (int c = 0; c < k; c++) w[r][c] = m < n ? a[c][r] : a[r][c];
        }
        for (int r = 0; r < k; r++) q[r][r] = 1;
        for (var sweep : traced.getSweeps()) {
            for (var rotation : sweep.rotations()) {
                int p = rotation.p() - 1, s = rotation.q() - 1;
                double alpha = 0, beta = 0, gamma = 0;
                for (double[] row : w) {
                    alpha += row[p] * row[p];
                    beta += row[s] * row[s];
                    gamma += row[p] * row[s];
                }
                close(rotation.alpha(), alpha);
                close(rotation.beta(), beta);
                close(rotation.gamma(), gamma);
                close(rotation.c() * rotation.c() + rotation.s() * rotation.s(), 1);
                for (double[][] working : new double[][][] {w, q}) {
                    for (double[] row : working) {
                        double left = row[p], right = row[s];
                        row[p] = rotation.c() * left - rotation.s() * right;
                        row[s] = rotation.s() * left + rotation.c() * right;
                    }
                }
            }
            for (int r = 0; r < w.length; r++) {
                for (int c = 0; c < k; c++) close(sweep.w()[r][c], w[r][c]);
            }
            for (int r = 0; r < k; r++) {
                for (int c = 0; c < k; c++) close(sweep.v()[r][c], q[r][c]);
            }
        }
        for (int i = 0; i < k; i++) {
            close(traced.getSingularValues()[i], concise.getSingularValues()[i]);
            for (int j = 0; j < k; j++) {
                double uu = 0, vv = 0;
                for (int r = 0; r < m; r++) uu += traced.getU()[r][i] * traced.getU()[r][j];
                for (int r = 0; r < n; r++) vv += traced.getV()[r][i] * traced.getV()[r][j];
                close(uu, i == j ? 1 : 0);
                close(vv, i == j ? 1 : 0);
            }
        }
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                double reconstruction = 0;
                for (int i = 0; i < k; i++) {
                    reconstruction += traced.getU()[r][i] * traced.getSingularValues()[i] * traced.getV()[c][i];
                }
                close(reconstruction, a[r][c]);
            }
        }
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

    private static void verifyOperationMarkers() throws Exception {
        String body = "A=[1 2 3;4 5 6]\nB=[7 8;9 10;11 12]\nC=A * @product B\n";
        LinAlgScene scene = LinAlgScene.parse(body + "A=[0]\n");
        var operation = scene.getOperation("product");
        check(scene.getOperations().size() == 1, "one captured operation");
        check(operation.getOperator().equals("*"), "operator retained");
        close(operation.getLeft().getRows()[0][0], 1);
        operation.getLeft().getRows()[0][0] = 999;
        close(operation.getLeft().getRows()[0][0], 1);
        scene.find("C").getValue().getRows()[0][0] = 999;
        close(operation.getResult().getRows()[0][0], 58);
        var trace = operation.multiplication();
        check(trace.getStepCount() == 12, "rectangular m*n*k count");
        for (int step = 0; step <= 12; step++) {
            var state = trace.getStep(step);
            check(state.step() == step, "trace index");
            for (int r = 0; r < 2; r++) {
                for (int c = 0; c < 2; c++) {
                    boolean filled = step >= (r * 2 + c + 1) * 3;
                    check(state.isFilled(r, c) == filled, "unfilled vs completed cells");
                    if (filled) close(state.cells()[r][c], operation.getResult().getRows()[r][c]);
                }
            }
            if (step == 0) {
                check(state.row() == -1 && state.term() == -1, "initial state has no active term");
            } else {
                int cell = (step - 1) / 3, k = (step - 1) % 3, r = cell / 2, c = cell % 2;
                check(state.row() == r && state.column() == c && state.term() == k, "row-major active indices");
                double expected = 0;
                for (int j = 0; j <= k; j++) {
                    expected += operation.getLeft().getRows()[r][j] * operation.getRight().getRows()[j][c];
                }
                close(state.partialSum(), expected);
                close(state.previousSum() + state.product(), expected);
            }
        }
        for (int step : new int[] {-1, 13}) {
            try {
                trace.getStep(step);
                throw new AssertionError("Invalid trace index accepted");
            } catch (IllegalArgumentException expected) {
                check(expected.getMessage().contains("between 0 and 12"), "trace bounds explicit");
            }
        }
        var zero = LinAlgScene.parse("C=[0] * @zero [3]").getOperation("zero").multiplication();
        check(!zero.getStep(0).isFilled(0, 0) && zero.getStep(1).isFilled(0, 0), "computed zero is not a blank");
        close(zero.getStep(1).cells()[0][0], 0);
        var empty = LinAlgScene.parse("A=nullspace([1 0;0 1])\nC=A * @empty A^T")
                .getOperation("empty").multiplication();
        check(empty.getStepCount() == 0 && empty.getStep(0).isFilled(1, 1), "zero inner dimension completes at zero");
        close(empty.getStep(0).cells()[1][1], 0);
        var nested = LinAlgScene.parse("A=[1 2;3 4]\nC=(A * @inner A) * @outer A + @sum A\n"
                + "D=-@neg A^T @trans\nE=A^-1 @inverse\nF=+@positive A\n"
                + "det(A * @inside A)\nsvd(A * @decomposition A)\nspan(A * @spanProduct A)");
        check(nested.getOperations().size() == 10, "markers throughout expressions and display directives");
        close(nested.getOperation("outer").getLeft().getRows()[0][0], 7);
        close(nested.getOperation("inner").getResult().getRows()[0][0], 7);
        check(LinAlgScene.parse(nested.toScript()).toScript().equals(nested.toScript()), "marker roundtrip");
        String name = "my // product \"quoted\" \\ # + %";
        String quoted = name.replace("\\", "\\\\").replace("\"", "\\\"");
        LinAlg quotedScript = new LinAlg(null);
        quotedScript.createInstanceFromScript("linalg:\nC=[2] * @\"" + quoted + "\" [3] // actual comment\n", null);
        close(quotedScript.getScene().getOperation(name).getResult().getRows()[0][0], 6);
        String selectedScript = quotedScript.selectOperation(name);
        LinAlg selected = new LinAlg(null);
        selected.createInstanceFromScript(selectedScript, null);
        check(selected.toLaTeX().contains("step 0 of 1"), "arbitrary marker name survives declaration roundtrip");
        check(!quotedScript.getScene().toScript().isEmpty(), "source retained");
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        try (var output = new java.io.ObjectOutputStream(bytes)) { output.writeObject(scene); }
        try (var input = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            var restored = (LinAlgScene) input.readObject();
            close(restored.getOperation("product").multiplication().getStep(12).cells()[1][1], 154);
        }
        error("C=[1] * @same [2]\nD=[3] * @same [4]", "Line 2:", "Duplicate");
        error("C=([1] * @same [2]) * @same [3]", "Line 1:", "Duplicate");
        error("C=[1] * @ [2]", "Line 1:", "operation name");
        error("C=[1] * @\"\" [2]", "Line 1:", "must not be empty");
        error("C=[1] * @\"unfinished [2]", "Line 1:", "operation name");
        error("C=[1 2] * @bad [1 2]", "Line 1:", "Cannot multiply");
        for (String script : new String[] {
                body + "--declarations--\noperation=missing;\n--declarations-end--",
                body + "--declarations--\noperation=product;\nstep=13;\n--declarations-end--",
                body + "--declarations--\noperation=product;\nstep=-2;\n--declarations-end--",
                body + "--declarations--\noperation=product;\nstep=no;\n--declarations-end--",
                body + "--declarations--\nstep=0;\n--declarations-end--",
                "C=2 * @scalar [1]\n--declarations--\noperation=scalar;\n--declarations-end--",
                "C=[1] + @sum [2]\n--declarations--\noperation=sum;\n--declarations-end--"}) {
            try {
                new LinAlg(null).createInstanceFromScript("linalg:\n" + script, null);
                throw new AssertionError("Invalid operation declaration accepted");
            } catch (IllegalArgumentException expected) {
                check(!expected.getMessage().isEmpty(), "selection error explicit");
            }
        }
        LinAlg linalg = new LinAlg(null);
        linalg.createInstanceFromScript("linalg:\n" + body, null);
        check(!linalg.toLaTeX().contains("@product"), "annotations hidden in normal formulas");
        LinAlg unmarked = new LinAlg(null);
        unmarked.createInstanceFromScript("linalg:\n" + body.replace(" @product", ""), null);
        check(linalg.toLaTeX().replaceAll("\\s+", "").equals(unmarked.toLaTeX().replaceAll("\\s+", "")),
                "markers do not change normal output");
        selected.createInstanceFromScript(linalg.selectOperation("product"), null);
        check(selected.toLaTeX().contains("\\square"), "empty cells displayed");
        check(!selected.toLaTeX().contains("draw=blue") && !selected.toLaTeX().contains("draw=red")
                && !selected.toLaTeX().contains("draw=green"), "initial frame has no active entries to circle");
        selected.createInstanceFromScript(selected.multiplicationStep(1), null);
        check(selected.toLaTeX().contains("Running sum") && selected.toLaTeX().contains("step 1 of 12"),
                "partial dot product displayed");
        selected.createInstanceFromScript(selected.multiplicationStep(12), null);
        check(!selected.toLaTeX().contains("\\square &") && selected.toLaTeX().contains("154"),
                "final frame displays complete result");
        String animation = linalg.animateMultiplication("product");
        LinAlg animated = new LinAlg(null);
        animated.createInstanceFromScript(
                veryFastPDF.script.ScriptConversionMethods.removeComments(animation), null);
        check(animated.getAnimate().size() == 13, "standard animation expands all 13 frames: " + animated.getAnimate());
        check(animated.getPreprocessors().keySet().containsAll(java.util.stream.IntStream.rangeClosed(0, 12)
                .mapToObj(i -> "x" + i).toList()), "all animation frame scripts generated");
        String matrixMeasurements = null;
        String frameLayout = null;
        for (int i = 0; i <= 12; i++) {
            String frameScript = animated.getPreprocessorFromThis("x" + i);
            check(frameScript != null, "animation frame present");
            var frame = veryFastPDF.script.ScriptConversionMethods.evaluateChain(
                    animated.getRawScript(), frameScript, null);
            check(frame instanceof LinAlg, "animation frame retains linalg type");
            check(((LinAlg) frame).toLaTeX().contains("step " + i + " of 12"), "animation frame state");
            String frameLatex = ((LinAlg) frame).toLaTeX();
            String currentLayout = frameLatex.substring(0, frameLatex.indexOf("\\begin{center}"));
            if (frameLayout == null) frameLayout = currentLayout;
            check(frameLayout.equals(currentLayout), "frame width reserves the same space at every step");
            var measurements = java.util.regex.Pattern.compile(
                    "\\\\begingroup\\\\dimen2=0pt.*?\\\\edef\\\\xwizarddepth\\{\\\\the\\\\dimen4\\}",
                    java.util.regex.Pattern.DOTALL).matcher(frameLatex.substring(frameLatex.indexOf("\\begin{center}")));
            StringBuilder frameMeasurements = new StringBuilder();
            int measuredMatrices = 0;
            while (measurements.find()) {
                frameMeasurements.append(measurements.group());
                measuredMatrices++;
            }
            check(measuredMatrices == 3, "each multiplication matrix reserves cell dimensions");
            if (matrixMeasurements == null) matrixMeasurements = frameMeasurements.toString();
            check(matrixMeasurements.equals(frameMeasurements.toString()), "matrix spacing does not depend on step");
            String resultCircle = "\\node[draw=green!50!black,circle,";
            boolean completedEntry = i > 0 && i % 3 == 0;
            check(frameLatex.split(java.util.regex.Pattern.quote(resultCircle), -1).length - 1
                    == (completedEntry ? 2 : 0), "matching result circles only when the current dot product is complete");
            if (completedEntry) {
                int cell = i / 3 - 1;
                String resultEntry = LinAlgScene.formatNumber(operation.getResult().getRows()[cell / 2][cell % 2], true);
                check(frameLatex.contains(resultCircle
                        + "inner sep=1pt,line width=0.7pt] (entry) {$\\strut " + resultEntry + "$};}"),
                        "result circle follows the newly completed output entry");
                check(frameLatex.contains(" = \\tikz[baseline=(entry.base)]{" + resultCircle
                        + "inner sep=1pt,line width=0.7pt] (entry) {$\\strut " + resultEntry + "$};}$$"),
                        "completed formula result has a matching green circle");
            }
            for (String color : new String[] {"blue", "red"}) {
                String circle = "\\node[draw=" + color + ",circle,";
                check(frameLatex.split(java.util.regex.Pattern.quote(circle), -1).length - 1 == (i == 0 ? 0 : 2),
                        "matching circles in matrix and calculation");
            }
            if (i > 0) {
                int cell = (i - 1) / 3, k = (i - 1) % 3;
                String leftEntry = LinAlgScene.formatNumber(operation.getLeft().getRows()[cell / 2][k], true);
                String rightEntry = LinAlgScene.formatNumber(operation.getRight().getRows()[k][cell % 2], true);
                check(frameLatex.contains("(entry) {$\\strut " + leftEntry + "$};}")
                        && frameLatex.contains("(entry) {$\\strut " + rightEntry + "$};}"),
                        "circles track the active operand values");
            }
        }
        if (System.getProperty("linalg.markerSvgDirectory") != null) {
            verifyRenderedMultiplication(animated);
        }
        veryFastPDF.script.RepresentableDefault.removeKnownStuff();
        LinAlg emptyScript = new LinAlg(null);
        emptyScript.createInstanceFromScript("linalg:\nA=nullspace([1 0;0 1])\nC=A * @empty A^T", null);
        LinAlg emptyAnimation = new LinAlg(null);
        emptyAnimation.createInstanceFromScript(veryFastPDF.script.ScriptConversionMethods.removeComments(
                emptyScript.animateMultiplication("empty")), null);
        check(emptyAnimation.getAnimate().size() == 1, "zero-step animation contains only initial/final frame");
    }

    private static void verifyRenderedMultiplication(LinAlg animated) throws Exception {
        Path directory = Path.of(System.getProperty("linalg.markerSvgDirectory")).toAbsolutePath();
        Files.createDirectories(directory);
        boolean desktop = eas.veryFastPDF.MainLink.isApplicationOriginDesktop();
        var latex = mainServlet.WebLink.class.getDeclaredField("pathLATEX");
        var pdf2svg = mainServlet.WebLink.class.getDeclaredField("pathPDF2SVG");
        latex.setAccessible(true);
        pdf2svg.setAccessible(true);
        Object oldLatex = latex.get(null), oldPdf2svg = pdf2svg.get(null);
        try {
            eas.veryFastPDF.MainLink.setWebMode();
            latex.set(null, System.getProperty("linalg.pdflatex"));
            pdf2svg.set(null, System.getProperty("linalg.pdf2svg"));
            var processor = animated.generatePDFscript(directory.toString());
            processor.createSVGFileFromAnimationInstructions(directory + java.io.File.separator, false, 147);
            Path svg = directory.resolve(mainServlet.WebLink.fileName("this") + ".svg");
            var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var document = factory.newDocumentBuilder().parse(svg.toFile());
            var groups = document.getElementsByTagName("g");
            int frames = 0;
            for (int i = 0; i < groups.getLength(); i++) {
                var id = groups.item(i).getAttributes().getNamedItem("id");
                if (id != null && id.getNodeValue().matches("x\\d+147")) frames++;
            }
            check(frames == 13, "rendered SVG contains every multiplication frame");
            check(document.getElementsByTagName("set").getLength() >= 26, "SVG click transitions generated");
            double frameWidth = -1;
            for (int i = 0; i <= 12; i++) {
                check(Files.size(directory.resolve(mainServlet.WebLink.fileName("x" + i) + ".pdf")) > 0,
                        "each teaching frame rendered as a PDF");
                String log = Files.readString(directory.resolve(mainServlet.WebLink.fileName("x" + i) + ".log"));
                check(!java.util.regex.Pattern.compile("(?m)^!").matcher(log).find(),
                        "each teaching frame compiles without LaTeX errors");
                String frameSvg = Files.readString(directory.resolve(mainServlet.WebLink.fileName("x" + i) + ".svg"));
                var viewBox = java.util.regex.Pattern.compile("viewBox=\"([^\"]+)\"").matcher(frameSvg);
                check(viewBox.find(), "rendered frame has a viewBox");
                double width = Double.parseDouble(viewBox.group(1).trim().split("\\s+")[2]);
                if (frameWidth < 0) frameWidth = width;
                check(Math.abs(width - frameWidth) < 0.01, "rendered frame width stays fixed");
            }
            System.out.println("Matrix multiplication SVG verified: " + svg);
        } finally {
            latex.set(null, oldLatex);
            pdf2svg.set(null, oldPdf2svg);
            if (desktop) eas.veryFastPDF.MainLink.setDesktopMode();
        }
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
