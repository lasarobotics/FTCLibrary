package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.util.Units;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class EncoderTest extends OpMode {

    private static final double WHEEL_RADIUS = 2;
    private static final Units.Distance WHEEL_RADIUS_UNIT = Units.Distance.INCHES;
    DcMotor frontLeft, frontRight, backRight;
    EncodedMotor backLeft;
    Controller one;

    public void init() {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("backLeft"),
                WHEEL_RADIUS, WHEEL_RADIUS_UNIT); //set wheel radius for distance calculations
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);

        //TODO these are all currently ASYNCHRONOUS
        //TODO meaning that they can only really be called from init() or a state machine
        backLeft.setTargetPosition(1, Units.Distance.FEET);
        backLeft.reset();
    }

    public void loop() {
        one.update(gamepad1);
        backLeft.update();

        if (backLeft.hasReachedPosition(1, Units.Distance.FEET))
            Tank.motor4(frontLeft, frontRight, backLeft, backRight, 0, 0);
        else
            Tank.motor4(frontLeft, frontRight, backLeft, backRight, 0.5, -0.5);

        telemetry.addData("Back Left (counts): ", backLeft.getCurrentPosition());
        telemetry.addData("Back Left (feet): ", backLeft.getCurrentPosition(Units.Distance.FEET));
    }

    public void stop() {

    }
}
