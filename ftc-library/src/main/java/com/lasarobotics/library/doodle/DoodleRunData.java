package com.lasarobotics.library.doodle;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * A struct containing the data needed when any Doodle action is called
 */
public class DoodleRunData {
    HardwareMap map;
    OpMode mode;

    public DoodleRunData(HardwareMap map, OpMode mode)
    {
        this.map = map;
        this.mode = mode;
    }

    public HardwareMap map() { return map; }
    public OpMode mode() { return mode; }
}
