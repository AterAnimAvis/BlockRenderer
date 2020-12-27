package com.unascribed.blockrenderer.client.api.vendor.joml;

import java.text.NumberFormat;

public interface _Access {

  static String formatNumbers(String input) {
    return Runtime.formatNumbers(input);
  }

  static String format(double input, NumberFormat formatter) {
    return Runtime.format(input, formatter);
  }

  static NumberFormat formatter() {
    return Options.NUMBER_FORMAT;
  }

  static boolean equals(double a, double b, double delta) {
    return Runtime.equals(a, b, delta);
  }

}
