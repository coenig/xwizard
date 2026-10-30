package veryFastPDF.algorithms.linearTrans;

import java.math.BigDecimal;
import java.math.MathContext;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Population statistics of one nonempty row or column vector. */
final class Statistics {
    static final MathContext PRECISION = MathContext.DECIMAL128;

    record Summary(double[] data, BigDecimal sum, BigDecimal mean, BigDecimal squaredDeviations,
            BigDecimal variance, BigDecimal std) {
        double result(String function) {
            BigDecimal value = switch (function) {
            case "mean" -> mean;
            case "variance" -> variance;
            case "std" -> std;
            default -> throw new IllegalArgumentException("Unknown statistic '" + function + "'.");
            };
            double result = value.doubleValue();
            if (!Double.isFinite(result) || (result == 0 && value.signum() != 0)) {
                throw new IllegalArgumentException(function
                        + "(...) result is outside the representable double range (overflow or underflow).");
            }
            return result;
        }
    }

    private Statistics() {
    }

    static Summary summarize(Value input) {
        if (input.isScalar() || input.getRowCount() == 0 || input.getColumnCount() == 0
                || (input.getRowCount() != 1 && input.getColumnCount() != 1)) {
            throw new IllegalArgumentException("Statistics require a nonempty row or column vector; "
                    + "use [2] for a single observation. Matrices are not pooled automatically.");
        }
        double[] data = new double[Math.max(input.getRowCount(), input.getColumnCount())];
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < data.length; i++) {
            data[i] = input.getRowCount() == 1 ? input.getRows()[0][i] : input.getRows()[i][0];
            if (!Double.isFinite(data[i])) {
                throw new IllegalArgumentException("Statistics require finite observations.");
            }
            sum = sum.add(BigDecimal.valueOf(data[i]));
        }
        BigDecimal count = BigDecimal.valueOf(data.length);
        BigDecimal mean = sum.divide(count, PRECISION);
        BigDecimal squaredDeviations = BigDecimal.ZERO;
        for (double value : data) {
            BigDecimal delta = BigDecimal.valueOf(value).subtract(mean);
            squaredDeviations = squaredDeviations.add(delta.multiply(delta, PRECISION));
        }
        BigDecimal variance = squaredDeviations.divide(count, PRECISION);
        return new Summary(data, sum, mean, squaredDeviations, variance, variance.sqrt(PRECISION));
    }
}
