package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eas.math.matrix.Matrix;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * Linear transformations (ML). So far, it parses a single matrix of floating
 * point numbers from the script body and renders it as a LaTeX math-mode matrix.
 * Only the default conversion methods are available.
 */
@Webproof(useInProductiveMode = true)
public class MatrixScript extends RepresentableDefault {

    private static final long serialVersionUID = 4120758296435718323L;

    public static final String SCRIPT_PREAMBLE = "matrix:";

    private Matrix matrix = new Matrix(0, 0);

    /** Declaration variable: iff true, the SVD A = U Sigma V^* is shown in addition to A. */
    private boolean showSVD = false;

    public MatrixScript(Exercise exercise) {
        super(exercise);
        this.setAllowCollapsingRules(false);
    }

    @Override
    public String[] getExampleScripts() {
        return new String[] {SCRIPT_PREAMBLE + "\n1 2.5 -3\n0 1e2 4"};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").trim().toLowerCase().startsWith(SCRIPT_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(script, father, 0);
        this.matrix = parseMatrix(this.getScriptWithoutPrepAndDeclAndPreamble());
    }

    /**
     * Parses a matrix of floating point numbers. Whitespace separates columns,
     * newlines separate rows; blank lines and extra padding are ignored.
     *
     * @throws IllegalArgumentException if a token is not a number or rows differ in length.
     */
    public static Matrix parseMatrix(String body) {
        List<double[]> rows = new ArrayList<>();

        for (String line : (body == null ? "" : body).split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] tokens = trimmed.split("\\s+");
            double[] row = new double[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                try {
                    row[i] = Double.parseDouble(tokens[i]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Not a number in row " + (rows.size() + 1) + ": '" + tokens[i] + "'", e);
                }
            }

            if (!rows.isEmpty() && rows.get(0).length != row.length) {
                throw new IllegalArgumentException(
                        "Row " + (rows.size() + 1) + " has " + row.length
                        + " columns, expected " + rows.get(0).length + ".");
            }
            rows.add(row);
        }

        int numRows = rows.size();
        int numCols = numRows == 0 ? 0 : rows.get(0).length;
        Matrix result = new Matrix(numCols, numRows); // Matrix is indexed (column, row).
        for (int j = 0; j < numRows; j++) {
            for (int i = 0; i < numCols; i++) {
                result.set(i, j, rows.get(j)[i]);
            }
        }
        return result;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * LaTeX math-mode representation of the parsed matrix; with {@code showSVD}
     * additionally A = U Sigma V^*.
     */
    public String toLaTeX() {
        double[][] a = new double[matrix.getRowCount()][matrix.getColumnCount()];
        for (int j = 0; j < a.length; j++) {
            for (int i = 0; i < a[j].length; i++) {
                a[j][i] = matrix.get(i, j);
            }
        }

        if (!showSVD || a.length == 0 || a[0].length == 0) {
            return "$$" + latexMatrix(a, false) + "$$";
        }

        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        return "$$A = " + latexMatrix(a, false) + "$$\n"
                + "$$A = U \\, \\Sigma \\, V^{*} = "
                + latexMatrix(svd.getU(), true)
                + latexMatrix(svd.getSigma(), true)
                + latexMatrix(svd.getVTransposed(), true)
                + "$$";
    }

    private static String latexMatrix(double[][] rows, boolean rounded) {
        StringBuilder sb = new StringBuilder("\\begin{pmatrix}\n");
        for (double[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                sb.append(i > 0 ? " & " : "").append(formatNumber(row[i], rounded));
            }
            sb.append(" \\\\\n");
        }
        return sb.append("\\end{pmatrix}").toString();
    }

    private static String formatNumber(double d, boolean rounded) {
        if (rounded) {
            d = Math.round(d * 10000.0) / 10000.0;
            if (d == 0) {
                return "0"; // also normalizes -0.0
            }
        }
        return d == Math.rint(d) && !Double.isInfinite(d)
                ? String.valueOf((long) d)
                : String.valueOf(d);
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        return new LaTeXPDF(
                LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
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
        return "Matrix";
    }

    @ConversionMethod(plainText = false)
    public String toggleSVD() {
        this.showSVD = !this.showSVD;
        return this.createScriptFromInstance();
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();
        String name = "Toggle SVD";

        try {
            MethodWrapper mw = new MethodWrapper(
                    this.getClass().getMethod("toggleSVD"),
                    MatrixScript.class, // Target script class. Important to set correctly!
                    this,
                    "Shows or hides the singular value decomposition A = U Sigma V* of the matrix.",
                    "Zeigt oder verbirgt die Singulärwertzerlegung A = U Sigma V* der Matrix.",
                    name,
                    "Singulärwertzerlegung umschalten");
            methods.put(name, mw);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        return methods;
    }

    private String matrixToScriptBody() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < matrix.getRowCount(); j++) {
            for (int i = 0; i < matrix.getColumnCount(); i++) {
                sb.append(i > 0 ? " " : "").append(formatNumber(matrix.get(i, j), false));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String createScriptFromInstance() {
        return SCRIPT_PREAMBLE + "\n" + matrixToScriptBody() + this.generateCompleteDeclarationsBlock();
    }
}
