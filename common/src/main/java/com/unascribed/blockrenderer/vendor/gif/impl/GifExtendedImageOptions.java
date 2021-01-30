package com.unascribed.blockrenderer.vendor.gif.impl;

import com.unascribed.blockrenderer.vendor.gif.api.DisposalMethod;
import com.unascribed.blockrenderer.vendor.gif.api.IGifExtendedImageOptions;
import org.intellij.lang.annotations.MagicConstant;

public class GifExtendedImageOptions extends GifImageOptions implements IGifExtendedImageOptions {

    private final int delay;
    private final int disposal;
    private final int index;

    public GifExtendedImageOptions(int top, int left, int delay, @MagicConstant(valuesFromClass = DisposalMethod.class) int disposal, int transparentColorIndex) {
        super(top, left);
        this.delay = delay;
        this.disposal = disposal;
        this.index = transparentColorIndex;
    }

    @Override
    public int getDelayTimeInMS() {
        return delay;
    }

    @Override
    public int getDisposalMethod() {
        return disposal;
    }

    @Override
    public int getTransparentColorIndex() {
        return index;
    }

}
