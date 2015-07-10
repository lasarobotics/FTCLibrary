package com.lasarobotics.ftc.monkeyc;

import android.content.Context;
import android.util.Log;

import com.lasarobotics.ftc.controller.Controller;
import com.lasarobotics.ftc.util.Timers;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

/**
 * The MonkeyC (MonkeySee) library that handles recording and storing driver controls
 * These controls can be inserted during runtime (when the robot is moving)
 * or can be created prior to a match.  MonkeyDo can then execute these commands.
 */
public class MonkeyC {
    ArrayList<MonkeyData> commands;
    Timers t;
    //Create a standalone MonkeyC instance without piping to any output
    public MonkeyC()
    {
        this.commands = new ArrayList<MonkeyData>();
        t = new Timers();
        t.startClock("global");
    }
    public void add(Controller instruction,Controller instruction2)
    {
        //Make copy of controller
        Controller local = new Controller(instruction);
        Controller local2 = new Controller(instruction2);
        float time = t.getClockValue("global");
        MonkeyData temp = new MonkeyData(local,local2,time);

        //Write to the instruction array for writing to disk later
        commands.add(temp);
    }
    public void add(Gamepad instruction,Gamepad instruction2)
    {
        //Make copy of controller
        Controller local = new Controller(instruction);
        Controller local2 = new Controller(instruction2);
        float time = t.getClockValue("global");
        MonkeyData temp = new MonkeyData(local,local2,time);

        //Write to the instruction array for writing to disk later
        commands.add(temp);
    }
    public void clear()
    {
        commands.clear();
    }

    public void write(String filename,Context context)
    {
        MonkeyUtil.writeFile(filename,commands, context);
    }
}
