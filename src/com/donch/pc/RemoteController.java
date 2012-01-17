package com.donch.pc;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class RemoteController {
  static {
    System.loadLibrary("bluecove_x64");
  }

  private static final int WIDTH = 500;
  private static final int HEIGHT = 500;

  private static Frame createFrame(NXTConnector connection) {
    final DataOutputStream out = connection.getDataOut();

    Frame frame = new Frame("Controller") {
      private static final long serialVersionUID = 1L;

      @Override
      public void paint(Graphics graphics) {
        int mX = this.getWidth() / 2;
        int mY = this.getHeight() / 2;
        graphics.drawLine(mX, mY - 10, mX, mY + 10);
        graphics.drawLine(mX - 10, mY, mX + 10, mY);
      }
    };

    System.out.println("Created");

    frame.setSize(RemoteController.WIDTH, RemoteController.HEIGHT);
    frame.setVisible(true);

    frame.setResizable(false);

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent event) {
        System.exit(0);
      }
    });

    frame.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent event) { }
      @Override
      public void mouseEntered(MouseEvent event) { }
      @Override
      public void mouseExited(MouseEvent event) { }

      @Override
      public void mouseReleased(MouseEvent event) { }

      @Override
      public void mousePressed(MouseEvent event) {
        try {
          out.writeInt((event.getX() - RemoteController.WIDTH / 2) * 2);
          out.writeInt((RemoteController.HEIGHT / 2 - event.getY()) * 2);
          out.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        System.out.println(event.getX() - RemoteController.WIDTH / 2);
        System.out.println(RemoteController.HEIGHT / 2 - event.getY());
      }
    });
    return frame;
  }

  public static void main(String[] args) throws IOException {
    createFrame(connect());
  }

  private static NXTConnector connect() {
    NXTConnector conn = new NXTConnector();

    conn.setDebug(true);

    if (!conn.connectTo("MrOrz", "0016530990D2", NXTCommFactory.BLUETOOTH)) {
      throw new RuntimeException("Failed to connect to NXT");
    }

    System.out.println("Connected");

    return conn;
  }
}
