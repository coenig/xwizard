package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** One-example MLP training step. All gradients use the saved, original parameters. */
public final class MlpTrace {
    public enum Phase { INITIAL, FORWARD, LOSS, BACKWARD, UPDATE }
    public record State(int step, Phase phase, int layer, int neuron) {}

    private final Value input;
    private final Value target;
    private final double rate;
    private final String lossFunction;
    private final List<Layer> layers = new ArrayList<>();
    private final List<State> states = new ArrayList<>();
    private final double loss;
    private final double updatedLoss;

    public static final class Layer {
        final String activation;
        final Value weights, bias, input, z, output, upstream, delta, weightGradient, biasGradient;
        final Value updatedWeights, updatedBias;

        private Layer(String activation, Value weights, Value bias, Value input, Value z, Value output,
                Value upstream, Value delta, Value weightGradient, Value biasGradient,
                Value updatedWeights, Value updatedBias) {
            this.activation = activation;
            this.weights = weights;
            this.bias = bias;
            this.input = input;
            this.z = z;
            this.output = output;
            this.upstream = upstream;
            this.delta = delta;
            this.weightGradient = weightGradient;
            this.biasGradient = biasGradient;
            this.updatedWeights = updatedWeights;
            this.updatedBias = updatedBias;
        }

        public String getActivation() { return activation; }
        public int getNeuronCount() { return weights.getRowCount(); }
        public Value getWeights() { return copy(weights); }
        public Value getBias() { return copy(bias); }
        public Value getOutput() { return copy(output); }
        public Value getDelta() { return copy(delta); }
        public Value getWeightGradient() { return copy(weightGradient); }
        public Value getBiasGradient() { return copy(biasGradient); }
        public Value getUpdatedWeights() { return copy(updatedWeights); }
        public Value getUpdatedBias() { return copy(updatedBias); }
    }

    public MlpTrace(LinAlgScene scene, String inputName, String targetName, String layerSpecification,
            String lossFunction, double learningRate) {
        if (!Double.isFinite(learningRate) || learningRate <= 0) {
            throw new IllegalArgumentException("MLP learning rate must be finite and positive.");
        }
        this.rate = learningRate;
        this.lossFunction = lossFunction.trim().toLowerCase(Locale.ROOT);
        if (!this.lossFunction.matches("mse|crossentropy")) {
            throw new IllegalArgumentException("MLP loss must be mse or crossentropy.");
        }
        input = column(scene, inputName);
        target = column(scene, targetName);
        String[] specifications = layerSpecification.split(";", -1);
        if (specifications.length == 0 || layerSpecification.isBlank()) {
            throw new IllegalArgumentException("MLP requires at least one layer: W,b,activation.");
        }
        List<Value> weights = new ArrayList<>(), biases = new ArrayList<>(), inputs = new ArrayList<>();
        List<Value> zs = new ArrayList<>(), outputs = new ArrayList<>();
        List<String> activations = new ArrayList<>();
        java.util.Set<String> parameterNames = new java.util.HashSet<>();
        Value current = input;
        for (int l = 0; l < specifications.length; l++) {
            String[] fields = specifications[l].trim().split(",", -1);
            if (fields.length != 3) {
                throw new IllegalArgumentException("MLP layer " + (l + 1) + " must be W,b,activation.");
            }
            if (!parameterNames.add(fields[0].trim()) || !parameterNames.add(fields[1].trim())) {
                throw new IllegalArgumentException("MLP layers must use distinct weight and bias names; tied parameters are not supported.");
            }
            String activation = fields[2].trim().toLowerCase(Locale.ROOT);
            if (!activation.matches("identity|relu|sigmoid|tanh|softmax")) {
                throw new IllegalArgumentException("Unsupported MLP activation '" + activation + "'.");
            }
            if (activation.equals("softmax") && l != specifications.length - 1) {
                throw new IllegalArgumentException("MLP softmax is supported only in the output layer.");
            }
            Value w = matrix(scene, fields[0]), b = column(scene, fields[1]);
            Value z = call("dense", w, current, b);
            Value output = activate(activation, z);
            weights.add(w);
            biases.add(b);
            inputs.add(current);
            zs.add(z);
            outputs.add(output);
            activations.add(activation);
            current = output;
        }
        int last = specifications.length - 1;
        if (this.lossFunction.equals("crossentropy") && !activations.get(last).equals("softmax")) {
            throw new IllegalArgumentException("MLP crossentropy requires a softmax output layer.");
        }
        Value lossInput = this.lossFunction.equals("crossentropy") ? zs.get(last) : current;
        loss = call(this.lossFunction, lossInput, target).getScalar();
        Value upstream = call(this.lossFunction + "grad", lossInput, target);
        Layer[] calculated = new Layer[specifications.length];
        for (int l = last; l >= 0; l--) {
            String activation = activations.get(l);
            Value delta = l == last && this.lossFunction.equals("crossentropy") ? upstream
                    : activation.equals("identity") ? upstream : call(activation + "back", zs.get(l), upstream);
            Value gw = call("densegradw", delta, inputs.get(l));
            Value gb = call("densegradb", delta);
            Value newW = update(weights.get(l), gw), newB = update(biases.get(l), gb);
            calculated[l] = new Layer(activation, weights.get(l), biases.get(l), inputs.get(l), zs.get(l),
                    outputs.get(l), upstream, delta, gw, gb, newW, newB);
            upstream = call("densegradx", weights.get(l), delta);
        }
        layers.addAll(List.of(calculated));
        current = input;
        Value updatedZ = null;
        for (Layer layer : layers) {
            updatedZ = call("dense", layer.updatedWeights, current, layer.updatedBias);
            current = activate(layer.activation, updatedZ);
        }
        updatedLoss = call(this.lossFunction,
                this.lossFunction.equals("crossentropy") ? updatedZ : current, target).getScalar();
        states.add(new State(0, Phase.INITIAL, -1, -1));
        for (int l = 0; l < layers.size(); l++) addStates(Phase.FORWARD, l);
        states.add(new State(states.size(), Phase.LOSS, -1, -1));
        for (int l = last; l >= 0; l--) addStates(Phase.BACKWARD, l);
        for (int l = 0; l < layers.size(); l++) addStates(Phase.UPDATE, l);
    }

