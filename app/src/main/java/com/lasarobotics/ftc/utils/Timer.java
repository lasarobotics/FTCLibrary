package com.lasarobotics.ftc.utils;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ehsan on 6/11/2015.
 */
public class Timer {
    private HashMap<String,Long> store;
    private long precision;
    public Timer(){
        precision = 5L;
    }
    public Timer(long precision){
        this.precision = precision;
    }
    public void startClock(String name){
        store.put(name,System.nanoTime());
    }
    public void resetClock(String name){
        if (store.containsKey(name)){
            store.put(name,System.nanoTime());
        }
        else{
            throw new IllegalArgumentException("Timer" + name + " does not exist.");
        }
    }

    /**
     * Get clock value. Defaults to millisecond precision.
     * @param name Name of the clock
     * @return Value of clock in milliseconds
     */
    public long getClockValue(String name){
        if (store.containsKey(name)){
           Long start = store.get(name);
           return TimeUnit.MILLISECONDS.convert(Math.abs(System.nanoTime()-start),TimeUnit.NANOSECONDS);
        }
        else{
            throw new IllegalArgumentException("Timer" + name + " does not exist.");
        }
    }

    /**
     * Get clock value with precision in a given time unit
     * @param name Name of the clock
     * @param timeUnit TimeUnit the output should be in
     * @return The value of the clock converted to the time unit specified (may loose precision)
     */
    public long getClockValue(String name,TimeUnit timeUnit){
        if (store.containsKey(name)){
            Long start = store.get(name);
            long nanoDiff = Math.abs(System.nanoTime() - start);
            return timeUnit.convert(nanoDiff,TimeUnit.NANOSECONDS);
        }
        else{
            throw new IllegalArgumentException("Timer" + name + " does not exist.");
        }
    }

    public boolean isAtTargetMillis(String name, long target){
        if (store.containsKey(name)){
            Long start = store.get(name);
            long milliDiff = TimeUnit.MILLISECONDS.convert(Math.abs(target - Math.abs(System.nanoTime()-start)),TimeUnit.NANOSECONDS);
            return milliDiff < precision;
        }
        else{
            throw new IllegalArgumentException("Timer" + name + " does not exist.");
        }
    }


    public long getPrecision() {
        return precision;
    }

    public void setPrecision(long precision) {
        this.precision = precision;
    }
}
