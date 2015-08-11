#include <string.h>
#include <jni.h>
#include "opencv2/opencv.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
using namespace std;
using namespace cv;

extern "C"
{
    JNIEXPORT jstring JNICALL Java_com_lasarobotics_ftc_camera_detection_Features_stringFromJNI(
            JNIEnv *env, jobject type);

    JNIEXPORT jstring JNICALL Java_com_lasarobotics_ftc_camera_detection_Features_stringFromJNI
            (JNIEnv *env, jobject type) {
        return env->NewStringUTF("Hello from JNI");
    }
}