package com.lasarobotics.ftc.sensor.modernrobotics;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Implements the Core Optical Distance Sensor with advanced methods
 */
public class Distance {
    OpticalDistanceSensor o;

    public Distance(OpticalDistanceSensor sensor)
    {
        this.o = sensor;
    }

    public void update(OpticalDistanceSensor sensor)
    {
        this.o = sensor;
    }

    public double getDistance()
    {
        return o.getLightDetected();
    }
}