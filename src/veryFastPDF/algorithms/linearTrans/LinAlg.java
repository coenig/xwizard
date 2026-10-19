package veryFastPDF.algorithms.linearTrans;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eas.math.matrix.Matrix;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Statement;
import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * Linear algebra scene: named matrices and vectors, defined by statements such as
 * {@code C = B * A}, shown as LaTeX math. See {@link LinAlgScene} for the syntax.
 * The legacy preamble {@code matrix:} is still accepted.
 */
@Webproof(useInProductiveMode = true)
public class LinAlg extends RepresentableDefault {

    private static final long serialVersionUID = 4120758296435718323L;

    public static final String SCRIPT_PREAMBLE = "linalg:";
    private static final String LEGACY_PREAMBLE = "matrix:";

    private static final Pattern LITERAL = Pattern.compile("^[\\[(][-+0-9.eE\\s,;]*[\\])]$");
    private static final Pattern MATRIX_LITERAL = Pattern.compile("\\[[-+0-9.eE\\s,;]+\\]");

    private LinAlgScene scene = new LinAlgScene();

    /** Declaration variable: iff true, 2D vectors and 2x2 matrices are plotted. */
    private boolean showPlot = false;

    /** 
     * Declaration variable: iff true, the formulas are written above the plot. They are always 
     * written if the plot is off, as there would be nothing left to show otherwise.
     */
    private boolean showFormulas = true;

    public LinAlg(Exercise exercise) {
        super(exercise);
        this.setAllowCollapsingRules(false);
    }

    @Override
    public String[] getExampleScripts() {
        return new String[] {
                SCRIPT_PREAMBLE + "\nA = [2 0; 0 0.5]\nB = [0 -1; 1 0]\nC = B * A\nv = (1, 1)\nw = C * v\n"
                        + DECL_BEG_TAG + "\nshowPlot=true\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\nR = [0 -1 0; 1 0 0; 0 0 1]\nS = [1 0 0.5; 0 1 0; 0 0 1]\nM = S * R\nv = (1, 0.5, 1)\nw = M * v\n"
                        + DECL_BEG_TAG + "\nshowPlot=true\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\nA = [1 2.5 -3; 0 100 4]\nsvd(A)\n",
                SCRIPT_PREAMBLE + "\nA = [1 2 3; 2 4 6; 1 1 1]\ndet(A)\nrank(A)\ntrace(A)\nrref(A)\nbasis(A)\nnullspace(A)\nspan(A)\nu = (1, 2, 1)\nv = (2, 4, 1)\nspan(u, v)\nn = norm(u)\n",
                SCRIPT_PREAMBLE + "\nA = [4 1 0; 2 3 0; 0 0 1]\neig(A)\n",
                SCRIPT_PREAMBLE + "\nA = [0 -1 0; 1 0 0; 0 0 5]\neig(A)\n",
                SCRIPT_PREAMBLE + "\nA = [0 2; 1 3]\nDet(A)\nRank(A)\nBasis(A)\nInv(A)\n",
                SCRIPT_PREAMBLE + "\nf(x, y) = x^2 + 2*x*y + 3*y^2\np = (1, 2)\ng = grad(f, p)\nGrad(f, p)\n"
                        + DECL_BEG_TAG + "\nshowPlot=true\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\nf(x, y) = x^2 + 2*x*y + 3*y^2\np = (1, 2)\nHessian(f, p)\nLaplacian(f, p)\n"
                        + "F(x, y) = (x*y, sin(x))\nJacobian(F, p)\nDivergence(F, p)\n"
                        + "G(x, y, z) = (-y, x, 0)\nCurl(G, (1, 2, 3))\n"};
    }

