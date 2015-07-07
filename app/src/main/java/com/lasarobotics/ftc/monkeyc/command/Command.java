package com.lasarobotics.ftc.monkeyc.command;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * A command that can be executed by the MonkeyC framework
 */
public interface Command {
    public void execute(HardwareMap map, OpMode mode);
}
