package com.lasarobotics.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Mecanum;
import com.lasarobotics.library.monkeyc.MonkeyData;
import com.lasarobotics.library.monkeyc.MonkeyDo;
import com.lasarobotics.library.sensor.legacy.hitechnic.Gyroscope;
import com.lasarobotics.ftcrobotcontroller.MyApplication;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * Created by Ehsan on 7/9/2015.
 */
public class MonkeyCDo extends OpMode {
    //basic FTC classes
    DcMotor leftFront;
    DcMotor rightFront;
    DcMotor leftBack;
    DcMotor rightBack;
    GyroSensor hw_gyro;
    MonkeyDo reader;
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
        reader = new MonkeyDo("test.txt", MyApplication.getAppContext());
    }
    @Override
    public void start(){
        reader.onStart();
    }

    @Override
    public void loop() {
        MonkeyData m = reader.getNextCommand();
        if (m.hasUpdate()){
            m = reader.getNextCommand();
            one = m.updateControllerOne(one);
            two = m.updateControllerTwo(two);
            telemetry.addData("Status", "Replaying commands for file " + reader.getFilename());
            //Drive commands go here
            Mecanum.Arcade(one.left_stick_y, one.left_stick_x, one.right_stick_x, leftFront, rightFront, leftBack, rightBack);
        }
        else {
            telemetry.addData("Status", "Done replaying");
        }
    }

    @Override
    public void stop() {
    }
}