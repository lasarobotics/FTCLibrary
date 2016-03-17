package com.lasarobotics.library.util;

/**
 * Units for conversion
 */
public class Units {

    public enum Distance implements Unit {
        INCHES(0.0254),
        FEET(0.3048),
        CENTIMETERS(1.0 / 100.0),
        METERS(1); //default unit

        protected double conversionFactor;

        Distance(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        /**
         * Convert a unit of angle into a unit of distance
         *
         * @param value       Angle value
         * @param wheelRadius Wheel radius
         * @param radiusUnits Units of wheel radius
         * @param convertFrom Unit, in Angle, of value
         * @param convertTo   Unit, in Distance, of value
         * @return Arc length, or distance the wheel will travel after moving value angles
         */
        public static double convertToAngle(double value, double wheelRadius,
                                            Units.Distance radiusUnits, Units.Distance convertFrom,
                                            Units.Angle convertTo) {
            //1 radian = 1 arc length = 1 Circumference / 2PI
            //Distance Traveled = radians * wheel radius in meters * conversion unit
            //Radians = Distance traveled / wheel radius in meters / conversion unit
            //Radians = m traveled / m radius

            //s = r * theta
            //theta = s / r
            double revolutions = convertFrom.convertTo(Distance.METERS, value) /
                    radiusUnits.convertTo(Distance.METERS, wheelRadius);
            return Angle.REVOLUTIONS.convertTo(convertTo, revolutions);
        }

        @Override
        public double getConversionFactor() {
            return conversionFactor;
        }

        public double convertTo(Distance other, double value) {
            value *= conversionFactor;
            value /= other.getConversionFactor();
            return value;
        }

        @Override
        public Unit getDefaultUnit() {
            return METERS;
        }
    }

    public enum Angle implements Unit {
        DEGREES(1),
        RADIANS(180.0 / Math.PI),
        REVOLUTIONS(360.0),
        ENCODER_COUNTS(2.0); //encoders have 1440 resolutions / rotation, so 1 count = 1/4 degree
        // (encoder counts updated to 2 for new motors)

        protected double conversionFactor;

        Angle(double conversionFactor) {
            this.conversionFactor = conversionFactor;
        }

        /**
         * Convert a unit of angle into a unit of distance
         *
         * @param value       Angle value
         * @param wheelRadius Wheel radius
         * @param radiusUnits Units of wheel radius
         * @param convertFrom Unit, in Angle, of value
         * @param convertTo   Unit, in Distance, of value
         * @return Arc length, or distance the wheel will travel after moving value angles
         */
        public static double convertToDistance(double value, double wheelRadius,
                                               Units.Distance radiusUnits, Units.Angle convertFrom,
                                               Units.Distance convertTo) {
            //1 radian = 1 arc length = 1 Circumference / 2PI
            //Distance Traveled = radians * wheel radius in meters * conversion unit
            //s = r * theta

            double revolutions = convertFrom.convertTo(Angle.REVOLUTIONS, value);
            return radiusUnits.convertTo(convertTo, revolutions * wheelRadius);

            //return convertFrom.convertTo(Units.Angle.RADIANS, value) * wheelRadius *
            //        radiusUnits.convertTo(convertTo, 1);
        }

        @Override
        public double getConversionFactor() {
            return conversionFactor;
        }

        public double convertTo(Angle other, double value) {
            value *= conversionFactor;
            value /= other.getConversionFactor();
            return value;
        }

        public Angle getDefaultUnit() {
            return DEGREES;
        }
    }

    private interface Unit {
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
         */
        //double convertTo(Unit other, double value);
    }
}
