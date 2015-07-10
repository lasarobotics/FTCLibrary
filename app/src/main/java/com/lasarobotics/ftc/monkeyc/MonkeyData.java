package com.lasarobotics.ftc.monkeyc;

import com.lasarobotics.ftc.controller.Controller;

/**
 * Created by Ehsan on 7/8/2015.
 */
public class MonkeyData {
    public Controller one;
    public Controller two;
    public float time;

    public MonkeyData(Controller local, Controller local2, float time) {
        this.one = local;
        this.two = local2;
        this.time = time;
    }
}
