package com.donch.pc.data;

import com.donch.pc.TargetDetector.Point;

public class DisjointSet {
  private DisjointSet parent;
  private int height = 0;
  private int left;
  private int right;
  private int top;
  private int bottom;
  private int size;

  public Point center() {
    DisjointSet v = find();
    return new Point((v.left + v.right) / 2, (v.top + v.bottom) / 2);
  }

  public void union(DisjointSet x) {
    if (x.parent == this.parent) {
      return;
    }

    DisjointSet a = x.find();
    DisjointSet b = this.find();

    DisjointSet p;

    if (a.height < b.height) {
      p = a.parent = b;
    } else if (a.height > b.height) {
      p = b.parent = a;
    } else {
      p = a.parent = b;
      b.height ++;
    }

    p.left = Math.min(a.left, b.left);
    p.right = Math.max(a.right, b.right);
    p.bottom = Math.max(a.bottom, b.bottom);
    p.top = Math.min(a.top, b.top);
    p.size = a.size + b.size;
  }

  public DisjointSet find() {
    if (parent == this) {
      return this;
    }

    parent = parent.find();
    return parent;
  }

  public void makeSet(int x, int y) {
    parent = this;
    height = 0;
    left = right = x;
    top = bottom = y;
    size = 1;
  }

  public boolean hasOverlap(DisjointSet x) {
    DisjointSet a = x.find();
    DisjointSet b = find();
    if (Math.signum(a.left - b.left) * Math.signum(a.right - b.right) < 0
        && Math.signum(a.top - b.top) * Math.signum(a.bottom - b.bottom) < 0) {
      return true;
    }
    return false;
  }

  public int getSize() {
    return size;
  }
}
