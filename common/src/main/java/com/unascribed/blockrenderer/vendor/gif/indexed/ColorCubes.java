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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ColorCubes {

    public static List<Set<Color>> divide(Set<Color> colors) {
        if (colors.size() == 0) return Collections.emptyList();

        Function<Color, Integer> cut = largestEdge(colors);
        Integer median = median(colors, cut);
        return divideBy(colors, cut, median);
    }

    public static Color average(Set<Color> colors) {
        int sumR = 0;
        int sumG = 0;
        int sumB = 0;

        for (Color color : colors) {
            sumR += color.red;
            sumG += color.green;
            sumB += color.blue;
        }

        int size = colors.size();
        return new Color(sumR / size, sumG / size, sumB / size);
    }

    public static Function<Color, Integer> largestEdge(Set<Color> colors) {
        int minR = 255;
        int maxR = 0;
        int minG = 255;
        int maxG = 0;
        int minB = 255;
        int maxB = 0;

        for (Color color : colors) {
            if (color.red < minR) minR = color.red;
            if (color.red > maxR) maxR = color.red;
            if (color.green < minG) minG = color.green;
            if (color.green > maxG) maxG = color.green;
            if (color.blue < minB) minB = color.blue;
            if (color.blue > maxB) maxB = color.blue;
        }

        double diffR = (maxR - minR) * 1.0;
        double diffG = (maxG - minG) * 0.8;
        double diffB = (maxB - minB) * 0.5;

        if (diffG >= diffB) {
            if (diffR >= diffG) {
                return color -> color.red;
            } else {
                return color -> color.green;
            }
        } else {
            if (diffR >= diffB) {
                return color -> color.red;
            } else {
                return color -> color.blue;
            }
        }
    }

    public static Integer median(Set<Color> colors, Function<Color, Integer> cut) {
        List<Integer> components = colors.stream().map(cut).collect(Collectors.toList());
        return Selection.selectKthElement(components, (int) Math.floor(components.size() / 2.0) + 1);
    }

    public static List<Set<Color>> divideBy(Set<Color> colors, Function<Color, Integer> cut, Integer median) {
        Set<Color> list0 = new HashSet<>();
        Set<Color> list1 = new HashSet<>();
        colors.forEach(color -> {
            if (cut.apply(color) < median) list0.add(color);
            else list1.add(color);
        });

        if (list0.size() > 0 && list1.size() > 0) return Arrays.asList(list0, list1);

        return Collections.emptyList();
    }

}
