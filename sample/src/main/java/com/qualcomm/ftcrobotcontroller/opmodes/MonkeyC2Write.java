package com.qualcomm.ftcrobotcontroller.opmodes;


import com.qualcomm.ftcrobotcontroller.MyApplication;
import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Mecanum;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.monkeyc.MonkeyC;
import com.lasarobotics.library.sensor.legacy.hitechnic.Gyroscope;
import com.qualcomm.ftcrobotcontroller.opmodes.MonkeyC2Do;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * MonkeyC2 Write Test
 */
public class MonkeyC2Write extends OpMode {
    //basic FTC classes
    DcMotor leftFront;
    DcMotor rightFront;
    DcMotor leftBack;
    DcMotor rightBack;
    GyroSensor hw_gyro;
    MonkeyC writer;
    //advanced FTC classes
    Controller one = new Controller();
    Controller two = new Controller();
    Gyroscope gyro;

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
        Tank.Motor4(leftFront, rightFront, leftBack, rightBack, one.left_stick_y, one.right_stick_y);
    }

    @Override
    public void stop() {
        writer.write("test.txt", MyApplication.getAppContext());
    }
}