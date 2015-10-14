package com.lasarobotics.library.options;


import com.lasarobotics.library.util.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ehsan on 10/13/15.
 */
public class OptionMenu{
    private ArrayList<Category> categories;
    private Context context;

    public OptionMenu(Context c,ArrayList<Category> categories) {
        context = c;
        this.categories = categories;
    }

    public static class Builder{
        ArrayList<Category> categories;
        Context context;
        public Builder(Context c){
            context = c;
            categories = new ArrayList<Category>();
        }
        public OptionMenu create (){
            return new OptionMenu(context,categories);
        }
        public void addCategory(Category c){
            categories.add(c);
        }
    }
    public void show(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Setup");
        View v = generateView();
        builder.setView(v);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d("setup", "Setup finished");
            }
        });
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                builder.show();
            }
        });
    }

    private View generateView() {
        LinearLayout l = new LinearLayout(context);
        for (Category c : categories){
            TextView t = new TextView(context);
            t.setText(c.name);
            l.addView(t);
        }
        return l;
    }
}
