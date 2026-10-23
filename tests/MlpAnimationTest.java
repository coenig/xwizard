import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.algorithms.linearTrans.LinAlgScene;
import veryFastPDF.algorithms.linearTrans.MlpTrace;
import veryFastPDF.algorithms.linearTrans.MlpTrace.Phase;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.ScriptConversionMethods;

/** Neuron-level training, finite-difference and standard animation-pipeline regression checks. */
public final class MlpAnimationTest {
    private static int checks;
    private static final String LAYERS = "W1,b1,tanh;W2,b2,relu;W3,b3,sigmoid";

    public static void main(String[] args) throws Exception {
        Map<String, double[][]> parameters = parameters();
        for (String activations : new String[] {LAYERS, "W1,b1,identity;W2,b2,tanh;W3,b3,identity",
                "W1,b1,sigmoid;W2,b2,relu;W3,b3,sigmoid"}) {
            verifyGradients(parameters, activations, "mse");
        }
        Map<String, double[][]> classification = parameters();
        classification.put("W3", new double[][] {{0.7, -0.4}, {-0.2, 0.6}});
        classification.put("b3", new double[][] {{0.05}, {-0.02}});
        classification.put("target", new double[][] {{0.3}, {0.7}});
        verifyGradients(classification, "W1,b1,tanh;W2,b2,relu;W3,b3,softmax", "crossentropy");
        verifyGradients(classification, "W1,b1,tanh;W2,b2,relu;W3,b3,softmax", "mse");

        Map<String, double[][]> singleLayer = parameters();
        singleLayer.put("W1", new double[][] {{0.1, 0.2}});
        singleLayer.put("b1", new double[][] {{0}});
        verifyGradients(singleLayer, "W1,b1,sigmoid", "mse");
        check(trace(singleLayer, "W1,b1,sigmoid", "mse").getStepCount() == 4, "single-layer step count");
        Map<String, double[][]> deeper = parameters();
        deeper.put("W4", new double[][] {{0.4}});
        deeper.put("b4", new double[][] {{0.1}});
        verifyGradients(deeper, LAYERS + ";W4,b4,tanh", "mse");
        check(trace(deeper, LAYERS + ";W4,b4,tanh", "mse").getStepCount() == 19, "four-layer step count");

        MlpTrace trace = trace(parameters, LAYERS, "mse");
        check(trace.getStepCount() == 16, "five neurons times three passes plus one loss step");
        check(trace.getStep(0).phase() == Phase.INITIAL, "initial state");
        int forward = 0, backward = 0, update = 0, losses = 0;
        for (int step = 1; step <= trace.getStepCount(); step++) {
            var state = trace.getStep(step);
            switch (state.phase()) {
            case FORWARD -> forward++;
            case BACKWARD -> backward++;
            case UPDATE -> update++;
            case LOSS -> losses++;
            default -> throw new AssertionError("Unexpected phase");
            }
        }
        check(forward == 5 && backward == 5 && update == 5 && losses == 1, "each neuron visited exactly once per phase");
        check(trace.getStep(7).layer() == 2 && trace.getStep(7).phase() == Phase.BACKWARD,
                "backpropagation starts at output");
        check(trace.getStep(10).layer() == 0 && trace.getStep(10).phase() == Phase.BACKWARD,
                "backpropagation reaches first layer");
        check(trace.getStep(12).layer() == 0 && trace.getStep(12).phase() == Phase.UPDATE,
                "updates start only after all gradients");
        check(trace.getUpdatedLoss() < trace.getLoss(), "example SGD reduces loss");
        trace.getLayers().get(0).getWeights().getRows()[0][0] = 1000;
        close(trace.getLayers().get(0).getWeights().getRows()[0][0], 0.1, 0);
        invalid(() -> trace.getStep(-1), "between");
        invalid(() -> trace.getStep(17), "between");

        String body = script(parameters);
        for (String config : new String[] {"", "W1,b1", LAYERS + ";", "missing,b1,tanh",
                "W1,b1,bogus", "W1,b1,softmax;W2,b2,tanh", "W1,b3,sigmoid",
                "W1,b1,tanh;W1,b1,tanh"}) {
            invalid(() -> new MlpTrace(LinAlgScene.parse(body), "x", "target", config, "mse", 0.1), "");
        }
        invalid(() -> new MlpTrace(LinAlgScene.parse(body), "x", "target", LAYERS, "crossentropy", 0.1), "softmax");
        invalid(() -> new MlpTrace(LinAlgScene.parse(body), "x", "target", LAYERS, "bogus", 0.1), "loss");
        for (double rate : new double[] {0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            invalid(() -> new MlpTrace(LinAlgScene.parse(body), "x", "target", LAYERS, "mse", rate), "rate");
        }
        invalid(() -> new MlpTrace(LinAlgScene.parse(body + "x=[1 2;3 4]\n"),
                "x", "target", LAYERS, "mse", 0.1), "one example");
        invalid(() -> new MlpTrace(LinAlgScene.parse(body + "target=(1,2)\n"),
                "x", "target", LAYERS, "mse", 0.1), "");
        invalid(() -> new MlpTrace(LinAlgScene.parse(body + "x=(1e999,2)\n"),
                "x", "target", LAYERS, "mse", 0.1), "finite");

        String source = "linalg:\n" + body + declarations(0);
        LinAlg linalg = new LinAlg(null);
        linalg.createInstanceFromScript(source, null);
        check(linalg.getMlpTrace().getStepCount() == 16, "declarations configure all layers");
        check(linalg.toLaTeX().contains("tikzpicture") && linalg.toLaTeX().contains("x="),
                "network and input matrices displayed together");
        check(!linalg.toLaTeX().contains("G_W"), "initial frame hides unneeded gradient matrices");
        check(linalg.toLaTeX().contains("mean squared error"), "initial frame names the selected loss");
        check(linalg.toLaTeX().contains("\\square"), "initial activations and gradients unfilled");
        LinAlg softmaxView = new LinAlg(null);
        softmaxView.createInstanceFromScript("linalg:\n" + script(classification)
                + declarations(5).replace(LAYERS, "W1,b1,tanh;W2,b2,relu;W3,b3,softmax"), null);
        check(softmaxView.toLaTeX().contains("\\operatorname{softmax}(z^{(3)})\\bigr)_{1}"),
                "softmax formula acts on the complete logits vector before selecting a neuron");
        LinAlg classificationView = new LinAlg(null);
        String classificationDeclarations = declarations(7)
                .replace(LAYERS, "W1,b1,tanh;W2,b2,relu;W3,b3,softmax")
                .replace("mlpLoss=mse", "mlpLoss=crossentropy");
        classificationView.createInstanceFromScript("linalg:\n" + script(classification)
                + classificationDeclarations, null);
        check(classificationView.toLaTeX().contains("cross-entropy")
                && classificationView.toLaTeX().contains("\\hat y=a^{(3)}="),
                "classification loss explains target probabilities alongside predictions");
        classificationView.mlpStep(8);
        check(classificationView.toLaTeX().contains("G_z^{(3)}=")
                && !classificationView.toLaTeX().contains("G_a^{(3)}="),
                "fused cross-entropy upstream is labeled as a logits gradient");
        LinAlg restored = new LinAlg(null);
        restored.createInstanceFromScript(linalg.createScriptFromInstance(), null);
        close(restored.getMlpTrace().getLoss(), trace.getLoss(), 1e-12);
        check(restored.getMlpTrace().getLayers().size() == 3, "semicolon-separated configuration roundtrip");
        close(restored.getMlpTrace().getUpdatedLoss(), trace.getUpdatedLoss(), 1e-12);
        for (String bad : new String[] {"mlpStep=no;", "mlpStep=-2;", "mlpStep=17;", "mlpLearningRate=no;",
                "operation=product;", "step=0;"}) {
            invalid(() -> new LinAlg(null).createInstanceFromScript(source.replace("mlpStep=0;", bad), null), "");
        }
        LinAlg finalFrame = new LinAlg(null);
        finalFrame.createInstanceFromScript("linalg:\n" + body + declarations(-1), null);
        check(finalFrame.toLaTeX().contains("L_{\\mathrm{after}}"), "default final step shows updated loss");
        invalid(finalFrame::nextMlpStep, "between");

        RepresentableDefault.removeKnownStuff();
        LinAlg animated = new LinAlg(null);
        animated.createInstanceFromScript(ScriptConversionMethods.removeComments(linalg.animateMlp()), null);
        check(animated.getAnimate().size() == 17, "animation includes initial state plus 16 teaching steps");
        var latexProcessor = new veryFastPDF.pdfProcessors.LaTeXPDF("", "", null);
        int webLimit = latexProcessor.getCodeSizeToBeConsideredLarge();
        check(65_854 <= webLimit, "web accepts the reported 65,854-character MLP frame");
        check(webLimit == 1_000_000, "LaTeX web rendering retains a finite one-million-character cap");
        check(new veryFastPDF.pdfProcessors.GraphViz(null, null).getCodeSizeToBeConsideredLarge() == 30_000,
                "Graphviz web limit remains unchanged");
        String width = null;
        for (int i = 0; i <= 16; i++) {
            var frame = (LinAlg) ScriptConversionMethods.evaluateChain(
                    animated.getRawScript(), animated.getPreprocessorFromThis("x" + i), null);
            check(frame.toLaTeX().contains("MLP training: step " + i + " of 16"), "frame step propagated through chain");
            String prefix = frame.toLaTeX().substring(0, frame.toLaTeX().indexOf("\\begin{minipage}"));
            // Measurements contain changing entry content, but the reserved cell dimensions use every possible value.
            check(prefix.contains("\\dimen6=14cm"), "fixed minimum canvas width");
            check(frame.toLaTeX().contains("original weights"), "gradient/update semantics explicit");
            String latex = frame.toLaTeX();
            var state = trace.getStep(i);
            if (state.layer() >= 0) {
                check(latex.contains("\\textbf{Layer " + (state.layer() + 1) + ", neuron "
                        + (state.neuron() + 1) + ":"), "frame identifies the current layer and neuron");
                String other = state.layer() == 0 ? "^{(3)}=" : "^{(1)}=";
                check(!latex.contains("W" + other), "unrelated weight matrices hidden");
                if (state.phase() == Phase.FORWARD) {
                    check(!latex.contains("G_W") && !latex.contains("\\delta^{"),
                            "forward frame hides gradients");
                    check(latex.contains("W^{(" + (state.layer() + 1) + ")}="),
                            "forward frame includes needed weight matrix");
                } else if (state.phase() == Phase.BACKWARD) {
                    check(latex.contains("G_W^{(" + (state.layer() + 1) + ")}=")
                            && !latex.contains("W_{\\mathrm{current}}"), "backward frame shows gradients, not updates");
                } else {
                    check(latex.contains("W_{\\mathrm{old}}") && latex.contains("W_{\\mathrm{current}}"),
                            "update frame compares old and current parameters");
                    check(!latex.contains("z^{("), "update frame hides irrelevant weighted sums");
                }
            } else if (state.phase() == Phase.LOSS) {
                check(latex.contains("\\hat y=a^{(3)}=") && latex.contains("t="), "loss compares prediction and target");
                check(latex.contains("mean squared error") && latex.contains("$$N=1"),
                        "loss explained and calculated numerically");
                check(!latex.contains("W^{("), "loss frame hides weights");
            }
            var processor = frame.generatePDFscript(null);
            check(processor.getPreparedSourceString().length() <= processor.getCodeSizeToBeConsideredLarge(),
                    "prepared MLP frame fits the web rendering limit: " + i);
            if (i == 0) width = frame.toLaTeX();
        }
        check(width != null, "initial frame generated");
        String webScript = "linalg:" + body + "\n--declarations--\n"
                + "/* Here comes the animation part. */\nprep0=#x0=this#;\n"
                + "@{prepA=#xA=x~{A-1}~.nextMlpStep#;}@.for[A, 1, x0.mlpSteps]\n"
                + "animate=x0@{->xB}@.for[B, 1, x0.mlpSteps];\n"
                + "/* EO animation part. */\nsvgHeight=1800;\ne=#n#;\nshowPlot=false;\nshowFormulas=true;\n"
                + "operation=;\nstep=-1;\nmlpLayers=#" + LAYERS + "#;\nmlpInput=x;\nmlpTarget=target;\n"
                + "mlpLoss=mse;\nmlpLearningRate=0.1;\nmlpStep=0\n--declarations-end--";
        RepresentableDefault.removeKnownStuff();
        for (int pass = 0; pass < 2; pass++) {
            LinAlg web = new LinAlg(null);
            web.createInstanceFromScript(ScriptConversionMethods.removeComments(webScript), null);
            check(web.getAnimate().size() == 17, "web animation reparses all frames: " + pass);
            check(web.getSvgHeight() == 1800, "web animation preserves preferred height");
        }
        var placeholderCache = RepresentableDefault.class.getDeclaredField("knownPreprocessors");
        placeholderCache.setAccessible(true);
        Object cache = placeholderCache.get(null);
        check(cache instanceof java.util.Map<?, ?>, "preprocessor placeholder cache available");
        var cacheMap = (java.util.Map<?, ?>) cache;
        check(!cacheMap.containsValue(null), "unresolved expansions are not cached");
        // An earlier evaluation may defer a loop before its named inputs are available.
        cacheMap.replaceAll((key, value) -> null);
        LinAlg deferred = new LinAlg(null);
        deferred.createInstanceFromScript(ScriptConversionMethods.removeComments(webScript), null);
        check(deferred.getAnimate().size() == 17, "unresolved cached loops are retried, not recursively repeated");
        if (System.getProperty("mlp.svgDirectory") != null) {
            RepresentableDefault.removeKnownStuff();
            LinAlg rendering = new LinAlg(null);
            rendering.createInstanceFromScript(ScriptConversionMethods.removeComments(webScript), null);
            render(rendering);
            var availableTypes = mainServlet.WebLink.availablePDFTypes;
            try {
                mainServlet.WebLink.availablePDFTypes = java.util.Collections.singletonList(LinAlg.class);
                var web = mainServlet.WebLink.getApplicablePDFType(webScript, true, null,
                        System.getProperty("mlp.svgDirectory"));
                check(((LinAlg) web).getAnimate().size() == 17, "web re-detection after rendering");
            } finally {
                mainServlet.WebLink.availablePDFTypes = availableTypes;
            }
        }
        System.out.println("Passed " + checks + " MLP checks.");
    }

    private static void verifyGradients(Map<String, double[][]> values, String layers, String loss) {
        MlpTrace trace = trace(values, layers, loss);
        for (int l = 0; l < trace.getLayers().size(); l++) {
            var layer = trace.getLayers().get(l);
            for (String prefix : new String[] {"W", "b"}) {
                String name = prefix + (l + 1);
                var gradient = prefix.equals("W") ? layer.getWeightGradient() : layer.getBiasGradient();
                var updated = prefix.equals("W") ? layer.getUpdatedWeights() : layer.getUpdatedBias();
                double[][] data = values.get(name);
                for (int r = 0; r < data.length; r++) {
                    for (int c = 0; c < data[r].length; c++) {
                        double original = data[r][c], h = 1e-6;
                        data[r][c] = original + h;
                        double plus = independentLoss(values, layers, loss);
                        data[r][c] = original - h;
                        double minus = independentLoss(values, layers, loss);
                        data[r][c] = original;
                        close(gradient.getRows()[r][c], (plus - minus) / (2 * h), 2e-7);
                        close(updated.getRows()[r][c], original - 0.1 * gradient.getRows()[r][c], 1e-12);
                    }
                }
            }
        }
        close(trace.getLoss(), independentLoss(values, layers, loss), 1e-12);
        Map<String, double[][]> updated = new LinkedHashMap<>(values);
        for (int l = 0; l < trace.getLayers().size(); l++) {
            updated.put("W" + (l + 1), trace.getLayers().get(l).getUpdatedWeights().getRows());
            updated.put("b" + (l + 1), trace.getLayers().get(l).getUpdatedBias().getRows());
        }
        close(trace.getUpdatedLoss(), independentLoss(updated, layers, loss), 1e-12);
    }

    private static double independentLoss(Map<String, double[][]> values, String layers, String loss) {
        StringBuilder body = new StringBuilder(script(values));
        String previous = "x", lastZ = "";
        int index = 0;
        for (String specification : layers.split(";")) {
            String[] fields = specification.split(",");
            index++;
            lastZ = "z" + index;
            body.append(lastZ).append("=dense(").append(fields[0]).append(",").append(previous)
                    .append(",").append(fields[1]).append(")\n");
            previous = "a" + index;
            body.append(previous).append("=").append(fields[2].equals("identity") ? lastZ : fields[2] + "(" + lastZ + ")")
                    .append("\n");
        }
        return LinAlgScene.parse(body + "loss=" + loss + "("
                + (loss.equals("crossentropy") ? lastZ : previous) + ",target)").find("loss").getValue().getScalar();
    }

    private static MlpTrace trace(Map<String, double[][]> values, String layers, String loss) {
        return new MlpTrace(LinAlgScene.parse(script(values)), "x", "target", layers, loss, 0.1);
    }

    private static Map<String, double[][]> parameters() {
        Map<String, double[][]> data = new LinkedHashMap<>();
        data.put("x", new double[][] {{1}, {2}});
        data.put("target", new double[][] {{0.8}});
        data.put("W1", new double[][] {{0.1, 0.2}, {-0.3, 0.4}});
        data.put("b1", new double[][] {{0}, {0}});
        data.put("W2", new double[][] {{0.5, -0.2}, {0.3, 0.1}});
        data.put("b2", new double[][] {{0.1}, {-0.1}});
        data.put("W3", new double[][] {{0.7, -0.4}});
        data.put("b3", new double[][] {{0.05}});
        return data;
    }

    private static String script(Map<String, double[][]> data) {
        StringBuilder sb = new StringBuilder();
        data.forEach((name, rows) -> {
            sb.append(name).append("=[");
            for (int r = 0; r < rows.length; r++) {
                if (r > 0) sb.append(";");
                for (int c = 0; c < rows[r].length; c++) {
                    sb.append(c == 0 ? "" : " ").append(rows[r][c]);
                }
            }
            sb.append("]\n");
        });
        return sb.toString();
    }

    private static String declarations(int step) {
        return "--declarations--\nmlpLayers=#" + LAYERS + "#;\nmlpInput=x;\nmlpTarget=target;\n"
                + "mlpLoss=mse;\nmlpLearningRate=0.1;\nmlpStep=" + step + ";\n--declarations-end--";
    }

    private static void render(LinAlg animated) throws Exception {
        Path directory = Path.of(System.getProperty("mlp.svgDirectory")).toAbsolutePath();
        Files.createDirectories(directory);
        var latex = mainServlet.WebLink.class.getDeclaredField("pathLATEX");
        var converter = mainServlet.WebLink.class.getDeclaredField("pathPDF2SVG");
        latex.setAccessible(true);
        converter.setAccessible(true);
        Object oldLatex = latex.get(null), oldConverter = converter.get(null);
        boolean desktop = eas.veryFastPDF.MainLink.isApplicationOriginDesktop();
        try {
            eas.veryFastPDF.MainLink.setWebMode();
            latex.set(null, System.getProperty("mlp.pdflatex"));
            converter.set(null, System.getProperty("mlp.pdf2svg"));
            animated.generatePDFscript(directory.toString()).createSVGFileFromAnimationInstructions(
                    directory + java.io.File.separator, false, 248);
            for (int i = 0; i <= 16; i++) {
                String base = mainServlet.WebLink.fileName("x" + i);
                String log = Files.readString(directory.resolve(base + ".log"));
                check(!java.util.regex.Pattern.compile("(?m)^!").matcher(log).find(), "MLP frame has no LaTeX errors: " + i);
                String svg = Files.readString(directory.resolve(base + ".svg"));
                var matcher = java.util.regex.Pattern.compile("viewBox=\"([^\"]+)\"").matcher(svg);
                check(matcher.find(), "frame has viewBox");
                String[] bounds = matcher.group(1).split(" ");
                check(Double.parseDouble(bounds[2]) > 0 && Double.parseDouble(bounds[3]) > 0,
                        "active-layer frame has positive dimensions");
            }
            String svg = Files.readString(directory.resolve(mainServlet.WebLink.fileName("this") + ".svg"));
            for (int i = 0; i <= 16; i++) check(svg.contains("id=\"x" + i + "248\""), "SVG teaching frame");
            check(svg.contains("begin=\"x16248.click\""), "final update wraps to initial frame");
            var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var document = factory.newDocumentBuilder().parse(
                    directory.resolve(mainServlet.WebLink.fileName("this") + ".svg").toFile());
            var groups = document.getElementsByTagName("g");
            int restartIndicators = 0;
            for (int i = 0; i < groups.getLength(); i++) {
                var group = (org.w3c.dom.Element) groups.item(i);
                if (group.getAttribute("id").equals("animationRestart248")) {
                    restartIndicators++;
                    check(((org.w3c.dom.Element) group.getParentNode()).getAttribute("id").equals("x16248"),
                            "restart indicator belongs only to the last frame");
                    check(group.getTextContent().contains("click to restart (0)"), "restart action is explained");
                    check(group.getAttribute("pointer-events").equals("none"), "indicator does not intercept frame clicks");
                }
            }
            check(restartIndicators == 1, "animation has exactly one restart indicator");
            Path staticDirectory = directory.resolve("static-frame");
            Files.createDirectories(staticDirectory);
            var staticProcessor = new veryFastPDF.pdfProcessors.LaTeXPDF(
                    "\\documentclass{article}\\pagestyle{empty}\\begin{document}Static output\\end{document}",
                    staticDirectory.toString(), null);
            staticProcessor.storeAsPDF(mainServlet.WebLink.fileName("this"), staticDirectory.toString());
            staticProcessor.createSVGFileFromAnimationInstructions(staticDirectory + java.io.File.separator, false, 249);
            String staticSvg = Files.readString(staticDirectory.resolve(mainServlet.WebLink.fileName("this") + ".svg"));
            check(!staticSvg.contains("animationRestart") && !staticSvg.contains("click to restart"),
                    "single-frame output has no restart indicator");
            System.out.println("Rendered MLP SVG: " + directory.resolve(mainServlet.WebLink.fileName("this") + ".svg"));
        } finally {
            latex.set(null, oldLatex);
            converter.set(null, oldConverter);
            if (desktop) eas.veryFastPDF.MainLink.setDesktopMode();
        }
    }

    private static void invalid(Runnable operation, String message) {
        try {
            operation.run();
            throw new AssertionError("Invalid MLP configuration accepted");
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage().contains(message), "explicit validation: " + expected.getMessage());
        }
    }

    private static void close(double actual, double expected, double tolerance) {
        check(Double.isFinite(actual) && Math.abs(actual - expected) <= tolerance, actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
