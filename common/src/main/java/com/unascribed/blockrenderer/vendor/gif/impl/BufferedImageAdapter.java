package com.unascribed.blockrenderer.vendor.gif.impl;

import com.unascribed.blockrenderer.vendor.gif.api.Color;
import com.unascribed.blockrenderer.vendor.gif.api.IImage;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class BufferedImageAdapter implements IImage {

    private final BufferedImage image;

    public BufferedImageAdapter(BufferedImage image) {
        this.image = image;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void forEach(Consumer<Color> consumer) {
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                consumer.accept(wrap(image.getRGB(x, y)));
            }
        }
    }

    private Color wrap(int rgb) {
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, (rgb >> 24) & 0xFF);
    }

}
