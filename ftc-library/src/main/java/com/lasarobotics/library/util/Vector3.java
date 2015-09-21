package com.lasarobotics.library.util;

/**
 * 3D Vector
 */
public class Vector3<T> {
    public final T x;
    public final T y;
    public final T z;
    
    public static class Builder<T> {
        
        public Builder x(T n) {
            this.x = n;
            return this;
        }
        
        public Builder y(T n) {
            this.y = n;
            return this;
        }
        
        public Builder z(T n) {
            this.z = n;
            return this;
        }
        
        public Vector3<T> build() { 
            return new Vector3<T>(x, y, x);
        }
    }

    public Vector3(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
