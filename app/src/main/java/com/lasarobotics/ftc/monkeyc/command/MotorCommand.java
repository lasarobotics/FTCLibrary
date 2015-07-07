package com.lasarobotics.ftc.monkeyc.command;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Moves a single motor
 */
public class MotorCommand implements Command {
    private String name;
    private double power;

    public MotorCommand(String name, double power) {
        this.power = power;
        this.name = name;
    }

    public void execute(HardwareMap map, OpMode mode){
        DcMotor motor = map.dcMotor.get(name);
        motor.setPower(power);
    }
}
