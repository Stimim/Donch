package com.donch.pc;

import java.awt.image.BufferedImage;

import com.donch.pc.ColorUtils.HSV;

public class ColorFilter implements ImageProcessor {
  private BufferedImage image = null;
  private BufferedImage result = null;

  private Range range;

  @Override
  public void setRange(Range range) {
    this.range = range;
  }

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

    result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    float avgV = averageV();

    for (int x = 0; x < width; ++ x) {
      for (int y = 0; y < height; ++ y) {
        int rgb = image.getRGB(x, y);
        if (range == null || range.inRange(x, y)) {
          HSV hsv = ColorUtils.rgb2hsv(rgb);

          float h = Math.abs(hsv.H - HSV.MAX_H / 2) / HSV.MAX_H * 2;
          float s = hsv.S / HSV.MAX_S;
          float v = ((hsv.V - avgV) / HSV.MAX_V + 0.5f);
          int r = ColorUtils.getR(rgb);
          int b = ColorUtils.getB(rgb);
          int g = ColorUtils.getG(rgb);
          float k = r <= 0 ? 0 : 1f - 1f * b * g / (r * r);
          if (k < 0) {
            k = 0;
          } else {
            k = (float) Math.pow(k, 5);
          }
//          k = 1;

          int value = Math.round((h * s * v * k) * 255);

          if (value < 100) {
            value = 0;
          }
//          else {
//            value = 255;
//          }
          result.setRGB(x, y, ColorUtils.getRGB(value, value, value));
        } else {
          result.setRGB(x, y, rgb);
        }
      }
    }
  }

  private float averageV() {
    int width = image.getWidth();
    int height = image.getHeight();

    float sum = 0;
    for (int x = 0; x < width; ++ x) {
      for (int y = 0; y < height; ++ y) {
        int rgb = image.getRGB(x, y);;

        HSV hsv = ColorUtils.rgb2hsv(rgb);

        sum += hsv.V;
      }
    }

    return sum / width / height;
  }

  @Override
  public BufferedImage getResult() {
    return result;
  }

}
