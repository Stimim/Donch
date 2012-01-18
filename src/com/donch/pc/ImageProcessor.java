package com.donch.pc;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
  /**
   * Sets the image that is going to process
   */
  void setSourceImage(BufferedImage image);

  void process();

  void setRange(Range range);

  /**
   * Returns the image that is processed
   */
  BufferedImage getResult();
}
