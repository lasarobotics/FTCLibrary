package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.controller.ButtonState;
import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.sensor.modernrobotics.Gyro;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Sample use of Custom Gyro I2CDriver
 */
public class GyroSample extends OpMode {
    Gyro g;
    Controller g1;

    @Override
    public void init() {
        g = new Gyro(hardwareMap.i2cDevice.get("gyro"));
        g1 = new Controller();
    }

    @Override
    public void loop() {
        g1.update(gamepad1);
        telemetry.addData("HEADING", g.getHeading());
        if (g1.a == ButtonState.PRESSED) {
            g.resetHeading();
        }
    }
}
