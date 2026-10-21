package veryFastPDF.algorithms.linearTrans;

import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Step;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Explicit reverse-mode primitives: backward calls consume upstream gradients, not a hidden tape. */
final class NeuralNetworkFunctions {
    static final String NAMES = "dense|densegradw|densegradb|densegradx|relu|sigmoid|tanh|softmax"
            + "|reluback|sigmoidback|tanhback|softmaxback|hadamard|mse|msegrad|crossentropy|crossentropygrad";

    private NeuralNetworkFunctions() {
    }

    static boolean supports(String function) {
        return function.matches(NAMES);
    }

    static Value evaluate(String function, List<Value> arguments, List<Step> steps) {
        int arity = switch (function) {
        case "dense" -> 3;
        case "relu", "sigmoid", "tanh", "softmax", "densegradb" -> 1;
        default -> 2;
        };
        if (arguments.size() != arity) {
            throw new IllegalArgumentException(function + "(...) expects exactly " + arity + " arguments.");
        }
        for (Value argument : arguments) requireFinite(argument);
        Value a = arguments.get(0);
        Value b = arguments.size() > 1 ? arguments.get(1) : a;
        return switch (function) {
        case "dense" -> dense(a, b, arguments.get(2), steps);
        case "densegradw" -> denseGradW(a, b, steps);
        case "densegradx" -> denseGradX(a, b, steps);
        case "densegradb" -> denseGradB(a, steps);
        case "softmax", "softmaxback" -> softmax(a, function.equals("softmaxback") ? b : null, steps);
        case "crossentropy", "crossentropygrad" ->
            crossEntropy(a, b, function.equals("crossentropygrad"), steps);
        case "mse", "msegrad" -> mse(a, b, function.equals("msegrad"), steps);
        default -> elementwise(function, a, b, steps);
        };
    }

    static String description(String function) {
        return switch (function) {
        case "dense" -> "Dense forward pass: $Z=WX+b$. Rows are neurons/features; columns are examples. "
                + "$W$ is output-by-input, $X$ is input-by-batch, and $b$ is an output-by-1 column, "
                + "explicitly broadcast to every example. No activation is applied implicitly.";
        case "densegradw" -> "Weight gradient: $G_W=G_ZX^T$. Arguments are upstream $G_Z$ and the "
                + "original layer input $X$. Sum contributions across examples; do not average again.";
        case "densegradx" -> "Input gradient: $G_X=W^TG_Z$. Arguments are the original weights $W$ "
                + "and upstream $G_Z$. This passes the chain rule to the preceding layer.";
        case "densegradb" -> "Bias gradient: $G_b=\\sum_c G_{Z,:,c}$. Sum the upstream gradient "
                + "over example columns to reverse the forward bias broadcast. Do not average again.";
        case "relu", "reluback" -> "ReLU applies $a=\\max(0,z)$ elementwise. Its backward call takes "
                + "preactivation $Z$ and upstream $G_A$, returning $G_Z=G_A\\odot [Z>0]$. "
                + "At the nondifferentiable point zero, this implementation chooses derivative zero.";
        case "sigmoid", "sigmoidback" -> "Sigmoid applies $a=1/(1+e^{-z})$ elementwise with a stable "
                + "sign-dependent evaluation. Backward takes preactivation $Z$ and upstream $G_A$: "
                + "$G_Z=G_A\\odot a\\odot(1-a)$. Saturation can produce near-zero gradients.";
        case "tanh", "tanhback" -> "Hyperbolic tangent applies $a=\\tanh z$ elementwise. Backward "
                + "takes preactivation $Z$ and upstream $G_A$: $G_Z=G_A\\odot(1-a^2)$.";
        case "softmax", "softmaxback" -> "Softmax normalizes each example column independently: "
                + "$a_i=e^{z_i-m}/\\sum_j e^{z_j-m}$, $m=\\max_j z_j$. "
                + "Backward takes logits $Z$ and upstream $G_A$: "
                + "$G_{Z,i}=a_i(G_{A,i}-\\sum_j a_jG_{A,j})$. "
                + "This includes cross-component coupling, not an elementwise derivative.";
        case "hadamard" -> "Elementwise product $A\\odot B$ with identical shapes. "
                + "Unlike matrix multiplication, matching entries are multiplied independently.";
        case "mse", "msegrad" -> "Mean squared error: $L=\\sum_i(A_i-T_i)^2/N$, averaged over "
                + "all entries, without a factor of one half. Arguments are predictions $A$ and targets $T$. "
                + "The prediction gradient is $G_A=2(A-T)/N$ with the same shape as $A$.";
        case "crossentropy", "crossentropygrad" -> "Fused softmax cross-entropy takes logits $Z$, "
                + "not probabilities, and same-shaped nonnegative target columns $T$ that sum to one. "
                + "$L=-\\sum_{c,i}T_{ic}\\log(\\operatorname{softmax}(Z)_{ic})/B$ averages over "
                + "$B$ example columns. Stable log-sum-exp avoids taking the log of rounded zero. "
                + "The logits gradient is $(\\operatorname{softmax}(Z)-T)/B$; "
                + "do not apply softmaxback to this fused gradient again.";
        default -> throw new IllegalArgumentException("Unknown neural-network function '" + function + "'.");
        };
    }

