package com.unascribed.blockrenderer.vendor.gif.impl;

import com.unascribed.blockrenderer.vendor.gif.api.Color;
import com.unascribed.blockrenderer.vendor.gif.api.IImage;

import java.util.function.Consumer;

public class Image implements IImage {

    private final Color[][] data;
    private final int width;
    private final int height;

    public Image(int width, int height, Color[] data) {
        this.data = new Color[height][width];
        this.width = width;
        this.height = height;

        for (int y = 0; y < this.data.length; y++) {
            System.arraycopy(data, y * width, this.data[y], 0, this.data[y].length);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void forEach(Consumer<Color> consumer) {
        for (Color[] datum : data) {
            for (Color color : datum) {
                consumer.accept(color);
            }
        }
    }
}
