package com.lasarobotics.ftc.monkeyc;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.lasarobotics.ftc.controller.ButtonFloat;
import com.lasarobotics.ftc.controller.ButtonToggle;
import com.lasarobotics.ftc.controller.Controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

/**
 * MonkeyUtil handles reading and writing text files with instructions created by MonkeyC
 */
public class MonkeyUtil {
    public final static String FILE_DIR = Environment.getExternalStorageDirectory() + "/MonkeyC/";
    //TODO static File[] getList();
    //TODO static File[] getList(Directory dir);

    //TODO static byte[] getData();

    private static MonkeyDelta<Boolean>[] getButtonDeltas(Controller current, Controller previous)
    {
        //Get values
        Hashtable<String, Boolean> button_current = current.getAllButtonValues();
        Hashtable<String, Boolean> button_previous = previous.getAllButtonValues();

        //Create delta array
        ArrayList<MonkeyDelta<Boolean>> deltas = new ArrayList<>();

        //Test if anything was changed
        boolean changed = false;

        //Compare buttons
        for (int i=0; i<button_current.size(); i++)
        {
            String key = button_current.keys().nextElement();
            Boolean cur = button_current.get(key);
            Boolean prev = button_previous.get(key);
            if (cur != prev)
            {
                changed = true;
                deltas.add(new MonkeyDelta<Boolean>(key, cur));
            }
        }

        if (!changed)
            return null;

        //Get array
        MonkeyDelta<Boolean>[] arr = new MonkeyDelta[] { };
        return deltas.toArray(arr);
    }

    private static MonkeyDelta<Float>[] getJoystickDeltas(Controller current, Controller previous)
    {
        //Get values
        Hashtable<String, Float> joystick_current = current.getAllJoystickValues();
        Hashtable<String, Float> joystick_previous = previous.getAllJoystickValues();

        //Create delta array
        ArrayList<MonkeyDelta<Float>> deltas = new ArrayList<>();

        //Test if anything was changed
        boolean changed = false;

        //Compare joysticks
        for (int i=0; i<joystick_current.size(); i++)
        {
            String key = joystick_current.keys().nextElement();
            Float cur = joystick_current.get(key);
            Float prev = joystick_previous.get(key);
            if (cur != prev)
            {
                changed = true;
                deltas.add(new MonkeyDelta<Float>(key, cur));
            }
        }

        if (!changed)
            return null;

        //Get array
        MonkeyDelta<Float>[] arr = new MonkeyDelta[] { };
        return deltas.toArray(arr);
    }

    static MonkeyData createDeltas(Controller current1, Controller previous1, Controller current2, Controller previous2, long time)
    {
        return new MonkeyData(getButtonDeltas(current1, previous1), getJoystickDeltas(current1, previous1),
                              getButtonDeltas(current2, previous2), getJoystickDeltas(current2, previous2), time);
    }

    //static Controller[] deserializeDeltas(Controller[] controller)

    public static void writeFile(String filename, ArrayList<MonkeyData> commands,Context context)
    {
        try {
            Type listOfTestObject = new TypeToken<List<MonkeyData>>(){}.getType();
            Type buttontoggle = (new TypeToken<ButtonToggle>(){}).getType();
            Type buttonfloat = (new TypeToken<ButtonFloat>(){}).getType();

            File dir = new File(FILE_DIR);
            File file = new File(FILE_DIR, filename);
            Log.d("ftc",file.getAbsolutePath() + "");

            if (!dir.exists()){
                dir.mkdirs();
            }
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            else {
                int i = 0;
                while (file.exists()) {
                    file = new File(FILE_DIR, filename + "." + i);
                    i++;
                }
                file.createNewFile();
            }
            GsonBuilder gb = new GsonBuilder();
            gb.registerTypeAdapter(buttontoggle, new JsonSerializer<ButtonToggle>(){

                @Override
                public JsonElement serialize(ButtonToggle arg0, Type arg1, JsonSerializationContext arg2) {
                    // TODO Auto-generated method stub
                    JsonObject obj = new JsonObject();
                    if (arg0 != null && arg0.state != null){
                        obj.add("state",new JsonPrimitive(arg0.state.getValue()));
                    }
                    else{
                        obj.add("state",new JsonPrimitive(-1));
                    }
                    return obj;
                }

            });
            gb.registerTypeAdapter(buttonfloat, new JsonSerializer<ButtonFloat>(){

                @Override
                public JsonElement serialize(ButtonFloat arg0, Type arg1, JsonSerializationContext arg2) {
                    // TODO Auto-generated method stub
                    JsonObject obj = new JsonObject();
                    if (arg0 != null && arg0.state != null){
                        obj.add("state",new JsonPrimitive(arg0.state.getValue()));
                    }
                    else{
                        obj.add("state",new JsonPrimitive(-1));
                    }
                    obj.add("value",new JsonPrimitive(arg0.value));
                    return obj;
                }

            });
            Gson g = gb.create();
            String out = g.toJson(commands, listOfTestObject);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(out);
            bw.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<MonkeyData> readFile(String filename,Context context) {
        File file = new File(FILE_DIR, filename);
        String out = "";
        ArrayList<MonkeyData> commands = new ArrayList<MonkeyData>();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNextLine()){
                out += s.nextLine();
            }
            Type listOfTestObject = new TypeToken<List<MonkeyData>>(){}.getType();
            commands = new Gson().fromJson(out,listOfTestObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return commands;
    }
}
