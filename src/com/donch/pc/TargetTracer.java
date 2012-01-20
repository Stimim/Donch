package com.donch.pc;

import java.io.IOException;
import java.util.List;

import com.donch.pc.TargetDetector.Point;

public class TargetTracer {
  private static final int MIN_AIMED_COUNT = 1;

  private final PoseController poseController;
  private final ShootingController shootingController;

  private Point target;

  private double lastTacho = 0;
  private double ratioX = 40;
  private double ratioY = 12;

  private double oldTargetX1 = 0;
  private double oldTargetX2 = 0;
  private double oldTargetX3 = 0;

  private int frameNum = 0;

  private int demoMode = 1;

  private Point frontSight = new Point(320, 350);

  // private final KalmanFilter kalmanFilter = new KalmanFilter();

  private boolean dontTrace = false;

  private int aimedCount = 0;

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

  public void setDemoMode(int demoMode) {
    this.demoMode = demoMode;
  }

  public void setFrontSight(Point point) {
    System.out.println(point.x + ", " + point.y);
    this.frontSight = point;
  }

  public Point getFrontSight() {
    return frontSight;
  }

  void setDontTrace(boolean b) {
    dontTrace = b;
  }

  public Point update(List<Point> targets) {
    if (target == null) {
      target = new Point(160, 120);
    }

    Point closest = findNewTarget(targets);

    // closest = kalmanFilter.update(closest);

    // closest = kalmanFilter.predict(1);

    if (closest == null) {
      return target;
    }

    adjustRatio(closest);

    frontSight = new Point(frontSight.x, (int) Math.round(-13 * lastTacho / 29 + 230.8 - 20));
    target = closest;

    try {
      frameNum++;

      if (demoMode == 2) {
        if (frameNum % 5 == 0) {
          frameNum = 0;
          int targetShift = 6 * ((int) oldTargetX3 - (int) oldTargetX1);
          if (320 - targetShift > 0 && 320 - targetShift < 640) {
            frontSight = new Point(320 - targetShift, frontSight.y);
          }
        }
        int dx = frontSight.x - target.x;
        int dy = target.y - frontSight.y;
        if (Math.abs(dx) <= 10 && Math.abs(dy) <= 10) {
          if (++aimedCount >= MIN_AIMED_COUNT) {
            if (shootingController != null) {
              shootingController.shoot();
            } else {
              System.out.println("Shoot!!!");
            }
          }
        } else {
          aimedCount = 0;
          if (poseController != null) {
            if (!dontTrace) {
              if (Math.abs(dx) > 10) {
                // System.out.println("dx :" + dx);
                if (Math.abs(dx) > 80) {
                  dx = dx > 0 ? 80 : -80;
                }
                lastTacho = poseController.sendCommand(PoseController.TURN, dx / ratioX);
              }
            }
            if (Math.abs(dy) > 10) {
              // System.out.println("dy :" + dy);
              double value = Math.floor(dy / ratioY);
              if (value == 0) {
                value = dy > 0 ? 1 : -1;
              }

              if (!(value > 0 && lastTacho >= 0)) {
                lastTacho = poseController.sendCommand(PoseController.RAISE, value);
                System.out.println("lastTacho = " + lastTacho);
              }
            }
          }
        }

        oldTargetX1 = oldTargetX2;
        oldTargetX2 = oldTargetX3;
        oldTargetX3 = target.x;
      } else { // demoMode == 1
        int dx = frontSight.x - target.x;
        int dy = target.y - frontSight.y;
        if (Math.abs(dx) <= 10 && Math.abs(dy) <= 10) {
          if (++aimedCount >= MIN_AIMED_COUNT) {
            if (shootingController != null) {
              shootingController.shoot();
            } else {
              System.out.println("Shoot!!!");
            }
          }
        } else {
          aimedCount = 0;
          if (poseController != null) {
            if (!dontTrace) {
              if (Math.abs(dx) > 10) {
                // System.out.println("dx :" + dx);
                if (Math.abs(dx) > 80) {
                  dx = dx > 0 ? 80 : -80;
                }
                lastTacho = poseController.sendCommand(PoseController.TURN, dx / ratioX);
              }
            }
            if (Math.abs(dy) > 10) {
              // System.out.println("dy :" + dy);
              double value = Math.floor(dy / ratioY);
              if (value == 0) {
                value = dy > 0 ? 1 : -1;
              }

              if (!(value > 0 && lastTacho >= 0)) {
                lastTacho = poseController.sendCommand(PoseController.RAISE, value);
                System.out.println("lastTacho = " + lastTacho);
              }
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return target;
  }

  private Point findNewTarget(List<Point> targets) {
    Point closest = null;
    double distance = 0;

    for (Point p : targets) {
      double d = Point.distance(p, target);

      if (d > 70) {
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
    return closest;
  }

  public void resetRatio() {
    ratioX = ratioY = 40;
  }

  private void adjustRatio(Point newTarget) {
    int expectedDx = frontSight.x - target.x;
    int expectedDy = target.y - frontSight.y;
    if (Math.abs(expectedDx) > 80) {
      expectedDx = expectedDx > 0 ? 80 : -80;
    }

    int dx = newTarget.x - target.x;
    int dy = target.y - newTarget.y;

    // adjust ratioX
    if (dx != 0 && expectedDx != 0) {
      // ratioX = Math.max(ratioX * dx / expectedDx, 30);
      // System.out.println(ratioX);
    }
    // adjust ratioY
    if (dy != 0 && expectedDy != 0) {
//      ratioY = Math.max(ratioY * dy / expectedDy, 10);
    }
  }
}
