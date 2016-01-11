package com.qualcomm.ftcrobotcontroller.opmodes;

import com.kauailabs.navx.ftc.navXPIDController;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.nav.MotorInfo;
import com.lasarobotics.library.nav.PID;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDataReceiver;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXPIDController;
import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Units;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.text.DecimalFormat;

public class EncoderTest extends OpMode implements NavXDataReceiver {

    private static final double WHEEL_RADIUS = 2;
    private static final Units.Distance WHEEL_RADIUS_UNIT = Units.Distance.INCHES;
    private static final double WHEEL_MECHANICAL_ADVANTAGE = 2;

    private static final String NAVX_DIM = "dim";               //device interface module name
    private static final int NAVX_PORT = 1;                     //port on device interface module

    private static final double NAVX_TOLERANCE_DEGREES = 10.0;   //degrees of tolerance for PID controllers
    private static final double NAVX_TARGET_ANGLE_DEGREES = 90.0;    //target angle for PID
    private static final double NAVX_YAW_PID_P = 0.05;
    private static final double NAVX_YAW_PID_I = 0.0;
    private static final double NAVX_YAW_PID_D = 0.0;

    private static final double DISTANCE_FEET = 1;              //distance in feet

    private static final DecimalFormat df = new DecimalFormat("#.##");

    NavXDevice navx;
    NavXPIDController yawPIDController;
    NavXPIDController.PIDState yawPIDState;

    DcMotor frontLeft, frontRight, backRight;
    EncodedMotor backLeft;
    Controller one;
    PID pidBackLeft;

    long lastTime = 0;
    int phase = 0;

