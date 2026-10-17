package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eas.math.matrix.Matrix;

/**
 * A "scene" of named linear-algebra objects, parsed from a script body made of
 * statements {@code name = expression}, one per line:
 * <pre>
 * A = [2 0; 0 0.5]     matrix literal (rows separated by ';', entries by space or ',')
 * v = (1, 1)           column vector
 * C = A * A            derived object
 * w = 2 * (C * v) - v  numbers, +, -, *, ^T (transpose), ^-1 (inverse), parentheses
 * </pre>
 * Everything after {@code //} in a line is a comment. For backwards compatibility,
 * a body without any '=' is read as one plain matrix (spaces separate columns,
 * newlines separate rows) named {@value #DEFAULT_MATRIX_NAME}.
 * <p>
 * Vectors are n x 1 matrices. The scene is immutable once parsed; use
 * {@link #withStatement(String, String)} to derive extended scenes.
 */
public class LinAlgScene implements Serializable {

    private static final long serialVersionUID = 7316592481503325747L;

    public static final String DEFAULT_MATRIX_NAME = "A";

    private static final String NAME_REGEX = "[A-Za-z][A-Za-z0-9_]*";
    private static final Pattern NAME = Pattern.compile(NAME_REGEX);
    private static final Pattern STATEMENT = Pattern.compile("^(" + NAME_REGEX + ")\\s*=\\s*(.*)$");
    private static final Pattern NUMBER = Pattern.compile("(\\d+\\.?\\d*|\\.\\d+)([eE][-+]?\\d+)?");

    /** A matrix, vector (n x 1 matrix) or plain number. */
    public static final class Value implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double[][] rows;
        private final boolean scalar;

        Value(double[][] rows, boolean scalar) {
            this.rows = rows;
            this.scalar = scalar;
        }

        static Value scalar(double d) {
            return new Value(new double[][] {{d}}, true);
        }

        public double[][] getRows() {
            return rows;
        }

        public boolean isScalar() {
            return scalar;
        }

        public int getRowCount() {
            return rows.length;
        }

        public int getColumnCount() {
            return rows.length == 0 ? 0 : rows[0].length;
        }

        public double getScalar() {
            return rows[0][0];
        }

        /** The value as a {@link Matrix} (which is indexed by column first). */
        public Matrix toMatrix() {
            Matrix m = new Matrix(getColumnCount(), getRowCount());
            for (int j = 0; j < getRowCount(); j++) {
                for (int i = 0; i < getColumnCount(); i++) {
                    m.set(i, j, rows[j][i]);
                }
            }
            return m;
        }

