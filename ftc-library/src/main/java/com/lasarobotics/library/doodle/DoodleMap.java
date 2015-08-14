package com.lasarobotics.library.doodle;

/**
 * Specifies the motors and servos encoded in the Doodle specification
 * These specs will be written into a config text file in human-readable JSON
 */
public class DoodleMap {
    public enum MotorFlags
    {
        MOVEMENT(1);

        int flag;
        MotorFlags(int flag)
        {
            this.flag = flag;
        }
    }


}
