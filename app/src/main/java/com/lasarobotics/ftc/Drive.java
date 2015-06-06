package com.lasarobotics.ftc;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Drive Train Math
 */
public class Drive {
    /**
     * Implements the Tank drive train
     * @param left Left motor
     * @param right Right motor
     * @param leftValue Left motor target value
     * @param rightValue Right motor target value
     */
    public static void tank(DcMotor left, DcMotor right, double leftValue,double rightValue ){
        left.setPower((leftValue/127)*100);
        right.setPower((rightValue/127)*100);
    }

    /**
     * Implements the Arcade drive train with three axis and four motors.
     * @param y The y-axis of the controller, forward/rev
     * @param x The x-axis of the controller, strafe
     * @param c The spin axis of the controller
     * @param leftFront The motor on the front left
     * @param rightFront The motor on the front right
     * @param leftBack The motor on the back left
     * @param rightBack The motor on the back right
     */
    public static void mecanumArcade(double y,double x, double c,DcMotor leftFront,DcMotor rightFront,DcMotor leftBack, DcMotor rightBack){
        double leftFrontVal = y + x + c;
        double rightFrontVal = y - x - c;
        double leftBackVal  = y - x + c;
        double rightBackVal  = y + x - c;
        leftFront.setPower(leftFrontVal*100);
        rightFront.setPower(rightFrontVal*100);
        leftBack.setPower(leftBackVal*100);
        rightBack.setPower(rightBackVal*100);
    }

    /**
     * Implements the Arcade drive train with field orientation
     * @param y The y-axis of the controller, forward/rev
     * @param x The x-axis of the controller, strafe
     * @param c The spin axis of the controller
     * @param gyroheading The current normalized gyro heading (between 0 and 360)
     * @param leftFront The motor on the front left
     * @param rightFront The motor on the front right
     * @param leftBack The motor on the back left
     * @param rightBack The motor on the back right
     */
    public static void mecanumArcadeFieldOriented(double y,double x, double c, double gyroheading,DcMotor leftFront,DcMotor rightFront,DcMotor leftBack, DcMotor rightBack){
        double cosA = java.lang.Math.cos(java.lang.Math.toRadians(Util.normalizeGyro(gyroheading)));
        double sinA = java.lang.Math.sin(java.lang.Math.toRadians(Util.normalizeGyro(gyroheading)));
        double xOut = x * cosA - y * sinA;
        double yOut = x * sinA + y * cosA;
        mecanumArcade(yOut,xOut,c,leftFront,rightFront,leftBack,rightBack);
    }
}
