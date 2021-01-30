package com.unascribed.blockrenderer.vendor.gif.impl;

import com.unascribed.blockrenderer.vendor.gif.api.IIndexedColorImage;

public class IndexedColorImage implements IIndexedColorImage {

    final int width;
    final int height;
    final int[] data;

    final int[] paletteData;

    public IndexedColorImage(int width, int height, int[] data, int[] paletteData) {
        this.width = width;
        this.height = height;
        this.data = data;
        this.paletteData = paletteData;
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
    public int[] getData() {
        return data;
    }

    @Override
    public int[] getPaletteData() {
        return paletteData;
    }

}
