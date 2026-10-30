package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/**
 * Builds the polynomial Vandermonde design matrix for one input feature. With optional responses
 * {@code y}, it also fits the least-squares polynomial and plots it against the data.
 */
final class PolynomialDesignMatrix {
    private static final int MAX_DEGREE = 12;
    private static final int CURVE_POINTS = 120;

    private PolynomialDesignMatrix() {}

    static Result create(List<Value> arguments) {
        if (arguments.size() != 2 && arguments.size() != 3) {
            throw new IllegalArgumentException(
                    "designMatrix(samples, degree) or designMatrix(samples, degree, y) expects two or three arguments.");
        }
        double[] x = vector(arguments.get(0), "samples");
        int count = x.length;

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
        double[] y = null;
        double[] coefficients = null;
        if (arguments.size() == 3) {
            y = vector(arguments.get(2), "responses y");
            if (y.length != count) {
                throw new IllegalArgumentException("designMatrix responses y need one value per sample ("
                        + count + "), not " + y.length + ".");
            }
            coefficients = leastSquares(rows, y);
        }
        return new Result(new Value(rows, false, degree + 1), new Request(x, rows, degree, y, coefficients));
    }

    private static double[] vector(Value value, String role) {
        if (value.isScalar() || value.getRowCount() == 0 || value.getColumnCount() == 0
                || value.getRowCount() != 1 && value.getColumnCount() != 1) {
            throw new IllegalArgumentException("designMatrix " + role + " must be a nonempty row or column vector.");
        }
        int count = Math.max(value.getRowCount(), value.getColumnCount());
        double[] entries = new double[count];
        for (int i = 0; i < count; i++) {
            entries[i] = value.getRowCount() == 1 ? value.getRows()[0][i] : value.getRows()[i][0];
            if (!Double.isFinite(entries[i])) {
                throw new IllegalArgumentException("designMatrix " + role + " must have finite entries.");
            }
        }
        return entries;
    }

