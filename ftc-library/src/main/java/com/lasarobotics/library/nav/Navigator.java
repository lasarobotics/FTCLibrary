package com.lasarobotics.library.nav;

import com.lasarobotics.library.sensor.kauailabs.navx.NavXDevice;
import com.lasarobotics.library.sensor.kauailabs.navx.NavXPIDController;
import com.lasarobotics.library.util.Vector2;
import com.lasarobotics.library.util.Vector3;

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
 */
public class Navigator {

    /***
     * INITIALIZATION
     ***/

    Vector3<Double> startLocation = new Vector3<>(0.0, 0.0, 0.0);
    double startHeading = 0.0;
    boolean omnidirectionalDrive = false;
    /***
     * CONTROLLERS
     ***/

    NavXDevice navx;
    NavXPIDController accelX;
    NavXPIDController accelY;
    NavXPIDController accelZ;
    NavXPIDController rotX;
    NavXPIDController rotY;
    NavXPIDController rotZ;

    public Navigator(NavXDevice navx, boolean omnidirectionalDrive) {
        this.omnidirectionalDrive = omnidirectionalDrive;
        this.navx = navx;
        reset(new Vector3<>(0.0, 0.0, 0.0), 0.0);
        initializeControllers();
    }

    public Navigator(NavXDevice navx, Vector2<Double> startLocation, double startHeading, boolean omnidirectionalDrive) {
        this.omnidirectionalDrive = omnidirectionalDrive;
        this.navx = navx;
        reset(startLocation, startHeading);
        initializeControllers();
    }

    public Navigator(NavXDevice navx, Vector3<Double> startLocation, double startHeading, boolean omnidirectionalDrive) {
        this.omnidirectionalDrive = omnidirectionalDrive;
        this.navx = navx;
        reset(startLocation, startHeading);
        initializeControllers();
    }

    public void reset(Vector2<Double> startLocation, double startHeading) {
        reset(new Vector3<>(startLocation.x, startLocation.y, 0.0), startHeading);
    }

    public void reset(Vector3<Double> startLocation, double startHeading) {
        this.startLocation = startLocation;
        this.startHeading = startHeading;
        navx.reset();
    }

    private void initializeControllers() {
        accelX = new NavXPIDController(navx, NavXPIDController.DataSource.LINEAR_ACCEL_X);
        accelY = new NavXPIDController(navx, NavXPIDController.DataSource.LINEAR_ACCEL_Y);
        accelZ = new NavXPIDController(navx, NavXPIDController.DataSource.LINEAR_ACCEL_Z);

        rotX = new NavXPIDController(navx, NavXPIDController.DataSource.PITCH);
        rotY = new NavXPIDController(navx, NavXPIDController.DataSource.ROLL);
        rotZ = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
    }
}
