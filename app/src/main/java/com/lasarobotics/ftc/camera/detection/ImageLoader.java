package com.lasarobotics.ftc.camera.detection;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.utils.Converters;

public class ImageLoader {

    public static final int
            LOAD_UNCHANGED = -1,
            LOAD_GRAYSCALE = 0,
            LOAD_COLOR = 1;

    public static Mat loadImage(String filename, int flags)
    {
        Mat m = new Mat();
        Imgcodecs.imread(filename, flags);
        return m;
    }

    public static Mat loadImage(String filename)
    {
        return loadImage(filename, LOAD_COLOR);
    }
}
