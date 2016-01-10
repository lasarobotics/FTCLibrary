package com.lasarobotics.library.nav;

/**
 * PID Targeting
 */
public class PID {
    protected double setpoint = 0;
    protected double processValue = 0;  // actual possition (Process Value)
    protected double error = 0;   // how much SP and PV are diff (SP - PV)
    protected double integral = 0; // curIntegral + (error * Delta Time)
    protected double derivative = 0;  //(error - prev error) / Delta time
    protected double previousError = 0; // error from last time (previous Error)
    protected double Kp = 0.2, Ki = 0.01, Kd = 1; // PID constant multipliers
    protected double dt = 100.0; // delta time
    protected double output = 0; // the drive amount that effects the PV.

    public PID(double p, double i, double d) {
        this.Kp = p;
        this.Ki = i;
        this.Kd = d;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    public void addMeasurement(double measuredValue, double timeDelta) {
        this.processValue = measuredValue;
        this.dt = timeDelta;
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

    public double getCoefficientProportional() {
        return Kp;
    }

    public double getCoefficientIntegral() {
        return Ki;
    }

    public double getCoefficientDerivative() {
        return Kd;
    }

    public double getLastTimeDelta() {
        return dt;
    }

    public double getOutputValue() {
        return output;
    }

    /*This represents the speed at which electronics could actualy
        sample the process values.. and do any work on them.
     * [most industrial batching processes are SLOW, on the order of minutes.
     *  but were going to deal in times 10ms to 1 second.
     *  Most PLC's have relativly slow data busses, and would sample
     *  temperatures on the order of 100's of milliseconds. So our
     *  starting time interval is 100ms]
     */
    private void tmrPID_Ctrl_Tick(object sender, EventArgs e) {   /*
             * Pseudocode from Wikipedia
             *
                previous_error = 0
                integral = 0
            start:
                error = setpoint - PV(actual_position)
                integral = integral + error*dt
                derivative = (error - previous_error)/dt
                output = Kp*error + Ki*integral + Kd*derivative
                previous_error = error
                wait(dt)
                goto start
             */
        // calculate the difference between the desired value and the actual value
        error = setpoint - PV;
        // track error over time, scaled to the timer interval
        integral = integral + (error * Dt);
        // determin the amount of change from the last time checked
        derivative = (error - previousError) / Dt;

        // calculate how much drive the output in order to get to the
        // desired setpoint.
        output = (Kp * error) + (Ki * integral) + (Kd * derivative);

        // remember the error for the next time around.
        previousError = error;

    }

    //This timer updates the process data. it needs to be the fastest
    // interval in the system.
    private void tmrPV_Tick(object sender, EventArgs e) {
            /* this my version of cruise control.
             * PV = PV + (output * .2) - (PV*.10);
             * The equation contains values for speed, efficiency,
             *  and wind resistance.
               Here 'PV' is the speed of the car.
               'output' is the amount of gas supplied to the engine.
             * (It is only 20% efficient in this example)
               And it looses energy at 10% of the speed. (The faster the
               car moves the more PV will be reduced.)
             * Noise is added randomly if checked, otherwise noise is 0.0
             * (Noise doesn't relate to the cruise control, but can be useful
             *  when modeling other processes.)
             */
        PV = PV + (output * 0.20) - (PV * 0.10) + noise;
        // change the above equation to fit your aplication
    }
}
