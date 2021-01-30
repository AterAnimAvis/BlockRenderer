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
import it.unimi.dsi.fastutil.ints.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorCubes {

    public static List<IntSet> divide(IntSet colors) {
        if (colors.size() == 0) return Collections.emptyList();

        Int2IntFunction cut = largestEdge(colors);
        int median = median(colors, cut);
        return divideBy(colors, cut, median);
    }

    public static int average(IntSet colors) {
        int sumR = 0;
        int sumG = 0;
        int sumB = 0;

        for (int color : colors) {
            sumR += Color.r(color);
            sumG += Color.g(color);
            sumB += Color.b(color);
        }

        int size = colors.size();

        return (sumR / size & 0xFF) | (sumG / size & 0xFF) << 8 | (sumB / size & 0xFF) << 16 | 0xFF000000;
    }

    public static Int2IntFunction largestEdge(IntSet colors) {
        int minR = 255;
        int maxR = 0;
        int minG = 255;
        int maxG = 0;
        int minB = 255;
        int maxB = 0;

        for (int color : colors) {
            if (Color.r(color) < minR) minR = Color.r(color);
            if (Color.r(color) > maxR) maxR = Color.r(color);
            if (Color.g(color) < minG) minG = Color.g(color);
            if (Color.g(color) > maxG) maxG = Color.g(color);
            if (Color.b(color) < minB) minB = Color.b(color);
            if (Color.b(color) > maxB) maxB = Color.b(color);
        }

        double diffR = (maxR - minR) * 1.0;
        double diffG = (maxG - minG) * 0.8;
        double diffB = (maxB - minB) * 0.5;

        if (diffG >= diffB) {
            if (diffR >= diffG) {
                return Color::r;
            } else {
                return Color::g;
            }
        } else {
            if (diffR >= diffB) {
                return Color::r;
            } else {
                return Color::b;
            }
        }
    }

    public static int median(IntSet colors, Int2IntFunction cut) {
        IntList components = colors.stream().map(cut).collect(Collectors.toCollection(IntArrayList::new));
        return Selection.selectKthElement(components, (int) Math.floor(components.size() / 2.0) + 1);
    }

    public static List<IntSet> divideBy(IntSet colors, Int2IntFunction cut, int median) {
        IntSet list0 = new IntOpenHashSet();
        IntSet list1 = new IntOpenHashSet();
        colors.forEach((int color) -> {
            if (cut.applyAsInt(color) < median) list0.add(color);
            else list1.add(color);
        });

        if (list0.size() > 0 && list1.size() > 0) return Arrays.asList(list0, list1);

        return Collections.emptyList();
    }

}
