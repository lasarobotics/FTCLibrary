package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.monkeyc.MonkeyData;
import com.lasarobotics.library.monkeyc.MonkeyDo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * MonkeyC2 Do Test
 */
public class MonkeyCDo extends OpMode {
    public static boolean isTested = false;
    //basic FTC classes
    DcMotor frontLeft, frontRight, backLeft, backRight;
    Controller one, two;
    MonkeyDo reader;

    public static void test() {
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

        frontLeft = hardwareMap.dcMotor.get("lf");
        frontRight = hardwareMap.dcMotor.get("rf");
        backLeft = hardwareMap.dcMotor.get("lb");
        backRight = hardwareMap.dcMotor.get("rb");

        one = new Controller(gamepad1);
        two = new Controller(gamepad2);

        //frontLeft.setDirection(DcMotor.Direction.REVERSE);
        //frontRight.setDirection(DcMotor.Direction.REVERSE);

        reader = new MonkeyDo("test.txt");
    }

    @Override
    public void start() {
        isTested = false;
        reader.onStart();
    }

    @Override
    public void loop() {
        MonkeyData m = reader.getNextCommand();
        if (m.hasUpdate()) {
            m = reader.getNextCommand();
            one = m.updateControllerOne(one);
            two = m.updateControllerTwo(two);

            if (one.x == ButtonState.PRESSED) {
                reader.pauseTime();
                test();
                reader.resumeTime();
            }

            if (isTested) {
                telemetry.addData("X KEY", "PRESSED!");
            } else {
                telemetry.addData("X KEY", "Not pressed");
            }

            telemetry.addData("Status", "Replaying commands for file " + reader.getFilename());

            //Drive commands go here
            Tank.motor4(frontLeft, frontRight, backLeft, backRight, -one.left_stick_y, one.right_stick_y);
        } else {
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