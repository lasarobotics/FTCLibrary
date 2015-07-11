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
import com.lasarobotics.ftc.controller.ButtonState;
import com.lasarobotics.ftc.controller.ButtonToggle;
import com.lasarobotics.ftc.controller.Controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * MonkeyUtil handles reading and writing text files with instructions created by MonkeyC
 */
public class MonkeyUtil {
    public final static String FILE_DIR = Environment.getExternalStorageDirectory() + "/MonkeyC/";
    //TODO static File[] getList();
    //TODO static File[] getList(Directory dir);

    //TODO static byte[] getData();

    private static JSONObject getDeltas(Controller current, Controller previous) throws JSONException {
        Gson g = getGson();
        JSONObject currentjson = new JSONObject(g.toJson(current));
        JSONObject previousjson = new JSONObject(g.toJson(previous));
        JSONObject out = new JSONObject();
        //Test if anything was changed
        boolean changed = false;
        Iterator<?> keys = previousjson.keys();
        while (keys.hasNext()) {

            String key = (String) keys.next();
            String cur = currentjson.get(key).toString();
            String prev = previousjson.get(key).toString();
            if (!cur.equals(prev)) {
                changed = true;
                try {
                    out.put(key, new JSONObject(cur));
                } catch (Exception e) {
                    out.put(key, cur);
                }
            }
        }

        if (!changed)
            return null;
        return out;
    }

    public static MonkeyData createDeltas(Controller current1, Controller previous1, Controller current2, Controller previous2, long time) {
        try {
            return new MonkeyData(getDeltas(current1, previous1), getDeltas(current2, previous2), time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //static Controller[] deserializeDeltas(Controller[] controller)

    public static void writeFile(String filename, ArrayList<MonkeyData> commands, Context context) {
        try {
            Type listOfTestObject = new TypeToken<List<MonkeyData>>() {
            }.getType();

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

    private static Gson getGson() {
        Type buttontoggle = (new TypeToken<ButtonToggle>() {
        }).getType();
        Type buttonfloat = (new TypeToken<ButtonFloat>() {
        }).getType();
        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(buttontoggle, new JsonSerializer<ButtonToggle>() {

            @Override
            public JsonElement serialize(ButtonToggle arg0, Type arg1, JsonSerializationContext arg2) {
                // TODO Compress this to key-value pairs ONLY
                JsonObject obj = new JsonObject();
                if (arg0 != null && arg0.state != null) {
                    obj.add("state", new JsonPrimitive(arg0.state.getValue()));
                } else {
                    obj.add("state", new JsonPrimitive(-1));
                }
                return obj;
            }

        });
        gb.registerTypeAdapter(buttonfloat, new JsonSerializer<ButtonFloat>() {

            @Override
            public JsonElement serialize(ButtonFloat arg0, Type arg1, JsonSerializationContext arg2) {
                // TODO Compress this to key-value pairs ONLY
                JsonObject obj = new JsonObject();
                if (arg0 != null && arg0.state != null) {
                    obj.add("state", new JsonPrimitive(arg0.state.getValue()));
                } else {
                    obj.add("state", new JsonPrimitive(-1));
                }
                obj.add("value", new JsonPrimitive(arg0.value));
                return obj;
            }

        });
        return gb.create();
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
}
