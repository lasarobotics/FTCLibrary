package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by arthur on 9/29/15.
 */
public class LiftTest extends OpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight;
    Controller one;

    public void init() {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);
    }

    public void loop() {
        one.update(gamepad1);
        Tank.motor4(frontLeft, frontRight, backLeft, backRight, -one.left_stick_y, one.right_stick_y);
    }

    public void stop() {

    }
}
