package com.lasarobotics.ftc.monkeyc;

import com.google.gson.annotations.SerializedName;

/**
 * Stores a change in a specific variable
 */
public class MonkeyDelta<T> {

    //TODO improve this so that we use key-value pairs instead
    @SerializedName("v")
    String variable;
    @SerializedName("d")
    T value;

    public MonkeyDelta(String variable, T value)
    {
        this.variable = variable;
        this.value = value;
    }
}
