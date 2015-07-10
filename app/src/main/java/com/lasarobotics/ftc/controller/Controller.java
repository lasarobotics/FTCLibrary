package com.lasarobotics.ftc.controller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Implements a functional controller with an event API
 */
public class Controller {

    public ButtonToggle dpad_up = new ButtonToggle();
    public ButtonToggle dpad_down = new ButtonToggle();
    public ButtonToggle dpad_left = new ButtonToggle();
    public ButtonToggle dpad_right = new ButtonToggle();
    public ButtonToggle a = new ButtonToggle();
    public ButtonToggle b = new ButtonToggle();
    public ButtonToggle x = new ButtonToggle();
    public ButtonToggle y = new ButtonToggle();
    public ButtonToggle guide = new ButtonToggle();
    public ButtonToggle start = new ButtonToggle();
    public ButtonToggle back = new ButtonToggle();
    public ButtonToggle left_bumper = new ButtonToggle();
    public ButtonToggle right_bumper = new ButtonToggle();

    //Triggers use a float for how much they are pressed
    public ButtonFloat left_trigger = new ButtonFloat();
    public ButtonFloat right_trigger = new ButtonFloat();

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

    private void handleUpdate(ButtonToggle b, boolean updatedstatus) {
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
    private void handleUpdate(ButtonFloat b, boolean updatedstatus) {
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
