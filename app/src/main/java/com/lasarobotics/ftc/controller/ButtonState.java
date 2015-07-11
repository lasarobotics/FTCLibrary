package com.lasarobotics.ftc.controller;
import com.google.gson.annotations.SerializedName;

/**
 * The state of the button
 */
public enum ButtonState
{
    @SerializedName("0")
    NOT_PRESSED(0),

    @SerializedName("1")
    PRESSED(1),

    @SerializedName("2")
    RELEASED(2),

    @SerializedName("3")
    HELD(3);
    private final int value;
    public int getValue() {
        return value;
    }
    private ButtonState(int value) {
        this.value = value;
    }
}
