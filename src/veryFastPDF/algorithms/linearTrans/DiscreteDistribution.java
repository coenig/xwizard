package veryFastPDF.algorithms.linearTrans;

import java.io.Serializable;
import java.util.List;

import veryFastPDF.algorithms.linearTrans.LinAlgScene.Value;

/** Discrete PMFs evaluated in log space without constructing factorials. */
final class DiscreteDistribution {
    private static final double LOG_TWO_PI = Math.log(2 * Math.PI);
    private static final double MAX_INTEGER = 9007199254740991d;

    record Request(String function, long trials, double parameter, long k) implements Serializable {
        private static final long serialVersionUID = 1L;

        double logMass(long count) {
            if (function.equals("binomial")) {
                if (count > trials) return Double.NEGATIVE_INFINITY;
                if (parameter == 0) return count == 0 ? 0 : Double.NEGATIVE_INFINITY;
                if (parameter == 1) return count == trials ? 0 : Double.NEGATIVE_INFINITY;
                if (count == 0) return trials * Math.log1p(-parameter);
                if (count == trials) return trials * Math.log(parameter);
                double failures = trials - count;
                return stirlingError(trials) - stirlingError(count) - stirlingError(failures)
                        - deviance(count, trials * parameter) - deviance(failures, trials * (1 - parameter))
                        - 0.5 * (LOG_TWO_PI + Math.log(count) + Math.log(failures) - Math.log(trials));
            }
            if (parameter == 0) return count == 0 ? 0 : Double.NEGATIVE_INFINITY;
            if (count == 0) return -parameter;
            return -stirlingError(count) - deviance(count, parameter) - 0.5 * (LOG_TWO_PI + Math.log(count));
        }

        double result() {
            double log = logMass(k);
            double result = Math.exp(log);
            if (!Double.isFinite(result) || (result == 0 && log != Double.NEGATIVE_INFINITY)) {
                throw new IllegalArgumentException(function
                        + "(...) probability is outside the representable double range (underflow).");
            }
            return result;
        }

        double mean() {
            return function.equals("binomial") ? trials * parameter : parameter;
        }

        double variance() {
            return function.equals("binomial") ? mean() * (1 - parameter) : parameter;
        }
    }

    private DiscreteDistribution() {
    }

    static Request parse(String function, List<Value> arguments) {
        boolean binomial = function.equals("binomial");
        int arity = binomial ? 3 : 2;
        if (arguments.size() != arity) {
            throw new IllegalArgumentException(function + "(...) expects exactly " + arity + " arguments.");
        }
        for (Value value : arguments) {
            if (!value.isScalar() || !Double.isFinite(value.getScalar())) {
                throw new IllegalArgumentException(function + "(...) requires finite scalar arguments.");
            }
        }
        long trials = binomial ? integer(arguments.get(0).getScalar(), "n") : 0;
        double parameter = arguments.get(binomial ? 1 : 0).getScalar();
        if (parameter < 0 || parameter > (binomial ? 1 : MAX_INTEGER)) {
            throw new IllegalArgumentException(binomial ? "binomial(...) requires p in [0, 1]."
                    : "poisson(...) requires lambda in [0, 9007199254740991].");
        }
        long k = integer(arguments.get(arity - 1).getScalar(), "k");
        return new Request(function, trials, parameter, k);
    }

    private static long integer(double value, String name) {
        if (value < 0 || value > MAX_INTEGER || value != Math.rint(value)) {
            throw new IllegalArgumentException(name + " must be a nonnegative integer at most 9007199254740991.");
        }
        return (long) value;
    }

    static double stirlingError(double n) {
        if (n <= 16) {
            double logFactorial = 0;
            for (int i = 2; i <= n; i++) logFactorial += Math.log(i);
            return logFactorial - (n + 0.5) * Math.log(n) + n - 0.5 * LOG_TWO_PI;
        }
        double inverse = 1 / n;
        double square = inverse * inverse;
        return inverse * (1.0 / 12 - square * (1.0 / 360 - square
                * (1.0 / 1260 - square * (1.0 / 1680 - square / 1188))));
    }

    // The series avoids cancellation in x*log(x/mu)+mu-x near the mode.
    static double deviance(double x, double mu) {
        if (Math.abs(x - mu) < 0.1 * (x + mu)) {
            double v = (x - mu) / (x + mu);
            double sum = (x - mu) * v;
            double term = 2 * x * v;
            double square = v * v;
            for (int j = 1; j < 100; j++) {
                term *= square;
                double next = sum + term / (2 * j + 1);
                if (next == sum) return sum;
                sum = next;
            }
            throw new IllegalArgumentException("Discrete probability series did not converge.");
        }
        return x * (Math.log(x) - Math.log(mu)) + mu - x;
    }
}
