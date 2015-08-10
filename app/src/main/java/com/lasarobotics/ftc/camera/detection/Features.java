package com.lasarobotics.ftc.camera.detection;

/**
 * Implememts feature detection
 */
public class Features {
    public native void FindFeatures(long matAddrGr, long matAddrRgba);
}
