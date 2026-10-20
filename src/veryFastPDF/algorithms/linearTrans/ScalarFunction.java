package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Scalar or vector-valued expression trees with symbolic partial differentiation. */
public final class ScalarFunction implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<String> parameters;
    private final Node expression;
    private final List<Node> components;

    public ScalarFunction(List<String> parameters, String source) {
        if (parameters.isEmpty() || parameters.stream().distinct().count() != parameters.size()
                || parameters.stream().anyMatch(p -> !LinAlgScene.isName(p))) {
            throw new IllegalArgumentException("Function parameters must be distinct variable names.");
        }
        this.parameters = List.copyOf(parameters);
        Parser parser = new Parser(source, parameters);
        List<Node> outputs = new ArrayList<>();
        if (parser.accept('(')) {
            Node first = parser.sum();
            if (parser.accept(',')) {
                outputs.add(first);
                do {
                    outputs.add(parser.sum());
                } while (parser.accept(','));
                parser.expect(')');
            } else {
                parser.position = 0;
            }
        }
        if (outputs.isEmpty()) {
            outputs.add(parser.sum());
        }
        components = List.copyOf(outputs);
        expression = components.get(0);
        parser.space();
        if (parser.position != source.length()) {
            throw new IllegalArgumentException("Unexpected scalar expression at position " + (parser.position + 1) + ".");
        }
    }

    public int dimension() {
        return parameters.size();
    }

    public List<String> getParameters() {
        return parameters;
    }

    public double evaluateScalar(double[] point) {
        if (components.size() != 1 || point.length != dimension()) {
            throw new IllegalArgumentException("Scalar evaluation requires a scalar function and matching coordinates.");
        }
        for (double coordinate : point) {
            if (!Double.isFinite(coordinate)) {
                throw new IllegalArgumentException("Evaluation point must have finite components.");
            }
        }
        return expression.evaluate(point);
    }

    public String toLaTeX() {
        if (components.size() == 1) {
            return expression.latex(parameters);
        }
        return matrixLaTeX(components.stream().map(n -> new Node[] {n}).toArray(Node[][]::new));
    }

    public String gradientLaTeX() {
        return derivativeLaTeX("grad");
    }

    public double[] gradient(double[] point) {
        double[][] matrix = differentiate("grad", point);
        double[] result = new double[matrix.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = matrix[i][0];
        }
        return result;
    }

    public double[][] differentiate(String operator, double[] point) {
        if (point.length != dimension()) {
            throw new IllegalArgumentException("Evaluation point has " + point.length + " components; expected "
                    + dimension() + ".");
        }
        for (double component : point) {
            if (!Double.isFinite(component)) {
                throw new IllegalArgumentException("Evaluation point must have finite components.");
            }
        }
        Node[][] derivatives = derivativeNodes(operator);
        for (Node component : components) {
            component.evaluate(point);
        }
        double[][] result = new double[derivatives.length][derivatives[0].length];
        for (int r = 0; r < result.length; r++) {
            for (int c = 0; c < result[r].length; c++) {
                result[r][c] = derivatives[r][c].evaluate(point);
            }
        }
        return result;
    }

    public String derivativeLaTeX(String operator) {
        Node[][] nodes = derivativeNodes(operator);
        return operator.equals("laplacian") || operator.equals("divergence")
                ? nodes[0][0].latex(parameters) : matrixLaTeX(nodes);
    }

    public List<String> differentiationSteps(String operator, double[] point) {
        List<String> steps = new ArrayList<>();
        Node[][] nodes = derivativeNodes(operator, steps);
        List<String> coordinates = new ArrayList<>();
        for (double value : point) coordinates.add("\\left(" + LinAlgScene.formatNumber(value, false) + "\\right)");
        for (int r = 0; r < nodes.length; r++) {
            for (int c = 0; c < nodes[r].length; c++) {
                steps.add("\\text{Substitute the point, entry }(" + (r + 1) + "," + (c + 1) + "):\\quad "
                        + nodes[r][c].latex(coordinates) + "="
                        + LinAlgScene.formatNumber(nodes[r][c].evaluate(point), true));
            }
        }
        return new ArrayList<>(new java.util.LinkedHashSet<>(steps));
    }

    private String matrixLaTeX(Node[][] nodes) {
        StringBuilder result = new StringBuilder("\\begin{pmatrix}");
        for (int r = 0; r < nodes.length; r++) {
            result.append(r == 0 ? "" : "\\\\");
            for (int c = 0; c < nodes[r].length; c++) {
                result.append(c == 0 ? "" : " & ").append(nodes[r][c].latex(parameters));
            }
        }
        return result.append("\\end{pmatrix}").toString();
    }

    private Node[][] derivativeNodes(String operator) {
        return derivativeNodes(operator, null);
    }

    private Node[][] derivativeNodes(String operator, List<String> steps) {
        if (operator.matches("grad|hessian|laplacian") && components.size() != 1) {
            throw new IllegalArgumentException(operator + "(...) requires a scalar-valued function.");
        }
        if (operator.equals("divergence") && components.size() != dimension()) {
            throw new IllegalArgumentException("divergence(...) requires a vector field with one component per variable.");
        }
        if (operator.equals("curl") && (dimension() != 3 || components.size() != 3)) {
            throw new IllegalArgumentException("curl(...) requires a three-component vector field in three variables.");
        }
        switch (operator) {
        case "grad":
            Node[][] gradient = new Node[dimension()][1];
            for (int i = 0; i < dimension(); i++) {
                gradient[i][0] = expression.derivative(i, steps, parameters);
                if (steps != null) steps.add("\\text{Gradient entry }" + (i + 1) + ":\\quad "
                        + gradient[i][0].latex(parameters));
            }
            return gradient;
        case "jacobian":
            Node[][] jacobian = new Node[components.size()][dimension()];
            if (steps != null) {
                for (int c = 0; c < dimension(); c++) {
                    steps.add("\\text{Input / column }" + (c + 1) + ":\\quad u_{" + (c + 1)
                            + "}=" + parameters.get(c).replace("_", "\\_"));
                }
                for (int r = 0; r < components.size(); r++) {
                    steps.add("\\text{Output / row }" + (r + 1) + ":\\quad F_{" + (r + 1)
                            + "}=" + components.get(r).latex(parameters));
                }
            }
            for (int r = 0; r < jacobian.length; r++) {
                for (int c = 0; c < dimension(); c++) {
                    jacobian[r][c] = components.get(r).derivative(c, steps, parameters);
                    if (steps != null) steps.add("\\text{Jacobian entry }(" + (r + 1) + "," + (c + 1)
                            + "):\\quad J_{" + (r + 1) + "," + (c + 1)
                            + "}=\\frac{\\partial F_{" + (r + 1) + "}}{\\partial "
                            + parameters.get(c).replace("_", "\\_") + "}="
                            + jacobian[r][c].latex(parameters));
                }
            }
            return jacobian;
        case "hessian":
            Node[][] hessian = new Node[dimension()][dimension()];
            for (int r = 0; r < dimension(); r++) {
                Node first = expression.derivative(r, steps, parameters);
                if (steps != null) steps.add("\\text{Hessian first partial }" + (r + 1) + ":\\quad "
                        + first.latex(parameters));
                for (int c = 0; c < dimension(); c++) {
                    hessian[r][c] = first.derivative(c, steps, parameters);
                    if (steps != null) steps.add("\\text{Hessian second partial }(" + (r + 1) + "," + (c + 1)
                            + "):\\quad " + hessian[r][c].latex(parameters));
                }
            }
            return hessian;
        case "laplacian":
        case "divergence":
            Node sum = constant(0);
            if (steps != null) steps.add("\\text{Initialize running total:}\\quad S_0=0");
            for (int i = 0; i < dimension(); i++) {
                Node term = operator.equals("laplacian")
                        ? expression.derivative(i, steps, parameters).derivative(i, steps, parameters)
                        : components.get(i).derivative(i, steps, parameters);
                Node previous = sum;
                sum = operation("+", previous, term);
                if (steps != null) {
                    String variable = parameters.get(i).replace("_", "\\_");
                    String derivative = operator.equals("laplacian")
                           ? "\\frac{\\partial^2 f}{\\partial " + variable + "^2}"
                           : "\\frac{\\partial F_{" + (i + 1) + "}}{\\partial " + variable + "}";
                    steps.add("\\text{Accumulate term }" + (i + 1) + ":\\quad S_{"
                           + (i + 1) + "}=S_{" + i + "}+" + derivative
                           + "=\\left(" + previous.latex(parameters) + "\\right)+\\left("
                           + term.latex(parameters) + "\\right)=" + sum.latex(parameters));
                }
            }
            return new Node[][] {{sum}};
        case "curl":
            Node[][] curl = new Node[3][1];
            for (int i = 0; i < 3; i++) {
                int j = (i + 1) % 3;
                int k = (i + 2) % 3;
                Node left = components.get(k).derivative(j, steps, parameters);
                Node right = components.get(j).derivative(k, steps, parameters);
                curl[i][0] = operation("-", left, right);
                if (steps != null) steps.add("\\text{Curl component }" + (i + 1) + ":\\quad "
                        + left.latex(parameters) + "-\\left(" + right.latex(parameters) + "\\right)="
                        + curl[i][0].latex(parameters));
            }
            return curl;
        default:
            throw new IllegalArgumentException("Unknown differential operator '" + operator + "'.");
        }
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
            return derivative(index, null, null);
        }

        Node derivative(int index, List<String> steps, List<String> names) {
            Node da = a == null ? constant(0) : a.derivative(index, steps, names);
            Node db = b == null ? constant(0) : b.derivative(index, steps, names);
            Node result = switch (op) {
            case "number" -> constant(0);
            case "variable" -> constant(variable == index ? 1 : 0);
            case "+", "-" -> operation(op, da, db);
            case "*" -> operation("+", operation("*", da, b), operation("*", a, db));
            case "/" -> operation("/", operation("-", operation("*", da, b), operation("*", a, db)),
                    operation("^", b, constant(2)));
            case "^" -> {
                if (zero(db)) {
                    if (zero(da) || zero(b)) yield constant(0);
                    yield operation("*", operation("*", b, operation("^", a, operation("-", b, constant(1)))), da);
                }
                yield operation("*", this, operation("+", operation("*", db, operation("log", a, null)),
                        operation("/", operation("*", b, da), a)));
            }
            case "sin" -> operation("*", operation("cos", a, null), da);
            case "cos" -> operation("*", operation("*", constant(-1), operation("sin", a, null)), da);
            case "exp" -> operation("*", this, da);
            case "log" -> operation("/", da, a);
            default -> throw new IllegalStateException("Unknown scalar operator " + op);
            };
            if (steps != null) {
                String rule = switch (op) {
                case "number" -> "constant";
                case "variable" -> "coordinate";
                case "+", "-" -> "sum/difference";
                case "*" -> "product";
                case "/" -> "quotient";
                case "^" -> "power and chain";
                default -> op + " and chain";
                };
                steps.add("\\underbrace{\\frac{\\partial}{\\partial " + names.get(index).replace("_", "\\_")
                        + "}\\left[" + latex(names) + "\\right]}_{\\text{" + rule + " rule}}="
                        + result.latex(names));
            }
            return result;
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
