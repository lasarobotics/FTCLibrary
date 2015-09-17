package com.lasarobotics.library.doodle.actions;

import com.lasarobotics.library.doodle.DoodleRunData;

/**
 * Defines a custom robot action
 * These actions are stored in the same file as the instruction data
 */
public abstract class Action {
    private String name;

    protected Action(String name) {
        this.name = name;
    }

    //Runs a snippet of code
    public abstract void run(DoodleRunData data);

    //Force the super class to create a toString() method
    public abstract String toString();
}