package com.unascribed.blockrenderer.client.varia.rendering;

import com.unascribed.blockrenderer.client.vendor.tr.TR;

import java.nio.ByteBuffer;

import static java.lang.Math.max;

public interface TileRenderer {

    /* ===================================================================================================== Api ==== */

    void orthographic(double left, double right, double bottom, double top, double zNear, double zFar);

    void beginTile();

    boolean endTile();

    /* =============================================================================================== Accessors ==== */

    ByteBuffer getBuffer();

    int getImageWidth();

    int getImageHeight();

    /* ================================================================================================= Utility ==== */

    default void clearBuffer() {
        getBuffer().clear();
    }

    /* ================================================================================================= Factory ==== */

    int BORDER_SIZE = 4;
    int MINIMUM_SIZE = BORDER_SIZE * 2 + 1;

    static TileRenderer forSize(int size, int tileSize) {
        return forSize(size, size, tileSize, tileSize);
    }

    static TileRenderer forSize(int width, int height, int tileWidth, int tileHeight) {

        /* Ensure Image Sizes are bigger than minimumSize */
        width = max(width, MINIMUM_SIZE);
        height = max(height, MINIMUM_SIZE);

        /* Ensure Tile Sizes are bigger than minimumSize */
        tileWidth = max(tileWidth, MINIMUM_SIZE);
        tileHeight = max(tileHeight, MINIMUM_SIZE);

        /* Ensure Tile Sizes is not bigger than Image Sizes */
        if (width < tileWidth) tileWidth = width;
        if (height < tileHeight) tileHeight = height;

        /* If Tile Sizes are the same as Image Sizes then no border otherwise a border of BORDER_SIZE pixels */
        boolean sameSize = width == tileWidth && height == tileHeight;
        return new TR(width, height, tileWidth, tileHeight, sameSize ? 0 : BORDER_SIZE);
    }

}