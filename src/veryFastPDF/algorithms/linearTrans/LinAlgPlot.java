package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Statement;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/**
 * Renders the 2D content of a {@link LinAlgScene} as a TikZ picture (plain TikZ,
 * no further packages): every 2D vector is drawn as an arrow from the origin, and
 * every 2x2 matrix as the image of the unit square under the linear map.
 */
public final class LinAlgPlot {

    private static final String[] COLORS = {"blue", "red", "green!60!black", "orange", "violet", "teal", "brown"};
    private static final double SIZE_CM = 6.0;

    /** Direction towards the viewer in the oblique projection used for 3D; larger means nearer. */
    private static final double[] VIEW_DEPTH = {1, 0.45, 0.35};

    private LinAlgPlot() {
    }

    /** @return true iff the scene contains something this plot can show. */
    public static boolean hasPlottableContent(LinAlgScene scene) {
        if (PolynomialDesignMatrix.hasContent(scene) || LinAlgFunctionPlot.hasContent(scene) || StatisticsPlot.hasContent(scene)
                || DistributionPlot.hasContent(scene) || NeuralNetworkPlot.hasContent(scene)) {
            return true;
        }
        for (Statement s : scene.getStatements()) {
            Value v = s.getValue();
            if (!s.isDirective() && (isVector2D(v) || isMatrix2D(v) || isVector3D(v) || isMatrix3D(v))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isVector2D(Value v) {
        return !v.isScalar() && v.getRowCount() == 2 && v.getColumnCount() == 1;
    }

    private static boolean isMatrix2D(Value v) {
        return !v.isScalar() && v.getRowCount() == 2 && v.getColumnCount() == 2;
    }

    private static boolean isVector3D(Value v) {
        return !v.isScalar() && v.getRowCount() == 3 && v.getColumnCount() == 1;
    }

    private static boolean isMatrix3D(Value v) {
        return !v.isScalar() && v.getRowCount() == 3 && v.getColumnCount() == 3;
    }

    /** The LaTeX code of the 2D and/or 3D pictures; requires the tikz package. */
    public static String toTikz(LinAlgScene scene) {
        String two = toTikz2D(scene);
        String three = toTikz3D(scene);
        String functions = LinAlgFunctionPlot.toTikz(scene);
        String statistics = StatisticsPlot.toTikz(scene);
        String distributions = DistributionPlot.toTikz(scene);
        String neural = NeuralNetworkPlot.toTikz(scene);
        String polynomialDesign = PolynomialDesignMatrix.toTikz(scene);
        return java.util.stream.Stream.of(two, three, functions, statistics, distributions, neural, polynomialDesign).filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.joining("\n\n\\medskip\n"));
    }

    private static String toTikz3D(LinAlgScene scene) {
        List<Statement> vectors = new ArrayList<>();
        List<Statement> matrices = new ArrayList<>();
        double max = 1;
        for (Statement s : scene.getStatements()) {
            if (s.isDirective()) {
                continue;
            }
            double[][] m = s.getValue().getRows();
            if (isVector3D(s.getValue())) {
                vectors.add(s);
                for (int i = 0; i < 3; i++) {
                    double start = s.getValue().getOrigin()[i];
                    max = Math.max(max, Math.max(Math.abs(start), Math.abs(start + m[i][0])));
                }
            } else if (isMatrix3D(s.getValue())) {
                matrices.add(s);
                for (int i = 0; i < 3; i++) {
                    double sum = 0;
                    for (int j = 0; j < 3; j++) {
                        max = Math.max(max, Math.abs(m[i][j]));
                        sum += Math.abs(m[i][j]);
                    }
                    max = Math.max(max, sum);
                }
            }
        }
        if (vectors.isEmpty() && matrices.isEmpty()) {
            return "";
        }

        int range = (int) Math.ceil(max - 1e-9);
        double unit = SIZE_CM / range;
        StringBuilder sb = new StringBuilder();
        // Oblique projection: x points down-left, y right, z up.
        sb.append("\\begin{tikzpicture}[x={(").append(f(-0.45 * unit)).append("cm,").append(f(-0.35 * unit))
                .append("cm)},y={(").append(f(unit)).append("cm,0cm)},z={(0cm,").append(f(unit))
                .append("cm)},>=stealth]\n");
        // Canvas coordinates (with units), since plain coordinates would be projected.
        double halfW = unit * 1.45 * range + 1.8;
        double halfH = unit * 1.35 * range + 0.8;
        sb.append("\\useasboundingbox (").append(f(-halfW)).append("cm,").append(f(-halfH)).append("cm) rectangle (")
                .append(f(halfW)).append("cm,").append(f(halfH)).append("cm);\n");
        String[] axis = {"x", "y", "z"};
        for (int k = 0; k < 3; k++) {
            String neg = k == 0 ? "-" + range + ",0,0" : k == 1 ? "0,-" + range + ",0" : "0,0,-" + range;
            String pos = k == 0 ? range + ",0,0" : k == 1 ? "0," + range + ",0" : "0,0," + range;
            sb.append("\\draw[->,thick,gray] (").append(neg).append(") -- (").append(pos)
                    .append(") node[above,black,font=\\small] {$").append(axis[k]).append("$};\n");
        }

        int color = 0;
        List<Object[]> faces = new ArrayList<>();
        StringBuilder labels = new StringBuilder();
        for (Statement s : matrices) {
            double[][] m = s.getValue().getRows();
            String c = COLORS[color++ % COLORS.length];
            // Corners of the image of the unit cube, indexed by bits (a,b,c) of the cube corner.
            double[][] p = new double[8][3];
            for (int corner = 0; corner < 8; corner++) {
                for (int i = 0; i < 3; i++) {
                    p[corner][i] = m[i][0] * (corner & 1) + m[i][1] * ((corner >> 1) & 1) + m[i][2] * ((corner >> 2) & 1);
                }
            }
            // Each of the 6 faces: fixed bit `bit` of the corner index is 0 or 1.
            for (int bit = 0; bit < 3; bit++) {
                int u = 1 << ((bit + 1) % 3);
                int w = 1 << ((bit + 2) % 3);
                for (int fixed = 0; fixed < 2; fixed++) {
                    int base = fixed << bit;
                    int[] idx = {base, base | u, base | u | w, base | w};
                    double depth = 0;
                    StringBuilder path = new StringBuilder();
                    for (int k = 0; k < 4; k++) {
                        double[] q = p[idx[k]];
                        depth += (q[0] * VIEW_DEPTH[0] + q[1] * VIEW_DEPTH[1] + q[2] * VIEW_DEPTH[2]) / 4;
                        path.append(k > 0 ? " -- " : "").append("(").append(pt(q)).append(")");
                    }
                    int shade = 28 + 14 * bit + 8 * fixed;
                    faces.add(new Object[] {depth, "\\filldraw[fill=" + c + "!" + shade + "!white,fill opacity=0.6,draw="
                            + c + "!80!black,thick,line join=round] " + path + " -- cycle;\n"});
                }
            }
            labels.append("\\node[").append(c).append(",font=\\small] at (").append(pt(p[7])).append(") {$")
                    .append(s.getName()).append("([0,1]^3)$};\n");
        }
        // Painter's algorithm: farthest faces first, so nearer faces hide the edges behind them.
        faces.sort((f1, f2) -> Double.compare((Double) f1[0], (Double) f2[0]));
        for (Object[] face : faces) {
            sb.append((String) face[1]);
        }
        sb.append(labels);
        for (Statement s : vectors) {
            double[][] v = s.getValue().getRows();
            double[] start = s.getValue().getOrigin();
            String c = COLORS[color++ % COLORS.length];
            sb.append("\\draw[->,very thick,").append(c).append("] (").append(pt(start)).append(") -- (")
                    .append(pt(new double[] {start[0] + v[0][0], start[1] + v[1][0], start[2] + v[2][0]}))
                    .append(") node[above right,font=\\small] {$").append(s.getName()).append("$};\n");
        }
        return sb.append("\\end{tikzpicture}").toString();
    }

    private static String pt(double[] p) {
        return f(p[0]) + "," + f(p[1]) + "," + f(p[2]);
    }

    private static String toTikz2D(LinAlgScene scene) {
        List<Statement> vectors = new ArrayList<>();
        List<Statement> matrices = new ArrayList<>();
        double max = 1;
        for (Statement s : scene.getStatements()) {
            if (s.isDirective()) {
                continue;
            }
            Value v = s.getValue();
            if (isVector2D(v)) {
                vectors.add(s);
                for (int i = 0; i < 2; i++) {
                    double start = v.getOrigin()[i];
                    max = Math.max(max, Math.max(Math.abs(start), Math.abs(start + v.getRows()[i][0])));
                }
            } else if (isMatrix2D(v)) {
                matrices.add(s);
                double[][] m = v.getRows();
                for (double x : new double[] {m[0][0], m[1][0], m[0][1], m[1][1],
                        m[0][0] + m[0][1], m[1][0] + m[1][1]}) {
                    max = Math.max(max, Math.abs(x));
                }
            }
        }

        if (vectors.isEmpty() && matrices.isEmpty()) {
            return "";
        }

        int range = (int) Math.ceil(max - 1e-9);
        double unit = SIZE_CM / range;
        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{tikzpicture}[x=").append(f(unit)).append("cm,y=").append(f(unit))
                .append("cm,>=stealth]\n");
        sb.append("\\useasboundingbox (").append(-range - 0.6).append(",").append(-range - 0.6).append(") rectangle (")
                .append(range + 1.2).append(",").append(range + 0.8).append(");\n");
        sb.append("\\draw[very thin,gray!35] (").append(-range).append(",").append(-range).append(") grid (")
                .append(range).append(",").append(range).append(");\n");
        sb.append("\\draw[->,thick] (").append(-range).append(",0) -- (").append(range).append(",0);\n");
        sb.append("\\draw[->,thick] (0,").append(-range).append(") -- (0,").append(range).append(");\n");
        if (range <= 8) {
            for (int i = -range; i <= range; i++) {
                if (i != 0) {
                    sb.append("\\node[below,font=\\scriptsize] at (").append(i).append(",0) {").append(i).append("};\n");
                    sb.append("\\node[left,font=\\scriptsize] at (0,").append(i).append(") {").append(i).append("};\n");
                }
            }
        }

        sb.append("\\draw[dashed,gray] (0,0) rectangle (1,1);\n");

        int color = 0;
        for (Statement s : matrices) {
            double[][] m = s.getValue().getRows();
            String c = COLORS[color++ % COLORS.length];
            sb.append("\\filldraw[").append(c).append(",fill opacity=0.15,thick] (0,0) -- (")
                    .append(f(m[0][0])).append(",").append(f(m[1][0])).append(") -- (")
                    .append(f(m[0][0] + m[0][1])).append(",").append(f(m[1][0] + m[1][1])).append(") -- (")
                    .append(f(m[0][1])).append(",").append(f(m[1][1])).append(") -- cycle;\n");
            sb.append("\\node[").append(c).append(",font=\\small] at (")
                    .append(f(m[0][0] + m[0][1])).append(",").append(f(m[1][0] + m[1][1]))
                    .append(") {$").append(s.getName()).append("([0,1]^2)$};\n");
        }
        for (Statement s : vectors) {
            double[][] v = s.getValue().getRows();
            double[] start = s.getValue().getOrigin();
            String c = COLORS[color++ % COLORS.length];
            sb.append("\\draw[->,very thick,").append(c).append("] (").append(f(start[0])).append(",")
                    .append(f(start[1])).append(") -- (")
                    .append(f(start[0] + v[0][0])).append(",").append(f(start[1] + v[1][0]))
                    .append(") node[above right,font=\\small] {$")
                    .append(s.getName()).append("$};\n");
        }
        return sb.append("\\end{tikzpicture}").toString();
    }

    private static String f(double d) {
        return LinAlgScene.formatNumber(d, true);
    }
}
