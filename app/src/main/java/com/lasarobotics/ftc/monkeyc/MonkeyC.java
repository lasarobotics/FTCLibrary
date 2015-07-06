package com.lasarobotics.ftc.monkeyc;

import org.apache.http.util.ByteArrayBuffer;

import java.util.ArrayList;

/**
 * The MonkeyC (MonkeySee) library that handles recording and storing driver controls
 * These controls can be inserted during runtime (when the robot is moving)
 * or can be created prior to a match.  MonkeyDo can then execute these instructions.
 */
public class MonkeyC {
    public MonkeyC()
    {

    }

    ArrayList<byte[]> instructions = new ArrayList<>();

    public void add(byte[] instruction)
    {
        ByteArrayBuffer buffer = new ByteArrayBuffer(instruction.length + 1);
        buffer.append(new byte[] { (byte)instruction.length }, 0, 1);
        buffer.append(instruction, 1, instruction.length);
        instructions.add(buffer.toByteArray());
    }

    public void clear()
    {
        instructions.clear();
    }
}