    @Override
    public String getExampleScriptName(int index, boolean english) {
        switch (index) {
        case 0:
            return english ? "2D transformations: scaling and rotation" : "2D-Transformationen: Skalierung und Rotation";
        case 1:
            return english ? "3D transformations: rotation and shear" : "3D-Transformationen: Rotation und Scherung";
        case 2:
            return english ? "SVD of a rectangular matrix" : "SVD einer rechteckigen Matrix";
        case 3:
            return english ? "Rank, basis, nullspace and span" : "Rang, Basis, Kern und lineare Huelle";
        case 4:
            return english ? "Real eigenvalue decomposition" : "Reelle Eigenwertzerlegung";
        case 5:
            return english ? "Eigenvalues: complex case and limitations" : "Eigenwerte: komplexer Fall und Grenzen";
        case 6:
            return english ? "Step-by-step elimination and ML uses" : "Elimination Schritt fuer Schritt und ML-Anwendungen";
        case 7:
            return english ? "Gradient: derivatives and 2D plot" : "Gradient: Ableitungen und 2D-Darstellung";
        case 8:
            return english ? "Jacobian, Hessian and vector calculus" : "Jacobi-Matrix, Hesse-Matrix und Vektoranalysis";
        default:
            return super.getExampleScriptName(index, english);
        }
    }

