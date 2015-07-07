package com.lasarobotics.ftc.monkeyc.instruction;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by ehsanasdar on 7/7/15.
 */
public class NullInstruction implements Instruction{
    public NullInstruction() {
    }

    public void execute(HardwareMap map){
        //Do nothing
    }
}
