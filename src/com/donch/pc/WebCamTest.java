package com.donch.pc;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import au.edu.jcu.v4l4j.CaptureCallback;
import au.edu.jcu.v4l4j.FrameGrabber;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.VideoFrame;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

import com.donch.pc.TargetDetector.Point;

public class WebCamTest {
  private static final String DEV_NAME = "/dev/video1";
  private static final int WIDTH = 640;
  private static final int HEIGHT = 480;
  private static final int INPUT = 0;
  private static final int STANDARD = V4L4JConstants.STANDARD_WEBCAM;

  private static final int BOX_SIZE = 75;

  private static void showImage(Frame frame, Image image) {
    frame.getGraphics().drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
  }

//   private static final PoseController poseController = null;
  private static final PoseController poseController = new PoseController("MrOrz", "0016530990D2");
//   private static final ShootingController shootingController = null;
  private static final ShootingController shootingController = new ShootingController("SaJim", "00165305E9EA");

  public static void main(String[] args) {
    try {
      VideoDevice vd = new VideoDevice(DEV_NAME);
      if (vd.supportRGBConversion()) {
        final TargetDetector targetDetector = new TargetDetector();

        final TargetTracer tracer = new TargetTracer(poseController, shootingController);

        final Frame frame = createFrame();
        final Frame processedFrame = createProcessedFrame(tracer);

        tracer.setTarget(new Point(frame.getWidth() / 2, frame.getHeight() / 2));

        FrameGrabber grabber = vd.getRGBFrameGrabber(WIDTH, HEIGHT, INPUT, STANDARD);
        // grabber.setFrameInterval(1, 20);

        grabber.setCaptureCallback(new CaptureCallback() {
          @Override
          public void exceptionReceived(V4L4JException caught) {
            caught.printStackTrace();
          }

          @Override
          public void nextFrame(VideoFrame vframe) {
            BufferedImage image = vframe.getBufferedImage();
            showImage(frame, image);

            Point oldTarget = tracer.getTarget();

            Range range = new Range(oldTarget.x - BOX_SIZE, oldTarget.x + BOX_SIZE, oldTarget.y
                - BOX_SIZE, oldTarget.y + BOX_SIZE);

            image = processImage(image, range);

            targetDetection(targetDetector, tracer, image, range);

            Point frontSight = tracer.getFrontSight();

            image.setRGB(frontSight.x, frontSight.y, ColorUtils.YELLOW);

            showImage(processedFrame, image);

            image = null;

            vframe.recycle();
          }

        });

        grabber.startCapture();

        Scanner scanner = new Scanner(System.in);
        scanner.next();

        grabber.stopCapture();
      }
    } catch (V4L4JException e) {
      e.printStackTrace();
    }
  }

  private static BufferedImage processImage(BufferedImage image, Range range) {
    ImageProcessor processor = new ColorFilter();

    processor.setSourceImage(image);

    processor.setRange(range);

    processor.process();

    return processor.getResult();
  }

  private static Frame createFrame() {
    Frame frame = new Frame();

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        if (shootingController != null) {
          try {
            shootingController.stop();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        System.exit(0);
      }
    });

    frame.setResizable(false);

    frame.setSize(WIDTH, HEIGHT);
    frame.setVisible(true);

    return frame;
  }

  private static Frame createProcessedFrame(final TargetTracer tracer) {
    Frame processedFrame = createFrame();

    processedFrame.addMouseListener(new MouseListener() {

      @Override
      public void mouseClicked(MouseEvent event) {
        switch (event.getButton()) {
        case MouseEvent.BUTTON1: // left click
          tracer.resetRatio();
          tracer.setTarget(new Point(event.getX(), event.getY()));
          break;
        case MouseEvent.BUTTON2:
          break;
        case MouseEvent.BUTTON3: // right click
          tracer.resetRatio();
          tracer.setFrontSight(new Point(event.getX(), event.getY()));
          break;
        }
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

      }

    });

    processedFrame.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent event) {
        System.out.println(event.getKeyCode());
        if (poseController != null) {
          switch (event.getKeyCode()) {
          case KeyEvent.VK_LEFT:
            poseController.sendCommand(PoseController.TURN, 90);
            break;
          case KeyEvent.VK_RIGHT:
            poseController.sendCommand(PoseController.TURN, -90);
            break;
          case KeyEvent.VK_UP:
            poseController.sendCommand(PoseController.MOVE, 100);
            break;
          case KeyEvent.VK_DOWN:
            poseController.sendCommand(PoseController.MOVE, -100);
            break;
          case KeyEvent.VK_PAGE_UP:
            poseController.sendCommand(PoseController.RAISE, -10);
            break;
          case KeyEvent.VK_PAGE_DOWN:
            poseController.sendCommand(PoseController.RAISE, 10);
            break;
          }
        }

        if (shootingController != null) {
          try {
            switch (event.getKeyCode()) {
            case 32: // space
              shootingController.shoot();
              break;
            case 'B':
              shootingController.back();
              break;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent event) {
        if (poseController == null) {
          return;
        }
        switch (event.getKeyCode()) {
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_PAGE_UP:
        case KeyEvent.VK_PAGE_DOWN:
          tracer.resetRatio();
          poseController.sendCommand(PoseController.STOP, 0);
        }
      }

      @Override
      public void keyTyped(KeyEvent event) {
      }

    });

    return processedFrame;
  }

  private static void targetDetection(final TargetDetector targetDetector,
      final TargetTracer tracer, BufferedImage image, Range range) {
    ArrayList<Point> targets = targetDetector.find(image, range);

    for (Point target : targets) {
      int x = target.x >= image.getWidth() ? image.getWidth() - 1 : (target.x < 0 ? 0 : target.x);
      int y = target.y >= image.getHeight() ? image.getHeight() - 1 : (target.y < 0 ? 0 : target.y);

      // System.out.println(x + ", " + y);
      image.setRGB(x, y, ColorUtils.GREEN);

      int _x = x;
      if (x > 0) {
        _x--;
        image.setRGB(x - 1, y, ColorUtils.GREEN);
      } else {
        _x++;
        image.setRGB(x + 1, y, ColorUtils.GREEN);
      }

      if (y > 0) {
        image.setRGB(x, y - 1, ColorUtils.GREEN);
        image.setRGB(_x, y - 1, ColorUtils.GREEN);
      } else {
        image.setRGB(x, y + 1, ColorUtils.GREEN);
        image.setRGB(_x, y + 1, ColorUtils.GREEN);
      }
    }

    Point p = tracer.update(targets);

    if (p != null) {
      int x = p.x >= image.getWidth() ? image.getWidth() - 1 : (p.x < 0 ? 0 : p.x);
      int y = p.y >= image.getHeight() ? image.getHeight() - 1 : (p.y < 0 ? 0 : p.y);

      image.setRGB(x, y, ColorUtils.RED);

      int _x = x;
      if (x > 0) {
        _x--;
        image.setRGB(x - 1, y, ColorUtils.RED);
      } else {
        _x++;
        image.setRGB(x + 1, y, ColorUtils.RED);
      }

      if (y > 0) {
        image.setRGB(x, y - 1, ColorUtils.RED);
        image.setRGB(_x, y - 1, ColorUtils.RED);
      } else {
        image.setRGB(x, y + 1, ColorUtils.RED);
        image.setRGB(_x, y + 1, ColorUtils.RED);
      }
    }
  }
}
