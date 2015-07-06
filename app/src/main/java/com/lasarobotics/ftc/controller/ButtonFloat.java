package com.lasarobotics.ftc.controller;

import com.lasarobotics.ftc.util.MathUtil;

/**
 * A button with weight
 */
public class ButtonFloat extends Button {
    public float value;
    private static double threshold = 0.1F;

    public static void setThreshold(double threshold)
    {
        threshold = MathUtil.coerce(0.0D, 1.0D, threshold);
    }
    public static double getThreshold() { return threshold; }

    public Boolean isPressed()
    {
        return (value >= threshold);
    }
}
