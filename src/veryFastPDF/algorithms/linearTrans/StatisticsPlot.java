package veryFastPDF.algorithms.linearTrans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Empirical histograms for datasets used by statistics calls. */
final class StatisticsPlot {
    private StatisticsPlot() {
    }

    private static List<Statistics.Summary> datasets(LinAlgScene scene) {
        List<Statistics.Summary> result = new ArrayList<>();
        for (var statement : scene.getStatements()) {
            for (Value input : statement.getStatisticalInputs()) {
                Statistics.Summary summary = Statistics.summarize(input);
                if (result.stream().noneMatch(previous -> Arrays.equals(previous.data(), summary.data()))) {
                    result.add(summary);
                }
            }
        }
        return result;
    }

    static boolean hasContent(LinAlgScene scene) {
        return scene.getStatements().stream().anyMatch(s -> !s.getStatisticalInputs().isEmpty());
    }

    static String toTikz(LinAlgScene scene) {
        List<String> plots = new ArrayList<>();
        for (Statistics.Summary summary : datasets(scene)) plots.add(plot(summary));
        return String.join("\n\n\\medskip\n", plots);
    }

    private static String plot(Statistics.Summary summary) {
        double[] data = summary.data();
        BigDecimal min = BigDecimal.valueOf(Arrays.stream(data).min().orElseThrow());
        BigDecimal max = BigDecimal.valueOf(Arrays.stream(data).max().orElseThrow());
        BigDecimal spread = max.subtract(min);
        int bins = spread.signum() == 0 ? 1 : Math.min(12, (int) Math.ceil(Math.sqrt(data.length)));
        int[] counts = new int[bins];
        BigDecimal width = spread.signum() == 0 ? min.abs().multiply(new BigDecimal("0.1")).max(BigDecimal.ONE)
                : spread.divide(BigDecimal.valueOf(bins), Statistics.PRECISION);
        BigDecimal binStart = spread.signum() == 0 ? min.subtract(width.divide(BigDecimal.valueOf(2))) : min;
        for (double value : data) {
            int bin = spread.signum() == 0 ? 0 : BigDecimal.valueOf(value).subtract(min)
                    .multiply(BigDecimal.valueOf(bins)).divide(spread, 0, RoundingMode.FLOOR).intValueExact();
            counts[Math.min(bin, bins - 1)]++;
        }
        BigDecimal lowSigma = summary.mean().subtract(summary.std());
        BigDecimal highSigma = summary.mean().add(summary.std());
        BigDecimal left = binStart.min(lowSigma);
        BigDecimal right = binStart.add(width.multiply(BigDecimal.valueOf(bins))).max(highSigma);
        BigDecimal padding = right.subtract(left).multiply(new BigDecimal("0.05"));
        left = left.subtract(padding);
        right = right.add(padding);
        StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=2cm,y=1.5cm,>=stealth]\n")
                .append("\\node[above,text width=12cm,align=center] at (0,3.5) {")
                .append("Empirical distribution ($n=").append(data.length).append("$)};\n");
        sb.append("\\fill[orange!15] (").append(x(lowSigma, left, right)).append(",0) rectangle (")
                .append(x(highSigma, left, right)).append(",3);\n");
        for (int i = 0; i < bins; i++) {
            BigDecimal start = binStart.add(width.multiply(BigDecimal.valueOf(i)));
            BigDecimal end = i == bins - 1 && spread.signum() != 0 ? max : start.add(width);
            double height = 3.0 * counts[i] / data.length;
            sb.append("\\filldraw[fill=blue!35,draw=blue!70!black] (")
                    .append(x(start, left, right)).append(",0) rectangle (").append(x(end, left, right))
                    .append(",").append(f(height)).append(");\n");
            sb.append("\\node[above,font=\\scriptsize] at (")
                    .append(x(start.add(end).divide(BigDecimal.valueOf(2)), left, right)).append(",")
                    .append(f(height)).append(") {").append(counts[i]).append("};\n");
        }
        for (BigDecimal boundary : new BigDecimal[] {lowSigma, highSigma}) {
            sb.append("\\draw[orange!80!black,dashed,thick] (").append(x(boundary, left, right))
                    .append(",0) -- (").append(x(boundary, left, right)).append(",3.1);\n");
        }
        sb.append("\\draw[red,very thick] (").append(x(summary.mean(), left, right))
                .append(",0) -- (").append(x(summary.mean(), left, right)).append(",3.2);\n")
                .append("\\draw[->] (-3,0) -- (3.2,0);\n\\draw[->] (-3,0) -- (-3,3.3);\n");
        for (int i = 0; i <= 4; i++) {
            BigDecimal tick = left.add(right.subtract(left).multiply(BigDecimal.valueOf(i))
                    .divide(BigDecimal.valueOf(4), Statistics.PRECISION));
            sb.append("\\node[below,font=\\scriptsize] at (").append(f(-3 + i * 1.5))
                    .append(",0) {$").append(label(tick)).append("$};\n")
                    .append("\\node[left,font=\\scriptsize] at (-3,").append(f(i * 0.75))
                    .append(") {").append(f(i * 0.25)).append("};\n");
        }
        sb.append("\\node[rotate=90,above] at (-3.3,1.5) {Relative frequency per bin};\n")
                .append("\\node[below,text width=13cm,align=center,font=\\small] at (0,-0.65) {")
                .append("Red: mean $\\mu=").append(label(summary.mean())).append("$. Dashed: $\\mu\\pm\\sigma$, ")
                .append("$\\sigma=").append(label(summary.std())).append("$. Shading: one standard deviation.\\\\ ")
                .append("Equal-width histogram; bar labels are counts. Bin masses sum to one, not bar areas. ")
                .append("Bins include their left edge; only the last bin includes its right edge. ")
                .append("This is observed data, not an assumed normal distribution; ")
                .append("no fixed probability coverage is implied by $\\mu\\pm\\sigma$.};\n");
        return sb.append("\\end{tikzpicture}").toString();
    }

    private static String x(BigDecimal value, BigDecimal left, BigDecimal right) {
        return f(value.subtract(left).divide(right.subtract(left), Statistics.PRECISION).doubleValue() * 6 - 3);
    }

    private static String label(BigDecimal value) {
        return LinAlg.statisticNumber(value);
    }

    private static String f(double value) {
        return LinAlgScene.formatNumber(value, true);
    }
}
