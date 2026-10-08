package org.addy.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public final class ObjectUtil {
    private ObjectUtil() {}

    public static boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof CharSequence cs) return cs.isEmpty();
        if (value instanceof Collection<?> col) return col.isEmpty();
        if (value instanceof Map<?, ?> map) return map.isEmpty();
        if (value.getClass().isArray()) return Array.getLength(value) == 0;
        return isEmptyByIntrospection(value);
    }

    public static <T> T requireNonEmpty(T value) {
        if (isEmpty(value))
            throw new IllegalArgumentException("The value should not be empty");

        return value;
    }

    public static <T> T requireNonEmptyElse(T value, T altValue) {
        return isEmpty(value) ? altValue : value;
    }

    private static boolean isEmptyByIntrospection(Object value) {
        try {
            Method isEmptyMethod = value.getClass().getMethod("isEmpty");
            int modifiers = isEmptyMethod.getModifiers();
            if (Modifier.isPublic(modifiers) &&
                    !Modifier.isStatic(modifiers) &&
                    boolean.class == isEmptyMethod.getReturnType())
                return (boolean) isEmptyMethod.invoke(value);
        } catch (NoSuchMethodException |
                 IllegalAccessException |
                 InvocationTargetException ignore) {
        }

        return false;
    }
}
