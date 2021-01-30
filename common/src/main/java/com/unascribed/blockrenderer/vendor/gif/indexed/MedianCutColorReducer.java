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

import com.unascribed.blockrenderer.varia.stream.IntegerArrayOutputStream;
import com.unascribed.blockrenderer.vendor.gif.api.Color;
import com.unascribed.blockrenderer.vendor.gif.api.IImage;
import it.unimi.dsi.fastutil.ints.*;

import java.util.List;
import java.util.stream.Collectors;

public class MedianCutColorReducer {

    public final ReducedColorPalette palette;

    public final int[] paletteData;

    public MedianCutColorReducer(IImage image, int maxPaletteSize) {
        IntSet colors = removeTransparent(extractColors(image));
        palette = reduceColors(colors, maxPaletteSize);
        paletteData = process();
    }

    private IntSet removeTransparent(IntSet colors) {
        return colors.stream().filter(color -> Color.a(color) != 0x00).collect(Collectors.toCollection(IntOpenHashSet::new));
    }

    private int[] process() {
        IntegerArrayOutputStream os = new IntegerArrayOutputStream();

        palette.palette.forEach((int color) -> {
            os.write(Color.r(color));
            os.write(Color.g(color));
            os.write(Color.b(color));
        });

        return os.toArray();
    }

    public static IntSet extractColors(IImage image) {
        IntSet colors = new IntOpenHashSet();
        image.forEach(colors::add);
        return colors;
    }

    public static ReducedColorPalette reduceColors(IntSet colors, int maxPaletteSize) {
        List<IntSet> cubes = MedianCut.medianCut(colors, maxPaletteSize);
        IntList palette = new IntArrayList();
        Int2IntMap reductions = new Int2IntOpenHashMap();

        palette.add(0, 0x00000000);

        cubes.forEach(cube -> {
            if (cube.size() == 0) throw new IllegalStateException();

            palette.add(ColorCubes.average(cube));
            cube.forEach((int color) -> reductions.put(color, palette.size() - 1));
        });

        return new ReducedColorPalette(palette, reductions);
    }

    public int[] remap(IImage image) {
        IntegerArrayOutputStream os = new IntegerArrayOutputStream();
        image.forEach(color -> os.write(map(color)));
        return os.toArray();
    }

    public int map(int color) {
        return palette.indexOfClosestColor(color);
    }

}
