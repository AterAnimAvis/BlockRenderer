package com.unascribed.blockrenderer.utils;

import net.minecraft.util.math.MathHelper;

public interface MathUtils {

    static int minimum(int... args) {
        if (args.length < 1) return 0;

        int minimum = args[0];
        for (int arg : args) if (arg < minimum) minimum = arg;
        return minimum;
    }

    static int roundAndClamp(double value, int minimum, int maximum, int threshold) {
        return roundAndClamp((int) value, minimum, maximum, threshold);
    }

    static int roundAndClamp(int value, int minimum, int maximum, int threshold) {
        int nearestPowerOfTwo = nearestPowerOfTwo(value);

        if (nearestPowerOfTwo < maximum && Math.abs(value-nearestPowerOfTwo) < threshold) value = nearestPowerOfTwo;

        return MathHelper.clamp(value, minimum, maximum);
    }

    static int nearestPowerOfTwo(int value) {
        int a = MathHelper.smallestEncompassingPowerOfTwo(value);
        int b = a >> 1;
        return a - value > value - b ? b : a;
    }

}
