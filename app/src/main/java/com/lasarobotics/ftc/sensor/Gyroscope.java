package com.lasarobotics.ftc.sensor;

import com.lasarobotics.ftc.util.MathUtil;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implements additional Gyroscopic control methods and events
 */
public class Gyroscope {
    private GyroSensor g;
    private ElapsedTime t;

    //IMPORTANT DELTAS

    private double lastvalue = 0.0D;
    private double timedelay = 0.0D;
    private boolean gotfirstt = false;
    private double dt = 0.0D;

    //CALIBRATION

    //The gyroscope offset created by calls to the calibrate() or reset() methods
    //This offset is the raw value used to zero the gyroscope
    private double offset = 0;
    //The gyroscope offset created by calls to the calibrate() method
    //This offset
//    private double offsetdt = 0;
    //The list containing multiple calibration counts
    //Each item is a change in rotation over time
    //Used to calculate average change in rotation to append to offset
//    private ArrayList<Double> cal_ddt_counts;
//    private ArrayList<Double> cal_ddt_time;

    public Gyroscope(GyroSensor g)
    {
        this.g = g;
        reset();
    }

    /**
     * Calibrate the gyroscope.
     * This method may be run over multiple loop() iterations.
     *
     * Every loop iteration, the calibrate() method recalculates the first derivative
     * to append to offsetdt (average of cal_ddt_counts).  Then it checks whether the second
     * derivative with respect to time is about zero - then the offset is stable and
     * calibration complete.
     * @return True if the gyroscope offset has stabilized, false to recommend a rerun.
     */
//    public boolean calibrate(GyroSensor g)
//    {
//        //Update variables and get first derivative
//        update(g);
//
//        //Update the offset so that the heading is zero
//        reset();
//
//        //Append the offset to the table of first derivatives
//        cal_ddt_counts.add(offset);
//        cal_ddt_time.add(timedelay);
//
//        //Calculate the average offset difference, offsetdt = sumdtdt.
//        double sumdtdt = 0.0D;
//        for (int i=0; i<cal_ddt_counts.size() - 1; i++) {
//            try {
//                sumdtdt += (cal_ddt_counts.get(i+1) - cal_ddt_counts.get(i)) / cal_ddt_time.get(i);
//            }
//            catch (Exception e)
//            {
//                sumdtdt += 0D;
//            }
//        }
//
//        try {
//            offsetdt = sumdtdt / cal_ddt_counts.size();
//        }
//        catch (Exception e)
//        {
//            offsetdt = 0;
//        }
//
//
//    }

    /**
     * Run this method on every loop() event.
     * Only to be run once calibration has ended.
     * Propagates values from the GyroSensor to the more advanced Gyroscope implementation.
     * @param g GyroSensor retrieved from the hardwareMap
     */
    public void update(GyroSensor g)
    {
        //update raw gyro rotation
        this.g = g;

        //calculate first derivative (change with respect to timedelay)
        if (!gotfirstt)
        {
            //store first value
            this.t = new ElapsedTime();
            this.timedelay = 0.0D;
            this.lastvalue = g.getRotation();
            this.dt = 0;
            gotfirstt = true;
        }
        else
        {
            //store new values
            this.timedelay = t.time();
            try
            {
                this.dt = (g.getRotation() - lastvalue) / timedelay;
            } catch (Exception e)
            {
                this.dt = 0.0D;
            }

            //prepare for next values
            this.lastvalue = g.getRotation();
            t.reset();
        }
    }

    /**
     * Gets the number of times the calibrate() method was successfully performed.
     * Can be used to end a calibrate() loop.
     * @return The count of successful calibrations
     */
//    public int calibrationCount()
//    {
//        return cal_ddt_counts.size();
//    }

    /**
     * Resets the gyroscope to a value of zero.
     */
    public void reset()
    {
        offset = offset - getRotation();
    }

    /**
     * Gets the gyroscope rotation in degrees
     * @return The offset gyroscope rotation in degrees
     */
    public double getRotation()
    {
        return g.getRotation() + offset;
    }

    /**
     * Gets the gyroscope heading in degrees, between 0 and 360
     * @return The gyro heading, 0 <= heading < 360
     */
    public double getHeading()
    {
        return normalize(getRotation());
    }

    /**
     * Normalize Gyroscope bounds to within 0 and 360
     * @param gyro The current Gyroscope value
     * @return The normalized Gyroscope value, between 0 and 360.
     */
    public static double normalize(double gyro) {
        while (gyro < 0.0D)
            gyro = (360.0D + gyro);
        return gyro % 360.0D;
    }

    /**
     * Gets the rate of the gyroscope in deg/sec
     * @return The rate of rotation in deg/sec
     */
    public double getRate()
    {
        return this.dt;
    }

    /**
     * Gets the time delay between the last readings.
     * @return The current time delay in seconds.
     */
    public double getTimeDelay()
    {
        return this.timedelay;
    }

    /**
     * Gets the gyroscope offset.
     * @return The offset, in degrees.
     */
    public double getOffset()
    {
        return this.offset;
    }

    /**
     * Gets the status of the gyroscope
     * @return The gyroscope status as a string
     */
    @Override
    public String toString() {
        return String.format("Gyroscope - rotation: %3.1f deg, rate: %3.1f deg/s, offset: %3.1f deg, over %1.4f sec\n" +
                             this.getRotation(), this.getRate(), offset, this.getTimeDelay());
    }
}
