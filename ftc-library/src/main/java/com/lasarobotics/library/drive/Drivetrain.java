package com.lasarobotics.library.drive;

/**
 * Drive train types and useful information about these drivetrains required by various classes
 */
public enum Drivetrain {
    TANK(false),
    MECANUM(true),
    OTHER_OMNIDIRECTIONAL(true);

    boolean isOmnidirectional;

    Drivetrain(boolean isOmnidirectional) {
        this.isOmnidirectional = isOmnidirectional;
    }

    /**
     * Test whether the selected drive train can strafe
     * If true, the drive train can, in addition to basic tank capabilities:
     * 1. Strafe (move sideways), and therefore can move in virtually any direction
     * 2. Supports field oriented drive - generally true if 1. is true
     * If this is NOT true, then it is assumed that the drive train also must
     * rotate in order to move to a target not directly in front or behind it
     *
     * @return True if omnidirectional, false otherwise
     */
    public boolean isOmnidirectional() {
        return isOmnidirectional;
    }
}
