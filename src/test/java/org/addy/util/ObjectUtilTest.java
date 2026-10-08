package org.addy.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class ObjectUtilTest {

    @Test
    void isEmptyWorks() {
        assertThat(ObjectUtil.isEmpty(null)).isTrue();
        assertThat(ObjectUtil.isEmpty("")).isTrue();
        assertThat(ObjectUtil.isEmpty(new int[0])).isTrue();
        assertThat(ObjectUtil.isEmpty(new double[] {})).isTrue();
        assertThat(ObjectUtil.isEmpty(List.of())).isTrue();
        assertThat(ObjectUtil.isEmpty(Set.of())).isTrue();
        assertThat(ObjectUtil.isEmpty(Map.of())).isTrue();
        assertThat(ObjectUtil.isEmpty(new Wrapper())).isTrue();

        assertThat(ObjectUtil.isEmpty(" ")).isFalse();
        assertThat(ObjectUtil.isEmpty("hello")).isFalse();
        assertThat(ObjectUtil.isEmpty(new int[1])).isFalse();
        assertThat(ObjectUtil.isEmpty(new double[] {3.14, 5})).isFalse();
        assertThat(ObjectUtil.isEmpty(List.of(0, 1, 2))).isFalse();
        assertThat(ObjectUtil.isEmpty(Set.of('a', 'b', 'c'))).isFalse();
        assertThat(ObjectUtil.isEmpty(Map.of(1, "one", 2, "two"))).isFalse();
        assertThat(ObjectUtil.isEmpty(new Wrapper(new Date()))).isFalse();
    }

    @Test
    void requireNonEmptyWorks() {
        assertThatThrownBy(() -> ObjectUtil.requireNonEmpty(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ObjectUtil.requireNonEmpty(new boolean[0]))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ObjectUtil.requireNonEmpty(Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> ObjectUtil.requireNonEmpty(Collections.emptyMap()))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> ObjectUtil.requireNonEmpty(new boolean[2]))
                .doesNotThrowAnyException();
        assertThatCode(() -> ObjectUtil.requireNonEmpty(Collections.singletonList(10)))
                .doesNotThrowAnyException();
        assertThatCode(() -> ObjectUtil.requireNonEmpty(Collections.singletonMap(true, "true")))
                .doesNotThrowAnyException();
    }

    @Test
    void requireNonEmptyElseWorks() {
        assertThat(ObjectUtil.requireNonEmptyElse(null, 16))
                .isEqualTo(16);
        assertThat(ObjectUtil.requireNonEmptyElse("", "Hi"))
                .isEqualTo("Hi");
        assertThat(ObjectUtil.requireNonEmptyElse(new int[0], new int[] {9}))
                .isEqualTo(new int[] {9});
        assertThat(ObjectUtil.requireNonEmptyElse(List.of(), List.of(7, 8, 9)))
                .isEqualTo(List.of(7, 8, 9));
        assertThat(ObjectUtil.requireNonEmptyElse(Map.of(), Map.of(1, 1, 2, 4, 3, 9)))
                .isEqualTo(Map.of(1, 1, 2, 4, 3, 9));

        assertThat(ObjectUtil.requireNonEmptyElse(" ", "Salute"))
                .isEqualTo(" ");
        assertThat(ObjectUtil.requireNonEmptyElse("hello", "monday"))
                .isEqualTo("hello");
        assertThat(ObjectUtil.requireNonEmptyElse(new double[] {3.14, 5}, new double[] {-7, 7}))
                .isEqualTo(new double[] {3.14, 5});
        assertThat(ObjectUtil.requireNonEmptyElse(List.of(0, 1, 2), Arrays.asList(7, 8, 9)))
                .isEqualTo(List.of(0, 1, 2));
        assertThat(ObjectUtil.requireNonEmptyElse(Set.of('a', 'b', 'c'), Set.of('A', 'B', 'C')))
                .isEqualTo(Set.of('a', 'b', 'c'));
        assertThat(ObjectUtil.requireNonEmptyElse(Map.of(1, "one", 2, "two"), Map.of(0, "zero")))
                .isEqualTo(Map.of(1, "one", 2, "two"));
    }

    record Wrapper(Object value) {
        public Wrapper() {
            this(null);
        }

        public boolean isEmpty() {
            return value == null;
        }
    }
}