    public void init() {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("backLeft"),
                new MotorInfo(WHEEL_RADIUS, WHEEL_RADIUS_UNIT, WHEEL_MECHANICAL_ADVANTAGE)); //set wheel radius for distance calculations
        backRight = hardwareMap.dcMotor.get("backRight");

        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);

        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        one = new Controller(gamepad1);

        //TODO these are all currently ASYNCHRONOUS
        //TODO meaning that they can only really be called from init() or a state machine
        backLeft.setTargetPosition(DISTANCE_FEET, Units.Distance.FEET);
        backLeft.reset();

        //Create PID looper
        pidBackLeft = new PID();
        pidBackLeft.setSetpoint(Units.Distance.convertToAngle(DISTANCE_FEET,
                WHEEL_RADIUS / WHEEL_MECHANICAL_ADVANTAGE,
                WHEEL_RADIUS_UNIT, Units.Distance.FEET, Units.Angle.ENCODER_COUNTS));
        pidBackLeft.setMaxAcceleration(0.02);

        //Initialize the navX controller
        //Make sure to implement NavXDataReceiver
        navx = new NavXDevice(hardwareMap, NAVX_DIM, NAVX_PORT);
        navx.reset();
        navx.registerCallback(this);

        //Initialize the navX PID controller
        //Using the yaw axis, we can find out how far we move forward
        yawPIDController = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
        //Set the target location
        yawPIDController.setSetpoint(NAVX_TARGET_ANGLE_DEGREES);
        //Allow crossing over the bounds (see setContinuous() documentation)
        yawPIDController.setContinuous(true);
        //Set angle tolerance
        //yawPIDController.setTolerance(NavXPIDController.ToleranceType.ABSOLUTE, NAVX_TOLERANCE_DEGREES);
        //Set P,I,D coefficients
        yawPIDController.setPID(NAVX_YAW_PID_P, NAVX_YAW_PID_I, NAVX_YAW_PID_D);
        //Disable antistall (more accurate, and since this is only used for compensation, we can ignore the stalls)
        yawPIDController.disableAntistall();
        //Making the tolerance very small makes the robot work hard to get to get to a very close estimate
        yawPIDController.setTolerance(navXPIDController.ToleranceType.NONE, 0);
        //Start data collection
        yawPIDController.start();

        //Initiate blank PID state
        yawPIDState = new NavXPIDController.PIDState();
    }

    public void init_loop() {
        lastTime = System.nanoTime();
    }

    private double coerce(double power) {
        return MathUtil.deadband(0.02, MathUtil.coerce(-1, 1, power));
    }

    public void loop() {
        long time = System.nanoTime();
        double timeDelta = (time - lastTime) / 1000000000.0;
        one.update(gamepad1);
        backLeft.update();
        pidBackLeft.addMeasurement(Math.abs(backLeft.getCurrentPosition()), timeDelta);

        double power = pidBackLeft.getOutputValue();
        double powerCompensation = 0;

        navx.displayTelemetry(telemetry);
        if (yawPIDController.isUpdateAvailable(yawPIDState)) {
            if (!yawPIDState.isOnTarget()) {
                powerCompensation = yawPIDState.getOutput();
            }
        }

        //Divide test into 3 segments
        //1 Rotate to angle
        //2 Move and rotate to angle
        //3 Rotate to angle

        double left = 0;
        double right = 0;

        switch (phase) {
            case 0: //rotate to angle
            case 2:
                left = coerce(powerCompensation);
                right = coerce(powerCompensation);

                if (MathUtil.equal(powerCompensation, 0)) {
                    if (backLeft.hasReachedPosition(DISTANCE_FEET, Units.Distance.FEET))
                        phase = 3;
                    else
                        phase = 1;
                } else
                    break;
            case 1: //move and rotate
                if (phase != 3) {
                    left = MathUtil.coerce(-1, 1, power) +
                            Math.sin(MathUtil.coerce(-1, 1, powerCompensation) * Math.PI / 2);
                    right = MathUtil.coerce(-1, 1, power) -
                            Math.sin(MathUtil.coerce(-1, 1, powerCompensation) * Math.PI / 2);
                    left = coerce(left);
                    right = coerce(right);

                    if (backLeft.hasReachedPosition(DISTANCE_FEET, Units.Distance.FEET)) {
                        phase = 2;
                        break;
                    }
                    //TODO fix this - must go back to 2 immediately
                    else
                        break;
                }
            case 3: //coast
                frontLeft.setPowerFloat();
                frontRight.setPowerFloat();
                backLeft.setPowerFloat();
                backRight.setPowerFloat();
                return;
        }

        /*if (backLeft.hasReachedPosition(DISTANCE_FEET, Units.Distance.FEET)) {
            frontLeft.setPowerFloat();
            frontRight.setPowerFloat();
            backLeft.setPowerFloat();
            backRight.setPowerFloat();
        }
        else
        {*/
        telemetry.addData("Original Power: ", power);


        //Only problem is if power = 0

        //Take the directional power compensation and multiply it by the power

        //left = MathUtil.coerce(-1, 1, -1 + powerCompensation);
        //right = MathUtil.coerce(-1, 1, 1 + powerCompensation);

        //double[] result = MathUtil.normalize(left, right, 1, true);
        //double powerFactor = power >= 1 ? 1 : MathUtil.coerce(-1, 1, Math.abs(power));
        //double powerFactor = 1;
        //left = coerce(result[0] * powerFactor);
        //right = coerce(result[1] * powerFactor);
        left = coerce(left);
        right = coerce(right);
        //}

        Tank.motor4(frontLeft, frontRight, backLeft, backRight, left, right);

        telemetry.addData("Back Left (counts): ", backLeft.getCurrentPosition());
        telemetry.addData("Back Left (feet): ", backLeft.getCurrentPosition(Units.Distance.FEET));
        telemetry.addData("Motor Power: ", power);
        telemetry.addData("Actual Power: ", left + ", " + right);
        telemetry.addData("Time Delta: ", timeDelta);
        telemetry.addData("Power Compensation", df.format(powerCompensation));

        lastTime = time;
    }

    @Override
    public void dataReceived(long timeDelta) {
        telemetry.addData("navX Collision", navx.hasCollided() ? "COLLIDED!" : "No collision");
        telemetry.addData("navX Jerk", df.format(navx.getJerk()));
    }

    public void stop() {
        navx.stop();
        yawPIDController.stop();
        frontLeft.setPowerFloat();
        frontRight.setPowerFloat();
        backLeft.setPowerFloat();
        backRight.setPowerFloat();
    }
}
