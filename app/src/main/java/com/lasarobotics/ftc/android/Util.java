package com.lasarobotics.ftc.android;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Android utilities
 */
public final class Util {
    public static Context getContext()
    {
        try {
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            return (Application) method.invoke(null, (Object[]) null);
        } catch (final ClassNotFoundException e) {
            // handle exception
            return null;
        } catch (final NoSuchMethodException e) {
            // handle exception
            return null;
        } catch (final IllegalArgumentException e) {
            // handle exception
            return null;
        } catch (final IllegalAccessException e) {
            // handle exception
            return null;
        } catch (final InvocationTargetException e) {
            // handle exception
            return null;
        }
    }

    public static String getDataDirectory(Context ctx)
    {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).applicationInfo.dataDir;
        }
        catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getWorkingDirectory()
    {
        return System.getProperty("user.dir");
    }

    public static String getDCIMDirectory()
    {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }
}
