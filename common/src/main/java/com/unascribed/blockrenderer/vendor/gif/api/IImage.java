package com.unascribed.blockrenderer.vendor.gif.api;

import java.util.function.Consumer;

public interface IImage {

    int getWidth();

    int getHeight();

    void forEach(Consumer<Color> consumer);

}
