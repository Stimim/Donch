package com.donch.pc;

import java.awt.image.BufferedImage;

/**
 * -1 -1 -1
 * -1  8 -1
 * -1 -1 -1
 *
 * Warning: this function is not multithread-safe.
 *
 * @author stimim
 */
public class LaplacianFilter implements ImageProcessor {
  private BufferedImage image = null;
  private BufferedImage result = null;

  @Override
  public void setSourceImage(BufferedImage image) {
    if (this.image == image) {
      return;
    }
    this.image = image;
    result = null;
  }

  @Override
  public void process() {
    if (result != null) {
      return;
    }

    int width = image.getWidth();
    int height = image.getHeight();

    result = new BufferedImage(width - 2, height - 2, BufferedImage.TYPE_INT_RGB);

    for (int x = 0; x < width - 2; ++ x) {
      for (int y = 0; y < height - 2; ++ y) {
        int r = 0;
        int g = 0;
        int b = 0;
        int rgb = 0;

        rgb = image.getRGB(x + 1, y + 1);
        r = ColorUtils.getR(rgb) * 8;
        g = ColorUtils.getG(rgb) * 8;
        b = ColorUtils.getB(rgb) * 8;

        for (int dx = 0; dx < 3; ++ dx) {
          for (int dy = 0; dy < 3; ++ dy) {
            if (dy == 1 && dx == 1) {
              continue;
            }

            rgb = image.getRGB(dx + x, dy + y);
            r -= ColorUtils.getR(rgb);
            g -= ColorUtils.getG(rgb);
            b -= ColorUtils.getB(rgb);
          }
        }

        result.setRGB(x, y, ColorUtils.getRGB(r, g, b));
      }
    }
  }

  @Override
  public BufferedImage getResult() {
    return result;
  }

}
