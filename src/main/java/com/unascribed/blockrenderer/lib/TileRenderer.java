/*
 * Copyright 2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 *
 * ---------------------
 *
 * Based on Brian Paul's tile rendering library, found
 * at <a href = "http://www.mesa3d.org/brianp/TR.html">http://www.mesa3d.org/brianp/TR.html</a>.
 *
 * Copyright (C) 1997-2005 Brian Paul.
 * Licensed under BSD-compatible terms with permission of the author.
 * See LICENSE.txt for license information.
 */
package com.unascribed.blockrenderer.lib;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;

/**
 * A <b>reduced</b> port of Brian Paul's tile rendering library, found at <a href = "http://www.mesa3d.org/brianp/TR.html">
 * http://www.mesa3d.org/brianp/TR.html </a> .
 * <p>
 * Original code Copyright (C) 1997-2005 Brian Paul. Licensed under
 * BSD-compatible terms with permission of the author. See LICENSE.txt
 * for license information.
 * </p>
 * <p>
 * Vastly simplified by AterAnimAvis (Practically a Reimplementation of the original TR Library) <br>
 * See <a href="https://github.com/sgothel/jogl/blob/master/src/jogl/classes/com/jogamp/opengl/util/">https://github.com/sgothel/jogl/blob/master/src/jogl/classes/com/jogamp/opengl/util/</a> for original source code.
 * </p>
 *
 * @author ryanm, sgothel, ateranimavis
 */
public class TileRenderer {

    public final ByteBuffer buffer;

    public final int imageWidth;
    public final int imageHeight;

    private final int tileBorder;
    private final int tileWidth;
    private final int tileHeight;

    private final int tileWidthNB;
    private final int tileHeightNB;

    private final int columns;
    private final int rows;

    private int currentColumn =  0;
    private int currentRow    =  0;
    private int currentTile   = -1;

    private int currentTileWidth;
    private int currentTileHeight;

    public TileRenderer(int width, int height, int tileWidth, int tileHeight, int tileBorder) {
        this.imageWidth  = width;
        this.imageHeight = height;

        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileBorder = tileBorder;

        tileWidthNB = tileWidth - 2 * tileBorder;
        tileHeightNB = tileHeight - 2 * tileBorder;

        columns = (imageWidth  + tileWidthNB  - 1) / tileWidthNB;
        rows    = (imageHeight + tileHeightNB - 1) / tileHeightNB;

        buffer = BufferUtils.createByteBuffer(width * height * 4);
    }

    public static TileRenderer forSize(int size, int tileSize) {
        return forSize(size, size, tileSize, tileSize);
    }

    public static TileRenderer forSize(int width, int height, int tileWidth, int tileHeight) {
        // If tileSize is the same as imageSize then don't add a border
        if (width == tileWidth && height == tileHeight)
            return new TileRenderer(width, height, tileWidth, tileHeight, 0);

        return new TileRenderer(width, height, tileWidth, tileHeight, 4);
    }

    private boolean perspective = false;
    private double  left        = 0;
    private double  right       = 0;
    private double  bottom      = 0;
    private double  top         = 0;
    private double  near        = 0;
    private double  far         = 0;

    private final int[] viewport = new int[4];

    /**
     * Sets the orthographic projection, Must be called before rendering the first tile.
     */
    public void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
        this.perspective = false;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.near = zNear;
        this.far = zFar;
    }

    /**
     * Sets the perspective projection frustum, Must be called before rendering the first tile.
     */
    public void frustum(double left, double right, double bottom, double top, double zNear, double zFar) {
        this.perspective = true;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.near = zNear;
        this.far = zFar;
    }

    public void perspective(double fovy, double aspect, double zNear, double zFar) {
        double ymax = zNear * Math.tan(fovy * Math.PI / 360);
        double xmax = ymax * aspect;
        frustum(-xmax, xmax, -ymax, ymax, zNear, zFar);
    }

    public void beginTile() {
        /* Save user's viewport, will be restored after last tile rendered */
        if (currentTile <= 0) {
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            currentTile = 0;
        }

        /* Which tile (by row and column) we're about to render */
        currentRow    = rows - 1 - (currentTile / columns);
        currentColumn = currentTile % columns;

        /* Compute actual size of this tile with border */
        int th, tw;

        if (currentRow < rows - 1) th = tileHeight;
        else th = imageHeight - (rows - 1) * tileHeightNB + 2 * tileBorder;

        if (currentColumn < columns - 1) tw = tileWidth;
        else tw = imageWidth - (columns - 1) * tileWidthNB + 2 * tileBorder;

        /* Save tile size, with border */
        currentTileHeight = th;
        currentTileWidth  = tw;

        GL11.glViewport(0, 0, tw, th);

        /* Save current matrix mode */
        final int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_PROJECTION );
        GL11.glLoadIdentity();

        /* Compute projection parameters */
        final double l = left + (right - left) * (currentColumn * tileWidthNB - tileBorder) / imageWidth;
        final double r = l + (right - left) * tw / imageWidth;
        final double b = bottom + (top - bottom) * (currentRow * tileHeightNB - tileBorder) / imageHeight;
        final double t = b + (top - bottom) * th / imageHeight;

        if (perspective) GL11.glFrustum(l, r, b, t, near, far);
        else GL11.glOrtho(l, r, b, t, near, far);

        /* Restore user's matrix mode */
        GL11.glMatrixMode(matrixMode);
    }

    public boolean endTile() {
        /* Be sure OpenGL rendering is finished */
        GL11.glFlush();

        /* Save current glPixelStore values */
        int prevRowLength  = GL11.glGetInteger(GL11.GL_PACK_ROW_LENGTH);
        int prevSkipRows   = GL11.glGetInteger(GL11.GL_PACK_SKIP_ROWS);
        int prevSkipPixels = GL11.glGetInteger(GL11.GL_PACK_SKIP_PIXELS);
        int prevAlignment  = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);

        /* Read Pixels */
        final int srcX = tileBorder;
        final int srcY = tileBorder;
        final int srcWidth = currentTileWidth - 2 * tileBorder;
        final int srcHeight = currentTileHeight - 2 * tileBorder;
        final int destX = tileWidthNB  * currentColumn;
        final int destY = tileHeightNB * currentRow;

        /* Setup pixel store for glReadPixels */
        GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, imageWidth);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, destY);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, destX);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);

        /* Read the tile into the final image */
        GL11.glReadPixels(srcX, srcY, srcWidth, srcHeight, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, buffer);

        /* Restore previous glPixelStore Values */
        GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH,  prevRowLength);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS,   prevSkipRows);
        GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, prevSkipPixels);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT,   prevAlignment);

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

}
