package com.lasarobotics.library.util;

public class Matrix {
    /**
     * Raw data
     */
    public double[] data;
    //Size
    protected int columns = 0;
    protected int rows = 0;

    public Matrix() {
        data = null;
    }

    public Matrix(int cols, int rows) {
        resize(cols, rows);
    }

    public Matrix(Matrix m) {
        set(m);
    }

    /**
     * Multiply another matrix by a scalar
     *
     * @param m      Matrix to multiply
     * @param scalar Scalar to multiply
     * @return Scalar-Matrix product
     */
    public static Matrix multiply(Matrix m, double scalar) {
        Matrix rv = m.Clone();
        rv.multiply(scalar);
        return rv;
    }

    /**
     * Multiply two matrices
     *
     * @param a First matrix
     * @param b Second matrix
     * @return A * B
     */
    public static Matrix multiply(Matrix a, Matrix b) {
        Matrix rv = new Matrix(b.columns, a.rows);
        int min = a.columns < b.rows ? a.columns : b.rows;
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < b.columns; j++) {
                double s = 0;
                for (int k = 0; k < min; k++) {
                    double av = a.get(k, i);
                    double bv = b.get(j, k);
                    s += av * bv;
                }
                rv.set(j, i, s);
            }
        }
        return rv;
    }

    /**
     * A * B * A^T (transpose of A)
     * Part of the process of matrix diagonalization and has other uses.
     *
     * @param a Matrix A
     * @param b Matrix B
     * @return A * B * A^T (transpose of A)
     */
    public static Matrix multiplyABAT(Matrix a, Matrix b) {
        Matrix rv = multiply(a, b);
        Matrix t = Matrix.transpose(a);
        rv.multiply(t);
        return rv;
    }

    /**
     * Add a scalar to a matrix
     *
     * @param a      Matrix
     * @param scalar Scalar
     * @return A + scalar
     */
    public static Matrix add(Matrix a, double scalar) {
        Matrix rv = new Matrix(a);
        rv.add(scalar);
        return rv;
    }

    /**
     * Add two matrices together (A + B)
     *
     * @param a First matrix
     * @param b Second matrix
     * @return A + B
     */
    public static Matrix add(Matrix a, Matrix b) {
        Matrix rv = new Matrix(a);
        rv.add(b);
        return rv;
    }

    /**
     * Subtract a scalar from a matrix
     *
     * @param a      Matrix
     * @param scalar Scalar
     * @return A - scalar
     */
    public static Matrix subtract(Matrix a, double scalar) {
        Matrix rv = new Matrix(a);
        rv.subtract(scalar);
        return rv;
    }

    /**
     * Subtract two matrices from each other (A - B)
     *
     * @param a First matrix
     * @param b Second matrix
     * @return A - B
     */
    public static Matrix subtract(Matrix a, Matrix b) {
        Matrix rv = new Matrix(a);
        rv.subtract(b);
        return rv;
    }


    /**
     * Get the trace of the index. Same as get(index, index).
     * @param index Index to lookup trace
     * @return Trace of the index
     */
    //TODO this isn't the trace... someone doesn't know linear algebra ;)
    /*public double trace(int index)
    {
        return get(index, index);
    }*/

    /**
     * Transpose matrix m
     *
     * @param m Matrix to transpose
     * @return Transposed matrix m (all rows -> columns, columns -> rows)
     */
    public static Matrix transpose(Matrix m) {
        Matrix rv = new Matrix(m.rows, m.columns);
        for (int i = 0; i < m.columns; i++) {
            for (int j = 0; j < m.rows; j++) {
                rv.set(j, i, m.get(i, j));
            }
        }
        return rv;
    }

    /**
     * Invert a matrix m
     *
     * @param m Matrix to invert
     * @return The inverted matrix
     */
    public static Matrix invert(Matrix m) {
        if (m.columns != m.rows) return null;
        double det = m.getDeterminant();
        if (det == 0) return null;

        Matrix rv = new Matrix(m);
        if (m.columns == 1) rv.data[0] = 1 / rv.data[0];
        det = 1 / det;
        if (m.columns == 2) {
            rv.data[0] = det * m.data[3];
            rv.data[3] = det * m.data[0];
            rv.data[1] = -det * m.data[2];
            rv.data[2] = -det * m.data[1];
        }
        if (m.columns == 3) {
            rv.data[0] = det * (m.data[8] * m.data[4]) - (m.data[7] * m.data[5]);
            rv.data[1] = -det * (m.data[8] * m.data[1]) - (m.data[7] * m.data[2]);
            rv.data[2] = det * (m.data[5] * m.data[1]) - (m.data[4] * m.data[2]);

            rv.data[3] = -det * (m.data[8] * m.data[3]) - (m.data[6] * m.data[5]);
            rv.data[4] = det * (m.data[8] * m.data[0]) - (m.data[6] * m.data[2]);
            rv.data[5] = -det * (m.data[5] * m.data[0]) - (m.data[3] * m.data[2]);

            rv.data[6] = det * (m.data[7] * m.data[3]) - (m.data[6] * m.data[4]);
            rv.data[7] = -det * (m.data[7] * m.data[0]) - (m.data[6] * m.data[2]);
            rv.data[8] = det * (m.data[4] * m.data[0]) - (m.data[3] * m.data[1]);
        }
        return rv;
    }

    /**
     * Set this matrix to the other matrix m.
     *
     * @param m Other matrix
     */
    public void set(Matrix m) {
        resize(m.columns, m.rows);
        System.arraycopy(m.data, 0, data, 0, m.data.length);
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        resize(columns, rows);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int columns) {
        resize(columns, rows);
    }

    /**
     * Resize the matrix
     *
     * @param cols Count of columns
     * @param rows Count of rows
     */
    public void resize(int cols, int rows) {
        if ((columns == cols) && (this.rows == rows)) return;
        columns = cols;
        this.rows = rows;
        data = new double[cols * rows];
        zero();
    }

    /**
     * Clone this matrix to another
     *
     * @return The cloned matrix
     */
    public Matrix Clone() {
        Matrix m = new Matrix();
        m.resize(this.columns, this.rows);
        System.arraycopy(data, 0, m.data, 0, data.length);
        return m;
    }

    /**
     * Get a value from the matrix
     *
     * @param x Column location
     * @param y Row location
     * @return The value
     */
    public double get(int x, int y) {
        return data[x + y * columns];
    }

    /**
     * Set the value at an index to the value of v
     *
     * @param x Column location
     * @param y Row location
     * @param v Value
     */
    public void set(int x, int y, double v) {
        data[x + (y * columns)] = v;
    }

    /**
     * Multiply the matrix by a scalar
     *
     * @param scalar Scalar to multiply the matrix by
     */
    public void multiply(double scalar) {
        for (int i = 0; i < data.length; i++) {
            data[i] *= scalar;
        }
    }

    /**
     * Multiply this matrix by another
     *
     * @param b Another matrix
     */
    public void multiply(Matrix b) {
        Matrix tmp = Matrix.multiply(this, b);
        this.set(tmp);
    }

    /**
     * Add a scalar to this matrix
     *
     * @param scalar The scalar value
     */
    public void add(double scalar) {
        for (int i = 0; i < data.length; i++) {
            data[i] += scalar;
        }
    }

    /**
     * Add A to this matrix
     *
     * @param a Other matrix
     */
    public void add(Matrix a) {
        for (int i = 0; i < data.length; i++) {
            data[i] += a.data[i];
        }
    }

    /**
     * Subtract a scalar from this matrix (returns this = this - scalar)
     *
     * @param scalar The scalar value
     */
    public void subtract(double scalar) {
        for (int i = 0; i < data.length; i++) {
            data[i] -= scalar;
        }
    }

    /**
     * Subtract A from this (returns this = this - A)
     *
     * @param a Other matrix
     */
    public void subtract(Matrix a) {
        for (int i = 0; i < data.length; i++) {
            data[i] -= a.data[i];
        }
    }

    /**
     * Transpose this matrix.
     */
    public void transpose() {
        Matrix rv = new Matrix(this.rows, this.columns);
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                rv.set(j, i, this.get(i, j));
            }
        }
        this.set(rv);
    }

    /**
     * Test if this matrix is the identity matrix
     *
     * @return True if this matrix is an identity, false otherwise
     */
    public boolean isIdentity() {
        if (columns != rows) return false;
        int check = columns + 1;
        int j = 0;
        for (double aData : data) {
            if (j == check) {
                j = 0;
                if (aData != 1) return false;
            } else {
                if (aData != 0) return false;
            }
            j++;
        }
        return true;
    }

    /**
     * Set this matrix as an identity matrix.
     * Will fail if rows != columns.
     */
    public void makeIdentity() {
        if (columns != rows) return;
        int check = columns + 1;
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            data[i] = (j == check) ? 1 : 0;
            j = j == check ? 1 : j + 1;
        }
    }

    /**
     * Zero the matrix
     */
    public void zero() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }

    /**
     * Get the determinant of the matrix
     *
     * @return The determinant of the matrix
     */
    public double getDeterminant() {
        if (columns != rows) return 0;

        if (columns == 0) return 0;
        if (columns == 1) return data[0];
        if (columns == 2) return (data[0] * data[3]) - (data[1] * data[2]);
        if (columns == 3) return
                (data[0] * ((data[8] * data[4]) - (data[7] * data[5]))) -
                        (data[3] * ((data[8] * data[1]) - (data[7] * data[2]))) +
                        (data[6] * ((data[5] * data[1]) - (data[4] * data[2])));

        // only supporting 1x1, 2x2 and 3x3
        return 0;
    }
}
