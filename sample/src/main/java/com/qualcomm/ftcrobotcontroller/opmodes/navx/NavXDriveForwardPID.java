package com.qualcomm.ftcrobotcontroller.opmodes.navx;

import com.kauailabs.navx.ftc.navXPIDController;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.nav.MotorInfo;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDataReceiver;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXPIDController;
import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Units;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.text.DecimalFormat;

/**
 * OpMode designed to test extended functionality of the NavX sensor
 * Requires four motors to test PID
 */
public class NavXDriveForwardPID extends OpMode implements NavXDataReceiver {

    private static final String NAVX_DIM = "dim";               //device interface module name
    private static final int NAVX_PORT = 1;                     //port on device interface module

    private static final double NAVX_TOLERANCE_DEGREES = 2.0;   //degrees of tolerance for PID controllers
    private static final double NAVX_TARGET_ANGLE_DEGREES = 180.0;    //target angle for PID
    private static final double NAVX_YAW_PID_P = 0.005;
    private static final double NAVX_YAW_PID_I = 0.0;
    private static final double NAVX_YAW_PID_D = 0.0;

    private static final double WHEEL_RADIUS = 2;
    private static final Units.Distance WHEEL_RADIUS_UNIT = Units.Distance.INCHES;

    private static final DecimalFormat df = new DecimalFormat("#.##");

    EncodedMotor frontLeft, frontRight, backLeft, backRight; //make sure these have encoders!
    Controller one;
    NavXDevice navx;
    NavXPIDController yawPIDController;
    NavXPIDController.PIDState yawPIDState;

    public void init() {
        //Create motors WITH ENCODERS (highly preferable)
        frontLeft = new EncodedMotor(hardwareMap.dcMotor.get("frontLeft"), new MotorInfo(WHEEL_RADIUS, WHEEL_RADIUS_UNIT));
        frontRight = new EncodedMotor(hardwareMap.dcMotor.get("frontRight"), new MotorInfo(WHEEL_RADIUS, WHEEL_RADIUS_UNIT));
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("backLeft"), new MotorInfo(WHEEL_RADIUS, WHEEL_RADIUS_UNIT));
        backRight = new EncodedMotor(hardwareMap.dcMotor.get("backRight"), new MotorInfo(WHEEL_RADIUS, WHEEL_RADIUS_UNIT));

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
        //Enable antistall (less accurate, but prevents motors from stalling)
        yawPIDController.enableAntistall();
        yawPIDController.setAntistallDeadband(0.02);
        //Making the tolerance very small makes the robot work hard to get to get to a very close estimate
        yawPIDController.setTolerance(navXPIDController.ToleranceType.NONE, 0);
        //Start data collection
        yawPIDController.start();

        //Initiate blank PID state
        yawPIDState = new NavXPIDController.PIDState();
    }

    @Override
    public void start() {
        navx.reset();
    }

    public void loop() {
        one.update(gamepad1);
        navx.displayTelemetry(telemetry);

        /* Drive straight forward at 1/2 of full drive speed */
        final double driveSpeed = 0.5;

        if (yawPIDController.isUpdateAvailable(yawPIDState)) {
            if (yawPIDState.isOnTarget()) {
                backLeft.setPower(driveSpeed);
                frontLeft.setPower(driveSpeed);
                backRight.setPower(driveSpeed);
                frontRight.setPower(driveSpeed);
                telemetry.addData("Motor Output", df.format(driveSpeed) + ", " +
                        df.format(driveSpeed));
            } else {
                double output = yawPIDState.getOutput();
                backLeft.setPower(MathUtil.coerce(-1, 1, driveSpeed + output));
                frontLeft.setPower(MathUtil.coerce(-1, 1, driveSpeed + output));
                backRight.setPower(MathUtil.coerce(-1, 1, driveSpeed - output));
                frontRight.setPower(MathUtil.coerce(-1, 1, driveSpeed - output));
                telemetry.addData("Motor Power", df.format(MathUtil.coerce(-1, 1, driveSpeed + output)) + ", " +
                        df.format(MathUtil.coerce(-1, 1, driveSpeed - output)));
            }
        }
    }

    public void stop() {
        navx.stop();
        yawPIDController.stop();
    }

    @Override
    public void dataReceived(long timeDelta) {
        telemetry.addData("navX Collision", navx.hasCollided() ? "COLLIDED!" : "No collision");
        telemetry.addData("navX Jerk", df.format(navx.getJerk()));
    }
}
