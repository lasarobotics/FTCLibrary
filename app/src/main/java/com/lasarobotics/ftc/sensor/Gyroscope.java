package com.lasarobotics.ftc.sensor;

import com.lasarobotics.ftc.util.Timers;
import com.qualcomm.robotcore.hardware.GyroSensor;

import java.util.concurrent.TimeUnit;


/**
 * Implements additional Gyroscopic control methods and events
 */
public class Gyroscope {
    private GyroSensor gyroSensor;
    //IMPORTANT DELTAS
    private double lastvalue = 0.0D;
    private double velPrevious = 0.0D;
    private double velCurr = 0.0D;
    private double dt = 0.0D;
    private double offset = 0;
    private double heading = 0;
    private Timers timers;
    private final String clockName = "gyro";
    public Gyroscope(GyroSensor g)
    {
        timers = new Timers();
        timers.startClock(clockName);
        gyroSensor = g;
        reset();
    }

     /* Run this method on every loop() event.
     * Only to be run once calibration has ended.
     * Propagates values from the GyroSensor to the more advanced Gyroscope implementation.
     * @param g GyroSensor retrieved from the hardwareMap
     */
    public void update(GyroSensor g)
    {
        //update raw gyro rotation
        gyroSensor = g;
        //store new values
        velPrevious = velCurr;
        velCurr = getRotation();
        dt = timers.getClockValue(clockName, TimeUnit.SECONDS);

        heading += (velPrevious+velCurr)*.5D*dt;

        //prepare for next values
        timers.resetClock(clockName);
    }

    /**
     * Resets the gyroscope to a value of zero.
     */
    public void reset()
    {
        heading = 0;
    }


    /**
     * Gets the gyroscope rotation in degrees
     * @return The offset gyroscope rotation in degrees
     */
    public double getRotation()
    {
        return gyroSensor.getRotation() - offset;
    }

    /**
     * Gets the gyroscope heading in degrees, between 0 and 360
     * @param normalize Whether to normailze the heading to between 0 and 360 degrees
     * @return The gyro heading
     */
    public double getHeading(boolean normalize)
    {
        if(normalize)
            return normalize(heading);
        return heading;
    }

    /**
     * Normalize Gyroscope bounds to within 0 and 360
     * @param heading The current Gyroscope value
     * @return The normalized Gyroscope value, between 0 and 360.
     */
    public static double normalize(double heading) {
        if (heading < 0){
            double localheading = 360 - (Math.abs(heading)%360);
            return localheading;
        }
        else{
            double localheading = (heading % 360);
            return localheading;
        }
    }


    /**
     * Gets the time difference between the last readings.
     * @return The current time delay in seconds.
     */
    public double getTimeDifference()
    {
        return this.dt;
    }

    /**
     * Gets the gyroscope offset.
     * @return The offset, in degrees.
     */
    public double getOffset()
    {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * Gets the status of the gyroscope
     * @return The gyroscope status as a string
     */
    @Override
    public String toString() {
        return String.format("Gyroscope - rotation: %3.1f deg, rate: %3.1f deg/s, offset: %3.1f deg, over %1.4f sec\n" +
                             this.getHeading(), this.getRotation(), offset, this.getTimeDifference());
    }
}
