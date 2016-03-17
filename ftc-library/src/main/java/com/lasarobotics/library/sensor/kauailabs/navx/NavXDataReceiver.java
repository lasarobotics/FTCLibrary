package com.lasarobotics.library.sensor.kauailabs.navx;

/**
 * Data receiver for the navX, allowing customized actions to be performed upon receiving data
 */
public interface NavXDataReceiver {
    void dataReceived(long timeDelta);
}