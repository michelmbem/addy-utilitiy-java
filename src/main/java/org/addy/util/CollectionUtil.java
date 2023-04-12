package org.addy.util;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public final class CollectionUtil {
    private CollectionUtil() {}

    public static <T> Optional<T> first(Collection<T> collection) {
        return collection.stream().findFirst();
    }

    public static <T> Optional<T> first(T[] array) {
        return Stream.of(array).findFirst();
    }

    public static <T> T requiredFirst(Collection<T> collection) {
        return collection.stream().findFirst().orElseThrow();
    }

    public static <T> T requiredFirst(T[] array) {
        return Stream.of(array).findFirst().orElseThrow();
    }
}
