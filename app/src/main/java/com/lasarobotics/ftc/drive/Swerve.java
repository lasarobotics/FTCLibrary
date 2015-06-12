package com.lasarobotics.ftc.drive;

import com.lasarobotics.ftc.utils.MathUtil;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;

/**
 * Methods for the Swerve drive train
 */
public class Swerve {
    /**
     * Implements the Swerve drive train with four motors and four lifting servos
     * Requires gyro input
     *
     * CAUTION - NO UNIT TEST
     *
     * @param y The y-axis of the controller, forward/rev
     * @param x The x-axis of the controller, strafe
     * @param rot The spin axis of the controller
     * @param gyroheading The current normalized gyro heading (between 0 and 360)
     * @param leftFront The motor on the front left
     * @param rightFront The motor on the front right
     * @param leftBack The motor on the back left
     * @param rightBack The motor on the back right
     * @param lf The servo on the front left
     * @param rf The servo on the front right
     * @param lb The servo on the back left
     * @param rb The servo on the back right
     */
    public static void Standard(double y, double x, double rot, double gyroheading,
                              DcMotor leftFront, DcMotor rightFront,DcMotor leftBack, DcMotor rightBack,
                              Servo lf, Servo rf, Servo lb, Servo rb){
        double cosA = Math.cos(Math.toRadians(MathUtil.normalizeGyro(gyroheading)));
        double sinA = Math.sin(Math.toRadians(MathUtil.normalizeGyro(gyroheading)));
        double xOut = x * cosA - y * sinA;
        double yOut = x * sinA + y * cosA;

        //Assuming a square robot for now
        double a = xOut - rot/2;
        double b = xOut + rot/2;
        double c = yOut - rot/2;
        double d = yOut + rot/2;

        double wrf = Math.sqrt(b*b + c*c);
        double wlf = Math.sqrt(b*b + d*d);
        double wlb = Math.sqrt(a*a + d*d);
        double wrb = Math.sqrt(a*a + c*c);

        double arf = Math.atan2(b, c) * 180/Math.PI;
        double alf = Math.atan2(b, d) * 180/Math.PI;
        double alb = Math.atan2(a, d) * 180/Math.PI;
        double arb = Math.atan2(a, c) * 180/Math.PI;

        //Move range to between 0 and +1, if not already
        double[] wheelspeed = {wrf, wlf, wlb, wrb};
        Arrays.sort(wheelspeed);
        if (wheelspeed[3] > 1){
            wlf /= wheelspeed[3];
            wrf /= wheelspeed[3];
            wrb /= wheelspeed[3];
            wlb /= wheelspeed[3];
        }

        leftFront.setPower(wlf);
        rightFront.setPower(wrf);
        leftBack.setPower(wlb);
        rightBack.setPower(wrb);

        lf.setPosition(alf);
        rf.setPosition(arf);
        lb.setPosition(alb);
        rb.setPosition(arb);
    }
}
