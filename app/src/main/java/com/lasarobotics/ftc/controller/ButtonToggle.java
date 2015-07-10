package com.lasarobotics.ftc.controller;

/**
 * A regular toggle button
 */
public class ButtonToggle {
    public ButtonState state;
    public ButtonToggle(){
        state = ButtonState.NOT_PRESSED;
    }
    public Boolean isPressed()
    {
        return (state == ButtonState.HELD || state == ButtonState.PRESSED);
    }
}
