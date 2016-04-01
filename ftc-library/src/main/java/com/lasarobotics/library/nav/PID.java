package com.lasarobotics.library.nav;

import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Units;

/**
 * PID Targeting
 */
public class PID {
    protected PIDCoefficients coefficients = new PIDCoefficients(0.005, 0, 0);  // PID constant multipliers
    protected double setpoint = 0;
    protected double processValue = 0;  // actual position (Process Value)
    protected double error = 0;   // how much SP and PV are diff (SP - PV)
    protected double integral = 0; // curIntegral + (error * Delta Time)
    protected double derivative = 0;  //(error - prev error) / Delta time
    protected double previousError = 0; // error from last time (previous Error)
    protected double dt = 100.0; // delta time
    protected double output = 0; // the drive amount that effects the PV.
    protected double outputFlattened = 0; // drive power flattened by the accel and decel
    protected double outputLast = 0; // last output power
    protected double maxChangePositive = 0; // maximum change in output per second
    protected double maxChangeNegative = 0; // maximum change in output per second
    protected double minOutput = -1; // maximum change in output per second
    protected double maxOutput = 1; // maximum change in output per second
    protected double minPower = 0; // minimum power for anti-stall protection

    public PID() {
        this.coefficients = new PIDCoefficients(0.05, 0, 0);
    }

    public PID(double p, double i, double d) {
        this.coefficients = new PIDCoefficients(p, i, d);
    }

    public PID(PIDCoefficients coefficients) {
        this.coefficients = coefficients;
    }

    public void setCoefficients(double p, double i, double d) {
        coefficients.p = p;
        coefficients.i = i;
        coefficients.d = d;
    }

    public void addMeasurement(double measuredValue, double timeDelta) {
        this.processValue = measuredValue;
        this.dt = timeDelta;
        update();
    }

    public double getSetpoint() {
        return this.setpoint;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public void setSetpointDistance(MotorInfo motorInfo, double distance, Units.Distance distanceUnit) {
        setSetpoint(Units.Distance.convertToAngle(distance,
                motorInfo.getEffectiveWheelRadius(distanceUnit),
                distanceUnit, Units.Distance.FEET, Units.Angle.ENCODER_COUNTS));
    }

    /**
     * Gets the most recent error - the difference between the setpoint and the measured value
     *
     * @return setpoint - measured value
     */
    public double getError() {
        return error;
    }

    /**
     * Get the integral value, or the sum of the recent error values
     *
     * @return Integral value I
     */
    public double getIntegralValue() {
        return integral;
    }

    /**
     * Get the derivative value, or how much the error is changing
     *
     * @return The slope of the error values
     */
    public double getDerivativeValue() {
        return derivative;
    }

    /**
     * Gets the error value from the previous measurement
     *
     * @return The previous error value
     */
    public double getPreviousError() {
        return previousError;
    }

    public PIDCoefficients getCoefficients() {
        return coefficients;
    }

    public void setCoefficients(PIDCoefficients coefficients) {
        coefficients.ff = 0.0;
        this.coefficients = coefficients;
    }

    public double getLastTimeDelta() {
        return dt;
    }

    public double getOutputValue() {
        return outputFlattened;
    }

    public double getOutputValueRaw() {
        return output;
    }

    public void setOutputRange(double min, double max) {
        this.minOutput = min;
        this.maxOutput = max;
    }

    public double getMaxAcceleration() {
        return maxChangePositive;
    }

    public void setMaxAcceleration(double maxChange) {
        this.maxChangePositive = maxChange;
    }

    public double getMaxDeceleration() {
        return maxChangeNegative;
    }

    public void setMaxDeceleration(double maxChange) {
        this.maxChangeNegative = Math.abs(maxChange);
    }

    public double getAntistallMinimumPower() {
        return minPower;
    }

    public void setAntistallMinimumPower(double minPower) {
        this.minPower = minPower;
    }

    private void update() {
        // calculate the difference between the desired value and the actual value
        error = setpoint - processValue;
        // track error over time, scaled to the timer interval
        integral = integral + (error * dt);
        // determin the amount of change from the last time checked
        derivative = (error - previousError) / dt;

        // calculate how much drive the output in order to get to the
        // desired setpoint.
        output = (coefficients.p * error) + (coefficients.i * integral) + (coefficients.d * derivative);

        //Clamp output
        output = MathUtil.coerce(minOutput, maxOutput, output);
        outputFlattened = output;

        //Make sure the system does not move too fast
        if ((outputFlattened - outputLast) > maxChangePositive * dt && maxChangePositive > 0)
            outputFlattened = outputLast + (maxChangePositive * dt);
        else if ((outputLast - outputFlattened) > maxChangeNegative * dt && maxChangeNegative > 0)
            outputFlattened = outputLast - (maxChangeNegative * dt);

        //Clamp and deadband the flattened output
        outputFlattened = MathUtil.deadband(minPower, MathUtil.coerce(minOutput, maxOutput, outputFlattened));

        // remember the error and output for the next time around.
        previousError = error;
        outputLast = outputFlattened;
    }

    public static class PIDCoefficients {
        double p;
        double i;
        double d;
        double ff;

        public PIDCoefficients(double p, double i, double d) {
            this.p = p;
            this.i = i;
            this.d = d;
            this.ff = 0.0;
        }

        public PIDCoefficients(double p, double i, double d, double ff) {
            this.p = p;
            this.i = i;
            this.d = d;
            this.ff = ff;
        }

        public double p() {
            return p;
        }

        public double i() {
            return i;
        }

        public double d() {
            return d;
        }

        public double ff() {
            return ff;
        }
    }
}
