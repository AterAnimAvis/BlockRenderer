package com.unascribed.blockrenderer.utils;

import com.unascribed.blockrenderer.lib.TileRenderer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public interface ImageUtils {

    static BufferedImage readPixels(TileRenderer renderer) {
        int width  = renderer.imageWidth;
        int height = renderer.imageHeight;

        // Turn a bytebuffer into a Java object we can do things to.
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = new int[width*height];
        renderer.buffer.asIntBuffer().get(pixels);

        img.setRGB(0, 0, width, height, pixels, 0, width);

        return img;
    }

    static BufferedImage createFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        /*
         * Creates a compound affine transform, instead of just one, as we need
         * to perform two transformations.
         *
         * The first one is to scale the image to 100% width, and -100% height.
         * (That's *negative* 100%.)
         */
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        /*
         * We then need to translate the image back up by it's height, as flipping
         * it over moves it off the bottom of the canvas.
         */
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        // Create a blank image with the same dimensions as the old one...
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        // ...get it's renderer...
        Graphics2D g = newImage.createGraphics();
        /// ...and draw the old image on top of it with our transform.
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

}
