package com.lasarobotics.ftc.util;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

/**
 * Implements advanced timers with events and precision manipulation.
 */
public class Timers {
    private Hashtable<String,Long> store = new Hashtable<String,Long>();
    private int defaultprecision;

    /**
     * Instantiates the timer class with the default millisecond precision.
     */
    public Timers(){
        defaultprecision = 5;
    }

    /**
     * Instantiates the timer class with an arbitrary precision in milliseconds.
     * @param precision Precision of the clock, in milliseconds.
     */
    public Timers(int precision){
        this.defaultprecision = precision;
    }

    /**
     * Start (and create, if nonexistent) a clock with a specified name.
     * @param name The clock name
     */
    public void startClock(String name){
        store.put(name,System.nanoTime());
    }

    /**
     * Reset a clock with the specified name.  Clock will continue running immediately.
     * @param name The clock name
     */
    public void resetClock(String name){
        if (store.containsKey(name)){
            store.put(name,System.nanoTime());
        }
        else{
            throw new IllegalArgumentException("Timer " + name + " does not exist.");
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
            throw new IllegalArgumentException("Timer " + name + " does not exist.");
        }
    }

    /**
     * Get clock value with precision in a given time unit
     * @param name Name of the clock
     * @param timeUnit TimeUnit the output should be in
     * @return The value of the clock converted to the time unit specified (may lose precision)
     */
    public long getClockValue(String name,TimeUnit timeUnit){
        if (store.containsKey(name)){
            Long start = store.get(name);
            long nanoDiff = Math.abs(System.nanoTime() - start);
            return timeUnit.convert(nanoDiff,TimeUnit.NANOSECONDS);
        }
        else{
            throw new IllegalArgumentException("Timer " + name + " does not exist.");
        }
    }

    /**
     * Returns whether the clock is at the specified amount of milliseconds
     * @param name The clock name
     * @param target The target time in milliseconds
     * @return True if at the target (+- precision), false otherwise
     */
    public boolean isAtTargetMillis(String name, long target){
        if (store.containsKey(name)){
            Long start = store.get(name);
            long milliDiff = TimeUnit.MILLISECONDS.convert(Math.abs(target - getClockValue(name)), TimeUnit.NANOSECONDS);
            return milliDiff < defaultprecision;
        }
        else{
            throw new IllegalArgumentException("Timer " + name + " does not exist.");
        }
    }
    /**
     * Returns whether the clock is at the specified amount of milliseconds
     * @param name The clock name
     * @param target The target time in milliseconds
     * @param precision How much target and clock value can differ by
     * @return True if at the target (+- precision), false otherwise
     */
    public boolean isAtTargetMillis(String name, long target,long precision){
        if (store.containsKey(name)){
            Long start = store.get(name);
            long milliDiff = TimeUnit.MILLISECONDS.convert(Math.abs(target - getClockValue(name)),TimeUnit.NANOSECONDS);
            return milliDiff < precision;
        }
        else{
            throw new IllegalArgumentException("Timer " + name + " does not exist.");
        }
    }

    /**
     * Gets the precision in milliseconds
     * @return Precision in milliseconds
     */
    public long getPrecision() {
        return defaultprecision;
    }

    /**
     * Sets the precision to a value
     * @param precision The precision of the clock, in milliseconds.
     */
    public void setPrecision(int precision) {
        this.defaultprecision = precision;
    }
}
