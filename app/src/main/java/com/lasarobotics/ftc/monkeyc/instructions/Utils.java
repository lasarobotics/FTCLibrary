package com.lasarobotics.ftc.monkeyc.instructions;

import java.nio.ByteBuffer;

/**
 * Provides internal MonkeyC functions
 */
abstract class Utils {
    public static ByteBuffer putString(ByteBuffer buf, String str)
    {
        buf.put((byte)str.length());
        for (char c : str.toCharArray())
            buf.putChar(c);
        return buf;
    }
}
