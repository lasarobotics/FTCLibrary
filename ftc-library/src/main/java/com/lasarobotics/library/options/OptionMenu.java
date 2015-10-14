package com.lasarobotics.library.options;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ehsan on 10/13/15.
 */
public class OptionMenu{
    private ArrayList<Category> categories;
    private Context context;
    private final int ID_OFFSET = 102223;
    private final HashMap<String,Integer> selectedIndices = new HashMap<>();
    private final HashMap<String,String> selectedOptions = new HashMap<>();
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
            if (c.options.size() > 0)
                categories.add(c);
            else
                throw new IllegalArgumentException("Category needs at least one option");
        }
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public String selectedOption(String categoryID){
        if (selectedOptions.containsKey(categoryID)){
            return selectedOptions.get(categoryID);
        }
        else{
            throw new IllegalArgumentException("Category does not exist");
        }
    }
    public int selectedOptionIndex(String categoryID){
        if (selectedIndices.containsKey(categoryID)){
            return selectedIndices.get(categoryID);
        }
        else{
            throw new IllegalArgumentException("Category does not exist");
        }
    }
    public void show(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Setup");
        final View v = generateView();
        builder.setView(v);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.d("setup", "Setup finished");
                for (int i = 0; i < categories.size(); i++){
                    Category c = categories.get(i);
                    Spinner s = (Spinner)v.findViewById(ID_OFFSET+i);
                    int selectedIndex = s.getSelectedItemPosition();
                    Log.d("setup", "category: " + c.name + " selected " + c.options.get(selectedIndex));
                    selectedIndices.put(c.name, selectedIndex);
                    selectedOptions.put(c.name,c.options.get(selectedIndex));
                }
            }
        });
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                builder.show();
            }
        });
    }

    private View generateView() {
        final LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                for(int i = 0; i < categories.size(); i++) {
                    Category c = categories.get(i);
                    LinearLayout row = new LinearLayout(context);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    TextView t = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null);
                    t.setText(c.name);
                    row.addView(t);

                    Spinner spinner = new Spinner(context);
                    spinner.setId(ID_OFFSET + i);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_dropdown_item, c.options);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    row.addView(spinner);
                    l.addView(row);
                }
            }
        });
        return l;
    }
}
