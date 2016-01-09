package com.qualcomm.ftcrobotcontroller.opmodes.navx;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDataReceiver;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.text.DecimalFormat;

/**
 * OpMode designed to test extended functionality of the NavX sensor
 * Requires four motors to test PID
 */
public class NavXSensorTest extends OpMode implements NavXDataReceiver {

    private static final String NAVX_DIM = "dim";               //device interface module name
    private static final int NAVX_PORT = 0;                     //port on device interface module

    private static final double NAVX_TOLERANCE_DEGREES = 2.0;   //degrees of tolerance for PID controllers
    private static final double NAVX_TARGET_ANGLE_DEGREES = 90.0;    //target angle for PID
    private static final double NAVX_YAW_PID_P = 0.005;
    private static final double NAVX_YAW_PID_I = 0.0;
    private static final double NAVX_YAW_PID_D = 0.0;

    private static final DecimalFormat df = new DecimalFormat("#.##");

    Controller one;
    NavXDevice navx;

    public void init() {
        //Instantiate controllers
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
    }

    public void stop() {
        navx.stop();
    }

    @Override
    public void dataReceived(long timeDelta) {
        telemetry.addData("NavX Collision", navx.hasCollided() ? "COLLIDED!" : "No collision");
        telemetry.addData("NavX Jerk", df.format(navx.getJerk()));
    }
}
