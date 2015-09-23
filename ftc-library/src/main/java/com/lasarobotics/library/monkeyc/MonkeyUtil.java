package com.lasarobotics.library.monkeyc;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import com.lasarobotics.library.controller.Controller;


import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * MonkeyUtil handles reading and writing text files with instructions created by MonkeyC
 */
public class MonkeyUtil {
    public final static String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MonkeyC/";


    private static JsonObject getDeltas(Controller current, Controller previous) throws JSONException {
        Gson g = getGson();
        JSONObject currentjson = new JSONObject(g.toJson(current));
        JSONObject previousjson = new JSONObject(g.toJson(previous));
        JsonObject out = new JsonObject();
        //Test if anything was changed
        boolean changed = false;
        Iterator<?> keys = previousjson.keys();
        while (keys.hasNext()) {

            String key = (String) keys.next();
            double cur = currentjson.getDouble(key);
            double prev = previousjson.getDouble(key);
            if (!(cur == prev)) {
                changed = true;
                out.addProperty(key, cur);
            }
        }

        if (!changed)
            return null;
        return out;
    }

    public static MonkeyData createDeltas(Controller current1, Controller previous1, Controller current2, Controller previous2, long time) {
        //Get controller deltas
        JsonObject one = null;
        JsonObject two = null;

        try {
            one = getDeltas(current1, previous1);
            two = getDeltas(current2, previous2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new MonkeyData(one, two, time);
    }


    public static void writeFile(String filename, ArrayList<MonkeyData> commands, Context context) {
        try {
            Type listOfTestObject = new TypeToken<List<MonkeyData>>() {
            }.getType();

            if (new File(FILE_DIR, filename).exists()) //if we already have a file named filename
            {
                new File(FILE_DIR, filename).delete();//we should delete it
            }
            File dir = new File(FILE_DIR);

            File file = new File(FILE_DIR, filename);
            Log.d("ftc", file.getAbsolutePath() + "");

            if (!dir.exists()) {
                dir.mkdirs();
            }
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            } else {
                int i = 0;
                while (file.exists()) {
                    file = new File(FILE_DIR, filename + "." + i);
                    i++;
                }
                file.createNewFile();
            }
            Gson g = getGson();
            String out = g.toJson(commands, listOfTestObject);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(out);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<MonkeyData> readFile(String filename, Context context) {
        File file = new File(FILE_DIR, filename);
        String out = "";
        ArrayList<MonkeyData> commands = new ArrayList<MonkeyData>();
        try {
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                out += s.nextLine();
            }
            Type listOfTestObject = new TypeToken<List<MonkeyData>>() {
            }.getType();
            commands = new Gson().fromJson(out, listOfTestObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return commands;
    }

    private static Gson getGson() {
        GsonBuilder gb = new GsonBuilder();
        return gb.create();
    }

    static void callMonkeyMethod(String m) {
        String methodName = m.substring(m.lastIndexOf('.') + 1);
        String className = m.substring(0, m.lastIndexOf('.'));

        //GET CLASS
        Class<?> clas;
        try {
            clas = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        //GET METHOD WITHIN CLASS
        Method method;
        try {
            method = clas.getMethod(methodName);//, param1.class, param2.class, ..);
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
            return;
        }

        //RUN METHOD
        try {
            Object obj = clas.newInstance();
            method.invoke(obj);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
