package com.lasarobotics.ftc.doodle.actions;

import com.lasarobotics.ftc.doodle.DoodleRunData;

/**
 * Move a motor at a specified power
 */
public class MotorMove extends Action {
    float power;
    String motor;

    public MotorMove(float power, String motor)
    {
        super("Move motor");
        this.power = power;
        this.motor = motor;
    }

    public void run(DoodleRunData data)
    {
        data.map().dcMotor.get(motor).setPower(power);
    }

    public String toString()
    {
        return "Move motor " + motor + " at " + (int)(100 * power) + "% power";
    }
}
