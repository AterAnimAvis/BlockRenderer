package com.unascribed.blockrenderer.varia;

import com.unascribed.blockrenderer.varia.rendering.STBWrapper;
import com.unascribed.blockrenderer.varia.rendering.TileRenderer;

public class Images {

    public static STBWrapper fromTRFlipped(TileRenderer renderer) {
        return new STBWrapper(renderer.getImageWidth(), renderer.getImageHeight(), renderer.getBuffer());
    }

    public static boolean same(STBWrapper a, STBWrapper b) {
        if (a.getWidth() != b.getWidth()) return false;
        if (a.getHeight() != b.getHeight()) return false;

        for (int x = 0; x < a.getWidth(); x++)
            for (int y = 0; y < a.getHeight(); y++)
                if (a.getPixelRGBA(x, y) != b.getPixelRGBA(x, y))
                    return false;

        return true;
    }

}
