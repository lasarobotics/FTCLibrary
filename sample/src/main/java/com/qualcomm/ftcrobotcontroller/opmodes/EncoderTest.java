package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.nav.EncodedMotor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class EncoderTest extends OpMode {

    EncodedMotor frontLeft, frontRight, backLeft, backRight;
    Controller one;

    public void init() {
        frontLeft = new EncodedMotor(hardwareMap.dcMotor.get("frontLeft"));
        frontRight = new EncodedMotor(hardwareMap.dcMotor.get("frontRight"));
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("backLeft"));
        backRight = new EncodedMotor(hardwareMap.dcMotor.get("backRight"));

        one = new Controller(gamepad1);

        //TODO these are all currently ASYNCHRONOUS
        //TODO meaning that they can only really be called from init() or a state machine
        backLeft.setTargetPosition(1000);
    }

    public void loop() {
        one.update(gamepad1);

        if (backLeft.hasReachedPosition(1000))
            Tank.motor4(frontLeft, frontRight, backLeft, backRight, 0, 0);
        else
            Tank.motor4(frontLeft, frontRight, backLeft, backRight, 0.5, -0.5);

        telemetry.addData("Front Left: ", frontLeft.getCurrentPosition());
        telemetry.addData("Front Right: ", frontRight.getCurrentPosition());
        telemetry.addData("Back Left: ", backLeft.getCurrentPosition());
        telemetry.addData("Back Right: ", backRight.getCurrentPosition());
    }

    public void stop() {

    }
}
