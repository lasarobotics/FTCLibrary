package com.lasarobotics.library.nav;

import com.lasarobotics.library.util.Matrix;

/**
 * Kalman
 * <p/>
 * predict:
 * X = F*X + H*U
 * P = F*P*F^T + Q.
 * <p/>
 * Update:
 * Y = M – H*X          Called the innovation = measurement – state transformed by H.
 * S = H*P*H^T + R      S= Residual covariance = covariane transformed by H + R
 * K = P * H^T *S^-1    K = Kalman gain = variance / residual covariance.
 * X = X + K*Y          Update with gain the new measurement
 * P = (I – K * H) * P  Update covariance to this time.
 * <p/>
 * Note:
 * Derived classes need to perhaps hide certain matrixes to simplify the
 * interface. Also perhaps override: setupFMatrix, predict, Reset or Update.
 */
public class KalmanFilter {
    /**
     * State matrix (X)
     */
    protected Matrix m_x = new Matrix();
    /**
     * Covariance matrix (P)
     */
    protected Matrix m_p = new Matrix();
    /*
     * Minimal covariance matrix (Q)
     */
    protected Matrix m_q = new Matrix();
    /**
     * Minimal innovative covariance, keeps filter from locking in to a solution (R)
     */
    protected Matrix m_r = new Matrix();
    /**
     * Converts m_x forward to a new time interval.
     * Depending on what the states mean in m_x this could be anything.
     * Often for 2D it is: 1, dt
     * 0,  1
     * Because that would convert a 2 dimentional motion, velocity vector
     * forward by time dt.
     * <p/>
     * For exampe, if acceleration was used instead of velocity
     * we could instead have dt*dt instead of dt and thus have a 2nd order
     * Kalman that estimated position with smooth accelerations.
     */
    protected Matrix m_f = new Matrix();
    /**
     * Converts measurement vector into state vector space.
     */
    protected Matrix m_h = new Matrix();
    /**
     * Apriori per update information. For example if we knew something
     * moved at a specific velocity always we could just use U to add
     * that in and decouple that from the other statistics addressed
     * by the Kalman filter.
     */
    protected Matrix m_u = new Matrix();
    private double lastGain;

    /**
     * Last gain determinant, useful for debug
     * @return Last gain determinant
     */
    public double getLastGain() {
        return lastGain;
    }

    /**
     * Get the state value at an index
     */
    public double value(int index) {
        return m_x.data[index];
    }

    /**
     * Last updated value[0] variance.
     *
     * @return Variance at the first data point
     */
    public double variance() {
        return m_p.data[0];
    }

    /**
     * Last updated variance at a specificindex
     *
     * @param index Index ro aearch for variance
     * @return The variance at a specific index
     */
    public double variance(int index) {
        return m_p.get(index, index);
    }

    /**
     * Setup matrix F based on dt, the time between last update and this update.
     * Default is for a rectangular time based:
     * 1, dt, dt^2, dt^3, ...
     * 0,  1, dt,   dt^2, ...
     * 0,  0, 1,    dt, ...
     * 0,  0, 0,     1, ...
     * ...
     *
     * @param dt Time delta between samples
     */
    public void setupFMatrix(double dt) {
        m_f.zero();

        for (int i = 0; i < m_f.getRows(); i++) {
            double m = 1;
            m_f.set(i, i, m);
            for (int j = i + 1; j < m_f.getColumns(); j++) {
                m *= dt;
                m_f.set(i, i, m);
            }
        }
    }

    /**
     * Predict the most significant value forward from
     * last measurement time by dt.
     * X = F*X + H*U
     *
     * @param dt Time delta between measurements
     * @return The predicted value at a time delta
     */
    public double predict(double dt) {
        setupFMatrix(dt);
        Matrix tmp = Matrix.multiply(m_f, m_x);
        Matrix tmp2 = Matrix.multiply(m_h, m_u);
        tmp.add(tmp2);
        return tmp.data[0];
    }

    /**
     * Get the estimated covariance of position predicted
     * forward from last measurement time by dt.
     * P = F*P*F^T + Q.
     *
     * @param dt Time delta between measurements
     * @return Approximate covariance at position predicted
     */
    public double variance(double dt) {
        setupFMatrix(dt);
        Matrix tmp = Matrix.multiplyABAT(m_f, m_p);
        tmp.add(m_q);
        return tmp.data[0];
    }

    public void reset(Matrix q, Matrix r, Matrix p, Matrix x) {
        m_q = q;
        m_r = r;
        m_p = p;
        m_x = x;
    }

    /**
     * Update the state by measurement m at dt time from last measurement
     *
     * @param measurement Previous measurement data
     * @param dt          Time delta since last measuremment
     * @return Latest estimate
     */
    public double update(Matrix measurement, double dt) {
        // Predict to now, then update.
        // Predict:
        //   X = F*X + H*U
        //   P = F*P*F^T + Q.
        // Update:
        //   Y = M – H*X          Called the innovation = measurement – state transformed by H.
        //   S = H*P*H^T + R      S= Residual covariance = covariane transformed by H + R
        //   K = P * H^T *S^-1    K = Kalman gain = variance / residual covariance.
        //   X = X + K*Y          Update with gain the new measurement
        //   P = (I – K * H) * P  Update covariance to this time.
        //
        // Same as 1D but mv is used instead of delta m_x[0], and H = [1,1].

        // X = F*X + H*U
        setupFMatrix(dt);
        Matrix t1 = Matrix.multiply(m_f, m_x);
        Matrix t2 = Matrix.multiply(m_h, m_u);
        t1.add(t2);
        m_x.set(t1);

        // P = F*P*F^T + Q
        m_p = Matrix.multiplyABAT(m_f, m_p);
        m_p.add(m_q);

        // Y = M – H*X
        t1 = Matrix.multiply(m_h, m_x);
        Matrix y = Matrix.subtract(measurement, t1);

        // S = H*P*H^T + R
        Matrix s = Matrix.multiplyABAT(m_h, m_p);
        s.add(m_r);

        // K = P * H^T *S^-1
        Matrix ht = Matrix.transpose(m_h);
        Matrix tmp = Matrix.multiply(m_p, ht);
        Matrix sinv = Matrix.invert(s);
        Matrix k = new Matrix(y.getRows(), m_x.getRows());
        if (sinv != null) {
            k = Matrix.multiply(tmp, sinv);
        }

        lastGain = k.getDeterminant();

        // X = X + K*Y
        m_x.add(Matrix.multiply(k, y));

        // P = (I – K * H) * P
        Matrix kh = Matrix.multiply(k, m_h);
        Matrix id = new Matrix(kh.getColumns(), kh.getRows());
        id.makeIdentity();
        id.subtract(kh);
        id.multiply(m_p);
        m_p.set(id);

        // Return latest estimate.
        return m_x.data[0];
    }
}