package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Timers;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

public class Teleop5998 extends OpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor intake, lift;
    Servo slide, dump, carabiner, climber;
    Controller firstController;
    Controller secondController;
    DcMotor goalOne, goalTwo;
    double modfier = 0.25;
    LiftStatus status;
    Timers robotTimer;

    public void init() {
        gamepad1.setJoystickDeadzone(.1F);
        gamepad2.setJoystickDeadzone(.1F);
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");
        intake = hardwareMap.dcMotor.get("intake");
        goalOne = hardwareMap.dcMotor.get("goalOne");
        goalTwo = hardwareMap.dcMotor.get("goalTwo");
        lift = hardwareMap.dcMotor.get("lift");
        slide = hardwareMap.servo.get("slide");
        dump = hardwareMap.servo.get("dump");
        carabiner = hardwareMap.servo.get("carabiner");
        climber = hardwareMap.servo.get("climber");
        robotTimer = new Timers();
        firstController = new Controller(gamepad1);
        secondController = new Controller(gamepad2);
        slide.setPosition(.5);
        dump.setPosition(.5);
        climber.setPosition(1);
        carabiner.setPosition(.85);
        status = LiftStatus.CENTER;
        lift.setDirection(DcMotor.Direction.REVERSE);
    }

    public void loop() {
        firstController.update(gamepad1);
        secondController.update(gamepad2);
        //Drive
        if (firstController.x == ButtonState.PRESSED) {
            if (modfier == 1)
                modfier = .25;
            else
                modfier = 1;
        }
        telemetry.addData("mod", modfier);
        Tank.motor4(frontLeft, frontRight, backLeft, backRight, -1 * firstController.left_stick_y * modfier,
                firstController.right_stick_y * modfier);

        //Hanging
        if (firstController.right_bumper == ButtonState.HELD) {
            goalOne.setPower(1);
        } else if (firstController.right_trigger > 0.2) {
            goalOne.setPower(-1);
        } else {
            goalOne.setPower(0);
        }

        if (firstController.left_bumper == ButtonState.HELD) {
            goalTwo.setPower(1);
        } else if (firstController.left_trigger > 0.2) {
            goalTwo.setPower(-1);
        } else {
            goalTwo.setPower(0);
        }

        if (firstController.a == ButtonState.HELD) {
            carabiner.setPosition(MathUtil.coerce(0, 1, carabiner.getPosition() + .005));
        } else if (firstController.y == ButtonState.HELD) {
            carabiner.setPosition(MathUtil.coerce(0, 1, carabiner.getPosition() - .005));
        }

        //Intake
        if (secondController.y == ButtonState.PRESSED) {
            intake.setPower(1);
        } else if (secondController.a == ButtonState.PRESSED) {
            intake.setPower(-1);
        } else if (secondController.b == ButtonState.PRESSED) {
            intake.setPower(0);
        }

        //Lift
        if (secondController.dpad_up == ButtonState.HELD) {
            lift.setPower(.6);
        } else if (secondController.dpad_down == ButtonState.HELD) {
            lift.setPower(-.6);
        } else {
            lift.setPower(0);
        }

        if (secondController.right_stick_x > .5) {
            slide.setPosition(MathUtil.coerce(0, 1, slide.getPosition() + .005));
        } else if (secondController.right_stick_x < -.5) {
            slide.setPosition(MathUtil.coerce(0, 1, slide.getPosition() - .005));
        } else if (secondController.right_stick_y > .5) {
            slide.setPosition(.5);
        }

        if (secondController.left_stick_x > .5) {
            dump.setPosition(MathUtil.coerce(0, 1, dump.getPosition() + .005));
        } else if (secondController.left_stick_x < -.5) {
            dump.setPosition(MathUtil.coerce(0, 1, dump.getPosition() - .005));
        } else if (secondController.left_stick_y > .5) {
            dump.setPosition(.5);
        }
        //Climbersq
        if (secondController.start == ButtonState.PRESSED) {
            climber.setPosition(0);
        } else if (secondController.back == ButtonState.PRESSED) {
            climber.setPosition(1);
        }
    }

    public void stop() { // make sure nothing moves after the end of the match
        if (intake != null) {
            intake.setPower(0);
            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);
            goalOne.setPower(0);
            goalTwo.setPower(0);
            lift.setPower(0);
        }
    }

    private enum LiftStatus {
        LEFT,
        RIGHT,
        CENTER
    }
}
