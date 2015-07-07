package com.lasarobotics.ftc.monkeyc.instruction;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by ehsanasdar on 7/7/15.
 */
public class MotorInstuction implements Instruction {
    private String name;
    private double power;

    public MotorInstuction(String name, double power) {
        this.power = power;
        this.name = name;
    }

    public void execute(HardwareMap map){
        DcMotor motor = map.dcMotor.get(name);
        motor.setPower(power);
    }
}
