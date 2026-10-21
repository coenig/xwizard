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
 * d = det(A)          functions also work inside expressions
 * rank(A)             standalone function calls display their result in place
 * span(A)             column span; span(u, v, ...) accepts column vectors
 * </pre>
 * Numeric functions: det, rank, trace, norm (Euclidean/Frobenius), rref,
 * basis (independent original columns), nullspace (basis columns), inv, transpose.
 * svd, evd (legacy eig) and span are display-only calls. Rank and bases use pivoted elimination
 * with a relative tolerance of 1e-12.
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
    private static final Pattern FUNCTION_DEFINITION = Pattern.compile(
            "^(" + NAME_REGEX + ")\\s*\\(([^()]*)\\)\\s*=\\s*(.+)$");
    private static final Pattern DIRECTIVE = Pattern.compile(
            "^(grad|jacobian|hessian|laplacian|divergence|curl|svd|evd|eig|eigenvalues|eigenvectors|span|det|rank|trace|norm|rref|basis|nullspace|inv|transpose|mean|variance|std|precision|recall|bayes|binomial|poisson|"
                    + NeuralNetworkFunctions.NAMES + ")\\s*\\((.*)\\)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern PLAIN_ROW = Pattern.compile("^[-+0-9.eE\\s]+$");
    private static final Pattern NUMBER = Pattern.compile("(\\d+\\.?\\d*|\\.\\d+)([eE][-+]?\\d+)?");

    /** A matrix, vector (n x 1 matrix) or plain number. */
    public static final class Value implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double[][] rows;
        private final boolean scalar;
        private final int columns;
        private double[] origin;
        private ScalarFunction gradientFunction;
        private String gradientFunctionName;

        public ScalarFunction getGradientFunction() {
            return gradientFunction;
        }

        public String getGradientFunctionName() {
            return gradientFunctionName;
        }

        public double[] getOrigin() {
            return origin == null ? new double[getRowCount()] : origin.clone();
        }

        Value(double[][] rows, boolean scalar) {
            this(rows, scalar, rows.length == 0 ? 0 : rows[0].length);
        }

        Value(double[][] rows, boolean scalar, int columns) {
            this.rows = rows;
            this.scalar = scalar;
            this.columns = columns;
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
            return columns;
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
        private final String function;
        private final String argument;
        private final Value functionInput;
        private final List<Explanation> explanations = new ArrayList<>();
        private final List<Value> statisticalInputs = new ArrayList<>();
        private boolean probabilityCalculation;
        private boolean neuralCalculation;
        private final List<DiscreteDistribution.Request> distributions = new ArrayList<>();
        private ScalarFunction definition;

        public ScalarFunction getDefinition() {
            return definition;
        }

        public List<Explanation> getExplanations() {
            return explanations;
        }

        public List<Value> getStatisticalInputs() {
            return statisticalInputs == null ? List.of() : statisticalInputs;
        }

        public boolean hasProbabilityCalculation() {
            return probabilityCalculation;
        }

        public boolean hasNeuralCalculation() {
            return neuralCalculation;
        }

        List<DiscreteDistribution.Request> getDistributions() {
            return distributions == null ? List.of() : distributions;
        }

        Statement(String name, String expression, Value value) {
            this(name, expression, value, null, null);
        }

        Statement(String name, String expression, Value value, String function, String argument) {
            this(name, expression, value, function, argument, null);
        }

        Statement(String name, String expression, Value value, String function, String argument, Value functionInput) {
            this.name = name;
            this.expression = expression;
            this.value = value;
            this.function = function;
            this.argument = argument;
            this.functionInput = functionInput;
        }

        public Value getFunctionInput() {
            return functionInput;
        }

        /** Iff this is a line like {@code svd(A)}, which is displayed in place and defines no name. */
        public boolean isDirective() {
            return function != null;
        }

        /** The display function name, or null for assignments. */
        public String getFunction() {
            return function;
        }

        /** The argument expression of a directive, else null. */
        public String getArgument() {
            return argument;
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
            if (!statements.get(i).isDirective() && !statements.get(i).value.isScalar()) {
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
            sb.append(s.isDirective() ? s.expression : s.name + " = " + s.expression).append("\n");
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
            plainMatrix &= PLAIN_ROW.matcher(line).matches();
        }
        if (plainMatrix) {
            double[][] rows = parsePlainMatrix(lines);
            scene.statements.add(new Statement(DEFAULT_MATRIX_NAME, literalText(rows), new Value(rows, false)));
            return scene;
        }

        Map<String, Value> env = new HashMap<>();
        Map<String, ScalarFunction> functions = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher definition = FUNCTION_DEFINITION.matcher(line);
            if (definition.matches()) {
                try {
                    String name = definition.group(1);
                    if (NeuralNetworkFunctions.supports(name.toLowerCase(java.util.Locale.ROOT))
                            || name.toLowerCase(java.util.Locale.ROOT).matches(
                            "grad|jacobian|hessian|laplacian|divergence|curl|svd|evd|eig|eigenvalues|eigenvectors|span|det|rank|trace|norm|rref|basis|nullspace|inv|transpose|mean|variance|std|precision|recall|bayes|binomial|poisson")) {
                        throw new IllegalArgumentException("Reserved function name '" + name + "'.");
                    }
                    List<String> parameters = java.util.Arrays.stream(definition.group(2).split(",", -1))
                            .map(String::trim).collect(java.util.stream.Collectors.toList());
                    ScalarFunction scalar = new ScalarFunction(parameters, definition.group(3));
                    Statement entry = new Statement("", line, Value.scalar(0), "definition", line);
                    entry.definition = scalar;
                    scene.statements.add(entry);
                    functions.put(name, scalar);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Line " + lineNumbers.get(i) + ": " + e.getMessage(), e);
                }
                continue;
            }
            Matcher d = DIRECTIVE.matcher(line);
            if (d.matches()) {
                try {
                    String function = d.group(1).toLowerCase(java.util.Locale.ROOT);
                    Value result;
                    if (function.matches("svd|evd|eig")) {
                        result = new ExpressionParser(d.group(2).trim(), env, functions).parseAll();
                        ExpressionParser.requireMatrix(result, function);
                    } else if (function.equals("span")) {
                        ExpressionParser parser = new ExpressionParser("(" + d.group(2) + ")", env, functions);
                        result = ExpressionParser.columnBasis(ExpressionParser.joinColumns(parser.parseArguments()));
                        parser.expectEnd();
                    } else {
                        result = new ExpressionParser(line, env, functions).parseAll();
                    }
                    scene.statements.add(statement("", line, result, function, d.group(2).trim(), env, functions));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Line " + lineNumbers.get(i) + ": " + e.getMessage(), e);
                }
                continue;
            }
            Matcher m = STATEMENT.matcher(line);
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Line " + lineNumbers.get(i) + ": expected 'name = expression', found '" + line + "'.");
            }
            String name = m.group(1);
            String expression = m.group(2).trim();
            try {
                Value value = new ExpressionParser(expression, env, functions).parseAll();
                scene.statements.add(statement(name, expression, value, null, null, env, functions));
                env.put(name, value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Line " + lineNumbers.get(i) + ": " + e.getMessage(), e);
            }
        }
        return scene;
    }

    private static Statement statement(String name, String expression, Value value, String function,
            String argument, Map<String, Value> env, Map<String, ScalarFunction> functions) {
        Matcher call = DIRECTIVE.matcher(expression);
        Value input = null;
        if (call.matches() && call.group(1).toLowerCase(java.util.Locale.ROOT).matches("det|trace|norm")
                && isSingleCall(expression)) {
            // Capture the input now: later redefinitions must not change an earlier explanation.
            input = new ExpressionParser(call.group(2), env, functions).parseAll();
        }
        Statement statement = new Statement(name, expression, value, function, argument, input);
        if (function != null && function.matches("svd|evd|eig|span")) {
            ExpressionParser parser = new ExpressionParser("(" + argument + ")", env, functions);
            Value source = ExpressionParser.joinColumns(parser.parseArguments());
            statement.explanations.addAll(parser.explanations);
            statement.statisticalInputs.addAll(parser.statisticalInputs);
            statement.probabilityCalculation = parser.probabilityCalculation;
            statement.neuralCalculation = parser.neuralCalculation;
            statement.distributions.addAll(parser.distributions);
            if (Character.isUpperCase(expression.charAt(0))) {
                statement.explanations.add(new Explanation(function, source));
            }
        } else {
            ExpressionParser parser = new ExpressionParser(expression, env, functions);
            parser.parseAll();
            statement.explanations.addAll(parser.explanations);
            statement.statisticalInputs.addAll(parser.statisticalInputs);
            statement.probabilityCalculation = parser.probabilityCalculation;
            statement.neuralCalculation = parser.neuralCalculation;
            statement.distributions.addAll(parser.distributions);
        }
        return statement;
    }

    public static final class Explanation implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String function;
        public final Value input;
        public final List<Step> steps = new ArrayList<>();
        public ScalarFunction scalarFunction;
        public String scalarFunctionName;
        public List<Integer> pivotColumns = List.of();
        public Value nullspaceBasis;
        public List<Value> arguments = List.of();

        Explanation(String function, Value input) {
            this.function = function;
            this.input = input;
            if (function.matches("rank|rref|basis|nullspace|span")) {
                ExpressionParser.Reduction reduction = new ExpressionParser.Reduction(input, steps);
                pivotColumns = List.copyOf(reduction.pivots);
                if (function.equals("nullspace")) {
                    nullspaceBasis = ExpressionParser.nullspaceBasis(input, reduction);
                }
            } else if (function.equals("det")) {
                ExpressionParser.determinant(input, steps);
            } else if (function.equals("inv")) {
                inverse(input, steps);
            } else if (function.equals("eigenvalues")) {
                EigenDecomposition.realEigenvalues(input.toMatrix(), steps);
            } else if (function.equals("eigenvectors")) {
                EigenDecomposition.realEigenvectors(input.toMatrix(), steps);
            }
        }
    }

    public static final class Step implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String operation;
        public final double[][] rows;

        Step(String operation, double[][] rows) {
            this.operation = operation;
            this.rows = new double[rows.length][];
            for (int i = 0; i < rows.length; i++) {
                this.rows[i] = rows[i].clone();
            }
        }
    }

    private static void record(List<Step> steps, String operation, double[][] rows) {
        if (steps != null) {
            steps.add(new Step(operation, rows));
        }
    }

    private static boolean isSingleCall(String expression) {
        int depth = 0;
        for (int i = expression.indexOf('('); i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                depth++;
            } else if (expression.charAt(i) == ')' && --depth == 0) {
                return i == expression.length() - 1;
            }
        }
        return false;
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
        BigDecimal value = BigDecimal.valueOf(d);
        if (rounded) {
            value = value.setScale(4, java.math.RoundingMode.HALF_UP);
        }
        return value.stripTrailingZeros().toPlainString();
    }

    /* *****************************************
     * Expression parsing and evaluation
     * *****************************************/

    /** Recursive-descent parser which evaluates while parsing. */
    private static final class ExpressionParser {
        private final List<Explanation> explanations = new ArrayList<>();
        private final List<Value> statisticalInputs = new ArrayList<>();
        private boolean probabilityCalculation;
        private boolean neuralCalculation;
        private final List<DiscreteDistribution.Request> distributions = new ArrayList<>();
        private final String s;
        private final Map<String, Value> env;
        private final Map<String, ScalarFunction> functions;
        private int pos = 0;

        ExpressionParser(String s, Map<String, Value> env, Map<String, ScalarFunction> functions) {
            this.s = s;
            this.env = env;
            this.functions = functions;
        }

        Value parseAll() {
            Value v = parseSum();
            expectEnd();
            return v;
        }

        private void expectEnd() {
            skipWhitespace();
            if (pos < s.length()) {
                throw new IllegalArgumentException(
                        "Unexpected '" + s.charAt(pos) + "' at position " + (pos + 1) + " of '" + s + "'.");
            }
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
                String identifier = name.group();
                skipWhitespace();
                if (peek('(')) {
                    String function = identifier.toLowerCase(java.util.Locale.ROOT);
                    if (function.matches("grad|jacobian|hessian|laplacian|divergence|curl")) {
                        expect('(');
                        skipWhitespace();
                        Matcher functionName = NAME.matcher(s).region(pos, s.length());
                        if (!functionName.lookingAt()) {
                            throw new IllegalArgumentException(function + "(...) expects a function name first.");
                        }
                        pos = functionName.end();
                        ScalarFunction scalar = functions.get(functionName.group());
                        if (scalar == null) {
                            throw new IllegalArgumentException("Unknown scalar function or vector function '" + functionName.group() + "'.");
                        }
                        expect(',');
                        Value point = parseSum();
                        expect(')');
                        if (point.isScalar() && scalar.dimension() == 1) {
                            point = new Value(new double[][] {{point.getScalar()}}, false);
                        }
                        if (point.isScalar() || point.getColumnCount() != 1) {
                            throw new IllegalArgumentException("Evaluation point must be a column vector.");
                        }
                        double[] coordinates = new double[point.getRowCount()];
                        for (int i = 0; i < coordinates.length; i++) coordinates[i] = point.rows[i][0];
                        double[][] rows = scalar.differentiate(function, coordinates);
                        Value result = function.matches("laplacian|divergence")
                                ? Value.scalar(rows[0][0]) : new Value(rows, false);
                        if (function.equals("grad") || function.equals("curl")) {
                            result.origin = coordinates.clone();
                        }
                        if (function.equals("grad")) {
                            result.gradientFunction = scalar;
                            result.gradientFunctionName = functionName.group();
                        }
                        if (Character.isUpperCase(identifier.charAt(0))) {
                            Explanation explanation = new Explanation(function, point);
                            explanation.scalarFunction = scalar;
                            explanation.scalarFunctionName = functionName.group();
                            explanations.add(explanation);
                        }
                        return result;
                    }
                    if (function.matches("svd|evd|eig|span")) {
                        throw new IllegalArgumentException(identifier
                            + "(...) is shown in place and cannot be used in an expression; write it on its own line.");
                    }
                    List<Value> arguments = parseArguments();
                    Value result = evaluateFunction(function, arguments);
                    probabilityCalculation |= function.matches("precision|recall|bayes|binomial|poisson");
                    neuralCalculation |= NeuralNetworkFunctions.supports(function);
                    if (function.matches("binomial|poisson")) {
                        distributions.add(DiscreteDistribution.parse(function, arguments));
                    }
                    if (function.matches("mean|variance|std")) {
                        statisticalInputs.add(arguments.get(0));
                    }
                    if (Character.isUpperCase(identifier.charAt(0))) {
                        Explanation explanation = new Explanation(function, arguments.get(0));
                        explanation.arguments = List.copyOf(arguments);
                        if (NeuralNetworkFunctions.supports(function)) {
                            NeuralNetworkFunctions.evaluate(function, arguments, explanation.steps);
                        }
                        explanations.add(explanation);
                    }
                    return result;
                }
                Value v = env.get(identifier);
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

        private List<Value> parseArguments() {
            expect('(');
            List<Value> arguments = new ArrayList<>();
            skipWhitespace();
            if (!peek(')')) {
                do {
                    arguments.add(parseSum());
                    skipWhitespace();
                } while (accept(','));
            }
            expect(')');
            return arguments;
        }

        private void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        private static void requireMatrix(Value a, String function) {
            if (a.isScalar()) {
                throw new IllegalArgumentException(function + "(...) needs a matrix or vector, not a number.");
            }
            for (double[] row : a.rows) {
                for (double entry : row) {
                    if (!Double.isFinite(entry)) {
                        throw new IllegalArgumentException(function + "(...) requires finite matrix entries.");
                    }
                }
            }
        }

        private static Value evaluateFunction(String function, List<Value> arguments) {
            if (NeuralNetworkFunctions.supports(function)) {
                return NeuralNetworkFunctions.evaluate(function, arguments, null);
            }
            if (function.matches("binomial|poisson")) {
                return Value.scalar(DiscreteDistribution.parse(function, arguments).result());
            }
            if (function.matches("precision|recall|bayes")) {
                return Value.scalar(ProbabilityMetrics.calculate(function, arguments).scalar(function));
            }
            if (!function.matches("det|rank|trace|norm|rref|basis|nullspace|inv|transpose|eigenvalues|eigenvectors|mean|variance|std")) {
                throw new IllegalArgumentException("Unknown function '" + function + "'.");
            }
            if (arguments.size() != 1) {
                throw new IllegalArgumentException(function + "(...) expects exactly one argument.");
            }
            Value a = arguments.get(0);
            if (function.matches("mean|variance|std")) {
                return Value.scalar(Statistics.summarize(a).result(function));
            }
            requireMatrix(a, function);
            switch (function) {
            case "eigenvalues":
                requireSquare(a, function);
                double[] eigenvalues = EigenDecomposition.realEigenvalues(a.toMatrix());
                double[][] values = new double[eigenvalues.length][1];
                for (int i = 0; i < eigenvalues.length; i++) values[i][0] = eigenvalues[i];
                return new Value(values, false);
            case "eigenvectors":
                requireSquare(a, function);
                return new Value(EigenDecomposition.realEigenvectors(a.toMatrix()), false);
            case "det":
                return Value.scalar(determinant(a));
            case "trace":
                requireSquare(a, function);
                double trace = 0;
                for (int i = 0; i < a.getRowCount(); i++) {
                    trace += a.rows[i][i];
                }
                return Value.scalar(trace);
            case "norm":
                double norm = 0;
                for (double[] row : a.rows) {
                    for (double entry : row) {
                        norm = Math.hypot(norm, entry);
                    }
                }
                return Value.scalar(norm);
            case "transpose":
                return transpose(a);
            case "inv":
                return inverse(a);
            case "basis":
                return columnBasis(a);
            default:
                Reduction reduction = new Reduction(a);
                if (function.equals("rank")) {
                    return Value.scalar(reduction.pivots.size());
                }
                if (function.equals("rref")) {
                    return new Value(reduction.rows, false, a.getColumnCount());
                }
                return nullspaceBasis(a, reduction);
            }
        }

        private static Value nullspaceBasis(Value a, Reduction reduction) {
            int n = a.getColumnCount();
            double[][] basis = new double[n][n - reduction.pivots.size()];
            int column = 0;
            for (int free = 0; free < n; free++) {
                if (!reduction.pivots.contains(free)) {
                    basis[free][column] = 1;
                    for (int i = 0; i < reduction.pivots.size(); i++) {
                        basis[reduction.pivots.get(i)][column] = -reduction.rows[i][free];
                    }
                    column++;
                }
            }
            return new Value(basis, false, column);
        }

        private static void requireSquare(Value a, String function) {
            if (a.getRowCount() != a.getColumnCount()) {
                throw new IllegalArgumentException(function + "(...) requires a square matrix, but got "
                        + a.dimensions() + ".");
            }
        }

        private static double determinant(Value a) {
            return determinant(a, null);
        }

        private static double determinant(Value a, List<Step> steps) {
            requireSquare(a, "det");
            double[][] rows = copyRows(a);
            record(steps, "\\text{Start}", rows);
            double determinant = 1;
            for (int c = 0; c < rows.length; c++) {
                int pivot = pivotRow(rows, c, c);
                if (rows[pivot][c] == 0) {
                    record(steps, "\\text{Zero pivot column: determinant is zero}", rows);
                    return 0;
                }
                if (pivot != c) {
                    double[] temp = rows[c];
                    rows[c] = rows[pivot];
                    rows[pivot] = temp;
                    determinant = -determinant;
                    record(steps, "R_{" + (c + 1) + "}\\leftrightarrow R_{" + (pivot + 1) + "}", rows);
                }
                double value = rows[c][c];
                determinant *= value;
                for (int r = c + 1; r < rows.length; r++) {
                    double factor = rows[r][c] / value;
                    for (int j = c + 1; j < rows.length; j++) {
                        rows[r][j] -= factor * rows[c][j];
                    }
                    rows[r][c] = 0;
                    if (factor != 0) {
                        record(steps, rowSubtract(r, c, factor), rows);
                    }
                }
            }
            record(steps, "\\text{Multiply diagonal entries, with one sign change per row swap}", rows);
            return determinant;
        }

        private static double[][] copyRows(Value a) {
            double[][] rows = new double[a.getRowCount()][];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = a.rows[i].clone();
            }
            return rows;
        }

        private static int pivotRow(double[][] rows, int first, int column) {
            int pivot = first;
            for (int i = first + 1; i < rows.length; i++) {
                if (Math.abs(rows[i][column]) > Math.abs(rows[pivot][column])) {
                    pivot = i;
                }
            }
            return pivot;
        }

        private static final class Reduction {
            final double[][] rows;
            final List<Integer> pivots = new ArrayList<>();

            Reduction(Value a) {
                this(a, null);
            }

            Reduction(Value a, List<Step> steps) {
                rows = copyRows(a);
                record(steps, "\\text{Start}", rows);
                double scale = 0;
                for (double[] row : rows) {
                    for (double entry : row) {
                        scale = Math.max(scale, Math.abs(entry));
                    }
                }
                if (scale == 0) {
                    return;
                }
                // Normalize first so the rank decision is independent of overall magnitude.
                for (double[] row : rows) {
                    for (int c = 0; c < row.length; c++) {
                        row[c] /= scale;
                    }
                }
                if (scale != 1) {
                    record(steps, "\\text{Divide all entries by }" + formatNumber(scale, false), rows);
                }
                int r = 0;
                for (int c = 0; c < a.getColumnCount() && r < rows.length; c++) {
                    int pivot = pivotRow(rows, r, c);
                    if (Math.abs(rows[pivot][c]) <= 1e-12) {
                        for (int i = r; i < rows.length; i++) {
                            rows[i][c] = 0;
                        }
                        continue;
                    }
                    double[] temp = rows[r];
                    rows[r] = rows[pivot];
                    rows[pivot] = temp;
                    if (pivot != r) {
                        record(steps, "R_{" + (r + 1) + "}\\leftrightarrow R_{" + (pivot + 1) + "}", rows);
                    }
                    double value = rows[r][c];
                    for (int j = c; j < a.getColumnCount(); j++) {
                        rows[r][j] /= value;
                    }
                    rows[r][c] = 1;
                    if (value != 1) {
                        record(steps, "R_{" + (r + 1) + "}\\gets R_{" + (r + 1) + "}/("
                                + formatNumber(value, false) + ")", rows);
                    }
                    for (int i = 0; i < rows.length; i++) {
                        if (i != r) {
                            double factor = rows[i][c];
                            for (int j = c + 1; j < a.getColumnCount(); j++) {
                                rows[i][j] -= factor * rows[r][j];
                            }
                            rows[i][c] = 0;
                            if (factor != 0) {
                                record(steps, rowSubtract(i, r, factor), rows);
                            }
                        }
                    }
                    pivots.add(c);
                    r++;
                }
                for (int i = r; i < rows.length; i++) {
                    java.util.Arrays.fill(rows[i], 0);
                }
                record(steps, "\\text{RREF; pivot columns (1-based): }"
                        + pivots.stream().map(p -> Integer.toString(p + 1))
                                .collect(java.util.stream.Collectors.joining(",\\,")), rows);
            }
        }

        private static Value columnBasis(Value a) {
            Reduction reduction = new Reduction(a);
            double[][] basis = new double[a.getRowCount()][reduction.pivots.size()];
            for (int i = 0; i < basis.length; i++) {
                for (int j = 0; j < reduction.pivots.size(); j++) {
                    basis[i][j] = a.rows[i][reduction.pivots.get(j)];
                }
            }
            return new Value(basis, false, reduction.pivots.size());
        }

        private static Value joinColumns(List<Value> arguments) {
            if (arguments.isEmpty()) {
                throw new IllegalArgumentException("span(...) needs a matrix or one or more column vectors.");
            }
            Value first = arguments.get(0);
            requireMatrix(first, "span");
            if (arguments.size() == 1) {
                return first;
            }
            double[][] rows = new double[first.getRowCount()][arguments.size()];
            for (int c = 0; c < arguments.size(); c++) {
                Value v = arguments.get(c);
                requireMatrix(v, "span");
                if (v.getRowCount() != first.getRowCount() || v.getColumnCount() != 1) {
                    throw new IllegalArgumentException("span(u, v, ...) requires column vectors of equal dimension.");
                }
                for (int r = 0; r < rows.length; r++) {
                    rows[r][c] = v.rows[r][0];
                }
            }
            return new Value(rows, false);
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
        return new Value(r, false, a.getColumnCount());
    }

    private static Value scale(Value a, double factor) {
        double[][] r = new double[a.getRowCount()][a.getColumnCount()];
        for (int j = 0; j < r.length; j++) {
            for (int i = 0; i < r[j].length; i++) {
                r[j][i] = a.rows[j][i] * factor;
            }
        }
        return new Value(r, a.isScalar(), a.getColumnCount());
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
        return new Value(r, false, b.getColumnCount());
    }

    private static Value transpose(Value a) {
        double[][] r = new double[a.getColumnCount()][a.getRowCount()];
        for (int j = 0; j < a.getRowCount(); j++) {
            for (int i = 0; i < a.getColumnCount(); i++) {
                r[i][j] = a.rows[j][i];
            }
        }
        return new Value(r, a.isScalar(), a.getRowCount());
    }

    private static Value inverse(Value a) {
        return inverse(a, null);
    }

    private static String rowSubtract(int row, int pivot, double factor) {
        return "R_{" + (row + 1) + "}\\gets R_{" + (row + 1) + "}-("
                + formatNumber(factor, false) + ")R_{" + (pivot + 1) + "}";
    }

    private static Value inverse(Value a, List<Step> steps) {
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

        double scale = 0;
        double[][] w = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                w[i][j] = a.rows[i][j];
                scale = Math.max(scale, Math.abs(a.rows[i][j]));
            }
            w[i][n + i] = 1;
        }
        record(steps, "\\text{Augment with identity: }[A\\mid I]", w);

        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(w[r][c]) > Math.abs(w[pivot][c])) {
                    pivot = r;
                }
            }
            if (Math.abs(w[pivot][c]) <= 1e-12 * scale) {
                throw new IllegalArgumentException("The matrix is singular or numerically singular, hence no reliable inverse can be computed.");
            }
            double[] tmp = w[c];
            w[c] = w[pivot];
            w[pivot] = tmp;
            if (pivot != c) {
                record(steps, "R_{" + (c + 1) + "}\\leftrightarrow R_{" + (pivot + 1) + "}", w);
            }
            double p = w[c][c];
            for (int k = 0; k < 2 * n; k++) {
                w[c][k] /= p;
            }
            if (p != 1) {
                record(steps, "R_{" + (c + 1) + "}\\gets R_{" + (c + 1) + "}/("
                        + formatNumber(p, false) + ")", w);
            }
            for (int r = 0; r < n; r++) {
                if (r != c) {
                    double f = w[r][c];
                    for (int k = 0; k < 2 * n; k++) {
                        w[r][k] -= f * w[c][k];
                    }
                    if (f != 0) {
                        record(steps, rowSubtract(r, c, f), w);
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
