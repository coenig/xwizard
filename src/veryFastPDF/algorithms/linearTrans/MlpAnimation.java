package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;
import veryFastPDF.algorithms.linearTrans.MlpTrace.Layer;
import veryFastPDF.algorithms.linearTrans.MlpTrace.Phase;
import veryFastPDF.algorithms.linearTrans.MlpTrace.State;

/** Fixed-layout teaching view of a single MLP training step. */
final class MlpAnimation {
    private MlpAnimation() {}

    static String toLaTeX(MlpTrace trace, int step) {
        State state = trace.getStep(step);
        List<String> rows = new ArrayList<>();
        rows.add("x=" + matrix(trace.getInput(), trace.getInput(), null, -1, "")
                + ",\\qquad t=" + matrix(trace.getTarget(), trace.getTarget(), null, -1, ""));
        for (int l = 0; l < trace.getLayers().size(); l++) {
            Layer layer = trace.getLayers().get(l);
            boolean[] forward = visibility(trace, state, Phase.FORWARD, l, layer.getNeuronCount());
            boolean[] backward = visibility(trace, state, Phase.BACKWARD, l, layer.getNeuronCount());
            boolean[] updated = visibility(trace, state, Phase.UPDATE, l, layer.getNeuronCount());
            Value w = changed(layer.weights, layer.updatedWeights, updated);
            Value b = changed(layer.bias, layer.updatedBias, updated);
            String suffix = "^{(" + (l + 1) + ")}";
            int active = state.layer() == l ? state.neuron() : -1;
            String color = state.phase() == Phase.UPDATE ? "green!50!black"
                    : state.phase() == Phase.BACKWARD ? "red" : "blue";
            rows.add("W" + suffix + "=" + matrix(w, combined(layer.weights, layer.updatedWeights), null, active, color)
                    + ",\\quad b" + suffix + "=" + matrix(b, combined(layer.bias, layer.updatedBias), null, active, color)
                    + ",\\quad z" + suffix + "=" + matrix(layer.z, layer.z, forward,
                            state.phase() == Phase.FORWARD ? active : -1, color)
                    + ",\\quad a" + suffix + "=" + matrix(layer.output, layer.output, forward,
                            state.phase() == Phase.FORWARD ? active : -1, color));
            rows.add("\\delta" + suffix + "=" + matrix(layer.delta, layer.delta, backward,
                            state.phase() == Phase.BACKWARD ? active : -1, color)
                    + ",\\quad G_W" + suffix + "=" + matrix(layer.weightGradient, layer.weightGradient, backward,
                            state.phase() == Phase.BACKWARD ? active : -1, color)
                    + ",\\quad G_b" + suffix + "=" + matrix(layer.biasGradient, layer.biasGradient, backward,
                            state.phase() == Phase.BACKWARD ? active : -1, color));
        }
        StringBuilder sb = new StringBuilder("\\begingroup\\dimen6=")
                .append(Math.max(14, trace.getLayers().size() * 3 + 2)).append("cm\n");
        for (String row : rows) {
            sb.append("\\setbox0=\\hbox{$").append(row).append("$}")
                    .append("\\ifdim\\wd0>\\dimen6\\dimen6=\\wd0\\fi\n");
        }
        sb.append("\\edef\\xwizardmlpwidth{\\the\\dimen6}")
                .append("\\begin{minipage}{\\xwizardmlpwidth}\\centering\n")
                .append("MLP training: step ").append(step).append(" of ").append(trace.getStepCount())
                .append(" --- ").append(state.phase()).append("\\par\\medskip\n")
                .append(network(trace, state));
        for (String row : rows) sb.append("$$").append(row).append("$$\n");
        sb.append("\\parbox{\\linewidth}{\\centering ")
                .append("One example; $\\delta=\\partial L/\\partial z$. Blue: forward neuron; red: backward neuron; ")
                .append("green: updated parameter row. Empty entries have not been visited. ")
                .append("All gradients use the original weights.}\\par\n");
        sb.append(formula(trace, state));
        if (state.phase() == Phase.LOSS || state.phase() == Phase.BACKWARD || state.phase() == Phase.UPDATE) {
            sb.append("$$L_{\\mathrm{before}}=").append(number(trace.getLoss())).append("$$\n");
        }
        if (step == trace.getStepCount()) {
            sb.append("$$L_{\\mathrm{after}}=").append(number(trace.getUpdatedLoss())).append("$$\n")
                    .append("One simultaneous SGD update completed. A positive learning rate does not guarantee a lower loss.\\par\n");
        }
        return sb.append("\\end{minipage}\\endgroup\n").toString();
    }

