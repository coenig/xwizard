package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.AttentionTrace.Stage;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Stage-level teaching view of one single-head self-attention step. */
final class AttentionAnimation {
    private static final String ACTIVE = "blue";
    private static final String MASKED = "black!40";

    private AttentionAnimation() {}

    static String toLaTeX(AttentionTrace trace, int step) {
        Stage stage = trace.getStage(step);
        int n = trace.getTokenCount();
        List<String> rows = matrixRows(trace, stage);
        StringBuilder sb = new StringBuilder("\\begingroup\\dimen6=").append(Math.max(14, 2 * n + 4)).append("cm\n");
        for (String row : rows) {
            sb.append("\\setbox0=\\hbox{$").append(row).append("$}")
                    .append("\\ifdim\\wd0>\\dimen6\\dimen6=\\wd0\\fi\n");
        }
        sb.append("\\advance\\dimen6 by 1em\\edef\\xwizardattnwidth{\\the\\dimen6}")
                .append("\\begin{minipage}{\\xwizardattnwidth}\\centering\n")
                .append("Self-attention: step ").append(step).append(" of ").append(trace.getStepCount())
                .append(" --- ").append(title(stage)).append("\\par\\medskip\n")
                .append(graph(trace, stage))
                .append("\\parbox{\\linewidth}{").append(explanation(trace, stage)).append("}\\par\\medskip\n");
        for (String row : rows) sb.append("$$").append(row).append("$$\n");
        sb.append("\\parbox{\\linewidth}{\\centering Rows are tokens. Blue: computed in this step; ")
                .append("$\\square$: not computed yet")
                .append(trace.isCausal() ? "; grey $-\\infty$ and $0$: masked later tokens" : "")
                .append(". Line thickness in the graph shows the attention weight $A_{ij}$ of query $i$ on key $j$. ")
                .append("Displayed numbers are rounded; all results use full precision.}\\par\n");
        sb.append(workedExample(trace, stage));
        return sb.append("\\end{minipage}\\endgroup\n").toString();
    }

    private static String title(Stage stage) {
        return switch (stage) {
        case INITIAL -> "INITIAL";
        case QUERY -> "QUERIES";
        case KEY -> "KEYS";
        case VALUE -> "VALUES";
        case SCORES -> "SCORES";
        case SOFTMAX -> "ATTENTION WEIGHTS";
        case OUTPUT -> "WEIGHTED SUM OF VALUES";
        case PROJECTION -> "OUTPUT PROJECTION";
        case COMPLETE -> "COMPLETE";
        };
    }

    private static boolean reached(Stage current, Stage stage) {
        return current.ordinal() >= stage.ordinal();
    }

    private static List<String> matrixRows(AttentionTrace trace, Stage stage) {
        int ex = trace.getTokenCount() - 1;
        boolean[][] mask = mask(trace);
        List<String> rows = new ArrayList<>();
        rows.add("X=" + matrix(trace.getInput()) + ",\\quad W_Q=" + matrix(trace.getQueryWeights())
                + ",\\quad W_K=" + matrix(trace.getKeyWeights()) + ",\\quad W_V=" + matrix(trace.getValueWeights()));
        rows.add("Q=XW_Q=" + result(trace.getQueries(), null, stage, Stage.QUERY, ex, 0)
                + ",\\quad K=XW_K=" + result(trace.getKeys(), null, stage, Stage.KEY, ex, 0)
                + ",\\quad V=XW_V=" + result(trace.getValues(), null, stage, Stage.VALUE, ex, 0));
        String scores = "S=\\frac{QK^{\\top}}{\\sqrt{d_k}}=" + result(trace.getScores(), mask, stage, Stage.SCORES, ex, 0);
        String weights = "A=\\operatorname{softmax}_{\\mathrm{row}}(S)="
                + result(trace.getWeights(), mask, stage, Stage.SOFTMAX, ex, -1);
        if (trace.getTokenCount() > 5) {
            rows.add(scores);
            rows.add(weights);
        } else {
            rows.add(scores + ",\\quad " + weights);
        }
        String mixed = "Z=AV=" + result(trace.getMixed(), null, stage, Stage.OUTPUT, ex, 0);
        if (trace.hasProjection()) {
            mixed += ",\\quad W_O=" + matrix(trace.getOutputWeights())
                    + ",\\quad O=ZW_O=" + result(trace.getOutput(), null, stage, Stage.PROJECTION, ex, 0);
        }
        rows.add(mixed);
        return rows;
    }

