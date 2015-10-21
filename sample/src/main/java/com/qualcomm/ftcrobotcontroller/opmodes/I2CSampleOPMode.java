package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.sensor.modernrobotics.Gyro;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Sample use of Custom Gyro I2CDriver
 */
public class I2CSampleOPMode extends OpMode {
    Gyro g;

    @Override
    public void init() {
        g = new Gyro(hardwareMap.i2cDevice.get("gyro"));

    }

    @Override
    public void loop() {
        telemetry.addData("HEADING", g.getHeading());
    }
}
