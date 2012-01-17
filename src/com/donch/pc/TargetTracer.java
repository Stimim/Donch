package com.donch.pc;

import java.util.List;

import com.donch.pc.TargetDetector.Point;

public class TargetTracer {
//  private final SimpleController controller;
  private Point point;

  public TargetTracer() {
//    controller = new SimpleController("MrOrz", "0016530990D2");
    this.point = null;
  }

  public void setPoint(Point point) {
    this.point = point;
  }

  public Point getPoint() {
    return point;
  }

  public Point update(List<Point> points) {
    if (point == null) {
      point = new Point(160, 120);
    }

    Point closest = null;
    double distance = 0;

    for (Point p : points) {
      double d = Point.distance(p, point);

      if (d > 50) {
        continue;
      }

      if (closest == null) {
        closest = p;
        distance = Point.distance(p, point);
      } else {
        if (d < distance) {
          distance = d;
          closest = p;
        }
      }
    }

    if (closest != null) {
      return point = new Point(closest.x, closest.y);
    } else {
      return point;
    }
  }
}
