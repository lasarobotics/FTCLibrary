package com.lasarobotics.library.doodle.maps;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Maps robot movement to specific motors
 */
public abstract class DoodleMap {

    /*** OVERRIDABLE METHODS ***/

    public abstract void move(float amplitude, float rotation, float translation);

    /*** OTHER METHODS ***/

    HardwareMap map;
    RangeOfMotion rangeOfMotion = RangeOfMotion.DRIVE_AMPLITUDE_ROTATION;

    protected DoodleMap(HardwareMap map, RangeOfMotion rangeOfMotion)
    {
        this.map = map;
        this.rangeOfMotion = rangeOfMotion;
    }

    public void update(HardwareMap map)
    {
        this.map = map;
    }

    /*
     * Designates the capabilities of the drive train
     */
    protected enum RangeOfMotion
    {
        DRIVE_AMPLITUDE_ONLY,                   //drive trains only capable of moving forwards and backwards, should not be used
        DRIVE_AMPLITUDE_ROTATION,               //drive trains capable of rotation and amplitude (such as tank drive)
        DRIVE_AMPLITUDE_ROTATION_TRANSLATION;   //drive trains capable of amplitude, rotation, and translation (such as mecanum drive)
    }

    public void move(float amplitude) { move(amplitude, 0, 0); }
    public void move(float amplitude, float rotation) { move(amplitude, rotation, 0); }
    public void move(float amplitude, Float translation) { move(amplitude, 0, translation); }
}
