package com.lasarobotics.ftc.sensor.modernrobotics;

import com.lasarobotics.ftc.util.RollingAverage;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

/**
 * Reads the robot battery voltage
 */
public class Voltage {
    VoltageSensor sensor;
    RollingAverage<Double> average;

    public final static int samples = 2000;

    public Voltage(HardwareMap map)
    {
        sensor = (VoltageSensor)map.voltageSensor.iterator().next();
        average = new RollingAverage<>(samples);
    }

    public void update()
    {
        average.addValue(getVoltageInstantaneous());
    }

    public double getVoltage()
    {
        return average.getAverage();
    }

    public double getVoltageInstantaneous()
    {
        return sensor.getVoltage();
    }
}
