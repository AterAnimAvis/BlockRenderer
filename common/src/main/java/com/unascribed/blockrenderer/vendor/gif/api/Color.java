package com.unascribed.blockrenderer.vendor.gif.api;

import java.util.Objects;

public class Color {

    public final int red;
    public final int blue;
    public final int green;
    public final int alpha;

    public Color(int red, int blue, int green) {
        this(red, blue, green, 0xFF);
    }

    public Color(int red, int blue, int green, int alpha) {
        this.red = red;
        this.blue = blue;
        this.green = green;
        this.alpha = alpha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return red == color.red && blue == color.blue && green == color.green && alpha == color.alpha;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, blue, green, alpha);
    }
}
