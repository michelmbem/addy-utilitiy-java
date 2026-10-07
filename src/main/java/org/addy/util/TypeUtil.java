package org.addy.util;

import java.lang.reflect.Array;
import java.util.List;

public final class TypeUtil {
    private TypeUtil() {}

    @SuppressWarnings("unchecked")
    public static <T> T defaultValue(Class<T> type) {
        return type.isPrimitive()
                ? (T) Array.get(Array.newInstance(type, 1), 0)
                : null;
    }

    public static Class<?> box(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == char.class) return Character.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        throw new IllegalArgumentException("Could not find a boxed version of " + type.getName());
    }

    public static Class<?> unbox(Class<?> type) {
        if (type.isPrimitive()) return type;
        if (type == Boolean.class) return boolean.class;
        if (type == Character.class) return char.class;
        if (type == Byte.class) return byte.class;
        if (type == Short.class) return short.class;
        if (type == Integer.class) return int.class;
        if (type == Long.class) return long.class;
        if (type == Float.class) return float.class;
        if (type == Double.class) return double.class;
        throw new IllegalArgumentException("Could not find an unboxed version of " + type.getName());
    }

    public static boolean isWideningConvertible(Class<?> fromType, Class<?> toType) {
        List<Class<?>> convertibleTypes = List.of(
                byte.class, short.class, int.class, long.class, float.class, double.class);
        int fromTypeIndex = convertibleTypes.indexOf(fromType);
        int toTypeIndex = convertibleTypes.indexOf(toType);
        return fromTypeIndex >= 0 && toTypeIndex >= 0 && fromTypeIndex <= toTypeIndex;
    }

    public static boolean isAssignable(Class<?> fromType, Class<?> toType) {
        return fromType == toType ||
                isWideningConvertible(fromType, toType) ||
                box(toType).isAssignableFrom(box(fromType));
    }
}
