package com.lasarobotics.library.util;

/**
 * Mathematical and Precision Utilities
 */
public final class MathUtil {

    /**
     * Double equality epsilon (maximum tolerance for a double)
     */
    private final static double EPSILON = 0.000001;

    /**
     * Suppresses constructor for noninstantiability
     */
    private MathUtil() {
        throw new AssertionError();
    }

    /**
     * Gives a "deadzone" where any value less
     * than this would return zero.
     *
     * @param deadband Maximum value that returns zero
     * @param value    Value to test
     * @return Deadbanded value
     */
    public static double deadband(double deadband, double value) {
        return (Math.abs(value) > deadband) ? value : 0;
    }

    /**
     * Returns if two double values are equal to within epsilon.
     *
     * @param a First value
     * @param b Second value
     * @return True if the values are equal, false otherwise
     */
    public static Boolean equal(double a, double b) {
        return (Math.abs(a - b) < EPSILON);
    }

    /**
     * Returns if two double values are equal to within a distance.
     *
     * @param a        First value
     * @param b        Second value
     * @param distance Maximum distance between a and b
     * @return True if the values are equal ot within distance, false otherwise
     */
    public static Boolean equal(double a, double b, double distance) {
        return (Math.abs(a - b) < distance);
    }

    /**
     * Ignores values equal to the fail value (normally zero).
     *
     * @param value     Current value
     * @param lastvalue Previous value
     * @param fail      Filter this value, normally zero
     * @return Filtered value
     */
    public static double filter(double value, double lastvalue, double fail) {
        return (value == fail) ? lastvalue : value;
    }

    /**
     * Forces a numerical value to be between a min
     * and a max.
     *
     * @param min   If less than min, returns min
     * @param max   If greater than max, returns max
     * @param value Value to test
     * @return Coerced value
     */
    public static double coerce(double min, double max, double value) {
        return (value > max) ? max : (value < min) ? min : value;
    }

    /**
     * Tests if a number is between the bounds, exclusive.
     *
     * @param min   If less than min, returns false
     * @param max   If greater than max, returns false
     * @param value Value to test
     * @return Returns true if value is between (exclusive) min and max, false otherwise.
     */
    public static boolean inBounds(double min, double max, double value) {
        return (value < max) && (value > min);
    }

    /**
     * Normalize values to a specific normalizeTo value.
     * The largest value is normalized to the normalizedTo value and the rest are multiplied
     * by the same factor.
     * For example, if given 2 and 4, normalized to 1, the result would be 0.5 and 1.
     * Negatives will be treated as positive magnitudes.
     *
     * @param val1            First value
     * @param val2            Second value
     * @param normalizeUpOnly Only normalize if the factor is less than 1. In other words,
     *                        if given 0.5 and normalize to 1, if true the result would be 0.5.
     *                        If false, the result would 1.
     * @param normalizeTo     Value to normalize to
     * @return Array of normalized values
     */
    public static double[] normalize(double val1, double val2, double normalizeTo, boolean normalizeUpOnly) {
        double max = Math.max(Math.abs(val1), Math.abs(val2));
        double factor = normalizeTo / max;
        if (factor >= 1 && normalizeUpOnly) return new double[]{val1, val2};
        return new double[]{val1 * factor, val2 * factor};
    }

    /**
     * Normalize values to a specific normalizeTo value.
     * The largest value is normalized to the normalizedTo value and the rest are multiplied
     * by the same factor.
     * For example, if given 2 and 4, normalized to 1, the result would be 0.5 and 1.
     * Negatives will be treated as positive magnitudes.
     *
     * @param vals            List of values
     * @param normalizeTo     Value to normalize to
     * @param normalizeUpOnly Only normalize if the factor is less than 1. In other words,
     *                        if given 0.5 and normalize to 1, if true the result would be 0.5.
     *                        If false, the result would 1.
     * @return Array of normalized values
     */
    public static double[] normalize(double[] vals, double normalizeTo, boolean normalizeUpOnly) {
        double max = Double.MIN_VALUE;
        for (double v : vals)
            if (Math.abs(v) > max) max = Math.abs(v);
        double factor = normalizeTo / max;
        if (factor >= 1 && normalizeUpOnly) return vals;
        double[] result = new double[vals.length - 1];
        for (int i = 0; i < vals.length; i++)
            result[i] = vals[i] * factor;
        return result;
    }

    /**
     * Sum an array of values
     *
     * @param vals Values to sum
     * @return Sum of values
     */
    public static double sum(double[] vals) {
        double sum = 0.0;
        for (double d : vals) {
            sum += d;
        }
        return sum;
    }

    /**
     * Average an array of values
     *
     * @param vals Values to average
     * @return Average of values
     */
    public static double average(double[] vals) {
        double sum = 0.0;
        for (double d : vals) {
            sum += d;
        }
        return sum / vals.length;
    }
}
