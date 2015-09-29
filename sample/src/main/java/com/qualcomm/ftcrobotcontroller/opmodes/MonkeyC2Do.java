package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.MyApplication;
import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Mecanum;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.monkeyc.MonkeyData;
import com.lasarobotics.library.monkeyc.MonkeyDo;
import com.lasarobotics.library.sensor.legacy.hitechnic.Gyroscope;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;

/**
 * MonkeyC2 Do Test
 */
public class MonkeyC2Do extends OpMode {
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

    public static boolean isTested = false;
    public static void test()
    {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isTested = true;
    }

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
    public void start() {
        isTested = false;
        reader.onStart();
    }

    @Override
    public void loop() {
        MonkeyData m = reader.getNextCommand();
        if (m.hasUpdate()){
            m = reader.getNextCommand();
            one = m.updateControllerOne(one);
            two = m.updateControllerTwo(two);

            if (one.x == ButtonState.PRESSED)
            {
                test();
                reader.waitForController();
            }

            if (isTested)
            {
                telemetry.addData("X KEY", "PRESSED!");
            }
            else
            {
                telemetry.addData("X KEY", "Not pressed");
            }

            telemetry.addData("Status", "Replaying commands for file " + reader.getFilename());
            //Drive commands go here
            Tank.motor4(leftFront, rightFront, leftBack, rightBack, one.left_stick_y, one.right_stick_y);
        }
        else {
            telemetry.addData("Status", "Done replaying!");
            //We can choose to stop the timer here, but why...
        }
        telemetry.addData("Commands", reader.getCommandsRead() + " read");
        telemetry.addData("Time", reader.getTime() + " seconds");
    }

    @Override
    public void stop() {
    }
}