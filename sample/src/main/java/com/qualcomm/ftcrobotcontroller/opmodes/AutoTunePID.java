package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.qualcomm.ftccommon.DbgLog;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Automatically tunes PID parameters
 */
public class AutoTunePID extends LinearOpMode {
    protected static DcMotor motorFR;
    protected static DcMotor motorFL;
    protected static DcMotor motorBR;
    protected static DcMotor motorBL;
    protected static volatile double[] rollAngle = new double[2], pitchAngle = new double[2], yawAngle = new double[2];
    protected double kP = 0.06;
    protected double kI = 0.02;
    protected double kD = 0.005;
    double dt;
    double prevError;
    double error = 0;
    double iError = 0;
    double dError;
    double time = getRuntime();
    double target_heading;
    TreeMap<Double, Double> time_displacement = new TreeMap<Double, Double>();
    NavXDevice navx;

    public void resetPID() throws InterruptedException {
        if (!opModeIsActive())
            return;
        if (Math.abs(target_heading - getGyroYaw() + 360) < Math.abs(target_heading - getGyroYaw()))
            error = target_heading - getGyroYaw() + 360;
        else
            error = target_heading - getGyroYaw();
        time = getRuntime();
        iError = 0;
    }

    public double tune_PID(double timeout) throws InterruptedException {
        long timer = (long) (timeout * Math.pow(10, 3)) + System.currentTimeMillis();
        double kP = 0.01; // start value. It IS too small.
        //double max;
        target_heading = 10;
        resetPID();
        while (!isTuned(kP, 5) && System.currentTimeMillis() < timer) {
            kP *= 2;
            target_heading = (target_heading == 10 ? 0 : 10);
        }
        kP /= 2;
        while (!isTuned(kP, 5) && System.currentTimeMillis() < timer) {
            kP *= 1.1;
            target_heading = (target_heading == 10 ? 0 : 10);
        }
        kP /= 1.1;
        while (!isTuned(kP, 5) && System.currentTimeMillis() < timer) {
            kP += .0005;
            target_heading = (target_heading == 10 ? 0 : 10);
        }
        kP -= .0005;
        Iterator<Double> it = time_displacement.keySet().iterator();
        double Tu = 0; // Tu is the period of the oscillation. T meaning period in physics.
        double previous = it.next();
        //finding the average period
        //note: next-previous is one half period... so needs to be doubled at the end
        while (it.hasNext()) {
            double next = it.next();
            Tu += next - previous;
            previous = next;
        }
        Tu = Tu * 2 / (time_displacement.size() - 1); // calculate period based off of some number of half-periods
        this.kP = 0.6 * kP;
        this.kI = 2 * this.kP / Tu;
        this.kD = this.kP * Tu / 8;
        return time_displacement.get(previous); // returns the amplitude of the
    }

    public double getGyroYaw() throws InterruptedException {
        return navx.getHeading();
    }

    public double get_PID() throws InterruptedException {
        return get_PID(getGyroYaw(), kP, kI, kD);
    }

    public double get_PID(double gyro) throws InterruptedException {
        return get_PID(gyro, kP, kI, kD);
    }

    public double get_PID(double kP, double kI, double kD) throws InterruptedException {
        return get_PID(getGyroYaw(), kP, kI, kD);
    }

    public double get_PID(double gyro, double kP, double kI, double kD) throws InterruptedException {
        if (!opModeIsActive())
            return 0;
        dt = getRuntime() - time;
        time = getRuntime();
        prevError = error;
        if (Math.abs(target_heading - gyro + 360) < Math.abs(target_heading - gyro))
            error = target_heading - gyro + 360;
        else
            error = target_heading - gyro;
        dError = (error - prevError) / dt;
        //make this a reimann right sum if needed to improve speed at the cost of accuracy
        iError = Range.clip(iError + 0.5 * (prevError + error) * dt, -125, 125) * 0.99; // a trapezoidal approximation of the integral.
        return kP * error + kD * dError + kI * iError;
    }

    public boolean isTuned(double kP, double timeout) throws InterruptedException {
        //TODO: add debug log info for debugging
        time_displacement = new TreeMap<Double, Double>();
        long timer = (long) (timeout * Math.pow(10, 3)) + System.currentTimeMillis();
        resetStartTime();
        double PID_change;
        double right;
        double left;
        double new_yaw = getGyroYaw();
        resetPID();
        double sign = Math.signum(get_PID(kP, 0, 0));
        while (System.currentTimeMillis() < timer) {
            while (new_yaw == getGyroYaw()) {
                waitOneFullHardwareCycle();
            }
            new_yaw = getGyroYaw();
            PID_change = get_PID(new_yaw, kP, 0, 0);
            if (sign != Math.signum(PID_change)) {
                time_displacement.put(getRuntime(), error);
                sign *= -1;
            }
            //keep the absolute value of the motors above 0.3 and less than 0.7
            right = Range.clip(PID_change, -1, 1);
            left = -right;
            setRightPower(right);
            setLeftPower(left);
            DbgLog.error(String.format("kP: %.4f", kP));
            DbgLog.error(time_displacement.toString());
            DbgLog.error(String.format("k*error: %.2f, error: %.2f", kP * error, error));
            DbgLog.error(String.format("gyro:%.2f, target:%.2f, PID (right):%.2f", getGyroYaw(), target_heading, PID_change));
        }
        DbgLog.error(String.format("time_displacement.size(): %d", time_displacement.size()));
        DbgLog.error(time_displacement.toString());
        if (time_displacement.size() < 3)
            return false;
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (Double aDouble : time_displacement.keySet()) {
            double next = Math.abs(time_displacement.get(aDouble)); // measuring amplitude differentials.
            max = Math.max(max, next);
            min = Math.min(min, next);
        }
        DbgLog.error(String.format("max-min: %.2f", max - min));
        return max - min <= 2;
    }

    void setLeftPower(double power) {
        if (!opModeIsActive())
            return;
        motorBL.setPower(Range.clip(-power, -1, 1));
        motorFL.setPower(Range.clip(-power, -1, 1));
    }

    void setRightPower(double power) {
        if (!opModeIsActive())
            return;
        motorBR.setPower(Range.clip(power, -1, 1));
        motorFR.setPower(Range.clip(power, -1, 1));
    }

    public void runOpMode() throws InterruptedException {
        motorFR = hardwareMap.dcMotor.get("rf");
        motorBR = hardwareMap.dcMotor.get("rb");
        motorFL = hardwareMap.dcMotor.get("lf");
        motorBL = hardwareMap.dcMotor.get("lb");
        navx = new NavXDevice(hardwareMap, "dim", 0);
        waitForStart();
        tune_PID(120);

        DbgLog.error("kP: %.5f, kI: %.5f, kD: %.5f", kP, kI, kD);
        telemetry.addData("hi", String.format(" kP: %.5f, kI: %.5f, kD: %.5f", kP, kI, kD));
        //kP = 0.01061
        //kI = 0.02116
        //kD = 0.00133
    }
}
