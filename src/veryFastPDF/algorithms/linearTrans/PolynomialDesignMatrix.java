package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Builds and plots the polynomial Vandermonde design matrix for one input feature. */
final class PolynomialDesignMatrix {
    private static final int MAX_DEGREE = 12;

    private PolynomialDesignMatrix() {}

    static Result create(List<Value> arguments) {
        if (arguments.size() != 2) {
            throw new IllegalArgumentException("designMatrix(samples, degree) expects exactly two arguments.");
        }
        Value samples = arguments.get(0);
        if (samples.isScalar() || samples.getRowCount() == 0 || samples.getColumnCount() == 0
                || samples.getRowCount() != 1 && samples.getColumnCount() != 1) {
            throw new IllegalArgumentException("designMatrix samples must be a nonempty row or column vector.");
        }
        int count = Math.max(samples.getRowCount(), samples.getColumnCount());
        double[] x = new double[count];
        for (int i = 0; i < count; i++) {
            x[i] = samples.getRowCount() == 1 ? samples.getRows()[0][i] : samples.getRows()[i][0];
            if (!Double.isFinite(x[i])) {
                throw new IllegalArgumentException("designMatrix samples must have finite entries.");
            }
        }

        Value degreeValue = arguments.get(1);
        if (!degreeValue.isScalar() || !Double.isFinite(degreeValue.getScalar())
                || degreeValue.getScalar() != Math.rint(degreeValue.getScalar())
                || degreeValue.getScalar() < 0 || degreeValue.getScalar() > MAX_DEGREE) {
            throw new IllegalArgumentException("designMatrix degree must be an integer from 0 to "
                    + MAX_DEGREE + ".");
        }
        int degree = (int) degreeValue.getScalar();
        double[][] rows = new double[count][degree + 1];
        for (int i = 0; i < count; i++) {
            rows[i][0] = 1;
            for (int power = 1; power <= degree; power++) {
                rows[i][power] = rows[i][power - 1] * x[i];
                if (!Double.isFinite(rows[i][power])) {
                    throw new IllegalArgumentException("designMatrix overflow at sample " + (i + 1)
                            + " and degree " + power + ".");
                }
            }
        }
        return new Result(new Value(rows, false, degree + 1), new Request(x, rows, degree));
    }

    static final class Result {
        final Value matrix;
        final Request plot;

        Result(Value matrix, Request plot) {
            this.matrix = matrix;
            this.plot = plot;
        }
    }

    static final class Request implements Serializable {
        private static final long serialVersionUID = 1L;
        private final double[] samples;
        private final double[][] rows;
        private final int degree;

        Request(double[] samples, double[][] rows, int degree) {
            this.samples = samples.clone();
            this.rows = new double[rows.length][];
            for (int i = 0; i < rows.length; i++) this.rows[i] = rows[i].clone();
            this.degree = degree;
        }

        String toTikz() {
            int columns = Math.min(2, degree + 1);
            int panelRows = (degree + columns) / columns;
            double minX = java.util.Arrays.stream(samples).min().orElseThrow();
            double maxX = java.util.Arrays.stream(samples).max().orElseThrow();
            StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm]\n")
                    .append("\\useasboundingbox (0,").append(f(-panelRows * 5.2))
                    .append(") rectangle (").append(f(columns * 6.4)).append(",0.6);\n");
            List<Integer> order = new ArrayList<>();
            for (int i = 0; i < samples.length; i++) order.add(i);
            order.sort(Comparator.comparingDouble(i -> samples[i]));

            for (int power = 0; power <= degree; power++) {
                int column = power % columns;
                int row = power / columns;
                double ox = column * 6.4 + 2;
                double oy = -row * 5.2 - 2.2;
                double scale = 0;
                for (double[] values : rows) scale = Math.max(scale, Math.abs(values[power]));
                if (scale == 0) scale = 1;

                sb.append("\\draw[gray!35] (").append(f(ox - 1.7)).append(",").append(f(oy - 1.65))
                        .append(") rectangle (").append(f(ox + 1.7)).append(",").append(f(oy + 1.65)).append(");\n")
                        .append("\\draw[->,gray] (").append(f(ox - 1.7)).append(",").append(f(oy - 1.45))
                        .append(") -- (").append(f(ox + 1.7)).append(",").append(f(oy - 1.45)).append(");\n")
                        .append("\\draw[->,gray] (").append(f(ox - 1.7)).append(",").append(f(oy - 1.45))
                        .append(") -- (").append(f(ox - 1.7)).append(",").append(f(oy + 1.45)).append(");\n")
                        .append("\\node[above,font=\\small] at (").append(f(ox)).append(",").append(f(oy + 1.65))
                        .append(") {$\\phi_{").append(power).append("}(x)=")
                        .append(power == 0 ? "1" : "x^{" + power + "}").append("$};\n")
                        .append("\\node[below,font=\\scriptsize] at (").append(f(ox)).append(",").append(f(oy - 1.65))
                        .append(") {$x_i\\in[").append(f(minX)).append(",").append(f(maxX))
                        .append("],\\quad y/").append(f(scale)).append("$};\n");

                if (samples.length > 1) {
                    sb.append("\\draw[blue,thick] ");
                    for (int n = 0; n < order.size(); n++) {
                        int i = order.get(n);
                        sb.append(n == 0 ? "" : " -- ").append("(")
                                .append(f(oxPosition(samples[i], minX, maxX, ox))).append(",")
                                .append(f(oy + 1.35 * rows[i][power] / scale)).append(")");
                    }
                    sb.append(";\n");
                }
                for (int i = 0; i < samples.length; i++) {
                    sb.append("\\fill[red] (").append(f(oxPosition(samples[i], minX, maxX, ox))).append(",")
                            .append(f(oy + 1.35 * rows[i][power] / scale)).append(") circle (1.5pt);\n");
                }
            }
            return sb.append("\\node[below,text width=12cm,align=center,font=\\small] at (")
                    .append(f(columns * 3.2)).append(",").append(f(-panelRows * 5.2))
                    .append(") {Each red point is a sample; blue joins samples in sorted $x$ order. "
                            + "Vertical values are scaled separately in each panel.};\n")
                    .append("\\end{tikzpicture}").toString();
        }

        private double oxPosition(double x, double min, double max, double origin) {
            return origin + (max == min ? 0 : -1.55 + 3.1 * (x - min) / (max - min));
        }

        private static String f(double value) {
            return LinAlgScene.formatNumber(value, true);
        }
    }

    static boolean hasContent(LinAlgScene scene) {
        return scene.getStatements().stream().anyMatch(statement -> !statement.getDesignMatrixPlots().isEmpty());
    }

    static String toTikz(LinAlgScene scene) {
        StringBuilder result = new StringBuilder();
        for (LinAlgScene.Statement statement : scene.getStatements()) {
            for (Request request : statement.getDesignMatrixPlots()) {
                if (!result.isEmpty()) result.append("\n\n\\medskip\n");
                result.append(request.toTikz());
            }
        }
        return result.toString();
    }
}
