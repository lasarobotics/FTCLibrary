package com.lasarobotics.ftc.monkeyc;

import com.google.gson.annotations.SerializedName;
import com.lasarobotics.ftc.controller.Controller;

import org.json.JSONObject;

/**
 * Contains a single time-stamped patched state of one Controller
 */
public class MonkeyData {
    //TODO can improve this to combine everything into one data set


    @SerializedName("g1")
    JSONObject deltas_gamepad1;
    @SerializedName("g2")
    JSONObject deltas_gamepad2;
    @SerializedName("t")
    long time;

    MonkeyData()
    {
        deltas_gamepad1 = null;
        deltas_gamepad2 = null;
        time = 0;
    }

    MonkeyData( JSONObject deltas_gamepad1, JSONObject deltas_gamepad2, long time) {
        this.deltas_gamepad1 = deltas_gamepad1;
        this.deltas_gamepad2 = deltas_gamepad2;
        this.time = time;
    }

//    Controller[] getNextState(Controller previous1, Controller previous2) {
//        if (hasUpdate()) return new Controller[] { previous1, previous2 };
//
//        Controller[] updated = new Controller[] { previous1, previous2 };
//
//        if (previous1 != null) {
//            for (MonkeyDelta<Boolean> b : deltas_button1)
//                updated[0].setState(b.variable, b.value ? 1 : 0);
//            for (MonkeyDelta<Float> f : deltas_joystick1)
//                updated[0].setState(f.variable, ((Float) f.value).intValue());
//        }
//        if (previous2 != null) {
//            for (MonkeyDelta<Boolean> b : deltas_button2)
//                updated[1].setState(b.variable, b.value ? 1 : 0);
//            for (MonkeyDelta<Float> f : deltas_joystick2)
//                updated[1].setState(f.variable, ((Float) f.value).intValue());
//        }
//        return updated;
//    }
//
//    public Controller updateControllerOne(Controller previous)
//    {
//        return getNextState(previous, null)[0];
//    }
//
//    public Controller updateControllerTwo(Controller previous)
//    {
//        return getNextState(null, previous)[1];
//    }
//
//    public boolean hasUpdate()
//    {
//        return ((deltas_button1 == null) && (deltas_joystick1 == null) &&
//                (deltas_button2 == null) && (deltas_joystick2 == null));
//    }

    public long getTime()
    {
        return time;
    }
}
