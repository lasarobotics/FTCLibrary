package com.lasarobotics.library.sensor.generic;

import com.qualcomm.robotcore.hardware.IrSeekerSensor;

/**
 * Implements an IR sensor with additional advanced methods
 *
 * TODO ungenericify to HT and ModernRobotics when testing equipment available
 */
public class IR {
    private IrSeekerSensor sensor;

    public IR(IrSeekerSensor s) {
        sensor = s;
    }

    public void update(IrSeekerSensor s) {
        sensor = s;
    }

    public double getStrength() {
        return sensor.getStrength();
    }

    public double getAngle() {
        return sensor.getAngle();
    }

    public Boolean hasSignal() {
        return sensor.signalDetected();
    }

    public IrSeekerSensor.IrSeekerIndividualSensor[] getSensors() {
        return sensor.getIndividualSensors();
    }
}
