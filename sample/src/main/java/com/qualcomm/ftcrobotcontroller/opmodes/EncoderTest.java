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
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import java.text.DecimalFormat;

public class EncoderTest extends OpMode implements NavXDataReceiver {

    //Figure out how many encoder counts you need to go one unit of distance
    private static final double ENCODER_COUNTS_PER_UNIT_DISTANCE = 1010.0;

    private static final String NAVX_DIM = "dim";               //device interface module name
    private static final int NAVX_PORT = 0;                     //port on device interface module

    private static final double NAVX_TARGET_ANGLE_DEGREES = 90.0;    //target angle for PID
    private static final double NAVX_YAW_PID_P = 0.005; //0.009; //.01
    private static final double NAVX_YAW_PID_I = 0.0; //0.0002; //.0005
    private static final double NAVX_YAW_PID_D = 0.0; //0.11; //.05

    private static final double PID_P = 0.0009;
    private static final double PID_I = 0.0;
    private static final double PID_D = 0.0;
    private static final double PID_MAX_ACCEL = 0; //3
    private static final double PID_MAX_DECEL = 0;

    private static final double DISTANCE_FEET = 1;              //distance in feet
    private static final double MIN_POWER = 0;              //distance in feet

    private static final double FORWARD_ROTATION_FEROCITY = 4; //how ferociously should we rotate? - lower if oscillating

    private static final DecimalFormat df = new DecimalFormat("#.##");

    NavXDevice navx;
    NavXPIDController yawPIDController;
    NavXPIDController.PIDState yawPIDState;

    EncodedMotor frontLeft, frontRight, backLeft, backRight;
    Controller one;
    PID pidLeft;
    PID pidRight;

    long lastTime = 0;
    int phase = 0;

    public void init() {
        MotorInfo motorInfo = new MotorInfo(ENCODER_COUNTS_PER_UNIT_DISTANCE, Units.Distance.FEET);
        frontLeft = new EncodedMotor(hardwareMap.dcMotor.get("lf"), motorInfo);
        frontRight = new EncodedMotor(hardwareMap.dcMotor.get("rf"), motorInfo);
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("lb"), motorInfo); //set wheel radius for distance calculations
        backRight = new EncodedMotor(hardwareMap.dcMotor.get("rb"), motorInfo);

        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);

        one = new Controller(gamepad1);

        //TODO these are all currently ASYNCHRONOUS
        //TODO meaning that they can only really be called from init() or a state machine
        backLeft.setTargetPosition(DISTANCE_FEET, Units.Distance.FEET);
        backRight.setTargetPosition(DISTANCE_FEET, Units.Distance.FEET);
        frontLeft.setTargetPosition(DISTANCE_FEET, Units.Distance.FEET);
        frontRight.setTargetPosition(DISTANCE_FEET, Units.Distance.FEET);

        //Create PID looper
        pidLeft = new PID();
        pidLeft.setSetpointDistance(backLeft.getMotorInfo(), DISTANCE_FEET, Units.Distance.FEET);
        pidLeft.setMaxAcceleration(PID_MAX_ACCEL);
        pidLeft.setMaxDeceleration(PID_MAX_DECEL);
        pidLeft.setCoefficients(PID_P, PID_I, PID_D);

        pidRight = new PID();
        pidRight.setSetpointDistance(backRight.getMotorInfo(), DISTANCE_FEET, Units.Distance.FEET);
        pidRight.setMaxAcceleration(PID_MAX_ACCEL);
        pidRight.setMaxDeceleration(PID_MAX_DECEL);
        pidRight.setCoefficients(PID_P, PID_I, PID_D);

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
        return MathUtil.deadband(MIN_POWER, MathUtil.coerce(-1, 1, power));
    }

    public void loop() {
        long time = System.nanoTime();
        double timeDelta = (time - lastTime) / 1000000000.0;
        one.update(gamepad1);
        backLeft.update();
        backRight.update();
        frontLeft.update();
        frontRight.update();

        double leftPos = (backLeft.getCurrentPosition() +
                frontLeft.getCurrentPosition()) / 2;
        double rightPos = (backRight.getCurrentPosition() +
                frontRight.getCurrentPosition()) / 2;

        pidLeft.addMeasurement(leftPos, timeDelta);
        pidRight.addMeasurement(rightPos, timeDelta);

        double power = (pidLeft.getOutputValue() + pidRight.getOutputValue()) / 2;
        double powerCompensation = 0;

        navx.displayTelemetry(telemetry);
        if (yawPIDController.isUpdateAvailable(yawPIDState)) {
            telemetry.addData("NavX Debug Coefficients", yawPIDController.getCoefficientDebug());
            DbgLog.error(yawPIDController.getCoefficientDebug());
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


        /*switch (phase) {
            case 0: //rotate to angle
            case 2:
                left = coerce(-powerCompensation);
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
        }*/
        telemetry.addData("Original Power: ", power);
        telemetry.addData("Target: ", pidLeft.getSetpoint());
        telemetry.addData("Wheel Radius (m): ", frontLeft.getMotorInfo().getEffectiveWheelRadius(Units.Distance.METERS));
        telemetry.addData("Wheel Radius (in): ", frontLeft.getMotorInfo().getEffectiveWheelRadius(Units.Distance.INCHES));

        double avgDist = Math.abs(pidLeft.getOutputValue() - pidLeft.getSetpoint()) + Math.abs(pidRight.getOutputValue() - pidRight.getSetpoint());
        avgDist /= 2;
        telemetry.addData("avgDist", avgDist);
        if (false && avgDist < 500) {
            telemetry.addData("DONE", "COASTING...");
            /*backLeft.setPowerFloat();
            backRight.setPowerFloat();
            frontLeft.setPowerFloat();
            frontRight.setPowerFloat();*/
        } else {
            left = MathUtil.coerce(-1, 1, power) -
                    Math.sin(MathUtil.coerce(-1, 1, powerCompensation * FORWARD_ROTATION_FEROCITY) * Math.PI / 2);
            right = MathUtil.coerce(-1, 1, power) +
                    Math.sin(MathUtil.coerce(-1, 1, powerCompensation * FORWARD_ROTATION_FEROCITY) * Math.PI / 2);

            left = coerce(left);
            right = coerce(right);

            //if (phase != 3)
            Tank.motor4(frontLeft, frontRight, backLeft, backRight, left, right);
        }

        telemetry.addData("Left (counts): ", leftPos);
        telemetry.addData("Right (counts): ", rightPos);
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
