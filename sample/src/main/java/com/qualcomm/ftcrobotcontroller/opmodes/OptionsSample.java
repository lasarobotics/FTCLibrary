package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.android.Util;
import com.lasarobotics.library.options.Category;
import com.lasarobotics.library.options.OptionMenu;
import com.qualcomm.ftcrobotcontroller.MyApplication;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import android.app.Activity;

/**
 * Created by ehsan on 10/13/15.
 */
public class OptionsSample extends OpMode {
    private OptionMenu menu;
    @Override
    public void init() {
        OptionMenu.Builder builder = new OptionMenu.Builder(hardwareMap.appContext);
        Category alliance = new Category("alliance");
        alliance.addOption("Red");
        alliance.addOption("Blue");
        builder.addCategory(alliance);
        Category time = new Category("time");
        time.addOption("Hello");
        time.addOption("Hi");
        builder.addCategory(time);
        menu = builder.create();
        menu.show();
    }

    @Override
    public void loop() {
        for (Category c: menu.getCategories())
            telemetry.addData(c.name,menu.selectedOption(c.name));
    }
}
