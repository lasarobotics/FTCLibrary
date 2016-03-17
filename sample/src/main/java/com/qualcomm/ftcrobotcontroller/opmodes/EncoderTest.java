package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.skynet.EncodedMotor;
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
        frontLeft.moveDistance(1000);
        frontRight.moveDistance(1000);
        backLeft.moveDistance(1000);
        backRight.moveDistance(1000);

        frontLeft.setPower(50);
        frontRight.setPower(50);
        backLeft.setPower(50);
        backRight.setPower(50);
    }

    public void loop() {
        one.update(gamepad1);

        if (frontLeft.hasReachedPosition(1000))
            frontLeft.setPower(0);
        if (frontRight.hasReachedPosition(1000))
            frontRight.setPower(0);
        if (backLeft.hasReachedPosition(1000))
            backLeft.setPower(0);
        if (backRight.hasReachedPosition(1000))
            backRight.setPower(0);
    }

    public void stop() {

    }
}
