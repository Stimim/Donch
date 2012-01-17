package com.donch.pc;

import java.io.IOException;
import java.util.List;

import com.donch.pc.TargetDetector.Point;

public class TargetTracer {
  private final SimpleController controller;
  private Point point;

  private static final Point FRONT_SIGHT = new Point(160, 180);

  public TargetTracer() {
    controller = new SimpleController("MrOrz", "0016530990D2");
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
      point = closest;
      if (Point.distance(point, FRONT_SIGHT) > 10) {
        int dx = point.x - FRONT_SIGHT.x;
        int dy = point.y - FRONT_SIGHT.y;

        try {
          if (Math.abs(dx) > 10) {
//            System.out.println("dx :" + dx);
             controller.sendCommand(SimpleController.TURN, -dx / 20);
          }
          if (Math.abs(dy) > 10) {
//            System.out.println("dy :" + dy);
            controller.sendCommand(SimpleController.RAISE, dy / 20);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return point;
    } else {
      return point;
    }
  }
}
