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

import java.util.List;
import java.util.Map;

public class ReducedColorPalette {

    public final List<Color> palette;
    public final Map<Color, Integer> reductions;

    public ReducedColorPalette(List<Color> palette, Map<Color, Integer> reductions) {
        this.palette = palette;
        this.reductions = reductions;
    }

    public int indexOfClosestColor(Color color) {
        return reductions.computeIfAbsent(color, this::searchClosestColorIndex);
    }

    private int searchClosestColorIndex(Color color) {
        int min = 0;
        int closestIndex = -1;

        // If the color is fully transparent return Special Index 0
        if (color.alpha == 0x00) return 0;

        // Skip the first entry as it's the fully transparent one
        for (int i = 1; i < palette.size(); i++) {
            Color p = palette.get(i);

            if (color == p) return i;

            int d = (int) Math.floor(Math.pow(color.red - p.red, 2) + Math.pow(color.green - p.green, 2) + Math.pow(color.blue - p.blue, 2));

            if (d == 0) return i;

            if (min == 0 || d < min) {
                closestIndex = i;
                min = d;
            }
        }

        return closestIndex;
    }

}
