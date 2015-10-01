package com.qualcomm.ftcrobotcontroller.opmodes;


import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.monkeyc.MonkeyC;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * MonkeyC2 Write Test
 */
public class MonkeyC2Write extends OpMode {
    //basic FTC classes
    DcMotor frontLeft, frontRight, backLeft, backRight;
    Controller one, two;

    MonkeyC writer;

    @Override
    public void init() {
        gamepad1.setJoystickDeadzone(.1F);
        gamepad2.setJoystickDeadzone(.1F);

        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);
        two = new Controller(gamepad2);
    }
    @Override
    public void start(){
        MonkeyC2Do.isTested = false;
        writer = new MonkeyC();
    }
    @Override
    public void loop() {
        //update gamepads to controllers with events
        one.update(gamepad1);
        two.update(gamepad2);
        writer.add(one, two);

        if (one.x == ButtonState.PRESSED)
        {
            MonkeyC2Do.test();
            writer.waitForController(one, two);
        }

        if (MonkeyC2Do.isTested)
        {
            telemetry.addData("X KEY", "PRESSED!");
        }
        else
        {
            telemetry.addData("X KEY", "Not pressed");
        }

        telemetry.addData("Status", writer.getCommandsWritten() + " commands written");
        telemetry.addData("Time", writer.getTime() + " seconds");

        //Drive commands go here (must match when playing back)
        Tank.motor4(frontLeft, frontRight, backLeft, backRight, one.left_stick_y * -1, one.right_stick_y);
    }

    @Override
    public void stop() {
        writer.write("test.txt");
    }
}