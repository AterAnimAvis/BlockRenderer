package com.unascribed.blockrenderer.vendor.gif.indexed;

import com.unascribed.blockrenderer.vendor.gif.impl.Image;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class MedianCutColorReducerTest {

    @SuppressWarnings("PointlessArithmeticExpression")
    int[] indexToColorArray(int[] paletteData, int index) {
        return new int[]{paletteData[index * 3 + 0], paletteData[index * 3 + 1], paletteData[index * 3 + 2]};
    }

    @Test
    void simpleColors() {
        Image imageData = new Image(2, 2, new int[]{
                0xFF000000, 0xFF000000,
                0xFFFFFFFF, 0xFFFFFFFF,
        });

        MedianCutColorReducer reducer = new MedianCutColorReducer(imageData, 2);

        assertArrayEquals(new int[]{0x00, 0x00, 0x00}, indexToColorArray(reducer.paletteData, reducer.map(0xFF000000)));
        assertArrayEquals(new int[]{0xFF, 0xFF, 0xFF}, indexToColorArray(reducer.paletteData, reducer.map(0xFFFFFFFF)));
    }

    @Test
    void reducedColors() {
        Image imageData = new Image(2, 2, new int[]{
                0xFF000000, 0xFF200000,
                0xFF100000, 0xFFFF0000,
        });

        MedianCutColorReducer reducer = new MedianCutColorReducer(imageData, 2);

        assertArrayEquals(new int[]{0x08, 0x00, 0x00}, indexToColorArray(reducer.paletteData, reducer.map(0xFF000000)));
        assertArrayEquals(new int[]{0x8F, 0x00, 0x00}, indexToColorArray(reducer.paletteData, reducer.map(0xFFFF0000)));
    }

}