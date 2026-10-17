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

    private LinAlgPlot() {
    }

    /** @return true iff the scene contains something this plot can show. */
    public static boolean hasPlottableContent(LinAlgScene scene) {
        for (Statement s : scene.getStatements()) {
            if (!s.isDirective() && (isVector2D(s.getValue()) || isMatrix2D(s.getValue()))) {
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

    /** The LaTeX code of the picture; requires the tikz package. */
    public static String toTikz(LinAlgScene scene) {
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
                max = Math.max(max, Math.max(Math.abs(v.getRows()[0][0]), Math.abs(v.getRows()[1][0])));
            } else if (isMatrix2D(v)) {
                matrices.add(s);
                double[][] m = v.getRows();
                for (double x : new double[] {m[0][0], m[1][0], m[0][1], m[1][1],
                        m[0][0] + m[0][1], m[1][0] + m[1][1]}) {
                    max = Math.max(max, Math.abs(x));
                }
            }
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
            String c = COLORS[color++ % COLORS.length];
            sb.append("\\draw[->,very thick,").append(c).append("] (0,0) -- (")
                    .append(f(v[0][0])).append(",").append(f(v[1][0])).append(") node[above right,font=\\small] {$")
                    .append(s.getName()).append("$};\n");
        }
        return sb.append("\\end{tikzpicture}").toString();
    }

    private static String f(double d) {
        return LinAlgScene.formatNumber(d, true);
    }
}
