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

import java.util.*;
import java.util.stream.Collectors;

public class MedianCutColorReducer {

    public final ReducedColorPalette palette;

    public final int[] paletteData;

    public MedianCutColorReducer(IImage image) {
        this(image, 0xFF);
    }

    public MedianCutColorReducer(IImage image, int maxPaletteSize) {
        Set<Color> colors = removeTransparent(extractColors(image));
        palette = reduceColors(colors, maxPaletteSize);
        paletteData = process();
    }

    private Set<Color> removeTransparent(Set<Color> colors) {
        return colors.stream().filter(color -> color.alpha != 0x00).collect(Collectors.toSet());
    }

    private int[] process() {
        IntegerArrayOutputStream os = new IntegerArrayOutputStream();

        palette.palette.forEach(color -> {
            os.write(color.red);
            os.write(color.green);
            os.write(color.blue);
        });

        return os.toArray();
    }

    public static Set<Color> extractColors(IImage image) {
        Set<Color> colors = new HashSet<>();
        image.forEach(colors::add);
        return colors;
    }

    public static ReducedColorPalette reduceColors(Set<Color> colors, int maxPaletteSize) {
        List<Set<Color>> cubes = MedianCut.medianCut(colors, maxPaletteSize);
        List<Color> palette = new ArrayList<>();
        Map<Color, Integer> reductions = new HashMap<>();

        palette.add(0, new Color(0x00, 0x00, 0x00, 0x00));

        cubes.forEach(cube -> {
            if (cube.size() == 0) throw new IllegalStateException();

            Color average = ColorCubes.average(cube);
            palette.add(average);
            cube.forEach(color -> reductions.put(color, palette.size() - 1));
        });

        return new ReducedColorPalette(palette, reductions);
    }

    public int[] remap(IImage image) {
        IntegerArrayOutputStream os = new IntegerArrayOutputStream();
        image.forEach(color -> os.write(map(color)));
        return os.toArray();
    }

    public int map(Color color) {
        return palette.indexOfClosestColor(color);
    }

}
