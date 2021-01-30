package com.unascribed.blockrenderer.vendor.gif.indexed;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectionTest {

    @Test
    void selectKthElementOrdered() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        for (int i = 1; i < 10; i++) {
            assertEquals(i, Selection.selectKthElement(list, i));
        }
    }

    @Test
    void selectKthElementUnordered() {
        List<Integer> list = Arrays.asList(4, 8, 2, 9, 7, 1, 5, 3, 6);
        for (int i = 1; i < 10; i++) {
            assertEquals(i, Selection.selectKthElement(list, i));
        }
    }
}