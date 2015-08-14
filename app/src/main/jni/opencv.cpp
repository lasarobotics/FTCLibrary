//
// OpenCV-Java overrides
//

#include <string.h>
#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
using namespace std;
using namespace cv;

extern "C"
{
    JNIEXPORT void JNICALL Java_com_lasarobotics_ftc_camera_detection_ImageLoader_imread(JNIEnv* jobject, jlong matPtr, jstring filename, jint flags)
    {
        //convert the jstring to a native string first
        const char *nativeString = (*env)->GetStringUTFChars(env, javaString, 0);
        Mat img = imread(nativeString, flags);

        //we create the Mat in Java then copy the memory over here
        Mat* mat = (Mat*) matPtr;
        mat->create(rows, cols, type);
        memcpy(mat->data, img.data, mat->step * mat->rows);
    }
} // extern "C"