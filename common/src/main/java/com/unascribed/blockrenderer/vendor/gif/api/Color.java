package com.unascribed.blockrenderer.vendor.gif.api;

public class Color {

    public static int r(int color) {
        return color & 0xFF;
    }

    public static int g(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int b(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int a(int color) {
        return (color >> 24) & 0xFF;
    }

}
