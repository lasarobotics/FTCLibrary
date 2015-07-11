package com.lasarobotics.ftc.doodle.actions.wait;

import com.lasarobotics.ftc.doodle.DoodleRunData;
import com.lasarobotics.ftc.doodle.actions.Action;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Waits a certain period of time
 */
public class WaitForever extends Action {

    public WaitForever()
    {
        super("Wait forever");
    }

    public void run(DoodleRunData data)
    {
        try {
            while(true)
            {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        return "Wait forever";
    }
}
