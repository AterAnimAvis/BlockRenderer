package com.unascribed.blockrenderer.vendor.gif.indexed;

import com.unascribed.blockrenderer.vendor.gif.api.Color;
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
        Image imageData = new Image(2, 2, new Color[]{
                new Color(0x00, 0x00, 0x00), new Color(0x00, 0x00, 0x00),
                new Color(0xFF, 0xFF, 0xFF), new Color(0xFF, 0xFF, 0xFF),
        });

        MedianCutColorReducer reducer = new MedianCutColorReducer(imageData, 2);

        assertArrayEquals(new int[]{0x00, 0x00, 0x00}, indexToColorArray(reducer.paletteData, reducer.map(new Color(0x00, 0x00, 0x00))));
        assertArrayEquals(new int[]{0xFF, 0xFF, 0xFF}, indexToColorArray(reducer.paletteData, reducer.map(new Color(0xFF, 0xFF, 0xFF))));
    }

    @Test
    void reducedColors() {
        Image imageData = new Image(2, 2, new Color[]{
                new Color(0x00, 0x00, 0x00), new Color(0x20, 0x00, 0x00),
                new Color(0x10, 0x00, 0x00), new Color(0xFF, 0x00, 0x00),
        });

        MedianCutColorReducer reducer = new MedianCutColorReducer(imageData, 2);

        assertArrayEquals(new int[]{0x08, 0x00, 0x00}, indexToColorArray(reducer.paletteData, reducer.map(new Color(0x00, 0x00, 0x00))));
        assertArrayEquals(new int[]{0x8F, 0x00, 0x00}, indexToColorArray(reducer.paletteData, reducer.map(new Color(0xFF, 0x00, 0x00))));
    }

}