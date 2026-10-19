package veryFastPDF.algorithms.linearTrans;

import java.util.ArrayList;
import java.util.List;

import eas.math.matrix.Matrix;

/**
 * Real singular value decomposition A = U * Sigma * V^T (thin form, k = min(m, n))
 * computed by one-sided Jacobi rotations. U is m x k, Sigma is k x k (diagonal,
 * descending), V is n x k. Column-sets of U and V are orthonormal.
 */
public class SingularValueDecomposition {

    private static final double EPS = 1e-14;

    private final double[][] u; // [row][col]
    private final double[] sigma;
    private final double[][] v;
    public record Rotation(int p, int q, double alpha, double beta, double gamma, double c, double s) {}
    public record Sweep(List<Rotation> rotations, double[][] w, double[][] v) {}
    private final List<Sweep> sweeps = new ArrayList<>();

    public SingularValueDecomposition(Matrix a) {
        this(a, false);
    }

    public SingularValueDecomposition(Matrix a, boolean trace) {
        int m = a.getRowCount();
        int n = a.getColumnCount();
        double[][] rows = new double[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                rows[i][j] = a.get(j, i);
            }
        }

        if (m >= n) {
            double[][][] r = decompose(rows, trace ? sweeps : null);
            this.u = r[0];
            this.sigma = r[1][0];
            this.v = r[2];
        } else {
            // A^T = U' S V'^T  =>  A = V' S U'^T
            double[][][] r = decompose(transpose(rows), trace ? sweeps : null);
            this.u = r[2];
            this.sigma = r[1][0];
            this.v = r[0];
        }
    }

    public List<Sweep> getSweeps() {
        return List.copyOf(sweeps);
    }

    public double[][] getU() {
        return u;
    }

    public double[] getSingularValues() {
        return sigma;
    }

    public double[][] getSigma() {
        double[][] s = new double[sigma.length][sigma.length];
        for (int i = 0; i < sigma.length; i++) {
            s[i][i] = sigma[i];
        }
        return s;
    }

    public double[][] getV() {
        return v;
    }

    public double[][] getVTransposed() {
        return transpose(v);
    }

    private static double[][] transpose(double[][] x) {
        int r = x.length;
        int c = r == 0 ? 0 : x[0].length;
        double[][] t = new double[c][r];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                t[j][i] = x[i][j];
            }
        }
        return t;
    }

    /** Requires rows >= cols. Returns {U, {sigma}, V}. */
    private static double[][][] decompose(double[][] a, List<Sweep> trace) {
        int m = a.length;
        int n = m == 0 ? 0 : a[0].length;
        double[][] w = new double[m][n];
        double[][] v = new double[n][n];
        for (int i = 0; i < m; i++) {
            w[i] = a[i].clone();
        }
        for (int i = 0; i < n; i++) {
            v[i][i] = 1;
        }

        boolean rotated = true;
        for (int sweep = 0; sweep < 100 && rotated; sweep++) {
            List<Rotation> rotations = trace == null ? null : new ArrayList<>();
            rotated = false;
            for (int p = 0; p < n - 1; p++) {
                for (int q = p + 1; q < n; q++) {
                    double alpha = 0;
                    double beta = 0;
                    double gamma = 0;
                    for (int i = 0; i < m; i++) {
                        alpha += w[i][p] * w[i][p];
                        beta += w[i][q] * w[i][q];
                        gamma += w[i][p] * w[i][q];
                    }
                    if (Math.abs(gamma) <= EPS * Math.sqrt(alpha * beta) || gamma == 0) {
                        continue;
                    }
                    rotated = true;
                    double zeta = (beta - alpha) / (2 * gamma);
                    double t = Math.signum(zeta == 0 ? 1 : zeta) / (Math.abs(zeta) + Math.sqrt(1 + zeta * zeta));
                    double c = 1 / Math.sqrt(1 + t * t);
                    double s = c * t;
                    if (rotations != null) {
                        rotations.add(new Rotation(p + 1, q + 1, alpha, beta, gamma, c, s));
                    }
                    for (int i = 0; i < m; i++) {
                        double wp = w[i][p];
                        double wq = w[i][q];
                        w[i][p] = c * wp - s * wq;
                        w[i][q] = s * wp + c * wq;
                    }
                    for (int i = 0; i < n; i++) {
                        double vp = v[i][p];
                        double vq = v[i][q];
                        v[i][p] = c * vp - s * vq;
                        v[i][q] = s * vp + c * vq;
                    }
                }
            }
            if (trace != null && rotated) {
                trace.add(new Sweep(List.copyOf(rotations),
                        java.util.Arrays.stream(w).map(double[]::clone).toArray(double[][]::new),
                        java.util.Arrays.stream(v).map(double[]::clone).toArray(double[][]::new)));
            }
        }

        double[] norms = new double[n];
        Integer[] order = new Integer[n];
        for (int j = 0; j < n; j++) {
            double sum = 0;
            for (int i = 0; i < m; i++) {
                sum += w[i][j] * w[i][j];
            }
            norms[j] = Math.sqrt(sum);
            order[j] = j;
        }
        java.util.Arrays.sort(order, (x, y) -> Double.compare(norms[y], norms[x]));

        double maxNorm = n == 0 ? 0 : norms[order[0]];
        double[][] u = new double[m][n];
        double[][] vSorted = new double[n][n];
        double[] sigma = new double[n];
        for (int k = 0; k < n; k++) {
            int j = order[k];
            sigma[k] = norms[j];
            for (int i = 0; i < n; i++) {
                vSorted[i][k] = v[i][j];
            }
            if (norms[j] > EPS * Math.max(1, maxNorm)) {
                for (int i = 0; i < m; i++) {
                    u[i][k] = w[i][j] / norms[j];
                }
            } else {
                sigma[k] = 0;
                completeColumn(u, k);
            }
        }

        return new double[][][] {u, {sigma}, vSorted};
    }

    /** Fills column k of u with a unit vector orthogonal to columns 0..k-1 (Gram-Schmidt on unit vectors). */
    private static void completeColumn(double[][] u, int k) {
        int m = u.length;
        for (int e = 0; e < m; e++) {
            double[] cand = new double[m];
            cand[e] = 1;
            for (int pass = 0; pass < 2; pass++) {
                for (int c = 0; c < k; c++) {
                    double dot = 0;
                    for (int i = 0; i < m; i++) {
                        dot += cand[i] * u[i][c];
                    }
                    for (int i = 0; i < m; i++) {
                        cand[i] -= dot * u[i][c];
                    }
                }
            }
            double norm = 0;
            for (double x : cand) {
                norm += x * x;
            }
            norm = Math.sqrt(norm);
            if (norm > 1e-8) {
                for (int i = 0; i < m; i++) {
                    u[i][k] = cand[i] / norm;
                }
                return;
            }
        }
    }
}
