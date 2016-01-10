package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.drive.Tank;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.nav.MotorInfo;
import com.lasarobotics.library.nav.PID;
import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Units;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class EncoderTest extends OpMode {

    private static final double WHEEL_RADIUS = 2;
    private static final Units.Distance WHEEL_RADIUS_UNIT = Units.Distance.INCHES;
    private static final double WHEEL_MECHANICAL_ADVANTAGE = 2;

    DcMotor frontLeft, frontRight, backRight;
    EncodedMotor backLeft;
    Controller one;
    PID pidBackLeft;

    long lastTime = 0;

    public void init() {
        frontLeft = hardwareMap.dcMotor.get("frontLeft");
        frontRight = hardwareMap.dcMotor.get("frontRight");
        backLeft = new EncodedMotor(hardwareMap.dcMotor.get("backLeft"),
                new MotorInfo(WHEEL_RADIUS, WHEEL_RADIUS_UNIT, WHEEL_MECHANICAL_ADVANTAGE)); //set wheel radius for distance calculations
        backRight = hardwareMap.dcMotor.get("backRight");

        one = new Controller(gamepad1);

        //TODO these are all currently ASYNCHRONOUS
        //TODO meaning that they can only really be called from init() or a state machine
        backLeft.setTargetPosition(1, Units.Distance.FEET);
        backLeft.reset();

        //Create PID looper
        pidBackLeft = new PID();
        pidBackLeft.setSetpoint(Units.Distance.convertToAngle(1, WHEEL_RADIUS, WHEEL_RADIUS_UNIT,
                Units.Distance.FEET, Units.Angle.ENCODER_COUNTS));
    }

    public void init_loop() {
        lastTime = System.nanoTime();
    }

    public void loop() {
        long time = System.nanoTime();
        double timeDelta = (time - lastTime) / 1000000000.0;
        one.update(gamepad1);
        backLeft.update();
        pidBackLeft.addMeasurement(Math.abs(backLeft.getCurrentPosition()), timeDelta);
        double power = pidBackLeft.getOutputValue();

        Tank.motor4(frontLeft, frontRight, backLeft, backRight,
                MathUtil.coerce(-1, 1, power), MathUtil.coerce(-1, 1, -power));

        telemetry.addData("Back Left (counts): ", backLeft.getCurrentPosition());
        telemetry.addData("Back Left (feet): ", backLeft.getCurrentPosition(Units.Distance.FEET));
        telemetry.addData("Motor Power: ", power);
        telemetry.addData("Time Delta: ", timeDelta);

        lastTime = time;
    }

    public void stop() {

    }
}
