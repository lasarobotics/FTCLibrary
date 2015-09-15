package com.lasarobotics.library.controller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Implements a functional controller with an event API
 */
public class Controller {

    //BUTTONS
    //Buttons use an integer to identify state, found in controller.ButtonState
    public int dpad_up;         //Directional pad UP
    public int dpad_down;       //Directional pad DOWN
    public int dpad_left;       //Directional pad LEFT
    public int dpad_right;      //Directional pad RIGHT
    public int a;               //A button
    public int b;               //B button
    public int x;               //X button
    public int y;               //Y button
    public int guide;           //Guide button
    public int start;           //START button
    public int back;            //BACK button
    public int left_bumper;     //left bumper
    public int right_bumper;    //right bumper

    //Triggers use a float for how much they are pressed
    public float left_trigger;  //left trigger
    public float right_trigger; //right trigger

    //Joysticks don't have any events, just values
    public float left_stick_x;  //left joystick X axis
    public float left_stick_y;  //left joystick Y axis
    public float right_stick_x; //right joystick X axis
    public float right_stick_y; //right joystick Y axis

    /**
     * Initialize a blank controller
     */
    public Controller() {

    }

    /**
     * Initialize a controller from another (cloning)
     * @param another Another Controller
     */
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

    /**
     * Initialize a controller from a Gamepad (FIRST library underlayer)
     * @param g
     */
    public Controller(Gamepad g) {
        update(g);
    }

    /**
     * Update the Controller states from a Gamepad.
     * CALL THIS METHOD ON EVERY EVENT LOOP!
     * @param g
     */
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
        right_bumper = handleUpdate(right_bumper,g.right_bumper);
        left_trigger = g.left_trigger;
        right_trigger = g.right_trigger;
        left_stick_x = g.left_stick_x;
        left_stick_y = g.left_stick_y;
        right_stick_x = g.right_stick_x;
        right_stick_y = g.right_stick_y;
    }

    /**
     * Update an individual button or bumper
     * @param b Variable from Controller
     * @param updatedstatus Boolean from Gamepad
     * @return The new state
     */
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

    @Override
    public String toString() {
        return "Controller{" +
                "dpad_up=" + dpad_up +
                ", dpad_down=" + dpad_down +
                ", dpad_left=" + dpad_left +
                ", dpad_right=" + dpad_right +
                ", a=" + a +
                ", b=" + b +
                ", x=" + x +
                ", y=" + y +
                ", guide=" + guide +
                ", start=" + start +
                ", back=" + back +
                ", left_bumper=" + left_bumper +
                ", right_bumper=" + right_bumper +
                ", left_trigger=" + left_trigger +
                ", right_trigger=" + right_trigger +
                ", left_stick_x=" + left_stick_x +
                ", left_stick_y=" + left_stick_y +
                ", right_stick_x=" + right_stick_x +
                ", right_stick_y=" + right_stick_y +
                '}';
    }
}
