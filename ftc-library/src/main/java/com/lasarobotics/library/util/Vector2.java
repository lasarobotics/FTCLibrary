package com.lasarobotics.library.util;

/**
 * 3D Vector : Immutable
 */
public class Vector2<T extends Number> {
    public final T x;
    public final T y;

    public Vector2(T x, T y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static class Builder<T extends Number> {
        private T x, y;

        public Builder<T> x(T n) {
            this.x = n;
            return this;
        }

        public Builder<T> y(T n) {
            this.y = n;
            return this;
        }

        // Illegal State Exception errors can be thrown if x or y is null
        public Vector2<T> build() {
            return new Vector2<T>(x, y);
        }
    }
}
