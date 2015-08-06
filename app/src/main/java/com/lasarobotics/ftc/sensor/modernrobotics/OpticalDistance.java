package com.lasarobotics.ftc.sensor.modernrobotics;

import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Implements the Core Optical OpticalDistance Sensor with advanced methods
 *
 * This sensor is only fully accurate UP TO 5 CM
 * Different lighting conditions greatly affect distance read after 5 cm away from the object
 */
public class OpticalDistance {
    OpticalDistanceSensor o;

    public OpticalDistance(OpticalDistanceSensor sensor)
    {
        this.o = sensor;
    }

    public void update(OpticalDistanceSensor sensor)
    {
        this.o = sensor;
    }

    /**
     * Gets the raw light reflected as a decimal
     * @return The raw light reflected as a decimal
     */
    public double getLightDetected()
    {
        return o.getLightDetected();
    }

    /**
     * Gets an approximate distance from the object in centimeters
     * Formula based on empirical measurements in 2700K lighting at room temperature with a white semi-reflective object perpendicular to the beam
     * @return An approximate distance in centimeters
     */
    public double getDistance()
    {
        return 0.8028*Math.pow(getLightDetected(), -0.999d);
    }
}