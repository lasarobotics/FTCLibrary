package com.lasarobotics.library.sensor.kauailabs.navx;

import com.kauailabs.navx.ftc.AHRS;
import com.kauailabs.navx.ftc.IDataArrivalSubscriber;
import com.lasarobotics.library.util.MathUtil;
import com.lasarobotics.library.util.Vector3;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;

/**
 * NavX MXP controller
 */
public class NavXDevice implements IDataArrivalSubscriber {

    static final DataType dataType = DataType.RAW_AND_PROCESSED;
    static final int MAX_NUM_CALLBACKS = 3;
    AHRS ahrs;
    double last_world_linear_accel_x = 0.0;
    double last_world_linear_accel_y = 0.0;
    private double maxJerk = 0.0;
    private long last_system_timestamp = 0;
    private long last_sensor_timestamp = 0;
    private double collisionThreshold = 0.5;
    private NavXDataReceiver[] callbacks;

    /**
     * Initialize a NavX MXP or NavX micro device
     *
     * @param map                       HardwareMap instance
     * @param deviceInterfaceModuleName String name of the device interface module the sensor is on
     * @param i2cPort                   The i2C port the sensor is currently on
     */
    public NavXDevice(HardwareMap map, String deviceInterfaceModuleName, int i2cPort) {
        initialize(map, deviceInterfaceModuleName, i2cPort, SensorSpeed.NORMAL_FAST, dataType);
    }

    /**
     * Initialize a NavX MXP or NavX micro device, and also set the sensor's speed
     *
     * @param map                       HardwareMap instance
     * @param deviceInterfaceModuleName String name of the device interface module the sensor is on
     * @param i2cPort                   The i2C port the sensor is currently on
     * @param speed                     Sensor read speed (recommended anything from VERY_SLOW to VERY_FAST - other values are experimental)
     */
    public NavXDevice(HardwareMap map, String deviceInterfaceModuleName, int i2cPort, SensorSpeed speed) {
        initialize(map, deviceInterfaceModuleName, i2cPort, speed, DataType.PROCESSED_DATA);
    }

    private void initialize(HardwareMap map, String deviceInterfaceModuleName, int i2cPort, SensorSpeed speed, DataType type) {
        ahrs = AHRS.getInstance(map.deviceInterfaceModule.get(deviceInterfaceModuleName),
                i2cPort, DataType.PROCESSED_DATA.getValue(), speed.getSpeedHertzByte());
        this.callbacks = new NavXDataReceiver[MAX_NUM_CALLBACKS];
        ahrs.registerCallback(this);
    }

    public boolean isCalibrating() {
        return ahrs.isCalibrating();
    }

    public boolean isCalibrated() {
        return !ahrs.isCalibrating() && ahrs.isMagnetometerCalibrated();
    }

    public boolean isConnected() {
        return ahrs.isConnected();
    }

    public boolean isMoving() {
        return ahrs.isMoving();
    }

    public boolean isMagneticDisturbance() {
        return ahrs.isMagneticDisturbance();
    }

    public boolean isRotating() {
        return ahrs.isRotating() && !ahrs.isMagneticDisturbance();
    }

    public void reset() {
        ahrs.zeroYaw();
    }

    public void waitForReset() throws InterruptedException {
        //noinspection StatementWithEmptyBody
        while (MathUtil.inBounds(-1, 1, this.getRotation().x)) {

            Thread.sleep(20);
        }
    }

