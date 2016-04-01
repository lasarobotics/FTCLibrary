package com.lasarobotics.library.drive;

import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.util.Units;
import com.lasarobotics.library.util.Vector2;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * [NOT IMPLEMENTED]
 * Drivetrain configuration information - tells where motors are placed in addition to
 */
public class DriveConfiguration {

    public static class LocatedMotor {
        DcMotor motorStandard = null;
        EncodedMotor motorEncoded = null;

        public LocatedMotor(DcMotor motor, Vector2<Double> location, Units.Distance locationUnit) {
            //TODO make sure motor is not null
        }

        public LocatedMotor(EncodedMotor encodedMotor, Vector2<Double> location, Units.Distance locationUnit) {

        }

        public boolean hasEncoder() {
            return (motorEncoded != null);
        }
    }
}
