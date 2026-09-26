package org.addy.util;

import java.util.*;
import java.util.function.Predicate;

public final class CollectionUtil {
    private CollectionUtil() {}

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> Optional<T> first(Collection<T> collection) {
        return collection.stream().findFirst();
    }

    public static <T> Optional<T> first(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).findFirst();
    }

    public static <T> T requiredFirst(Collection<T> collection) {
        return first(collection).orElseThrow();
    }

    public static <T> T requiredFirst(Collection<T> collection, Predicate<T> predicate) {
        return first(collection, predicate).orElseThrow();
    }

    public static <T> List<T> toList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static <T> Set<T> toSet(Collection<T> collection) {
        return new HashSet<>(collection);
    }

    @SafeVarargs
    public static <T> List<T> concat(Collection<T> collection1,
                                     Collection<T> collection2,
                                     Collection<T>... otherCollections) {
        ArrayList<T> result = new ArrayList<>(collection1);
        result.addAll(collection2);

        if (otherCollections != null) {
            for (Collection<T> otherCollection : otherCollections) {
                result.addAll(otherCollection);
            }
        }

        return result;
    }

    public static <T> List<T> repeat(T item, int times) {
        ArrayList<T> result = new ArrayList<>(times);
        for (int i = 0; i < times; ++i) {
            result.add(item);
        }
        return result;
    }
}
