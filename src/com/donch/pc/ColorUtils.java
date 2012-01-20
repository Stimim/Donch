package com.donch.pc;

public class ColorUtils {
  public static final byte BLACK = 0;
  public static final int RED = 0xFFFF0000;
  public static final int GREEN = 0xFF00FF00;
  public static final int BLUE = 0xFF0000FF;
  public static final int YELLOW = 0xFFFFFF00;
  public static final int WHITE = 0xFFFFFFFF;

  public static int asGray(int value) {
    return value & 0xFF;
  }

  public static int getR(int rgb) {
    return ((rgb & 0x00FF0000) >> 16);
  }

  public static int getG(int rgb) {
    return ((rgb & 0x0000FF00) >> 8);
  }

  public static int getB(int rgb) {
    return (rgb & 0x000000FF);
  }

  public static int getRGB(int r, int g, int b) {
    return (0xFF << 24) | (saturate(r) << 16) | (saturate(g) << 8) | saturate(b);
  }

  public static int saturate(int value) {
    return value > 255 ? 255 : (value < 0 ? 0 : value);
  }

  public static class HSV {
    public static final float MAX_H = 360;
    public static final float MAX_S = 100;
    public static final float MAX_V = 100;

    float H = 0; // between 0 ~ 360
    float S = 0; // between 0 ~ 100
    float V = 0; // between 0 ~ 100
  }

  public static HSV rgb2hsv(int rgb) {
    int R = getR(rgb);
    int G = getG(rgb);
    int B = getB(rgb);

    float r = (R / 255f);
    float g = (G / 255f);
    float b = (B / 255f);

    HSV hsv = new HSV();

    int minRGB = Math.min(Math.min(R, G), B);
    int maxRGB = Math.max(Math.max(R, G), B);

    // Black-gray-white
    if (minRGB == maxRGB) {
      hsv.V = Math.round(minRGB / 255f * 100);
      return hsv;
    }

    // Colors other than black-gray-white:
    float d = (R == minRGB) ? g - b : ((B == minRGB) ? r - g : b - r);
    float h = (R == minRGB) ? 3 : ((B == minRGB) ? 1 : 5);
    hsv.H = (60 * (h - d / (maxRGB - minRGB) * 255f));
    hsv.S = (100f * (maxRGB - minRGB) / maxRGB);
    hsv.V = (100 * maxRGB / 255f);

    return hsv;
  }
}
