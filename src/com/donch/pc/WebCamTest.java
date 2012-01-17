package com.donch.pc;

import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
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
  private static final int WIDTH = 320;
  private static final int HEIGHT = 240;
  private static final int INPUT = 0;
  private static final int STANDARD = V4L4JConstants.STANDARD_WEBCAM;

  private static void showImage(Frame frame, Image image) {
    frame.getGraphics().drawImage(image, 0, 0, image.getWidth(null), image.getHeight(null), null);
  }

  public static void main(String[] args) {
    try {
      VideoDevice vd = new VideoDevice(DEV_NAME);
      if (vd.supportRGBConversion()) {
        final TargetDetector targetDetector = new TargetDetector();
        final TargetTracer tracer = new TargetTracer();

        final Frame frame = createFrame();
        final Frame processedFrame = createProcessedFrame(tracer);

        tracer.setPoint(new Point(frame.getWidth() / 2, frame.getHeight() / 2));

        FrameGrabber grabber = vd.getRGBFrameGrabber(WIDTH, HEIGHT, INPUT, STANDARD);

        grabber.setCaptureCallback(new CaptureCallback() {
          @Override
          public void exceptionReceived(V4L4JException caught) {
            caught.printStackTrace();
          }

          @Override
          public void nextFrame(VideoFrame vframe) {
            System.out.println(System.currentTimeMillis() + ":" + vframe.getBytes().length);

            BufferedImage image = vframe.getBufferedImage();
            showImage(frame, image);

            image = processImage(image, WIDTH, HEIGHT);

            ArrayList<Point> targets = targetDetector.find(image);

            for (Point target : targets) {
              int x = target.x >= image.getWidth() ? image.getWidth() - 1 : (target.x < 0 ? 0 : target.x);
              int y = target.y >= image.getHeight() ? image.getHeight() - 1 : (target.y < 0 ? 0 : target.y);

              System.out.println(x + ", " + y);
              image.setRGB(x, y, ColorUtils.GREEN);

              int _x = x;
              if (x > 0) {
                _x --;
                image.setRGB(x - 1, y, ColorUtils.GREEN);
              } else {
                _x ++;
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

              System.out.println(x + ", " + y);
              image.setRGB(x, y, ColorUtils.RED);

              int _x = x;
              if (x > 0) {
                _x --;
                image.setRGB(x - 1, y, ColorUtils.RED);
              } else {
                _x ++;
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

  private static BufferedImage processImage(BufferedImage image, int width, int height) {
    return edge(image, width, height);
  }

  private static BufferedImage edge(BufferedImage image, int width, int height) {
    ImageProcessor processor = new ColorFilter();

    processor.setSourceImage(image);

    processor.process();

    return processor.getResult();
  }

  private static Frame createFrame() {
    Frame frame = new Frame();

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
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
        tracer.setPoint(new Point(event.getX(), event.getY()));
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

    return processedFrame;
  }
}
