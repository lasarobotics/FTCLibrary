package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.util.Timers;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Teleop5998 extends OpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor intake, lift;
    //Servo slide, dump;
    Controller firstController;
    Controller secondController;
    DcMotor goalOne, goalTwo;
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
        //slide = hardwareMap.servo.get("slide");
        //dump = hardwareMap.servo.get("dump");
        robotTimer = new Timers();
        firstController = new Controller(gamepad1);
        secondController = new Controller(gamepad2);
        //slide.setPosition(.53);
        //dump.setPosition(.53);
        status = LiftStatus.CENTER;
    }

    public void loop() {
        firstController.update(gamepad1);
        secondController.update(gamepad2);
        Tank.motor4(frontLeft, frontRight, backLeft, backRight, -firstController.left_stick_y,
                firstController.right_stick_y);

        if (firstController.right_bumper == ButtonState.PRESSED) {
            goalOne.setPower(1);
        } else if (firstController.right_trigger > 0.2) {
            goalOne.setPower(-1);
        } else {
            goalOne.setPower(0);
        }

        if (firstController.left_bumper == ButtonState.PRESSED) {
            goalTwo.setPower(1);
        } else if (firstController.left_trigger > 0.2) {
            goalTwo.setPower(-1);
        } else {
            goalTwo.setPower(0);
        }

        if (secondController.dpad_up == ButtonState.PRESSED) {
            intake.setPower(1);
        } else if (secondController.dpad_down == ButtonState.PRESSED) {
            intake.setPower(-1);
        } else if (secondController.dpad_left == ButtonState.PRESSED) {
            intake.setPower(0);
        } else if (secondController.dpad_right == ButtonState.PRESSED) {
            intake.setPower(0);
        }

        if (secondController.left_bumper == ButtonState.HELD) {
            lift.setPower(.3);
        } else if (secondController.right_bumper == ButtonState.HELD) {
            lift.setPower(-.3);
        } else {
            lift.setPower(0);
        }

        /*if (secondController.x == ButtonState.HELD) {
            status = LiftStatus.LEFT;
            robotTimer.startClock("dumpTimer");
            slide.setPosition(1);
        } else if (secondController.b == ButtonState.HELD) {
            status = LiftStatus.RIGHT;
            robotTimer.startClock("dumpTimer");
            slide.setPosition(0);
        } else if (secondController.a == ButtonState.HELD) {
            status = LiftStatus.CENTER;
            slide.setPosition(0.5);
            dump.setPosition(0.5);
        }

        if ((status == LiftStatus.LEFT) && (robotTimer.getClockValue("dumpTimer") >= 100)) {
            dump.setPosition(0);
        } else if ((status == LiftStatus.RIGHT) && (robotTimer.getClockValue("dumpTimer") >= 100)) {
            dump.setPosition(1);
        }*/


    }

    public void stop() { // make sure nothing moves after the end of the match
        intake.setPower(0);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        goalOne.setPower(0);
        goalTwo.setPower(0);
        lift.setPower(0);
        //slide.setPosition(.53);
        //dump.setPosition(.53);
    }

    private enum LiftStatus {
        LEFT,
        RIGHT,
        CENTER
    }
}
