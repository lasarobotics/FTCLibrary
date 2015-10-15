package com.lasarobotics.library.sensor.modernrobotics;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.TypeConversion;

import java.util.concurrent.locks.Lock;

/**
 * Created by ehsan on 10/14/15.
 */
public class Gyro implements I2cController.I2cPortReadyCallback{
    private static final int ADDRESS_I2C = 32;
    public static final int ADDRESS_COMMAND = 3;
    public static final int OFFSET_HEADING_LSB = 4;
    public static final int OFFSET_HEADING_MSB = 5;
    public static final int OFFSET_INTEGRATEDZ_LSB = 6;
    public static final int OFFSET_INTEGRATEDZ_MSB = 7;
    public static final int OFFSET_RAWX_LSB = 8;
    public static final int OFFSET_RAWX_MSB = 9;
    public static final int OFFSET_RAWY_LSB = 10;
    public static final int OFFSET_RAWY_MSB = 11;
    public static final int OFFSET_RAWZ_LSB = 13;
    public static final int OFFSET_RAWZ_MSB = 14;
    public static final int OFFSET_OFFSETZ_LSB = 14;
    public static final int OFFSET_OFFSETZ_MSB = 15;
    public static final int OFFSET_ZSCALE_LSB = 16;
    public static final int OFFSET_ZSCALE_MSB = 17;

    public static final int COMMAND_NORMAL = 0;
    public static final int COMMAND_RESETALL = 78;
    public static final int COMMAND_RESETINTEGRATOR = 82;

    private I2cDevice gyro;
    private final byte[] readCache;
    private final Lock readLock;
    private final byte[] writeCache;
    private final Lock writeLock;
    public Gyro (I2cDevice gIn){
        gyro = gIn;

        this.readCache = gyro.getI2cReadCache();
        this.readLock = gyro.getI2cReadCacheLock();
        this.writeCache = gyro.getI2cWriteCache();
        this.writeLock = gyro.getI2cWriteCacheLock();
        gyro.enableI2cReadMode(ADDRESS_I2C, 3, 14);
        //gyro.enableI2cWriteMode(ADDRESS_I2C,3,14);
        gyro.setI2cPortActionFlag();
        gyro.writeI2cCacheToController();
        gyro.registerForI2cPortReadyCallback(this);
    }
    public int readValue(int offset){
        byte out;
        try {
            readLock.lock();
            out = readCache[offset];
        } finally {
            readLock.unlock();
        }
        return TypeConversion.unsignedByteToInt(out);
    }
    public int readValue(int offsetlsb, int offsetmsb){
        byte lsb;
        byte msb;
        try {
            readLock.lock();
            lsb = readCache[offsetlsb];
            msb = readCache[offsetmsb];
        } finally {
            readLock.unlock();
        }
        return (msb << 8) | lsb;
    }
    public int getRotationX(){
        return readValue(OFFSET_RAWX_LSB,OFFSET_RAWX_MSB);
    }

    public int getRotationY(){
        return readValue(OFFSET_RAWY_LSB,OFFSET_RAWY_MSB);
    }

    public int getRotationZ(){
        return readValue(OFFSET_RAWZ_LSB,OFFSET_RAWZ_MSB);
    }
    @Override
    public void portIsReady(int i) {
        gyro.readI2cCacheFromController();
        gyro.writeI2cCacheToController();
    }
}
