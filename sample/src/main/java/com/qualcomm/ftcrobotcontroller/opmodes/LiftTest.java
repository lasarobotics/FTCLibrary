package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by arthur on 9/29/15.
 */
public class LiftTest extends OpMode {

    DcMotor lift;
    DcMotor liftAngle;
    Controller one;

    public void init() {
        lift = hardwareMap.dcMotor.get("lift");
        liftAngle = hardwareMap.dcMotor.get("liftAngle");
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

        lift.setPower(one.left_stick_y);
    }

    public void stop() {

    }
}
