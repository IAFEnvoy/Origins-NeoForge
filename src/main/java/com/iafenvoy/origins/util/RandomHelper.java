package com.iafenvoy.origins.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomHelper {
    public static int nextInt(int min, int max) {
        return nextInt(new Random(), min, max);
    }

    public static int nextInt(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static double nextDouble(double min, double max) {
        return nextDouble(new Random(), min, max);
    }

    public static double nextDouble(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    public static double randomize(double origin, double ratio) {
        return rangeRand(origin, Math.abs(origin * ratio));
    }

    public static double rangeRand(double origin, double range) {
        return origin + rangeRand(range);
    }

    public static double rangeRand(double range) {
        return nextDouble(-range, range);
    }

    public static <T> T randomOne(Random random, List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

    public static <T> T randomOne(List<T> list) {
        return randomOne(ThreadLocalRandom.current(), list);
    }

    public static <T> T randomOne(T[] list) {
        return randomOne(List.of(list));
    }
}
