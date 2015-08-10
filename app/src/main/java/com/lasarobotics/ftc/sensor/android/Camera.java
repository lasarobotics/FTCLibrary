package com.lasarobotics.ftc.sensor.android;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Implements the Android camera
 */
public class Camera {
    public static boolean isHardwareAvailable(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
