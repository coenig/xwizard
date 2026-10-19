package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import eas.math.matrix.Matrix;

/**
 * Real eigenvalue decomposition A = Q * Lambda * Q^-1 of a square matrix.
 * Symmetric matrices use Jacobi rotations (Q orthogonal); other matrices use
 * QR iteration for the eigenvalues and SVD null spaces for the eigenvectors.
 * {@link #compute(Matrix)} returns null if no real diagonalization was found;
 * {@link #tryCompute(Matrix)} instead returns an object with a failure reason.
 * Standalone real eigenvalues and eigenvectors do not require diagonalizability.
 */
public class EigenDecomposition {

    private static final int MAX_QR_ITERATIONS = 5000;

    private final double[] eigenvalues;
    private final double[][] q;
    private final double[][] qInverse;
    private final boolean symmetric;
    private final String failureReason;

    private EigenDecomposition(double[] ev, double[][] q, double[][] qInv, boolean symmetric) {
        this.eigenvalues = ev;
        this.q = q;
        this.qInverse = qInv;
        this.symmetric = symmetric;
        this.failureReason = null;
    }

    private EigenDecomposition(String failureReason) {
        this.eigenvalues = null;
        this.q = null;
        this.qInverse = null;
        this.symmetric = false;
        this.failureReason = failureReason;
    }

    public static EigenDecomposition compute(Matrix a) {
        EigenDecomposition e = tryCompute(a);
        return e.isAvailable() ? e : null;
    }

    public static EigenDecomposition tryCompute(Matrix matrix) {
        return computeReal(matrix, true);
    }

    public static double[] realEigenvalues(Matrix matrix) {
        double[][] a = squareRows(matrix);
        double scale = scale(a);
        boolean symmetric = isSymmetric(a, scale);
        double[] values;
        if (symmetric) {
            values = symmetricCase(a, scale).eigenvalues.clone();
        } else {
            Spectrum spectrum = qrSpectrum(a, scale);
            requireRealSpectrum(spectrum);
            Integer[] order = sortedOrder(spectrum.real);
            values = new double[order.length];
            for (int i = 0; i < order.length; i++) values[i] = spectrum.real[order[i]];
        }
        for (double value : values) {
            if (!Double.isFinite(value)) {
                throw new IllegalArgumentException("Eigenvalue calculation produced a non-finite result.");
            }
        }
        return values;
    }

    public static double[][] realEigenvectors(Matrix matrix) {
        EigenDecomposition result = computeReal(matrix, false);
        if (!result.isAvailable()) {
            throw new IllegalArgumentException("Cannot compute real eigenvectors: " + result.getFailureReason());
        }
        return result.getQ();
    }