    static String mlUsage(String function) {
        if (function.equals("hadamard")) return "Elementwise multiplication combines local derivatives "
                + "with upstream gradients in the chain rule and implements masks or gates without mixing features.";
        if (function.startsWith("dense")) return "Dense layers learn affine feature combinations. Compose "
                + "these primitives across layers using saved forward inputs and preactivations. Compute all "
                + "gradients using the original weights before updating parameters with $W\\gets W-\\eta G_W$.";
        if (function.startsWith("crossentropy")) return "Use for mutually exclusive classification with "
                + "one-hot or soft-label targets. Its fused gradient already includes softmax and loss averaging.";
        if (function.startsWith("mse")) return "Use for regression. The loss gradient supplies the initial "
                + "upstream gradient for explicit backpropagation; averaging is already included.";
        return "Activations introduce nonlinearity; backward calls apply the chain rule to an upstream "
                + "loss gradient. Ordinary matrices and explicit gradient flow also provide building blocks "
                + "for future convolution and pooling operators; no automatic differentiation is implied.";
    }

    static String beginnerExplanation(String function) {
        return switch (function) {
        case "dense" -> "Think of one neuron as a weighted vote: each input is multiplied by a learned weight, "
                + "then these contributions are added. A positive weight reinforces an input; a negative weight "
                + "opposes it. Bias is a learned offset, independent of the input. "
                + "For example $x=(1,2)^T$, $w=(0.2,0.3)$, $b=0.1$ gives "
                + "$z=0.2\\cdot1+0.3\\cdot2+0.1=0.9$. This weighted sum is the preactivation. "
                + "An activation then produces the neuron's output $a$. Each row of $W$ is one neuron's weights.";
        case "relu", "reluback" -> "ReLU means rectified linear unit: negative inputs become zero and positive "
                + "inputs pass through unchanged. For example $-2\\mapsto0$ and $2\\mapsto2$. "
                + "The slope is zero on the negative side and one on the positive side. "
                + "In backpropagation this blocks or passes the incoming gradient. A neuron stuck on the "
                + "negative side may stop learning. The corner at zero has no unique derivative; we choose zero.";
        case "sigmoid", "sigmoidback" -> "Sigmoid smoothly squashes a real input into $(0,1)$: "
                + "$z=0$ gives $a=0.5$, large positive inputs approach one and negative inputs approach zero. "
                + "It is useful for a binary-output score, but a number in $(0,1)$ is not automatically a "
                + "well-calibrated probability. The curve flattens at both ends (saturation). Its local slope "
                + "is largest at zero ($0.25$); flat regions pass back very small gradients.";
        case "tanh", "tanhback" -> "Tanh squashes inputs into $(-1,1)$ and is centered at zero: "
                + "negative inputs stay negative, positive inputs stay positive, and $0\\mapsto0$. "
                + "Its slope at zero is one; near either extreme the curve flattens and gradients shrink. "
                + "Unlike a plain weighted sum, this nonlinear response lets stacked layers model more "
                + "than a single affine transformation.";
        case "softmax", "softmaxback" -> "Each column contains competing class scores called logits, not "
                + "probabilities. Softmax turns that entire score column into positive shares summing to one. "
                + "For equal scores all classes get equal shares. Raising one score changes every share, "
                + "so there is no single independent scalar softmax curve. Subtracting the largest score "
                + "does not change the shares but avoids overflowing exponentials. In finite precision "
                + "extreme probabilities can round to zero or one.";
        case "mse", "msegrad" -> "The target is the desired answer; the prediction is the network's current "
                + "answer. Loss is one number measuring their disagreement, not an activation. MSE subtracts "
                + "target from prediction, squares each difference so signs cannot cancel, and averages. "
                + "Prediction $0.7$ versus target $1$ gives loss $0.09$. Its gradient is $-0.6$ for one entry: "
                + "increasing that prediction locally lowers the loss. A zero gradient here means an exact match.";
        case "crossentropy", "crossentropygrad" -> "For classification, a one-hot target puts one at the "
                + "correct class and zero elsewhere. With one example, the loss is minus the log of the "
                + "probability assigned to the correct class: probability $0.8$ gives about $0.2231$, "
                + "while $0.1$ gives about $2.3026$. Confidently wrong predictions are penalized strongly. "
                + "This function takes raw logits and applies softmax internally, not a second softmax "
                + "to already normalized probabilities. Soft targets instead distribute the desired class shares.";
        case "densegradw" -> "Training asks how changing each weight would change the final loss. "
                + "For one example, a weight's gradient is its neuron's incoming gradient times the input "
                + "connected to that weight. Across examples, add those contributions. "
                + "The result has exactly the same shape as $W$, so it can be used in a parameter update.";
        case "densegradb" -> "A bias is added once to its neuron for every example. Its local derivative "
                + "is one, so its gradient is the sum of that neuron's incoming gradients across examples. "
                + "The result is one bias-gradient column, matching the original bias, not one bias per example.";
        case "densegradx" -> "To teach an earlier layer, pass the loss sensitivity backward through the "
                + "current weights. Each input receives contributions from all neurons it influenced. "
                + "The transpose reverses the direction of the connections; it does not invert the weights. "
                + "Use the original forward-pass weights, not weights already updated during this step.";
        case "hadamard" -> "This multiplies corresponding entries, without summing across connections. "
                + "For example $(2,3)^T\\odot(4,5)^T=(8,15)^T$. "
                + "It is useful when an activation's local derivative scales each incoming gradient separately.";
        default -> throw new IllegalArgumentException("Unknown neural-network function '" + function + "'.");
        };
    }

