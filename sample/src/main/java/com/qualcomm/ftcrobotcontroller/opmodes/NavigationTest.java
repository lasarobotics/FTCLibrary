package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.nav.Navigator;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.text.DecimalFormat;

public class NavigationTest extends OpMode {

    //NavX setup
    private static final String NAVX_DIM = "dim";               //device interface module name
    private static final int NAVX_PORT = 1;                     //port on device interface module

    //PID setup
    private static final double ROT_KP = 0.005;
    private static final double ROT_KI = 0;
    private static final double ROT_KD = 0;
    private static final double TRANS_KP = 0.0009;
    private static final double TRANS_KI = 0.0;
    private static final double TRANS_KD = 0.0;
    private static final double PID_MAX_ACCEL = 3; //Maximum acceleration
    private static final double PID_MAX_DECEL = 0; //Maximum deceleration
    private static final double ROTATION_BIAS = 4; //how ferociously should we rotate? - lower if oscillating

    //Anti-stall protection
    private static final double ANTISTALL_MIN_POWER = 0;    //minimum motor power
    private static final double ANTISTALL_POWER_BOOST = 0;  //power boost when we're not quite at the target

    //Formatting
    private static final DecimalFormat df = new DecimalFormat("#.##");

    //Navigation
    NavXDevice navx;
    Navigator navigator;
    EncodedMotor frontLeft, frontRight, backLeft, backRight;

    long lastTime = 0;

    public void init() {

    }

    public void init_loop() {
        lastTime = System.nanoTime();
    }

    public void loop() {

    }

    public void stop() {

    }
}
