package com.donch.pc;

import lejos.util.Matrix;


public class KalmanFilter {

  private final Matrix A = new Matrix(new double[][] {
      {1, 1, 0.5},
      {0, 1, 1},
      {0, 0, 1},
  });

  private Matrix P = new Matrix(new double[][] {
      {0, 0, 0},
      {0, 1000, 0},
      {0, 0, 1000},
  });

  private final Matrix G = new Matrix(new double[][] {
      {0.5},
      {1},
      {1},
  });

  private final Matrix H = new Matrix(new double[][] {
      {1, 0, 0},
  });

  private final Matrix Q = new Matrix(1, 1, 100);

  private final Matrix R = new Matrix(1, 1, 100);

  private Matrix x = new Matrix(new double[][] {
      {0},
      {0},
      {0},
  });

  private final Matrix I = Matrix.identity(3, 3);

  public double update(double p) {
//    % estimate
//    x(:, t) = A * x(:, t - 1);
    x = A.times(x);

    double z = H.times(x).get(0, 0); // our estimation

//    % Prediction of the plant covariance
//    P = A * P * A' + G * Q * G';
    P = A.times(P).times(A.transpose()).plus(G.times(Q).times(G.transpose()));

    if (p >= 0) {
//    % Innovation Covariance
//    S = H * P * H' + R;
    Matrix S = H.times(P).times(H.transpose()).plus(R);
//    % Kalman's gain
//    K = P * H' / S;
    Matrix K = P.times(H.transpose()).times(1 / S.get(0, 0));

  //    % State check up and update
  //    x(:,t) = x(:,t) + K * (X(t - 1)-z(t));
      x.plusEquals(K.times(p - z));
  //    % Covariance check up and update
  //    P = (eye(3) - K * H) * P;
      P.arrayTimesEquals(I.minus(K.times(H)));
    }

    return z;
  }
}
