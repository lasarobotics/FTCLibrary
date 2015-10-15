package com.qualcomm.ftcrobotcontroller.opmodes;

import com.lasarobotics.library.options.Category;
import com.lasarobotics.library.options.NumberCategory;
import com.lasarobotics.library.options.OptionMenu;
import com.lasarobotics.library.options.SingleSelectCategory;
import com.lasarobotics.library.options.TextCategory;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

/**
 * Options menu sample OPMode implementation
 */
public class OptionsSample extends OpMode {
    private OptionMenu menu;

    @Override
    public void init() {
        OptionMenu.Builder builder = new OptionMenu.Builder(hardwareMap.appContext);
        //Setup a SingleSelectCategory
        SingleSelectCategory alliance = new SingleSelectCategory("alliance");
        alliance.addOption("Red");
        alliance.addOption("Blue");
        builder.addCategory(alliance);
        //Setup a TextCategory
        TextCategory robotName = new TextCategory("robot name");
        builder.addCategory(robotName);
        //Setup a NumberCategory
        NumberCategory time = new NumberCategory("time");
        builder.addCategory(time);
        //Create menu
        menu = builder.create();
        //Display menu
        menu.show();
    }

    @Override
    public void loop() {
        //Loops through each category, getting the input from menu
        for (Category c : menu.getCategories())
            telemetry.addData(c.getName(), menu.selectedOption(c.getName()));
    }
}
