package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.sensor.modernrobotics.Gyro;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.I2cDevice;

import java.util.concurrent.locks.Lock;

/**
 * Created by ehsan on 10/14/15.
 */
public class I2CSampleOPMode extends OpMode {
    I2cDevice gyro;
    Gyro g;
    @Override
    public void init() {
        g = new Gyro(hardwareMap.i2cDevice.get("hi"));

    }

    @Override
    public void loop() {

    }
}
