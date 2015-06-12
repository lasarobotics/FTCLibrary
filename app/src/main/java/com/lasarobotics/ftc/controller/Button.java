package com.lasarobotics.ftc.controller;

/**
 * A button on a controller
 */
public abstract class Button {
    public ButtonState state;
    public abstract Boolean isPressed();
}
