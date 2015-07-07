package com.lasarobotics.ftc.monkeyc;

import com.google.gson.Gson;
import com.lasarobotics.ftc.monkeyc.command.Command;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * MonkeyWrite handles reading and writing text files with instructions created by MonkeyC
 */
public class MonkeyWrite {

    //TODO static File[] getList();
    //TODO static File[] getList(Directory dir);

    //TODO static byte[] getData();

    public static void writeFile(String filename, ArrayList<Command> commands)
    {
        try {
            PrintWriter p = new PrintWriter(new File(filename));
            Gson g = new Gson();
            p.write(g.toJson(commands));
            p.close();
        } catch (IOException e)
        {
            return;
        }
    }
}
