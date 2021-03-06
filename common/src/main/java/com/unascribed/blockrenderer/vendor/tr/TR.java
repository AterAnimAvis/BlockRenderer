/*
 * Copyright (c) 1997-2005 Brian Paul. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Brian Paul or the names of contributors may be
 * used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. THE COPYRIGHT HOLDERS AND CONTRIBUTORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO
 * EVENT WILL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY
 * LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF THE COPYRIGHT HOLDERS OR
 * CONTRIBUTORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package com.unascribed.blockrenderer.vendor.tr;

import com.unascribed.blockrenderer.varia.rendering.TileRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * A <b>reduced</b> port of Brian Paul's tile rendering library, found at <a href =
 * "http://www.mesa3d.org/brianp/TR.html"> http://www.mesa3d.org/brianp/TR.html </a> .
 * <p>
 * Original code Copyright (C) 1997-2005 Brian Paul. <br> Licensed under BSD-compatible terms with permission of the
 * author. <br> See LICENSE.md for license information.
 * </p>
 * <p>
 * Reduced to bare minimums by AterAnimAvis <br> See <a href="https://github.com/sgothel/jogl/blob/master/src/jogl/classes/com/jogamp/opengl/util/">JOGL#opengl/util/</a>
 * for original source code.
 * </p>
 *
 * @author Brian Paul, ryanm, sgothel, ateranimavis
 */
public class TR implements TileRenderer {

    private final ByteBuffer buffer;

    public final int imageWidth;
    public final int imageHeight;

    private final int tileBorder;
    private final int tileWidth;
    private final int tileHeight;

    private final int tileWidthNB;
    private final int tileHeightNB;

    private final int columns;
    private final int rows;

    private int currentColumn = 0;
    private int currentRow = 0;
    private int currentTile = -1;

    private int currentTileWidth;
    private int currentTileHeight;

    public TR(int width, int height, int tileWidth, int tileHeight, int tileBorder) {
        imageWidth = width;
        imageHeight = height;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileBorder = tileBorder;

        tileWidthNB = tileWidth - 2 * tileBorder;
        tileHeightNB = tileHeight - 2 * tileBorder;

        columns = (imageWidth + tileWidthNB - 1) / tileWidthNB;
        rows = (imageHeight + tileHeightNB - 1) / tileHeightNB;

        buffer = BufferUtils.createByteBuffer(width * height * 4);
    }

    private double left = 0;
    private double right = 0;
    private double bottom = 0;
    private double top = 0;
    private double near = 0;
    private double far = 0;

    private final int[] viewport = new int[4];

    /**
     * Sets the orthographic projection, Must be called before rendering the first tile.
     */
    @Override
    public void orthographic(double left, double right, double bottom, double top, double zNear, double zFar) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.near = zNear;
        this.far = zFar;
    }

    @Override
    public void beginTile() {
        /* Save user's viewport, will be restored after last tile rendered */
        if (currentTile <= 0) {
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            currentTile = 0;
        }

        /* Which tile (by row and column) we're about to render */
        currentRow = rows - 1 - (currentTile / columns);
        currentColumn = currentTile % columns;

        /* Compute actual size of this tile with border */
        int th, tw;

        if (currentRow < rows - 1) {
            th = tileHeight;
        } else {
            th = imageHeight - (rows - 1) * tileHeightNB + 2 * tileBorder;
        }

        if (currentColumn < columns - 1) {
            tw = tileWidth;
        } else {
            tw = imageWidth - (columns - 1) * tileWidthNB + 2 * tileBorder;
        }

        /* Save tile size, with border */
        currentTileHeight = th;
        currentTileWidth = tw;

        /* Apply the viewport */
        GL11.glViewport(0, 0, tw, th);

        /* Save current matrix mode */
        final int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        /* Compute projection parameters */
        final double l = left + (right - left) * (currentColumn * tileWidthNB - tileBorder) / imageWidth;
        final double r = l + (right - left) * tw / imageWidth;
        final double b = bottom + (top - bottom) * (currentRow * tileHeightNB - tileBorder) / imageHeight;
        final double t = b + (top - bottom) * th / imageHeight;

        /* Apply the projection */
        GL11.glOrtho(l, r, b, t, near, far);

        /* Restore user's matrix mode */
        GL11.glMatrixMode(matrixMode);
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public boolean endTile() {
        /* Be sure OpenGL rendering is finished */
        GL11.glFlush();

        /* Save current glPixelStore values */
        int prevRowLength = GL11.glGetInteger(GL11.GL_PACK_ROW_LENGTH);
        int prevSkipRows = GL11.glGetInteger(GL11.GL_PACK_SKIP_ROWS);
        int prevSkipPixels = GL11.glGetInteger(GL11.GL_PACK_SKIP_PIXELS);
        int prevAlignment = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);

        /* Read Pixels */
        final int srcX = tileBorder;
        final int srcY = tileBorder;
        final int srcWidth = currentTileWidth - 2 * tileBorder;
        final int srcHeight = currentTileHeight - 2 * tileBorder;
        final int destX = tileWidthNB * currentColumn;
        final int destY = tileHeightNB * currentRow;

        /* Setup pixel store for glReadPixels */
        GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, imageWidth);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, destY);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, destX);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);

        /* Read the tile into the final image */
        GL11.glReadPixels(srcX, srcY, srcWidth, srcHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        /* Restore previous glPixelStore Values */
        GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, prevRowLength);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, prevSkipRows);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, prevSkipPixels);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, prevAlignment);

        /* Increment Tile Counter */
        currentTile++;

        if (currentTile >= rows * columns) {
            /* Restore user's viewport */
            GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            currentTile = -1;
            return false;
        }

        return true;
    }

    @NotNull
    @Override
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public int getImageWidth() {
        return imageWidth;
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

}

