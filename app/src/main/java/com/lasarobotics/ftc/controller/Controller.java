package com.lasarobotics.ftc.controller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Implements a functional controller with an event API
 */
public class Controller {

    public int dpad_up;
    public int dpad_down;
    public int dpad_left;
    public int dpad_right;
    public int a;
    public int b;
    public int x;
    public int y;
    public int guide;
    public int start;
    public int back;
    public int left_bumper;
    public int right_bumper;

    //Triggers use a float for how much they are pressed
    public float left_trigger;
    public float right_trigger;

    //Joysticks don't have any events
    public float left_stick_x;
    public float left_stick_y;
    public float right_stick_x;
    public float right_stick_y;
    public Controller() {
    }
    public Controller(Controller another) {
        this.dpad_up = another.dpad_up;
        this.dpad_down = another.dpad_down;
        this.dpad_left = another.dpad_left;
        this.dpad_right = another.dpad_right;
        this.a = another.a;
        this.b = another.b;
        this.x = another.x;
        this.y = another.y;
        this.guide = another.guide;
        this.start = another.start;
        this.back = another.back;
        this.left_bumper = another.left_bumper;
        this.right_bumper = another.right_bumper;

        this.left_trigger = another.left_trigger;
        this.right_trigger = another.right_trigger;

        this.left_stick_x = another.left_stick_x;
        this.left_stick_y = another.left_stick_y;
        this.right_stick_x = another.right_stick_x;
        this.right_stick_y = another.right_stick_y;
    }

    public Controller(Gamepad g) {
        update(g);
    }

    public void update(Gamepad g){
        dpad_up = handleUpdate(dpad_up,g.dpad_up);
        dpad_down = handleUpdate(dpad_down,g.dpad_down);
        dpad_left = handleUpdate(dpad_left,g.dpad_left);
        dpad_right = handleUpdate(dpad_right,g.dpad_right);
        a = handleUpdate(a,g.a);
        b = handleUpdate(b,g.b);
        x = handleUpdate(x,g.x);
        y = handleUpdate(y,g.y);
        guide = handleUpdate(guide,g.guide);
        start = handleUpdate(start,g.start);
        back = handleUpdate(back,g.back);
        left_bumper = handleUpdate(left_bumper,g.left_bumper);
        right_trigger = handleUpdate(right_bumper,g.right_bumper);
        left_trigger = g.left_trigger;
        right_trigger = g.right_trigger;
        left_stick_x = g.left_stick_x;
        left_stick_y = g.left_stick_y;
        right_stick_x = g.right_stick_x;
        right_stick_y = g.right_stick_y;
    }

    private int handleUpdate(Integer b, boolean updatedstatus) {
        if (updatedstatus) {
            if (b == ButtonState.NOT_PRESSED || b == ButtonState.RELEASED)
                return ButtonState.PRESSED;
            else
                return ButtonState.HELD;
        } else {
            if (b == ButtonState.PRESSED || b == ButtonState.HELD)
                return ButtonState.RELEASED;
            else
                return ButtonState.NOT_PRESSED;
        }
    }
}
