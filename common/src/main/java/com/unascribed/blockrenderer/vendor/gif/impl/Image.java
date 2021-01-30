package com.unascribed.blockrenderer.vendor.gif.impl;

import com.unascribed.blockrenderer.vendor.gif.api.IImage;

import java.util.function.IntConsumer;

public class Image implements IImage {

    private final int[][] data;
    private final int width;
    private final int height;

    public Image(int width, int height, int[] data) {
        this.data = new int[height][width];
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
    public void forEach(IntConsumer consumer) {
        for (int[] datum : data) {
            for (int color : datum) {
                consumer.accept(color);
            }
        }
    }
}
