package org.addy.util;

import java.util.*;
import java.util.stream.Stream;

public final class CollectionUtil {
    private CollectionUtil() {}

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

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

    public static <T> List<T> toList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static <T> Set<T> toSet(Collection<T> collection) {
        return new HashSet<>(collection);
    }

    public static <T> List<T> concat(Collection<T> collection1, Collection<T> collection2) {
        ArrayList<T> result = new ArrayList<>(collection1);
        result.addAll(collection2);
        return result;
    }

    public static <T> List<T> repeat(T item, int times) {
        ArrayList<T> result = new ArrayList<>(times);
        Collections.fill(result, item);
        return result;
    }
}
