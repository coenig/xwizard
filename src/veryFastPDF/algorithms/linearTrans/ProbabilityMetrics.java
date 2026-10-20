package veryFastPDF.algorithms.linearTrans;

import java.math.BigDecimal;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

final class ProbabilityMetrics {
    record Calculation(List<BigDecimal> inputs, BigDecimal numerator, BigDecimal denominator,
            BigDecimal result) {
        double scalar(String function) {
            double value = result.doubleValue();
            if (!Double.isFinite(value) || (value == 0 && result.signum() != 0)) {
                throw new IllegalArgumentException(function + "(...) result is outside the representable double range.");
            }
            return value;
        }
    }

    private ProbabilityMetrics() {
    }

    static Calculation calculate(String function, List<Value> arguments) {
        boolean bayes = function.equals("bayes");
        int count = bayes ? 3 : 2;
        if (arguments.size() != count) {
            throw new IllegalArgumentException(function + "(...) expects exactly " + count + " arguments.");
        }
        List<BigDecimal> inputs = arguments.stream().map(value -> {
            if (!value.isScalar() || !Double.isFinite(value.getScalar()) || value.getScalar() < 0
                    || (bayes && value.getScalar() > 1)) {
                throw new IllegalArgumentException(function + "(...) requires "
                        + (bayes ? "scalar probabilities in [0, 1]." : "finite nonnegative scalar counts."));
            }
            return BigDecimal.valueOf(value.getScalar());
        }).toList();
        BigDecimal numerator = inputs.get(0);
        BigDecimal denominator;
        if (bayes) {
            numerator = numerator.multiply(inputs.get(1));
            denominator = inputs.get(2);
            if (denominator.compareTo(numerator) < 0
                    || denominator.compareTo(numerator.add(BigDecimal.ONE.subtract(inputs.get(0)))) > 0) {
                throw new IllegalArgumentException("bayes(...) has inconsistent probabilities: evidence must be "
                        + "between prior * likelihood and prior * likelihood + (1 - prior).");
            }
        } else {
            denominator = numerator.add(inputs.get(1));
        }
        if (denominator.signum() == 0) {
            throw new IllegalArgumentException(function + "(...) is undefined: "
                    + (bayes ? "evidence must be greater than zero." : "the sum of counts must be greater than zero."));
        }
        return new Calculation(inputs, numerator, denominator,
                numerator.divide(denominator, Statistics.PRECISION));
    }
}