        private String dimensions() {
            return getRowCount() + "x" + getColumnCount();
        }
    }

    /** One line {@code name = expression} together with its evaluated value. */
    public static final class Statement implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final String expression;
        private final Value value;

        Statement(String name, String expression, Value value) {
            this.name = name;
            this.expression = expression;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getExpression() {
            return expression;
        }

        public Value getValue() {
            return value;
        }
    }

    private final List<Statement> statements = new ArrayList<>();

    public List<Statement> getStatements() {
        return statements;
    }

    public Statement find(String name) {
        Statement found = null;
        for (Statement s : statements) {
            if (s.name.equals(name)) {
                found = s; // The latest definition wins.
            }
        }
        return found;
    }

    /** The last statement whose value is a matrix or vector (not a plain number), or null. */
    public Statement lastNonScalar() {
        for (int i = statements.size() - 1; i >= 0; i--) {
            if (!statements.get(i).value.isScalar()) {
                return statements.get(i);
            }
        }
        return null;
    }

    /** The last statement whose value has at least two columns, i.e., is not a vector, or null. */
    public Statement lastMatrix() {
        for (int i = statements.size() - 1; i >= 0; i--) {
            Value v = statements.get(i).value;
            if (!v.isScalar() && v.getColumnCount() >= 2) {
                return statements.get(i);
            }
        }
        return null;
    }

    /** The first of A, B, ..., Z, M1, M2, ... that is not yet in use. */
    public String nextFreeName() {
        for (char c = 'A'; c <= 'Z'; c++) {
            if (find(String.valueOf(c)) == null) {
                return String.valueOf(c);
            }
        }
        int k = 1;
        while (find("M" + k) != null) {
            k++;
        }
        return "M" + k;
    }

    public static boolean isName(String s) {
        return s != null && NAME.matcher(s).matches();
    }

    /** The script body (without preamble and declarations) representing this scene. */
    public String toScript() {
        StringBuilder sb = new StringBuilder();
        for (Statement s : statements) {
            sb.append(s.name).append(" = ").append(s.expression).append("\n");
        }
        return sb.toString();
    }

    /**
     * @return A new scene with the statement {@code name = expression} appended.
     * @throws IllegalArgumentException if the expression is invalid in this scene.
     */
    public LinAlgScene withStatement(String name, String expression) {
        return parse(toScript() + name + " = " + expression + "\n");
    }

    /**
     * Parses a script body.
     *
     * @throws IllegalArgumentException with a line number if a statement is invalid.
     */
    public static LinAlgScene parse(String body) {
        List<String> lines = new ArrayList<>();
        List<Integer> lineNumbers = new ArrayList<>();
        String[] raw = (body == null ? "" : body).split("\\R");
        for (int i = 0; i < raw.length; i++) {
            String line = raw[i];
            int comment = line.indexOf("//");
            if (comment >= 0) {
                line = line.substring(0, comment);
            }
            line = line.trim();
            if (!line.isEmpty()) {
                lines.add(line);
                lineNumbers.add(i + 1);
            }
        }

        LinAlgScene scene = new LinAlgScene();
        if (lines.isEmpty()) {
            return scene;
        }

        boolean plainMatrix = true;
        for (String line : lines) {
            plainMatrix &= line.indexOf('=') < 0;
        }
        if (plainMatrix) {
            double[][] rows = parsePlainMatrix(lines);
            scene.statements.add(new Statement(DEFAULT_MATRIX_NAME, literalText(rows), new Value(rows, false)));
            return scene;
        }

        Map<String, Value> env = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher m = STATEMENT.matcher(line);
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Line " + lineNumbers.get(i) + ": expected 'name = expression', found '" + line + "'.");
            }
            String name = m.group(1);
            String expression = m.group(2).trim();
            try {
                Value value = new ExpressionParser(expression, env).parseAll();
                env.put(name, value);
                scene.statements.add(new Statement(name, expression, value));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Line " + lineNumbers.get(i) + ": " + e.getMessage(), e);
            }
        }
        return scene;
    }

    private static double[][] parsePlainMatrix(List<String> lines) {
        List<String[]> tokenRows = new ArrayList<>();
        for (String line : lines) {
            tokenRows.add(line.split("\\s+"));
        }
        return toRows(tokenRows);
    }

    private static double[][] toRows(List<String[]> tokenRows) {
        double[][] rows = new double[tokenRows.size()][];
        for (int j = 0; j < rows.length; j++) {
            String[] tokens = tokenRows.get(j);
            if (j > 0 && tokens.length != rows[0].length) {
                throw new IllegalArgumentException("Row " + (j + 1) + " has " + tokens.length
                        + " columns, expected " + rows[0].length + ".");
            }
            rows[j] = new double[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                try {
                    rows[j][i] = Double.parseDouble(tokens[i]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Not a number in row " + (j + 1) + ": '" + tokens[i] + "'", e);
                }
            }
        }
        return rows;
    }

    private static String literalText(double[][] rows) {
        StringBuilder sb = new StringBuilder("[");
        for (int j = 0; j < rows.length; j++) {
            sb.append(j > 0 ? "; " : "");
            for (int i = 0; i < rows[j].length; i++) {
                sb.append(i > 0 ? " " : "").append(formatNumber(rows[j][i], false));
            }
        }
        return sb.append("]").toString();
    }

    /**
     * Formats a number without exponent notation.
     *
     * @param rounded  Iff the number is to be rounded to four decimals (for display of computed values).
     */
    public static String formatNumber(double d, boolean rounded) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return String.valueOf(d);
        }
        if (rounded) {
            d = Math.round(d * 10000.0) / 10000.0;
            if (d == 0) {
                return "0"; // Also normalizes -0.0.
            }
        }
        return d == Math.rint(d)
                ? String.valueOf((long) d)
                : new BigDecimal(String.valueOf(d)).toPlainString();
    }

    /* *****************************************
     * Expression parsing and evaluation
     * *****************************************/

    /** Recursive-descent parser which evaluates while parsing. */
    private static final class ExpressionParser {
        private final String s;
        private final Map<String, Value> env;
        private int pos = 0;

        ExpressionParser(String s, Map<String, Value> env) {
            this.s = s;
            this.env = env;
        }

        Value parseAll() {
            Value v = parseSum();
            skipWhitespace();
            if (pos < s.length()) {
                throw new IllegalArgumentException(
                        "Unexpected '" + s.charAt(pos) + "' at position " + (pos + 1) + " of '" + s + "'.");
            }
            return v;
        }

        private Value parseSum() {
            Value v = parseProduct();
            while (true) {
                skipWhitespace();
                if (accept('+')) {
                    v = add(v, parseProduct(), false);
                } else if (accept('-')) {
                    v = add(v, parseProduct(), true);
                } else {
                    return v;
                }
            }
        }

        private Value parseProduct() {
            Value v = parseUnary();
            while (true) {
                skipWhitespace();
                if (accept('*')) {
                    v = multiply(v, parseUnary());
                } else {
                    return v;
                }
            }
        }

        private Value parseUnary() {
            skipWhitespace();
            if (accept('-')) {
                return scale(parseUnary(), -1);
            }
            if (accept('+')) {
                return parseUnary();
            }
            return parsePostfix();
        }

        private Value parsePostfix() {
            Value v = parsePrimary();
            while (true) {
                skipWhitespace();
                if (s.startsWith("^T", pos)) {
                    pos += 2;
                    v = transpose(v);
                } else if (s.startsWith("^-1", pos)) {
                    pos += 3;
                    v = inverse(v);
                } else if (pos < s.length() && s.charAt(pos) == '^') {
                    throw new IllegalArgumentException(
                            "Only ^T (transpose) and ^-1 (inverse) are supported after '^'.");
                } else {
                    return v;
                }
            }
        }

        private Value parsePrimary() {
            skipWhitespace();
            if (pos >= s.length()) {
                throw new IllegalArgumentException("Unexpected end of expression in '" + s + "'.");
            }

            char c = s.charAt(pos);
            if (c == '(') {
                pos++;
                Value first = parseSum();
                skipWhitespace();
                if (!peek(',')) {
                    expect(')');
                    return first;
                }
                List<Double> components = new ArrayList<>();
                components.add(requireNumber(first));
                while (accept(',')) {
                    components.add(requireNumber(parseSum()));
                    skipWhitespace();
                }
                expect(')');
                double[][] column = new double[components.size()][1];
                for (int i = 0; i < column.length; i++) {
                    column[i][0] = components.get(i);
                }
                return new Value(column, false);
            }

            if (c == '[') {
                int end = s.indexOf(']', pos);
                if (end < 0) {
                    throw new IllegalArgumentException("Missing ']' in '" + s + "'.");
                }
                String content = s.substring(pos + 1, end);
                pos = end + 1;
                return new Value(parseMatrixLiteral(content), false);
            }

            Matcher number = NUMBER.matcher(s);
            number.region(pos, s.length());
            if (number.lookingAt()) {
                pos = number.end();
                return Value.scalar(Double.parseDouble(number.group()));
            }

            Matcher name = NAME.matcher(s);
            name.region(pos, s.length());
            if (name.lookingAt()) {
                pos = name.end();
                Value v = env.get(name.group());
                if (v == null) {
                    throw new IllegalArgumentException("Unknown name '" + name.group()
                            + "'. Names have to be defined in an earlier line.");
                }
                return v;
            }

            throw new IllegalArgumentException(
                    "Unexpected '" + c + "' at position " + (pos + 1) + " of '" + s + "'.");
        }

        private static double[][] parseMatrixLiteral(String content) {
            List<String[]> tokenRows = new ArrayList<>();
            for (String row : content.split(";")) {
                String trimmed = row.trim();
                if (!trimmed.isEmpty()) {
                    tokenRows.add(trimmed.split("[\\s,]+"));
                }
            }
            if (tokenRows.isEmpty()) {
                throw new IllegalArgumentException("Empty matrix '[" + content + "]'.");
            }
            return toRows(tokenRows);
        }

        private static double requireNumber(Value v) {
            if (!v.isScalar()) {
                throw new IllegalArgumentException("The components of a vector (a, b, ...) have to be numbers.");
            }
            return v.getScalar();
        }

        private void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        private boolean peek(char c) {
            return pos < s.length() && s.charAt(pos) == c;
        }

        private boolean accept(char c) {
            if (peek(c)) {
                pos++;
                return true;
            }
            return false;
        }

        private void expect(char c) {
            skipWhitespace();
            if (!accept(c)) {
                throw new IllegalArgumentException("Expected '" + c + "' at position " + (pos + 1) + " of '" + s + "'.");
            }
        }
    }

    private static Value add(Value a, Value b, boolean subtract) {
        String op = subtract ? "subtract" : "add";
        if (a.isScalar() && b.isScalar()) {
            return Value.scalar(a.getScalar() + (subtract ? -1 : 1) * b.getScalar());
        }
        if (a.isScalar() != b.isScalar()) {
            throw new IllegalArgumentException("Cannot " + op + " a number and a matrix.");
        }
        if (a.getRowCount() != b.getRowCount() || a.getColumnCount() != b.getColumnCount()) {
            throw new IllegalArgumentException("Cannot " + op + " a " + a.dimensions() + " and a "
                    + b.dimensions() + " matrix: the dimensions have to be equal.");
        }
        double[][] r = new double[a.getRowCount()][a.getColumnCount()];
        for (int j = 0; j < r.length; j++) {
            for (int i = 0; i < r[j].length; i++) {
                r[j][i] = a.rows[j][i] + (subtract ? -1 : 1) * b.rows[j][i];
            }
        }
        return new Value(r, false);
    }

    private static Value scale(Value a, double factor) {
        double[][] r = new double[a.getRowCount()][a.getColumnCount()];
        for (int j = 0; j < r.length; j++) {
            for (int i = 0; i < r[j].length; i++) {
                r[j][i] = a.rows[j][i] * factor;
            }
        }
        return new Value(r, a.isScalar());
    }

    private static Value multiply(Value a, Value b) {
        if (a.isScalar()) {
            return scale(b, a.getScalar());
        }
        if (b.isScalar()) {
            return scale(a, b.getScalar());
        }
        if (a.getColumnCount() != b.getRowCount()) {
            throw new IllegalArgumentException("Cannot multiply a " + a.dimensions() + " by a " + b.dimensions()
                    + " matrix: the left factor has " + a.getColumnCount() + " columns, but the right factor has "
                    + b.getRowCount() + " rows.");
        }
        double[][] r = new double[a.getRowCount()][b.getColumnCount()];
        for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < r[i].length; j++) {
                double sum = 0;
                for (int k = 0; k < a.getColumnCount(); k++) {
                    sum += a.rows[i][k] * b.rows[k][j];
                }
                r[i][j] = sum;
            }
        }
        return new Value(r, false);
    }

    private static Value transpose(Value a) {
        double[][] r = new double[a.getColumnCount()][a.getRowCount()];
        for (int j = 0; j < a.getRowCount(); j++) {
            for (int i = 0; i < a.getColumnCount(); i++) {
                r[i][j] = a.rows[j][i];
            }
        }
        return new Value(r, a.isScalar());
    }

    private static Value inverse(Value a) {
        if (a.isScalar()) {
            if (a.getScalar() == 0) {
                throw new IllegalArgumentException("Cannot invert 0.");
            }
            return Value.scalar(1 / a.getScalar());
        }
        int n = a.getRowCount();
        if (n != a.getColumnCount()) {
            throw new IllegalArgumentException(
                    "Only square matrices can be inverted, but this one is " + a.dimensions() + ".");
        }

        double scale = 1;
        double[][] w = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                w[i][j] = a.rows[i][j];
                scale = Math.max(scale, Math.abs(a.rows[i][j]));
            }
            w[i][n + i] = 1;
        }

        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(w[r][c]) > Math.abs(w[pivot][c])) {
                    pivot = r;
                }
            }
            if (Math.abs(w[pivot][c]) < 1e-12 * scale) {
                throw new IllegalArgumentException("The matrix is singular, hence it has no inverse.");
            }
            double[] tmp = w[c];
            w[c] = w[pivot];
            w[pivot] = tmp;
            double p = w[c][c];
            for (int k = 0; k < 2 * n; k++) {
                w[c][k] /= p;
            }
            for (int r = 0; r < n; r++) {
                if (r != c) {
                    double f = w[r][c];
                    for (int k = 0; k < 2 * n; k++) {
                        w[r][k] -= f * w[c][k];
                    }
                }
            }
        }

        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(w[i], n, inv[i], 0, n);
        }
        return new Value(inv, false);
    }
}
