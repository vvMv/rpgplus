package com.vmv.core.math;

import java.util.Random;

public class MathUtils {
    public static double round(double value, int precision) {
        double scale = Math.pow(10, precision);
        return Math.round(value * scale) / scale;
    }

    public static int getRandom(int max, int min) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static double getRandom(double max, double min) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static double getRandomPercentage() {
        return getRandom(100, 0);
    }

}
