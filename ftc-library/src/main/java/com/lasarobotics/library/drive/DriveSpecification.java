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
     * @param radiusUnit  Unit of wheel radius
     */
    DriveSpecification(Drivetrain drive, double wheelRadius, Units.Distance radiusUnit) {
        this.drive = drive;
        this.wheelRadius = radiusUnit.convertTo(Units.Distance.METERS, wheelRadius);
    }

    public double getWheelRadius() {
        return wheelRadius;
    }

    public Drivetrain getDrivetrain() {
        return drive;
    }
}
