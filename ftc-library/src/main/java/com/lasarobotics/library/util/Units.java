package com.lasarobotics.library.util;

/**
 * Units for conversion
 */
public class Units {

    public enum Distance implements Unit {
        ENCODER_COUNTS(Double.NaN),
        WHEEL_REVOLUTIONS(Double.NaN),
        WHEEL_DEGREES(Double.NaN),
        INCHES(0.0254),
        FEET(0.3048),
        CENTIMETERS(1.0 / 100.0),
        METERS(1); //default unit

        protected double conversionFactor;

        Distance(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        @Override
        public double getConversionFactor() {
            return conversionFactor;
        }

        @Override
        public double convertTo(Unit other, double value) throws DistanceManualConversionException {
            if (other.getConversionFactor() == Double.NaN || this.getConversionFactor() == Double.NaN)
                throw new DistanceManualConversionException();
            value *= conversionFactor;
            value /= other.getConversionFactor();
            return value;
        }

        @Override
        public Unit getDefaultUnit() {
            return METERS;
        }

        @Override
        public boolean isConvertable() {
            return conversionFactor != Double.NaN;
        }
    }

    public enum Angle implements Unit {
        DEGREES(1),
        RADIANS(180 / Math.PI);

        protected double conversionFactor;

        Angle(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        @Override
        public double getConversionFactor() {
            return conversionFactor;
        }

        @Override
        public double convertTo(Unit other, double value) throws DistanceManualConversionException {
            value *= conversionFactor;
            value /= other.getConversionFactor();
            return value;
        }

        @Override
        public boolean isConvertable() {
            return true;
        }

        public Angle getDefaultUnit() {
            return DEGREES;
        }
    }

    interface Unit {
        /**
         * Default unit. All conversion factors are relative to converting to this unit
         *
         * @return Default unit
         */
        Unit getDefaultUnit();

        /**
         * Get conversion factor to the default unit
         *
         * @return Conversion factor
         */
        double getConversionFactor();

        /**
         * Convert a value to another unit.
         *
         * @param other Another units
         * @param value Value to convert
         * @return Converted value, if possible
         * @throws DistanceManualConversionException Method will throw exception if the unit
         *                                           requested or the current unit are not inter-convertible because some information
         *                                           is lacking. e.g. we need wheel radius to convert inches into wheel revolutions.
         */
        double convertTo(Unit other, double value) throws DistanceManualConversionException;

        /**
         * Test whether a unit is convertable and therefore will not throw an error if converted from/to
         *
         * @return True if convertable, false otherwise
         */
        boolean isConvertable();
    }

    /**
     * Exception that throws when more data is necessary to convert from one
     */
    public static class DistanceManualConversionException extends Exception {
        DistanceManualConversionException() {
            super("Unable to convert to this unit due to unknown information! Manually convert.");
        }
    }
}
