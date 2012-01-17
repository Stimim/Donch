package com.donch.pc.utils;

public class MatrixUtils {
  public static double[][] mult(double[][] a, double[][] b) {
    if (a[0].length != b.length) {
      return null;
    }

    double[][] c = new double[a.length][b[0].length];

    for (int i = 0; i < c.length; ++ i) {
      for (int j = 0; j < c[i].length; ++ j) {
        c[i][j] = 0;
        for (int k = 0; k < b.length; ++ k) {
          c[i][j] += a[i][k] * b[k][j];
        }
      }
    }

    return c;
  }

  public static double[][] mult(double[][] a, double b) {
    double[][] c = new double[a.length][a[0].length];

    for (int i = 0; i < a.length; ++ i) {
      for (int j = 0; j < a[i].length; ++ j) {
        c[i][j] = a[i][j] * b;
      }
    }

    return c;
  }

  public static double[][] div(double[][] a, double b) {
    double[][] c = new double[a.length][a[0].length];

    for (int i = 0; i < a.length; ++ i) {
      for (int j = 0; j < a[i].length; ++ j) {
        c[i][j] = a[i][j] / b;
      }
    }

    return c;
  }

  public static double[][] add(double[][] a, double[][] b) {
    if (a.length != b.length || a[0].length != b[0].length) {
      return null;
    }

    double[][] c = new double[a.length][b[0].length];

    for (int i = 0; i < c.length; ++ i) {
      for (int j = 0; j < c[i].length; ++ i) {
        c[i][j] = a[i][j] + b[i][j];
      }
    }

    return c;
  }

  public static double[][] sub(double[][] a, double[][] b) {
    if (a.length != b.length || a[0].length != b[0].length) {
      return null;
    }

    double[][] c = new double[a.length][b[0].length];

    for (int i = 0; i < c.length; ++ i) {
      for (int j = 0; j < c[i].length; ++ i) {
        c[i][j] = a[i][j] - b[i][j];
      }
    }

    return c;
  }

  public static double[][] transpose(double[][] a) {
    double[][] t = new double[a[0].length][a.length];

    for (int i = 0; i < a.length; ++ i) {
      for (int j = 0; j < a[i].length; ++ i) {
        t[j][i] = a[i][j];
      }
    }

    return t;
  }

  public static double[][] toMatrix(double x) {
    return new double[][] {{x}};
  }
}
