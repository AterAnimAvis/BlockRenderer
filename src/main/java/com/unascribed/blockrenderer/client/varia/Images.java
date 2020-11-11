package com.unascribed.blockrenderer.client.varia;

import com.unascribed.blockrenderer.client.varia.rendering.TileRenderer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Images {

    public static BufferedImage fromTRFlipped(TileRenderer renderer) {
        return flip(fromTR(renderer));
    }

    public static BufferedImage fromTR(TileRenderer renderer) {
        int width = renderer.getImageWidth();
        int height = renderer.getImageHeight();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[] pixels = new int[width * height];

        renderer.getBuffer().asIntBuffer().get(pixels);

        img.setRGB(0, 0, width, height, pixels, 0, width);

        return img;
    }

    private static BufferedImage flip(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage old, AffineTransform at) {
        /* Create a new Image the same size and type as the old image */
        BufferedImage img = new BufferedImage(old.getWidth(), old.getHeight(), old.getType());

        /* Get the graphics */
        Graphics2D g = img.createGraphics();

        /* Apply the transformations */
        g.transform(at);

        /* Render the old */
        g.drawImage(old, 0, 0, null);
        g.dispose();

        /* Return Resulting */
        return img;
    }

    public static boolean same(BufferedImage a, BufferedImage b) {
        if (a.getWidth() != b.getWidth()) return false;
        if (a.getHeight() != b.getHeight()) return false;

        for (int x = 0; x < a.getWidth(); x++)
            for (int y = 0; y < a.getHeight(); y++)
                if (a.getRGB(x, y) != b.getRGB(x, y))
                    return false;

        return true;
    }

}