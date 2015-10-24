package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.sensor.modernrobotics.Gyro;
import com.lasarobotics.library.util.Timers;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Sample use of Custom Gyro I2CDriver
 */
public class GyroSample extends OpMode {
    Gyro g;
    Timers t = new Timers();

    @Override
    public void init() {
        g = new Gyro(hardwareMap.i2cDevice.get("gyro"));
        //g.resetHeading();
        t.startClock("test");
    }

    @Override
    public void loop() {
        telemetry.addData("Heading", g.getHeading());
        telemetry.addData("RotX", g.getRotationX());
        telemetry.addData("RotY", g.getRotationY());
        telemetry.addData("RotZ", g.getRotationZ());
        telemetry.addData("OffsetZ", g.getOffsetZ());
        telemetry.addData("IntegratedZ", g.getIntegratedZ());
        if (t.getTimeSeconds("test") >= 15) {
            g.resetHeading();
            t.resetClock("test");
        }
    }
}
