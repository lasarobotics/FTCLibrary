package com.lasarobotics.library.nav;

import android.util.Log;

import com.lasarobotics.library.drive.DriveSpecification;
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

    static NavXDevice navx;

    /**
     * Controllers - we track all axis for finding out when we are on a platform,
     * but because robots only have really 1 DoF, we only track the acceleration
     * relative to the field and the yaw rotation
     */
    static NavXPIDController accelX;
    static NavXPIDController accelY;
    //static NavXPIDController accelZ;
    //static NavXPIDController rotX;
    //static NavXPIDController rotY;
    static NavXPIDController rotZ;

    Vector3<Double> startLocation = new Vector3<>(0.0, 0.0, 0.0);   //robot start location in field coordinates
    double startHeading = 0.0;                                      //robot start heading relative to field
    DriveSpecification driveSpec;                                   //drive specifications

    Vector3<Double> position = new Vector3<>(0.0, 0.0, 0.0);        //current position
    Vector3<Double> velocity = new Vector3<>(0.0, 0.0, 0.0);        //current velocity
    Vector3<Double> acceleration = new Vector3<>(0.0, 0.0, 0.0);    //current acceleration

    NavigatorTarget target = null;                                  //current target
    double toleranceRot = 2;                                        //rotational tolerance in degrees
    double toleranceLoc = 0.01;                                     //directional tolerance in meters

    //
    // INITIALIZATION
    //

    public Navigator(NavXDevice navx, DriveSpecification driveSpec) {
        Navigator.navx = navx;
        zero(new Vector3<>(0.0, 0.0, 0.0), 0.0);
        initialize(driveSpec);
    }

    public Navigator(NavXDevice navx, DriveSpecification driveSpec, Vector2<Double> startLocation, double startHeading) {
        Navigator.navx = navx;
        zero(startLocation, startHeading);
        initialize(driveSpec);
    }

    public Navigator(NavXDevice navx, DriveSpecification driveSpec, Vector3<Double> startLocation, double startHeading) {
        Navigator.navx = navx;
        zero(startLocation, startHeading);
        initialize(driveSpec);
    }

    private void initialize(DriveSpecification driveSpec) {
        zero();
        this.driveSpec = driveSpec;

        accelX = new NavXPIDController(navx, NavXPIDController.DataSource.LINEAR_ACCEL_X);
        accelY = new NavXPIDController(navx, NavXPIDController.DataSource.LINEAR_ACCEL_Y);
        //accelZ = new NavXPIDController(navx, NavXPIDController.DataSource.LINEAR_ACCEL_Z);

        //rotX = new NavXPIDController(navx, NavXPIDController.DataSource.PITCH);
        //rotY = new NavXPIDController(navx, NavXPIDController.DataSource.ROLL);
        rotZ = new NavXPIDController(navx, NavXPIDController.DataSource.YAW);
    }

    public void zero() {
        zero(new Vector3<>(0.0, 0.0, 0.0), 0.0);
    }

    public void zero(Vector2<Double> startLocation, double startHeading) {
        zero(new Vector3<>(startLocation.x, startLocation.y, 0.0), startHeading);
    }

    public void zero(Vector3<Double> startLocation, double startHeading) {
        this.startLocation = startLocation;
        this.startHeading = startHeading;
        navx.reset();

        position = new Vector3<>(0.0, 0.0, 0.0);
        velocity = new Vector3<>(0.0, 0.0, 0.0);
        acceleration = new Vector3<>(0.0, 0.0, 0.0);
    }

    public void setPID(Controller controller, double p, double i, double d) {
        controller.getController().setPID(p, i, d);
        Log.d("Navigator PID", controller.getController().getCoefficientString());
    }

    public void setTarget(NavigatorTarget target) {
        boolean start = this.target == null; //if true, we should start tasks
        this.target = target;
        updateParameters();
    }

    //
    // TARGETING
    //

    private void updateParameters() {
        accelX.setTolerance(NavXPIDController.ToleranceType.ABSOLUTE, toleranceLoc);
        accelY.setTolerance(NavXPIDController.ToleranceType.ABSOLUTE, toleranceLoc);
        rotZ.setTolerance(NavXPIDController.ToleranceType.ABSOLUTE, toleranceRot);

        //accelX.setSetpoint();
    }

    public enum Controller {
        ACCEL_X,
        ACCEL_Y,
        GYRO_Z;

        public NavXPIDController getController() {
            switch (this) {
                case ACCEL_X:
                    return accelX;
                case ACCEL_Y:
                    return accelY;
                case GYRO_Z:
                    return rotZ;
                default:
                    throw new RuntimeException("No such instance!");
            }
        }
    }

    //TODO: implement Bezier path-based targeting ?

    //
    // RESULTS
    //

    public static class NavigatorResult {
        Vector2<Float> power;

        NavigatorResult() {
            power = new Vector2<Float>(0.0f, 0.0f);
        }

        /**
         * Get a power vector suitable for arcade control
         *
         * @return A vector with the power returning as an X and Y magnitude
         */
        public Vector2<Float> getPowerVector() {
            return power;
        }

        /**
         * Get a power vector suitable for tank drive control
         *
         * @return A vector with the first index being the left side motors and the second index the right motors
         */
        public Vector2<Float> getPowerTankDrive() {
            //FIXME implement
            throw new RuntimeException("Not yet implemented!");
        }

        /**
         * Get a power vector suitable for arcade control or an omnidirectional drive train
         *
         * @return A vector with the first index being the magnitude and the second a heading,
         * which can can be from -360 (counterclockwise rotation) to 360 (clockwise)
         */
        public Vector2<Float> getPowerMagnitudeDirection() {
            //FIXME implement
            throw new RuntimeException("Not yet implemented!");
        }

    }

    public static class NavigatorTarget {
        Vector2<Double> targetPosition;
        double heading;

        /**
         * Instantiate a navigator target with a target position in field coordinates and a target heading
         *
         * @param targetPosition Target position, in field coordinates. These coordinates can be reset().
         * @param heading        Target heading, or direction to face. 0 is North, -90 or 270 is West, etc. Relative to robot.
         */
        public NavigatorTarget(Vector2<Double> targetPosition, double heading) {
            this.targetPosition = targetPosition;
            this.heading = heading;
        }

        /**
         * Instantiate a navigator target with a 2D target vector, either in field coordinates
         * or a distance relative to the robot's current position
         * If robotRelative is true, the target position is a vector representing the
         * distance away from the current location
         * The direction of the vector represents the heading.
         * Otherwise, targetPosition is a location in field coordinates, defined by startLocation.
         *
         * @param targetPosition Robot's target position, in either field or inertial coordinates
         * @param heading        Robot's target heading, either relative to the field (as with field oriented
         *                       drive) or to the robot's current rotation
         * @param robotRelative  True if targetPosition is in inertial (robot-relative) coordianates,
         *                       False if targetPosition is in field coordinates
         */
        public NavigatorTarget(Vector2<Double> targetPosition, double heading, boolean robotRelative) {
            //FIXME implement
            throw new RuntimeException("Not yet implemented!");
        }
    }
}