    @Override
    public boolean isAcceptableScript(String code) {
        String c = (code + "").trim().toLowerCase();
        return c.startsWith(SCRIPT_PREAMBLE) || c.startsWith(LEGACY_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(script, father, 0);
        this.scene = LinAlgScene.parse(this.getScriptWithoutPrepAndDeclAndPreamble());
    }

    public LinAlgScene getScene() {
        return scene;
    }

    /** LaTeX math-mode representation of all statements and the requested decompositions. */
    public String toLaTeX() {
        if (scene.getStatements().isEmpty()) {
            return "\\text{No matrices defined.}";
        }

        StringBuilder sb = new StringBuilder();
        if (showFormulas || !showPlot) {
            appendFormulas(sb);
        }

        if (showPlot) {
            if (LinAlgPlot.hasPlottableContent(scene)) {
                sb.append("\\begin{center}\n").append(LinAlgPlot.toTikz(scene)).append("\n\\end{center}\n");
            } else {
                sb.append("\\begin{center}Nothing to plot: define a 2D/3D vector like (1, 2, 3) or a 2x2/3x3 matrix.\\end{center}\n");
            }
        }
        return sb.toString();
    }

    private void appendFormulas(StringBuilder sb) {
        for (Statement s : scene.getStatements()) {
            appendExplanations(sb, s);
            if (s.getDefinition() != null) {
                String signature = s.getExpression().substring(0, s.getExpression().indexOf('=')).trim();
                sb.append("$$").append(signature.replace("_", "\\_")).append(" = ")
                        .append(s.getDefinition().toLaTeX()).append("$$\n");
                continue;
            }
            if (s.isDirective()) {
                if (s.getFunction().equals("svd") || s.getFunction().equals("eig")) {
                    appendDecomposition(sb, s);
                } else {
                    sb.append("$$").append(expressionToLaTeX(s.getExpression())).append(" = ")
                            .append(simpleFormula(s))
                            .append(s.getFunction().equals("span") ? spanToLaTeX(s.getValue())
                                    : valueToLaTeX(s.getValue()))
                            .append("$$\n");
                }
                continue;
            }
            sb.append("$$").append(s.getName()).append(" = ");
            if (!LITERAL.matcher(s.getExpression()).matches()) {
                sb.append(expressionToLaTeX(s.getExpression())).append(" = ");
            }
            sb.append(simpleFormula(s));
            sb.append(valueToLaTeX(s.getValue())).append("$$\n");
        }
    }

    private static String simpleFormula(Statement statement) {
        Value input = statement.getFunctionInput();
        if (input == null) {
            return "";
        }
        String function = statement.getExpression().substring(0, statement.getExpression().indexOf('('))
                .trim().toLowerCase(java.util.Locale.ROOT);
        double[][] a = input.getRows();
        int rows = input.getRowCount();
        int columns = input.getColumnCount();
        if (function.equals("det") && rows == columns && rows > 0) {
            if (rows == 1 || Character.isUpperCase(statement.getExpression().charAt(0))) {
                return "";
            }
            StringBuilder formula = new StringBuilder();
            for (int c = 0; c < columns; c++) {
                double[][] minor = new double[rows - 1][columns - 1];
                for (int r = 1; r < rows; r++) {
                    int targetColumn = 0;
                    for (int j = 0; j < columns; j++) {
                        if (j != c) {
                            minor[r - 1][targetColumn++] = a[r][j];
                        }
                    }
                }
                formula.append(c == 0 ? "" : c % 2 == 1 ? " - " : " + ")
                        .append(factor(a[0][c])).append(" \\cdot \\det\\left(")
                        .append(latexMatrix(minor, false)).append("\\right)");
            }
            return formula.append(" = ").toString();
        }
        if (function.equals("trace") && rows > 0 && rows <= 4) {
            StringBuilder formula = new StringBuilder();
            for (int i = 0; i < rows; i++) {
                formula.append(i == 0 ? "" : " + ").append(factor(a[i][i]));
            }
            return formula.append(" = ").toString();
        }
        if (function.equals("norm") && rows * columns > 0 && rows * columns <= 4) {
            StringBuilder formula = new StringBuilder("\\sqrt{");
            for (double[] row : a) {
                for (double entry : row) {
                    if (formula.length() > "\\sqrt{".length()) {
                        formula.append(" + ");
                    }
                    formula.append(factor(entry)).append("^{2}");
                }
            }
            return formula.append("} = ").toString();
        }
        return "";
    }

    private static void appendExplanations(StringBuilder sb, Statement statement) {
        for (LinAlgScene.Explanation explanation : statement.getExplanations()) {
            String description;
            String mlUsage;
            switch (explanation.function) {
            case "jacobian":
                description = "Differentiate every output component with respect to every input variable. "
                        + "Rows correspond to output components; columns follow the declared parameter order. "
                        + "Then substitute the evaluation point.";
                mlUsage = "Jacobians describe local input-output sensitivity and underpin backpropagation "
                        + "and change-of-variables calculations in normalizing flows.";
                break;
            case "hessian":
                description = "Differentiate the scalar function twice: entry $(i,j)$ is "
                        + "$\\partial_j(\\partial_i f)$. Rows and columns follow parameter order. "
                        + "For a twice continuously differentiable function, the matrix is symmetric.";
                mlUsage = "Hessians describe loss curvature for Newton-type optimization, "
                        + "conditioning analysis and local uncertainty approximations.";
                break;
            case "laplacian":
                description = "Sum the pure second derivatives: $\\Delta f=\\sum_i\\partial_i^2 f$. "
                        + "This is the trace of the Hessian. Then substitute the evaluation point.";
                mlUsage = "The continuous Laplacian appears in diffusion models and physics-informed learning. "
                        + "Graph-based learning uses a related, discrete graph Laplacian, not this operator.";
                break;
            case "divergence":
                description = "Differentiate component $i$ with respect to variable $i$ and sum: "
                        + "$\\nabla\\cdot F=\\sum_i\\partial_i F_i$. This is the trace of the Jacobian.";
                mlUsage = "Divergence measures local expansion of a learned flow and governs "
                        + "log-density changes in continuous normalizing flows.";
                break;
            case "curl":
                description = "For a three-dimensional field, form "
                        + "$\\nabla\\times F=(\\partial_y F_z-\\partial_z F_y,"
                        + "\\partial_z F_x-\\partial_x F_z,\\partial_x F_y-\\partial_y F_x)^T$, "
                        + "using the declared variable order as the coordinate order.";
                mlUsage = "Curl measures local rotation in learned three-dimensional vector fields, "
                        + "useful for physics-informed constraints and checking whether a field is conservative.";
                break;
            case "grad":
                description = "Differentiate the scalar function with respect to each variable, holding the others "
                        + "constant (product, quotient and chain rules). Stack the partial derivatives in parameter "
                        + "order, then substitute the evaluation point.";
                mlUsage = "Gradients drive loss minimization: gradient descent updates parameters "
                        + "opposite to the loss gradient; input gradients also measure sensitivity.";
                break;
            case "det":
                description = "Gaussian elimination: row additions preserve the determinant; each row swap "
                        + "changes its sign. The determinant is the signed product of the triangular diagonal.";
                mlUsage = "The absolute determinant measures volume scaling in density transformations; "
                        + "log-determinants also occur in Gaussian likelihoods and covariance models.";
                break;
            case "rank":
                description = "Gauss--Jordan elimination: the rank is the number of pivot columns (nonzero rows).";
                mlUsage = "Rank reveals redundant features and limits the effective dimensionality "
                        + "of data matrices and low-rank model representations.";
                break;
            case "rref":
                description = "Gauss--Jordan elimination: choose a pivot, scale its row to make the pivot one, "
                        + "and eliminate the other entries in its column.";
                mlUsage = "RREF helps explain feature dependencies and linear constraints in small examples; "
                        + "large numerical ML problems usually use QR or SVD instead.";
                break;
            case "basis":
                description = "Use Gauss--Jordan elimination to identify pivot columns, then select those columns "
                        + "from the ORIGINAL input. Row operations identify dependencies but change the column space.";
                mlUsage = "A basis removes redundant feature directions while preserving the column space. "
                        + "These selected original columns are not necessarily orthogonal or principal components.";
                break;
            case "span":
                description = "Use Gauss--Jordan elimination to identify pivot columns, then select those columns "
                        + "from the ORIGINAL input. Row operations identify dependencies but change the column space.";
                mlUsage = "The span describes all outputs expressible as linear combinations of feature "
                        + "columns, clarifying the representational limits of a linear model.";
                break;
            case "nullspace":
                description = "Reduce to RREF. Nonpivot columns represent free variables. Set each free variable "
                        + "to one in turn and the others to zero; solve the pivot equations to obtain basis columns.";
                mlUsage = "Nullspace directions leave linear predictions unchanged, exposing "
                        + "unidentifiable parameters and degrees of freedom in constrained models.";
                break;
            case "inv":
                description = "Gauss--Jordan elimination on the augmented matrix: reduce the left half to identity. "
                        + "The right half is then the inverse.";
                mlUsage = "Inverses appear in least-squares and Gaussian-model formulas; "
                        + "in numerical ML, solve linear systems rather than explicitly forming the inverse.";
                break;
            case "trace":
                description = "Add the diagonal entries: $\\operatorname{trace}(A)=\\sum_i a_{ii}$.";
                mlUsage = "The trace of a covariance matrix is total variance; "
                        + "trace expressions also summarize quadratic losses and matrix regularizers.";
                break;
            case "norm":
                description = "Square the entries, sum them, and take the square root: "
                        + "$\\|A\\|_F=\\sqrt{\\sum_{i,j}a_{ij}^2}$. For a vector this is its Euclidean length. "
                        + "The numerical calculation uses hypot to avoid unnecessary overflow.";
                mlUsage = "Euclidean and Frobenius norms measure errors and parameter size "
                        + "in distance-based methods, gradient clipping and squared L2 regularization.";
                break;
            case "transpose":
                description = "Exchange rows and columns: $(A^T)_{ij}=A_{ji}$.";
                mlUsage = "Transposes align dimensions in batched predictions, covariance calculations "
                        + "and backpropagation through linear layers.";
                break;
            case "svd":
                description = "One-sided Jacobi SVD: rotate pairs of columns until they are orthogonal, "
                        + "accumulating the rotations in an orthogonal matrix. Their lengths are the singular values. "
                        + "Normalize and sort the columns to obtain the factors; for a wide matrix, first "
                        + "decompose its transpose and exchange the left and right factors.";
                mlUsage = "SVD supports low-rank compression, denoising, pseudoinverses and PCA "
                        + "when applied to centered data.";
                break;
            case "eig":
                description = "For symmetric matrices, Jacobi rotations diagonalize the matrix and accumulate "
                        + "orthonormal eigenvectors. Otherwise, QR iteration finds eigenvalues and SVD nullspaces "
                        + "of $A-\\lambda I$ provide eigenvectors. A real decomposition requires real eigenvalues "
                        + "and enough independent eigenvectors; reconstruction is checked.";
                mlUsage = "Eigenvectors of covariance matrices give PCA directions; eigenvalues "
                        + "describe their variance, while Hessian eigenvalues reveal loss curvature.";
                break;
            default:
                throw new IllegalStateException("Missing explanation for " + explanation.function);
            }
            sb.append("\n\\begin{center}\\parbox{14cm}{\\textbf{")
                    .append(explanation.function).append(":} ").append(description);
            if (explanation.function.matches("rank|rref|basis|nullspace|span")) {
                sb.append(" Entries are scaled first; pivots at most $10^{-12}$ in magnitude are treated as zero.");
            }
            sb.append("\\par\\smallskip\\textbf{ML use:} ").append(mlUsage).append("}\\end{center}\n");
            if (explanation.scalarFunction != null) {
                String name = explanation.scalarFunctionName.replace("_", "\\_");
                String label = explanation.function.equals("grad") ? "\\nabla " + name
                        : "\\operatorname{" + explanation.function + "}(" + name + ")";
                sb.append("$$").append(label)
                        .append(" = ").append(explanation.scalarFunction.derivativeLaTeX(explanation.function))
                        .append(",\\qquad p = ").append(valueToLaTeX(explanation.input)).append("$$\n");
            }
            for (LinAlgScene.Step step : explanation.steps) {
                sb.append("$$\\begin{gathered}").append(step.operation).append("\\\\[3pt]")
                        .append(latexMatrix(step.rows, true,
                                explanation.function.equals("inv") ? explanation.input.getColumnCount() : 0))
                        .append("\\end{gathered}$$\n");
            }
        }
    }

    private static String factor(double number) {
        String text = LinAlgScene.formatNumber(number, false);
        return number < 0 ? "\\left(" + text + "\\right)" : text;
    }

    private void appendDecomposition(StringBuilder sb, Statement directive) {
        String label = expressionToLaTeX(directive.getArgument());
        Matrix m = directive.getValue().toMatrix();

        if (directive.getFunction().equals("svd")) {
            boolean explain = directive.getExplanations().stream().anyMatch(e -> e.function.equals("svd"));
            SingularValueDecomposition svd = new SingularValueDecomposition(m, explain);
            if (explain) {
                appendSvdSteps(sb, svd, m);
            }
            sb.append("$$").append(label).append(" = U \\, \\Sigma \\, V^{*} = ")
                    .append(latexMatrix(svd.getU(), true))
                    .append(latexMatrix(svd.getSigma(), true))
                    .append(latexMatrix(svd.getVTransposed(), true))
                    .append("$$\n");
        } else {
            EigenDecomposition eig = EigenDecomposition.tryCompute(m);
            if (eig.isAvailable()) {
                sb.append("$$").append(label).append(" = Q \\, \\Lambda \\, Q^{")
                        .append(eig.isSymmetric() ? "T" : "-1").append("} = ")
                        .append(latexMatrix(eig.getQ(), true))
                        .append(latexMatrix(eig.getLambda(), true))
                        .append(latexMatrix(eig.getQInverse(), true))
                        .append("$$\n");
            } else {
                sb.append("\\begin{center}\\parbox{14cm}{\\centering Eigenvalue decomposition of $").append(label)
                        .append("$ not available: ").append(eig.getFailureReason()).append(".}\\end{center}\n");
            }

        }
    }

    private static void appendSvdSteps(StringBuilder sb, SingularValueDecomposition svd, Matrix matrix) {
        int rows = matrix.getRowCount(), columns = matrix.getColumnCount();
        int k = Math.min(rows, columns);
        boolean transposed = rows < columns;
        sb.append("\\begin{center}\\parbox{14cm}{Thin SVD: for a real $")
                .append(rows).append("\\times ").append(columns).append("$ matrix $A$, $k=").append(k)
                .append("$, $U$ is $").append(rows).append("\\times ").append(k)
                .append("$, $\\Sigma$ is $").append(k).append("\\times ").append(k)
                .append("$, $V$ is $").append(columns).append("\\times ").append(k)
                .append("$, and $V^*=V^T$ is $").append(k).append("\\times ").append(columns)
                .append("$. A full SVD instead uses square $U$ and $V$ and a rectangular $\\Sigma$; "
                        + "the additional orthonormal directions multiply zero entries.}\\end{center}\n");
        sb.append("\\begin{center}\\parbox{14cm}{Initialize $W=B$, $Q=I$, where $B=")
                .append(transposed ? "A^T" : "A")
                .append("$. For columns $p,q$, compute $\\alpha=w_p^Tw_p$, $\\beta=w_q^Tw_q$, "
                        + "$\\gamma=w_p^Tw_q$. Choose a Jacobi rotation and update "
                        + "$w_p'=cw_p-sw_q$, $w_q'=sw_p+cw_q$; apply the same rotation to $Q$. "
                        + "Thus $W=BQ$. Values below are rounded for display.}\\end{center}\n");
        sb.append("$$\\zeta=\\frac{\\beta-\\alpha}{2\\gamma},\\quad "
                + "t=\\frac{\\operatorname{sgn}_+(\\zeta)}{|\\zeta|+\\sqrt{1+\\zeta^2}},\\quad "
                + "c=\\frac{1}{\\sqrt{1+t^2}},\\quad s=ct$$\n");
        sb.append("\\begin{center}\\parbox{14cm}{Here $\\operatorname{sgn}_+(0)=1$. "
                + "Skip pairs with $\\gamma=0$ or $|\\gamma|\\leq 10^{-14}\\sqrt{\\alpha\\beta}$. "
                + "Each sweep visits every column pair; the matrices below are snapshots at its end.}\\end{center}\n");
        double[][] initial = new double[transposed ? columns : rows][transposed ? rows : columns];
        for (int r = 0; r < initial.length; r++) {
            for (int c = 0; c < initial[r].length; c++) {
                initial[r][c] = transposed ? matrix.get(r, c) : matrix.get(c, r);
            }
        }
        sb.append("$$W_0=").append(latexMatrix(initial, true)).append("$$\n");
        int sweepNumber = 0;
        for (var sweep : svd.getSweeps()) {
            sb.append("\\begin{center}Jacobi sweep ").append(++sweepNumber).append("\\end{center}\n");
            for (var rotation : sweep.rotations()) {
                sb.append("$$ (p,q)=(").append(rotation.p()).append(",").append(rotation.q())
                        .append("),\\quad (\\alpha,\\beta,\\gamma)=(")
                        .append(LinAlgScene.formatNumber(rotation.alpha(), true)).append(",")
                        .append(LinAlgScene.formatNumber(rotation.beta(), true)).append(",")
                        .append(LinAlgScene.formatNumber(rotation.gamma(), true))
                        .append("),\\quad (c,s)=(").append(LinAlgScene.formatNumber(rotation.c(), true))
                        .append(",").append(LinAlgScene.formatNumber(rotation.s(), true)).append(")$$\n");
            }
            sb.append("$$W=").append(latexMatrix(sweep.w(), true)).append(",\\qquad Q=")
                    .append(latexMatrix(sweep.v(), true)).append("$$\n");
        }
        if (svd.getSweeps().isEmpty()) {
            sb.append("\\begin{center}The columns are already orthogonal; no rotations are needed.\\end{center}\n");
        }
        double[][] finalW = svd.getSweeps().isEmpty() ? initial
                : svd.getSweeps().get(svd.getSweeps().size() - 1).w();
        sb.append("$$ (\\|w_1\\|_2,\\ldots,\\|w_k\\|_2)=(");
        for (int c = 0; c < k; c++) {
            double norm = 0;
            for (double[] row : finalW) norm = Math.hypot(norm, row[c]);
            sb.append(c == 0 ? "" : ",").append(LinAlgScene.formatNumber(norm, true));
        }
        sb.append(")$$\n");
        sb.append("\\begin{center}\\parbox{14cm}{Take column lengths $\\sigma_j=\\|w_j\\|_2$, "
                + "sort them in descending order, and permute both $W$ and $Q$ accordingly. "
                + "Normalize nonzero columns: $P_j=w_j/\\sigma_j$. For numerically zero singular values, "
                + "complete $P$ with orthonormal columns instead of dividing by zero. ")
                .append(transposed ? "Since $A^T=P\\Sigma Q^T$, set $U=Q$ and $V=P$."
                        : "Set $U=P$ and $V=Q$.")
                .append("}\\end{center}\n");
        sb.append("$$U=").append(latexMatrix(svd.getU(), true)).append(",\\quad\\Sigma=")
                .append(latexMatrix(svd.getSigma(), true)).append(",\\quad V=")
                .append(latexMatrix(svd.getV(), true)).append("$$\n");
    }

    private static String expressionToLaTeX(String expr) {
        String formatted = expr
                .replace("^T", "^{T}")
                .replace("^-1", "^{-1}")
                .replace("*", " \\cdot ")
                .replaceAll("(?i)\\b(grad|jacobian|hessian|laplacian|divergence|curl|det|rank|trace|norm|rref|basis|nullspace|inv|transpose|span)(?=\\s*\\()",
                        "\\\\operatorname{$1}")
                .replaceAll("(?<![0-9.])([A-Za-z])_(\\w+)", "$1_{$2}");
        return MATRIX_LITERAL.matcher(formatted).replaceAll(match -> Matcher.quoteReplacement(
                valueToLaTeX(LinAlgScene.parse("M = " + match.group()).find("M").getValue())));
    }

    private static String valueToLaTeX(Value v) {
        if (!v.isScalar() && (v.getColumnCount() == 0 || v.getRowCount() == 0)) {
            return "\\left[\\,\\right]_{" + v.getRowCount() + "\\times " + v.getColumnCount() + "}";
        }
        return v.isScalar() ? LinAlgScene.formatNumber(v.getScalar(), true) : latexMatrix(v.getRows(), true);
    }

    private static String spanToLaTeX(Value basis) {
        if (basis.getColumnCount() == 0) {
            return "\\{\\mathbf{0}\\}";
        }
        StringBuilder sb = new StringBuilder("\\operatorname{span}\\left\\{");
        for (int c = 0; c < basis.getColumnCount(); c++) {
            double[][] column = new double[basis.getRowCount()][1];
            for (int r = 0; r < column.length; r++) {
                column[r][0] = basis.getRows()[r][c];
            }
            sb.append(c == 0 ? "" : ",\\;").append(latexMatrix(column, true));
        }
        return sb.append("\\right\\}").toString();
    }

    private static String latexMatrix(double[][] rows, boolean rounded) {
        return latexMatrix(rows, rounded, 0);
    }

    private static String latexMatrix(double[][] rows, boolean rounded, int dividerColumn) {
        StringBuilder sb = new StringBuilder();
        if (dividerColumn > 0) {
            sb.append("\\left(\\begin{array}{").append("c".repeat(dividerColumn)).append("|")
                    .append("c".repeat(dividerColumn)).append("}\n");
        } else {
            sb.append("\\begin{pmatrix}\n");
        }
        for (double[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                sb.append(i > 0 ? " & " : "").append(LinAlgScene.formatNumber(row[i], rounded));
            }
            sb.append(" \\\\\n");
        }
        return sb.append(dividerColumn > 0 ? "\\end{array}\\right)" : "\\end{pmatrix}").toString();
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        return new LaTeXPDF(
                LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM.replace(
                        "\\begin{document}", "\\usepackage{tikz}\n\\begin{document}")
                + toLaTeX()
                + LaTeXCommands.POSTAMBLE_STANDARD,
                pdfPath,
                this);
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }

    @Override
    public String getGermanName() {
        return "Lineare Algebra";
    }

    @ConversionMethod(plainText = false)
    public String togglePlot() {
        this.showPlot = !this.showPlot;
        return this.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String toggleFormulas() {
        this.showFormulas = !this.showFormulas;
        return this.createScriptFromInstance();
    }

    /** Appends {@code next = scalar * <last matrix>}. */
    @ConversionMethod(plainText = false)
    public String scalarMultiply(double scalar) {
        Statement last = scene.lastNonScalar();
        if (last == null) {
            throw new IllegalArgumentException("There is no matrix to multiply yet.");
        }
        return appendStatement(LinAlgScene.formatNumber(scalar, false) + " * " + operand(last.getName()));
    }

    /** Appends {@code next = left * right}; operands are names or expressions. */
    @ConversionMethod(plainText = false)
    public String multiply(String left, String right) {
        return appendStatement(operand(left) + " * " + operand(right));
    }

    /** Appends {@code next = basis^-1 * target}: the coordinates of target with respect to the basis (columns). */
    @ConversionMethod(plainText = false)
    public String changeBasis(String basis, String target) {
        return appendStatement(operand(basis) + "^-1 * " + operand(target));
    }

    private static String operand(String s) {
        String t = s.trim();
        return LinAlgScene.isName(t) ? t : "(" + t + ")";
    }

    private String appendStatement(String expression) {
        String name = scene.nextFreeName();
        this.scene = scene.withStatement(name, expression); // Validates the expression.
        return this.createScriptFromInstance();
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        try {
            addMethod(methods, "scalarMultiply", new Class<?>[] {Double.TYPE},
                    "Multiply with scalar", "Mit Skalar multiplizieren",
                    "Adds a new matrix: the scalar times the last matrix or vector.",
                    "Fügt eine neue Matrix hinzu: der Skalar mal die letzte Matrix bzw. der letzte Vektor.")
                    .setParameterExplanation(0, "The scalar.");
            MethodWrapper mul = addMethod(methods, "multiply", new Class<?>[] {String.class, String.class},
                    "Multiply", "Multiplizieren",
                    "Adds a new object defined as the product of two objects (names or expressions).",
                    "Fügt ein neues Objekt hinzu, definiert als Produkt zweier Objekte (Namen oder Ausdrücke).");
            mul.setParameterExplanation(0, "Left factor, e.g. B");
            mul.setParameterExplanation(1, "Right factor, e.g. A or v");
            MethodWrapper cb = addMethod(methods, "changeBasis", new Class<?>[] {String.class, String.class},
                    "Change basis", "Basiswechsel",
                    "Adds the coordinates of an object with respect to a new basis (the columns of a square matrix).",
                    "Fügt die Koordinaten eines Objekts bezüglich einer neuen Basis (die Spalten einer quadratischen Matrix) hinzu.");
            cb.setParameterExplanation(0, "Basis matrix (columns are the new basis vectors), e.g. B");
            cb.setParameterExplanation(1, "Vector or matrix to express in the new basis, e.g. v");
            addMethod(methods, "togglePlot", new Class<?>[0],
                    "Toggle 2D plot", "2D-Plot umschalten",
                    "Shows or hides a plot of all 2D/3D vectors and of the unit square/cube mapped by all 2x2/3x3 matrices.",
                    "Zeigt oder verbirgt einen Plot aller 2D-Vektoren und des Einheitsquadrats unter allen 2x2-Matrizen.");
            addMethod(methods, "toggleFormulas", new Class<?>[0],
                    "Toggle formulas", "Formeln umschalten",
                    "Shows or hides the formulas above the plot. They are always shown while the plot is off.",
                    "Zeigt oder verbirgt die Formeln über dem Plot. Solange der Plot aus ist, werden sie immer gezeigt.");
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return methods;
    }

    private MethodWrapper addMethod(HashMap<String, MethodWrapper> methods, String method, Class<?>[] types,
            String name, String nameG, String tooltip, String tooltipG) throws NoSuchMethodException {
        MethodWrapper mw = new MethodWrapper(
                this.getClass().getMethod(method, types),
                LinAlg.class, // Target script class. Important to set correctly!
                this, tooltip, tooltipG, name, nameG);
        methods.put(name, mw);
        return mw;
    }

    @Override
    public String createScriptFromInstance() {
        return SCRIPT_PREAMBLE + "\n" + scene.toScript() + this.generateCompleteDeclarationsBlock();
    }
}