    private void addStates(Phase phase, int layer) {
        for (int n = 0; n < layers.get(layer).getNeuronCount(); n++) {
            states.add(new State(states.size(), phase, layer, n));
        }
    }

    private Value update(Value old, Value gradient) {
        double[][] rows = new double[old.getRowCount()][old.getColumnCount()];
        for (int r = 0; r < rows.length; r++) {
            for (int c = 0; c < rows[r].length; c++) {
                rows[r][c] = old.getRows()[r][c] - rate * gradient.getRows()[r][c];
                if (!Double.isFinite(rows[r][c])) throw new IllegalArgumentException("MLP parameter update overflowed.");
            }
        }
        return new Value(rows, false, old.getColumnCount());
    }

    private static Value matrix(LinAlgScene scene, String name) {
        var statement = scene.find(name.trim());
        if (statement == null || statement.isDirective() || statement.getValue().isScalar()) {
            throw new IllegalArgumentException("MLP needs a named matrix or column vector: '" + name.trim() + "'.");
        }
        Value value = statement.getValue();
        if (value.getRowCount() == 0 || value.getColumnCount() == 0) {
            throw new IllegalArgumentException("MLP matrices must not be empty.");
        }
        for (double[] row : value.getRows()) {
            for (double entry : row) {
                if (!Double.isFinite(entry)) throw new IllegalArgumentException("MLP entries must be finite.");
            }
        }
        return copy(value);
    }

    private static Value column(LinAlgScene scene, String name) {
        Value value = matrix(scene, name);
        if (value.getColumnCount() != 1) {
            throw new IllegalArgumentException("MLP animation takes one example: '" + name.trim() + "' must be a column vector.");
        }
        return value;
    }

    private static Value copy(Value value) {
        double[][] rows = new double[value.getRowCount()][];
        for (int r = 0; r < rows.length; r++) rows[r] = value.getRows()[r].clone();
        return new Value(rows, value.isScalar(), value.getColumnCount());
    }

    private static Value call(String function, Value... values) {
        return NeuralNetworkFunctions.evaluate(function, List.of(values), null);
    }

    private static Value activate(String activation, Value z) {
        return activation.equals("identity") ? z : call(activation, z);
    }

    public Value getInput() { return copy(input); }
    public Value getTarget() { return copy(target); }
    public double getLearningRate() { return rate; }
    public String getLossFunction() { return lossFunction; }
    public List<Layer> getLayers() { return List.copyOf(layers); }
    public double getLoss() { return loss; }
    public double getUpdatedLoss() { return updatedLoss; }
    public int getStepCount() { return states.size() - 1; }

    public State getStep(int step) {
        if (step < 0 || step >= states.size()) {
            throw new IllegalArgumentException("MLP step must be between 0 and " + getStepCount() + ".");
        }
        return states.get(step);
    }

    boolean visible(State state, Phase phase, int layer, int neuron) {
        for (State candidate : states) {
            if (candidate.step > state.step) return false;
            if (candidate.phase == phase && candidate.layer == layer && candidate.neuron == neuron) return true;
        }
        return false;
    }
}
