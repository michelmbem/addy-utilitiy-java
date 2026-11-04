package org.addy.util;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class CollectionUtil {
    private CollectionUtil() {}

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> Optional<T> first(Collection<T> collection) {
        return collection.stream().findFirst();
    }

    public static <T> T first(Collection<T> collection, T defaultValue) {
        return first(collection).orElse(defaultValue);
    }

    public static <T> Optional<T> first(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).findFirst();
    }

    public static <T> T first(Collection<T> collection, Predicate<T> predicate, T defaultValue) {
        return first(collection, predicate).orElse(defaultValue);
    }

    public static <T> Optional<T> last(Collection<T> collection) {
        return collection.stream().reduce(CollectionUtil::alwaysTakeSecond);
    }

    public static <T> T last(Collection<T> collection, T defaultValue) {
        return last(collection).orElse(defaultValue);
    }

    public static <T> Optional<T> last(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).reduce(CollectionUtil::alwaysTakeSecond);
    }

    public static <T> T last(Collection<T> collection, Predicate<T> predicate, T defaultValue) {
        return last(collection, predicate).orElse(defaultValue);
    }

    public static <T> T requiredFirst(Collection<T> collection) {
        return first(collection).orElseThrow();
    }

    public static <T> T requiredFirst(Collection<T> collection, Predicate<T> predicate) {
        return first(collection, predicate).orElseThrow();
    }

    public static <T> T requiredLast(Collection<T> collection) {
        return last(collection).orElseThrow();
    }

    public static <T> T requiredLast(Collection<T> collection, Predicate<T> predicate) {
        return last(collection, predicate).orElseThrow();
    }

    public static <T> List<T> toList(Collection<T> collection) {
        return new ArrayList<>(collection);
    }

    public static <T> Set<T> toSet(Collection<T> collection) {
        return new HashSet<>(collection);
    }

    @SafeVarargs
    public static <T> List<T> concat(Collection<T>... collections) {
        return collections == null
                ? null
                : Stream.of(collections).flatMap(Collection::stream).toList();
    }

    public static <T> List<T> repeat(T item, int times) {
        ArrayList<T> result = new ArrayList<>(times);
        for (int i = 0; i < times; ++i) {
            result.add(item);
        }
        return result;
    }
    
    private static <T> T alwaysTakeSecond(T first, T second) {
        return second;
    }
}
