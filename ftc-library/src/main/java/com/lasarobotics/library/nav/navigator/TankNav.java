package com.lasarobotics.library.nav.navigator;

import com.kauailabs.navx.ftc.navXPIDController;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.nav.MotorInfo;
import com.lasarobotics.library.nav.PID;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXPIDController;
import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Units;
import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * TankDrive navigation
 */
public class TankNav extends Navigator {
    EncodedMotor[] motorsLeft, motorsRight;
    NavigationParams params;
    NavXPIDController rotPID;
    PID transPIDLeft;
    PID transPIDRight;
    NavXPIDController.PIDState rotState;
    AsyncMotorResult lastResult;
    long lastTime = System.nanoTime();
    Telemetry telemetry;

    public TankNav(NavXDevice navx, EncodedMotor[] motorsLeft, EncodedMotor[] motorsRight, Telemetry telemetry) {
        super(navx);
        this.motorsLeft = motorsLeft;
        this.motorsRight = motorsRight;
        this.telemetry = telemetry;
        this.params = new NavigationParams();
    }

    public TankNav(NavXDevice navx, NavigationParams params, EncodedMotor[] motorsLeft, EncodedMotor[] motorsRight, Telemetry telemetry) {
        super(navx);
        this.motorsLeft = motorsLeft;
        this.motorsRight = motorsRight;
        this.telemetry = telemetry;
        this.params = params;
    }

    public void coast() {
        for (EncodedMotor m : motorsLeft)
            m.setPowerFloat();
        for (EncodedMotor m : motorsRight)
            m.setPowerFloat();
    }

    public void stop() {
        for (EncodedMotor m : motorsLeft)
            m.setPower(0.0);
        for (EncodedMotor m : motorsRight)
            m.setPower(0.0);
    }

    private double updateTime() {
        long time = System.nanoTime();
        double dt = (time - lastTime) / 1000000000.0;
        lastTime = time;
        return dt;
    }

    private void updateMotors() {
        for (EncodedMotor m : motorsLeft)
            m.update();
        for (EncodedMotor m : motorsRight)
            m.update();
    }

    private double meanPos(EncodedMotor[] motors) {
        double mean = 0.0;
        for (EncodedMotor m : motors)
            mean += m.getCurrentPosition();
        mean /= motors.length;
        return mean;
    }

    public void rotateInPlaceAsyncStart(double targetDegrees, double angleTolerance) {
        lastResult = new AsyncMotorResult();
        navx.reset();
        try {
            navx.waitForReset();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        //Initialize the navX PID controller
        //Using the yaw axis, we can find out how far we move forward
        rotPID = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
        //Set the target location
        rotPID.setSetpoint(targetDegrees);
        //Allow crossing over the bounds (see setContinuous() documentation)
        rotPID.setContinuous(true);
        //Set P,I,D coefficients
        rotPID.setCoefficients(params.kRotMoving);
        //Disable antistall (more accurate, and since this is only used for compensation, we can ignore the stalls)
        rotPID.disableAntistall();
        //Making the tolerance very small makes the robot work hard to get to get to a very close estimate
        if (angleTolerance > 0)
            rotPID.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, angleTolerance);
        else
            rotPID.setTolerance(navXPIDController.ToleranceType.NONE, 0);
        //Start data collection
        rotPID.start();

        //Initiate blank PID state
        rotState = new NavXPIDController.PIDState();

        updateTime();
    }

    public AsyncMotorResult rotateInPlaceAsyncRun(double powerFactor) {
        double dt = updateTime();
        //updateMotors();

        double power = 0.0;
        power = rotPID.getOutputValue();
        lastResult = new AsyncMotorResult(-power * powerFactor, power * powerFactor, rotPID.isOnTarget());
        displayDebug(telemetry);
        telemetry.addData("Motor Power", power * powerFactor);
        return lastResult;
    }

    public void rotateInPlace(double targetDegrees, double power) {
        rotateInPlace(targetDegrees, power, 0, 0);
    }

