package com.donch.pc;

import java.io.DataOutputStream;
import java.io.IOException;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

public class ShootingController {
  public static final int STOP = 1000;
  public static final int SHOOT = 5;

  private final NXTConnector connection;
  private final DataOutputStream out;

  private Long lastShootTime = 0L;

  public ShootingController(String nxt, String addr) {
    connection = new NXTConnector();

    if (!connection.connectTo(nxt, addr, NXTCommFactory.BLUETOOTH)) {
      throw new RuntimeException("Failed to connect to NXT");
    }

    out = connection.getDataOut();
  }

  public void shoot() throws IOException {
    long time = System.currentTimeMillis();
    if (time - lastShootTime < 4000) {
      return;
    }

    synchronized (lastShootTime) {
      if (time - lastShootTime < 4000) {
        return;
      }
      lastShootTime = time;
    }

    System.out.println("Shoot!!!");
    out.writeInt(SHOOT);
    out.flush();
  }

  public void stop() throws IOException {
    out.writeInt(STOP);
    out.flush();
  }
}
