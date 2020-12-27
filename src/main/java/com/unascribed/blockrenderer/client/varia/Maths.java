package com.unascribed.blockrenderer.client.varia;

import com.unascribed.blockrenderer.client.api.vendor.joml.Matrix4dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector3dc;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector4d;
import com.unascribed.blockrenderer.client.api.vendor.joml.Vector4dc;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        return MathHelper.clamp(value, minimum, maximum);
    }

    static int nearestPowerOfTwo(int value) {
        int a = MathHelper.smallestEncompassingPowerOfTwo(value);
        int b = a >> 1;
        return a - value > value - b ? b : a;
    }

    static Vector4dc getProjectedBounds(Matrix4dc modelView, Vector3dc bounds) {
        List<Vector4d> corners = getCorners(bounds);
        List<Vector4d> projectedCorners = corners.stream().map(modelView::transform).collect(Collectors.toList());

        Vector4d minWorld = new Vector4d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, 0.0f);
        Vector4d maxWorld = new Vector4d(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE, 1.0f);

        for (Vector4d point : projectedCorners) {
            if (point.x < minWorld.x) minWorld.x = point.x;
            if (point.y < minWorld.y) minWorld.y = point.y;
            if (point.z < minWorld.z) minWorld.z = point.z;
            if (point.x > maxWorld.x) maxWorld.x = point.x;
            if (point.y > maxWorld.y) maxWorld.y = point.y;
            if (point.z > maxWorld.z) maxWorld.z = point.z;
        }

        return maxWorld.sub(minWorld);
    }

    static List<Vector4d> getCorners(Vector3dc max) {
        List<Vector4d> result = new ArrayList<>();

        //@formatter:off
        result.add(new Vector4d(      0,       0,       0, 1.0f));
        result.add(new Vector4d(      0,       0, max.z(), 1.0f));
        result.add(new Vector4d(      0, max.y(),       0, 1.0f));
        result.add(new Vector4d(      0, max.y(), max.z(), 1.0f));
        result.add(new Vector4d(max.x(),       0,       0, 1.0f));
        result.add(new Vector4d(max.x(),       0, max.z(), 1.0f));
        result.add(new Vector4d(max.x(), max.y(),       0, 1.0f));
        result.add(new Vector4d(max.x(), max.y(), max.z(), 1.0f));
        //@formatter:on

        return result;
    }

}