    /** Minimizes ||Phi beta - y||^2 with Householder QR, which avoids squaring the condition number. */
    private static double[] leastSquares(double[][] phi, double[] y) {
        int m = phi.length;
        int n = phi[0].length;
        String underdetermined = "designMatrix(x, degree, y) needs at least degree+1 = " + n
                + " distinct samples to fit y; with too few (or nearly equal) samples the fit is not unique. "
                + "Lower the degree, add samples, or center/scale x.";
        if (m < n) throw new IllegalArgumentException(underdetermined);
        double[][] a = new double[m][];
        for (int i = 0; i < m; i++) a[i] = phi[i].clone();
        double[] b = y.clone();
        for (int k = 0; k < n; k++) {
            double original = 0;
            double norm = 0;
            for (int i = 0; i < m; i++) original = Math.hypot(original, phi[i][k]);
            for (int i = k; i < m; i++) norm = Math.hypot(norm, a[i][k]);
            if (!(norm > 1e-12 * original)) throw new IllegalArgumentException(underdetermined);
            double alpha = a[k][k] > 0 ? -norm : norm;
            double[] v = new double[m - k];
            for (int i = k; i < m; i++) v[i - k] = a[i][k];
            v[0] -= alpha;
            double vv = 0;
            for (double entry : v) vv += entry * entry;
            for (int j = k; j < n; j++) {
                double dot = 0;
                for (int i = k; i < m; i++) dot += v[i - k] * a[i][j];
                double factor = 2 * dot / vv;
                for (int i = k; i < m; i++) a[i][j] -= factor * v[i - k];
            }
            double dot = 0;
            for (int i = k; i < m; i++) dot += v[i - k] * b[i];
            double factor = 2 * dot / vv;
            for (int i = k; i < m; i++) b[i] -= factor * v[i - k];
        }
        double[] beta = new double[n];
        for (int k = n - 1; k >= 0; k--) {
            double sum = b[k];
            for (int j = k + 1; j < n; j++) sum -= a[k][j] * beta[j];
            beta[k] = sum / a[k][k];
            if (!Double.isFinite(beta[k])) throw new IllegalArgumentException(underdetermined);
        }
        return beta;
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
        private final double[] targets;
        private final double[] coefficients;

        Request(double[] samples, double[][] rows, int degree, double[] targets, double[] coefficients) {
            this.samples = samples.clone();
            this.rows = new double[rows.length][];
            for (int i = 0; i < rows.length; i++) this.rows[i] = rows[i].clone();
            this.degree = degree;
            this.targets = targets == null ? null : targets.clone();
            this.coefficients = coefficients == null ? null : coefficients.clone();
        }

        boolean hasFit() {
            return targets != null && coefficients != null;
        }

        double[] coefficients() {
            return coefficients == null ? null : coefficients.clone();
        }

        double[] targets() {
            return targets == null ? null : targets.clone();
        }

        double predict(double x) {
            double value = 0;
            for (int power = degree; power >= 0; power--) value = value * x + coefficients[power];
            return value;
        }

        double sumOfSquaredErrors() {
            double sse = 0;
            for (int i = 0; i < samples.length; i++) {
                double residual = targets[i] - predict(samples[i]);
                sse += residual * residual;
            }
            return sse;
        }

        /** The fitted polynomial, e.g. {@code 0.1 - 0.03x + 1.0071x^{2}}, for LaTeX math mode. */
        String polynomialLatex() {
            double maxAbsX = 0;
            double maxAbsY = 0;
            for (double x : samples) maxAbsX = Math.max(maxAbsX, Math.abs(x));
            for (double y : targets) maxAbsY = Math.max(maxAbsY, Math.abs(y));
            StringBuilder sb = new StringBuilder();
            for (int power = 0; power <= degree; power++) {
                // Hide round-off terms whose largest contribution on the samples is negligible.
                if (Math.abs(coefficients[power]) * Math.pow(maxAbsX, power) <= 1e-9 * Math.max(1, maxAbsY)) {
                    continue;
                }
                String magnitude = coefficientLatex(Math.abs(coefficients[power]));
                boolean negative = coefficients[power] < 0;
                if (sb.length() == 0) {
                    if (negative) sb.append("-");
                } else {
                    sb.append(negative ? " - " : " + ");
                }
                if (power == 0 || !magnitude.equals("1")) sb.append(magnitude);
                if (power >= 1) sb.append("x");
                if (power >= 2) sb.append("^{").append(power).append("}");
            }
            return sb.length() == 0 ? "0" : sb.toString();
        }

        /** Four decimals for ordinary sizes; four significant digits for tiny or huge high-degree coefficients. */
        static String coefficientLatex(double value) {
            double magnitude = Math.abs(value);
            if (magnitude == 0 || magnitude >= 1e-3 && magnitude < 1e7) return f(value);
            String[] parts = String.format(java.util.Locale.ROOT, "%.3e", value).split("e");
            String mantissa = parts[0].contains(".") ? parts[0].replaceAll("0+$", "").replaceAll("\\.$", "") : parts[0];
            return mantissa + "\\cdot 10^{" + Integer.parseInt(parts[1]) + "}";
        }

        String toTikz() {
            return hasFit() ? fitTikz() : featureTikz();
        }

        private String fitTikz() {
            final double width = 10;
            final double height = 6;
            double minX = java.util.Arrays.stream(samples).min().orElseThrow();
            double maxX = java.util.Arrays.stream(samples).max().orElseThrow();
            double lo = minX == maxX ? minX - 1 : minX;
            double hi = minX == maxX ? maxX + 1 : maxX;
            double[] curveX = new double[CURVE_POINTS];
            double[] curveY = new double[CURVE_POINTS];
            for (int k = 0; k < CURVE_POINTS; k++) {
                curveX[k] = lo + (hi - lo) * k / (CURVE_POINTS - 1);
                curveY[k] = predict(curveX[k]);
            }
            double minY = java.util.Arrays.stream(targets).min().orElseThrow();
            double maxY = java.util.Arrays.stream(targets).max().orElseThrow();
            double span = maxY > minY ? maxY - minY : Math.max(1, Math.abs(maxY));
            // Let the curve widen the view, but not so far that wild oscillations squash the data.
            double yLo = Math.max(Math.min(minY, java.util.Arrays.stream(curveY).min().orElseThrow()), minY - span);
            double yHi = Math.min(Math.max(maxY, java.util.Arrays.stream(curveY).max().orElseThrow()), maxY + span);
            if (yHi <= yLo) {
                yLo -= 1;
                yHi += 1;
            }
            double padY = 0.08 * (yHi - yLo);
            double padX = 0.05 * (hi - lo);
            double[] view = {lo - padX, hi + padX, yLo - padY, yHi + padY, width, height};

            StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm]\n")
                    .append("\\node[above,font=\\small] at (").append(f(width / 2)).append(",").append(f(height))
                    .append(") {Least-squares polynomial of degree ").append(degree).append("};\n")
                    .append("\\draw[gray!35] (0,0) rectangle (").append(f(width)).append(",").append(f(height))
                    .append(");\n");
            if (view[2] < 0 && 0 < view[3]) {
                sb.append("\\draw[gray!50] (0,").append(f(mapY(0, view))).append(") -- (").append(f(width))
                        .append(",").append(f(mapY(0, view))).append(");\n");
            }
            if (view[0] < 0 && 0 < view[1]) {
                sb.append("\\draw[gray!50] (").append(f(mapX(0, view))).append(",0) -- (")
                        .append(f(mapX(0, view))).append(",").append(f(height)).append(");\n");
            }
            for (double tick : minX == maxX ? new double[] {minX} : new double[] {minX, maxX}) {
                sb.append("\\draw[gray] (").append(f(mapX(tick, view))).append(",0) -- ++(0,-0.1) node[below,font=\\scriptsize] {$")
                        .append(f(tick)).append("$};\n");
            }
            for (double tick : minY == maxY ? new double[] {minY} : new double[] {minY, maxY}) {
                sb.append("\\draw[gray] (0,").append(f(mapY(tick, view))).append(") -- ++(-0.1,0) node[left,font=\\scriptsize] {$")
                        .append(f(tick)).append("$};\n");
            }
            sb.append("\\node[below right,font=\\small] at (").append(f(width)).append(",0) {$x$};\n")
                    .append("\\node[left,font=\\small] at (0,").append(f(height)).append(") {$y$};\n")
                    .append("\\begin{scope}\n\\clip (0,0) rectangle (").append(f(width)).append(",")
                    .append(f(height)).append(");\n\\draw[blue,thick] ");
            for (int k = 0; k < CURVE_POINTS; k++) {
                sb.append(k == 0 ? "" : " -- ").append("(").append(f(mapX(curveX[k], view))).append(",")
                        .append(f(mapY(curveY[k], view))).append(")");
            }
            sb.append(";\n");
            for (int i = 0; i < samples.length; i++) {
                sb.append("\\draw[gray,dashed] (").append(f(mapX(samples[i], view))).append(",")
                        .append(f(mapY(targets[i], view))).append(") -- (").append(f(mapX(samples[i], view)))
                        .append(",").append(f(mapY(predict(samples[i]), view))).append(");\n");
            }
            sb.append("\\end{scope}\n");
            for (int i = 0; i < samples.length; i++) {
                sb.append("\\fill[red] (").append(f(mapX(samples[i], view))).append(",")
                        .append(f(mapY(targets[i], view))).append(") circle (2pt);\n");
            }
            return sb.append("\\node[below,text width=11cm,align=flush center,font=\\small] (fit) at (")
                    .append(f(width / 2)).append(",-0.6) {$\\hat y(x)=")
                    .append(polynomialLatex()).append("$};\n")
                    .append("\\node[below,text width=11cm,align=flush center,font=\\small] at (fit.south) ")
                    .append("{Red: data $(x_i,y_i)$; blue: fitted polynomial minimizing "
                            + "$\\sum_i(y_i-\\hat y(x_i))^2$; dashed: residuals. SSE $=")
                    .append(f(sumOfSquaredErrors())).append("$};\n")
                    .append("\\end{tikzpicture}").toString();
        }

