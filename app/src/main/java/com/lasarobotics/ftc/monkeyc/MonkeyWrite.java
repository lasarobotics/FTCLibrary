package com.lasarobotics.ftc.monkeyc;

import com.google.gson.Gson;
import com.lasarobotics.ftc.monkeyc.instruction.Instruction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * MonkeyWrite handles reading and writing text files
 */
public class MonkeyWrite {

    //TODO static File[] getList();
    //TODO static File[] getList(Directory dir);

    //TODO static byte[] getData();

    public static void writeFile(String filename, ArrayList<Instruction> instructions)
    {
        try {
            PrintWriter p = new PrintWriter(new File(filename));
            Gson g = new Gson();
            p.write(g.toJson(instructions));
            p.close();
        } catch (IOException e)
        {
            return;
        }
    }
}
