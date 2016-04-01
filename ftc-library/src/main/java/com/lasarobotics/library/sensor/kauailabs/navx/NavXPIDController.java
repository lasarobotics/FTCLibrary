package com.lasarobotics.library.sensor.kauailabs.navx;

import android.util.Log;

import com.kauailabs.navx.ftc.navXPIDController;
import com.lasarobotics.library.nav.PID;
import com.lasarobotics.library.util.MathUtil;

import java.util.Locale;

/**
 * PID Controller designed for the navX MXP or micro
 */
public class NavXPIDController extends navXPIDController {

    private final static int waitTimeout = 50;      //wait timeout in ms
    private double antistallDeadband = 0.03; //minimum motor power - prevents stalls
    private boolean enableAntistall = false;  //true to enable antistall deadband

    public NavXPIDController(NavXDevice navX, DataSource dataSource) {
        super(navX.ahrs, dataSource.val);
        setPID(0.5, 0, 0, 0);
        setOutputRange(-1.0, 1.0);
    }

    public PID.PIDCoefficients getCoefficients() {
        return new PID.PIDCoefficients(p, i, d, ff);
    }

    public void setCoefficients(PID.PIDCoefficients c) {
        this.p = c.p();
        this.i = c.i();
        this.d = c.d();
        this.ff = c.ff();
    }

    public String getCoefficientString() {
        return "p: " + p + ", i: " + i + ", d: " + d + ", ff: " + ff;
    }

    public String getCoefficientDebugString() {
        return String.format(Locale.US,
                "p: %3.4f i: %3.4f d: %3.4f", p * error_current, i * error_total, d * error_d);
    }

    public void reset() {
        super.reset();
    }

    public boolean isEnabled() {
        return super.isEnabled();
    }

    public void start() {
        super.enable(true);
    }

    public void stop() {
        super.enable(false);
    }

    public void waitForUpdate(PIDState state) {
        try {
            if (!super.waitForNewUpdate(state, waitTimeout))
                throw new InterruptedException();
        } catch (InterruptedException e) {
            Log.e("navX PID", "waitForUpdate() failed!");
        }
    }

    public boolean isUpdateAvailable(PIDState state) {
        boolean isAvailable = isNewUpdateAvailable(state);
        if (isAvailable) {
            state.output = MathUtil.deadband(antistallDeadband, state.output);
        }
        return isAvailable;
    }

    /**
     * Returns the latest output value, selected by the DataSource when creating the controller
     *
     * @return Latest output value as a double
     */
    public double getOutputValue() {
        return enableAntistall ? MathUtil.deadband(antistallDeadband, super.get()) : super.get();
    }

    public double getError() {
        return super.getError();
    }

    public double getAntistallDeadband() {
        return antistallDeadband;
    }

    public void setAntistallDeadband(double minMotorPower) {
        antistallDeadband = minMotorPower;
    }

    public void setAntistall(boolean enableAntistall) {
        this.enableAntistall = enableAntistall;
    }

    public void enableAntistall() {
        enableAntistall = true;
    }

    public void disableAntistall() {
        enableAntistall = false;
    }

    public void setTolerance(ToleranceType toleranceType, double tolerance) {
        super.setTolerance(toleranceType.val, tolerance);
    }

    /**
     * The data source specifies the
     * sensor data source type used by the controller as it's input data source.
     * These data sources are timestamped by the navX-Model device and thus are delivered
     * with sufficient data (a highly-accurate "sensor timestamp") to allow the
     * PID controller to compensate for any jitter in the transmission from the
     * navX-Model device to the controller.
     */
    public enum DataSource {
        YAW(navXPIDController.navXTimestampedDataSource.YAW),
        PITCH(navXPIDController.navXTimestampedDataSource.PITCH),
        ROLL(navXPIDController.navXTimestampedDataSource.ROLL),
        HEADING(navXPIDController.navXTimestampedDataSource.FUSED_HEADING),
        LINEAR_ACCEL_X(navXPIDController.navXTimestampedDataSource.LINEAR_ACCEL_X),
        LINEAR_ACCEL_Y(navXPIDController.navXTimestampedDataSource.LINEAR_ACCEL_Y),
        LINEAR_ACCEL_Z(navXPIDController.navXTimestampedDataSource.LINEAR_ACCEL_Z);

        navXPIDController.navXTimestampedDataSource val;

        DataSource(navXPIDController.navXTimestampedDataSource val) {
            this.val = val;
        }
    }

    /**
     * The ToleranceType enumeration defines the type of tolerance to be used by the
     * controller to determine whether the controller is "on_target".
     */
    public enum ToleranceType {
        NONE(navXPIDController.ToleranceType.NONE),
        PERCENT(navXPIDController.ToleranceType.PERCENT),
        ABSOLUTE(navXPIDController.ToleranceType.ABSOLUTE);

        navXPIDController.ToleranceType val;

        ToleranceType(navXPIDController.ToleranceType val) {
            this.val = val;
        }
    }

    /**
     * The PIDState class encapsulates the data used by the controller to
     * communicate current state to a client of the controller.  The client
     * creates the instance of the PIDState, and continually provides it to the
     * navxPIDController's waitForNewUpdate() and isNewDataAvailable() methods,
     * depending upon whether the client wishes to block (wait) for new updates,
     * or periodically poll to determine when new data is available.
     */
    static public class PIDState extends navXPIDController.PIDResult {

        public PIDState() {
            super();
        }

        /**
         * Returns the timestamp of the last data sample processed.
         */
        public long getTimestamp() {
            return super.getTimestamp();
        }

        /**
         * Returns true if the device arrived at the target location.
         */
        public boolean isOnTarget() {
            return super.isOnTarget();
        }

        /**
         * Returns the output value calculated by the controller which
         * corresponds to the most recent input data sample.
         */
        public double getOutput() {
            return super.getOutput();
        }
    }
}
