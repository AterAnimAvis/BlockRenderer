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

import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MedianCut {

    public static List<IntSet> medianCut(IntSet colors, int max) {
        return divideCubesUntil(Collections.singletonList(colors), max);
    }

    public static List<IntSet> divideCubesUntil(List<IntSet> cubes, int limit) {
        while (true) {
            if (cubes.size() >= limit) break;

            IntSet largestCube = getLargestCube(cubes);
            List<IntSet> divided = ColorCubes.divide(largestCube);
            if (divided.size() < 2) break;
            cubes = Stream.concat(cubes.stream().filter((c) -> !c.equals(largestCube)), divided.stream()).collect(Collectors.toList());
        }

        return cubes;
    }

    public static IntSet getLargestCube(List<IntSet> cubes) {
        return cubes.stream().reduce((a, b) -> a.size() > b.size() ? a : b).orElseThrow(IllegalStateException::new);
    }

}
