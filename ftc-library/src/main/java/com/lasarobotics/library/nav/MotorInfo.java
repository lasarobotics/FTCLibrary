package com.lasarobotics.library.nav;

import com.lasarobotics.library.util.Units;

/**
 * Motor and axle info
 */
public class MotorInfo {
    private double wheelRadius;             //wheel radius in meters
    private double mechanicalAdvantage;     //mechanical advantage (2x = 2x effective radius)

    public MotorInfo(double encoderCountsPerUnit, Units.Distance distanceUnit) {
        //encoderCountsPerUnit should be converted to encoder counts per meter
        //(encoder counts / feet) * (feet / real encoder counts)
        //(1000 / 1 foot) * (1 foot / 720 real counts) = radius of 1000/720

        this.wheelRadius = distanceUnit.convertTo(Units.Distance.METERS, encoderCountsPerUnit) / Units.Angle.ENCODER_COUNTS.getConversionFactor();
        this.mechanicalAdvantage = 1;
    }

    public MotorInfo(double wheelRadius, Units.Distance radiusUnits, double mechanicalAdvantage) {
        this.wheelRadius = radiusUnits.convertTo(Units.Distance.METERS, wheelRadius);
        this.mechanicalAdvantage = mechanicalAdvantage;
    }

    public double getWheelRadius(Units.Distance unit) {
        return Units.Distance.METERS.convertTo(unit, wheelRadius);
    }

    public double getMechanicalAdvantage() {
        return mechanicalAdvantage;
    }

    public double getEffectiveWheelRadius(Units.Distance unit) {
        return getWheelRadius(unit) / mechanicalAdvantage;
    }
}
