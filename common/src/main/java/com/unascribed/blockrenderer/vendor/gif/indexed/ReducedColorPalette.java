/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013, 2016 NOBUOKA Yu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.unascribed.blockrenderer.vendor.gif.indexed;

import com.unascribed.blockrenderer.vendor.gif.api.Color;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntList;

public class ReducedColorPalette {

    public final IntList palette;
    public final Int2IntMap reductions;

    public ReducedColorPalette(IntList palette, Int2IntMap reductions) {
        this.palette = palette;
        this.reductions = reductions;
    }

    public int indexOfClosestColor(int color) {
        return reductions.computeIfAbsent(color, this::searchClosestColorIndex);
    }

    private int searchClosestColorIndex(int color) {
        int min = 0;
        int closestIndex = -1;

        // If the color is fully transparent return Special Index 0
        if (Color.a(color) == 0x00) return 0;

        // Skip the first entry as it's the fully transparent one
        for (int i = 1; i < palette.size(); i++) {
            int p = palette.getInt(i);

            if (color == p) return i;

            int cr = Color.r(color);
            int cg = Color.g(color);
            int cb = Color.b(color);

            int pr = Color.r(p);
            int pg = Color.g(p);
            int pb = Color.b(p);

            int d = (int) Math.floor(Math.pow(cr - pr, 2) + Math.pow(cg - pg, 2) + Math.pow(cb - pb, 2));

            if (d == 0) return i;

            if (min == 0 || d < min) {
                closestIndex = i;
                min = d;
            }
        }

        return closestIndex;
    }

}
