package com.lasarobotics.ftc.monkeyc;

import com.lasarobotics.ftc.monkeyc.command.Command;

import java.util.ArrayList;

/**
 * The MonkeyC (MonkeySee) library that handles recording and storing driver controls
 * These controls can be inserted during runtime (when the robot is moving)
 * or can be created prior to a match.  MonkeyDo can then execute these commands.
 */
public class MonkeyC {
    ArrayList<Command> commands;
    MonkeyDo _doPointer = null;

    //Create a standalone MonkeyC instance without piping to any output
    public MonkeyC()
    {
        this.commands = new ArrayList<Command>();
        this._doPointer = null;
    }

    //Pipe MonkeyC to a MonkeyDo class and run commands directly
    public MonkeyC(MonkeyDo othermonkey)
    {
        this.commands = new ArrayList<Command>();
        this._doPointer = othermonkey;
    }

    public void add(Command instruction)
    {
        //Write to the instruction array for writing to disk later
        commands.add(instruction);

        if (_doPointer != null)
        {
            //Perform the actions immediately
            _doPointer.run(instruction);
        }
    }

    public void add(Command[] instructions)
    {
        for (Command instruction : instructions)
            add(instruction);
    }

    public void add(ArrayList<Command> instructions)
    {
        for (Command instruction : instructions)
            add(instruction);
    }

    public MonkeyC(ArrayList<Command> commands) {
        this.commands = commands;
    }

    public void clear()
    {
        commands.clear();
    }

    public void write(String filename)
    {
        MonkeyWrite.writeFile(filename, commands);
    }
}
