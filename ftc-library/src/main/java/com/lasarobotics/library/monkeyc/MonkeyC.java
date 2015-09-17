package com.lasarobotics.library.monkeyc;

import android.content.Context;

import com.google.gson.Gson;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.util.Timers;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

/**
 * The MonkeyC (MonkeySee) library that handles recording and storing driver controls
 * These controls can be inserted during runtime (when the robot is moving)
 * or can be created prior to a match.  MonkeyDo can then execute these commands.
 */
public class MonkeyC {
    private ArrayList<MonkeyData> commands;
    private Controller previous1 = new Controller();
    private Controller previous2 = new Controller();
    Timers t;

    public MonkeyC() {
        this.commands = new ArrayList<MonkeyData>();
        t = new Timers();
        t.startClock("global");
    }

    public void add(Controller c1, Controller c2) {
        //Make copy of controller
        Controller local1 = new Controller(c1);
        Controller local2 = new Controller(c2);

        //Get current time stamp
        long time = t.getClockValue("global");

        //Get previous controller state JSON
        Gson gson = new Gson();
        //GsonBuilder gsonbuild = new GsonBuilder();

        //String json1 = gson.toJson(local1);
        //JsonObject jsobj = new JsonObject();

        //Get controller deltas
        MonkeyData data = MonkeyUtil.createDeltas(local1, previous1, local2, previous2, time);

        //Update previous values to current values
        this.previous1 = new Controller(local1);
        this.previous2 = new Controller(local2);

        //Write to the instruction array for writing to disk later
        if (data.getDeltasGamepad1() != null || data.getDeltasGamepad2() != null)
            commands.add(data);
    }

    public void add(Gamepad instruction, Gamepad instruction2) {
        //TODO test this - status on update() may remain either just pressed or just unpressed
        Controller one = new Controller(instruction);
        Controller two = new Controller(instruction2);
        add(one, two);
    }

    public void clear() {
        commands.clear();
    }

    public void write(String filename, Context context) {
        MonkeyUtil.writeFile(filename, commands, context);
    }

    public int size() {
        return commands.size();
    }
}