    private static boolean[] visibility(MlpTrace trace, State state, Phase phase, int layer, int count) {
        boolean[] rows = new boolean[count];
        for (int n = 0; n < count; n++) rows[n] = trace.visible(state, phase, layer, n);
        return rows;
    }

    private static Value changed(Value original, Value updated, boolean[] rows) {
        double[][] result = new double[rows.length][];
        for (int r = 0; r < rows.length; r++) {
            result[r] = (rows[r] ? updated : original).getRows()[r];
        }
        return new Value(result, false, original.getColumnCount());
    }

    private static Value combined(Value a, Value b) {
        double[][] rows = new double[a.getRowCount() + b.getRowCount()][];
        System.arraycopy(a.getRows(), 0, rows, 0, a.getRowCount());
        System.arraycopy(b.getRows(), 0, rows, a.getRowCount(), b.getRowCount());
        return new Value(rows, false, a.getColumnCount());
    }

    private static String matrix(Value value, Value sizing, boolean[] visible, int active, String color) {
        StringBuilder sb = new StringBuilder(LinAlg.multiplicationMatrixSpacing(sizing)).append("\\begin{pmatrix}\n");
        for (int r = 0; r < value.getRowCount(); r++) {
            for (int c = 0; c < value.getColumnCount(); c++) {
                String entry = visible != null && !visible[r] ? "\\square" : number(value.getRows()[r][c]);
                if (r == active && (visible == null || visible[r])) entry = LinAlg.circledFactor(entry, color);
                sb.append(c == 0 ? "" : " & ").append(LinAlg.spacedMultiplicationEntry(entry, c));
            }
            sb.append(" \\\\\n");
        }
        return sb.append("\\end{pmatrix}\\endgroup").toString();
    }

