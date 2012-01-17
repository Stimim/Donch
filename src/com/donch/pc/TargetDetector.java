package com.donch.pc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.donch.pc.data.DisjointSet;

public class TargetDetector {
  private final ArrayList<DisjointSet> pool = new ArrayList<DisjointSet>();
  private int lastUsable;

  public ArrayList<Point> find(BufferedImage image) {
    lastUsable = 0;

    int width = image.getWidth();
    int height = image.getHeight();

    DisjointSet[] nodes = new DisjointSet[width * height];

    for (int x = 0; x < width; ++ x) {
      for (int y = 0; y < height; ++ y) {
        if (ColorUtils.asGray(image.getRGB(x, y)) == ColorUtils.BLACK) {
          nodes[y * width + x] = null;
        } else {
          if (lastUsable < pool.size()) {
            nodes[y * width + x] = pool.get(lastUsable++);
          } else {
            pool.add(nodes[y * width + x] = new DisjointSet());
            lastUsable++;
          }

          nodes[y * width + x].makeSet(x, y);
        }
      }
    }

    for (int x = 0; x < width; ++ x) {
      for (int y = 0; y < height; ++ y) {
        if (nodes[y * width + x] != null) {
          for (int dx = -1; dx < 1; ++ dx) {
            for (int dy = -1; dy < 1; ++ dy) {
              if (dx != 0 || dy != 0) {
                int _x = dx + x;
                int _y = dy + y;
                if (_x < 0 || _y < 0) {
                  continue;
                }

                if (nodes[_y * width + _x] != null) {
                  nodes[_y * width + _x].union(nodes[y * width + x]);
                }
              }
            }
          }
        }
      }
    }

    ArrayList<Point> targets = new ArrayList<Point>();

    ArrayList<DisjointSet> roots = new ArrayList<DisjointSet>();

    for (int x = 0; x < width; ++ x) {
      for (int y = 0; y < height; ++ y) {
        DisjointSet v = nodes[y * width + x];

        if (v != null && v.find() == v) {
          roots.add(v);
        }
      }
    }

    for (boolean finished = false; !finished; ) {
      finished = true;
      ArrayList<DisjointSet> array = new ArrayList<DisjointSet>(roots.size());

      for (DisjointSet v : roots) {
        boolean need = true;
        for (DisjointSet u : array) {
          if (v.hasOverlap(u)) {
            need = false;
            break;
          }
        }

        if (need) {
          array.add(v);
        } else {
          finished = false;
        }
      }

      roots = array;
    }

    for (DisjointSet v : roots) {
      targets.add(v.center());
    }

    System.out.println(targets.size());

    return targets;
  }

  public static class Point {
    final int x;
    final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public static double distance(Point u, Point v) {
      return Math.sqrt(Math.pow(Math.abs(u.x - v.x), 2) + Math.pow(Math.abs(u.y - v.y), 2));
    }
  }
}
