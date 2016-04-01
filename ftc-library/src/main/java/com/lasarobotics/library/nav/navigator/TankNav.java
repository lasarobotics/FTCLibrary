package com.lasarobotics.library.nav.navigator;

import com.kauailabs.navx.ftc.navXPIDController;
import com.lasarobotics.library.nav.EncodedMotor;
import com.lasarobotics.library.nav.PID;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXPIDController;
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

    public TankNav(NavXDevice navx, EncodedMotor[] motorsLeft, EncodedMotor[] motorsRight) {
        super(navx);
        this.motorsLeft = motorsLeft;
        this.motorsRight = motorsRight;
    }

    public TankNav(NavXDevice navx, NavigationParams params, EncodedMotor[] motorsLeft, EncodedMotor[] motorsRight) {
        super(navx);
        this.motorsLeft = motorsLeft;
        this.motorsRight = motorsRight;
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

    public void rotateInPlaceAsyncStart(double targetDegrees, double distTolernace) {
        navx.reset();

        //Initialize the navX PID controller
        //Using the yaw axis, we can find out how far we move forward
        rotPID = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
        //Set the target location
        rotPID.setSetpoint(targetDegrees);
        //Allow crossing over the bounds (see setContinuous() documentation)
        rotPID.setContinuous(true);
        //Set P,I,D coefficients
        rotPID.setCoefficients(params.kRotStatic);
        //Disable antistall (more accurate, and since this is only used for compensation, we can ignore the stalls)
        rotPID.disableAntistall();
        //Making the tolerance very small makes the robot work hard to get to get to a very close estimate
        rotPID.setTolerance(navXPIDController.ToleranceType.ABSOLUTE, distTolernace);
        //Start data collection
        rotPID.start();

        //Initiate blank PID state
        rotState = new NavXPIDController.PIDState();

        updateTime();
    }

    public AsyncMotorResult rotateInPlaceAsyncRun() {
        double dt = updateTime();

        double power = 0.0;
        if (rotPID.isUpdateAvailable(rotState))
            lastResult = new AsyncMotorResult(-power, power, rotState.isOnTarget());
        return lastResult;
    }

    public void rotateInPlace(double targetDegrees) throws InterruptedException {
        rotateInPlace(targetDegrees, 0, 0);
    }

    public void rotateInPlace(double targetDegrees, double distTolerance, double secTimeout) throws InterruptedException {
        AsyncMotorResult result = new AsyncMotorResult();

        rotateInPlaceAsyncStart(targetDegrees, distTolerance);
        long t = System.nanoTime();
        while (true) {
            result = rotateInPlaceAsyncRun();
            if (result.isAtTarget() ||
                    ((secTimeout > 0 && (System.nanoTime() - t) / 1000000000.0 > secTimeout)))
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