package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * PurpleBot (TM) Test Teleop
 */
public class TestPurpleBot extends OpMode {

    DcMotor leftFront, rightFront, leftBack, rightBack;
    Controller one;

    @Override
    public void init() {
        leftFront = hardwareMap.dcMotor.get("lf");
        rightFront = hardwareMap.dcMotor.get("rf");
        leftBack = hardwareMap.dcMotor.get("lb");
        rightBack = hardwareMap.dcMotor.get("rb");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {
        //leftFront.setPower(0.3);
        //rightFront.setPower(0.3);

        Tank.motor4(leftFront, rightFront, leftBack, rightBack, 1, 1);
    }

    @Override
    public void stop() {
        Tank.motor4(leftFront, rightFront, leftBack, rightBack, 0, 0);
    }
}
