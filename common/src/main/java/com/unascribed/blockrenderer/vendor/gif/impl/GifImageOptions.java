package com.unascribed.blockrenderer.vendor.gif.impl;

import com.unascribed.blockrenderer.vendor.gif.api.IGifImageOptions;

public class GifImageOptions implements IGifImageOptions {

    private final int top;
    private final int left;

    public GifImageOptions(int top, int left) {
        this.top = top;
        this.left = left;
    }

    @Override
    public int getTopPosition() {
        return top;
    }

    @Override
    public int getLeftPosition() {
        return left;
    }

}
