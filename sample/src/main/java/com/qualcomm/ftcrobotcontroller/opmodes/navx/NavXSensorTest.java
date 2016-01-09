package com.qualcomm.ftcrobotcontroller.opmodes.navx;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDataReceiver;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXPIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.text.DecimalFormat;

/**
 * OpMode designed to test extended functionality of the NavX sensor
 * Requires four motors to test PID
 */
public class NavXSensorTest extends OpMode implements NavXDataReceiver {

    private static final String NAVX_DIM = "dim";               //device interface module name
    private static final int NAVX_PORT = 1;                     //port on device interface module

    private static final double NAVX_TOLERANCE_DEGREES = 2.0;   //degrees of tolerance for PID controllers
    private static final double NAVX_TARGET_ANGLE_DEGREES = 90.0;    //target angle for PID
    private static final double NAVX_YAW_PID_P = 0.005;
    private static final double NAVX_YAW_PID_I = 0.0;
    private static final double NAVX_YAW_PID_D = 0.0;

    private static final DecimalFormat df = new DecimalFormat("#.##");

    EncodedMotor frontLeft, frontRight, backLeft, backRight; //make sure these have encoders!
    Controller one;
    NavXDevice navx;
    NavXPIDController yawPIDController;
    NavXPIDController.PIDState yawPIDState;

    public void init() {
        //Create motors WITH ENCODERS (important!)
        frontLeft = new EncodedMotor(hardwareMap.dcMotor.get("frontLeft"));
        frontRight = new EncodedMotor(hardwareMap.dcMotor.get("frontRight"));
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("backLeft"));
        backRight = new EncodedMotor(hardwareMap.dcMotor.get("backRight"));

        //Instantiate controllers
        one = new Controller(gamepad1);

        //Initialize the navX controller
        //Make sure to implement NavXDataReceiver
        navx = new NavXDevice(hardwareMap, NAVX_DIM, NAVX_PORT);
        navx.registerCallback(this);

        //Initialize the navX PID controller
        //Using the yaw axis, we can find out how far we move forward
        yawPIDController = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
        //Set the target location
        yawPIDController.setSetpoint(NAVX_TARGET_ANGLE_DEGREES);
        //Allow crossing over the bounds (see setContinuous() documentation)
        yawPIDController.setContinuous(true);
        //Set angle tolerance
        yawPIDController.setTolerance(NavXPIDController.ToleranceType.ABSOLUTE, NAVX_TOLERANCE_DEGREES);
        //Set P,I,D coefficients
        yawPIDController.setPID(NAVX_YAW_PID_P, NAVX_YAW_PID_I, NAVX_YAW_PID_D);
        //Start data collection
        yawPIDController.start();
    }

    @Override
    public void start() {
        navx.reset();
    }

    public void loop() {
        one.update(gamepad1);
        navx.displayTelemetry(telemetry);

        if (yawPIDController.isUpdateAvailable(yawPIDState)) {
            if (yawPIDState.isOnTarget()) {
                frontLeft.setPowerFloat();
                frontRight.setPowerFloat();
                backLeft.setPowerFloat();
                backRight.setPowerFloat();
                telemetry.addData("Motor Power", df.format(0.00));
            } else {
                double power = yawPIDState.getOutput();
                Tank.motor4(frontLeft, frontRight, backLeft, backRight,
                        power, power);
                telemetry.addData("Motor Power", df.format(power));
            }
        }
    }

    public void stop() {
        navx.stop();
        yawPIDController.stop();
    }

    @Override
    public void dataReceived(long timeDelta) {
        telemetry.addData("NavX Collision", navx.hasCollided() ? "COLLIDED!" : "No collision");
        telemetry.addData("NavX Jerk", navx.getJerk());
    }
}
