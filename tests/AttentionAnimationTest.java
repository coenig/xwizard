import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import veryFastPDF.algorithms.linearTrans.AttentionTrace;
import veryFastPDF.algorithms.linearTrans.AttentionTrace.Stage;
import veryFastPDF.algorithms.linearTrans.LinAlg;
import veryFastPDF.algorithms.linearTrans.LinAlgScene;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.ScriptConversionMethods;

/** Self-attention numerics, validation, frame contents and animation-pipeline checks. */
public final class AttentionAnimationTest {
    private static int checks;
    private static final String MATRICES = "X=[1 0 1;0 2 0;1 1 0]\nWq=[1 0;0 1;1 1]\nWk=[0 1;1 0;1 1]\nWv=[1 0;0 1;1 1]\n";

    public static void main(String[] args) throws Exception {
        AttentionTrace trace = new AttentionTrace(LinAlgScene.parse(MATRICES), "X,Wq,Wk,Wv", false, "the,cat,sat");
        equal(trace.getQueries(), new double[][] {{2, 1}, {0, 2}, {1, 1}}, "Q=XWq");
        equal(trace.getKeys(), new double[][] {{1, 2}, {2, 0}, {1, 1}}, "K=XWk");
        equal(trace.getValues(), new double[][] {{2, 1}, {0, 2}, {1, 1}}, "V=XWv");
        double r = Math.sqrt(2);
        equal(trace.getScores(), new double[][] {{4 / r, 4 / r, 3 / r}, {4 / r, 0, 2 / r}, {3 / r, 2 / r, 2 / r}}, "S");
        double[][] expectedA = new double[3][3];
        double[][] s = trace.getScores().getRows();
        for (int i = 0; i < 3; i++) {
            double sum = 0;
            for (int j = 0; j < 3; j++) sum += Math.exp(s[i][j]);
            for (int j = 0; j < 3; j++) expectedA[i][j] = Math.exp(s[i][j]) / sum;
        }
        equal(trace.getWeights(), expectedA, "A=softmax(S)");
        equal(trace.getMixed(), multiply(expectedA, trace.getValues().getRows()), "Z=AV");
        check(trace.getOutput() == null && !trace.hasProjection(), "no projection without Wo");
        check(trace.getStepCount() == 7, "seven stages without projection");
        Stage[] order = {Stage.INITIAL, Stage.QUERY, Stage.KEY, Stage.VALUE, Stage.SCORES, Stage.SOFTMAX,
                Stage.OUTPUT, Stage.COMPLETE};
        for (int i = 0; i < order.length; i++) check(trace.getStage(i) == order[i], "stage order " + i);
        check(trace.getTokens().equals(java.util.List.of("the", "cat", "sat")), "token labels");
        trace.getWeights().getRows()[0][0] = 99;
        check(trace.getWeights().getRows()[0][0] != 99, "getters return copies");

        AttentionTrace causal = new AttentionTrace(LinAlgScene.parse(MATRICES + "Wo=[1;-1]"),
                "X, Wq, Wk, Wv, Wo", true, "");
        check(causal.getStepCount() == 8 && causal.getStage(7) == Stage.PROJECTION, "projection stage added");
        for (int i = 0; i < 3; i++) {
            double sum = 0;
            for (int j = 0; j < 3; j++) {
                double a = causal.getWeights().getRows()[i][j];
                sum += a;
                if (j > i) {
                    check(a == 0 && causal.getScores().getRows()[i][j] == Double.NEGATIVE_INFINITY, "causal mask");
                } else {
                    check(a > 0, "allowed weight positive");
                }
            }
            close(sum, 1, 1e-12);
        }
        equal(causal.getMixed().getRows()[0], trace.getValues().getRows()[0], "first causal token only sees itself");
        equal(causal.getOutput(), multiply(causal.getMixed().getRows(), new double[][] {{1}, {-1}}), "O=ZWo");
        check(causal.getTokens().get(2).equals("$x_{3}$"), "default token labels");

        AttentionTrace large = new AttentionTrace(LinAlgScene.parse("X=[1000 0;0 1000]\nW=[1 0;0 1]"),
                "X,W,W,W", false, "");
        close(large.getWeights().getRows()[0][0], 1, 1e-12);

        LinAlgScene scene = LinAlgScene.parse(MATRICES + "s=2\nWbad=[1 0;0 1]\nWk3=[1 0 0;0 1 0;0 0 1]\nWo=[1 2 3]");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk", false, ""), "X,Wq,Wk,Wv");
        invalid(() -> new AttentionTrace(scene, "", false, ""), "X,Wq,Wk,Wv");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk,missing", false, ""), "named matrix: 'missing'");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk,s", false, ""), "named matrix: 's'");
        invalid(() -> new AttentionTrace(scene, "X,Wbad,Wk,Wv", false, ""), "Wbad needs 3 rows");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk3,Wv", false, ""), "same dimension d_k");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk,Wv,Wo", false, ""), "Wo needs 2 rows");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk,Wv", false, "a,b"), "2 labels but the input has 3");
        invalid(() -> new AttentionTrace(scene, "X,Wq,Wk,Wv", false, "a,b c,d"), "letters or digits");
        invalid(() -> trace.getStage(8), "between 0 and 7");
        StringBuilder tooMany = new StringBuilder("X=[");
        for (int i = 0; i < 13; i++) tooMany.append(i == 0 ? "" : ";").append(i).append(" 1");
        invalid(() -> new AttentionTrace(LinAlgScene.parse(tooMany + "]\nW=[1;1]"), "X,W,W,W", false, ""), "at most 12");

        // Frames.
        LinAlg rep = parse(MATRICES, "attention=#X,Wq,Wk,Wv#;\nattnTokens=#the,cat,sat#;\nattnStep=0;\n");
        String previousWidth = null;
        for (int step = 0; step <= 7; step++) {
            rep.attentionStep(step);
            String latex = rep.toLaTeX();
            Stage stage = trace.getStage(step);
            check(latex.contains("Self-attention: step " + step + " of 7"), "frame title " + step);
            check(count(latex, "\\begin{pmatrix}") == 2 * 10, "every matrix in every frame keeps the layout stable " + step);
            check(count(latex, "\\begingroup") == count(latex, "\\endgroup"), "balanced groups " + step);
            check(latex.contains("{\\strut the}") && latex.contains("{\\strut sat}"), "token labels in graph");
            String graph = latex.substring(latex.indexOf("\\begin{tikzpicture}"), latex.indexOf("\\end{tikzpicture}"));
            String box = graph.substring(0, graph.indexOf('\n', graph.indexOf("bounding box")));
            if (previousWidth != null) check(box.equals(previousWidth), "graph bounding box stable");
            previousWidth = box;
            int lines = count(graph, "\\draw[");
            check(lines == (stage.ordinal() >= Stage.SCORES.ordinal() ? 9 : 0), "attention lines at stage " + stage);
            check(count(graph, "\\draw[blue") == (stage.ordinal() >= Stage.SOFTMAX.ordinal() ? 9 : 0),
                    "weighted lines from softmax on");
            if (stage == Stage.INITIAL) check(latex.contains("\\square"), "initial results unknown");
            if (stage == Stage.COMPLETE) {
                check(!latex.contains("{\\color{blue}") && !latex.contains("draw=blue"), "completion has no highlights");
                check(latex.contains("residual connection"), "completion explains the rest of the block");
            }
        }
        rep.attentionStep(1);
        check(rep.toLaTeX().contains("q_{31}=") && rep.toLaTeX().contains("1\\cdot 1+1\\cdot 0+0\\cdot 1"),
                "query worked example uses the last token");
        rep.attentionStep(5);
        String softmax = rep.toLaTeX();
        check(softmax.contains("\\operatorname{softmax}") && softmax.contains("m=\\max_j"), "softmax worked example");
        check(softmax.contains(LinAlgScene.formatNumber(trace.getWeights().getRows()[2][0], true)), "weights shown");

        LinAlg masked = parse(MATRICES + "Wo=[1;-1]\n", "attention=#X,Wq,Wk,Wv,Wo#;\nattnCausal=true;\nattnStep=4;\n");
        String scores = masked.toLaTeX();
        check(scores.contains("-\\infty") && scores.contains("Masked pairs"),
                "masked scores shown");
        check(count(scores.substring(scores.indexOf("\\begin{tikzpicture}"), scores.indexOf("\\end{tikzpicture}")),
                "\\draw[") == 6, "masked pairs are not drawn");
        masked.attentionStep(7);
        check(masked.toLaTeX().contains("OUTPUT PROJECTION") && masked.toLaTeX().contains("o_{31}="), "projection frame");
        check(masked.getDynamicMethods().get("Attention step").isMethodButtonVisible(), "attention step visible");

        // Script round trip and animation.
        String script = rep.createScriptFromInstance();
        check(script.contains("attention=#X,Wq,Wk,Wv#") && script.contains("attnTokens=#the,cat,sat#"),
                "list declarations quoted");
        LinAlg reparsed = new LinAlg(null);
        reparsed.createInstanceFromScript(ScriptConversionMethods.removeComments(script), null);
        check(reparsed.toLaTeX().equals(rep.toLaTeX()), "script round trip");
        LinAlg plain = parse("A=[1 2;3 4]\n", "");
        check(plain.createScriptFromInstance().contains("attention=##"), "empty attention declaration roundtrips");
        visible(plain, false, "Attention step", "Next attention step", "Animate attention");
        visible(plain, true, "Multiply");
        LinAlg first = parse(MATRICES, "attention=#X,Wq,Wk,Wv#;\nattnStep=0;\n");
        visible(first, true, "Attention step", "Next attention step", "Animate attention");
        visible(first, false, "Multiply", "MLP step", "Multiplication step", "Toggle plots");
        first.attentionStep(7);
        visible(first, false, "Next attention step");
        first.attentionStep(2);
        check(first.attentionNextStep().contains("attnStep=3"), "next step advances");
        check(first.attentionSteps().equals("7"), "step count method");
        invalid(() -> first.attentionStep(8), "between 0 and 7");
        invalid(() -> parse(MATRICES, "attnStep=2;\n"), "Define attention");
        invalid(() -> parse(MATRICES, "attention=#X,Wq,Wk,Wv#;\nattnStep=x;\n"), "integer");
        invalid(() -> parse(MATRICES + "C=X * @p Wq\n", "attention=#X,Wq,Wk,Wv#;\noperation=p;\n"), "only one of");
        LinAlg cleared = parse(MATRICES + "C=X * @p Wq\n", "attention=#X,Wq,Wk,Wv#;\n");
        check(!cleared.selectOperation("p").contains("attention=#X"), "selecting a multiplication leaves attention");

        RepresentableDefault.removeKnownStuff();
        String animation = parse(MATRICES, "attention=#X,Wq,Wk,Wv#;\nattnTokens=#the,cat,sat#;\n").animateAttention();
        LinAlg animated = new LinAlg(null);
        animated.createInstanceFromScript(ScriptConversionMethods.removeComments(animation), null);
        check(animated.getAnimate().size() == 8, "animation covers every stage");
        String start = parse(MATRICES, "attention=#X,Wq,Wk,Wv#;\nattnStep=0;\n").createScriptFromInstance();
        for (int step = 1; step <= 7; step++) {
            var chained = (LinAlg) ScriptConversionMethods.evaluateChain(start,
                    "this" + ".attentionNextStep".repeat(step), null);
            check(chained.toLaTeX().contains("step " + step + " of 7"), "animation chain reaches step " + step);
        }
        LinAlg example = new LinAlg(null);
        example.createInstanceFromScript(ScriptConversionMethods.removeComments(example.getExampleScripts()[18]), null);
        check(example.getAttentionTrace().getStepCount() == 7, "example script is an attention animation");
        check(example.getExampleScriptName(18, true).startsWith("Transformer"), "example name");
        var processor = example.generatePDFscript(null);
        check(processor.getPreparedSourceString().length() <= processor.getCodeSizeToBeConsideredLarge(),
                "frame fits the web rendering limit");

        if (System.getProperty("attn.svgDirectory") != null) {
            RepresentableDefault.removeKnownStuff();
            LinAlg rendering = new LinAlg(null);
            rendering.createInstanceFromScript(ScriptConversionMethods.removeComments(animation), null);
            render(rendering);
        }
        System.out.println("Passed " + checks + " attention checks.");
    }

    private static void render(LinAlg animated) throws Exception {
        Path directory = Path.of(System.getProperty("attn.svgDirectory")).toAbsolutePath();
        Files.createDirectories(directory);
        var latex = mainServlet.WebLink.class.getDeclaredField("pathLATEX");
        var converter = mainServlet.WebLink.class.getDeclaredField("pathPDF2SVG");
        var pdftk = mainServlet.WebLink.class.getDeclaredField("pathPdftk");
        latex.setAccessible(true);
        converter.setAccessible(true);
        pdftk.setAccessible(true);
        Object oldLatex = latex.get(null), oldConverter = converter.get(null), oldPdftk = pdftk.get(null);
        boolean desktop = eas.veryFastPDF.MainLink.isApplicationOriginDesktop();
        try {
            eas.veryFastPDF.MainLink.setWebMode();
            latex.set(null, System.getProperty("attn.pdflatex"));
            converter.set(null, System.getProperty("attn.pdf2svg"));
            pdftk.set(null, System.getProperty("attn.pdftk"));
            animated.generatePDFscript(directory.toString()).createSVGFileFromAnimationInstructions(
                    directory + java.io.File.separator, false, 250);
            for (int i = 0; i <= 7; i++) {
                String base = mainServlet.WebLink.fileName("x" + i);
                String log = Files.readString(directory.resolve(base + ".log"));
                check(!java.util.regex.Pattern.compile("(?m)^!").matcher(log).find(), "frame has no LaTeX errors: " + i);
                check(!log.contains("Overfull \\hbox"), "frame has no overfull boxes: " + i);
            }
            String svg = Files.readString(directory.resolve(mainServlet.WebLink.fileName("this") + ".svg"));
            for (int i = 0; i <= 7; i++) check(svg.contains("id=\"x" + i + "250\""), "SVG frame " + i);
            System.out.println("Rendered attention SVG: " + directory.resolve(mainServlet.WebLink.fileName("this") + ".svg"));
        } finally {
            latex.set(null, oldLatex);
            converter.set(null, oldConverter);
            pdftk.set(null, oldPdftk);
            if (desktop) eas.veryFastPDF.MainLink.setDesktopMode();
        }
    }

    private static LinAlg parse(String body, String declarations) {
        LinAlg rep = new LinAlg(null);
        rep.createInstanceFromScript(ScriptConversionMethods.removeComments("linalg:\n" + body
                + "--declarations--\n" + declarations + "--declarations-end--"), null);
        return rep;
    }

    private static void visible(LinAlg rep, boolean expected, String... names) {
        HashMap<String, MethodWrapper> methods = rep.getDynamicMethods();
        for (String name : names) check(methods.get(name).isMethodButtonVisible() == expected, name + " visibility");
    }

    private static double[][] multiply(double[][] a, double[][] b) {
        double[][] result = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < b.length; k++) result[i][j] += a[i][k] * b[k][j];
            }
        }
        return result;
    }

    private static int count(String text, String part) {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    private static void equal(LinAlgScene.Value actual, double[][] expected, String message) {
        check(actual.getRowCount() == expected.length, message + " rows");
        for (int i = 0; i < expected.length; i++) equal(actual.getRows()[i], expected[i], message);
    }

    private static void equal(double[] actual, double[] expected, String message) {
        check(actual.length == expected.length, message + " columns");
        for (int j = 0; j < expected.length; j++) {
            check(Math.abs(actual[j] - expected[j]) < 1e-12, message + ": " + actual[j] + " != " + expected[j]);
        }
    }

    private static void invalid(Runnable operation, String message) {
        try {
            operation.run();
            throw new AssertionError("Invalid attention configuration accepted: " + message);
        } catch (IllegalArgumentException expected) {
            check(expected.getMessage() != null && expected.getMessage().contains(message),
                    "explicit validation: " + expected.getMessage() + " (expected " + message + ")");
        }
    }

    private static void close(double actual, double expected, double tolerance) {
        check(Math.abs(actual - expected) <= tolerance, actual + " != " + expected);
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