    private static double[][] squareRows(Matrix matrix) {
        int n = matrix.getRowCount();
        if (n == 0 || n != matrix.getColumnCount()) {
            throw new IllegalArgumentException("Eigenvalues and eigenvectors require a nonempty square matrix.");
        }
        double[][] a = new double[n][n];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                a[r][c] = matrix.get(c, r);
                if (!Double.isFinite(a[r][c])) {
                    throw new IllegalArgumentException("Eigenvalues and eigenvectors require finite matrix entries.");
                }
            }
        }
        return a;
    }

    private static double scale(double[][] a) {
        double scale = 1;
        for (double[] row : a) for (double entry : row) scale = Math.max(scale, Math.abs(entry));
        return scale;
    }

    private static boolean isSymmetric(double[][] a, double scale) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < i; j++) {
                if (Math.abs(a[i][j] - a[j][i]) > 1e-12 * scale) return false;
            }
        }
        return true;
    }

    private static void requireRealSpectrum(Spectrum spectrum) {
        if (spectrum == null) {
            throw new IllegalArgumentException("QR iteration did not converge; eigenvalues could not be determined reliably.");
        }
        if (!spectrum.complex.isEmpty()) {
            throw new IllegalArgumentException("The matrix has non-real eigenvalues; complex results are not supported.");
        }
    }

    private static EigenDecomposition computeReal(Matrix matrix, boolean diagonalization) {
        int n = matrix.getRowCount();
        if (n == 0 || n != matrix.getColumnCount()) {
            return new EigenDecomposition("the matrix is " + matrix.getRowCount() + "$\\times$" + matrix.getColumnCount()
                    + ", not square. Eigenvalues and eigenvectors are only defined for square matrices");
        }

        double[][] a = new double[n][n];
        double scale = 1;
        boolean sym = true;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                a[i][j] = matrix.get(j, i);
                if (!Double.isFinite(a[i][j])) {
                    return new EigenDecomposition("the matrix contains non-finite entries");
                }
                scale = Math.max(scale, Math.abs(a[i][j]));
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                sym &= Math.abs(a[i][j] - a[j][i]) <= 1e-12 * scale;
            }
        }

        EigenDecomposition result = sym ? symmetricCase(a, scale) : generalCase(a, scale, diagonalization);
        if (result.isAvailable() && diagonalization) {
            double err = reconstructionError(a, result);
            if (err > 1e-6 * scale) {
                return new EigenDecomposition("the numerical check $Q \\Lambda Q^{-1} = A$ failed (maximal deviation "
                        + String.format(java.util.Locale.ROOT, "%.2g", err)
                        + "); the eigenvector matrix is probably ill-conditioned, i.e. the matrix is numerically "
                        + "close to a non-diagonalizable one");
            }
        }
        return result;
    }

    public boolean isAvailable() {
        return eigenvalues != null;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public boolean isSymmetric() {
        return symmetric;
    }

    public double[][] getQ() {
        return q;
    }

    public double[][] getQInverse() {
        return qInverse;
    }

    public double[][] getLambda() {
        double[][] l = new double[eigenvalues.length][eigenvalues.length];
        for (int i = 0; i < eigenvalues.length; i++) {
            l[i][i] = eigenvalues[i];
        }
        return l;
    }

    private static double reconstructionError(double[][] a, EigenDecomposition e) {
        int n = a.length;
        double err = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double s = 0;
                for (int k = 0; k < n; k++) {
                    s += e.q[i][k] * e.eigenvalues[k] * e.qInverse[k][j];
                }
                err = Math.max(err, Math.abs(s - a[i][j]));
            }
        }
        return err;
    }

    private static EigenDecomposition symmetricCase(double[][] input, double scale) {
        int n = input.length;
        double[][] a = new double[n][];
        double[][] v = new double[n][n];
        for (int i = 0; i < n; i++) {
            a[i] = input[i].clone();
            v[i][i] = 1;
        }

        for (int sweep = 0; sweep < 100; sweep++) {
            double off = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < i; j++) {
                    off += a[i][j] * a[i][j];
                }
            }
            if (Math.sqrt(off) <= 1e-14 * scale) {
                break;
            }
            for (int p = 0; p < n - 1; p++) {
                for (int r = p + 1; r < n; r++) {
                    if (a[p][r] == 0) {
                        continue;
                    }
                    double theta = (a[r][r] - a[p][p]) / (2 * a[p][r]);
                    double t = Math.signum(theta == 0 ? 1 : theta) / (Math.abs(theta) + Math.sqrt(1 + theta * theta));
                    double c = 1 / Math.sqrt(1 + t * t);
                    double s = t * c;
                    for (int k = 0; k < n; k++) { // A <- A * J
                        double akp = a[k][p];
                        double akr = a[k][r];
                        a[k][p] = c * akp - s * akr;
                        a[k][r] = s * akp + c * akr;
                    }
                    for (int k = 0; k < n; k++) { // A <- J^T * A
                        double apk = a[p][k];
                        double ark = a[r][k];
                        a[p][k] = c * apk - s * ark;
                        a[r][k] = s * apk + c * ark;
                    }
                    for (int k = 0; k < n; k++) {
                        double vkp = v[k][p];
                        double vkr = v[k][r];
                        v[k][p] = c * vkp - s * vkr;
                        v[k][r] = s * vkp + c * vkr;
                    }
                }
            }
        }

        double[] ev = new double[n];
        for (int i = 0; i < n; i++) {
            ev[i] = a[i][i];
        }
        Integer[] order = sortedOrder(ev);
        double[] evSorted = new double[n];
        double[][] q = new double[n][n];
        for (int k = 0; k < n; k++) {
            evSorted[k] = ev[order[k]];
            for (int i = 0; i < n; i++) {
                q[i][k] = v[i][order[k]];
            }
        }
        return new EigenDecomposition(evSorted, q, transpose(q), true);
    }

    private static EigenDecomposition generalCase(double[][] a, double scale, boolean diagonalization) {
        int n = a.length;
        Spectrum spectrum = qrSpectrum(a, scale);
        if (spectrum == null) {
            return new EigenDecomposition("the QR iteration used to compute the eigenvalues did not converge within "
                    + MAX_QR_ITERATIONS + " iterations, so the eigenvalues could not be determined reliably");
        }
        if (!spectrum.complex.isEmpty()) {
            StringBuilder sb = new StringBuilder("the matrix has non-real eigenvalues, namely ");
            for (int i = 0; i < spectrum.complex.size(); i++) {
                double[] c = spectrum.complex.get(i);
                sb.append(i > 0 ? ", " : "").append("$").append(fmt(c[0])).append(" \\pm ").append(fmt(c[1])).append("i$");
            }
            sb.append(". Over the real numbers, a matrix can only be diagonalized if all its eigenvalues are real");
            return new EigenDecomposition(sb.toString());
        }
        double[] ev = spectrum.real;
        Integer[] order = sortedOrder(ev);

        List<double[]> columns = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        boolean[] done = new boolean[n];
        for (int idx = 0; idx < n; idx++) {
            int i = order[idx];
            if (done[i]) {
                continue;
            }
            int mult = 0;
            for (int jdx = 0; jdx < n; jdx++) {
                int j = order[jdx];
                if (!done[j] && Math.abs(ev[j] - ev[i]) <= 1e-6 * scale) {
                    done[j] = true;
                    mult++;
                }
            }

            double[][] shifted = new double[n][n];
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    shifted[r][c] = a[r][c] - (r == c ? ev[i] : 0);
                }
            }
            Matrix b = new Matrix(n, n);
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    b.set(c, r, shifted[r][c]);
                }
            }
            SingularValueDecomposition svd = new SingularValueDecomposition(b);
            double[] sv = svd.getSingularValues();
            double[][] v = svd.getV();
            int found = 0;
            for (int k = n - 1; k >= 0 && found < mult; k--) {
                if (sv[k] > 1e-6 * scale) {
                    break;
                }
                double[] col = new double[n];
                for (int r = 0; r < n; r++) {
                    col[r] = v[r][k];
                }
                columns.add(col);
                values.add(ev[i]);
                found++;
            }
            if (found == 0 || (diagonalization && found < mult)) {
                return new EigenDecomposition("the eigenvalue $\\lambda = " + fmt(ev[i]) + "$ has algebraic multiplicity "
                        + mult + " but only " + found + " linearly independent eigenvector" + (found == 1 ? "" : "s")
                        + " (geometric multiplicity " + found + "). The matrix is defective: there are not enough "
                        + "eigenvectors to form an invertible $Q$, so it is not diagonalizable");
            }
        }

        double[][] q = new double[n][columns.size()];
        double[] evOut = new double[columns.size()];
        for (int k = 0; k < columns.size(); k++) {
            evOut[k] = values.get(k);
            for (int r = 0; r < n; r++) {
                q[r][k] = columns.get(k)[r];
            }
        }
        if (!diagonalization) {
            for (int k = 0; k < columns.size(); k++) {
                double residual = 0;
                for (int r = 0; r < n; r++) {
                    double av = 0;
                    for (int c = 0; c < n; c++) av += a[r][c] * q[c][k];
                    residual = Math.max(residual, Math.abs(av - evOut[k] * q[r][k]));
                }
                if (!Double.isFinite(residual) || residual > 1e-6 * scale) {
                    return new EigenDecomposition("the numerical eigenvector residual check failed");
                }
            }
            return new EigenDecomposition(evOut, q, null, false);
        }
        double[][] qInv = invert(q);
        if (qInv == null) {
            return new EigenDecomposition("the computed eigenvectors are linearly dependent, "
                    + "so the matrix is not diagonalizable");
        }
        return new EigenDecomposition(evOut, q, qInv, false);
    }

    private static String fmt(double d) {
        double r = Math.round(d * 10000.0) / 10000.0;
        if (r == 0) {
            return "0";
        }
        return new java.math.BigDecimal(String.valueOf(r)).stripTrailingZeros().toPlainString();
    }

    private static class Spectrum {
        double[] real;
        List<double[]> complex = new ArrayList<>(); // {realPart, imaginaryPart}
    }

    /**
     * Eigenvalues by unshifted QR iteration until the matrix is quasi-triangular
     * (1x1 and 2x2 diagonal blocks); null if that is not reached.
     */
    private static Spectrum qrSpectrum(double[][] input, double scale) {
        int n = input.length;
        double tol = 1e-12 * scale;
        double[][] a = new double[n][];
        for (int i = 0; i < n; i++) {
            a[i] = input[i].clone();
        }

        for (int iter = 0; iter <= MAX_QR_ITERATIONS; iter++) {
            if (isQuasiTriangular(a, tol)) {
                return extractSpectrum(a, tol, scale);
            }
            if (iter == MAX_QR_ITERATIONS) {
                break;
            }
            double[][] qm = new double[n][n];
            double[][] r = new double[n][n];
            for (int j = 0; j < n; j++) { // modified Gram-Schmidt
                double[] col = new double[n];
                for (int i = 0; i < n; i++) {
                    col[i] = a[i][j];
                }
                for (int k = 0; k < j; k++) {
                    double dot = 0;
                    for (int i = 0; i < n; i++) {
                        dot += qm[i][k] * col[i];
                    }
                    r[k][j] = dot;
                    for (int i = 0; i < n; i++) {
                        col[i] -= dot * qm[i][k];
                    }
                }
                double norm = 0;
                for (double x : col) {
                    norm += x * x;
                }
                norm = Math.sqrt(norm);
                r[j][j] = norm;
                if (norm < 1e-300) {
                    return null;
                }
                for (int i = 0; i < n; i++) {
                    qm[i][j] = col[i] / norm;
                }
            }
            double[][] next = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double s = 0;
                    for (int k = 0; k < n; k++) {
                        s += r[i][k] * qm[k][j];
                    }
                    next[i][j] = s;
                }
            }
            a = next;
        }
        return null;
    }

    private static boolean isQuasiTriangular(double[][] a, double tol) {
        int n = a.length;
        for (int i = 2; i < n; i++) {
            for (int j = 0; j < i - 1; j++) {
                if (Math.abs(a[i][j]) > tol) {
                    return false;
                }
            }
        }
        for (int i = 0; i + 2 < n; i++) {
            if (Math.abs(a[i + 1][i]) > tol && Math.abs(a[i + 2][i + 1]) > tol) {
                return false;
            }
        }
        return true;
    }

    private static Spectrum extractSpectrum(double[][] a, double tol, double scale) {
        int n = a.length;
        Spectrum s = new Spectrum();
        List<Double> real = new ArrayList<>();
        int i = 0;
        while (i < n) {
            if (i < n - 1 && Math.abs(a[i + 1][i]) > tol) {
                double tr = a[i][i] + a[i + 1][i + 1];
                double det = a[i][i] * a[i + 1][i + 1] - a[i][i + 1] * a[i + 1][i];
                double disc = tr * tr / 4 - det;
                if (disc < -1e-12 * scale * scale) {
                    s.complex.add(new double[] {tr / 2, Math.sqrt(-disc)});
                } else {
                    double root = Math.sqrt(Math.max(disc, 0));
                    real.add(tr / 2 + root);
                    real.add(tr / 2 - root);
                }
                i += 2;
            } else {
                real.add(a[i][i]);
                i++;
            }
        }
        s.real = new double[real.size()];
        for (int k = 0; k < s.real.length; k++) {
            s.real[k] = real.get(k);
        }
        return s;
    }

    private static Integer[] sortedOrder(double[] values) {
        Integer[] order = new Integer[values.length];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        java.util.Arrays.sort(order, (x, y) -> Double.compare(values[y], values[x]));
        return order;
    }

    private static double[][] transpose(double[][] x) {
        int n = x.length;
        double[][] t = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                t[j][i] = x[i][j];
            }
        }
        return t;
    }

    /** Gauss-Jordan inversion with partial pivoting; null if singular. */
    private static double[][] invert(double[][] m) {
        int n = m.length;
        double[][] a = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(m[i], 0, a[i], 0, n);
            a[i][n + i] = 1;
        }
        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(a[r][c]) > Math.abs(a[pivot][c])) {
                    pivot = r;
                }
            }
            if (Math.abs(a[pivot][c]) < 1e-10) {
                return null;
            }
            double[] tmp = a[c];
            a[c] = a[pivot];
            a[pivot] = tmp;
            double p = a[c][c];
            for (int k = 0; k < 2 * n; k++) {
                a[c][k] /= p;
            }
            for (int r = 0; r < n; r++) {
                if (r != c) {
                    double f = a[r][c];
                    for (int k = 0; k < 2 * n; k++) {
                        a[r][k] -= f * a[c][k];
                    }
                }
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], n, inv[i], 0, n);
        }
        return inv;
    }
}
