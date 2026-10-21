package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Plain TikZ teaching views, separate from the numerical forward/backward primitives. */
final class NeuralNetworkPlot {
    record Request(String activation, Value input) implements Serializable {
        private static final long serialVersionUID = 1L;
    }

    private NeuralNetworkPlot() {
    }

    static boolean hasContent(LinAlgScene scene) {
        return scene.getStatements().stream().anyMatch(statement -> !statement.getNeuralPlots().isEmpty());
    }

    static String toTikz(LinAlgScene scene) {
        Map<String, Set<Double>> scalarInputs = new LinkedHashMap<>();
        List<Request> softmax = new ArrayList<>();
        for (var statement : scene.getStatements()) {
            for (Request request : statement.getNeuralPlots()) {
                if (request.activation().equals("softmax")) {
                    if (softmax.stream().noneMatch(previous ->
                            Arrays.deepEquals(previous.input().getRows(), request.input().getRows()))) {
                        softmax.add(request);
                    }
                } else {
                    Set<Double> values = scalarInputs.computeIfAbsent(request.activation(), key -> new LinkedHashSet<>());
                    for (double[] row : request.input().getRows()) for (double value : row) values.add(value);
                }
            }
        }
        List<String> plots = new ArrayList<>();
        scalarInputs.forEach((activation, values) -> plots.add(curve(activation, values)));
        for (Request request : softmax) plots.add(softmax(request.input()));
        return String.join("\n\n\\medskip\n", plots);
    }

