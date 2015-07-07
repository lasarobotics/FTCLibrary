package com.lasarobotics.ftc.monkeyc.command;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by ehsanasdar on 7/7/15.
 */
public class NullCommand implements Command {
    public NullCommand() {
    }

    public void execute(HardwareMap map, OpMode mode){
        //Do nothing
    }
}
