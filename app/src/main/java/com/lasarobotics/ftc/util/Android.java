package com.lasarobotics.ftc.util;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Android utilities
 */
public final class Android {
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
}
