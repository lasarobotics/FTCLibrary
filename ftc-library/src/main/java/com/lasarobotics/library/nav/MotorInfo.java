package com.lasarobotics.library.nav;

import com.lasarobotics.library.util.Units;

/**
 * Motor and axle info
 */
public class MotorInfo {
    private double wheelRadius;             //wheel radius in meters
    private double mechanicalAdvantage;     //mechanical advantage (2x = 2x effective radius)

    public MotorInfo(double wheelRadius, Units.Distance radiusUnits) {
        this.wheelRadius = radiusUnits.convertTo(Units.Distance.METERS, wheelRadius);
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
