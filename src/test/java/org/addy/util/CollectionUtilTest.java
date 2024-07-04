package org.addy.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilTest {
    private static String[] ARRAY = {"one", "two", "three"};
    private static Collection<String> LIST = Arrays.asList(ARRAY);

    @Test
    void isEmptyWorks() {
        assertTrue(CollectionUtil.isEmpty((Collection<?>) null));
        assertTrue(CollectionUtil.isEmpty((Object[]) null));
        assertTrue(CollectionUtil.isEmpty(new ArrayList<>()));
        assertTrue(CollectionUtil.isEmpty(new HashSet<>()));
        assertTrue(CollectionUtil.isEmpty(Collections.emptyList()));
        assertTrue(CollectionUtil.isEmpty(Collections.emptySet()));
        assertTrue(CollectionUtil.isEmpty(new Object[0]));
        assertTrue(CollectionUtil.isEmpty(new String[] {}));
    }

    @Test
    void firstWorks() {
        assertEquals("one", CollectionUtil.first(ARRAY).orElse("four"));
        assertEquals("one", CollectionUtil.first(LIST).orElse("four"));
    }

    @Test
    void requiredFirstWorks() {
        assertEquals("one", CollectionUtil.requiredFirst(ARRAY));
        assertEquals("one", CollectionUtil.requiredFirst(LIST));
        assertThrows(NoSuchElementException.class, () -> CollectionUtil.requiredFirst(new Object[0]));
        assertThrows(NoSuchElementException.class, () -> CollectionUtil.requiredFirst(Collections.emptyList()));
    }

    @Test
    void toListWorks() {
        assertEquals(LIST, CollectionUtil.toList(LIST));
    }

    @Test
    void toSetWorks() {
        var theSet = Stream.of(ARRAY).collect(Collectors.toSet());
        assertEquals(theSet, CollectionUtil.toSet(LIST));
    }

    @Test
    void concatWorks() {
        var list2 = Arrays.asList("four", "five", "six");
        var list3 = Arrays.asList("one", "two", "three", "four", "five", "six");
        assertEquals(list3, CollectionUtil.concat(LIST, list2));
    }
}
