package com.lasarobotics.library.sensor.modernrobotics;

import com.qualcomm.robotcore.hardware.I2cController;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.TypeConversion;

import java.util.concurrent.locks.Lock;

/**
 * Driver for the Modern Robotics Gyroscope
 */
public class Gyro implements I2cController.I2cPortReadyCallback {
    public static final int ADDRESS_COMMAND = 3;
    public static final int OFFSET_COMMAND = 4;
    public static final int OFFSET_HEADING_LSB = 5;
    public static final int OFFSET_HEADING_MSB = 6;
    public static final int OFFSET_INTEGRATEDZ_LSB = 7;
    public static final int OFFSET_INTEGRATEDZ_MSB = 8;
    public static final int OFFSET_RAWX_LSB = 9;
    public static final int OFFSET_RAWX_MSB = 10;
    public static final int OFFSET_RAWY_LSB = 11;
    public static final int OFFSET_RAWY_MSB = 12;
    public static final int OFFSET_RAWZ_LSB = 13;
    public static final int OFFSET_RAWZ_MSB = 14;
    public static final int OFFSET_OFFSETZ_LSB = 15;
    public static final int OFFSET_OFFSETZ_MSB = 16;
    public static final int OFFSET_ZSCALE_LSB = 17;
    public static final int OFFSET_ZSCALE_MSB = 18;
    public static final int BUFFER_LENGTH = 15;
    public static final int WRITE_LENGTH = 1;
    public static final int COMMAND_NORMAL = 0;
    public static final int COMMAND_RESETALL = 78;
    public static final int COMMAND_RESETINTEGRATOR = 82;
    private static final int ADDRESS_I2C = 32;
    private final byte[] readCache;
    private final Lock readLock;
    private final byte[] writeCache;
    private final Lock writeLock;
    private I2cDevice gyro;
    private I2cStatus status;

    /**
     * Instantiate the gyro driver by:
     * Storing readCache,readLock,writeCache,writeLock for use in program
     * Registering for portReadyCallback
     *
     * @param gIn I2cDevice for gyroscope
     */
    public Gyro(I2cDevice gIn) {
        gyro = gIn;

        this.readCache = gyro.getI2cReadCache();
        this.readLock = gyro.getI2cReadCacheLock();
        this.writeCache = gyro.getI2cWriteCache();
        this.writeLock = gyro.getI2cWriteCacheLock();
        gyro.enableI2cReadMode(ADDRESS_I2C, ADDRESS_COMMAND, BUFFER_LENGTH);
        gyro.setI2cPortActionFlag();
        gyro.writeI2cCacheToController();
        gyro.registerForI2cPortReadyCallback(this);
    }

    /**
     * Read a value from a specific register on the gyroscope
     *
     * @param offset Offset to read from (defined as constants in this file)
     * @return unsigned integer value of the register
     */
    public int readValue(int offset) {
        byte out;
        try {
            //Lock cache
            readLock.lock();
            //Get value from cache
            out = readCache[offset];
        } finally {
            //Release lock
            readLock.unlock();
        }
        return TypeConversion.unsignedByteToInt(out);
    }

    /**
     * Read a value from a specfic set of lsb/msb register pairs used to represent a 16 bit integer
     *
     * @param offsetlsb Least significant byte (bottom 8 bits of 16 bit unsigned integer)
     * @param offsetmsb Most significant byte (upper 8 bits of 16 bit unsigned integer)
     * @return 16 bit unsigned integer from a combination of the msb and lsb.
     */
    public int readValue(int offsetlsb, int offsetmsb) {
        byte lsb;
        byte msb;
        try {
            //Lock cache
            readLock.lock();
            //Get values from cache
            lsb = readCache[offsetlsb];
            msb = readCache[offsetmsb];
        } finally {
            //Release lock
            readLock.unlock();
        }
        //Convert msb/lsb pair to unsigned integer
        //& 0x0000ff00 used to ensure unsigned
        return ((msb << 8) & 0x0000ff00) | (lsb & 0x000000ff);
    }

