import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.algorithms.linearTrans.LinAlgScene;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Numerical chain-rule and script integration checks, without a test-framework dependency. */
public final class NeuralNetworkTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        verifyBeginnerPlots(args);
        scalar("sigmoid(1000)", 1);
        scalar("sigmoid(-1000)", 0);
        scalar("tanh(1000)", 1);
        scalar("relu(-2)", 0);
        scalar("relu(3)", 3);
        scalar("reluback(0,5)", 0);
        scalar("sigmoidback(0,8)", 2);
        scalar("tanhback(0,3)", 3);
        scalar("hadamard(3,4)", 12);
        scalar("mse(3,1)", 4);
        scalar("msegrad(3,1)", 4);
        close(value("", "sigmoidback(40,1)").getScalar() / Math.exp(-40), 1, 1e-12);
        close(value("", "tanhback(20,1)").getScalar() / (4 * Math.exp(-40)), 1, 1e-12);
        matrix("dense([1 2;3 4],[1 0;0 1],(10,20))", new double[][] {{11, 12}, {23, 24}});
        matrix("densegradw([1 2;3 4],[5 6;7 8;9 10])",
                new double[][] {{17, 23, 29}, {39, 53, 67}});
        matrix("densegradx([1 2 3;4 5 6],[1 2;3 4])",
                new double[][] {{13, 18}, {17, 24}, {21, 30}});
        matrix("densegradb([1 2;3 4])", new double[][] {{3}, {7}});
        matrix("hadamard([1 2;3 4],[5 6;7 8])", new double[][] {{5, 12}, {21, 32}});
        matrix("msegrad([1 2;3 4],[0 0;0 0])", new double[][] {{0.5, 1}, {1.5, 2}});
        scalar("mse([1 2;3 4],[0 0;0 0])", 7.5);
        matrix("softmax([1000 0;1000 0])", new double[][] {{0.5, 0.5}, {0.5, 0.5}});
        matrix("softmax([1 2 3])", new double[][] {{1, 1, 1}});
        matrix("softmaxback([2;3],[1;1])", new double[][] {{0}, {0}});
        scalar("crossentropy([1000;-1000],[0;1])", 2000);
        matrix("crossentropygrad([1000;-1000],[0;1])", new double[][] {{1}, {-1}});
        scalar("crossentropy([1000;1000],[1;0])", Math.log(2));
        matrix("crossentropygrad([1],[0.9999999999995])", new double[][] {{0}});

        Map<String, double[][]> inputs = new LinkedHashMap<>();
        inputs.put("Z", new double[][] {{-0.7, 0.2}, {1.4, -1.1}, {0.3, 0.8}});
        inputs.put("G", new double[][] {{0.2, -0.5}, {-0.7, 0.9}, {1.2, 0.3}});
        inputs.put("T", new double[][] {{0.2, 0}, {0.3, 1}, {0.5, 0}});
        for (String activation : new String[] {"relu", "sigmoid", "tanh", "softmax"}) {
            finiteDifference(inputs, "Z", "trace(transpose(G)*" + activation + "(Z))",
                    activation + "back(Z,G)");
        }
        finiteDifference(inputs, "Z", "mse(Z,T)", "msegrad(Z,T)");
        finiteDifference(inputs, "Z", "trace(transpose(G)*hadamard(Z,T))", "hadamard(G,T)");
        finiteDifference(inputs, "Z", "crossentropy(Z,T)", "crossentropygrad(Z,T)");
        Value softmax = value(script(inputs), "softmax(Z)");
        Value shifted = value(script(inputs) + "C=[500 500;500 500;500 500]\n", "softmax(Z+C)");
        equal(softmax, shifted);
        for (int c = 0; c < 2; c++) {
            double sum = 0;
            for (int r = 0; r < 3; r++) sum += softmax.getRows()[r][c];
            close(sum, 1, 1e-12);
        }
        Value back = value(script(inputs), "softmaxback(Z,G)");
        for (int c = 0; c < 2; c++) {
            double sum = 0;
            for (int r = 0; r < 3; r++) sum += back.getRows()[r][c];
            close(sum, 0, 1e-12);
        }

        Map<String, double[][]> network = new LinkedHashMap<>();
        network.put("X", new double[][] {{1, -0.5}, {0.4, 0.8}});
        network.put("W1", new double[][] {{0.2, -0.1}, {0.3, 0.4}, {-0.2, 0.5}});
        network.put("b1", new double[][] {{0.1}, {0.05}, {-0.1}});
        network.put("W2", new double[][] {{0.3, -0.2, 0.1}, {-0.1, 0.4, 0.2}});
        network.put("b2", new double[][] {{0.02}, {-0.03}});
        network.put("T", new double[][] {{0.8, 0.2}, {0.1, 0.9}});
        String forward = "z1=dense(W1,X,b1)\na1=tanh(z1)\nz2=dense(W2,a1,b2)\na2=sigmoid(z2)\n";
        String backward = "ga2=msegrad(a2,T)\ngz2=sigmoidback(z2,ga2)\n"
                + "gW2=densegradw(gz2,a1)\ngb2=densegradb(gz2)\n"
                + "ga1=densegradx(W2,gz2)\ngz1=tanhback(z1,ga1)\n"
                + "gW1=densegradw(gz1,X)\ngb1=densegradb(gz1)\ngX=densegradx(W1,gz1)\n";
        for (String parameter : new String[] {"X", "W1", "b1", "W2", "b2"}) {
            finiteDifference(network, parameter, forward + "loss=mse(a2,T)\n",
                    backward, "loss", "g" + parameter);
        }
        String full = script(network) + forward + backward;
        LinAlgScene trained = LinAlgScene.parse(full + "loss=mse(a2,T)\n"
                + "W1new=W1-0.1*gW1\nb1new=b1-0.1*gb1\nW2new=W2-0.1*gW2\nb2new=b2-0.1*gb2\n"
                + "newloss=mse(sigmoid(dense(W2new,tanh(dense(W1new,X,b1new)),b2new)),T)\n");
        check(trained.find("newloss").getValue().getScalar() < trained.find("loss").getValue().getScalar(),
                "real two-layer SGD update reduces MSE");
        String classForward = "z1=dense(W1,X,b1)\na1=tanh(z1)\nz2=dense(W2,a1,b2)\n";
        String classBackward = "gz2=crossentropygrad(z2,T)\ngW2=densegradw(gz2,a1)\ngb2=densegradb(gz2)\n"
                + "gz1=tanhback(z1,densegradx(W2,gz2))\ngW1=densegradw(gz1,X)\ngb1=densegradb(gz1)\n";
        network.put("T", new double[][] {{0.8, 0.2}, {0.2, 0.8}});
        for (String parameter : new String[] {"W1", "b1", "W2", "b2"}) {
            finiteDifference(network, parameter, classForward + "loss=crossentropy(z2,T)\n",
                    classBackward, "loss", "g" + parameter);
        }
        for (String invalid : new String[] {
                "dense([1 2],[1],[0])", "dense([1 2],(1,2),0)",
                "dense([1 2],(1,2),[0 0])", "dense([1],1,[0])",
                "densegradw([1 2],[1])", "densegradx([1 2],(1,2))", "densegradb(1)",
                "relu(nullspace([1]))", "softmax(1)", "sigmoid([1e309])",
                "hadamard([1],1)", "reluback([1 2],(1,2))", "mse([1],1)",
                "crossentropy([1;2],[0.2;0.3])", "crossentropy([1;2],[-1;2])",
                "crossentropy([1 2],[1;0])", "crossentropy(1,1)",
                "mse(1e308,-1e308)", "dense([1e308],[2],[0])",
                "relu()", "mse(1)", "dense([1],[1])", "softmax([1], [1])",
                "dense(x)=x", "softmaxback(x)=x"}) {
            error(invalid);
        }

        String[] names = {"dense", "densegradw", "densegradb", "densegradx", "relu", "sigmoid", "tanh",
                "softmax", "reluback", "sigmoidback", "tanhback", "softmaxback", "hadamard",
                "mse", "msegrad", "crossentropy", "crossentropygrad"};
        String[] arguments = {"[1],[2],[3]", "[1],[2]", "[1]", "[1],[2]", "[2]", "[2]", "[2]",
                "[1;2]", "[2],[3]", "[2],[3]", "[2],[3]", "[1;2],[3;4]", "[2],[3]",
                "[2],[3]", "[2],[3]", "[1;2],[0;1]", "[1;2],[0;1]"};
        StringBuilder evidence = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            String call = names[i] + "(" + arguments[i] + ")";
            String capitalized = Character.toUpperCase(call.charAt(0)) + call.substring(1);
            equal(value("", call), value("", capitalized));
            equal(value("", call), LinAlgScene.parse(capitalized).getStatements().get(0).getValue());
            equal(value("", call), value("", call.toUpperCase(java.util.Locale.ROOT)));
            LinAlg explained = new LinAlg(null);
            explained.createInstanceFromScript("linalg:\ny=" + capitalized, null);
            check(explained.toLaTeX().contains("ML use") && !explained.getScene().find("y").getExplanations()
                    .get(0).steps.isEmpty(), "every capitalized ANN function has actual numerical steps: " + names[i]);
            check(explained.toLaTeX().contains("\\textbf{Calculation:}")
                    && explained.toLaTeX().contains("learning rate")
                    && explained.toLaTeX().contains("Backpropagation applies the chain rule"),
                    "every capitalized ANN function introduces beginner concepts: " + names[i]);
            evidence.append(explained.toLaTeX());
            LinAlg concise = new LinAlg(null);
            concise.createInstanceFromScript("linalg:\ny=" + call, null);
            check(!concise.toLaTeX().contains("ML use"), "lowercase ANN remains concise");
        }
        LinAlgScene persisted = LinAlgScene.parse("W=[1]\nx=[2]\nb=[3]\ny=Dense(W,x,b)\nW=[999]\n"
                + "g=Sigmoidback(y,Msegrad(sigmoid(y),[0]))\n");
        check(persisted.toScript().equals(LinAlgScene.parse(persisted.toScript()).toScript()),
                "ANN expressions retain source on roundtrip");
        check(persisted.find("y").getExplanations().get(0).steps.stream()
                .anyMatch(step -> step.operation.equals("Z=WX+b") && step.rows[0][0] == 5),
                "dense explanation snapshots survive later weight redefinitions");
        var bytes = new java.io.ByteArrayOutputStream();
        try (var out = new java.io.ObjectOutputStream(bytes)) { out.writeObject(persisted); }
        try (var in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            LinAlgScene restored = (LinAlgScene) in.readObject();
            check(restored.find("g").getExplanations().size() == 2, "nested backward traces serialized");
            check(restored.find("g").hasNeuralCalculation(), "neural scalar formatting flag serialized");
            equal(restored.find("g").getValue(), persisted.find("g").getValue());
        }
        LinAlg reference = new LinAlg(null);
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        try {
            for (String help : new String[] {reference.veryQuickHelpText(), reference.veryQuickHelpText_G(),
                    reference.helpText(), reference.helpText_G()}) {
                check(help.contains("dense(W, X, b)") && help.contains("softmax(Z)"), "ANN help bilingual");
            }
            for (String help : new String[] {reference.helpText(), reference.helpText_G()}) {
                check(help.contains("densegradw(GZ, X)") && help.contains("crossentropygrad(Z, T)")
                        && help.contains("sigmoidback(Z, GA)"), "forward/backward API documented bilingually");
            }
        } finally {
            mainServlet.WebLink.availablePDFTypes = availableTypes;
        }
        for (int i = 12; i < 14; i++) {
            LinAlg demo = new LinAlg(null);
            demo.createInstanceFromScript(reference.getExampleScripts()[i], null);
            check(demo.getScene().find("newloss").getValue().getScalar()
                    < demo.getScene().find("loss").getValue().getScalar(), "named ANN example lowers loss");
            check(!demo.getExampleScriptName(i, true).startsWith("Example"), "named ANN dropdown label");
            evidence.append(demo.toLaTeX());
            if (args.length > 0) {
                Files.writeString(Path.of(args[0]).resolveSibling("ann-example-" + i + ".tex"),
                        LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM + demo.toLaTeX()
                                + LaTeXCommands.POSTAMBLE_STANDARD);
            }
        }
        if (args.length > 0) {
            String preamble = LaTeXCommands.PREAMBLE_STANDARD.replace("\\begin{document}",
                    "\\usepackage[paperwidth=34cm,paperheight=44cm,margin=2cm]{geometry}\n\\begin{document}");
            Files.writeString(Path.of(args[0]), preamble + evidence + LaTeXCommands.POSTAMBLE_STANDARD);
            LinAlg oneNeuron = new LinAlg(null);
            oneNeuron.createInstanceFromScript("linalg:\nz=Dense([0.2 0.3],(1,2),[0.1])\n"
                    + "a=Sigmoid(z)\nloss=Mse(a,[1])\ngz=Sigmoidback(z,Msegrad(a,[1]))\n"
                    + "gW=Densegradw(gz,(1,2))\ngb=Densegradb(gz)", null);
            Files.writeString(Path.of(args[0]).resolveSibling("ann-neuron-demo.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM + oneNeuron.toLaTeX()
                            + LaTeXCommands.POSTAMBLE_STANDARD);
        }
        System.out.println("Passed " + checks + " ANN checks.");
    }

    private static void finiteDifference(Map<String, double[][]> inputs, String parameter,
            String loss, String gradient) {
        finiteDifference(inputs, parameter, "loss=" + loss + "\n", "gradient=" + gradient + "\n", "loss", "gradient");
    }

    private static void verifyBeginnerPlots(String[] args) throws Exception {
        String body = "z=[-100 -2 0 2 100]\na=Sigmoid(z)\nsigmoidback(z,[1 1 1 1 1])\n"
                + "relu(z)\ntanhback(z,[1 1 1 1 1])\n"
                + "Z=[2 1;1 0;0 -1;-1 -2]\np=softmax(Z)\nsoftmaxback(Z,[1 1;1 1;1 1;1 1])\n"
                + "crossentropy(Z,[1 0;0 1;0 0;0 0])\nZ=[0;0;0;0]\nSoftmax(Z)\n";
        LinAlgScene scene = LinAlgScene.parse(body);
        String plot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(scene);
        check(plot.split("Activation and local slope", -1).length - 1 == 3,
                "one curve pair per activation across forward/backward calls");
        check(plot.split("Softmax class shares", -1).length - 1 == 3,
                "softmax backward and fused loss deduplicated, redefined inputs retained");
        check(plot.contains("Not shown: 2 outside the window"), "out-of-window inputs explicitly counted");
        check(plot.contains("No renormalization") && plot.contains("not a weight update"),
                "beginner plot limitations explicit");
        check(plot.contains("open dot at one is excluded"), "ReLU derivative corner explained");
        check(!plot.contains("NaN") && !plot.contains("Infinity"), "all neural plot coordinates finite");
        check(plot.equals(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(LinAlgScene.parse(scene.toScript()))),
                "neural plot source roundtrip");
        var bytes = new java.io.ByteArrayOutputStream();
        try (var out = new java.io.ObjectOutputStream(bytes)) { out.writeObject(scene); }
        try (var in = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))) {
            check(plot.equals(veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz((LinAlgScene) in.readObject())),
                    "neural plot snapshots serialized");
        }
        for (String activation : new String[] {"relu", "sigmoid", "tanh"}) {
            String scalarPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                    LinAlgScene.parse(activation + "(0)"));
            check(veryFastPDF.algorithms.linearTrans.LinAlgPlot.hasPlottableContent(
                    LinAlgScene.parse(activation + "(0)")), "scalar activation alone plottable");
            check(scalarPlot.contains("Activation and local slope"), "lowercase standalone activation captured");
            check(scalarPlot.split("\\\\fill\\[red\\]", -1).length - 1 == 2,
                    "preactivation marked on activation and derivative panels");
        }
        String sigmoidPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(LinAlgScene.parse("sigmoid(0)"));
        check(sigmoidPlot.contains("\\fill[red] (-3.5,1.6) circle (2pt)")
                && sigmoidPlot.contains("\\fill[red] (3.5,2.8) circle (2pt)"),
                "sigmoid zero has activation 0.5 and local slope 0.25 on respective scales");
        String reluPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(LinAlgScene.parse("relu(0)"));
        check(reluPlot.contains("\\draw[blue,fill=white] (3.5,2.9333) circle (2pt)"),
                "ReLU derivative one at zero drawn open, no diagonal interpolation");
        String many = java.util.stream.IntStream.range(0, 21).mapToObj(i -> Double.toString(-5 + i * 0.5))
                .collect(java.util.stream.Collectors.joining(" ", "[", "]"));
        String capped = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(LinAlgScene.parse("sigmoid(" + many + ")"));
        check(capped.contains("9 beyond the marker limit")
                && capped.split("\\\\fill\\[red\\]", -1).length - 1 == 24, "curve markers bounded and disclosed");
        String targets = "[1 0;0 1;0 0;0 0]";
        String crossentropyPlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("crossentropy([2 1;1 0;0 -1;-1 -2]," + targets + ")"));
        check(crossentropyPlot.split("Softmax class shares", -1).length - 1 == 2,
                "fused loss contributes actual logits per example");
        String bars = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(LinAlgScene.parse("softmax([0;0;0;0])"));
        var heights = java.util.regex.Pattern.compile(
                "\\\\filldraw\\[fill=blue!30,draw=blue\\] \\([-+0-9.]+,0\\) rectangle "
                + "\\([-+0-9.]+,([-+0-9.]+)\\);").matcher(bars);
        int barCount = 0;
        while (heights.find()) {
            close(Double.parseDouble(heights.group(1)), 0.8, 1e-12);
            barCount++;
        }
        check(barCount == 4 && bars.contains("omitted class mass $\\approx 0"),
                "equal logits plot four true quarter-probability bars");
        StringBuilder large = new StringBuilder("[");
        for (int r = 0; r < 15; r++) large.append(r > 0 ? ";" : "").append("0 0 0 0 0");
        large.append("]");
        String largePlot = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("softmax(" + large + ")"));
        check(largePlot.contains("Showing first 12 of 15 classes; omitted class mass $\\approx 0.2")
                && largePlot.contains("Showing first 4 of 5 example columns"),
                "softmax limits preserve global normalization and disclose omitted mass");
        check(largePlot.split("\\\\filldraw", -1).length - 1 == 48, "softmax plot workload bounded");
        String nested = veryFastPDF.algorithms.linearTrans.LinAlgPlot.toTikz(
                LinAlgScene.parse("span(sigmoid(0)*[1 0;0 1])"));
        check(nested.contains("Activation and local slope"), "nested display-only calls capture activations");
        LinAlg hidden = new LinAlg(null);
        hidden.createInstanceFromScript("linalg:\na=Sigmoid(0)", null);
        check(!hidden.toLaTeX().contains("tikzpicture") && hidden.toLaTeX().contains("saturation")
                && hidden.toLaTeX().contains("not new weights"), "beginner explanations independent of opt-in plots");
        LinAlg plotOnly = new LinAlg(null);
        plotOnly.createInstanceFromScript("linalg:\nsigmoid(0)\n" + LinAlg.DECL_BEG_TAG
                + "\nshowPlot=true;\nshowFormulas=false;\n" + LinAlg.DECL_END_TAG, null);
        check(plotOnly.toLaTeX().contains("Activation and local slope")
                && !plotOnly.toLaTeX().contains("ML use"), "plot-only mode retains scalar activation views");
        LinAlg reference = new LinAlg(null);
        LinAlg demo = new LinAlg(null);
        demo.createInstanceFromScript(reference.getExampleScripts()[14], null);
        check(demo.toLaTeX().contains("Activation and local slope")
                && demo.toLaTeX().contains("Softmax class shares"), "beginner dropdown example wires all activation plots");
        var availableTypes = mainServlet.WebLink.availablePDFTypes;
        mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
        try {
            check(demo.helpText().contains("local-slope") && demo.helpText_G().contains("Steigungskurven"),
                    "beginner plotting conventions in bilingual help");
        } finally {
            mainServlet.WebLink.availablePDFTypes = availableTypes;
        }
        if (args.length > 0) {
            String preamble = LaTeXCommands.PREAMBLE_STANDARD.replace("\\begin{document}",
                    "\\usepackage{tikz}\n\\usepackage[paperwidth=24cm,paperheight=32cm,margin=2cm]{geometry}\n"
                    + "\\begin{document}");
            Files.writeString(Path.of(args[0]).resolveSibling("ann-activation-demo.tex"),
                    preamble + demo.toLaTeX() + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("ann-plot-demo.tex"),
                    preamble + plot + "\\begin{center}" + largePlot + "\\end{center}" + LaTeXCommands.POSTAMBLE_STANDARD);
            Files.writeString(Path.of(args[0]).resolveSibling("ann-activation-crop.tex"),
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM.replace("\\begin{document}",
                            "\\usepackage{tikz}\n\\begin{document}") + demo.toLaTeX()
                            + LaTeXCommands.POSTAMBLE_STANDARD);
        }
    }

    private static void finiteDifference(Map<String, double[][]> inputs, String parameter,
            String forward, String backward, String lossName, String gradientName) {
        Value analytic = LinAlgScene.parse(script(inputs) + forward + backward).find(gradientName).getValue();
        double[][] data = inputs.get(parameter);
        check(analytic.getRowCount() == data.length && analytic.getColumnCount() == data[0].length,
                "gradient preserves parameter shape");
        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c < data[r].length; c++) {
                double original = data[r][c], epsilon = 1e-5;
                data[r][c] = original + epsilon;
                double plus = LinAlgScene.parse(script(inputs) + forward).find(lossName).getValue().getScalar();
                data[r][c] = original - epsilon;
                double minus = LinAlgScene.parse(script(inputs) + forward).find(lossName).getValue().getScalar();
                data[r][c] = original;
                close(analytic.getRows()[r][c], (plus - minus) / (2 * epsilon), 2e-8);
            }
        }
    }

    private static String script(Map<String, double[][]> inputs) {
        StringBuilder script = new StringBuilder();
        inputs.forEach((name, rows) -> {
            script.append(name).append("=[");
            for (int r = 0; r < rows.length; r++) {
                if (r > 0) script.append(';');
                for (int c = 0; c < rows[r].length; c++) {
                    if (c > 0) script.append(' ');
                    script.append(rows[r][c]);
                }
            }
            script.append("]\n");
        });
        return script.toString();
    }

    private static Value value(String body, String expression) {
        return LinAlgScene.parse(body + "result=" + expression).find("result").getValue();
    }

    private static void scalar(String expression, double expected) {
        Value actual = value("", expression);
        check(actual.isScalar(), "scalar result");
        close(actual.getScalar(), expected, 1e-12);
    }

    private static void matrix(String expression, double[][] expected) {
        Value actual = value("", expression);
        check(!actual.isScalar() && actual.getRowCount() == expected.length
                && actual.getColumnCount() == expected[0].length, "matrix shape");
        for (int r = 0; r < expected.length; r++) {
            for (int c = 0; c < expected[r].length; c++) close(actual.getRows()[r][c], expected[r][c], 1e-12);
        }
    }

    private static void equal(Value a, Value b) {
        check(a.isScalar() == b.isScalar() && a.getRowCount() == b.getRowCount()
                && a.getColumnCount() == b.getColumnCount(), "identical result shape");
        for (int r = 0; r < a.getRowCount(); r++) {
            for (int c = 0; c < a.getColumnCount(); c++) close(a.getRows()[r][c], b.getRows()[r][c], 1e-12);
        }
    }

    private static void error(String expression) {
        try {
            LinAlgScene.parse(expression.contains("=x") ? expression : "result=" + expression);
            throw new AssertionError("Expected error: " + expression);
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().contains("Line 1:"), "line-numbered neural input error");
        }
    }

    private static void close(double actual, double expected, double tolerance) {
        check(Double.isFinite(actual) && Math.abs(actual - expected) <= tolerance,
                actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
