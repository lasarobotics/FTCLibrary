package com.lasarobotics.library.sensor.legacy.lego;

import com.qualcomm.robotcore.hardware.LegacyModule;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteOrder;

/**
 * Internal NXT touch sensor implementation
 */
class TouchInternal extends TouchSensor {
    private final LegacyModule legacyModule;
    private final int physicalPort;
    private boolean isread = false;

    TouchInternal(LegacyModule legacyModule, int physicalPort) {
        //legacyModule.setDigitalLine(physicalPort, 0, true);
        //legacyModule.enableAnalogReadMode(physicalPort);



        this.legacyModule = legacyModule;
        this.physicalPort = physicalPort;

        init();
    }

    private void init()
    {
        legacyModule.registerForPortReadyCallback(new LegacyModule.PortReadyCallback() {
            @Override
            public void portIsReady(int i) {
                if (isread) {
                    byte[] a = legacyModule.readAnalog(physicalPort);
                    legacyModule.readI2cCacheFromModule(physicalPort);
                    byte[] b = legacyModule.getI2cReadCache(physicalPort);
                    byte[] c = legacyModule.getI2cWriteCache(physicalPort);
                    byte j = b[0];
                }
                else
                {
                    //legacyModule.enableNxtI2cReadMode(physicalPort, 16, 0, 1);
                    legacyModule.setNxtI2cPortActionFlag(physicalPort);
                    //legacyModule.writeI2cCacheToModule(physicalPort);
                    isread = true;
                }
            }
        }, physicalPort);
    }

    public double getRawValue() {
        byte[] a = this.legacyModule.readAnalog(this.physicalPort);
        double var1 = (double) TypeConversion.byteArrayToShort(a, ByteOrder.LITTLE_ENDIAN);
        return var1;
    }

    public double getValue() {
        double var1 = getRawValue();
        var1 = var1 > 675.0D?0.0D:1.0D;
        return var1;
    }

    public boolean isPressed() {
        return this.getValue() > 0.0D;
    }

    public String getDeviceName() {
        return "NXT Touch Sensor";
    }

    public String getConnectionInfo() {
        return this.legacyModule.getConnectionInfo() + "; port " + this.physicalPort;
    }

    public int getVersion() {
        return 1;
    }

    public void close() {
    }
}
