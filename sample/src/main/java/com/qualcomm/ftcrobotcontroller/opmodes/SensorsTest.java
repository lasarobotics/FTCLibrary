package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.android.Sensors;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class SensorsTest extends OpMode {

    Sensors sensors;

    public void init() {
        sensors = new Sensors();
        //sensors.stop();
    }

    @Override
    public void init_loop() {
        loop();
    }

    public void loop() {
        telemetry.addData("Linear Acceleration", sensors.getLinearAcceleration().toString());
        telemetry.addData("Integrated Velocity", sensors.getIntegratedVelocity().toString());
        telemetry.addData("Integrated Position", sensors.getIntegratedPosition().toString());
    }

    public void stop() {
        sensors.stop();
    }
}
