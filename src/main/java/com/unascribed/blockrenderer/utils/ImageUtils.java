package com.unascribed.blockrenderer.utils;

import com.unascribed.blockrenderer.lib.TileRenderer;

import java.awt.image.BufferedImage;

public interface ImageUtils {

    static BufferedImage readPixels(TileRenderer renderer) {
        int width  = renderer.imageWidth;
        int height = renderer.imageHeight;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = new int[width*height];

        renderer.buffer.asIntBuffer().get(pixels);

        img.setRGB(0, 0, width, height, pixels, 0, width);

        return img;
    }

}
