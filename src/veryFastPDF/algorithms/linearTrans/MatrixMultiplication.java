package veryFastPDF.algorithms.linearTrans;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Lazy schoolbook multiplication trace; step zero precedes all arithmetic. */
public final class MatrixMultiplication {
    private final Value left;
    private final Value right;
    private final int stepCount;

    MatrixMultiplication(Value left, Value right) {
        this.left = left;
        this.right = right;
        try {
            stepCount = Math.multiplyExact(Math.multiplyExact(left.getRowCount(), right.getColumnCount()),
                    left.getColumnCount());
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Matrix multiplication has too many steps to index.", e);
        }
    }

    public int getStepCount() {
        return stepCount;
    }

    /** Cells remain absent until their complete dot product has been accumulated. */
    public State getStep(int step) {
        if (step < 0 || step > stepCount) {
            throw new IllegalArgumentException("Multiplication step must be between 0 and " + stepCount + ".");
        }
        Double[][] cells = new Double[left.getRowCount()][right.getColumnCount()];
        int terms = left.getColumnCount();
        int visited = 0;
        int activeRow = -1, activeColumn = -1, activeTerm = -1;
        double sum = 0, previous = 0, product = 0;
        for (int r = 0; r < cells.length; r++) {
            for (int c = 0; c < right.getColumnCount(); c++) {
                double accumulator = 0;
                int completed = 0;
                for (int k = 0; k < terms && visited < step; k++) {
                    previous = accumulator;
                    product = left.getRows()[r][k] * right.getRows()[k][c];
                    accumulator += product;
                    completed++;
                    visited++;
                    activeRow = r;
                    activeColumn = c;
                    activeTerm = k;
                    sum = accumulator;
                }
                if (completed == terms) cells[r][c] = accumulator;
            }
        }
        return new State(step, cells, activeRow, activeColumn, activeTerm, previous, product, sum);
    }

    /** Indices are zero-based; -1 denotes that no scalar operation has happened yet. */
    public record State(int step, Double[][] cells, int row, int column, int term,
            double previousSum, double product, double partialSum) {
        public boolean isFilled(int row, int column) {
            return cells[row][column] != null;
        }
    }
}
