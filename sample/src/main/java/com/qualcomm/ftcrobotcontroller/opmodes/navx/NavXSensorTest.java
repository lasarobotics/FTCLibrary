package com.qualcomm.ftcrobotcontroller.opmodes.navx;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDataReceiver;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * OpMode designed to test extended functionality of the NavX sensor
 * Requires four motors to test PID
 */
public class NavXSensorTest extends OpMode implements NavXDataReceiver {

    final String NAVX_DIM = "dim";   //device interface module name
    final int NAVX_PORT = 1;         //port on device interface module

    DcMotor frontLeft, frontRight, backLeft, backRight;
    Controller one;
    NavXDevice navx;

    public void init() {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);

        //Initialize the navX controller
        //Make sure to implement NavXDataReceiver
        navx = new NavXDevice(hardwareMap, NAVX_DIM, NAVX_PORT);
        navx.registerCallback(this);
    }

    @Override
    public void start() {
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

    @Override
    public void dataReceived(long timeDelta) {
        telemetry.addData("NavX Collision", navx.hasCollided() ? "COLLIDED!" : "No collision");
        telemetry.addData("NavX Jerk", navx.getJerk());
    }
}
