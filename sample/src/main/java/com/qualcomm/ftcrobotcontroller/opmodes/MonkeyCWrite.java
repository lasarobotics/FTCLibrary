package com.qualcomm.ftcrobotcontroller.opmodes;


import com.lasarobotics.library.drive.Mecanum;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.monkeyc.MonkeyC;
import com.lasarobotics.library.sensor.legacy.hitechnic.Gyroscope;
import com.qualcomm.ftcrobotcontroller.MyApplication;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * A Test Teleop
 */
public class MonkeyCWrite extends OpMode {
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
    public void start() {
        writer = new MonkeyC();
    }

    @Override
    public void loop() {
        //update gamepads to controllers with events
        one.update(gamepad1);
        two.update(gamepad2);
        writer.add(one, two);
        telemetry.addData("Status", writer.getCommandsWritten() + " commands written");
        //Drive commands go here (must match when playing back)
        Mecanum.Arcade(one.left_stick_y, one.left_stick_x, one.right_stick_x, leftFront, rightFront, leftBack, rightBack);
    }

    @Override
    public void stop() {
        writer.write("test.txt", MyApplication.getAppContext());
    }
}
