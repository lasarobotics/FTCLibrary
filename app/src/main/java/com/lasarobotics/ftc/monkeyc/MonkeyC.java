package com.lasarobotics.ftc.monkeyc;

import org.apache.http.util.ByteArrayBuffer;

import java.io.File;
import java.util.ArrayList;

/**
 * The MonkeyC (MonkeySee) library that handles recording and storing driver controls
 * These controls can be inserted during runtime (when the robot is moving)
 * or can be created prior to a match.  MonkeyDo can then execute these instructions.
 */
public class MonkeyC {
    MonkeyDo _doPointer = null;
    MonkeyWrite _writePointer = null;
    ArrayList<byte[]> instructions = new ArrayList<>();

    //Create a standalone MonkeyC instance without piping to any output
    public MonkeyC()
    {
        this._doPointer = null;
        this._writePointer = null;
    }

    //Pipe MonkeyC to a MonkeyDo class and run commands directly
    public MonkeyC(MonkeyDo othermonkey)
    {
        this._doPointer = othermonkey;
        this._writePointer = null;
    }

    //Pipe MonkeyC commands directly to a file (will overwrite)
    public MonkeyC(MonkeyWrite othermonkey)
    {
        this._writePointer = othermonkey;
        this._doPointer = null;
    }

    public void add(byte[] instruction)
    {
        //Write to the instruction array for later
        ByteArrayBuffer buffer = new ByteArrayBuffer(instruction.length + 1);
        buffer.append(new byte[]{(byte) instruction.length}, 0, 1);
        buffer.append(instruction, 1, instruction.length);
        instructions.add(buffer.toByteArray());

        if (_doPointer == null) {
            if (_writePointer != null)
            {
                //Write to a file
                _writePointer.write(instruction);
            }
        }
        else
        {
            //Perform the actions immediately
            _doPointer.run(instruction);
        }
    }

    public void clear()
    {
        instructions.clear();
    }

    public void write(File file)
    {
        MonkeyWrite.writeFile(file, instructions);
    }
}
