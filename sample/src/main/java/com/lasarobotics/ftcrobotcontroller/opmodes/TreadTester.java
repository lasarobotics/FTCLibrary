package com.lasarobotics.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Mecanum;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.sensor.modernrobotics.OpticalDistance;
import com.lasarobotics.library.util.RollingAverage;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Creates a rolling-average test for the optical distance sensor, attempting to correlate reflectivity with distance
 */
public class TreadTester extends OpMode {

    //basic FTC classes
    DcMotor leftFront;
    DcMotor rightFront;
    DcMotor leftBack;
    DcMotor rightBack;

    //advanced FTC classes
    Controller one = new Controller();
    Controller two = new Controller();


    @Override
    public void init() {
        gamepad1.setJoystickDeadzone(.1F);
        gamepad2.setJoystickDeadzone(.1F);
        leftFront = hardwareMap.dcMotor.get("leftFront");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightBack = hardwareMap.dcMotor.get("rightBack");
    }

    @Override
    public void loop() {
        //update gamepads to controllers with events
        one.update(gamepad1);
        two.update(gamepad2);
        Tank.Motor4(leftFront,rightFront,leftBack,rightBack,-gamepad1.left_stick_y,gamepad1.right_stick_y);
    }

    @Override
    public void stop() {

    }
}