package com.donch.pc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class PoseController {
  public static final int OK = 8000;
  public static final int TURN = 0;
  public static final int MOVE = 1;
  public static final int RAISE = 2;
  public static final int STOP = 1000;

  private final NXTConnector connection;
  private final DataOutputStream out;
  private final DataInputStream in;

  public PoseController(String nxt, String addr) {
    connection = new NXTConnector();

    System.out.println("Connecting to " + nxt);

    if (!connection.connectTo(nxt, addr, NXTCommFactory.BLUETOOTH)) {
      throw new RuntimeException("Failed to connect to NXT");
    }

    out = connection.getDataOut();
    in = connection.getDataIn();
  }

  public double sendCommand(int type, double value) {
    try {
      synchronized (out) {
        out.writeInt(type);
        out.writeDouble(value);

        out.flush();
      }

      synchronized (in) {
        int retval = in.readInt();
        if (retval != OK) {
          System.out.println("Retval = " + retval);
        }

        return in.readDouble();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }
}