    /**
     * Get heading register
     *
     * @return Gyroscope heading reading
     */
    public int getHeading() {
        return readValue(OFFSET_HEADING_LSB, OFFSET_HEADING_MSB);
    }

    /**
     * Get raw X rotation from gyroscope
     *
     * @return Raw x value
     */
    public int getRotationX() {
        return readValue(OFFSET_RAWX_LSB, OFFSET_RAWX_MSB);
    }

    /**
     * Get raw Y rotation from gyroscope
     *
     * @return Raw y value
     */
    public int getRotationY() {
        return readValue(OFFSET_RAWY_LSB, OFFSET_RAWY_MSB);
    }

    /**
     * Get raw Z rotation from gyroscope
     *
     * @return Raw z value
     */
    public int getRotationZ() {
        return readValue(OFFSET_RAWZ_LSB, OFFSET_RAWZ_MSB);
    }

    /**
     * Get Z axis offset from gyroscope
     *
     * @return Z axis offset
     */
    public int getOffsetZ() {
        return readValue(OFFSET_OFFSETZ_LSB, OFFSET_OFFSETZ_MSB);
    }

    /**
     * Get Z axis scaling coefficient from gyroscope
     *
     * @return Z axis scaling coefficient
     */
    public int getZScale() {
        return readValue(OFFSET_ZSCALE_LSB, OFFSET_ZSCALE_MSB);
    }

    /**
     * Get Z axis integrator value from gyroscope
     *
     * @return Z axis integrator
     */
    public int getIntegratedZ() {
        return readValue(OFFSET_INTEGRATEDZ_LSB, OFFSET_INTEGRATEDZ_MSB);
    }

    /**
     * Returns if gyroscope is in write mode
     *
     * @return true if gyroscope in write mode, false otherwise
     */
    public boolean isInWriteMode() {
        return gyro.isI2cPortInWriteMode();
    }

    /**
     * Returns if gyroscope is in read mode
     *
     * @return true if gyroscope in read mode, false otherwise
     */
    public boolean isInReadMode() {
        return gyro.isI2cPortInReadMode();
    }

    /**
     * Handles I2C read and write operations when port is ready
     *
     * @param port Port number
     */
    @Override
    public void portIsReady(int port) {
        //Set that an action is occuring
        gyro.setI2cPortActionFlag();
        //Read gyro values into readCache
        gyro.readI2cCacheFromController();
        //If we have something to write
        if (status == I2cStatus.WRITE) {
            //Enable write mode and write cache
            gyro.enableI2cWriteMode(ADDRESS_I2C, ADDRESS_COMMAND, WRITE_LENGTH);
            gyro.writeI2cCacheToController();
            //Set our state to post write
            status = I2cStatus.POSTWRITE;
        } else if (status == I2cStatus.POSTWRITE) {
            //If we just wrote something, reenable read mode
            gyro.enableI2cReadMode(ADDRESS_I2C, ADDRESS_COMMAND, BUFFER_LENGTH);
            gyro.writeI2cCacheToController();
            //Set our status to normal
            status = I2cStatus.NORMAL;
        } else {
            //If our status is normal only write port flag
            gyro.writeI2cPortFlagOnlyToController();
        }
    }

    public void resetHeading() {
        try {
            //Lock write cache
            writeLock.lock();
            //Write reset command to write register
            writeCache[OFFSET_COMMAND] = COMMAND_RESETALL;
        } finally {
            //Unlock write cache
            writeLock.unlock();
            //Set state to indicate something needs to be written
            status = I2cStatus.WRITE;
        }
    }

    /**
     * Status tracking enum
     */
    private static enum I2cStatus {
        NORMAL,
        WRITE,
        POSTWRITE;

        private I2cStatus() {
        }
    }
}