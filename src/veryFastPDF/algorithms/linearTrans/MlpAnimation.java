package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;
import veryFastPDF.algorithms.linearTrans.MlpTrace.Layer;
import veryFastPDF.algorithms.linearTrans.MlpTrace.Phase;
import veryFastPDF.algorithms.linearTrans.MlpTrace.State;

/** Neuron-level teaching view of a single MLP training step. */
final class MlpAnimation {
    private MlpAnimation() {}

    static String toLaTeX(MlpTrace trace, int step) {
        State state = trace.getStep(step);
        List<String> rows = calculationRows(trace, state);
        StringBuilder sb = new StringBuilder("\\begingroup\\dimen6=")
                .append(Math.max(14, trace.getLayers().size() * 3 + 2)).append("cm\n");
        for (String row : rows) {
            sb.append("\\setbox0=\\hbox{$").append(row).append("$}")
                    .append("\\ifdim\\wd0>\\dimen6\\dimen6=\\wd0\\fi\n");
        }
        sb.append("\\edef\\xwizardmlpwidth{\\the\\dimen6}")
                .append("\\begin{minipage}{\\xwizardmlpwidth}\\centering\n")
                .append("MLP training: step ").append(step).append(" of ").append(trace.getStepCount())
                .append(" --- ").append(state.phase() == Phase.REFORWARD ? "FORWARD AFTER UPDATE" : state.phase())
                .append("\\par\\medskip\n")
                .append(network(trace, state))
                .append("\\parbox{\\linewidth}{").append(explanation(trace, state)).append("}\\par\\medskip\n");
        for (String row : rows) sb.append("$$").append(row).append("$$\n");
        sb.append("\\parbox{\\linewidth}{\\centering ")
                .append("Blue: forward neuron; red: backward neuron; green: updated parameter row. ")
                .append("Edge labels are weights: source $j$ to neuron $i$ in layer $l$ uses $W^{(l)}_{ij}$. ")
                .append("Squares mark entries not yet visited in this pass. ")
                .append("All gradients use the original weights.}\\par\n");
        sb.append(formula(trace, state));
        if (state.phase() == Phase.LOSS || state.phase() == Phase.BACKWARD || state.phase() == Phase.UPDATE
                || state.phase() == Phase.REFORWARD || state.phase() == Phase.COMPLETE) {
            sb.append("$$L_{\\mathrm{before}}=").append(number(trace.getLoss())).append("$$\n");
        }
        if (step == trace.getStepCount()) {
            sb.append("$$\\hat y_{\\mathrm{after}}=")
                    .append(matrix(trace.getLayers().get(trace.getLayers().size() - 1).updatedOutput))
                    .append(",\\qquad t=").append(matrix(trace.getTarget())).append("$$\n")
                    .append("$$L_{\\mathrm{after}}=").append(number(trace.getUpdatedLoss())).append("$$\n")
                    .append("One simultaneous SGD update completed. A positive learning rate does not guarantee a lower loss.\\par\n");
        }
        return sb.append("\\end{minipage}\\endgroup\n").toString();
    }

