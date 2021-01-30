package com.unascribed.blockrenderer.vendor.gif.indexed;

import java.util.List;

public class Selection {

    /**
     * Find the k-th smallest number in a list.
     *
     * @param list Target list.
     * @param k    Number of Target element.
     */
    public static int selectKthElement(List<Integer> list, int k) {
        return list.stream().sorted().skip(k - 1).findFirst().orElseThrow(IllegalStateException::new);
    }

}
