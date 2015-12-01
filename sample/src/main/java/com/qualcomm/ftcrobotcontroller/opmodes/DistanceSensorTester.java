package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.sensor.modernrobotics.OpticalDistance;
import com.lasarobotics.library.util.RollingAverage;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

/**
 * Creates a rolling-average test for the optical distance sensor, attempting to correlate reflectivity with distance
 */
public class DistanceSensorTester extends OpMode {
    static final int avgcount = 2000;
    OpticalDistance distance;
    OpticalDistanceSensor d;
    RollingAverage<Double> average;
    int totalcount = 0;

    @Override
    public void init() {
        d = hardwareMap.opticalDistanceSensor.get("ODS");
        distance = new OpticalDistance(d);
        average = new RollingAverage<>(avgcount);
    }

    @Override
    public void loop() {
        distance.update(d);

        double current = distance.getDistance();
        if (current != 0) {
            average.addValue(current);
            totalcount += 1;
        }

        telemetry.addData("current", current);
        telemetry.addData("average", average.getAverage());
        telemetry.addData("count", average.getSize());
        telemetry.addData("totalcount", totalcount);
    }
}
