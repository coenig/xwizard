package veryFastPDF.algorithms.linearTrans;

import java.util.HashMap;
import java.util.List;
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

    /** Declaration variable: iff true, geometric, function and statistical views are plotted. */
    private boolean showPlot = false;

    /** 
     * Declaration variable: iff true, the formulas are written above the plot. They are always 
     * written if the plot is off, as there would be nothing left to show otherwise.
     */
    private boolean showFormulas = true;

    /** Selected marker (URL-encoded for safe declaration roundtrips), or empty for the whole scene. */
    private String operation = "";
    /** -1 displays the completed multiplication; zero displays its initially empty result. */
    private int step = -1;
    private String mlpLayers = "";
    private String mlpInput = "x";
    private String mlpTarget = "target";
    private String mlpLoss = "mse";
    private double mlpLearningRate = 0.1;
    private int mlpStep = -1;
    /** Matrix names X,Wq,Wk,Wv[,Wo]; empty disables the self-attention view. */
    private String attention = "";
    private boolean attnCausal = false;
    private String attnTokens = "";
    private int attnStep = -1;

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
                SCRIPT_PREAMBLE + "\nA = [4 1 0; 2 3 0; 0 0 1]\nvalues = eigenvalues(A)\nvectors = eigenvectors(A)\nevd(A)\n",
                SCRIPT_PREAMBLE + "\nA = [0 -1 0; 1 0 0; 0 0 5]\nevd(A)\n",
                SCRIPT_PREAMBLE + "\nA = [0 2; 1 3]\nDet(A)\nRank(A)\nBasis(A)\nInv(A)\n",
                SCRIPT_PREAMBLE + "\nf(x, y) = x^2 + 2*x*y + 3*y^2\np = (1, 2)\ng = grad(f, p)\nGrad(f, p)\n"
                        + DECL_BEG_TAG + "\nshowPlot=true\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\nf(x, y) = x^2 + 2*x*y + 3*y^2\np = (1, 2)\nHessian(f, p)\nLaplacian(f, p)\n"
                        + "F(x, y) = (x*y, sin(x))\nJacobian(F, p)\nDivergence(F, p)\n"
                        + "G(x, y, z) = (-y, x, 0)\nCurl(G, (1, 2, 3))\n",
                SCRIPT_PREAMBLE + "\ndata = [2 4 4 4 5 5 7 9]\nmu = mean(data)\n"
                        + "v = variance(data)\nsigma = std(data)\nMean(data)\nVariance(data)\nStd(data)\n"
                        + "A = [1 0; 0 2]\nscaled = sigma * A\n"
                        + DECL_BEG_TAG + "\nshowPlot=true\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\ntp = 80\nfp = 20\nfn = 10\np = precision(tp, fp)\nr = recall(tp, fn)\n"
                        + "Precision(tp, fp)\nRecall(tp, fn)\n"
                        + "// H: condition present; E: positive test\nprior = 0.01\nlikelihood = 0.9\n"
                        + "evidence = 0.9 * prior + 0.05 * (1 - prior)\n"
                        + "posterior = bayes(prior, likelihood, evidence)\nBayes(prior, likelihood, evidence)\n",
                SCRIPT_PREAMBLE + "\n// Successes in 10 independent trials, each with success probability 0.3\n"
                        + "b = binomial(10, 0.3, 3)\nBinomial(10, 0.3, 3)\n"
                        + "// Event count in a fixed interval with mean rate 4\n"
                        + "q = poisson(4, 2)\nPoisson(4, 2)\n"
                        + DECL_BEG_TAG + "\nshowPlot=true;\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\n// One example per column; two layers, explicit chain rule\n"
                        + "x = (1, 2)\ntarget = [1]\nW1 = [0.1 0.2; -0.3 0.4]\nb1 = (0, 0)\n"
                        + "W2 = [0.5 -0.2]\nb2 = [0.1]\n"
                        + "z1 = Dense(W1, x, b1)\na1 = Tanh(z1)\nz2 = Dense(W2, a1, b2)\n"
                        + "a2 = Sigmoid(z2)\nloss = Mse(a2, target)\n"
                        + "ga2 = Msegrad(a2, target)\ngz2 = Sigmoidback(z2, ga2)\n"
                        + "gW2 = Densegradw(gz2, a1)\ngb2 = Densegradb(gz2)\n"
                        + "ga1 = Densegradx(W2, gz2)\ngz1 = Tanhback(z1, ga1)\n"
                        + "gW1 = Densegradw(gz1, x)\ngb1 = Densegradb(gz1)\ngx = Densegradx(W1, gz1)\n"
                        + "// Compute all gradients before updating any weights\neta = 0.1\n"
                        + "W1new = W1 - eta*gW1\nb1new = b1 - eta*gb1\n"
                        + "W2new = W2 - eta*gW2\nb2new = b2 - eta*gb2\n"
                        + "newloss = mse(sigmoid(dense(W2new, tanh(dense(W1new,x,b1new)), b2new)), target)\n",
                SCRIPT_PREAMBLE + "\n// Two examples (columns), two inputs, three classes (rows)\n"
                        + "X = [1 0; 0 1]\nW = [0.2 -0.1; -0.3 0.4; 0.1 0.2]\nb = (0, 0, 0)\n"
                        + "T = [1 0; 0 1; 0 0]\nZ = Dense(W, X, b)\nA = Softmax(Z)\n"
                        + "loss = Crossentropy(Z, T)\ngZ = Crossentropygrad(Z, T)\n"
                        + "gW = Densegradw(gZ, X)\ngb = Densegradb(gZ)\n"
                        + "// Fused loss already differentiates softmax: do not use softmaxback here\n"
                        + "Wnew = W - 0.1*gW\nbnew = b - 0.1*gb\nnewloss = crossentropy(dense(Wnew,X,bnew), T)\n",
                SCRIPT_PREAMBLE + "\n// A preactivation z is the weighted sum BEFORE the activation\n"
                        + "z = [-5 -2 -1 0 1 2 5]\nRelu(z)\nSigmoid(z)\nTanh(z)\n"
                        + "// One column = one example, four rows = four competing classes\n"
                        + "scores = (2, 1, 0, -1)\nSoftmax(scores)\n"
                        + DECL_BEG_TAG + "\nshowPlot=true;\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\nA = [1 2 3; 4 5 6]\nB = [7 8; 9 10; 11 12]\n"
                        + "C = A * @product B\n" + DECL_BEG_TAG
                        + "\noperation=product;\nstep=0;\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\nx=(1,2)\ntarget=[0.8]\n"
                        + "W1=[0.1 0.2;-0.3 0.4]\nb1=(0,0)\n"
                        + "W2=[0.5 -0.2;0.3 0.1]\nb2=(0.1,-0.1)\n"
                        + "W3=[0.7 -0.4]\nb3=[0.05]\n" + DECL_BEG_TAG
                        + "\nmlpLayers=#W1,b1,tanh;W2,b2,relu;W3,b3,sigmoid#;\n"
                        + "mlpInput=x;\nmlpTarget=target;\nmlpLoss=mse;\nmlpLearningRate=0.1;\nmlpStep=0;\nsvgHeight=1000;\n"
                        + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\n// One sample per row; columns are the intercept and powers of x.\n"
                        + "// The optional responses y fit and plot the least-squares polynomial.\n"
                        + "x=(-3,-2,-1,0,1,2,3)\ny=(8.5,3.6,1.4,0.2,1.3,4.6,8.8)\nPhi=DesignMatrix(x,2,y)\n"
                        + DECL_BEG_TAG + "\nshowPlot=true;\n" + DECL_END_TAG,
                SCRIPT_PREAMBLE + "\n// One token per row: three 3-dimensional token embeddings"
                        + "\nX=[1 0 1;0 2 0;1 1 0]\nWq=[1 0;0 1;1 1]\nWk=[0 1;1 0;1 1]\nWv=[1 0;0 1;1 1]\n" + DECL_BEG_TAG
                        + "\nattention=#X,Wq,Wk,Wv#;\nattnTokens=#the,cat,sat#;\nattnCausal=false;\nattnStep=0;\nsvgHeight=900;\n"
                        + DECL_END_TAG};
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
        case 9:
            return english ? "Statistics: mean, variance and standard deviation"
                    : "Statistik: Mittelwert, Varianz und Standardabweichung";
        case 10:
            return english ? "Classification: precision, recall and Bayes' formula"
                    : "Klassifikation: Precision, Recall und Bayes-Formel";
        case 11:
            return english ? "Discrete distributions: binomial and Poisson"
                    : "Diskrete Verteilungen: Binomial und Poisson";
        case 12:
            return english ? "Neural network: two-layer forward pass and backpropagation"
                    : "Neuronales Netz: zwei Schichten und Backpropagation";
        case 13:
            return english ? "Neural network: batched softmax classification"
                    : "Neuronales Netz: Softmax-Klassifikation im Batch";
        case 14:
            return english ? "ANN basics: activation curves, slopes and class shares"
                    : "ANN-Grundlagen: Aktivierungskurven, Steigungen und Klassenanteile";
        case 15:
            return english ? "Marked matrix multiplication: steps and animation"
                    : "Markierte Matrixmultiplikation: Schritte und Animation";
        case 16:
            return english ? "MLP: forward pass, backpropagation and SGD animation"
                    : "MLP: Vorwaertslauf, Backpropagation und SGD-Animation";
        case 17:
            return english ? "Polynomial regression: design matrix and features"
                    : "Polynomiale Regression: Designmatrix und Merkmale";
        case 18:
            return english ? "Transformer: self-attention step animation"
                    : "Transformer: Self-Attention-Schritt als Animation";
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
        operation = "";
        step = -1;
        mlpLayers = "";
        mlpInput = "x";
        mlpTarget = "target";
        mlpLoss = "mse";
        mlpLearningRate = 0.1;
        mlpStep = -1;
        attention = "";
        attnCausal = false;
        attnTokens = "";
        attnStep = -1;
        for (var declaration : extractNVPairs(script, BEGIN_LITERAL, END_LITERAL,
                END_VALUE, DECL_BEG_TAG, DECL_END_TAG)) {
            if (declaration.get(0).equals("step") || declaration.get(0).equals("mlpStep")
                    || declaration.get(0).equals("attnStep")) {
                try {
                    Integer.parseInt(declaration.get(1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Animation step must be an integer.", e);
                }
            }
            if (declaration.get(0).equals("mlpLearningRate")) {
                try {
                    Double.parseDouble(declaration.get(1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("MLP learning rate must be a number.", e);
                }
            }
        }
        this.applyDeclarationsAndPreprocessors(script, father, 0);
        this.scene = LinAlgScene.parse(this.getScriptWithoutPrepAndDeclAndPreamble());
        if (!mlpLayers.isEmpty()) {
            if (!operation.isEmpty() || step != -1) {
                throw new IllegalArgumentException("Select either an MLP or a marked matrix multiplication, not both.");
            }
            getMlpTrace().getStep(selectedMlpStep());
        } else if (mlpStep != -1) {
            throw new IllegalArgumentException("Define mlpLayers before selecting an MLP step.");
        }
        if (!attention.isEmpty()) {
            if (!mlpLayers.isEmpty() || !operation.isEmpty() || step != -1) {
                throw new IllegalArgumentException("Select only one of attention, an MLP or a marked matrix multiplication.");
            }
            getAttentionTrace().getStage(selectedAttentionStep());
        } else if (attnStep != -1) {
            throw new IllegalArgumentException("Define attention before selecting an attention step.");
        }
        if (!operation.isEmpty()) {
            selectedMultiplication().getStep(selectedStep());
            operation = encodeOperation(decodeOperation());
        } else if (step != -1) {
            throw new IllegalArgumentException("Select an operation marker before selecting a multiplication step.");
        }
    }

    public LinAlgScene getScene() {
        return scene;
    }

    /** LaTeX math-mode representation of all statements and the requested decompositions. */
    public String toLaTeX() {
        if (!attention.isEmpty()) {
            return AttentionAnimation.toLaTeX(getAttentionTrace(), selectedAttentionStep());
        }
        if (!mlpLayers.isEmpty()) {
            return MlpAnimation.toLaTeX(getMlpTrace(), selectedMlpStep());
        }
        if (!operation.isEmpty()) {
            return multiplicationToLaTeX();
        }
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
                sb.append("\\begin{center}Nothing to plot: define a 2D/3D vector or matrix, "
                        + "a gradient, statistics on data, a distribution, or a neural activation.\\end{center}\n");
            }
        }
        return sb.toString();
    }

    private static String encodeOperation(String name) {
        return java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String decodeOperation() {
        return java.net.URLDecoder.decode(operation, java.nio.charset.StandardCharsets.UTF_8);
    }

    private LinAlgScene.Operation selectedOperation() {
        if (operation.isEmpty()) throw new IllegalArgumentException("No operation marker selected.");
        return scene.getOperation(decodeOperation());
    }

    private MatrixMultiplication selectedMultiplication() {
        return selectedOperation().multiplication();
    }

    private int selectedStep() {
        if (step < -1) throw new IllegalArgumentException("Multiplication step must be -1 or a nonnegative integer.");
        return step == -1 ? selectedMultiplication().getStepCount() : step;
    }

    private String multiplicationToLaTeX() {
        LinAlgScene.Operation selected = selectedOperation();
        Value left = selected.getLeft(), right = selected.getRight(), result = selected.getResult();
        MatrixMultiplication trace = selected.multiplication();
        MatrixMultiplication.State state = trace.getStep(selectedStep());
        String layout = multiplicationFactorToLaTeX(left, -1, -1, "black") + " \\cdot "
                + multiplicationFactorToLaTeX(right, -1, -1, "black") + " = "
                + multiplicationFactorToLaTeX(result, -1, -1, "black");
        StringBuilder sb = new StringBuilder("\\begingroup\\setbox0=\\hbox{$")
                .append(layout).append("$}\\dimen0=\\wd0")
                .append("\\ifdim\\dimen0<14cm\\dimen0=14cm\\fi")
                .append("\\setbox0=\\hbox{Matrix multiplication: step ").append(trace.getStepCount())
                .append(" of ").append(trace.getStepCount())
                .append(". Unfilled entries are shown as $\\square$.}")
                .append("\\ifdim\\wd0>\\dimen0\\dimen0=\\wd0\\fi")
                .append("\\edef\\xwizardframewidth{\\the\\dimen0}")
                .append("\\begin{minipage}{\\xwizardframewidth}\n")
                .append("\\begin{center}Matrix multiplication: step ")
                .append(state.step()).append(" of ").append(trace.getStepCount())
                .append(". Unfilled entries are shown as $\\square$.\\end{center}\n$$")
                .append(multiplicationFactorToLaTeX(left, state.row(), state.term(), "blue")).append(" \\cdot ")
                .append(multiplicationFactorToLaTeX(right, state.term(), state.column(), "red")).append(" = ");
        if (result.getRowCount() == 0 || result.getColumnCount() == 0) {
            sb.append(valueToLaTeX(result));
        } else {
            sb.append(multiplicationMatrixSpacing(result)).append("\\begin{pmatrix}\n");
            for (int r = 0; r < state.cells().length; r++) {
                Double[] row = state.cells()[r];
                for (int c = 0; c < row.length; c++) {
                    String entry = row[c] == null ? "\\square" : LinAlgScene.formatNumber(row[c], true);
                    String content = r == state.row() && c == state.column() && state.isFilled(r, c)
                            ? circledFactor(entry, "green!50!black") : entry;
                    sb.append(c == 0 ? "" : " & ").append(spacedMultiplicationEntry(content, c));
                }
                sb.append(" \\\\\n");
            }
            sb.append("\\end{pmatrix}\\endgroup");
        }
        sb.append("$$\n");
        if (state.term() >= 0) {
            int r = state.row(), c = state.column(), k = state.term();
            String partialSum = LinAlgScene.formatNumber(state.partialSum(), true);
            sb.append("$$s_{").append(r + 1).append(",").append(c + 1).append("}^{(")
                    .append(k + 1).append(")} = ")
                    .append(factor(state.previousSum())).append(" + ")
                    .append(circledFactor(factor(left.getRows()[r][k]), "blue")).append(" \\cdot ")
                    .append(circledFactor(factor(right.getRows()[k][c]), "red")).append(" = ")
                    .append(state.isFilled(r, c) ? circledFactor(partialSum, "green!50!black") : partialSum)
                    .append("$$\n")
                    .append("\\begin{center}Row ").append(r + 1).append(", column ").append(c + 1)
                    .append(", term ").append(k + 1).append(" of ")
                    .append(left.getColumnCount()).append(". ")
                    .append(state.isFilled(r, c) ? "Output entry complete." : "Running sum; output entry still unfilled.")
                    .append("\\end{center}\n");
        }
        return sb.append("\\end{minipage}\\endgroup\n").toString();
    }

    static String circledFactor(String latex, String color) {
        return "\\tikz[baseline=(entry.base)]{\\node[draw=" + color
                + ",circle,inner sep=1pt,line width=0.7pt] (entry) {$\\strut " + latex + "$};}";
    }

    private static String multiplicationFactorToLaTeX(Value value, int activeRow, int activeColumn, String color) {
        if (value.getRowCount() == 0 || value.getColumnCount() == 0) return valueToLaTeX(value);
        StringBuilder sb = new StringBuilder(multiplicationMatrixSpacing(value)).append("\\begin{pmatrix}\n");
        for (int r = 0; r < value.getRowCount(); r++) {
            for (int c = 0; c < value.getColumnCount(); c++) {
                String entry = LinAlgScene.formatNumber(value.getRows()[r][c], true);
                String content = r == activeRow && c == activeColumn ? circledFactor(entry, color) : entry;
                sb.append(c == 0 ? "" : " & ").append(spacedMultiplicationEntry(content, c));
            }
            sb.append(" \\\\\n");
        }
        return sb.append("\\end{pmatrix}\\endgroup").toString();
    }

    /** Measure the final entries, including circles, identically in every frame. */
    static String multiplicationMatrixSpacing(Value value) {
        StringBuilder sb = new StringBuilder("\\begingroup\\dimen2=0pt\\dimen4=0pt\n");
        for (int c = 0; c < value.getColumnCount(); c++) {
            sb.append("\\dimen0=0pt\n");
            java.util.Set<String> entries = new java.util.LinkedHashSet<>();
            entries.add("\\square");
            for (double[] row : value.getRows()) entries.add(LinAlgScene.formatNumber(row[c], true));
            for (String entry : entries) {
                sb.append("\\setbox0=\\hbox{$").append(circledFactor(entry, "black")).append("$}")
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

    static String spacedMultiplicationEntry(String content, int column) {
        return "\\makebox[\\csname xwizardwidth" + column + "\\endcsname][c]{$"
                + "\\vrule width0pt height\\xwizardheight depth\\xwizarddepth " + content + "$}";
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
            String valueLaTeX = s.getValue().isScalar()
                    && (!s.getStatisticalInputs().isEmpty() || s.hasProbabilityCalculation() || s.hasNeuralCalculation())
                    ? statisticNumber(java.math.BigDecimal.valueOf(s.getValue().getScalar()))
                    : valueToLaTeX(s.getValue());
            if (s.isDirective()) {
                if (s.getFunction().matches("svd|evd|eig")) {
                    appendDecomposition(sb, s);
                } else {
                    sb.append("$$").append(expressionToLaTeX(s.getExpression())).append(" = ")
                            .append(simpleFormula(s))
                            .append(s.getFunction().equals("span") ? spanToLaTeX(s.getValue())
                                    : valueLaTeX)
                            .append("$$\n");
                }
                continue;
            }
            sb.append("$$").append(s.getName()).append(" = ");
            if (!LITERAL.matcher(s.getExpression()).matches()) {
                sb.append(expressionToLaTeX(s.getExpression())).append(" = ");
            }
            sb.append(simpleFormula(s));
            sb.append(valueLaTeX).append("$$\n");
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
            if (NeuralNetworkFunctions.supports(explanation.function)) {
                sb.append("\\begin{center}\\parbox{14cm}{\\textbf{").append(explanation.function)
                        .append(":} ").append(NeuralNetworkFunctions.beginnerExplanation(explanation.function))
                        .append("\\par\\smallskip\\textbf{Calculation:} ")
                        .append(NeuralNetworkFunctions.description(explanation.function))
                        .append(" Here $G_V=\\partial L/\\partial V$ denotes the gradient of the final scalar "
                                + "loss $L$ with respect to an intermediate value $V$; an upstream gradient "
                                + "arrives from the later part of the network.")
                        .append(" A derivative is a local slope: it estimates the output change for a tiny "
                                + "input change. Backpropagation applies the chain rule: "
                                + "$\\partial L/\\partial z=(\\partial L/\\partial a)(\\partial a/\\partial z)$. "
                                + "It computes sensitivities, not new weights. Gradient descent then takes "
                                + "a separate small step opposite the gradient; the positive learning rate "
                                + "$\\eta$ controls its size. A step that is too large need not lower the loss.")
                        .append(" Intermediate values are rounded for display only.")
                        .append("\\par\\smallskip\\textbf{ML use:} ")
                        .append(NeuralNetworkFunctions.mlUsage(explanation.function)).append("}\\end{center}\n");
                for (LinAlgScene.Step step : explanation.steps) appendAlgorithmStep(sb, step, 0);
                continue;
            }
            String description;
            String mlUsage;
            switch (explanation.function) {
            case "binomial":
                description = "A binomial random variable counts successes in $n$ independent Bernoulli trials "
                        + "with the same success probability $p$. "
                        + "$P(X=k)=\\binom{n}{k}p^k(1-p)^{n-k}$ for integer $0\\le k\\le n$. "
                        + "The coefficient counts which $k$ trials succeed; the other factors give each "
                        + "arrangement's probability. Outside the support the probability is zero.";
                mlUsage = "Binomial models describe binary outcome counts and repeated classifier successes "
                        + "under independent trials with constant success probability. Correlated outcomes "
                        + "or varying probabilities require a different model.";
                break;
            case "poisson":
                description = "A Poisson random variable counts events in a fixed exposure interval, "
                        + "with mean $\\lambda\\ge0$: $P(X=k)=e^{-\\lambda}\\lambda^k/k!$. "
                        + "The homogeneous Poisson-process interpretation assumes independent events "
                        + "at a constant rate; multiply rate by exposure to obtain $\\lambda$. "
                        + "The distribution has support $k=0,1,2,\\ldots$, with mean and variance $\\lambda$.";
                mlUsage = "Poisson models describe event counts such as requests, clicks or arrivals; "
                        + "Poisson regression predicts their expected counts. Overdispersion "
                        + "(variance above the mean) can call for a negative-binomial model.";
                break;
            case "designmatrix":
            {
                int degree = (int) explanation.arguments.get(1).getScalar();
                int sampleCount = Math.max(explanation.input.getRowCount(), explanation.input.getColumnCount());
                description = "For polynomial regression, build the Vandermonde design matrix "
                        + "$\\Phi_{ij}=x_i^{j-1}$ for $i=1,\\ldots," + sampleCount
                        + "$ and $j=1,\\ldots," + (degree + 1) + "$. Each row is one sample; the first column "
                        + "is all ones for the intercept, followed by $x,x^2,\\ldots,x^{" + degree + "}$.";
                mlUsage = "Polynomial regression is linear in its fitted coefficients even though its features "
                        + "are powers of the input. Centering/scaling samples can improve conditioning, especially "
                        + "at high degree; this function uses raw powers.";
                if (explanation.arguments.size() == 3) {
                    description += " With responses $y$, the least-squares coefficients $\\hat\\beta$ minimize "
                            + "$\\|\\Phi\\beta-y\\|^2$; the fitted polynomial approximates the data and passes "
                            + "through every point only when the degree is at least $n-1$.";
                }
                break;
            }
            case "precision":
                description = "Precision asks: of the predicted positives, how many are truly positive? "
                        + "$\\mathrm{precision}=\\frac{TP}{TP+FP}$. "
                        + "$TP$ counts true positives and $FP$ counts false positives. "
                        + "With no predicted positives, the ratio is undefined.";
                mlUsage = "Precision measures the reliability of positive predictions, useful when false "
                        + "alarms are costly, such as spam filtering or fraud alerts. It depends on the "
                        + "decision threshold and class prevalence; it is not overall accuracy.";
                break;
            case "recall":
                description = "Recall asks: of the actual positives, how many were detected? "
                        + "$\\mathrm{recall}=\\frac{TP}{TP+FN}$. "
                        + "$TP$ counts true positives and $FN$ counts false negatives. "
                        + "With no actual positives, the ratio is undefined.";
                mlUsage = "Recall (sensitivity or true-positive rate) measures how many positive cases a "
                        + "classifier finds, useful when missed cases are costly. Changing the decision "
                        + "threshold typically trades recall against precision.";
                break;
            case "bayes":
                description = "Bayes' formula updates a hypothesis $H$ after observing evidence $E$: "
                        + "$P(H\\mid E)=\\frac{P(E\\mid H)P(H)}{P(E)}$. "
                        + "Arguments are prior $P(H)$, likelihood $P(E\\mid H)$ and evidence $P(E)>0$, "
                        + "in that order. The likelihood and posterior reverse the conditioning; "
                        + "they are not interchangeable. Evidence is supplied, not inferred.";
                mlUsage = "Bayes' formula underpins Bayesian inference and probabilistic classifiers. "
                        + "A rare positive class can still have a low posterior probability after a "
                        + "positive test, even when sensitivity is high: the base rate matters.";
                break;
            case "mean":
                description = "The arithmetic mean summarizes the center of equally weighted observations: "
                        + "$\\mu=\\frac{1}{n}\\sum_i x_i$. Add the observations, then divide by their count.";
                mlUsage = "Means center features before PCA and summarize average predictions or errors; "
                        + "fit preprocessing statistics on training data only to avoid leakage.";
                break;
            case "variance":
            case "std":
                description = "First compute the mean, then deviations and squared deviations. "
                        + "Population variance is $\\sigma^2=\\frac{1}{n}\\sum_i(x_i-\\mu)^2$; "
                        + "standard deviation is $\\sigma=\\sqrt{\\sigma^2}$, in the original data units. "
                        + "The denominator is $n$, not the sample estimator's $n-1$.";
                mlUsage = "Variance measures feature spread and standard deviation sets the scale for "
                        + "standardization. Constant features have zero spread and must not be divided by zero. "
                        + "Fit preprocessing statistics on training data only.";
                break;
            case "jacobian":
                description = "Differentiate every output component with respect to every input variable. "
                        + "Inputs are the declared arguments (for example $x,y$), not the function's returned values. "
                        + "Outputs are the components of the returned vector (for example $F_1=xy$, "
                        + "$F_2=\\sin x$). A row is not an output value: row $i$ contains the sensitivities "
                        + "of output $F_i$ to all inputs. Column $j$ varies input $u_j$ in declared parameter order, "
                        + "holding the other inputs fixed: $J_{ij}=\\partial F_i/\\partial u_j$. "
                        + "With $m$ outputs and $n$ inputs, $J$ is $m\\times n$. "
                        + "The point supplies input coordinates at which these derivatives are evaluated. "
                        + "For a small input change, $\\delta F\\approx J(p)\\,\\delta u$.";
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
                        + "This is the trace of the Hessian. $S_i$ is the running total of the first $i$ "
                        + "pure second derivatives, not the current derivative alone; start with $S_0=0$ "
                        + "and add each new term. Then substitute the evaluation point.";
                mlUsage = "The continuous Laplacian appears in diffusion models and physics-informed learning. "
                        + "Graph-based learning uses a related, discrete graph Laplacian, not this operator.";
                break;
            case "divergence":
                description = "Differentiate component $i$ with respect to variable $i$ and sum: "
                        + "$\\nabla\\cdot F=\\sum_i\\partial_i F_i$. This is the trace of the Jacobian. "
                        + "$S_i$ is the running total after $i$ terms, starting from $S_0=0$.";
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
                description = "The nullspace (kernel) is $\\ker(A)=\\{x\\mid Ax=0\\}$: all input directions "
                        + "mapped to the zero vector. It is a subspace of the input space, with one coordinate "
                        + "per column of $A$, not per row. Invertible row operations preserve the solutions "
                        + "of $Ax=0$, so reduce to RREF. Nonpivot columns represent free variables. Set each free variable "
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
            case "eigenvalues":
                description = "An eigenvalue $\\lambda$ satisfies $Av=\\lambda v$ for some nonzero vector $v$. "
                        + "Equivalently, $\\det(A-\\lambda I)=0$. Symmetric matrices use Jacobi rotations; "
                        + "other matrices use QR iteration. Return real eigenvalues in descending order, "
                        + "repeating them according to algebraic multiplicity. A complete eigenvector basis "
                        + "is not required.";
                mlUsage = "Covariance eigenvalues measure variance along principal directions; "
                        + "Hessian eigenvalues describe loss curvature and conditioning.";
                break;
            case "eigenvectors":
                description = "Eigenvectors are nonzero directions satisfying $Av=\\lambda v$. For symmetric "
                        + "matrices use Jacobi rotations; otherwise find a basis of $\\ker(A-\\lambda I)$ "
                        + "using SVD for each real eigenvalue. Return unit-length vectors as columns, "
                        + "grouped by descending eigenvalue. Signs and bases within repeated eigenspaces are not unique. "
                        + "For diagonalizable matrices, column $j$ matches entry $j$ of eigenvalues. "
                        + "A defective matrix returns fewer columns: one basis per eigenspace, not duplicate vectors. "
                        + "In that case algebraic multiplicities in eigenvalues exceed the corresponding "
                        + "numbers of eigenvector columns. Nonsymmetric eigenvectors need not be orthogonal.";
                mlUsage = "Covariance eigenvectors give PCA directions and interpretable feature combinations.";
                break;
            case "evd":
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
            sb.append(" Numerical intermediate values are rounded for display, not during computation.")
                    .append("\\par\\smallskip\\textbf{ML use:} ").append(mlUsage).append("}\\end{center}\n");
            if (explanation.scalarFunction != null) {
                String name = explanation.scalarFunctionName.replace("_", "\\_");
                double[] point = new double[explanation.input.getRowCount()];
                for (int i = 0; i < point.length; i++) point[i] = explanation.input.getRows()[i][0];
                for (String step : explanation.scalarFunction.differentiationSteps(explanation.function, point)) {
                    sb.append("$$").append(step).append("$$\n");
                }
                String label = explanation.function.equals("grad") ? "\\nabla " + name
                        : "\\operatorname{" + explanation.function + "}(" + name + ")";
                sb.append("$$").append(label)
                        .append(" = ").append(explanation.scalarFunction.derivativeLaTeX(explanation.function))
                        .append(",\\qquad p = ").append(valueToLaTeX(explanation.input)).append("$$\n");
            }
            for (LinAlgScene.Step step : explanation.steps) {
                appendAlgorithmStep(sb, step,
                        explanation.function.equals("inv") ? explanation.input.getColumnCount() : 0);
            }
            appendElementarySteps(sb, explanation);
            if (explanation.function.equals("nullspace")) {
                appendNullspaceSteps(sb, explanation);
            }
        }
    }

    private static void appendAlgorithmStep(StringBuilder sb, LinAlgScene.Step step, int divider) {
        if (step.rows.length == 0) {
            sb.append("$$").append(step.operation).append("$$\n");
            return;
        }
        if (step.operation.contains("[Q\\mid I]")) divider = step.rows[0].length / 2;
        sb.append("$$\\begin{gathered}").append(step.operation).append("\\\\[3pt]")
                .append(latexMatrix(step.rows, true, divider)).append("\\end{gathered}$$\n");
    }

    private static void appendElementarySteps(StringBuilder sb, LinAlgScene.Explanation explanation) {
        Value input = explanation.input;
        double[][] rows = input.getRows();
        switch (explanation.function) {
        case "binomial":
        case "poisson":
            appendDistributionSteps(sb, explanation);
            break;
        case "designmatrix": {
            int degree = (int) explanation.arguments.get(1).getScalar();
            int count = Math.max(input.getRowCount(), input.getColumnCount());
            PolynomialDesignMatrix.Request fit = explanation.arguments.size() == 3
                    ? PolynomialDesignMatrix.create(explanation.arguments).plot : null;
            double[] targets = fit == null ? null : fit.targets();
            for (int i = 0; i < count; i++) {
                double x = input.getRowCount() == 1 ? input.getRows()[0][i] : input.getRows()[i][0];
                StringBuilder row = new StringBuilder("\\left(");
                for (int power = 0; power <= degree; power++) {
                    row.append(power == 0 ? "" : ",\\ ")
                            .append(number(Math.pow(x, power)));
                }
                sb.append("$$\\text{Sample }x_{").append(i + 1).append("}=").append(number(x))
                        .append(":\\quad (1,x_i,\\ldots,x_i^{").append(degree).append("})=")
                        .append(row).append("\\right)");
                if (fit != null) {
                    sb.append(",\\quad y_{").append(i + 1).append("}=").append(number(targets[i]))
                            .append(",\\ \\hat y_{").append(i + 1).append("}=").append(number(fit.predict(x)));
                }
                sb.append("$$\n");
            }
            sb.append("$$\\Phi_{ij}=x_i^{j-1},\\qquad \\text{polynomial prediction }\\hat y=\\Phi\\beta$$\n");
            if (fit != null) {
                double[] beta = fit.coefficients();
                StringBuilder coefficients = new StringBuilder();
                for (int k = 0; k < beta.length; k++) {
                    coefficients.append(k == 0 ? "" : ",\\ ")
                            .append(PolynomialDesignMatrix.Request.coefficientLatex(beta[k]));
                }
                sb.append("$$\\hat\\beta=\\arg\\min_\\beta\\|\\Phi\\beta-y\\|^2=(").append(coefficients)
                        .append(")^T$$\n$$\\hat y(x)=").append(fit.polynomialLatex())
                        .append(",\\qquad \\text{SSE}=\\sum_i(y_i-\\hat y_i)^2=")
                        .append(number(fit.sumOfSquaredErrors())).append("$$\n");
            }
            break;
        }
        case "precision":
        case "recall":
        case "bayes":
            appendProbabilitySteps(sb, explanation);
            break;
        case "mean":
        case "variance":
        case "std":
            appendStatisticsSteps(sb, explanation);
            break;
        case "trace":
            double sum = 0;
            sb.append("$$s_0=0$$\n");
            for (int i = 0; i < rows.length; i++) {
                double previous = sum;
                sum += rows[i][i];
                sb.append("$$s_{").append(i + 1).append("}=s_{").append(i).append("}+a_{")
                        .append(i + 1).append(",").append(i + 1).append("}=")
                        .append(number(previous)).append("+").append(factor(rows[i][i]))
                        .append("=").append(number(sum)).append("$$\n");
            }
            break;
        case "norm":
            double length = 0;
            int entry = 0;
            sb.append("$$h_0=0,\\qquad\\operatorname{hypot}(h,a)=\\sqrt{h^2+a^2}$$\n");
            for (int r = 0; r < rows.length; r++) {
                for (int c = 0; c < rows[r].length; c++) {
                    double previous = length;
                    length = Math.hypot(length, rows[r][c]);
                    sb.append("$$h_{").append(++entry).append("}=\\operatorname{hypot}(")
                            .append(number(previous)).append(",").append(number(rows[r][c])).append(")=")
                            .append(number(length)).append("\\quad(a_{").append(r + 1).append(",")
                            .append(c + 1).append("})$$\n");
                }
            }
            break;
        case "transpose":
            if (rows.length == 0 || input.getColumnCount() == 0) {
                sb.append("\\begin{center}No entries to move: exchange the dimensions $")
                        .append(input.getRowCount()).append("\\times ").append(input.getColumnCount())
                        .append("$ to $").append(input.getColumnCount()).append("\\times ")
                        .append(input.getRowCount()).append("$.\\end{center}\n");
            }
            for (int r = 0; r < rows.length; r++) {
                if (input.getColumnCount() == 0) break;
                double[][] column = new double[input.getColumnCount()][1];
                for (int c = 0; c < column.length; c++) column[c][0] = rows[r][c];
                sb.append("$$\\text{Input row }").append(r + 1).append(":\\ ")
                        .append(latexMatrix(new double[][] {rows[r]}, true))
                        .append("\\quad\\longrightarrow\\quad\\text{Output column }").append(r + 1).append(":\\ ")
                        .append(latexMatrix(column, true)).append("$$\n");
            }
            break;
        case "rank":
            sb.append("$$\\operatorname{rank}(A)=\\text{number of pivots}=")
                    .append(explanation.pivotColumns.size()).append("$$\n");
            break;
        case "basis":
        case "span":
            int index = 0;
            for (int pivot : explanation.pivotColumns) {
                double[][] column = new double[rows.length][1];
                for (int r = 0; r < rows.length; r++) column[r][0] = rows[r][pivot];
                sb.append("$$b_{").append(++index).append("}=\\text{original column }").append(pivot + 1)
                        .append("=").append(latexMatrix(column, true)).append("$$\n");
            }
            sb.append("\\begin{center}").append(index == 0
                    ? "No pivots: the column space is the zero subspace, with an empty basis."
                    : "Keep these original columns in pivot order; discard the dependent columns.")
                    .append("\\end{center}\n");
            break;
        case "det":
            double[][] triangular = explanation.steps.get(explanation.steps.size() - 1).rows;
            long swaps = explanation.steps.stream().filter(s -> s.operation.contains("\\leftrightarrow")).count();
            sb.append("$$\\det(A)=(-1)^{").append(swaps).append("}");
            for (int i = 0; i < triangular.length; i++) {
                sb.append("\\cdot ").append(factor(triangular[i][i]));
            }
            sb.append("$$\n");
            break;
        default:
            break;
        }
    }

    private static String number(double value) {
        return LinAlgScene.formatNumber(value, true);
    }

    private static void appendDistributionSteps(StringBuilder sb, LinAlgScene.Explanation explanation) {
        DiscreteDistribution.Request request = DiscreteDistribution.parse(explanation.function, explanation.arguments);
        boolean binomial = request.function().equals("binomial");
        String parameter = statisticNumber(java.math.BigDecimal.valueOf(request.parameter()));
        if (binomial) {
            sb.append("$$n=").append(request.trials()).append(",\\quad p=").append(parameter)
                    .append(",\\quad k=").append(request.k()).append("$$\n");
            if (request.k() > request.trials()) {
                sb.append("$$k>n\\quad\\Longrightarrow\\quad P(X=k)=0$$\n");
                return;
            }
            if (request.parameter() == 0 || request.parameter() == 1 || request.trials() == 0) {
                sb.append("\\begin{center}Degenerate binomial: all mass lies at $k=")
                        .append(request.parameter() == 1 ? request.trials() : 0)
                        .append("$. No ambiguous $0^0$ needs to be evaluated.\\end{center}\n");
            } else {
                sb.append("$$\\binom{n}{k}=\\frac{n!}{k!(n-k)!}=\\frac{")
                        .append(request.trials()).append("!}{").append(request.k()).append("!\\,")
                        .append(request.trials() - request.k()).append("!}$$\n")
                        .append("$$P(X=").append(request.k()).append(")=\\binom{")
                        .append(request.trials()).append("}{").append(request.k()).append("}")
                        .append("\\left(").append(parameter).append("\\right)^{").append(request.k())
                        .append("}\\left(1-").append(parameter).append("\\right)^{")
                        .append(request.trials() - request.k()).append("}$$\n");
            }
        } else {
            sb.append("$$\\lambda=").append(parameter).append(",\\quad k=").append(request.k()).append("$$\n");
            if (request.parameter() == 0) {
                sb.append("\\begin{center}Zero event rate: all mass lies at $k=0$.\\end{center}\n");
            } else {
                sb.append("$$P(X=").append(request.k()).append(")=\\frac{e^{-")
                        .append(parameter).append("}\\left(").append(parameter).append("\\right)^{")
                        .append(request.k()).append("}}{").append(request.k()).append("!}$$\n");
            }
        }
        double log = request.logMass(request.k());
        sb.append("\\begin{center}\\parbox{14cm}{Numerically, factorials and powers are not formed directly. "
                + "A stable log-PMF uses Stirling corrections and a cancellation-resistant deviance calculation, "
                + "then exponentiates once. Exact zero-probability cases are handled separately.}\\end{center}\n");
        if (Double.isFinite(log)) {
            appendDistributionLogSteps(sb, request);
            sb.append("$$\\ell=\\log P(X=k)\\approx ")
                    .append(statisticNumber(java.math.BigDecimal.valueOf(log))).append("$$\n")
                    .append("$$P(X=k)=e^{\\ell}\\approx ");
        } else {
            sb.append("$$P(X=k)=");
        }
        sb.append(statisticNumber(java.math.BigDecimal.valueOf(request.result()))).append("$$\n")
                .append("$$E[X]=").append(statisticNumber(java.math.BigDecimal.valueOf(request.mean())))
                .append(",\\quad \\operatorname{Var}(X)=")
                .append(statisticNumber(java.math.BigDecimal.valueOf(request.variance()))).append("$$\n");
    }

    private static void appendDistributionLogSteps(StringBuilder sb, DiscreteDistribution.Request request) {
        long k = request.k();
        long n = request.trials();
        double parameter = request.parameter();
        if (request.function().equals("binomial") && k == 0 && parameter > 0 && parameter < 1) {
            sb.append("$$\\ell=n\\log(1-p)=").append(n).append("\\log\\left(1-")
                    .append(number(parameter)).append("\\right)$$\n");
        } else if (request.function().equals("binomial") && k == n && parameter > 0 && parameter < 1) {
            sb.append("$$\\ell=n\\log p=").append(n).append("\\log\\left(")
                    .append(number(parameter)).append("\\right)$$\n");
        } else if (request.function().equals("poisson") && k == 0) {
            sb.append("$$\\ell=-\\lambda=").append(number(-parameter)).append("$$\n");
        } else if (parameter > 0 && (!request.function().equals("binomial") || parameter < 1)) {
            sb.append("$$D(a,b)=a\\log(a/b)+b-a,\\quad "
                    + "s(a)=\\log(a!)-(a+\\tfrac12)\\log a+a-\\tfrac12\\log(2\\pi)$$\n");
            if (request.function().equals("binomial")) {
                double correction = DiscreteDistribution.stirlingError(n) - DiscreteDistribution.stirlingError(k)
                        - DiscreteDistribution.stirlingError(n - k);
                double d1 = DiscreteDistribution.deviance(k, n * parameter);
                double d2 = DiscreteDistribution.deviance(n - k, n * (1 - parameter));
                double normalization = 0.5 * (Math.log(2 * Math.PI) + Math.log(k)
                        + Math.log(n - k) - Math.log(n));
                sb.append("$$C=s(n)-s(k)-s(n-k)\\approx ").append(number(correction)).append("$$\n")
                        .append("$$D_1=D(k,np)\\approx ").append(number(d1))
                        .append(",\\quad D_2=D(n-k,n(1-p))\\approx ").append(number(d2)).append("$$\n")
                        .append("$$N=\\tfrac12\\log\\left(2\\pi k(1-k/n)\\right)\\approx ")
                        .append(number(normalization)).append("$$\n")
                        .append("$$\\ell=C-D_1-D_2-N\\approx (").append(number(correction))
                        .append(")-(").append(number(d1)).append(")-(").append(number(d2))
                        .append(")-(").append(number(normalization)).append(")$$\n");
            } else {
                double correction = DiscreteDistribution.stirlingError(k);
                double deviance = DiscreteDistribution.deviance(k, parameter);
                double normalization = 0.5 * (Math.log(2 * Math.PI) + Math.log(k));
                sb.append("$$s(k)\\approx ").append(number(correction))
                        .append(",\\quad D(k,\\lambda)\\approx ").append(number(deviance)).append("$$\n")
                        .append("$$N=\\tfrac12\\log(2\\pi k)\\approx ").append(number(normalization)).append("$$\n")
                        .append("$$\\ell=-s(k)-D(k,\\lambda)-N\\approx -(").append(number(correction))
                        .append(")-(").append(number(deviance)).append(")-(")
                        .append(number(normalization)).append(")$$\n");
            }
        }
    }

    private static void appendProbabilitySteps(StringBuilder sb, LinAlgScene.Explanation explanation) {
        ProbabilityMetrics.Calculation calculation = ProbabilityMetrics.calculate(
                explanation.function, explanation.arguments);
        List<java.math.BigDecimal> inputs = calculation.inputs();
        if (explanation.function.equals("bayes")) {
            sb.append("$$P(H)=").append(statisticNumber(inputs.get(0)))
                    .append(",\\quad P(E\\mid H)=").append(statisticNumber(inputs.get(1)))
                    .append(",\\quad P(E)=").append(statisticNumber(inputs.get(2))).append("$$\n")
                    .append("$$P(H\\cap E)=P(H)P(E\\mid H)=").append(statisticNumber(inputs.get(0)))
                    .append("\\cdot ").append(statisticNumber(inputs.get(1))).append("=")
                    .append(statisticNumber(calculation.numerator())).append("$$\n")
                    .append("$$P(H\\mid E)=\\frac{P(H\\cap E)}{P(E)}=\\frac{");
        } else {
            String other = explanation.function.equals("precision") ? "FP" : "FN";
            sb.append("$$TP=").append(statisticNumber(inputs.get(0))).append(",\\quad ")
                    .append(other).append("=").append(statisticNumber(inputs.get(1))).append("$$\n")
                    .append("$$TP+").append(other).append("=").append(statisticNumber(inputs.get(0)))
                    .append("+").append(statisticNumber(inputs.get(1))).append("=")
                    .append(statisticNumber(calculation.denominator())).append("$$\n")
                    .append("$$\\mathrm{").append(explanation.function).append("}=\\frac{TP}{TP+")
                    .append(other).append("}=\\frac{");
        }
        sb.append(statisticNumber(calculation.numerator())).append("}{")
                .append(statisticNumber(calculation.denominator())).append("}\\approx ")
                .append(statisticNumber(calculation.result())).append("$$\n");
    }

    private static void appendStatisticsSteps(StringBuilder sb, LinAlgScene.Explanation explanation) {
        Statistics.Summary summary = Statistics.summarize(explanation.input);
        java.math.BigDecimal sum = java.math.BigDecimal.ZERO;
        sb.append("\\begin{center}\\parbox{14cm}{Each vector entry is one equally weighted observation. "
                + "Two-pass calculation with high-precision decimal intermediates avoids premature overflow "
                + "and loss of small differences. Displayed decimals are rounded.}\\end{center}\n")
                .append("$$n=").append(summary.data().length).append(",\\qquad S_0=0$$\n");
        for (int i = 0; i < summary.data().length; i++) {
            double value = summary.data()[i];
            sum = sum.add(java.math.BigDecimal.valueOf(value));
            sb.append("$$S_{").append(i + 1).append("}=S_{").append(i).append("}+")
                    .append(value < 0 ? "(" : "").append(statisticNumber(java.math.BigDecimal.valueOf(value)))
                    .append(value < 0 ? ")" : "").append("=").append(statisticNumber(sum)).append("$$\n");
        }
        sb.append("$$\\mu=\\frac{S_n}{n}=\\frac{").append(statisticNumber(summary.sum())).append("}{")
                .append(summary.data().length).append("}=").append(statisticNumber(summary.mean())).append("$$\n");
        if (explanation.function.equals("mean")) return;
        java.math.BigDecimal squares = java.math.BigDecimal.ZERO;
        for (int i = 0; i < summary.data().length; i++) {
            java.math.BigDecimal delta = java.math.BigDecimal.valueOf(summary.data()[i]).subtract(summary.mean());
            java.math.BigDecimal squared = delta.multiply(delta, Statistics.PRECISION);
            squares = squares.add(squared);
            sb.append("$$d_{").append(i + 1).append("}=x_{").append(i + 1).append("}-\\mu=")
                    .append(statisticNumber(delta)).append(",\\quad d_{").append(i + 1).append("}^2=")
                    .append(statisticNumber(squared)).append(",\\quad D_{").append(i + 1).append("}=")
                    .append(statisticNumber(squares)).append("$$\n");
        }
        sb.append("$$\\sigma^2=\\frac{D_n}{n}=\\frac{").append(statisticNumber(summary.squaredDeviations()))
                .append("}{").append(summary.data().length).append("}=")
                .append(statisticNumber(summary.variance())).append("$$\n");
        if (explanation.function.equals("std")) {
            sb.append("$$\\sigma=\\sqrt{").append(statisticNumber(summary.variance())).append("}=")
                    .append(statisticNumber(summary.std())).append("$$\n");
        }
    }

    static String statisticNumber(java.math.BigDecimal value) {
        if (value.signum() == 0) return "0";
        int exponent = value.precision() - value.scale() - 1;
        if (exponent >= 6 || exponent <= -4) {
            value = value.round(new java.math.MathContext(5, java.math.RoundingMode.HALF_UP));
            exponent = value.precision() - value.scale() - 1;
            return value.movePointLeft(exponent)
                    .stripTrailingZeros().toPlainString() + "\\cdot 10^{" + exponent + "}";
        }
        return value.setScale(4, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private static void appendNullspaceSteps(StringBuilder sb, LinAlgScene.Explanation explanation) {
        Value basis = explanation.nullspaceBasis;
        int n = explanation.input.getColumnCount();
        int rank = explanation.pivotColumns.size();
        int nullity = basis.getColumnCount();
        sb.append("\\begin{center}\\parbox{14cm}{Rank--nullity: there are $n=").append(n)
                .append("$ unknowns and $r=").append(rank).append("$ pivots, so "
                        + "$\\dim\\ker(A)=n-r=").append(nullity).append("$. ")
                .append("The returned matrix $N$ has size $").append(n).append("\\times ").append(nullity)
                .append("$; its columns are basis vectors, not all the vectors in the nullspace.}\\end{center}\n");
        if (nullity == 0) {
            sb.append("$$x=\\mathbf{0},\\qquad\\ker(A)=\\{\\mathbf{0}\\}$$\n")
                    .append("\\begin{center}\\parbox{14cm}{There are no free variables. The zero vector is "
                            + "the only solution, but it is not a basis vector: the trivial subspace has an empty "
                            + "basis, represented by the returned matrix with zero columns.}\\end{center}\n");
            return;
        }
        List<Integer> free = new java.util.ArrayList<>();
        for (int c = 0; c < n; c++) {
            if (!explanation.pivotColumns.contains(c)) free.add(c);
        }
        sb.append("\\begin{center}Free variables (all indices are 1-based):\\end{center}\n$$");
        for (int j = 0; j < free.size(); j++) {
            sb.append(j == 0 ? "" : ",\\quad ").append("x_{").append(free.get(j) + 1)
                    .append("}=t_{").append(j + 1).append("}");
        }
        sb.append("$$\n");
        if (rank > 0) {
            sb.append("\\begin{center}Solve the pivot equations in terms of the free variables:\\end{center}\n");
            for (int pivot : explanation.pivotColumns) {
                sb.append("$$x_{").append(pivot + 1).append("}=");
                boolean term = false;
                for (int j = 0; j < nullity; j++) {
                    double coefficient = basis.getRows()[pivot][j];
                    if (coefficient != 0) {
                        String formatted = LinAlgScene.formatNumber(coefficient, true);
                        sb.append(term ? " + " : "")
                                .append(coefficient < 0 ? "\\left(" + formatted + "\\right)" : formatted).append("\\,t_{")
                                .append(j + 1).append("}");
                        term = true;
                    }
                }
                sb.append(term ? "$$\n" : "0$$\n");
            }
        }
        for (int j = 0; j < nullity; j++) {
            double[][] vector = new double[n][1];
            for (int r = 0; r < n; r++) vector[r][0] = basis.getRows()[r][j];
            sb.append("\\begin{center}Set $t_{").append(j + 1)
                    .append("}=1$ and all other free parameters to zero:\\end{center}\n")
                    .append("$$v_{").append(j + 1).append("}=").append(latexMatrix(vector, true)).append("$$\n");
        }
        sb.append("\\begin{center}\\parbox{14cm}{General solution: every nullspace vector is a linear "
                + "combination of these columns. They are independent because their free-coordinate entries "
                + "form an identity matrix; they need not be unit length or orthogonal.}\\end{center}\n$$x=");
        for (int j = 0; j < nullity; j++) {
            sb.append(j == 0 ? "" : "+").append("t_{").append(j + 1).append("}v_{").append(j + 1).append("}");
        }
        sb.append("=Nt,\\qquad AN=0$$\n")
                .append("\\begin{center}\\parbox{14cm}{Thus adding $Nt$ to a parameter vector leaves linear "
                        + "predictions unchanged: $A(\\theta+Nt)=A\\theta$. These identities hold up to numerical "
                        + "tolerance; displayed coefficients are rounded.}\\end{center}\n");
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
            boolean explain = directive.getExplanations().stream().anyMatch(e -> e.function.matches("evd|eig"));
            List<LinAlgScene.Step> steps = explain ? new java.util.ArrayList<>() : null;
            EigenDecomposition eig = EigenDecomposition.tryCompute(m, steps);
            if (steps != null) {
                for (var step : steps) {
                    appendAlgorithmStep(sb, step, 0);
                }
            }
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
        sb.append("\\begin{center}\\parbox{14cm}{Notation: $V^T$ is the transpose: exchange rows and columns. "
                + "$V^*=\\overline{V}^{\\,T}$ is the conjugate transpose (also written $V^H$): "
                + "additionally conjugate each entry, replacing $a+bi$ by $a-bi$. "
                + "For example, if $C=(1\\quad i)$, then $C^T=\\binom{1}{i}$, "
                + "but $C^*=\\binom{1}{-i}$. Our script supports only real entries, so conjugation changes "
                + "nothing and $V^*=V^T$. We write $A=U\\Sigma V^*$ to match the general SVD notation. "
                + "The right singular vectors are columns of $V$; $V^*$ takes inner products with them, "
                + "expressing the input in those directions before scaling by $\\Sigma$ and mapping by $U$. "
                + "Orthonormal columns satisfy $V^*V=I$. In the thin rectangular case, this does not mean "
                + "$V^*$ is a two-sided inverse.}\\end{center}\n");
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
        String formatted = LinAlgScene.withoutMarkers(expr)
                .replace("^T", "^{T}")
                .replace("^-1", "^{-1}")
                .replace("*", " \\cdot ")
                .replaceAll("(?i)\\b(" + NeuralNetworkFunctions.NAMES
                        + "|grad|jacobian|hessian|laplacian|divergence|curl|det|rank|trace|norm|rref|basis|nullspace|inv|transpose|span|eigenvalues|eigenvectors|mean|variance|std|precision|recall|bayes|binomial|poisson)(?=\\s*\\()",
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
                        "\\begin{document}", "\\usepackage{tikz}\n\\usepackage{amssymb}\n\\begin{document}")
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

    @ConversionMethod(plainText = false)
    public String selectOperation(String name) {
        scene.getOperation(name).multiplication();
        mlpLayers = "";
        mlpStep = -1;
        attention = "";
        attnStep = -1;
        operation = encodeOperation(name);
        step = 0;
        return createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String selectOnlyMultiplication() {
        return selectOperation(onlyMultiplicationName());
    }

    @ConversionMethod(plainText = false)
    public String animateOnlyMultiplication() {
        return animateMultiplication(onlyMultiplicationName());
    }

    private List<LinAlgScene.Operation> markedMultiplications() {
        return scene.getOperations().stream().filter(op -> op.getOperator().equals("*")
                && !op.getLeft().isScalar() && op.getRight() != null && !op.getRight().isScalar()).toList();
    }

    private String onlyMultiplicationName() {
        List<LinAlgScene.Operation> products = markedMultiplications();
        if (products.size() != 1) {
            throw new IllegalArgumentException("Expected exactly one marked matrix multiplication; specify an operation marker name.");
        }
        return products.get(0).getName();
    }

    @ConversionMethod(plainText = false)
    public String multiplicationStep(int number) {
        selectedMultiplication().getStep(number);
        step = number;
        return createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String nextMultiplicationStep() {
        return multiplicationStep(selectedStep() + 1);
    }

    @ConversionMethod
    public String multiplicationSteps() {
        return Integer.toString(selectedMultiplication().getStepCount());
    }

    @ConversionMethod(plainText = false)
    public String animateMultiplication(String name) {
        selectOperation(name);
        return createCompleteAnimationScript("\n"
                + createStdAnimation("", "nextMultiplicationStep", "multiplicationSteps"));
    }

    public MlpTrace getMlpTrace() {
        if (mlpLayers.isEmpty()) throw new IllegalArgumentException("Define mlpLayers before animating an MLP.");
        return new MlpTrace(scene, mlpInput, mlpTarget, mlpLayers, mlpLoss, mlpLearningRate);
    }

    private int selectedMlpStep() {
        if (mlpStep < -1) throw new IllegalArgumentException("MLP step must be -1 or nonnegative.");
        return mlpStep == -1 ? getMlpTrace().getStepCount() : mlpStep;
    }

    @ConversionMethod(plainText = false)
    public String mlpStep(int number) {
        getMlpTrace().getStep(number);
        mlpStep = number;
        return createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String nextMlpStep() {
        return mlpStep(selectedMlpStep() + 1);
    }

    @ConversionMethod
    public String mlpSteps() {
        return Integer.toString(getMlpTrace().getStepCount());
    }

    @ConversionMethod(plainText = false)
    public String animateMlp() {
        mlpStep(0);
        return createCompleteAnimationScript("\n" + createStdAnimation("", "nextMlpStep", "mlpSteps"));
    }

    public AttentionTrace getAttentionTrace() {
        if (attention.isEmpty()) throw new IllegalArgumentException("Define attention before animating self-attention.");
        return new AttentionTrace(scene, attention, attnCausal, attnTokens);
    }

    private int selectedAttentionStep() {
        if (attnStep < -1) throw new IllegalArgumentException("Attention step must be -1 or nonnegative.");
        return attnStep == -1 ? getAttentionTrace().getStepCount() : attnStep;
    }

    @ConversionMethod(plainText = false)
    public String attentionStep(int number) {
        getAttentionTrace().getStage(number);
        attnStep = number;
        return createScriptFromInstance();
    }

    /** Not nextAttentionStep: the standard animation loop substitutes its variables A and B in method names. */
    @ConversionMethod(plainText = false)
    public String attentionNextStep() {
        return attentionStep(selectedAttentionStep() + 1);
    }

    @ConversionMethod
    public String attentionSteps() {
        return Integer.toString(getAttentionTrace().getStepCount());
    }

    @ConversionMethod(plainText = false)
    public String animateAttention() {
        attentionStep(0);
        return createCompleteAnimationScript("\n" + createStdAnimation("", "attentionNextStep", "attentionSteps"));
    }

    /** Wraps list-valued declarations as literals so their commas and semicolons survive parsing. */
    private String quoteListDeclarations(String script) {
        return script.replace("mlpLayers=" + mlpLayers, "mlpLayers=#" + mlpLayers + "#")
                .replace("attention=" + attention, "attention=#" + attention + "#")
                .replace("attnTokens=" + attnTokens, "attnTokens=#" + attnTokens + "#");
    }

    @Override
    public String generateCompleteDeclarationsBlock() {
        return quoteListDeclarations(super.generateCompleteDeclarationsBlock());
    }

    @Override
    public String createCompleteAnimationScript(String loop) {
        return quoteListDeclarations(super.createCompleteAnimationScript(loop));
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
    public HashMap<String, String> getMethodNameAbbreviations() {
        HashMap<String, String> names = super.getMethodNameAbbreviations();
        boolean single = markedMultiplications().size() == 1;
        names.put("selectOperation", single ? "Select named multiplication" : "Select multiplication");
        names.put("selectOnlyMultiplication", "Select multiplication");
        names.put("multiplicationStep", "Multiplication step");
        names.put("nextMultiplicationStep", "Next multiplication step");
        names.put("animateMultiplication", single ? "Animate named multiplication" : "Animate multiplication");
        names.put("animateOnlyMultiplication", "Animate multiplication");
        names.put("mlpStep", "MLP step");
        names.put("nextMlpStep", "Next MLP neuron");
        names.put("animateMlp", "Animate MLP training");
        names.put("attentionStep", "Attention step");
        names.put("attentionNextStep", "Next attention step");
        names.put("animateAttention", "Animate attention");
        return names;
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();
        boolean single = markedMultiplications().size() == 1;

        try {
            addMethod(methods, "mlpStep", new Class<?>[] {Integer.TYPE},
                    "MLP step", "MLP-Schritt",
                    "Shows one neuron in the forward, backward or update pass.",
                    "Zeigt ein Neuron im Vorwaerts-, Rueckwaerts- oder Aktualisierungslauf.")
                    .setParameterExplanation(0, "Step number from 0 to mlpSteps.");
            addMethod(methods, "nextMlpStep", new Class<?>[0],
                    "Next MLP neuron", "Naechstes MLP-Neuron",
                    "Advances the MLP training animation by one neuron.",
                    "Setzt die MLP-Trainingsanimation um ein Neuron fort.");
            addMethod(methods, "animateMlp", new Class<?>[0],
                    "Animate MLP training", "MLP-Training animieren",
                    "Animates forward/backward propagation, one SGD update, and a new forward pass to compare activations.",
                    "Animiert Vorwaertslauf, Backpropagation, einen SGD-Schritt und einen neuen Vorwaertslauf zum Vergleich.", 5);
            methods.put("mlpSteps", new MethodWrapper(getClass().getMethod("mlpSteps"), this, "mlpSteps"));
            addMethod(methods, "attentionStep", new Class<?>[] {Integer.TYPE},
                    "Attention step", "Attention-Schritt",
                    "Shows one stage of the self-attention computation.",
                    "Zeigt eine Stufe der Self-Attention-Berechnung.")
                    .setParameterExplanation(0, "Step number from 0 to attentionSteps.");
            addMethod(methods, "attentionNextStep", new Class<?>[0],
                    "Next attention step", "Naechster Attention-Schritt",
                    "Advances the self-attention animation by one stage.",
                    "Setzt die Self-Attention-Animation um eine Stufe fort.");
            addMethod(methods, "animateAttention", new Class<?>[0],
                    "Animate attention", "Attention animieren",
                    "Animates queries, keys, values, scores, softmax weights and the weighted sum of values.",
                    "Animiert Queries, Keys, Values, Scores, Softmax-Gewichte und die gewichtete Summe der Values.", 5);
            methods.put("attentionSteps", new MethodWrapper(getClass().getMethod("attentionSteps"), this, "attentionSteps"));
            addMethod(methods, "selectOperation", new Class<?>[] {String.class},
                    single ? "Select named multiplication" : "Select multiplication", "Multiplikation auswaehlen",
                    "Shows the initially empty result of a marked matrix multiplication.",
                    "Zeigt das anfangs leere Ergebnis einer markierten Matrixmultiplikation.")
                    .setParameterExplanation(0, "Operation marker name, without @.");
            addMethod(methods, "multiplicationStep", new Class<?>[] {Integer.TYPE},
                    "Multiplication step", "Multiplikationsschritt",
                    "Shows a multiply/accumulate step (0 is the empty result).",
                    "Zeigt einen Multiplikations-/Akkumulationsschritt (0 ist das leere Ergebnis).")
                    .setParameterExplanation(0, "Step number, from 0 to multiplicationSteps.");
            addMethod(methods, "nextMultiplicationStep", new Class<?>[0],
                    "Next multiplication step", "Naechster Multiplikationsschritt",
                    "Performs one scalar multiplication/accumulation.",
                    "Fuehrt eine skalare Multiplikation/Akkumulation aus.");
            addMethod(methods, "animateMultiplication", new Class<?>[] {String.class},
                    single ? "Animate named multiplication" : "Animate multiplication", "Multiplikation animieren",
                    "Creates a click-through SVG animation using the standard declaration loop.",
                    "Erstellt eine SVG-Klickanimation mit der Standard-Deklarationsschleife.", 5)
                    .setParameterExplanation(0, "Operation marker name, without @.");
            if (single) {
                methods.get("Select named multiplication").setMethodButtonVisible(false);
                methods.get("Animate named multiplication").setMethodButtonVisible(false);
                addMethod(methods, "selectOnlyMultiplication", new Class<?>[0],
                        "Select multiplication", "Multiplikation auswaehlen",
                        "Selects the only marked matrix multiplication.",
                        "Waehlt die einzige markierte Matrixmultiplikation aus.");
                addMethod(methods, "animateOnlyMultiplication", new Class<?>[0],
                        "Animate multiplication", "Multiplikation animieren",
                        "Animates the only marked matrix multiplication.",
                        "Animiert die einzige markierte Matrixmultiplikation.");
            }
            methods.put("multiplicationSteps", new MethodWrapper(
                    getClass().getMethod("multiplicationSteps"), this, "multiplicationSteps"));
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
                    "Toggle plots", "Plots umschalten",
                    "Shows or hides geometric, function, histogram, distribution and neural activation plots.",
                    "Zeigt oder verbirgt Geometrie-, Funktions-, Histogramm-, Verteilungs- und Aktivierungsplots.");
            addMethod(methods, "toggleFormulas", new Class<?>[0],
                    "Toggle formulas", "Formeln umschalten",
                    "Shows or hides the formulas above the plot. They are always shown while the plot is off.",
                    "Zeigt oder verbirgt die Formeln über dem Plot. Solange der Plot aus ist, werden sie immer gezeigt.");
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        boolean mlp = !mlpLayers.isEmpty();
        boolean multiplication = !operation.isEmpty();
        boolean markedProduct = !markedMultiplications().isEmpty();
        boolean attn = !attention.isEmpty();
        boolean ordinaryView = !mlp && !multiplication && !attn;
        boolean plottable = ordinaryView && LinAlgPlot.hasPlottableContent(scene);
        methods.get("MLP step").setMethodButtonVisible(mlp);
        methods.get("Next MLP neuron").setMethodButtonVisible(mlp
                && selectedMlpStep() < getMlpTrace().getStepCount());
        methods.get("Animate MLP training").setMethodButtonVisible(mlp);
        methods.get("Attention step").setMethodButtonVisible(attn);
        methods.get("Next attention step").setMethodButtonVisible(attn
                && selectedAttentionStep() < getAttentionTrace().getStepCount());
        methods.get("Animate attention").setMethodButtonVisible(attn);
        methods.get("Select multiplication").setMethodButtonVisible(markedProduct);
        methods.get("Animate multiplication").setMethodButtonVisible(markedProduct);
        methods.get("Multiplication step").setMethodButtonVisible(multiplication);
        methods.get("Next multiplication step").setMethodButtonVisible(multiplication
                && selectedStep() < selectedMultiplication().getStepCount());
        methods.get("Multiply with scalar").setMethodButtonVisible(ordinaryView && scene.lastNonScalar() != null);
        methods.get("Multiply").setMethodButtonVisible(ordinaryView);
        methods.get("Change basis").setMethodButtonVisible(ordinaryView);
        methods.get("Toggle plots").setMethodButtonVisible(plottable);
        methods.get("Toggle formulas").setMethodButtonVisible(plottable && showPlot);

        return methods;
    }

    private MethodWrapper addMethod(HashMap<String, MethodWrapper> methods, String method, Class<?>[] types,
            String name, String nameG, String tooltip, String tooltipG) throws NoSuchMethodException {
        return addMethod(methods, method, types, name, nameG, tooltip, tooltipG, 0);
    }

    private MethodWrapper addMethod(HashMap<String, MethodWrapper> methods, String method, Class<?>[] types,
            String name, String nameG, String tooltip, String tooltipG, int section) throws NoSuchMethodException {
        MethodWrapper mw = new MethodWrapper(
                this.getClass().getMethod(method, types),
                LinAlg.class, // Target script class. Important to set correctly!
                this, tooltip, tooltipG, name, nameG, section);
        methods.put(name, mw);
        return mw;
    }

    @Override
    public String createScriptFromInstance() {
        return SCRIPT_PREAMBLE + "\n" + scene.toScript() + this.generateCompleteDeclarationsBlock();
    }
}