    public void rotateInPlace(double targetDegrees, double power, double distTolerance, double secTimeout) {
        AsyncMotorResult result;

        rotateInPlaceAsyncStart(targetDegrees, distTolerance);
        long t = System.nanoTime();
        while (true) {
            result = rotateInPlaceAsyncRun(power);
            if (result.isAtTarget() ||
                    ((secTimeout > 0 && ((System.nanoTime() - t) / 1000000000.0 > secTimeout))))
                return;
            t = System.nanoTime();
            for (EncodedMotor l : motorsLeft)
                l.setPower(result.getLeftPower());
            for (EncodedMotor r : motorsRight)
                r.setPower(result.getRightPower());
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public void moveStabilizedAsyncStart(double distTarget, Units.Distance distUnit) {
        lastResult = new AsyncMotorResult();
        navx.reset();
        try {
            navx.waitForReset();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        //Initialize the navX PID controller
        //Using the yaw axis, we can find out how far we move forward
        rotPID = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
        //Set the target location
        rotPID.setSetpoint(0.0);
        //Allow crossing over the bounds (see setContinuous() documentation)
        rotPID.setContinuous(true);
        //Set P,I,D coefficients
        rotPID.setCoefficients(params.kRotMoving);
        //Disable antistall (more accurate, and since this is only used for compensation, we can ignore the stalls)
        rotPID.disableAntistall();
        //Making the tolerance very small makes the robot work hard to get to get to a very close estimate
        rotPID.setTolerance(navXPIDController.ToleranceType.NONE, 0);
        //Start data collection
        rotPID.start();

        //Initiate blank PID state
        rotState = new NavXPIDController.PIDState();

        //Get mean (average) motor info
        MotorInfo mean = MotorInfo.mean(motorsLeft, motorsRight);

        //Create translation PID
        transPIDLeft = new PID();
        transPIDLeft.setSetpointDistance(mean, distTarget, distUnit);
        transPIDLeft.setCoefficients(params.kTrans);

        transPIDRight = new PID();
        transPIDRight.setSetpointDistance(mean, distTarget, distUnit);
        transPIDRight.setCoefficients(params.kTrans);

        updateTime();
    }

    public AsyncMotorResult moveStabilizedAsyncRun(double powerFactor, double distTolerance) {
        double dt = updateTime();
        updateMotors();

        transPIDLeft.addMeasurement(meanPos(motorsLeft), dt);
        transPIDRight.addMeasurement(meanPos(motorsRight), dt);

        double power = 0.0;
        double powerComp = 0.0;

        power = (transPIDLeft.getOutputValue() + transPIDRight.getOutputValue()) / 2.0;
        powerComp = rotPID.getOutputValue();

        double left, right;
        left = MathUtil.coerce(-1, 1, power) -
                Math.sin(MathUtil.coerce(-1, 1, powerComp * params.kRotMovingFerocity) * Math.PI / 2);
        right = MathUtil.coerce(-1, 1, power) +
                Math.sin(MathUtil.coerce(-1, 1, powerComp * params.kRotMovingFerocity) * Math.PI / 2);

        left = coerceMotorValue(left, params.kMinMotorPower);
        right = coerceMotorValue(right, params.kMinMotorPower);

        lastResult = new AsyncMotorResult(left * powerFactor, right * powerFactor,
                rotState.isOnTarget()
                        && ((distTolerance > 0) || (Math.abs(transPIDLeft.getError()) <= distTolerance)
                        && (Math.abs(transPIDRight.getError()) <= distTolerance)));
        navx.displayTelemetry(telemetry);
        return lastResult;
    }

    public void moveStabilized(double distTarget, Units.Distance distUnit, double power) throws InterruptedException {
        moveStabilized(distTarget, distUnit, power, 0, 0);
    }

    public void moveStabilized(double distTarget, Units.Distance distUnit, double power, double distTolerance, double secTimeout) throws InterruptedException {
        AsyncMotorResult result = new AsyncMotorResult();

        moveStabilizedAsyncStart(distTarget, distUnit);
        long t = System.nanoTime();
        while (true) {
            result = moveStabilizedAsyncRun(power, distTolerance);
            if (result.isAtTarget() ||
                    ((secTimeout > 0 && ((System.nanoTime() - t) / 1000000000.0 > secTimeout))))
                break;
            t = System.nanoTime();
            for (EncodedMotor l : motorsLeft)
                l.setPower(result.getLeftPower());
            for (EncodedMotor r : motorsRight)
                r.setPower(result.getRightPower());
            Thread.sleep(5);
        }
    }

    public void displayDebug(Telemetry telemetry) {
        navx.displayTelemetry(telemetry);
        telemetry.addData("RotPID Coefficients", rotPID.getCoefficientDebugString());
    }

    @Override
    public void dataReceived(long timeDelta) {

    }

    public static class NavigationParams {
        PID.PIDCoefficients kTrans = new PID.PIDCoefficients(0.0009, 0.0, 0.0);
        PID.PIDCoefficients kRotStatic = new PID.PIDCoefficients(0.01, 0.0, 0.0005);
        PID.PIDCoefficients kRotMoving = new PID.PIDCoefficients(0.005, 0.0, 0.0);
        double kRotMovingFerocity = 4;
        double kTransMaxAccel = 0.0;
        double kTransMaxDecel = 0.0;
        double kMinMotorPower = 0.00;

        NavigationParams() {

        }

        public void setTranslationPID(PID.PIDCoefficients c) {
            kTrans = c;
        }

        public void setStaticRotationPID(PID.PIDCoefficients c) {
            kRotStatic = c;
        }

        public void setMovingRotationPID(PID.PIDCoefficients c) {
            kRotMoving = c;
        }

        public void setRotationFerocity(double k) {
            kRotMovingFerocity = k;
        }

        public void setTranslationMaxAccel(double maxAccel, double maxDecel) {
            kTransMaxAccel = maxAccel;
            kTransMaxDecel = maxDecel;
        }

        public void setMinimumMotorPower(double minPower) {
            this.kMinMotorPower = minPower;
        }
    }

    public static class AsyncMotorResult {
        double left, right;
        boolean atTarget;

        AsyncMotorResult() {
            this.left = 0;
            this.right = 0;
            this.atTarget = false;
        }

        AsyncMotorResult(double left, double right, boolean atTarget) {
            this.left = left;
            this.right = right;
            this.atTarget = atTarget;
        }

        public boolean isAtTarget() {
            return atTarget;
        }

        public double getLeftPower() {
            return left;
        }

        public double getRightPower() {
            return right;
        }
    }
}