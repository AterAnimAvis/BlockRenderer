package com.unascribed.blockrenderer.vendor.gif.api;

import java.util.function.IntConsumer;

public interface IImage {

    int getWidth();

    int getHeight();

    void forEach(IntConsumer consumer);

}
