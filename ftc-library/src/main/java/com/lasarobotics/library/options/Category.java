package com.lasarobotics.library.options;

import java.util.ArrayList;

/**
 * Holds a set of options for a menu type
 */
public class Category {
    public String name;
    public ArrayList<String> options;
    public Category(String name){
        this.name = name;
        options = new ArrayList<String>();
    }
    public void addOption(String item){
        options.add(item);
    }
}
