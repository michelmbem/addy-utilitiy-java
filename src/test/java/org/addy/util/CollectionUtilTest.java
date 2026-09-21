package org.addy.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilTest {
    private static final Collection<String> LIST = List.of("one", "two", "three");

    @Test
    void isEmptyWorks() {
        assertTrue(CollectionUtil.isEmpty(null));
        assertTrue(CollectionUtil.isEmpty(new ArrayList<>()));
        assertTrue(CollectionUtil.isEmpty(new HashSet<>()));
        assertTrue(CollectionUtil.isEmpty(Collections.emptyList()));
        assertTrue(CollectionUtil.isEmpty(Collections.emptySet()));
    }

    @Test
    void firstWorks() {
        assertEquals("one", CollectionUtil.first(LIST).orElse("four"));
    }

    @Test
    void requiredFirstWorks() {
        assertEquals("one", CollectionUtil.requiredFirst(LIST));
        assertThrows(NoSuchElementException.class, () -> CollectionUtil.requiredFirst(Collections.emptyList()));
    }

    @Test
    void toListWorks() {
        assertEquals(LIST, CollectionUtil.toList(LIST));
    }

    @Test
    void toSetWorks() {
        var theSet = new HashSet<>(LIST);
        assertEquals(theSet, CollectionUtil.toSet(LIST));
    }

    @Test
    void concatWorks() {
        var list2 = List.of("four", "five", "six");
        var list3 = List.of("one", "two", "three", "four", "five", "six");
        assertEquals(list3, CollectionUtil.concat(LIST, list2));
    }
}
