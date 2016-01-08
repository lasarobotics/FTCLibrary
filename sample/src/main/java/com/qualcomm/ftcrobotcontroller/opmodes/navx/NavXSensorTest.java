package com.qualcomm.ftcrobotcontroller.opmodes.navx;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * OpMode designed to test extended functionality of the NavX sensor
 * Requires four motors to test PID
 */
public class NavXSensorTest extends OpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight;
    Controller one;
    NavXDevice navx;

    public void init() {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);
        navx = new NavXDevice(hardwareMap, "dim", 1);
    }

    @Override
    public void init_loop() {
        navx.reset();
    }

    public void loop() {
        one.update(gamepad1);
        navx.displayTelemetry(telemetry);


        Tank.motor4(frontLeft, frontRight, backLeft, backRight, one.left_stick_y, one.right_stick_y);
    }

    public void stop() {
        navx.stop();
    }
}