    public void displayTelemetry(Telemetry telemetry) {
        boolean connected = ahrs.isConnected();
        telemetry.addData("navX Status", connected ?
                "Connected" : "Disconnected");
        String gyrocal, magcal, yaw, pitch, roll, compass_heading;
        String fused_heading, ypr, cf, motion;
        DecimalFormat df = new DecimalFormat("#.##");

        if (connected) {
            gyrocal = (ahrs.isCalibrating() ?
                    "CALIBRATING" : "Calibration Complete");
            magcal = (ahrs.isMagnetometerCalibrated() ?
                    "Calibrated" : "UNCALIBRATED");
            yaw = df.format(ahrs.getYaw());
            pitch = df.format(ahrs.getPitch());
            roll = df.format(ahrs.getRoll());
            ypr = yaw + ", " + pitch + ", " + roll;
            compass_heading = df.format(ahrs.getCompassHeading());
            fused_heading = df.format(ahrs.getFusedHeading());
            if (!ahrs.isMagnetometerCalibrated()) {
                compass_heading = "-------";
            }
            cf = compass_heading + ", " + fused_heading;
            if (ahrs.isMagneticDisturbance()) {
                cf += " (Mag. Disturbance)";
            }
            motion = (ahrs.isMoving() ? "Moving" : "Not Moving");
            if (ahrs.isRotating()) {
                motion += ", Rotating";
            }

            telemetry.addData("navX GyroAccel", gyrocal);
            telemetry.addData("navX Y,P,R", ypr);
            telemetry.addData("navX Magnetometer", magcal);
            telemetry.addData("navX Compass,9Axis", cf);
            telemetry.addData("navX Motion", motion);
        }
    }

    /**
     * Get update frequency in Hertz
     *
     * @return Update rate in Hertzs
     */
    public float getUpdateRate() {
        return (float) ahrs.getActualUpdateRate();
    }

    /**
     * Get the number of the current update
     *
     * @return The update count
     */
    public int getUpdateCount() {
        return (int) ahrs.getUpdateCount();
    }

    /**
     * Get the ambient temperature, in degrees Celsius
     *
     * @return Ambient temperature, in degrees Celsius
     */
    public float getTemperature() {
        return ahrs.getTempC();
    }

    public Vector3<Float> getLinearAcceleration() {
        return new Vector3<>(ahrs.getWorldLinearAccelX(), ahrs.getWorldLinearAccelY(), ahrs.getWorldLinearAccelZ());
    }

    /**
     * Gets the rotation vector from the processed gyroscope measurements
     *
     * @return YAW (x-axis), PITCH (y-axis), ROLL (z-axis) rotation in meters/second
     */
    public Vector3<Float> getRotation() {
        return new Vector3<>(ahrs.getYaw(), ahrs.getPitch(), ahrs.getRoll());
    }

    /**
     * Get the heading in degrees, from 0 to 360.
     *
     * @return The heading in degrees from 0 to 360.
     */
    public float getHeading() {
        return ahrs.getFusedHeading();
    }

    public void stop() {
        ahrs.close();
        for (int i = 0; i < callbacks.length; i++) {
            callbacks[i] = null;
        }
    }

    public double getCollisionThreshold() {
        return collisionThreshold;
    }

    public void setCollisionThreshold(double thresholdGs) {
        if (thresholdGs <= 0.0)
            throw new InvalidParameterException("Threshold must be greater than 0 Gs!");
        this.collisionThreshold = thresholdGs;
    }

    /**
     * Test whether the robot has collided - using the set or default threshold
     *
     * @return True if collided, false otherwise
     */
    public boolean hasCollided() {
        if (collisionThreshold <= 0.0)
            throw new InvalidParameterException("Threshold must be greater than 0 Gs!");
        return maxJerk > collisionThreshold;
    }

    /**
     * Test whether the robot has collided
     *
     * @param thresholdGs Threshold for collision, in Gs. Default is 0.5 Gs.
     * @return True if collided, false otherwise
     */
    public boolean hasCollided(double thresholdGs) {
        if (thresholdGs <= 0.0)
            throw new InvalidParameterException("Threshold must be greater than 0 Gs!");
        return maxJerk > thresholdGs;
    }

    /**
     * Get the instantaneous jerk, in m/s^3.
     *
     * @return Jerk (derivative of acceleration, i.e. change in acceleration) in m/s^3.
     */
    public double getJerk() {
        return maxJerk;
    }

