package com.lasarobotics.library.nav;

import com.lasarobotics.library.util.Units;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

/**
 * Drive encoder support
 */
public class EncodedMotor extends DcMotor {
    protected double wheelRadius;   //wheel radius in meters
    private boolean isEncoderEnabled = true;
    private boolean encodersResetting = false;
    private boolean requestReset = false;
    private int encoderOffset = 0;
    private MotorInfo motorInfo;

    /**
     * Initialize the encoded motor. You will need to run this during the init() method and run
     * the update() method every loop().
     *
     * @param motor DcMotor instance.
     */
    public EncodedMotor(DcMotor motor, MotorInfo info) {
        super(motor.getController(), motor.getPortNumber(), motor.getDirection());
        enableEncoder();
        requestReset();
        this.wheelRadius = info.getEffectiveWheelRadius(Units.Distance.METERS);
        this.motorInfo = info;
    }

    protected EncodedMotor(DcMotorController controller, int portNumber, MotorInfo info) {
        super(controller, portNumber);
        enableEncoder();
        requestReset();
        this.wheelRadius = info.getEffectiveWheelRadius(Units.Distance.METERS);
        this.motorInfo = info;
    }

    protected EncodedMotor(DcMotorController controller, int portNumber, MotorInfo info, Direction direction) {
        super(controller, portNumber, direction);
        enableEncoder();
        requestReset();
        this.wheelRadius = info.getEffectiveWheelRadius(Units.Distance.METERS);
        this.motorInfo = info;
    }

    /**
     * Move a certain distance, in encoder counts
     * If the distance is negative, the wheel will move in reverse - forward otherwise
     *
     * It is assumed that there are 1440 encoder counts in a full rotation.
     *
     * @param distance The distance to move, in encoder counts
     */
    public void moveDistance(double distance) {
        if (distance == 0)
            return;

        requestReset();

        if (distance < 0)
            super.setDirection(Direction.REVERSE);
        else
            super.setDirection(Direction.FORWARD);

        setTargetPosition(Math.abs((int) distance));
    }

    public void moveDistance(double distance, Units.Distance unit) {
        moveDistance(convertDistanceToEncoderCounts(distance, unit));
    }

    /**
     * Checks if the robot has reached the expected encoder position.
     * After reaching the position, it is recommended to requestReset the encoders.
     *
     * @param position Expected position, in encoder counts
     * @return True if the robot has reached the position
     */
    public boolean hasReachedPosition(double position) {
        return Math.abs(getCurrentPosition()) >= Math.abs(position);
    }

    /**
     * Test if the encoder has reached a specific position in a unit
     * After reaching the position, it is recommended to requestReset the encoders.
     *
     * @param position Position, in units
     * @param unit     Unit of position
     * @return True if reached the expected position, false otherwise
     */
    public boolean hasReachedPosition(double position, Units.Distance unit) {
        return Math.abs(getCurrentPosition(unit)) >= Math.abs(position);
    }

    public void enableEncoder() {
        //This command requires (at least ?) one execution loop to requestReset the encoders
        setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        isEncoderEnabled = true;
    }

    public void disableEncoder() {
        //This command requires (at least ?) one execution loop to requestReset the encoders
        setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        isEncoderEnabled = false;
    }

    public boolean isEncoderEnabled() {
        return isEncoderEnabled;
    }

    public void update() {
        //Check for encoder requestReset
        if (requestReset && !encodersResetting)
            resetEncoder();
        if (encodersResetting && super.getCurrentPosition() == 0) {
            encodersResetting = false;
            encoderOffset = 0;
            enableEncoder();
        }
    }

    private void resetEncoder() {
        //This command requires (at least ?) one execution loop to requestReset the encoders
        setMode(DcMotorController.RunMode.RESET_ENCODERS);

        encodersResetting = true;
        requestReset = false;
        encoderOffset = -super.getCurrentPosition();
    }

    /**
     * Reset encoder asynchronously
     *
     * (May take two frames for full effect)
     */
    public void requestReset() {
        if (encodersResetting)
            return;

        requestReset = true;
    }

    /**
     * Reset encoder immediately
     * (May take one frame for full effect)
     */
    public void resetNow() {
        resetEncoder();
    }

    public void setTargetPosition(double position, Units.Distance unit) {
        setTargetPosition((int) convertDistanceToEncoderCounts(position, unit));
    }

    public MotorInfo getMotorInfo() {
        return motorInfo;
    }

    @Override
    public int getTargetPosition() {
        return super.getTargetPosition() - encoderOffset;
    }

    @Override
    public void setTargetPosition(int position) {
        super.setTargetPosition(position + encoderOffset);
    }

    public double getTargetPosition(Units.Distance unit) {
        return convertEncoderCountsToDistance(getTargetPosition(), unit);
    }

    public double convertDistanceToEncoderCounts(double distance, Units.Distance unit) {
        return Units.Distance.convertToAngle(distance, wheelRadius, Units.Distance.METERS,
                unit, Units.Angle.ENCODER_COUNTS);
    }

    public double convertEncoderCountsToDistance(double encoderCounts, Units.Distance unit) {
        return Units.Angle.convertToDistance(encoderCounts, wheelRadius, Units.Distance.METERS,
                Units.Angle.ENCODER_COUNTS, unit);
    }

    /**
     * Tests if the encoder has requestReset.
     * Note that encoders take at least one execution loop to fully requestReset.
     *
     * @return True for requestReset, false for not requestReset.
     */
    public boolean hasEncoderReset() {
        return getCurrentPosition() == 0;
    }

    @Override
    public int getCurrentPosition() {
        if (encodersResetting)
            return 0;
        return super.getCurrentPosition() + encoderOffset;
    }

    public double getCurrentPosition(Units.Distance unit) {
        return convertEncoderCountsToDistance(getCurrentPosition(), unit);
    }
}