    private static List<String> calculationRows(MlpTrace trace, State state) {
        List<String> rows = new ArrayList<>();
        if (state.phase() == Phase.INITIAL) {
            rows.add("x=" + matrix(trace.getInput()) + ",\\qquad t=" + matrix(trace.getTarget()));
            return rows;
        }
        if (state.phase() == Phase.COMPLETE) {
            Layer output = trace.getLayers().get(trace.getLayers().size() - 1);
            rows.add("\\hat y_{\\mathrm{before}}=" + matrix(output.output));
            return rows;
        }
        if (state.phase() == Phase.LOSS) {
            Layer output = trace.getLayers().get(trace.getLayers().size() - 1);
            rows.add("\\hat y=a^{(" + trace.getLayers().size() + ")}=" + matrix(output.output)
                    + ",\\qquad t=" + matrix(trace.getTarget()));
            return rows;
        }
        int l = state.layer(), active = state.neuron();
        Layer layer = trace.getLayers().get(l);
        String suffix = "^{(" + (l + 1) + ")}";
        if (state.phase() == Phase.FORWARD || state.phase() == Phase.REFORWARD) {
            boolean after = state.phase() == Phase.REFORWARD;
            boolean[] visible = visibility(trace, state, state.phase(), l, layer.getNeuronCount());
            Value input = after ? layer.updatedInput : layer.input;
            Value weights = after ? layer.updatedWeights : layer.weights;
            Value bias = after ? layer.updatedBias : layer.bias;
            Value z = after ? layer.updatedZ : layer.z;
            Value output = after ? layer.updatedOutput : layer.output;
            String label = after ? "_{\\mathrm{new}}" : "";
            rows.add("a" + label + "^{(" + l + ")}=" + matrix(input));
            rows.add("W" + label + suffix + "=" + matrix(weights, combined(layer.weights, layer.updatedWeights), null, active, "blue")
                    + ",\\quad b" + label + suffix + "=" + matrix(bias, combined(layer.bias, layer.updatedBias), null, active, "blue"));
            rows.add("z" + label + suffix + "=" + matrix(z, combined(layer.z, layer.updatedZ), visible, active, "blue")
                    + ",\\quad a" + label + suffix + "=" + matrix(output, combined(layer.output, layer.updatedOutput), visible, active, "blue"));
            if (after) rows.add("a_{\\mathrm{before}}" + suffix + "=" + matrix(layer.output));
        } else if (state.phase() == Phase.BACKWARD) {
            boolean[] visible = visibility(trace, state, Phase.BACKWARD, l, layer.getNeuronCount());
            rows.add("a^{(" + l + ")}=" + matrix(layer.input)
                    + ",\\quad z" + suffix + "=" + matrix(layer.z));
            boolean fused = trace.getLossFunction().equals("crossentropy") && l == trace.getLayers().size() - 1;
            rows.add((fused ? "G_z" : "G_a") + suffix + "=" + matrix(layer.upstream));
            if (l < trace.getLayers().size() - 1) {
                Layer next = trace.getLayers().get(l + 1);
                rows.add("W^{(" + (l + 2) + ")}=" + matrix(next.weights)
                        + ",\\quad\\delta^{(" + (l + 2) + ")}=" + matrix(next.delta));
            }
            rows.add("\\delta" + suffix + "=" + matrix(layer.delta, layer.delta, visible, active, "red"));
            rows.add("G_W" + suffix + "=" + matrix(layer.weightGradient, layer.weightGradient, visible, active, "red")
                    + ",\\quad G_b" + suffix + "=" + matrix(layer.biasGradient, layer.biasGradient, visible, active, "red"));
        } else {
            boolean[] updated = visibility(trace, state, Phase.UPDATE, l, layer.getNeuronCount());
            rows.add("G_W" + suffix + "=" + matrix(layer.weightGradient)
                    + ",\\quad G_b" + suffix + "=" + matrix(layer.biasGradient));
            rows.add("W_{\\mathrm{old}}" + suffix + "=" + matrix(layer.weights)
                    + ",\\quad b_{\\mathrm{old}}" + suffix + "=" + matrix(layer.bias));
            rows.add("W_{\\mathrm{current}}" + suffix + "="
                    + matrix(changed(layer.weights, layer.updatedWeights, updated),
                            combined(layer.weights, layer.updatedWeights), null, active, "green!50!black")
                    + ",\\quad b_{\\mathrm{current}}" + suffix + "="
                    + matrix(changed(layer.bias, layer.updatedBias, updated),
                            combined(layer.bias, layer.updatedBias), null, active, "green!50!black"));
        }
        return rows;
    }