    /**
     * Registers a callback interface.  This interface
     * will be called back when new data is available,
     * based upon a change in the sensor timestamp.
     * <p/>
     * Note that this callback will occur within the context of the
     * device IO thread, which is not the same thread context the
     * caller typically executes in.
     */
    public boolean registerCallback(NavXDataReceiver callback) {
        boolean registered = false;
        for (int i = 0; i < this.callbacks.length; i++) {
            if (this.callbacks[i] == null) {
                this.callbacks[i] = callback;
                registered = true;
                break;
            }
        }
        return registered;
    }

    /**
     * Deregisters a previously registered callback interface.
     * <p/>
     * Be sure to deregister any callback which have been
     * previously registered, to ensure that the object
     * implementing the callback interface does not continue
     * to be accessed when no longer necessary.
     */
    public boolean deregisterCallback(NavXDataReceiver callback) {
        boolean deregistered = false;
        for (int i = 0; i < this.callbacks.length; i++) {
            if (this.callbacks[i] == callback) {
                this.callbacks[i] = null;
                deregistered = true;
                break;
            }
        }
        return deregistered;
    }

    @Override
    public void untimestampedDataReceived(long curr_system_timestamp, Object kind) {
        long system_timestamp_delta = curr_system_timestamp - last_system_timestamp;
        for (NavXDataReceiver callback : callbacks) {
            if (callback != null) {
                callback.dataReceived(system_timestamp_delta);
            }
        }
    }

    @Override
    public void timestampedDataReceived(long curr_system_timestamp, long curr_sensor_timestamp, Object kind) {
        //Test for collisions
        long sensor_timestamp_delta = curr_sensor_timestamp - last_sensor_timestamp;
        long system_timestamp_delta = curr_system_timestamp - last_system_timestamp;
        double curr_world_linear_accel_x = ahrs.getWorldLinearAccelX();
        double currentJerkX = curr_world_linear_accel_x - last_world_linear_accel_x;
        last_world_linear_accel_x = curr_world_linear_accel_x;
        double curr_world_linear_accel_y = ahrs.getWorldLinearAccelY();
        double currentJerkY = curr_world_linear_accel_y - last_world_linear_accel_y;
        last_world_linear_accel_y = curr_world_linear_accel_y;

        maxJerk = Math.max(Math.abs(currentJerkX), Math.abs(currentJerkY));

        last_sensor_timestamp = curr_sensor_timestamp;
        last_system_timestamp = curr_system_timestamp;

        for (NavXDataReceiver callback : callbacks) {
            if (callback != null) {
                callback.dataReceived(sensor_timestamp_delta);
            }
        }
    }


    public enum DataType {
        PROCESSED_DATA(AHRS.DeviceDataType.kProcessedData),
        RAW_DATA(AHRS.DeviceDataType.kQuatAndRawData),
        RAW_AND_PROCESSED(AHRS.DeviceDataType.kAll);

        private AHRS.DeviceDataType value;

        DataType(AHRS.DeviceDataType value) {
            this.value = value;
        }

        AHRS.DeviceDataType getValue() {
            return value;
        }
    }

    /**
     * NavX response speed
     * <p/>
     * Anything with "EXTREMELY" or "SLOWEST"/"FASTEST" is experimental.
     */
    public enum SensorSpeed {

        SLOWEST(1.0),
        EXTREMELY_SLOW(5.0),
        VERY_SLOW(15.38),
        SLOW(25.0),
        NORMAL_SLOW(33.3),
        NORMAL(40.0),
        NORMAL_FAST(50.0),
        VERY_FAST(66.6),
        EXTREMELY_FAST(100.0),
        FASTEST(200.0);

        double hertz;

        SensorSpeed(double hz) {
            this.hertz = hz;
        }

        public double getSpeedHertz() {
            return hertz;
        }

        public byte getSpeedHertzByte() {
            return (byte) ((int) hertz);
        }
    }
}