    private static String curve(String activation, Set<Double> inputs) {
        StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm,>=stealth]\n");
        panel(sb, activation, false, -3.5, inputs);
        panel(sb, activation, true, 3.5, inputs);
        long outside = inputs.stream().filter(value -> value < -5 || value > 5).count();
        long visible = inputs.size() - outside;
        sb.append("\\node[below,text width=13cm,align=center,font=\\small] at (0,-0.8) {")
                .append("Activation and local slope: ").append(activation).append(". Fixed window $-5\\le z\\le5$. ")
                .append("Red dots mark up to 12 distinct supplied preactivations in this window; ")
                .append("the same inputs are marked on both panels. ")
                .append("Not shown: ").append(outside).append(" outside the window, ")
                .append(Math.max(0, visible - 12)).append(" beyond the marker limit. ")
                .append("Read each panel's own vertical scale.\\\\ ")
                .append("Backpropagation multiplies the local slope by the incoming loss gradient; ")
                .append("the slope alone is not a weight update. ");
        if (activation.equals("relu")) {
            sb.append("ReLU has no derivative at zero; the filled dot shows our chosen value zero, "
                    + "and the open dot at one is excluded.");
        } else {
            sb.append("Flat, saturated regions have small slopes and can weaken the backward gradient.");
        }
        return sb.append("};\n\\end{tikzpicture}").toString();
    }

    private static void panel(StringBuilder sb, String activation, boolean derivative, double center,
            Set<Double> inputs) {
        double low = !derivative && activation.equals("tanh") ? -1.1 : -0.1;
        double high = derivative ? (activation.equals("sigmoid") ? 0.3 : 1.1)
                : activation.equals("relu") ? 5.5 : 1.1;
        sb.append("\\node[above] at (").append(f(center)).append(",3.7) {$")
                .append(derivative ? "a'(z)" : "a(z)").append("$: ").append(activation).append("};\n")
                .append("\\draw[->] (").append(f(center - 2.6)).append(",").append(f(y(0, low, high)))
                .append(") -- (").append(f(center + 2.8)).append(",").append(f(y(0, low, high)))
                .append(") node[right] {$z$};\n")
                .append("\\draw[->] (").append(f(center)).append(",0) -- (").append(f(center))
                .append(",3.5);\n");
        for (int tick : new int[] {-5, 0, 5}) {
            sb.append("\\node[below,font=\\scriptsize] at (").append(f(center + tick / 2.0))
                    .append(",-0.1) {").append(tick).append("};\n");
        }
        double[] ticks = derivative ? activation.equals("sigmoid") ? new double[] {0, 0.125, 0.25}
                : new double[] {0, 0.5, 1} : activation.equals("relu") ? new double[] {0, 2.5, 5}
                : activation.equals("tanh") ? new double[] {-1, 0, 1} : new double[] {0, 0.5, 1};
        for (double tick : ticks) {
            sb.append("\\node[left,font=\\scriptsize,fill=white,inner sep=1pt] at (")
                    .append(f(center - 2.6)).append(",").append(f(y(tick, low, high))).append(") {")
                    .append(f(tick)).append("};\n");
        }
        if (derivative && activation.equals("relu")) {
            sb.append("\\draw[blue,thick] (").append(f(center - 2.5)).append(",").append(f(y(0, low, high)))
                    .append(") -- (").append(f(center)).append(",").append(f(y(0, low, high))).append(");\n")
                    .append("\\draw[blue,thick] (").append(f(center)).append(",").append(f(y(1, low, high)))
                    .append(") -- (").append(f(center + 2.5)).append(",").append(f(y(1, low, high))).append(");\n");
        } else {
            sb.append("\\draw[blue,thick] plot coordinates {");
            for (int i = 0; i <= 160; i++) {
                double z = -5 + i / 16.0;
                double value = derivative ? NeuralNetworkFunctions.activationDerivative(activation, z)
                        : NeuralNetworkFunctions.activationValue(activation, z);
                sb.append("(").append(f(center + z / 2)).append(",").append(f(y(value, low, high))).append(")");
            }
            sb.append("};\n");
        }
        int shown = 0;
        for (double z : inputs) {
            if (z < -5 || z > 5 || shown >= 12) continue;
            double value = derivative ? NeuralNetworkFunctions.activationDerivative(activation, z)
                    : NeuralNetworkFunctions.activationValue(activation, z);
            sb.append("\\fill[red] (").append(f(center + z / 2)).append(",")
                    .append(f(y(value, low, high))).append(") circle (2pt);\n");
            shown++;
        }
        if (derivative && activation.equals("relu")) {
            sb.append("\\fill[blue] (").append(f(center)).append(",").append(f(y(0, low, high)))
                    .append(") circle (1.5pt);\n")
                    .append("\\draw[blue,fill=white] (").append(f(center)).append(",")
                    .append(f(y(1, low, high))).append(") circle (2pt);\n");
        }
    }

    private static double y(double value, double low, double high) {
        return (value - low) / (high - low) * 3.2;
    }

    private static String softmax(Value logits) {
        Value probabilities = NeuralNetworkFunctions.evaluate("softmax", List.of(logits), null);
        int examples = Math.min(4, logits.getColumnCount());
        int classes = Math.min(12, logits.getRowCount());
        List<String> plots = new ArrayList<>();
        for (int c = 0; c < examples; c++) {
            StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm,>=stealth]\n")
                    .append("\\node[above,text width=12cm,align=center] at (0,3.7) {Softmax class shares: example column ")
                    .append(c + 1).append("};\n")
                    .append("\\draw[->] (-5,0) -- (5.4,0) node[right] {class};\n")
                    .append("\\draw[->] (-5,0) -- (-5,3.5) node[above] {$a_i$};\n");
            for (int tick = 0; tick <= 4; tick++) {
                sb.append("\\node[left,font=\\scriptsize] at (-5,").append(f(tick * 0.8))
                        .append(") {").append(f(tick * 0.25)).append("};\n");
            }
            double width = 9.6 / classes;
            double shownMass = 0;
            for (int r = 0; r < classes; r++) {
                double x = -4.8 + width * r;
                double probability = probabilities.getRows()[r][c];
                shownMass += probability;
                sb.append("\\filldraw[fill=blue!30,draw=blue] (").append(f(x + width * 0.1))
                        .append(",0) rectangle (").append(f(x + width * 0.9)).append(",")
                        .append(f(probability * 3.2)).append(");\n")
                        .append("\\node[below,font=\\scriptsize] at (").append(f(x + width / 2))
                        .append(",0) {").append(r + 1).append("};\n")
                        .append("\\node[above,font=\\scriptsize] at (").append(f(x + width / 2)).append(",")
                        .append(f(probability * 3.2)).append(") {$")
                        .append(LinAlg.statisticNumber(java.math.BigDecimal.valueOf(probability))).append("$};\n");
            }
            sb.append("\\node[below,text width=12cm,align=center,font=\\small] at (0,-0.6) {")
                    .append("Scores compete: softmax transforms the entire logit column into class shares ")
                    .append("summing to one. These are not independent sigmoid outputs or observed frequencies.\\\\ ")
                    .append("Showing first ").append(classes).append(" of ").append(logits.getRowCount())
                    .append(" classes; omitted class mass $\\approx ")
                    .append(LinAlg.statisticNumber(java.math.BigDecimal.valueOf(Math.max(0, 1 - shownMass))))
                    .append("$. No renormalization. Showing first ").append(examples).append(" of ")
                    .append(logits.getColumnCount()).append(" example columns. ")
                    .append("Bars do not show the coupled softmax derivative or a training history.")
                    .append("};\n\\end{tikzpicture}");
            plots.add(sb.toString());
        }
        return String.join("\n\n\\medskip\n", plots);
    }

    private static String f(double value) {
        return LinAlgScene.formatNumber(value, true);
    }
}