    private static Value dense(Value w, Value x, Value bias, List<Step> steps) {
        matrix(w); matrix(x); matrix(bias);
        if (w.getColumnCount() != x.getRowCount() || bias.getRowCount() != w.getRowCount()
                || bias.getColumnCount() != 1) {
            throw new IllegalArgumentException("dense(W, X, b) requires W[o,i], X[i,batch], b[o,1].");
        }
        double[][] result = new double[w.getRowCount()][x.getColumnCount()];
        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[r].length; c++) {
                double sum = 0;
                for (int j = 0; j < w.getColumnCount(); j++) {
                    double previous = sum;
                    double product = checked(w.getRows()[r][j] * x.getRows()[j][c]);
                    sum = checked(sum + product);
                    scalarStep(steps, "z_{" + (r + 1) + "," + (c + 1) + "}^{(" + (j + 1)
                            + ")}=" + n(previous) + "+(" + n(w.getRows()[r][j]) + ")("
                            + n(x.getRows()[j][c]) + ")", sum);
                }
                result[r][c] = checked(sum + bias.getRows()[r][0]);
                scalarStep(steps, "z_{" + (r + 1) + "," + (c + 1) + "}="
                        + n(sum) + "+(" + n(bias.getRows()[r][0]) + ")", result[r][c]);
            }
        }
        record(steps, "Z=WX+b", result);
        return new Value(result, false);
    }

    private static Value denseGradW(Value dz, Value x, List<Step> steps) {
        matrix(dz); matrix(x);
        if (dz.getColumnCount() != x.getColumnCount()) {
            throw new IllegalArgumentException("densegradw(GZ, X) requires matching example-column counts.");
        }
        return product(dz, x, false, true, "G_W=G_ZX^T", steps);
    }

    private static Value denseGradX(Value w, Value dz, List<Step> steps) {
        matrix(w); matrix(dz);
        if (w.getRowCount() != dz.getRowCount()) {
            throw new IllegalArgumentException("densegradx(W, GZ) requires W and GZ to have matching output rows.");
        }
        return product(w, dz, true, false, "G_X=W^TG_Z", steps);
    }

    private static Value product(Value a, Value b, boolean transposeA, boolean transposeB,
            String label, List<Step> steps) {
        int rows = transposeA ? a.getColumnCount() : a.getRowCount();
        int columns = transposeB ? b.getRowCount() : b.getColumnCount();
        int inner = transposeA ? a.getRowCount() : a.getColumnCount();
        double[][] result = new double[rows][columns];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                double sum = 0;
                for (int j = 0; j < inner; j++) {
                    double left = transposeA ? a.getRows()[j][r] : a.getRows()[r][j];
                    double right = transposeB ? b.getRows()[c][j] : b.getRows()[j][c];
                    double previous = sum;
                    sum = checked(sum + checked(left * right));
                    scalarStep(steps, "g_{" + (r + 1) + "," + (c + 1) + "}^{(" + (j + 1)
                            + ")}=" + n(previous) + "+(" + n(left) + ")(" + n(right) + ")", sum);
                }
                result[r][c] = sum;
            }
        }
        record(steps, label, result);
        return new Value(result, false);
    }

    private static Value denseGradB(Value dz, List<Step> steps) {
        matrix(dz);
        double[][] result = new double[dz.getRowCount()][1];
        for (int r = 0; r < result.length; r++) {
            double sum = 0;
            for (int c = 0; c < dz.getColumnCount(); c++) {
                double previous = sum;
                sum = checked(sum + dz.getRows()[r][c]);
                scalarStep(steps, "g_{b," + (r + 1) + "}^{(" + (c + 1) + ")}="
                        + n(previous) + "+(" + n(dz.getRows()[r][c]) + ")", sum);
            }
            result[r][0] = sum;
        }
        record(steps, "G_b=\\sum_c G_{Z,:,c}", result);
        return new Value(result, false);
    }

    private static Value elementwise(String function, Value z, Value upstream, List<Step> steps) {
        boolean backward = function.endsWith("back");
        if (backward || function.equals("hadamard")) sameShape(z, upstream);
        double[][] result = new double[z.getRowCount()][z.getColumnCount()];
        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[r].length; c++) {
                double v = z.getRows()[r][c], g = upstream.getRows()[r][c];
                String activation = backward ? function.substring(0, function.length() - 4) : function;
                double a = function.equals("hadamard") ? checked(v * g) : activationValue(activation, v);
                if (backward) {
                    double derivative = activationDerivative(activation, v);
                    scalarStep(steps, "a_{" + (r + 1) + "," + (c + 1) + "}="
                            + "\\operatorname{" + function.replace("back", "") + "}(" + n(v) + ")", a);
                    scalarStep(steps, "d_{" + (r + 1) + "," + (c + 1) + "}="
                            + switch (function) {
                            case "reluback" -> "[" + n(v) + ">0]";
                            case "sigmoidback" -> "\\frac{e^{-|z|}}{(1+e^{-|z|})^2}\\bigg|_{z=" + n(v) + "}";
                            default -> "\\frac{4e^{-2|z|}}{(1+e^{-2|z|})^2}\\bigg|_{z=" + n(v) + "}";
                            }, derivative);
                    scalarStep(steps, "g_{z," + (r + 1) + "," + (c + 1) + "}=("
                            + n(g) + ")(" + n(derivative) + ")", checked(g * derivative));
                    a = checked(g * derivative);
                } else {
                    scalarStep(steps, "a_{" + (r + 1) + "," + (c + 1) + "}="
                            + (function.equals("hadamard") ? "(" + n(v) + ")(" + n(g) + ")"
                                    : "\\operatorname{" + function + "}(" + n(v) + ")"), a);
                }
                result[r][c] = a;
            }
        }
        return new Value(result, z.isScalar());
    }

    private static Value softmax(Value z, Value upstream, List<Step> steps) {
        matrix(z);
        if (upstream != null) sameShape(z, upstream);
        double[][] result = new double[z.getRowCount()][z.getColumnCount()];
        for (int c = 0; c < z.getColumnCount(); c++) {
            double max = z.getRows()[0][c];
            for (int r = 1; r < z.getRowCount(); r++) max = Math.max(max, z.getRows()[r][c]);
            scalarStep(steps, "m_{" + (c + 1) + "}=\\max_i z_{i," + (c + 1) + "}", max);
            double sum = 0;
            for (int r = 0; r < z.getRowCount(); r++) {
                result[r][c] = Math.exp(checked(z.getRows()[r][c] - max));
                sum += result[r][c];
                scalarStep(steps, "e_{" + (r + 1) + "," + (c + 1) + "}=\\exp("
                        + n(z.getRows()[r][c]) + "-(" + n(max) + "))", result[r][c]);
            }
            scalarStep(steps, "s_{" + (c + 1) + "}=\\sum_i e_{i," + (c + 1) + "}", sum);
            double dot = 0;
            for (int r = 0; r < z.getRowCount(); r++) {
                result[r][c] /= sum;
                scalarStep(steps, "a_{" + (r + 1) + "," + (c + 1) + "}=e_{"
                        + (r + 1) + "," + (c + 1) + "}/s_{" + (c + 1) + "}", result[r][c]);
                if (upstream != null) dot = checked(dot + checked(result[r][c] * upstream.getRows()[r][c]));
            }
            if (upstream != null) {
                scalarStep(steps, "d_{" + (c + 1) + "}=\\sum_i a_{i," + (c + 1)
                        + "}g_{a,i," + (c + 1) + "}", dot);
                for (int r = 0; r < z.getRowCount(); r++) {
                    double probability = result[r][c];
                    result[r][c] = checked(probability * checked(upstream.getRows()[r][c] - dot));
                    scalarStep(steps, "g_{z," + (r + 1) + "," + (c + 1) + "}="
                            + n(probability) + "(" + n(upstream.getRows()[r][c]) + "-(" + n(dot) + "))",
                            result[r][c]);
                }
            }
        }
        return new Value(result, false);
    }

    private static Value mse(Value prediction, Value target, boolean gradient, List<Step> steps) {
        sameShape(prediction, target);
        double count = (double) prediction.getRowCount() * prediction.getColumnCount();
        double[][] result = new double[prediction.getRowCount()][prediction.getColumnCount()];
        double loss = 0;
        scalarStep(steps, "N=\\text{number of prediction entries}", count);
        if (!gradient) scalarStep(steps, "L_0", 0);
        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[r].length; c++) {
                double delta = checked(prediction.getRows()[r][c] - target.getRows()[r][c]);
                scalarStep(steps, "d_{" + (r + 1) + "," + (c + 1) + "}="
                        + n(prediction.getRows()[r][c]) + "-(" + n(target.getRows()[r][c]) + ")", delta);
                if (gradient) {
                    result[r][c] = checked((delta / count) * 2);
                    scalarStep(steps, "g_{" + (r + 1) + "," + (c + 1) + "}=2d_{"
                            + (r + 1) + "," + (c + 1) + "}/" + n(count), result[r][c]);
                } else {
                    double contribution = checked((delta / count) * delta);
                    loss = checked(loss + contribution);
                    scalarStep(steps, "L\\gets L+d_{" + (r + 1) + "," + (c + 1) + "}^2/" + n(count), loss);
                }
            }
        }
        return gradient ? new Value(result, prediction.isScalar()) : Value.scalar(loss);
    }

    private static Value crossEntropy(Value logits, Value targets, boolean gradient, List<Step> steps) {
        matrix(logits); sameShape(logits, targets);
        for (int c = 0; c < targets.getColumnCount(); c++) {
            double sum = 0;
            for (int r = 0; r < targets.getRowCount(); r++) {
                if (targets.getRows()[r][c] < 0 || targets.getRows()[r][c] > 1) {
                    throw new IllegalArgumentException("crossentropy targets must be probabilities in [0,1].");
                }
                sum += targets.getRows()[r][c];
            }
            if (Math.abs(sum - 1) > 1e-12) {
                throw new IllegalArgumentException("crossentropy target columns must sum to one (tolerance 1e-12).");
            }
        }
        double[][] probabilities = softmax(logits, null, steps).getRows();
        double[][] result = new double[logits.getRowCount()][logits.getColumnCount()];
        double loss = 0;
        int batch = logits.getColumnCount();
        scalarStep(steps, "B=\\text{number of example columns}", batch);
        if (!gradient) scalarStep(steps, "L_0", 0);
        for (int c = 0; c < batch; c++) {
            double max = logits.getRows()[0][c], expSum = 0, targetSum = 0;
            for (int r = 0; r < logits.getRowCount(); r++) max = Math.max(max, logits.getRows()[r][c]);
            for (int r = 0; r < logits.getRowCount(); r++) {
                expSum += Math.exp(checked(logits.getRows()[r][c] - max));
                targetSum += targets.getRows()[r][c];
            }
            double logSum = Math.log(expSum);
            scalarStep(steps, "h_{" + (c + 1) + "}=\\log\\sum_i e^{z_{i," + (c + 1) + "}-m_{" + (c + 1) + "}}", logSum);
            for (int r = 0; r < logits.getRowCount(); r++) {
                double logP = checked(checked(logits.getRows()[r][c] - max) - logSum);
                if (gradient) {
                    result[r][c] = checked((probabilities[r][c] * targetSum - targets.getRows()[r][c]) / batch);
                    scalarStep(steps, "g_{z," + (r + 1) + "," + (c + 1)
                            + "}=(a_{ic}\\sum_j t_{jc}-t_{ic})/" + batch, result[r][c]);
                } else {
                    loss = checked(loss - checked(targets.getRows()[r][c] * (logP / batch)));
                    scalarStep(steps, "L\\gets L-(" + n(targets.getRows()[r][c]) + ")("
                            + n(logP) + ")/" + batch, loss);
                }
            }
        }
        return gradient ? new Value(result, false) : Value.scalar(loss);
    }

    static double activationValue(String activation, double z) {
        return switch (activation) {
        case "relu" -> Math.max(0, z);
        case "tanh" -> Math.tanh(z);
        case "sigmoid" -> {
            if (z >= 0) yield 1 / (1 + Math.exp(-z));
            double e = Math.exp(z);
            yield e / (1 + e);
        }
        default -> throw new IllegalArgumentException("Unknown scalar activation '" + activation + "'.");
        };
    }

    static double activationDerivative(String activation, double z) {
        double e = Math.exp(-Math.abs(z));
        return switch (activation) {
        case "relu" -> z > 0 ? 1 : 0;
        case "sigmoid" -> e / ((1 + e) * (1 + e));
        case "tanh" -> {
            double square = e * e;
            yield 4 * square / ((1 + square) * (1 + square));
        }
        default -> throw new IllegalArgumentException("Unknown scalar activation '" + activation + "'.");
        };
    }

    private static void matrix(Value value) {
        if (value.isScalar()) {
            throw new IllegalArgumentException("Neural layer operations require matrices/column vectors; use [x] for 1x1.");
        }
    }

    private static void sameShape(Value a, Value b) {
        if (a.isScalar() != b.isScalar() || a.getRowCount() != b.getRowCount()
                || a.getColumnCount() != b.getColumnCount()) {
            throw new IllegalArgumentException("Neural operation requires identical shapes; implicit broadcasting is not allowed.");
        }
    }

    private static void requireFinite(Value value) {
        if (value.getRowCount() == 0 || value.getColumnCount() == 0) {
            throw new IllegalArgumentException("Neural operations require nonempty inputs.");
        }
        for (double[] row : value.getRows()) for (double entry : row) checked(entry);
    }

    private static double checked(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Neural operation encountered a non-finite input or intermediate (overflow).");
        }
        return value;
    }

    private static String n(double value) {
        return LinAlg.statisticNumber(java.math.BigDecimal.valueOf(value));
    }

    private static void scalarStep(List<Step> steps, String expression, double value) {
        record(steps, expression + "=" + n(value), new double[0][]);
    }

    private static void record(List<Step> steps, String expression, double[][] rows) {
        if (steps != null) steps.add(new Step(expression, rows));
    }
}