        private static double mapX(double x, double[] view) {
            return view[4] * (x - view[0]) / (view[1] - view[0]);
        }

        private static double mapY(double y, double[] view) {
            return view[5] * (y - view[2]) / (view[3] - view[2]);
        }

        private String featureTikz() {
            int columns = Math.min(2, degree + 1);
            int panelRows = (degree + columns) / columns;
            double minX = java.util.Arrays.stream(samples).min().orElseThrow();
            double maxX = java.util.Arrays.stream(samples).max().orElseThrow();
            StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm]\n")
                    .append("\\useasboundingbox (0,").append(f(-panelRows * 5.2 - 1.3))
                    .append(") rectangle (").append(f(columns * 6.4)).append(",0.6);\n");

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

                if (maxX > minX) {
                    sb.append("\\draw[blue,thick] ");
                    for (int k = 0; k < CURVE_POINTS; k++) {
                        double t = minX + (maxX - minX) * k / (CURVE_POINTS - 1);
                        sb.append(k == 0 ? "" : " -- ").append("(")
                                .append(f(oxPosition(t, minX, maxX, ox))).append(",")
                                .append(f(oy + 1.35 * Math.pow(t, power) / scale)).append(")");
                    }
                    sb.append(";\n");
                }
                for (int i = 0; i < samples.length; i++) {
                    sb.append("\\fill[red] (").append(f(oxPosition(samples[i], minX, maxX, ox))).append(",")
                            .append(f(oy + 1.35 * rows[i][power] / scale)).append(") circle (1.5pt);\n");
                }
            }
            return sb.append("\\node[below,text width=12cm,align=flush center,font=\\small] at (")
                    .append(f(columns * 3.2)).append(",").append(f(-panelRows * 5.2))
                    .append(") {Red points are the samples $(x_i,x_i^k)$; blue is the feature curve $\\phi_k(x)=x^k$. "
                            + "Vertical values are scaled separately in each panel. "
                            + "Pass responses as DesignMatrix(x, degree, y) to plot the fitted polynomial.};\n")
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
