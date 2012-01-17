package com.donch.pc;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
  /**
   * Sets the image that is going to process
   */
  void setSourceImage(BufferedImage image);

  void process();

  /**
   * Returns the image that is processed
   */
  BufferedImage getResult();
}
