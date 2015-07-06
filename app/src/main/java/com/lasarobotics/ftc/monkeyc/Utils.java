package com.lasarobotics.ftc.monkeyc;

import java.nio.ByteBuffer;

/**
 * Provides internal MonkeyC functions
 */
abstract class Utils {
    static ByteBuffer putString(ByteBuffer buf, String str)
    {
        buf.put((byte)str.length());
        for (char c : str.toCharArray())
            buf.putChar(c);
        return buf;
    }

    static String getString(MonkeyDo monkey)
    {
        ByteBuffer buf = monkey.instruction;

        short length = (short)(buf.get() & 0xFF);
        StringBuilder s = new StringBuilder();

        for(int i=0; i<length; i++)
            s.append(buf.getChar());

        return s.toString();
    }
}
