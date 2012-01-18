package com.donch.pc;

import java.io.IOException;
import java.util.List;

import com.donch.pc.TargetDetector.Point;

public class TargetTracer {
  private final PoseController poseController;
  private final ShootingController shootingController;

  private Point target;

  private double lastTacho = 0;

  private Point frontSight = new Point(320, 380);

  public TargetTracer(PoseController poseController, ShootingController shootingController) {
    this.poseController = poseController;
    this.shootingController = shootingController;
    this.target = null;
  }

  public void setTarget(Point target) {
    this.target = target;
  }

  public Point getTarget() {
    return target;
  }

  public void setFrontSight(Point point) {
    System.out.println(point.x + ", " + point.y);
    this.frontSight = point;
  }

  public Point update(List<Point> targets) {
    if (target == null) {
      target = new Point(160, 120);
    }

    Point closest = null;
    double distance = 0;

    for (Point p : targets) {
      double d = Point.distance(p, target);

      if (d > 50) {
        continue;
      }

      if (closest == null) {
        closest = p;
        distance = Point.distance(p, target);
      } else {
        if (d < distance) {
          distance = d;
          closest = p;
        }
      }
    }

    if (closest != null) {
      target = closest;
        int dx = target.x - frontSight.x;
        int dy = target.y - frontSight.y;

        try {
          boolean canShoot = true;
          if (Math.abs(dx) > 10) {
            canShoot = false;
//            System.out.println("dx :" + dx);
            if (poseController != null) {
              if (Math.abs(dx) > 80) {
                dx = dx > 0 ? 80 : -80;
              }
              lastTacho = poseController.sendCommand(PoseController.TURN, -dx / 40.);
            }
          }
          if (Math.abs(dy) > 10) {
            canShoot = false;
//            System.out.println("dy :" + dy);
            if (poseController != null) {
              double value = dy / 40;
              if (value == 0) {
                value = dy > 0 ? 1 : -1;
              }

              if (!(value > 0 && lastTacho >= 0)) {
                lastTacho = poseController.sendCommand(PoseController.RAISE, value);
                System.out.println("lastTacho = " + lastTacho);
              }
            }
          }
          if (canShoot) {
            if (shootingController != null) {
              shootingController.shoot();
            } else {
              System.out.println("Shoot!!!");
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      return target;
    } else {
      return target;
    }
  }
}
