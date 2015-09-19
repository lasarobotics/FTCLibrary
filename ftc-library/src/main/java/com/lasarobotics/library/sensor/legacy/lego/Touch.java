package com.lasarobotics.library.sensor.legacy.lego;

import com.lasarobotics.library.controller.ButtonState;
import com.qualcomm.robotcore.hardware.LegacyModule;

/**
 * Implements the NXT touch sensor
 */
public class Touch extends TouchInternal {
    public Touch(LegacyModule legacyModule, int physicalPort) {
        super(legacyModule, physicalPort);
    }

    private int state = ButtonState.NOT_PRESSED;

    /**
     * Update the sensor events - run this every loop()
     */
    public void update() {
        if (super.isPressed()) {
            if (state == ButtonState.NOT_PRESSED || state == ButtonState.RELEASED)
                state = ButtonState.PRESSED;
            else
                state = ButtonState.HELD;
        } else {
            if (state == ButtonState.PRESSED || state == ButtonState.HELD)
                state = ButtonState.RELEASED;
            else
                state = ButtonState.NOT_PRESSED;
        }
    }

    /**
     * Gets the ButtonState instance of this button
     *
     * @return A ButtonState instance as an integer
     */
    public int getState() {
        return state;
    }

    /**
     * Checks if the sensor was JUST PRESSED
     *
     * @return True if just pressed, false otherwise
     */
    public boolean isPressed() {
        return state == ButtonState.PRESSED;
    }

    /**
     * Checks if the sensor was JUST RELEASED
     *
     * @return True if just released, false otherwise
     */
    public boolean isReleased() {
        return state == ButtonState.RELEASED;
    }

    /**
     * Checks if the sensor is held down
     *
     * @return True if pressed or held, false otherwise
     */
    public boolean isHeldDown() {
        return state == ButtonState.PRESSED || state == ButtonState.HELD;
    }
}
