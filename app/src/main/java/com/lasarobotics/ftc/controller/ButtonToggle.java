package com.lasarobotics.ftc.controller;

/**
 * A regular toggle button
 */
public class ButtonToggle extends Button {
    public Boolean isPressed()
    {
        return (state == ButtonState.HELD || state == ButtonState.PRESSED);
    }
}