    private static boolean[][] mask(AttentionTrace trace) {
        if (!trace.isCausal()) return null;
        int n = trace.getTokenCount();
        boolean[][] mask = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) mask[i][j] = trace.isMasked(i, j);
        }
        return mask;
    }

    /** A computed matrix: squares before its stage, blue (with the worked entry circled) in its stage. */
    private static String result(Value value, boolean[][] mask, Stage current, Stage stage, int row, int column) {
        if (!reached(current, stage)) return matrix(value, mask, false, "", -1, -1);
        boolean active = current == stage;
        return matrix(value, mask, true, active ? ACTIVE : "", active ? row : -1, column);
    }

    private static String matrix(Value value) {
        return matrix(value, null, true, "", -1, -1);
    }

    /**
     * @param highlightColumn  -1 circles the whole highlighted row.
     */
    private static String matrix(Value value, boolean[][] mask, boolean known, String color,
            int highlightRow, int highlightColumn) {
        StringBuilder sb = new StringBuilder(spacing(value)).append("\\begin{pmatrix}\n");
        for (int r = 0; r < value.getRowCount(); r++) {
            for (int c = 0; c < value.getColumnCount(); c++) {
                String entry;
                if (!known) {
                    entry = "\\square";
                } else {
                    entry = entry(value.getRows()[r][c]);
                    boolean masked = mask != null && mask[r][c];
                    boolean highlighted = r == highlightRow && (highlightColumn == -1 || c == highlightColumn);
                    if (highlighted && !masked) {
                        entry = LinAlg.circledFactor(entry, color);
                    } else if (masked) {
                        entry = "{\\color{" + MASKED + "}" + entry + "}";
                    } else if (!color.isEmpty()) {
                        entry = "{\\color{" + color + "}" + entry + "}";
                    }
                }
                sb.append(c == 0 ? "" : " & ").append(LinAlg.spacedMultiplicationEntry(entry, c));
            }
            sb.append(" \\\\\n");
        }
        return sb.append("\\end{pmatrix}\\endgroup").toString();
    }

    /** Same column widths in every frame: measure all final entries, circled, and the placeholder. */
    private static String spacing(Value value) {
        StringBuilder sb = new StringBuilder("\\begingroup\\dimen2=0pt\\dimen4=0pt\n");
        for (int c = 0; c < value.getColumnCount(); c++) {
            sb.append("\\dimen0=0pt\n");
            java.util.Set<String> entries = new java.util.LinkedHashSet<>();
            entries.add("\\square");
            for (double[] row : value.getRows()) entries.add(entry(row[c]));
            for (String entry : entries) {
                sb.append("\\setbox0=\\hbox{$").append(LinAlg.circledFactor(entry, "black")).append("$}")
                        .append("\\ifdim\\wd0>\\dimen0\\dimen0=\\wd0\\fi")
                        .append("\\ifdim\\ht0>\\dimen2\\dimen2=\\ht0\\fi")
                        .append("\\ifdim\\dp0>\\dimen4\\dimen4=\\dp0\\fi\n");
            }
            sb.append("\\expandafter\\edef\\csname xwizardwidth").append(c)
                    .append("\\endcsname{\\the\\dimen0}\n");
        }
        return sb.append("\\edef\\xwizardheight{\\the\\dimen2}")
                .append("\\edef\\xwizarddepth{\\the\\dimen4}\n").toString();
    }

    private static String entry(double value) {
        return Double.isInfinite(value) ? (value < 0 ? "-\\infty" : "\\infty") : number(value);
    }

    private static String graph(AttentionTrace trace, Stage stage) {
        int n = trace.getTokenCount(), ex = n - 1;
        Value weights = trace.getWeights();
        StringBuilder sb = new StringBuilder("\\begin{tikzpicture}[x=1cm,y=1cm]\n")
                .append("\\path[use as bounding box] (-3.2,0.6) rectangle (").append(2 * (n - 1) + 1).append(",-3);\n")
                .append("\\node[anchor=east,font=\\scriptsize,align=right] at (-1,0) {keys $k_j$\\\\values $v_j$};\n")
                .append("\\node[anchor=east,font=\\scriptsize,align=right] at (-1,-2.4) {queries $q_i$\\\\outputs $z_i$};\n");
        boolean weighted = reached(stage, Stage.SOFTMAX);
        if (reached(stage, Stage.SCORES)) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (trace.isMasked(i, j)) continue;
                    String style = weighted
                            ? "blue,line width=" + String.format(java.util.Locale.ROOT, "%.2f", 0.2 + 3 * weights.getRows()[i][j])
                                    + "pt,opacity=" + String.format(java.util.Locale.ROOT, "%.2f", 0.25 + 0.75 * weights.getRows()[i][j])
                            : "gray!50";
                    sb.append("\\draw[").append(style).append("] (").append(2 * j).append(",-0.35) -- (")
                            .append(2 * i).append(",-2.05);\n");
                }
            }
            if (weighted) {
                // Label only the worked query's lines, near the keys where these lines are far apart.
                for (int j = 0; j < n; j++) {
                    sb.append("\\path (").append(2 * j).append(",-0.35) -- (").append(2 * ex)
                            .append(",-2.05) node[pos=0.2,fill=white,inner sep=1pt,font=\\scriptsize,text=blue] {$")
                            .append(number(weights.getRows()[ex][j])).append("$};\n");
                }
            }
        }
        boolean keysActive = stage == Stage.KEY || stage == Stage.VALUE;
        boolean queriesActive = stage == Stage.QUERY || stage == Stage.OUTPUT || stage == Stage.PROJECTION;
        for (int j = 0; j < n; j++) {
            sb.append(token(trace.getTokens().get(j), 2 * j, 0, keysActive));
            sb.append(token(trace.getTokens().get(j), 2 * j, -2.4, queriesActive));
        }
        return sb.append("\\end{tikzpicture}\\par\n").toString();
    }

    private static String token(String label, double x, double y, boolean active) {
        return "\\node[draw=" + (active ? ACTIVE + ",very thick" : "black") + ",rounded corners,minimum width=1.5cm,"
                + "minimum height=0.7cm,fill=white,font=\\small] at (" + x + "," + y + ") {\\strut " + label + "};\n";
    }

    private static String explanation(AttentionTrace trace, Stage stage) {
        return switch (stage) {
        case INITIAL -> "\\textbf{Goal: let every token gather information from the other tokens.} "
                + "Each row of $X$ is one token's embedding vector: " + trace.getTokenCount() + " tokens with "
                + trace.getInput().getColumnCount() + " features each. Self-attention computes a query, a key and a "
                + "value for every token, compares every query with every key, turns the comparisons into weights, "
                + "and mixes the values with these weights. The learned matrices $W_Q$, $W_K$, $W_V$ are the same for all tokens."
                + (trace.isCausal() ? " A \\textbf{causal mask} is active: each token may only look at itself and earlier tokens, "
                        + "as in a text-generating decoder." : "");
        case QUERY -> "\\textbf{Queries: what is each token looking for?} Multiply each token row by $W_Q$: "
                + "$q_i=x_iW_Q$, so $Q=XW_Q$. Every row is computed with the same matrix.";
        case KEY -> "\\textbf{Keys: what does each token offer?} $K=XW_K$. A query will be compared with every key; "
                + "a large dot product $q_i\\cdot k_j$ means token $j$ is relevant for token $i$.";
        case VALUE -> "\\textbf{Values: what does each token pass on?} $V=XW_V$. When token $i$ attends to token $j$, "
                + "it receives $v_j$, not $x_j$ itself.";
        case SCORES -> "\\textbf{Compare every query with every key.} $S_{ij}=q_i\\cdot k_j/\\sqrt{d_k}$ for all pairs at once: "
                + "$S=QK^{\\top}/\\sqrt{d_k}$. Dividing by $\\sqrt{d_k}$ keeps the scores from growing with the dimension, "
                + "so the softmax does not saturate. Row $i$ holds token $i$'s scores for all tokens."
                + (trace.isCausal() ? " Masked pairs ($j>i$, later tokens) get $-\\infty$, so they receive zero weight." : "");
        case SOFTMAX -> "\\textbf{Turn scores into attention weights.} Apply softmax to each row: all weights become "
                + "positive and each row sums to $1$. A larger score gives a larger share. "
                + "The line thickness in the graph now shows $A_{ij}$; the numbers belong to the circled row.";
        case OUTPUT -> "\\textbf{Mix the values.} $z_i=\\sum_j A_{ij}v_j$, i.e.\\ $Z=AV$: each output row is a weighted "
                + "average of the value rows. This is the new, context-aware representation of token $i$.";
        case PROJECTION -> "\\textbf{Project the result.} $O=ZW_O$ maps the mixed values to the output dimension. "
                + "In multi-head attention, this matrix also combines the heads.";
        case COMPLETE -> "\\textbf{Finished: one self-attention step.} Each output row depends on "
                + (trace.isCausal() ? "the token itself and all earlier tokens" : "all tokens")
                + ", weighted by how well its query matches their keys. In a transformer block, the result is "
                + "then added to $X$ (residual connection), normalized, and passed through a feed-forward network; "
                + "these parts are not shown here.";
        };
    }

    private static String workedExample(AttentionTrace trace, Stage stage) {
        int n = trace.getTokenCount(), ex = n - 1;
        String i = Integer.toString(n);
        return switch (stage) {
        case INITIAL -> "Ready: compute $Q$, $K$ and $V$ from $X$.\\par\n";
        case QUERY -> productEntry("q", trace.getInput(), trace.getQueryWeights(), trace.getQueries(), ex,
                "x_{" + i + "m}(W_Q)_{m1}");
        case KEY -> productEntry("k", trace.getInput(), trace.getKeyWeights(), trace.getKeys(), ex,
                "x_{" + i + "m}(W_K)_{m1}");
        case VALUE -> productEntry("v", trace.getInput(), trace.getValueWeights(), trace.getValues(), ex,
                "x_{" + i + "m}(W_V)_{m1}");
        case SCORES -> "$$S_{" + i + "1}=\\frac{q_{" + i + "}\\cdot k_{1}}{\\sqrt{d_k}}=\\frac{"
                + terms(trace.getQueries().getRows()[ex], trace.getKeys().getRows()[0]) + "}{\\sqrt{"
                + trace.getKeyDimension() + "}}=" + LinAlg.circledFactor(number(trace.getScores().getRows()[ex][0]), ACTIVE)
                + "$$\n$d_k=" + trace.getKeyDimension() + "$ is the number of columns of $W_Q$ and $W_K$.\\par\n";
        case SOFTMAX -> softmaxRow(trace, ex);
        case OUTPUT -> "$$z_{" + i + "1}=\\sum_j A_{" + i + "j}v_{j1}="
                + terms(trace.getWeights().getRows()[ex], column(trace.getValues(), 0)) + "="
                + LinAlg.circledFactor(number(trace.getMixed().getRows()[ex][0]), ACTIVE) + "$$\n";
        case PROJECTION -> productEntry("o", trace.getMixed(), trace.getOutputWeights(), trace.getOutput(), ex,
                "z_{" + i + "m}(W_O)_{m1}");
        case COMPLETE -> completion(trace);
        };
    }

    private static String productEntry(String name, Value left, Value right, Value result, int row, String term) {
        return "$$" + name + "_{" + (row + 1) + "1}=\\sum_m " + term + "="
                + terms(left.getRows()[row], column(right, 0)) + "="
                + LinAlg.circledFactor(number(result.getRows()[row][0]), ACTIVE) + "$$\n";
    }

    private static String softmaxRow(AttentionTrace trace, int ex) {
        double[] scores = trace.getScores().getRows()[ex];
        double max = Double.NEGATIVE_INFINITY;
        for (double s : scores) max = Math.max(max, s);
        StringBuilder exps = new StringBuilder(), result = new StringBuilder();
        double sum = 0;
        for (int j = 0; j < scores.length; j++) {
            double e = Math.exp(scores[j] - max);
            sum += e;
            exps.append(j == 0 ? "" : ",\\ ").append(number(e));
            result.append(j == 0 ? "" : ",\\ ").append(number(trace.getWeights().getRows()[ex][j]));
        }
        String i = Integer.toString(ex + 1);
        return "$$A_{" + i + "j}=\\frac{e^{S_{" + i + "j}-m}}{\\sum_l e^{S_{" + i + "l}-m}},\\qquad m=\\max_j S_{" + i + "j}="
                + number(max) + "$$\n"
                + "$$\\bigl(e^{S_{" + i + "j}-m}\\bigr)_j=(" + exps + "),\\qquad\\sum=" + number(sum) + "$$\n"
                + "$$A_{" + i + ",:}=(" + result + ")$$\n"
                + "Subtracting the row maximum $m$ does not change the result but avoids overflow.\\par\n";
    }

    private static String completion(AttentionTrace trace) {
        StringBuilder sums = new StringBuilder();
        for (int r = 0; r < trace.getTokenCount(); r++) {
            double sum = 0;
            for (double w : trace.getWeights().getRows()[r]) sum += w;
            sums.append(r == 0 ? "" : ",\\ ").append(number(sum));
        }
        return "$$\\Bigl(\\sum_j A_{ij}\\Bigr)_i=(" + sums + ")$$\n"
                + "Result: " + (trace.hasProjection() ? "$O$" : "$Z$") + ", one row per token, "
                + "ready for the next layer.\\par\n";
    }

    private static double[] column(Value value, int column) {
        double[] result = new double[value.getRowCount()];
        for (int r = 0; r < result.length; r++) result[r] = value.getRows()[r][column];
        return result;
    }

    private static String terms(double[] a, double[] b) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < a.length; k++) {
            sb.append(k == 0 ? "" : "+").append(factor(a[k])).append("\\cdot ").append(factor(b[k]));
        }
        return sb.toString();
    }

    private static String factor(double value) {
        String text = number(value);
        return text.startsWith("-") ? "(" + text + ")" : text;
    }

    private static String number(double value) {
        return LinAlgScene.formatNumber(value, true);
    }
}
