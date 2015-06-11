package com.lasarobotics.ftc.controller;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by Ehsan on 6/9/2015.
 */
public class Controller {

    public Button dpad_up;
    public Button dpad_down;
    public Button dpad_left;
    public Button dpad_right;
    public Button a;
    public Button b;
    public Button x;
    public Button y;
    public Button guide;
    public Button start;
    public Button back;
    public Button left_bumper;
    public Button right_bumper;
    //Triggers use a float for how much they are pressed, so we will not count them as a button for now
//    public Button left_trigger;
//    public Button right_trigger;
    public void update(Gamepad g){
        handleUpdate(dpad_up,g.dpad_up);
        handleUpdate(dpad_down,g.dpad_down);
        handleUpdate(dpad_left,g.dpad_left);
        handleUpdate(a,g.a);
        handleUpdate(b,g.b);
        handleUpdate(x,g.x);
        handleUpdate(y,g.y);
        handleUpdate(guide,g.guide);
        handleUpdate(start,g.start);
        handleUpdate(back,g.back);
        handleUpdate(left_bumper,g.left_bumper);
        handleUpdate(right_bumper,g.right_bumper);

    }

    private void handleUpdate(Button b, boolean updatedstatus) {
        //Could be updated to detect "bumps"
        if (updatedstatus){
            if(b.state == ButtonState.NOT_PRESSED){
                b.state = ButtonState.PRESSED;
            }
            if(b.state == ButtonState.PRESSED){
                b.state = ButtonState.HELD;
            }
        }
        else{
            b.state = ButtonState.NOT_PRESSED;
        }
    }
}
