package com.unascribed.blockrenderer.vendor.gif.api;

public interface IIndexedColorImage {

    int getWidth();

    int getHeight();

    int[] getData();

    int[] getPaletteData();

}