    private static String explanation(MlpTrace trace, State state) {
        if (state.phase() == Phase.COMPLETE) {
            return "\\textbf{Finished.} All neurons now show their activations after the update. "
                    + "No calculation is selected. Compare the prediction and loss before and after training below.";
        }
        if (state.phase() == Phase.INITIAL) {
            return "\\textbf{Goal: learn from one example.} $x$ is the input and $t$ is the desired output (target). "
                    + "First predict an output, then measure its error (loss), then work backwards to find how each "
                    + "parameter affects that error, adjust the weights and biases once, and predict again to see the effect. "
                    + "The selected loss is "
                    + (trace.getLossFunction().equals("mse") ? "mean squared error (MSE). " : "cross-entropy. ")
                    + "Only the current calculation and its required inputs are shown below the network.";
        }
        if (state.phase() == Phase.LOSS) {
            return "\\textbf{Measure the prediction error.} The final activation $\\hat y$ is the network's prediction; "
                    + "$t$ is the desired answer. The loss $L$ is one number measuring their disagreement. "
                    + (trace.getLossFunction().equals("mse")
                            ? "Here we use \\textbf{mean squared error}: square each prediction-minus-target difference "
                                    + "and average over the $N$ output entries. Zero means an exact match; smaller is better."
                            : "Here we use \\textbf{cross-entropy}: weight each negative log predicted probability "
                                    + "by its target probability. Smaller is better; with a one-hot target this is "
                                    + "$-\\log$ of the probability assigned to the correct class.")
                    + " $G_a=\\partial L/\\partial a$ describes how changing an output changes the loss.";
        }
        String heading = "\\textbf{Layer " + (state.layer() + 1) + ", neuron " + (state.neuron() + 1) + ": ";
        return switch (state.phase()) {
        case REFORWARD -> heading + "predict again after gradient descent.} Use the updated weights and biases "
                + "and the newly recomputed activations from the previous layer, with the same input example. "
                + "Compare $a_{\\mathrm{new}}$ with $a_{\\mathrm{before}}$ to see how training changed this layer. "
                + "In the network, visited neurons show their new values; unvisited neurons still show their old values. "
                + "This is a forward pass only, not another training update.";
        case FORWARD -> heading + "make a prediction.} Multiply the incoming activations $a$ by this neuron's "
                + "weight row $W$, add its bias $b$ to obtain the weighted sum $z$, then apply the activation function "
                + "to obtain $a$. The circled row and entries belong to this neuron. Input activations are $a^{(0)}=x$.";
        case BACKWARD -> heading + "measure sensitivity to the loss.} "
                + (trace.getLossFunction().equals("crossentropy") && state.layer() == trace.getLayers().size() - 1
                        ? "The fused cross-entropy gradient below is already with respect to logits $z$ "
                                + "(shown as $G_z$, not $G_a$). "
                        : "The incoming gradient $G_a=\\partial L/\\partial a$ says how this neuron's output affects the loss. ")
                + "Compute $\\delta=\\partial L/\\partial z$, then the weight gradient "
                + "$G_W=\\partial L/\\partial W$ and bias gradient $G_b=\\partial L/\\partial b$. "
                + "A gradient is a sensitivity, not a new parameter value. Weights do not change yet.";
        case UPDATE -> heading + "adjust its parameters.} Subtract the learning rate $\\eta$ times the saved gradient "
                + "from each original parameter. Green marks the newly updated row; other rows retain their current values. "
                + "All rows belong to one simultaneous SGD (stochastic gradient descent) update, displayed one at a time.";
        default -> throw new IllegalArgumentException("Unexpected MLP phase.");
        };
    }

