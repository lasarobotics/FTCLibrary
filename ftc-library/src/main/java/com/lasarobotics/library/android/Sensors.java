package com.lasarobotics.library.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.WindowManager;

import com.lasarobotics.library.util.ScreenOrientation;
import com.lasarobotics.library.util.Vector3;

/**
 * Contains methods for reading Android native sensors, other than the camera
 */
public final class Sensors implements SensorEventListener {

    private static final float PITCH_TOLERANCE = 20.0f;
    private static final float PITCH_TOLERANCE_HIGH = 45.0f;
    private static final float ROLL_MINIMUM = 10.0f;
    private static final int READ_SPEED = SensorManager.SENSOR_DELAY_FASTEST;

    static float[] gravity = new float[3];
    static float[] linearAcceleration = new float[3];
    static float[] geomagnetic = new float[3];

    static float[] integratedVelocity = new float[3];
    static float[] integratedPosition = new float[3];
    static long lastTime = 0; //in nanoseconds

    private static boolean activated = false;
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final Sensor mMagneticField;
    private ScreenOrientation screenOrientation = null;

    public Sensors() {
        mSensorManager = (SensorManager) Util.getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        activated = false;
        resume();
        resetIntegration();
    }

    public void resume() {
        if (activated)
            return;
        mSensorManager.registerListener(this, mAccelerometer, READ_SPEED);
        mSensorManager.registerListener(this, mMagneticField, READ_SPEED);
        activated = true;
        resetIntegration();
    }

    public void stop() {
        if (!activated)
            return;
        activated = false;
        mSensorManager.unregisterListener(this);
    }

    /**
     * Get the current linear acceleration in m/s^2
     *
     * @return The instantaneous linear acceleration in m/s^2
     */
    public Vector3<Float> getLinearAcceleration() {
        return new Vector3<>(linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]);
    }

    public Vector3<Float> getIntegratedVelocity() {
        return new Vector3<>(integratedVelocity[0], integratedVelocity[1], integratedVelocity[2]);
    }

    public Vector3<Float> getIntegratedPosition() {
        return new Vector3<>(integratedPosition[0], integratedPosition[1], integratedPosition[2]);
    }

    public void resetIntegration() {
        integratedPosition = new float[]{0, 0, 0};
        integratedVelocity = new float[]{0, 0, 0};
        lastTime = 0;
    }

    public boolean hasOrientation() {
        return screenOrientation != null;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation != null ? screenOrientation : ScreenOrientation.LANDSCAPE;
    }

    public ScreenOrientation getActivityScreenOrientation() {
        WindowManager windowManager = (WindowManager) Util.getContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        return ScreenOrientation.getFromSurface(rotation);
    }

    public double getScreenOrientationCompensation() {
        return getScreenOrientation().getAngle() - getActivityScreenOrientation().getAngle() + ScreenOrientation.PORTRAIT.getAngle();
    }

    private void updateScreenOrientation() {
        float[] R = new float[9];
        float[] I = new float[9];
        SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
        float[] orientation = new float[3];
        SensorManager.getOrientation(R, orientation);

        //device rotation angle = pitch (first value) [clockwise from horizontal]

        double pitch = orientation[1] / 2 / Math.PI * 360.0;
        double roll = orientation[2] / 2 / Math.PI * 360.0;
        double azimuth = orientation[0] / 2 / Math.PI * 360.0;

        Log.w("Rotation", pitch + ", " + roll + ", " + azimuth);

        //If the phone is too close to the ground, don't update
        if (Math.abs(roll) <= ROLL_MINIMUM)
            return;

        ScreenOrientation current = screenOrientation;

        if (Math.abs(pitch) <= PITCH_TOLERANCE)
            if (roll > 0.0f)
                current = ScreenOrientation.LANDSCAPE_WEST;
            else
                current = ScreenOrientation.LANDSCAPE;
        else if (Math.abs(pitch) >= PITCH_TOLERANCE_HIGH)
            if (pitch > 0.0f)
                current = ScreenOrientation.PORTRAIT;
            else
                current = ScreenOrientation.PORTRAIT_REVERSE;

        screenOrientation = current;
    }

    private void integrateSensors() {
        long currentTime = System.nanoTime();
        long timeDelta = currentTime - lastTime;

        if (lastTime == 0) {
            lastTime = currentTime;
            return;
        }

        lastTime = currentTime;

        final float NANO_TO_SEC = 1000000000.0f;

        //dv = a * dt
        integratedVelocity[0] += linearAcceleration[0] * timeDelta / NANO_TO_SEC;
        integratedVelocity[1] += linearAcceleration[1] * timeDelta / NANO_TO_SEC;
        integratedVelocity[2] += linearAcceleration[2] * timeDelta / NANO_TO_SEC;

        //dx = v * dt
        integratedPosition[0] += integratedVelocity[0] * timeDelta / NANO_TO_SEC;
        integratedPosition[1] += integratedVelocity[1] * timeDelta / NANO_TO_SEC;
        integratedPosition[2] += integratedVelocity[2] * timeDelta / NANO_TO_SEC;
    }

    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                // alpha is calculated as t / (t + dT)
                // with t, the low-pass filter's time-constant
                // and dT, the event delivery rate
                final float alpha = 0.8f;

                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                linearAcceleration[0] = event.values[0] - gravity[0];
                linearAcceleration[1] = event.values[1] - gravity[1];
                linearAcceleration[2] = event.values[2] - gravity[2];
                updateScreenOrientation();
                integrateSensors();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomagnetic = event.values;
                updateScreenOrientation();
                integrateSensors();
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
