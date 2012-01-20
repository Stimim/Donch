package com.donch.pc;

import lejos.util.Matrix;

import com.donch.pc.TargetDetector.Point;


public class KalmanFilter {

  private final Matrix A = new Matrix(new double[][] {
      {1, 0, 1, 0},
      {0, 1, 0, 1},
      {0, 0, 1, 0},
      {0, 0, 0, 1},
  });

  private static final Matrix INIT_P = new Matrix(new double[][] {
      {100, 0, 0, 0},
      {0, 100, 0, 0},
      {0, 0, 100, 0},
      {0, 0, 0, 100},
  });

  private Matrix P = INIT_P;

  private final Matrix H = new Matrix(new double[][] {
      {1, 0, 0, 0},
      {0, 1, 0, 0},
  });

  private final Matrix Q = new Matrix(new double[][] {
      {0.01, 0, 0, 0},
      {0, 0.01, 0, 0},
      {0, 0, 0.01, 0},
      {0, 0, 0, 0.01},
  });

  private final Matrix R = new Matrix(new double[][] {
      {0.2845, 0.0045},
      {0.0045, 0.0455},
  });

  private final Matrix Bu = new Matrix(new double[][] {
      {0},
      {0},
      {0},
      {0},
  });

  private Matrix x = new Matrix(new double[][] {
      {0},
      {0},
  });

  private int counter = 0;
  boolean initialized = false;

  private final Matrix I = Matrix.identity(4, 4);

  private static final int MAX_NOT_FOUND_INTERVAL = 60;

  public Point predict(int t) {
    if (!initialized) {
      return null;
    }
    Matrix xp = x;
    for (int i = 0; i < t; ++ i) {
      xp = A.times(xp).plus(Bu);
    }

    return new Point((int) xp.get(0, 0), (int) xp.get(1, 0));
  }

  public Point update(Point p) {
    if (p == null) {
      if (!initialized) {
        return null;
      } else if (++counter > MAX_NOT_FOUND_INTERVAL) {
        counter = 0;
        initialized = false;
      }
    }

    Matrix xp;
    if (!initialized) {
      xp = new Matrix(new double[][] {
          {320},
          {240},
          {0},
          {0},
      });
    } else {
      xp = A.times(x).plus(Bu);
    }

    initialized = true;

    Matrix PP = A.times(P).times(A.transpose()).plus(Q);
    Matrix K = PP.times(H.transpose()).times(H.times(PP).times(H.transpose()).plus(R).inverse());

    if (p != null) {
      Matrix m = new Matrix(new double[][] {
          {p.x},
          {p.y},
      });
      x = xp.plus(K.times(m.minus(H.times(xp))));
    } else {
      x = xp;
    }

    P = (I.minus(K.times(H))).times(PP);

    return new Point((int) x.get(0, 0), (int) x.get(1, 0));
  }
}
