package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/**
 * One single-head scaled dot-product self-attention step. Rows of the input
 * are tokens: {@code Q=XW_Q, K=XW_K, V=XW_V, A=softmax(QK^T/sqrt(d_k)), Z=AV}
 * and optionally {@code O=ZW_O}.
 */
public final class AttentionTrace {
    public enum Stage { INITIAL, QUERY, KEY, VALUE, SCORES, SOFTMAX, OUTPUT, PROJECTION, COMPLETE }

    static final int MAX_TOKENS = 12;

    private final Value input, queryWeights, keyWeights, valueWeights, outputWeights;
    private final Value queries, keys, values, scores, weights, mixed, output;
    private final boolean causal;
    private final List<String> tokens = new ArrayList<>();
    private final List<Stage> stages = new ArrayList<>();

    /**
     * @param specification  Matrix names "X,Wq,Wk,Wv" or "X,Wq,Wk,Wv,Wo".
     * @param causal         Iff token i may only attend to tokens j &lt;= i.
     * @param tokenLabels    Optional comma-separated token labels; blank for x_1, x_2, ...
     */
    public AttentionTrace(LinAlgScene scene, String specification, boolean causal, String tokenLabels) {
        String[] names = specification.split(",", -1);
        if (specification.isBlank() || names.length < 4 || names.length > 5) {
            throw new IllegalArgumentException("Attention needs X,Wq,Wk,Wv or X,Wq,Wk,Wv,Wo (matrix names).");
        }
        this.causal = causal;
        input = matrix(scene, names[0]);
        queryWeights = matrix(scene, names[1]);
        keyWeights = matrix(scene, names[2]);
        valueWeights = matrix(scene, names[3]);
        outputWeights = names.length == 5 ? matrix(scene, names[4]) : null;
        int n = input.getRowCount(), d = input.getColumnCount();
        if (n > MAX_TOKENS) {
            throw new IllegalArgumentException("Attention animation supports at most " + MAX_TOKENS
                    + " tokens (rows of " + names[0].trim() + ").");
        }
        requireRows(queryWeights, d, names[1], names[0]);
        requireRows(keyWeights, d, names[2], names[0]);
        requireRows(valueWeights, d, names[3], names[0]);
        if (keyWeights.getColumnCount() != queryWeights.getColumnCount()) {
            throw new IllegalArgumentException("Queries and keys must have the same dimension d_k: "
                    + names[1].trim() + " has " + queryWeights.getColumnCount() + " columns, "
                    + names[2].trim() + " has " + keyWeights.getColumnCount() + ".");
        }
        if (outputWeights != null && outputWeights.getRowCount() != valueWeights.getColumnCount()) {
            throw new IllegalArgumentException(names[4].trim() + " needs " + valueWeights.getColumnCount()
                    + " rows, one per value dimension (columns of " + names[3].trim() + ").");
        }
        parseTokens(tokenLabels, n);

        queries = multiply(input, queryWeights);
        keys = multiply(input, keyWeights);
        values = multiply(input, valueWeights);
        double scale = Math.sqrt(queryWeights.getColumnCount());
        double[][] s = new double[n][n], a = new double[n][n];
        for (int i = 0; i < n; i++) {
            double max = Double.NEGATIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                s[i][j] = isMasked(i, j) ? Double.NEGATIVE_INFINITY
                        : dot(queries.getRows()[i], keys.getRows()[j]) / scale;
                max = Math.max(max, s[i][j]);
            }
            double sum = 0;
            for (int j = 0; j < n; j++) {
                a[i][j] = isMasked(i, j) ? 0 : Math.exp(s[i][j] - max);
                sum += a[i][j];
            }
            for (int j = 0; j < n; j++) a[i][j] /= sum;
        }
        scores = new Value(s, false, n);
        weights = new Value(a, false, n);
        mixed = multiply(weights, values);
        output = outputWeights == null ? null : multiply(mixed, outputWeights);
        for (Value value : outputWeights == null ? List.of(queries, keys, values, mixed)
                : List.of(queries, keys, values, mixed, output)) {
            requireFinite(value, "Attention values overflowed.");
        }
        for (Stage stage : Stage.values()) {
            if (stage != Stage.PROJECTION || outputWeights != null) stages.add(stage);
        }
    }

    private void parseTokens(String tokenLabels, int n) {
        if (tokenLabels == null || tokenLabels.isBlank()) {
            for (int i = 1; i <= n; i++) tokens.add("$x_{" + i + "}$");
            return;
        }
        String[] labels = tokenLabels.split(",", -1);
        if (labels.length != n) {
            throw new IllegalArgumentException("attnTokens has " + labels.length + " labels but the input has "
                    + n + (n == 1 ? " token (row)." : " tokens (rows)."));
        }
        for (String label : labels) {
            if (!label.trim().matches("[A-Za-z0-9]{1,12}")) {
                throw new IllegalArgumentException("Token labels must be 1 to 12 letters or digits: '" + label.trim() + "'.");
            }
            tokens.add(label.trim());
        }
    }

    private static void requireRows(Value weights, int rows, String name, String inputName) {
        if (weights.getRowCount() != rows) {
            throw new IllegalArgumentException(name.trim() + " needs " + rows + " rows, one per feature (column of "
                    + inputName.trim() + "), but has " + weights.getRowCount() + ".");
        }
    }

    private static Value matrix(LinAlgScene scene, String name) {
        var statement = scene.find(name.trim());
        if (statement == null || statement.isDirective() || statement.getValue().isScalar()) {
            throw new IllegalArgumentException("Attention needs a named matrix: '" + name.trim() + "'.");
        }
        Value value = statement.getValue();
        if (value.getRowCount() == 0 || value.getColumnCount() == 0) {
            throw new IllegalArgumentException("Attention matrices must not be empty.");
        }
        requireFinite(value, "Attention entries must be finite.");
        return copy(value);
    }

    private static void requireFinite(Value value, String message) {
        for (double[] row : value.getRows()) {
            for (double entry : row) {
                if (!Double.isFinite(entry)) throw new IllegalArgumentException(message);
            }
        }
    }

    static Value multiply(Value left, Value right) {
        double[][] result = new double[left.getRowCount()][right.getColumnCount()];
        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[r].length; c++) {
                for (int k = 0; k < left.getColumnCount(); k++) {
                    result[r][c] += left.getRows()[r][k] * right.getRows()[k][c];
                }
            }
        }
        return new Value(result, false, right.getColumnCount());
    }

    private static double dot(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    private static Value copy(Value value) {
        if (value == null) return null;
        double[][] rows = new double[value.getRowCount()][];
        for (int r = 0; r < rows.length; r++) rows[r] = value.getRows()[r].clone();
        return new Value(rows, false, value.getColumnCount());
    }

    public boolean isMasked(int query, int key) {
        return causal && key > query;
    }

    public boolean isCausal() { return causal; }
    public boolean hasProjection() { return outputWeights != null; }
    public int getTokenCount() { return input.getRowCount(); }
    public int getKeyDimension() { return queryWeights.getColumnCount(); }
    public List<String> getTokens() { return List.copyOf(tokens); }
    public Value getInput() { return copy(input); }
    public Value getQueryWeights() { return copy(queryWeights); }
    public Value getKeyWeights() { return copy(keyWeights); }
    public Value getValueWeights() { return copy(valueWeights); }
    public Value getOutputWeights() { return copy(outputWeights); }
    public Value getQueries() { return copy(queries); }
    public Value getKeys() { return copy(keys); }
    public Value getValues() { return copy(values); }
    /** Scaled scores; masked entries are negative infinity. */
    public Value getScores() { return copy(scores); }
    public Value getWeights() { return copy(weights); }
    /** The attention output {@code AV}, before the optional projection. */
    public Value getMixed() { return copy(mixed); }
    /** {@code ZW_O}, or {@code null} without projection. */
    public Value getOutput() { return copy(output); }
    public int getStepCount() { return stages.size() - 1; }

    public Stage getStage(int step) {
        if (step < 0 || step >= stages.size()) {
            throw new IllegalArgumentException("Attention step must be between 0 and " + getStepCount() + ".");
        }
        return stages.get(step);
    }
}
