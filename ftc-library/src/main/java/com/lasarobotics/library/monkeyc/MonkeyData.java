package com.lasarobotics.library.monkeyc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import com.lasarobotics.library.controller.Controller;
import com.lasarobotics.library.util.Constants;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Contains a single time-stamped patched state of one Controller
 */
public class MonkeyData {
    //TODO can improve this to combine everything into one data set


    @SerializedName("g1")
    private JsonObject deltasGamepad1;
    @SerializedName("g2")
    private JsonObject deltasGamepad2;
    @SerializedName("t")
    private long time;

    MonkeyData() {
        deltasGamepad1 = null;
        deltasGamepad2 = null;
        time = -1;
    }

    MonkeyData(JsonObject deltasGamepad1, JsonObject deltasGamepad2, long time) {
        this.deltasGamepad1 = deltasGamepad1;
        this.deltasGamepad2 = deltasGamepad2;
        this.time = time;
    }


    public Controller updateControllerOne(Controller previous) {
        if (deltasGamepad1 != null) {
            Gson g = new Gson();
            try {
                JSONObject previousjson = new JSONObject(g.toJson(previous));
                JSONObject patch = new JSONObject(deltasGamepad1.toString());
                Iterator<?> keys = patch.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    previousjson.remove(key);
                    previousjson.put(key, patch.get(key));
                }
                return g.fromJson(previousjson.toString(), Controller.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return previous;
        } else {
            return previous;
        }
    }

    public Controller updateControllerTwo(Controller previous) {
        if (deltasGamepad2 != null) {
            Gson g = new Gson();
            try {
                JSONObject previousjson = new JSONObject(g.toJson(previous));
                JSONObject patch = new JSONObject(deltasGamepad2.toString());
                Iterator<?> keys = patch.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    previousjson.remove(key);
                    previousjson.put(key, patch.get(key));
                }
                return g.fromJson(previousjson.toString(), Controller.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return previous;
        } else {
            return previous;
        }
    }

    public boolean hasUpdate() {
        if (deltasGamepad1 != null || deltasGamepad2 != null) {
            return true;
        }
        return time == Constants.MONKEYC_STARTING_CONSTANT;
    }

    public JsonObject getDeltasGamepad1() {
        return deltasGamepad1;
    }

    public void setDeltasGamepad1(JsonObject deltasGamepad1) {
        this.deltasGamepad1 = deltasGamepad1;
    }

    public JsonObject getDeltasGamepad2() {
        return deltasGamepad2;
    }

    public void setDeltasGamepad2(JsonObject deltasGamepad2) {
        this.deltasGamepad2 = deltasGamepad2;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }
}
