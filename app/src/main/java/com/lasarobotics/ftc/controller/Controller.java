package com.lasarobotics.ftc.controller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Implements a functional controller with an event API
 */
public class Controller {

    public ButtonToggle dpad_up;
    public ButtonToggle dpad_down;
    public ButtonToggle dpad_left;
    public ButtonToggle dpad_right;
    public ButtonToggle a;
    public ButtonToggle b;
    public ButtonToggle x;
    public ButtonToggle y;
    public ButtonToggle guide;
    public ButtonToggle start;
    public ButtonToggle back;
    public ButtonToggle left_bumper;
    public ButtonToggle right_bumper;

    //Triggers use a float for how much they are pressed
    public ButtonFloat left_trigger;
    public ButtonFloat right_trigger;

    //Joysticks don't have any events
    public float left_stick_x;
    public float left_stick_y;
    public float right_stick_x;
    public float right_stick_y;

    public void update(Gamepad g){
        handleUpdate(dpad_up,g.dpad_up);
        handleUpdate(dpad_down,g.dpad_down);
        handleUpdate(dpad_left,g.dpad_left);
        handleUpdate(dpad_right,g.dpad_right);
        handleUpdate(a,g.a);
        handleUpdate(b,g.b);
        handleUpdate(x,g.x);
        handleUpdate(y,g.y);
        handleUpdate(guide,g.guide);
        handleUpdate(start,g.start);
        handleUpdate(back,g.back);
        handleUpdate(left_bumper,g.left_bumper);
        handleUpdate(right_bumper,g.right_bumper);
        handleUpdate(left_trigger,(g.left_trigger) > ButtonFloat.getThreshold());
        handleUpdate(right_trigger,(g.right_trigger) > ButtonFloat.getThreshold());

        left_stick_x = g.left_stick_x;
        left_stick_y = g.left_stick_y;
        right_stick_x = g.right_stick_x;
        right_stick_y = g.right_stick_y;
    }

    private void handleUpdate(Button b, boolean updatedstatus) {
        if (updatedstatus){
            if(b.state == ButtonState.NOT_PRESSED || b.state == ButtonState.RELEASED)
                b.state = ButtonState.PRESSED;
            if(b.state == ButtonState.PRESSED)
                b.state = ButtonState.HELD;
        }
        else {
            if (b.state == ButtonState.PRESSED || b.state == ButtonState.HELD)
                b.state = ButtonState.RELEASED;
            if (b.state == ButtonState.RELEASED)
                b.state = ButtonState.NOT_PRESSED;
        }
    }
}
