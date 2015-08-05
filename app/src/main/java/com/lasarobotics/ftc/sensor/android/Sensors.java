package com.lasarobotics.ftc.sensor.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.lasarobotics.ftc.util.Android;

import java.util.List;

/**
 * Lists Android sensors, converts sensors to this library's format, and tests if certain sensor is present
 *
 * Use for any Android internal device sensor implemented in hardware OR software
 */
public final class Sensors implements SensorEventListener {
    public static List<Sensor> getAllSensors()
    {
        Context ctx = Android.getContext();
        SensorManager sensors;
        sensors = (SensorManager)ctx.getSystemService(ctx.SENSOR_SERVICE);
        List<Sensor> all = sensors.getSensorList(Sensor.TYPE_ALL);
        return all;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }
}
