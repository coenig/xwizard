package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Local sampled function views accompanying gradients, using only plain TikZ. */
final class LinAlgFunctionPlot {
    private static final int SAMPLES = 40;

    private LinAlgFunctionPlot() {
    }

    private static List<Value> gradients(LinAlgScene scene) {
        List<Value> result = new ArrayList<>();
        for (var statement : scene.getStatements()) {
            Value value = statement.getValue();
            if (value.getGradientFunction() == null || value.getRowCount() > 2) continue;
            boolean duplicate = result.stream().anyMatch(previous ->
                    previous.getGradientFunction() == value.getGradientFunction()
                    && java.util.Arrays.equals(previous.getOrigin(), value.getOrigin()));
            if (!duplicate) result.add(value);
        }
        return result;
    }

    static boolean hasContent(LinAlgScene scene) {
        return !gradients(scene).isEmpty();
    }

    static String toTikz(LinAlgScene scene) {
        StringBuilder result = new StringBuilder();
        for (Value gradient : gradients(scene)) {
            if (!result.isEmpty()) result.append("\n\n\\medskip\n");
            result.append(plot(gradient));
        }
        return result.toString();
    }

    private static String plot(Value gradient) {
        ScalarFunction function = gradient.getGradientFunction();
        double[] point = gradient.getOrigin();
        boolean curve = point.length == 1;
        double[][] samples = new double[SAMPLES + 1][curve ? 1 : SAMPLES + 1];
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        int omitted = 0;
        for (int i = 0; i <= SAMPLES; i++) {
            for (int j = 0; j < samples[i].length; j++) {
                double[] coordinates = curve ? new double[] {point[0] - 2 + i * 4.0 / SAMPLES}
                        : new double[] {point[0] - 2 + i * 4.0 / SAMPLES, point[1] - 2 + j * 4.0 / SAMPLES};
                try {
                    samples[i][j] = function.evaluateScalar(coordinates);
                    min = Math.min(min, samples[i][j]);
                    max = Math.max(max, samples[i][j]);
                } catch (IllegalArgumentException domainError) {
                    // A local window can extend outside log/division/power domains.
                    samples[i][j] = Double.NaN;
                    omitted++;
                }
            }
        }
        String name = gradient.getGradientFunctionName().replace("_", "\\_");
        StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=2.5cm,y=2.5cm,>=stealth]\n");
        sb.append("\\node[above,text width=10cm,align=center] at (0,2.3) {$")
                .append(name).append(" = ").append(function.toLaTeX()).append("$\\\\ ")
                .append(curve ? "Local function curve" : "Local contour map").append("};\n");
        if (!Double.isFinite(max - min) || !Double.isFinite(min)) {
            return sb.append("\\node at (0,0) {Function range too large to plot locally.};\n\\end{tikzpicture}")
                    .toString();
        }
        double range = max == min ? 1 : max - min;
        sb.append("\\draw[gray] (-2,-2) rectangle (2,2);\n");
        sb.append("\\begin{scope}\\clip (-2,-2) rectangle (2,2);\n");
        if (curve) {
            for (int i = 0; i < SAMPLES; i++) {
                double a = samples[i][0], b = samples[i + 1][0];
                if (Double.isFinite(a) && Double.isFinite(b)) {
                    segment(sb, "blue,thick", -2 + i * 4.0 / SAMPLES, height(a, min, range),
                            -2 + (i + 1) * 4.0 / SAMPLES, height(b, min, range));
                }
            }
            double value = function.evaluateScalar(point);
            double slope = gradient.getRows()[0][0];
            double tangentScale = Math.max(range, Math.abs(slope));
            double dx = range / tangentScale;
            double dy = 3.6 * (slope / tangentScale);
            segment(sb, "red,dashed", -dx, height(value, min, range) - dy,
                    dx, height(value, min, range) + dy);
            sb.append("\\fill[red] (0,").append(f(height(value, min, range))).append(") circle (2pt);\n");
        } else if (max != min) {
            for (int level = 1; level < 8; level++) {
                double target = min + range * level / 8;
                String color = "blue!" + (15 + level * 10) + "!red";
                for (int i = 0; i < SAMPLES; i++) {
                    for (int j = 0; j < SAMPLES; j++) {
                        double x = -2 + i * 4.0 / SAMPLES, y = -2 + j * 4.0 / SAMPLES;
                        double step = 4.0 / SAMPLES;
                        double[] a = {x, y, samples[i][j]};
                        double[] b = {x + step, y, samples[i + 1][j]};
                        double[] c = {x + step, y + step, samples[i + 1][j + 1]};
                        double[] d = {x, y + step, samples[i][j + 1]};
                        contour(sb, color, target, a, b, c);
                        contour(sb, color, target, a, c, d);
                    }
                }
            }
        }
        sb.append("\\end{scope}\n");
        // The contour legend lies outside the clipped plotting area.
        if (!curve && max != min) {
            for (int level = 1; level < 8; level++) {
                sb.append("\\node[blue!").append(15 + level * 10)
                        .append("!red,right,font=\\scriptsize] at (2.1,").append(f(2 - level * 0.5))
                        .append(") {$").append(f(min + range * level / 8)).append("$};\n");
            }
        }
        if (!curve) {
            double gx = gradient.getRows()[0][0], gy = gradient.getRows()[1][0];
            double scale = Math.max(Math.abs(gx), Math.abs(gy));
            if (scale > 0) {
                double length = Math.hypot(gx / scale, gy / scale);
                sb.append("\\draw[->,very thick,black] (0,0) -- (")
                        .append(f(gx / scale / length)).append(",").append(f(gy / scale / length))
                        .append(") node[above right] {$\\nabla ").append(name).append("$};\n");
            }
            sb.append("\\fill[black] (0,0) circle (2pt) node[below right] {$p$};\n");
        }
        String x = function.getParameters().get(0).replace("_", "\\_");
        String y = curve ? name : function.getParameters().get(1).replace("_", "\\_");
        sb.append("\\node[below] at (0,-2.1) {$").append(x).append("\\in[")
                .append(f(point[0] - 2)).append(",").append(f(point[0] + 2)).append("]$};\n");
        sb.append("\\node[rotate=90,above] at (-2.1,0) {$").append(y).append("\\in[")
                .append(f(curve ? min : point[1] - 2)).append(",")
                .append(f(curve ? min + range : point[1] + 2)).append("]$};\n");
        sb.append("\\node[below,text width=11cm,align=center,font=\\small] at (0,-2.5) {")
                .append(curve ? "Red point: $(p,f(p))$. Dashed tangent: slope $f'(p)="
                        + f(gradient.getRows()[0][0]) + "$."
                        : "Point: $p$. Arrow: unit gradient direction (length scaled). "
                        + "Zero gradients have no arrow. Colors label function levels; "
                        + "constant functions have no contour lines.")
                .append(" Sampled local approximation; discontinuities may not be resolved.");
        if (omitted > 0) sb.append(" Undefined/non-finite samples omitted: ").append(omitted).append(".");
        return sb.append("};\n\\end{tikzpicture}").toString();
    }

    private static double height(double value, double min, double range) {
        return -1.8 + 3.6 * ((value - min) / range);
    }

    private static void contour(StringBuilder sb, String color, double level, double[]... vertices) {
        List<double[]> crossings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            double[] a = vertices[i], b = vertices[(i + 1) % 3];
            if (!Double.isFinite(a[2]) || !Double.isFinite(b[2])) return;
            if ((a[2] < level) != (b[2] < level)) {
                double t = (level - a[2]) / (b[2] - a[2]);
                crossings.add(new double[] {a[0] + t * (b[0] - a[0]), a[1] + t * (b[1] - a[1])});
            }
        }
        if (crossings.size() == 2) {
            segment(sb, color, crossings.get(0)[0], crossings.get(0)[1], crossings.get(1)[0], crossings.get(1)[1]);
        }
    }

    private static void segment(StringBuilder sb, String style, double x, double y, double u, double v) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(u) || !Double.isFinite(v)) return;
        sb.append("\\draw[").append(style).append("] (").append(f(x)).append(",").append(f(y))
                .append(") -- (").append(f(u)).append(",").append(f(v)).append(");\n");
    }

    private static String f(double value) {
        return LinAlgScene.formatNumber(value, true);
    }
}
