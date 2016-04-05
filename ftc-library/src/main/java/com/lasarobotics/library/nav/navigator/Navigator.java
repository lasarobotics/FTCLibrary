package com.lasarobotics.library.nav.navigator;

import com.lasarobotics.library.sensor.kauailabs.navx.NavXDataReceiver;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.util.MathUtil;

/**
 * Smart navigation class that performs two operations:
 * 1. Locates the robot on the field at any given time
 * 2. Moves the robot to another point on the field
 * <p/>
 * Location is given through integration of the sensors.
 * <p/>
 * Movement can be set by:
 * 1. A single point. The robot will move to this point immediately.
 * 2. A path containing several points. The robot will move through the path
 * as if it is a Bezier curve.
 * <p/>
 * Movement supports by 2D and 3D movement, as well as rotation.
 * <p/>
 * The Navigator currently supports only navX-based controllers.
 * <p/>
 * <p/>
 * The current idea is to use:
 * 1. the yaw-/z-axis PID controller as a precise rotation mechanism and self-correcting drive.
 * 2. motor encoders to calculate distance traveled in the direction - more encoders give better positioning data
 * Possibly even:
 * 3. x and y accelorometers to measure (approximate) position
 */
abstract class Navigator implements NavXDataReceiver {

    protected NavXDevice navx;

    protected Navigator(NavXDevice navx) {
        this.navx = navx;
    }

    public static double coerceMotorValue(double value, double minValue) {
        return MathUtil.deadband(minValue, MathUtil.coerce(-1, 1, value));
    }
}
