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

    DcMotor lift;
    DcMotor liftAngle;
    DcMotor frontLeft, frontRight, backLeft, backRight;
    Controller one;

    public void init() {
        lift = hardwareMap.dcMotor.get("lift");
        liftAngle = hardwareMap.dcMotor.get("liftAngle");
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);
    }

    public void loop() {
        one.update(gamepad1);

        if (one.dpad_up == ButtonState.PRESSED || one.dpad_up == ButtonState.HELD)
        {
            liftAngle.setPower(0.25);
        }
        else if(one.dpad_down == ButtonState.PRESSED || one.dpad_down == ButtonState.HELD)
        {
            liftAngle.setPower(-0.25);
        }
        else {
            liftAngle.setPower(0);
        }

        if (one.y == ButtonState.PRESSED || one.y == ButtonState.HELD)
        {
            liftAngle.setPower(1);
        }
        else if(one.a == ButtonState.PRESSED || one.a == ButtonState.HELD)
        {
            liftAngle.setPower(-1);
        }
        else {
            liftAngle.setPower(0);
        }


        Tank.motor4(frontLeft, frontRight, backLeft, backRight, one.left_stick_y * -1, one.right_stick_y);
    }

    public void stop() {

    }
}
