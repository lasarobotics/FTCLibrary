package com.lasarobotics.ftcrobotcontroller.opmodes;


import com.lasarobotics.library.drive.Mecanum;
import com.lasarobotics.library.controller.Controller;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * A Test Teleop
 */
public class MecanumPrototypeTeleop extends OpMode {
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

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        //update gamepads to controllers with events
        one.update(gamepad1);
        two.update(gamepad2);

        Mecanum.Arcade(one.left_stick_y, one.left_stick_x, one.right_stick_x, leftFront, rightFront, leftBack, rightBack);
    }

    @Override
    public void stop() {

    }
}
