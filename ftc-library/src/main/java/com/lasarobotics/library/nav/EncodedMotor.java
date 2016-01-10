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
    private int encoderOffset = 0;

    /**
     * Initialize the encoded motor. You will need to run this during the init() method and run
     * the update() method every loop().
     *
     * @param motor DcMotor instance.
     */
    public EncodedMotor(DcMotor motor, double wheelRadius, Units.Distance radiusUnits) {
        super(motor.getController(), motor.getPortNumber(), motor.getDirection());
        enableEncoder();
        calculateRadius(wheelRadius, radiusUnits);
    }

    protected EncodedMotor(DcMotorController controller, int portNumber, double wheelRadius, Units.Distance radiusUnits) {
        super(controller, portNumber);
        enableEncoder();
        calculateRadius(wheelRadius, radiusUnits);
    }

    protected EncodedMotor(DcMotorController controller, int portNumber, Direction direction, double wheelRadius, Units.Distance radiusUnits) {
        super(controller, portNumber, direction);
        enableEncoder();
        calculateRadius(wheelRadius, radiusUnits);
    }

    private void calculateRadius(double wheelRadius, Units.Distance radiusUnit) {
        this.wheelRadius = radiusUnit.convertTo(Units.Distance.METERS, wheelRadius);
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

        reset();

        if (distance < 0)
            super.setDirection(Direction.REVERSE);
        else
            super.setDirection(Direction.FORWARD);

        setTargetPosition(Math.abs((int) distance));
    }

    /**
     * Checks if the robot has reached the expected encoder position.
     * After reaching the position, it is recommended to reset the encoders.
     *
     * @param position Expected position, in encoder counts
     * @return True if the robot has reached the position
     */
    public boolean hasReachedPosition(double position) {
        return Math.abs(getCurrentPosition()) >= Math.abs(position);
    }

    /**
     * Test if the encoder has reached a specific position in a unit
     * After reaching the position, it is recommended to reset the encoders.
     *
     * @param position Position, in units
     * @param unit     Unit of position
     * @return True if reached the expected position, false otherwise
     */
    public boolean hasReachedPosition(double position, Units.Distance unit) {
        return Math.abs(getCurrentPosition(unit)) >= Math.abs(position);
    }

    public void enableEncoder() {
        //This command requires (at least ?) one execution loop to reset the encoders
        setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);

        isEncoderEnabled = true;
    }

    public void disableEncoder() {
        //This command requires (at least ?) one execution loop to reset the encoders
        setMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        isEncoderEnabled = false;
    }

    public boolean isEncoderEnabled() {
        return isEncoderEnabled;
    }

    public void update() {
        //Check for encoder reset
        if (encodersResetting && super.getCurrentPosition() == 0) {
            encodersResetting = false;
            encoderOffset = 0;
            enableEncoder();
        }
    }

    /**
     * Reset encoders.
     */
    public void reset() {
        if (encodersResetting)
            return;

        //This command requires (at least ?) one execution loop to reset the encoders
        setMode(DcMotorController.RunMode.RESET_ENCODERS);

        encodersResetting = true;
        encoderOffset = -super.getCurrentPosition();
    }

    @Override
    public void setTargetPosition(int position) {
        super.setTargetPosition(position + encoderOffset);
    }

    public void setTargetPosition(double position, Units.Distance unit) {
        setTargetPosition((int) convertDistanceToEncoderCounts(position, unit));
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
     * Tests if the encoder has reset.
     * Note that encoders take at least one execution loop to fully reset.
     *
     * @return True for reset, false for not reset.
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
