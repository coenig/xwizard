package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** A scalar expression tree with symbolic partial differentiation. */
public final class ScalarFunction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> parameters;
    private final Node expression;

    public ScalarFunction(List<String> parameters, String source) {
        if (parameters.isEmpty() || parameters.stream().distinct().count() != parameters.size()
                || parameters.stream().anyMatch(p -> !LinAlgScene.isName(p))) {
            throw new IllegalArgumentException("Function parameters must be distinct variable names.");
        }
        this.parameters = List.copyOf(parameters);
        Parser parser = new Parser(source, parameters);
        expression = parser.sum();
        parser.space();
        if (parser.position != source.length()) {
            throw new IllegalArgumentException("Unexpected scalar expression at position " + (parser.position + 1) + ".");
        }
    }

    public int dimension() {
        return parameters.size();
    }

    public String toLaTeX() {
        return expression.latex(parameters);
    }

    public String gradientLaTeX() {
        StringBuilder result = new StringBuilder("\\begin{pmatrix}");
        for (int i = 0; i < dimension(); i++) {
            result.append(i == 0 ? "" : "\\\\").append(expression.derivative(i).latex(parameters));
        }
        return result.append("\\end{pmatrix}").toString();
    }

    public double[] gradient(double[] point) {
        if (point.length != dimension()) {
            throw new IllegalArgumentException("Gradient point has " + point.length + " components; expected "
                    + dimension() + ".");
        }
        for (double component : point) {
            if (!Double.isFinite(component)) {
                throw new IllegalArgumentException("Gradient point must have finite components.");
            }
        }
        expression.evaluate(point);
        double[] result = new double[dimension()];
        for (int i = 0; i < result.length; i++) {
            result[i] = expression.derivative(i).evaluate(point);
        }
        return result;
    }

    private static Node constant(double value) {
        return new Node("number", value, -1, null, null);
    }

    private static Node operation(String op, Node a, Node b) {
        if (a.op.equals("number") && b != null && b.op.equals("number")) {
            return constant(new Node(op, 0, -1, a, b).evaluate(new double[0]));
        }
        if (op.equals("+") && zero(a)) return b;
        if ((op.equals("+") || op.equals("-")) && zero(b)) return a;
        if (op.equals("*") && (zero(a) || zero(b))) return constant(0);
        if (op.equals("*") && one(a)) return b;
        if ((op.equals("*") || op.equals("/") || op.equals("^")) && one(b)) return a;
        if (op.equals("^") && zero(b)) return constant(1);
        return new Node(op, 0, -1, a, b);
    }

    private static boolean zero(Node n) {
        return n != null && n.op.equals("number") && n.number == 0;
    }

    private static boolean one(Node n) {
        return n != null && n.op.equals("number") && n.number == 1;
    }

    private static final class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        final String op;
        final double number;
        final int variable;
        final Node a;
        final Node b;

        Node(String op, double number, int variable, Node a, Node b) {
            this.op = op;
            this.number = number;
            this.variable = variable;
            this.a = a;
            this.b = b;
        }

        double evaluate(double[] point) {
            double x = a == null ? 0 : a.evaluate(point);
            double y = b == null ? 0 : b.evaluate(point);
            double result;
            switch (op) {
            case "number": result = number; break;
            case "variable": result = point[variable]; break;
            case "+": result = x + y; break;
            case "-": result = x - y; break;
            case "*": result = x * y; break;
            case "/": result = x / y; break;
            case "^": result = Math.pow(x, y); break;
            case "sin": result = Math.sin(x); break;
            case "cos": result = Math.cos(x); break;
            case "exp": result = Math.exp(x); break;
            case "log": result = Math.log(x); break;
            default: throw new IllegalStateException("Unknown scalar operator " + op);
            }
            if (!Double.isFinite(result)) {
                throw new IllegalArgumentException("Scalar expression is undefined or non-finite at this point (" + op + ").");
            }
            return result;
        }

        Node derivative(int index) {
            Node da = a == null ? constant(0) : a.derivative(index);
            Node db = b == null ? constant(0) : b.derivative(index);
            switch (op) {
            case "number": return constant(0);
            case "variable": return constant(variable == index ? 1 : 0);
            case "+": case "-": return operation(op, da, db);
            case "*": return operation("+", operation("*", da, b), operation("*", a, db));
            case "/": return operation("/", operation("-", operation("*", da, b), operation("*", a, db)),
                    operation("^", b, constant(2)));
            case "^":
                if (zero(db)) {
                    if (zero(da) || zero(b)) return constant(0);
                    return operation("*", operation("*", b, operation("^", a, operation("-", b, constant(1)))), da);
                }
                return operation("*", this, operation("+", operation("*", db, operation("log", a, null)),
                        operation("/", operation("*", b, da), a)));
            case "sin": return operation("*", operation("cos", a, null), da);
            case "cos": return operation("*", operation("*", constant(-1), operation("sin", a, null)), da);
            case "exp": return operation("*", this, da);
            case "log": return operation("/", da, a);
            default: throw new IllegalStateException("Unknown scalar operator " + op);
            }
        }

        String latex(List<String> names) {
            if (op.equals("number")) return LinAlgScene.formatNumber(number, false);
            if (op.equals("variable")) return names.get(variable).replace("_", "\\_");
            String left = a.latex(names);
            if (op.matches("sin|cos|exp|log")) {
                return "\\" + op + "\\left(" + left + "\\right)";
            }
            String right = b.latex(names);
            if (op.equals("/")) return "\\frac{" + left + "}{" + right + "}";
            if (op.equals("^")) return "\\left(" + left + "\\right)^{" + right + "}";
            return "\\left(" + left + (op.equals("*") ? " \\cdot " : " " + op + " ") + right + "\\right)";
        }
    }

    private static final class Parser {
        private static final Pattern NUMBER = Pattern.compile("(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][-+]?\\d+)?");
        private static final Pattern NAME = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
        final String source;
        final List<String> parameters;
        int position;

        Parser(String source, List<String> parameters) {
            this.source = source;
            this.parameters = parameters;
        }

        void space() {
            while (position < source.length() && Character.isWhitespace(source.charAt(position))) position++;
        }

        boolean accept(char c) {
            space();
            if (position < source.length() && source.charAt(position) == c) {
                position++;
                return true;
            }
            return false;
        }

        void expect(char c) {
            if (!accept(c)) throw new IllegalArgumentException("Expected '" + c + "' in scalar expression.");
        }

        Node sum() {
            Node result = product();
            while (true) {
                if (accept('+')) result = operation("+", result, product());
                else if (accept('-')) result = operation("-", result, product());
                else return result;
            }
        }

        Node product() {
            Node result = unary();
            while (true) {
                if (accept('*')) result = operation("*", result, unary());
                else if (accept('/')) result = operation("/", result, unary());
                else return result;
            }
        }

        Node unary() {
            if (accept('+')) return unary();
            if (accept('-')) return operation("*", constant(-1), unary());
            Node result = primary();
            if (accept('^')) result = operation("^", result, unary());
            return result;
        }

        Node primary() {
            if (accept('(')) {
                Node result = sum();
                expect(')');
                return result;
            }
            space();
            Matcher number = NUMBER.matcher(source).region(position, source.length());
            if (number.lookingAt()) {
                position = number.end();
                double value = Double.parseDouble(number.group());
                if (!Double.isFinite(value)) throw new IllegalArgumentException("Scalar constants must be finite.");
                return constant(value);
            }
            Matcher name = NAME.matcher(source).region(position, source.length());
            if (name.lookingAt()) {
                position = name.end();
                String identifier = name.group();
                int index = parameters.indexOf(identifier);
                if (index >= 0) return new Node("variable", 0, index, null, null);
                if (identifier.matches("sin|cos|exp|log")) {
                    expect('(');
                    Node argument = sum();
                    expect(')');
                    return operation(identifier, argument, null);
                }
                throw new IllegalArgumentException("Unknown scalar variable or function '" + identifier + "'.");
            }
            throw new IllegalArgumentException("Expected a scalar expression at position " + (position + 1) + ".");
        }
    }
}
