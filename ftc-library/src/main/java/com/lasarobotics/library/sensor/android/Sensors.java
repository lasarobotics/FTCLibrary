package com.lasarobotics.library.sensor.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.lasarobotics.library.android.Util;

import java.util.List;

/**
 * Lists Android manager, converts manager to this library's format, and tests if certain sensor is
 * present
 *
 * Use for any Android internal device sensor implemented in hardware OR software
 */
public final class Sensors {
    static SensorManager manager = (SensorManager) Util.getContext().getSystemService(Context.SENSOR_SERVICE);

    public static List<Sensor> getAllSensors() {
        return manager.getSensorList(Sensor.TYPE_ALL);
    }

    public static Sensor getSensor(int type) {
        return manager.getDefaultSensor(type);
    }

    public static Boolean hasSensor(int type) {
        return getSensor(type) != null;
    }
}
