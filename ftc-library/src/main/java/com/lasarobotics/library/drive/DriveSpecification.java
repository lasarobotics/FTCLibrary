package com.lasarobotics.library.drive;

import com.lasarobotics.library.util.Units;

/**
 * Drivetrain specification used by a variety of classes
 */
public class DriveSpecification {
    Drivetrain drive;       //Drive train type
    double wheelRadius;     //wheel radius in meters

    /**
     * Instantiate a drIve train specIfication
     *
     * @param drive       Current drive train
     * @param wheelRadius Wheel radius
     * @param radiusUnit  Unit of whell radius
     */
    DriveSpecification(Drivetrain drive, double wheelRadius, Units.Distance radiusUnit) {
        this.drive = drive;
        try {
            this.wheelRadius = radiusUnit.convertTo(Units.Distance.METERS, wheelRadius);
        } catch (Units.DistanceManualConversionException e) {
            throw new RuntimeException("Cannot convert from this unit!");
        }
    }

    public double getWheelRadius() {
        return wheelRadius;
    }

    public double convertToDistance(double value, Units.Angle convertFrom, Units.Distance convertTo) {
        try {
            //1 radian = 1 arc length = 1 Circumference / 2PI
            //Distance Traveled = radians * wheel radius in meters * conversion unit
            return convertFrom.convertTo(Units.Angle.RADIANS, value) * wheelRadius *
                    Units.Distance.METERS.convertTo(convertTo, 1);
        } catch (Units.DistanceManualConversionException e) {
            throw new RuntimeException("Cannot convert to this unit!");
        }
    }

    public Drivetrain getDrivetrain() {
        return drive;
    }
}
