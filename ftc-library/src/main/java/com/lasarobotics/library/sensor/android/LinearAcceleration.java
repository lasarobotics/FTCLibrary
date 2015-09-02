package com.lasarobotics.library.sensor.android;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.lasarobotics.library.util.Vector3;

/**
 * Gets the forces placed upon the object in the x, y, and z directions excluding gravity in m/s^2
 */
public class LinearAcceleration implements SensorEventListener {
    Sensor sensor;
    float[] values;

    public LinearAcceleration() {
        sensor = Sensors.getSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensors.manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        values = event.values;
    }

    public Vector3<Float> getAcceleration()
    {
        if (values != null)
            return new Vector3<Float>(values[0], values[1], values[2]);
        return new Vector3<Float>(null, null, null);
    }
}
