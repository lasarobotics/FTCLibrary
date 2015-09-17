package com.lasarobotics.library.doodle.actions.wait;

import com.lasarobotics.library.doodle.DoodleRunData;
import com.lasarobotics.library.doodle.actions.Action;

/**
 * Waits a certain period of time
 */
public class WaitForever extends Action {

    public WaitForever() {
        super("Wait forever");
    }

    public void run(DoodleRunData data) {
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "Wait forever";
    }
}
