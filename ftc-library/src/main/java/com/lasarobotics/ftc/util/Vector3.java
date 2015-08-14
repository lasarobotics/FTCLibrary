package com.lasarobotics.ftc.util;

/**
 * 3D Vector
 */
public class Vector3<T> {
    private T x;
    private T y;
    private T z;

    public Vector3(T x, T y, T z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public T x() { return x; }
    public T y() { return y; }
    public T z() { return z; }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
