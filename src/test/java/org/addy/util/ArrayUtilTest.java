package org.addy.util;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilTest {
    private static String[] ARRAY = {"one", "two", "three"};

    @Test
    void isEmptyWorks() {
        assertTrue(ArrayUtil.isEmpty((byte[]) null));
        assertTrue(ArrayUtil.isEmpty(new Object[0]));
        assertTrue(ArrayUtil.isEmpty(new String[] {}));
        assertTrue(ArrayUtil.isEmpty(new float[0]));
        assertTrue(ArrayUtil.isEmpty(new long[] {}));
    }

    @Test
    void firstWorks() {
        assertEquals("one", ArrayUtil.first(ARRAY).orElse("four"));
    }

    @Test
    void requiredFirstWorks() {
        assertEquals("one", ArrayUtil.requiredFirst(ARRAY));
        assertThrows(NoSuchElementException.class, () -> ArrayUtil.requiredFirst(new Object[0]));
    }
}
