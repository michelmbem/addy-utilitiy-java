package org.addy.util;

import java.util.Optional;

public final class ArrayUtil {
    private ArrayUtil() {}

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(short[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }

    public static <T> Optional<T> first(T[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Boolean> first(boolean[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Character> first(char[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Byte> first(byte[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Short> first(short[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Integer> first(int[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Long> first(long[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Float> first(float[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static Optional<Double> first(double[] array) {
        return array.length > 0 ? Optional.of(array[0]) : Optional.empty();
    }

    public static <T> T requiredFirst(T[] array) {
        return first(array).orElseThrow();
    }

    public static boolean requiredFirst(boolean[] array) {
        return first(array).orElseThrow();
    }

    public static char requiredFirst(char[] array) {
        return first(array).orElseThrow();
    }

    public static byte requiredFirst(byte[] array) {
        return first(array).orElseThrow();
    }

    public static short requiredFirst(short[] array) {
        return first(array).orElseThrow();
    }

    public static int requiredFirst(int[] array) {
        return first(array).orElseThrow();
    }

    public static long requiredFirst(long[] array) {
        return first(array).orElseThrow();
    }

    public static float requiredFirst(float[] array) {
        return first(array).orElseThrow();
    }

    public static double requiredFirst(double[] array) {
        return first(array).orElseThrow();
    }
}
