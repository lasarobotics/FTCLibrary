package com.lasarobotics.ftc.monkeyc;

import com.google.gson.annotations.SerializedName;
import com.lasarobotics.ftc.controller.Controller;

/**
 * Contains a single time-stamped patched state of one Controller
 */
public class MonkeyData {
    //TODO can improve this to combine everything into one data set

    @SerializedName("b1")
    MonkeyDelta<Boolean>[] deltas_button1;
    @SerializedName("j1")
    MonkeyDelta<Float>[] deltas_joystick1;
    @SerializedName("b2")
    MonkeyDelta<Boolean>[] deltas_button2;
    @SerializedName("j2")
    MonkeyDelta<Float>[] deltas_joystick2;
    @SerializedName("t")
    long time;

    MonkeyData()
    {
        deltas_button1 = null;
        deltas_joystick1 = null;
        deltas_button2 = null;
        deltas_joystick2 = null;
        time = 0;
    }

    MonkeyData(MonkeyDelta<Boolean>[] deltas_button1, MonkeyDelta<Float>[] deltas_joystick1,
               MonkeyDelta<Boolean>[] deltas_button2, MonkeyDelta<Float>[] deltas_joystick2, long time) {
        this.deltas_button1 = deltas_button1;
        this.deltas_joystick1 = deltas_joystick1;
        this.deltas_button2 = deltas_button2;
        this.deltas_joystick2 = deltas_joystick2;
        this.time = time;
    }

    Controller[] getNextState(Controller previous1, Controller previous2) {
        if (hasUpdate()) return new Controller[] { previous1, previous2 };

        Controller[] updated = new Controller[] { previous1, previous2 };

        if (previous1 != null) {
            for (MonkeyDelta<Boolean> b : deltas_button1)
                updated[0].setState(b.variable, b.value ? 1 : 0);
            for (MonkeyDelta<Float> f : deltas_joystick1)
                updated[0].setState(f.variable, ((Float) f.value).intValue());
        }
        if (previous2 != null) {
            for (MonkeyDelta<Boolean> b : deltas_button2)
                updated[1].setState(b.variable, b.value ? 1 : 0);
            for (MonkeyDelta<Float> f : deltas_joystick2)
                updated[1].setState(f.variable, ((Float) f.value).intValue());
        }
        return updated;
    }

    public Controller updateControllerOne(Controller previous)
    {
        return getNextState(previous, null)[0];
    }

    public Controller updateControllerTwo(Controller previous)
    {
        return getNextState(null, previous)[1];
    }

    public boolean hasUpdate()
    {
        return ((deltas_button1 == null) && (deltas_joystick1 == null) &&
                (deltas_button2 == null) && (deltas_joystick2 == null));
    }

    public long getTime()
    {
        return time;
    }
}
