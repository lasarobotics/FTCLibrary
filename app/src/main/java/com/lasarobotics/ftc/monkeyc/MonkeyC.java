package com.lasarobotics.ftc.monkeyc;

import com.lasarobotics.ftc.monkeyc.instruction.Instruction;

import org.apache.http.util.ByteArrayBuffer;

import java.io.File;
import java.util.ArrayList;

/**
 * The MonkeyC (MonkeySee) library that handles recording and storing driver controls
 * These controls can be inserted during runtime (when the robot is moving)
 * or can be created prior to a match.  MonkeyDo can then execute these instructions.
 */
public class MonkeyC {
    ArrayList<Instruction> instructions;

    //Create a standalone MonkeyC instance without piping to any output
    public MonkeyC()
    {
        this.instructions = new ArrayList<Instruction>();
    }

    public MonkeyC(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }

    public void add(Instruction i)
    {
        instructions.add(i);
    }

    public void clear()
    {
        instructions.clear();
    }

    public void write(String filename)
    {
        MonkeyWrite.writeFile(filename, instructions);
    }
}
