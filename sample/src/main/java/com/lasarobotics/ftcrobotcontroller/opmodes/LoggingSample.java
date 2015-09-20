package com.lasarobotics.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.util.Log;
import com.lasarobotics.library.util.Timers;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.Random;

public class LoggingSample extends OpMode {

    Log l;
    Timers t;
    @Override
    public void init() {
        l = new Log("/FTC_Logs/","test.txt");
        t = new Timers();
        t.startClock("global");
    }

    @Override
    public void loop() {
        telemetry.addData("Teleop", "running for " + t.getClockValue("global"));
        l.add("simul", new Random().nextInt() + "");
    }
    @Override
    public void stop(){
        l.saveAs(Log.FileType.CSV);
        l.saveAs(Log.FileType.JSON);
        l.saveAs(Log.FileType.TEXT);
    }
}