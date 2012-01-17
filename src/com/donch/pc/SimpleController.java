package com.donch.pc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class SimpleController {
  public static final int OK = 8000;
  public static final int TURN = 0;
  public static final int MOVE = 1;
  public static final int RAISE = 2;

  private final NXTConnector connection;
  private final DataOutputStream out;
  private final DataInputStream in;

  public SimpleController(String nxt, String addr) {
    connection = new NXTConnector();

    if (!connection.connectTo(nxt, addr, NXTCommFactory.BLUETOOTH)) {
      throw new RuntimeException("Failed to connect to NXT");
    }

    out = connection.getDataOut();
    in = connection.getDataIn();
  }

  public int sendCommand(int type, int value) throws IOException {
    out.writeInt(type);
    out.writeInt(value);

    out.flush();

    return in.readInt();
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

  public static void main(String[] args) throws IOException {
    NXTConnector connection = connect();

    DataInputStream in = connection.getDataIn();
    DataOutputStream out = connection.getDataOut();

    Scanner scanner = new Scanner(System.in);

    while (true) {
      int type = scanner.nextInt();
      int amount = scanner.nextInt();

      System.out.println(type + " " + amount);

      out.writeInt(type);
      out.writeInt(amount);

      out.flush();
      out.flush();

      int retval = in.readInt();

      if (retval != OK) {
        break;
      }
    }
  }
}