    private static String network(MlpTrace trace, State state) {
        List<Integer> counts = new ArrayList<>();
        counts.add(trace.getInput().getRowCount());
        for (Layer layer : trace.getLayers()) counts.add(layer.getNeuronCount());
        int max = counts.stream().mapToInt(Integer::intValue).max().orElseThrow();
        StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm]\n")
                .append("\\path[use as bounding box] (-1,1) rectangle (")
                .append((counts.size() - 1) * 3 + 1).append(",").append(-max * 1.5).append(");\n");
        for (int l = 1; l < counts.size(); l++) {
            for (int i = 0; i < counts.get(l - 1); i++) {
                for (int n = 0; n < counts.get(l); n++) {
                    boolean active = state.layer() == l - 1 && (state.phase() == Phase.BACKWARD
                            || state.phase() == Phase.FORWARD || state.phase() == Phase.UPDATE) && state.neuron() == n;
                    String color = state.phase() == Phase.FORWARD ? "blue"
                            : state.phase() == Phase.BACKWARD ? "red" : "green!50!black";
                    sb.append("\\draw[").append(active ? color + ",thick" : "gray!35")
                            .append(state.phase() == Phase.BACKWARD ? ",<-" : ",->").append("] (")
                            .append((l - 1) * 3 + 0.6).append(",").append(-i * 1.5)
                            .append(") -- (").append(l * 3 - 0.6).append(",").append(-n * 1.5).append(");\n");
                }
            }
        }
        for (int l = 0; l < counts.size(); l++) {
            sb.append("\\node at (").append(l * 3).append(",0.9) {")
                    .append(l == 0 ? "Input" : "Layer " + l + ": " + trace.getLayers().get(l - 1).activation)
                    .append("};\n");
            for (int n = 0; n < counts.get(l); n++) {
                String value = l == 0 ? number(trace.getInput().getRows()[n][0])
                        : trace.visible(state, Phase.FORWARD, l - 1, n)
                                ? number(trace.getLayers().get(l - 1).output.getRows()[n][0]) : "\\square";
                boolean active = state.layer() == l - 1 && state.neuron() == n;
                String color = !active ? "black" : state.phase() == Phase.FORWARD ? "blue"
                        : state.phase() == Phase.BACKWARD ? "red" : "green!50!black";
                sb.append("\\node[draw=").append(color)
                        .append(",circle,minimum size=1.2cm,inner sep=0pt,fill=white,text width=1.1cm,align=center,font=\\scriptsize")
                        .append(active ? ",very thick" : "").append("] at (").append(l * 3).append(",")
                        .append(-n * 1.5).append(") {$a^{(").append(l).append(")}_{").append(n + 1)
                        .append("}$\\\\$").append(value).append("$};\n");
            }
        }
        return sb.append("\\end{tikzpicture}\\par\n").toString();
    }

    private static String formula(MlpTrace trace, State state) {
        if (state.phase() == Phase.INITIAL) return "Ready: compute each neuron's weighted sum and activation.\\par\n";
        if (state.phase() == Phase.LOSS) {
            return trace.getLossFunction().equals("mse")
                    ? "$$L=\\frac{1}{N}\\sum_i(a_i-t_i)^2,\\qquad G_a=\\frac{2(a-t)}{N}$$\n"
                    : "$$L=-\\sum_i t_i\\log(\\operatorname{softmax}(z)_i),\\quad "
                            + "\\delta_i=a_i\\sum_j t_j-t_i$$\n"
                            + "Fused softmax cross-entropy: do not apply the softmax derivative twice.\\par\n";
        }
        int l = state.layer(), n = state.neuron();
        Layer layer = trace.getLayers().get(l);
        String index = "_{" + (n + 1) + "}^{(" + (l + 1) + ")}";
        StringBuilder sb = new StringBuilder();
        if (state.phase() == Phase.FORWARD) {
            sb.append("$$z").append(index).append("=\\sum_j W_{").append(n + 1)
                    .append("j}^{(").append(l + 1).append(")}a_j^{(").append(l).append(")}+b").append(index)
                    .append("=").append(number(layer.z.getRows()[n][0])).append("$$\n")
                    .append("$$a").append(index).append("=");
            if (layer.activation.equals("softmax")) {
                sb.append("\\bigl(\\operatorname{softmax}(z^{(").append(l + 1)
                        .append(")})\\bigr)_{").append(n + 1).append("}");
            } else {
                sb.append("\\operatorname{").append(layer.activation).append("}(z").append(index).append(")");
            }
            sb.append("=").append(LinAlg.circledFactor(number(layer.output.getRows()[n][0]), "blue")).append("$$\n");
            if (layer.activation.equals("softmax")) {
                sb.append("Softmax uses all output logits: $a_i=e^{z_i-m}/\\sum_j e^{z_j-m}$. ")
                        .append("All logits are computed together; probabilities are revealed one neuron at a time.\\par\n");
            }
        } else if (state.phase() == Phase.BACKWARD) {
            sb.append("$$\\delta").append(index).append("=")
                    .append(LinAlg.circledFactor(number(layer.delta.getRows()[n][0]), "red"))
                    .append(",\\quad G_{W,").append(n + 1).append("j}^{(").append(l + 1)
                    .append(")}=\\delta").append(index).append("a_j^{(").append(l)
                    .append(")},\\quad G_{b,").append(n + 1).append("}^{(").append(l + 1)
                    .append(")}=\\delta").append(index).append("$$\n");
            if (l < trace.getLayers().size() - 1) {
                sb.append("$$G_{a,").append(n + 1).append("}^{(").append(l + 1)
                        .append(")}=\\sum_q W_{q,").append(n + 1).append("}^{(").append(l + 2)
                        .append(")}\\delta_q^{(").append(l + 2).append(")}=")
                        .append(number(layer.upstream.getRows()[n][0])).append("$$\n");
            }
            sb.append(backwardRule(trace, state, layer));
        } else {
            sb.append("$$W_{").append(n + 1).append(",:}^{(").append(l + 1)
                    .append(")\\mathrm{new}}=W_{").append(n + 1).append(",:}^{(").append(l + 1)
                    .append(")}-\\eta G_{W,").append(n + 1).append(",:}^{(").append(l + 1)
                    .append(")},\\quad b_{").append(n + 1).append("}^{(").append(l + 1)
                    .append(")\\mathrm{new}}=b")
                    .append(index).append("-\\eta G_{b,").append(n + 1).append("}^{(").append(l + 1).append(")}")
                    .append(",\\quad\\eta=").append(number(trace.getLearningRate())).append("$$\n")
                    .append("Updated row is green. Forward activations and loss gradients above remain the saved pre-update values.\\par\n");
        }
        return sb.toString();
    }

    private static String backwardRule(MlpTrace trace, State state, Layer layer) {
        if (trace.getLossFunction().equals("crossentropy") && state.layer() == trace.getLayers().size() - 1) {
            return "Fused output gradient: $\\delta_i=a_i\\sum_j t_j-t_i$ (one example).\\par\n";
        }
        return switch (layer.activation) {
        case "identity" -> "$$\\delta_i=G_{a,i}$$\n";
        case "relu" -> "$$\\delta_i=G_{a,i}[z_i>0]$$\n";
        case "sigmoid" -> "$$\\delta_i=G_{a,i}\\,\\sigma(z_i)(1-\\sigma(z_i))$$\n";
        case "tanh" -> "$$\\delta_i=G_{a,i}(1-\\tanh^2 z_i)$$\n";
        case "softmax" -> "$$\\delta_i=a_i(G_{a,i}-\\sum_j a_jG_{a,j})$$\n";
        default -> throw new IllegalArgumentException("Unsupported MLP activation.");
        };
    }

    private static String number(double value) {
        return LinAlgScene.formatNumber(value, true);
    }
}
