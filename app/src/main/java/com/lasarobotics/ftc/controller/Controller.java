package com.lasarobotics.ftc.controller;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Implements a functional controller with an event API
 */
public class Controller {

    private static final Map<Integer, ButtonState> intToButtonStateMap = new HashMap<Integer, ButtonState>();
    static {
        for (ButtonState type : ButtonState.values()) {
            intToButtonStateMap.put(type.getValue(), type);
        }
    }
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
        this.dpad_up = new ButtonToggle();
        this.dpad_up.state = intToButtonStateMap.get(dpad_up.state.getValue());

        this.dpad_down = new ButtonToggle();
        this.dpad_down.state = intToButtonStateMap.get(dpad_down.state.getValue());

        this.dpad_left = new ButtonToggle();
        this.dpad_left.state = intToButtonStateMap.get(dpad_left.state.getValue());

        this.dpad_right = new ButtonToggle();
        this.dpad_right.state  = intToButtonStateMap.get(dpad_right.state.getValue());

        this.a = new ButtonToggle();
        this.a.state  = intToButtonStateMap.get(a.state.getValue());

        this.b = new ButtonToggle();
        this.b.state  = intToButtonStateMap.get(b.state.getValue());

        this.x = new ButtonToggle();
        this.x.state  = intToButtonStateMap.get(x.state.getValue());

        this.y = new ButtonToggle();
        this.y.state  = intToButtonStateMap.get(y.state.getValue());

        this.guide = new ButtonToggle();
        this.guide.state  = intToButtonStateMap.get(guide.state.getValue());

        this.start = new ButtonToggle();
        this.start.state  = intToButtonStateMap.get(start.state.getValue());

        this.back = new ButtonToggle();
        this.back.state  = intToButtonStateMap.get(back.state.getValue());

        this.left_bumper = another.left_bumper;
        this.right_bumper = another.right_bumper;
        this.left_trigger = another.left_trigger;
        this.right_trigger = new ButtonFloat();
        this.right_trigger.value = another.right_trigger.value;

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

    //TODO make an abstract button class so we don't have two duplicate methods
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

    public Hashtable<String, Boolean> getAllButtonValues()
    {
        Hashtable<String,Boolean> table = new Hashtable<>();
        table.put("du", dpad_up.isPressed());
        table.put("dd", dpad_down.isPressed());
        table.put("dl", dpad_left.isPressed());
        table.put("dr", dpad_right.isPressed());
        table.put("a", a.isPressed());
        table.put("b", b.isPressed());
        table.put("x", x.isPressed());
        table.put("y", y.isPressed());
        table.put("guide", guide.isPressed());
        table.put("start", start.isPressed());
        table.put("back", back.isPressed());
        table.put("bl", left_bumper.isPressed());
        table.put("br", right_bumper.isPressed());
        table.put("tl", left_trigger.isPressed());
        table.put("tr", right_trigger.isPressed());
        return table;
    }
    public Hashtable<String, Float> getAllJoystickValues()
    {
        Hashtable<String,Float> table = new Hashtable<>();
        table.put("lx", left_stick_x);
        table.put("ly", left_stick_y);
        table.put("rx", right_stick_x);
        table.put("ry", right_stick_y);
        return table;
    }

    public void setState(String id, int value)
    {
        switch(id)
        {
            case "du":
                handleUpdate(dpad_up, value == 1); break;
            case "dd":
                handleUpdate(dpad_down, value == 1); break;
            case "dl":
                handleUpdate(dpad_left, value == 1); break;
            case "dr":
                handleUpdate(dpad_right, value == 1); break;
            case "a":
                handleUpdate(a, value == 1); break;
            case "b":
                handleUpdate(b, value == 1); break;
            case "x":
                handleUpdate(x, value == 1); break;
            case "y":
                handleUpdate(y, value == 1); break;
            case "guide":
                handleUpdate(guide, value == 1); break;
            case "start":
                handleUpdate(start, value == 1); break;
            case "back":
                handleUpdate(back, value == 1); break;
            case "bl":
                handleUpdate(left_bumper, value == 1); break;
            case "br":
                handleUpdate(right_bumper, value == 1); break;
            case "tl":
                handleUpdate(left_trigger, value == 1); break;
            case "tr":
                handleUpdate(right_trigger, value == 1); break;
            case "lx":
                left_stick_x = (float)value; break;
            case "ly":
                left_stick_y = (float)value; break;
            case "rx":
                right_stick_x = (float)value; break;
            case "ry":
                right_stick_y = (float)value; break;
            default:
                return;
        }
    }
}