    private static String matrix(Value value) {
        return matrix(value, value, null, -1, "");
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
        StringBuilder labels = new StringBuilder();
        for (int l = 1; l < counts.size(); l++) {
            Layer layer = trace.getLayers().get(l - 1);
            for (int i = 0; i < counts.get(l - 1); i++) {
                for (int n = 0; n < counts.get(l); n++) {
                    boolean active = state.layer() == l - 1 && (state.phase() == Phase.BACKWARD
                            || state.phase() == Phase.FORWARD || state.phase() == Phase.UPDATE
                            || state.phase() == Phase.REFORWARD) && state.neuron() == n;
                    String color = state.phase() == Phase.FORWARD || state.phase() == Phase.REFORWARD ? "blue"
                            : state.phase() == Phase.BACKWARD ? "red" : "green!50!black";
                    sb.append("\\draw[").append(active ? color + ",thick" : "gray!35")
                            .append(state.phase() == Phase.BACKWARD ? ",<-" : ",->").append("] (")
                            .append((l - 1) * 3 + 0.6).append(",").append(-i * 1.5)
                            .append(") -- (").append(l * 3 - 0.6).append(",").append(-n * 1.5).append(");\n");
                    boolean updated = trace.visible(state, Phase.UPDATE, l - 1, n);
                    double weight = (updated ? layer.updatedWeights : layer.weights).getRows()[n][i];
                    // Keep labels away from the central crossings, on the incoming side of each neuron.
                    labels.append("\\path (").append((l - 1) * 3 + 0.6).append(",").append(-i * 1.5)
                            .append(") -- (").append(l * 3 - 0.6).append(",").append(-n * 1.5)
                            .append(") node[pos=0.65,fill=white,inner sep=1pt,font=\\scriptsize,text=")
                            .append(active ? color : "black!70").append("] {$").append(number(weight)).append("$};\n");
                }
            }
        }
        sb.append(labels);
        for (int l = 0; l < counts.size(); l++) {
            sb.append("\\node at (").append(l * 3).append(",0.9) {")
                    .append(l == 0 ? "Input" : "Layer " + l + ": " + trace.getLayers().get(l - 1).activation)
                    .append("};\n");
            for (int n = 0; n < counts.get(l); n++) {
                boolean recomputed = l > 0 && trace.visible(state, Phase.REFORWARD, l - 1, n);
                String value = l == 0 ? number(trace.getInput().getRows()[n][0])
                        : recomputed ? number(trace.getLayers().get(l - 1).updatedOutput.getRows()[n][0])
                        : trace.visible(state, Phase.FORWARD, l - 1, n)
                                ? number(trace.getLayers().get(l - 1).output.getRows()[n][0]) : "\\square";
                boolean active = state.layer() == l - 1 && state.neuron() == n;
                String color = !active ? "black" : state.phase() == Phase.FORWARD || state.phase() == Phase.REFORWARD ? "blue"
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
        if (state.phase() == Phase.COMPLETE) return "";
        if (state.phase() == Phase.INITIAL) return "Ready: compute each neuron's weighted sum and activation.\\par\n";
        if (state.phase() == Phase.LOSS) {
            return trace.getLossFunction().equals("mse")
                    ? "$$L=\\frac{1}{N}\\sum_i(\\hat y_i-t_i)^2,\\qquad G_a=\\frac{2(\\hat y-t)}{N}$$\n"
                            + mseCalculation(trace)
                    : "$$L=-\\sum_i t_i\\log(\\operatorname{softmax}(z)_i),\\quad "
                            + "\\delta_i=a_i\\sum_j t_j-t_i$$\n"
                            + "Fused softmax cross-entropy: do not apply the softmax derivative twice.\\par\n";
        }
        int l = state.layer(), n = state.neuron();
        Layer layer = trace.getLayers().get(l);
        String index = "_{" + (n + 1) + "}^{(" + (l + 1) + ")}";
        StringBuilder sb = new StringBuilder();
        if (state.phase() == Phase.FORWARD || state.phase() == Phase.REFORWARD) {
            boolean after = state.phase() == Phase.REFORWARD;
            if (after) sb.append("The following forward equations use only the updated parameters and new activations.\\par\n");
            sb.append("$$z").append(index).append("=\\sum_j W_{").append(n + 1)
                    .append("j}^{(").append(l + 1).append(")}a_j^{(").append(l).append(")}+b").append(index)
                    .append("=").append(number((after ? layer.updatedZ : layer.z).getRows()[n][0])).append("$$\n")
                    .append("$$a").append(index).append("=");
            if (layer.activation.equals("softmax")) {
                sb.append("\\bigl(\\operatorname{softmax}(z^{(").append(l + 1)
                        .append(")})\\bigr)_{").append(n + 1).append("}");
            } else {
                sb.append("\\operatorname{").append(layer.activation).append("}(z").append(index).append(")");
            }
            sb.append("=").append(LinAlg.circledFactor(number((after ? layer.updatedOutput : layer.output).getRows()[n][0]), "blue")).append("$$\n");
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
            if (l == trace.getLayers().size() - 1 && trace.getLossFunction().equals("mse")) {
                sb.append("At the output, $G_a=2(\\hat y-t)/N$ comes from differentiating the mean squared error.\\par\n");
            }
        } else {
            sb.append("$$W_{").append(n + 1).append(",:}^{(").append(l + 1)
                    .append(")\\mathrm{new}}=W_{").append(n + 1).append(",:}^{(").append(l + 1)
                    .append(")}-\\eta G_{W,").append(n + 1).append(",:}^{(").append(l + 1)
                    .append(")},\\quad b_{").append(n + 1).append("}^{(").append(l + 1)
                    .append(")\\mathrm{new}}=b")
                    .append(index).append("-\\eta G_{b,").append(n + 1).append("}^{(").append(l + 1).append(")}")
                    .append(",\\quad\\eta=").append(number(trace.getLearningRate())).append("$$\n")
                    .append("Updated row is green. The network diagram still shows the saved pre-update activations; ")
                    .append("the next forward pass will reveal the new activations and loss.\\par\n");
        }
        return sb.toString();
    }

    private static String mseCalculation(MlpTrace trace) {
        Value prediction = trace.getLayers().get(trace.getLayers().size() - 1).output;
        Value target = trace.getTarget();
        StringBuilder sb = new StringBuilder("$$N=").append(target.getRowCount()).append(",\\qquad L=\\frac{");
        for (int i = 0; i < target.getRowCount(); i++) {
            if (i > 0) sb.append("+");
            sb.append("\\bigl(").append(number(prediction.getRows()[i][0])).append("-(")
                    .append(number(target.getRows()[i][0])).append(")\\bigr)^2");
        }
        return sb.append("}{").append(target.getRowCount()).append("}=")
                .append(number(trace.getLoss())).append("$$\n")
                .append("Displayed numbers are rounded; the loss uses full precision.\\par\n").toString();
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
