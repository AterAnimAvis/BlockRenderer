package com.unascribed.blockrenderer.varia;

public interface Maths {

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

        if (nearestPowerOfTwo < maximum && Math.abs(value - nearestPowerOfTwo) < threshold) value = nearestPowerOfTwo;

        return clamp(value, minimum, maximum);
    }

    static int nearestPowerOfTwo(int value) {
        int a = smallestEncompassingPowerOfTwo(value);
        int b = a >> 1;
        return a - value > value - b ? b : a;
    }

    static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        } else {
            return Math.min(num, max);
        }
    }

    static int smallestEncompassingPowerOfTwo(int value) {
        int i = value - 1;
        i = i | i >> 1;
        i = i | i >> 2;
        i = i | i >> 4;
        i = i | i >> 8;
        i = i | i >> 16;
        return i + 1;
    }

}
