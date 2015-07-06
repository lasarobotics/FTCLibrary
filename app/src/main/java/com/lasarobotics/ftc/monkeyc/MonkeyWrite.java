package com.lasarobotics.ftc.monkeyc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * MonkeyWrite handles reading and writing text files
 */
public class MonkeyWrite {

    File file;

    //Write monkey data from MonkeyC to a file
    MonkeyWrite(File file)
    {
        this.file = file;
    }

    void write(byte[] instruction)
    {
        writeFile(file, instruction);
    }

    //TODO static File[] getList();
    //TODO static File[] getList(Directory dir);

    //TODO static byte[] getData();

    static void writeFile(File file, ArrayList<byte[]> instructions)
    {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            for (byte[] bytes : instructions)
                stream.write(bytes);
            stream.flush();
            stream.close();
        } catch (IOException e)
        {
            return;
        }
    }

    static void writeFile(File file, byte[] instruction)
    {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(instruction);
            stream.flush();
            stream.close();
        } catch (IOException e)
        {
            return;
        }
    }
}
