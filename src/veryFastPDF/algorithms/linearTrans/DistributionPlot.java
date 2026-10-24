package veryFastPDF.algorithms.linearTrans;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

final class DistributionPlot {
    private DistributionPlot() {
    }

    static boolean hasContent(LinAlgScene scene) {
        return scene.getStatements().stream().anyMatch(statement -> !statement.getDistributions().isEmpty());
    }

    static String toTikz(LinAlgScene scene) {
        Set<DiscreteDistribution.Request> requests = new LinkedHashSet<>();
        scene.getStatements().forEach(statement -> requests.addAll(statement.getDistributions()));
        return requests.stream().map(DistributionPlot::plot)
                .collect(java.util.stream.Collectors.joining("\n\n\\medskip\n"));
    }

    private static String plot(DiscreteDistribution.Request request) {
        boolean binomial = request.function().equals("binomial");
        long supportEnd = binomial ? request.trials() : Long.MAX_VALUE;
        long center = (long) Math.floor(request.mean());
        long halfWidth = Math.min(50, (long) Math.ceil(6 * Math.sqrt(request.variance()) + 6));
        long first = Math.max(0, center - halfWidth);
        long last = Math.min(supportEnd, center + halfWidth);
        if (binomial && supportEnd <= 100) {
            first = 0;
            last = supportEnd;
        }
        int count = (int) (last - first + 1);
        double[] masses = new double[count];
        double max = 0, total = 0;
        int underflowed = 0;
        for (int i = 0; i < count; i++) {
            double log = request.logMass(first + i);
            masses[i] = Math.exp(log);
            if (masses[i] == 0 && Double.isFinite(log)) underflowed++;
            max = Math.max(max, masses[i]);
            total += masses[i];
        }
        double scale = max * 1.15;
        StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=2cm,y=1.5cm,>=stealth]\n")
                .append("\\node[above,text width=13cm,align=center] at (0,3.4) {")
                .append(binomial ? "Binomial PMF: $n=" + request.trials() + ",\\ p="
                        : "Poisson PMF: $\\lambda=")
                .append(number(request.parameter())).append("$};\n")
                .append("\\draw[->] (-3,0) -- (3.3,0) node[right] {$j$};\n")
                .append("\\draw[->] (-3,0) -- (-3,3.2) node[above] {$P(X=j)$};\n");
        for (int i = 0; i < count; i++) {
            String color = first + i == request.k() ? "red" : "blue!70!black";
            double x = x(first + i, first, last);
            sb.append("\\draw[").append(color).append(",thick] (").append(f(x))
                    .append(",0) -- (").append(f(x)).append(",").append(f(3 * masses[i] / scale)).append(");\n")
                    .append("\\fill[").append(color).append("] (").append(f(x)).append(",")
                    .append(f(3 * masses[i] / scale)).append(") circle (1pt);\n");
        }
        Set<Long> ticks = new LinkedHashSet<>();
        for (int i = 0; i <= 4; i++) ticks.add(first + Math.round((last - first) * i / 4.0));
        for (long tick : ticks) {
            sb.append("\\node[below,font=\\scriptsize] at (").append(f(x(tick, first, last)))
                    .append(",0) {").append(tick).append("};\n");
        }
        for (int i = 1; i <= 4; i++) {
            sb.append("\\node[left,font=\\scriptsize] at (-3,").append(f(i * 0.75))
                    .append(") {$").append(number(scale * i / 4)).append("$};\n");
        }
        sb.append("\\node[below,text width=13cm,align=center,font=\\small] at (0,-0.65) {")
                .append("Discrete probability masses, not a continuous density. ")
                .append("Requested $k=").append(request.k()).append("$: $P(X=k)=")
                .append(number(request.result())).append("$. ")
                .append(request.k() >= first && request.k() <= last
                        ? "Red highlights the requested outcome. " : "Requested outcome is outside the plotted window. ")
                .append("Mean $=").append(number(request.mean())).append("$; variance $=")
                .append(number(request.variance())).append("$.\\\\ ");
        if (binomial && first == 0 && last == supportEnd) {
            sb.append("Full support shown; masses sum to one.");
        } else {
            sb.append("Truncated window $").append(first).append("\\le j\\le ").append(last)
                    .append("$ (at most 101 consecutive outcomes). ")
                    .append("Probability outside window $\\approx ").append(number(Math.max(0, 1 - total)))
                    .append("$. Masses are not renormalized.");
        }
        if (underflowed > 0) {
            sb.append(" ").append(underflowed)
                    .append(" plotted tail masses underflow the numeric range and are drawn as zero.");
        }
        return sb.append("};\n\\end{tikzpicture}").toString();
    }

    private static double x(long k, long first, long last) {
        return last == first ? 0 : -2.8 + 5.6 * (k - first) / (last - first);
    }

    private static String number(double value) {
        return LinAlg.statisticNumber(BigDecimal.valueOf(value));
    }

    private static String f(double value) {
        return LinAlgScene.formatNumber(value, true);
    }
}
