package org.addy.util;

import java.util.Objects;
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

    public static <T> boolean equals(T[] first, T[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (!Objects.equals(first[i], second[i]))
                return false;
        }
        return true;
    }

    public static boolean equals(boolean[] first, boolean[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(char[] first, char[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(byte[] first, byte[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(short[] first, short[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(int[] first, int[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(long[] first, long[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(float[] first, float[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
    }

    public static boolean equals(double[] first, double[] second) {
        if (first == null) return second == null;
        if (second == null || first.length != second.length)
            return false;
        for (int i = 0; i < first.length; ++i) {
            if (first[i] != second[i])
                return false;
        }
        return true;
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

    public static <T> T first(T[] array, T defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Boolean first(boolean[] array, boolean defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Character first(char[] array, char defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Byte first(byte[] array, byte defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Short first(short[] array, short defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Integer first(int[] array, int defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Long first(long[] array, long defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Float first(float[] array, float defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static Double first(double[] array, double defaultValue) {
        return first(array).orElse(defaultValue);
    }

    public static <T> Optional<T> last(T[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Boolean> last(boolean[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Character> last(char[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Byte> last(byte[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Short> last(short[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Integer> last(int[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Long> last(long[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Float> last(float[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static Optional<Double> last(double[] array) {
        return array.length > 0 ? Optional.of(array[array.length - 1]) : Optional.empty();
    }

    public static <T> T last(T[] array, T defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Boolean last(boolean[] array, boolean defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Character last(char[] array, char defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Byte last(byte[] array, byte defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Short last(short[] array, short defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Integer last(int[] array, int defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Long last(long[] array, long defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Float last(float[] array, float defaultValue) {
        return last(array).orElse(defaultValue);
    }

    public static Double last(double[] array, double defaultValue) {
        return last(array).orElse(defaultValue);
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

    public static <T> T requiredLast(T[] array) {
        return last(array).orElseThrow();
    }

    public static boolean requiredLast(boolean[] array) {
        return last(array).orElseThrow();
    }

    public static char requiredLast(char[] array) {
        return last(array).orElseThrow();
    }

    public static byte requiredLast(byte[] array) {
        return last(array).orElseThrow();
    }

    public static short requiredLast(short[] array) {
        return last(array).orElseThrow();
    }

    public static int requiredLast(int[] array) {
        return last(array).orElseThrow();
    }

    public static long requiredLast(long[] array) {
        return last(array).orElseThrow();
    }

    public static float requiredLast(float[] array) {
        return last(array).orElseThrow();
    }

    public static double requiredLast(double[] array) {
        return last(array).orElseThrow();
    }
}
