package com.donch.pc;

public class Range {
  public final int left;
  public final int right;
  public final int top;
  public final int bottom;

  public Range(int left, int right, int top, int bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  public boolean inRange(int x, int y) {
    return xInRange(x) && yInRange(y);
  }

  public boolean xInRange(int x) {
    return x < right && x >= left;
  }

  public boolean yInRange(int y) {
    return y < bottom && y >= top;
  }
}
