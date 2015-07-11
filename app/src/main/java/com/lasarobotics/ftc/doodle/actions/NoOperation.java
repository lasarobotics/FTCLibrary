package com.lasarobotics.ftc.doodle.actions;

import com.lasarobotics.ftc.doodle.DoodleRunData;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Dummy action that does absolutely nothing but waste precious disk space.
 * It's a great starting template though.
 */
public class NoOperation extends Action {

    public NoOperation()
    {
        super("Do nothing");
    }

    public void run(DoodleRunData data)
    {

    }

    public String toString()
    {
        return "Do nothing";
    }
}
