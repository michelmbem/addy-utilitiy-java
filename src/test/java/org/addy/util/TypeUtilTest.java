package org.addy.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeUtilTest {

    @Test
    void defaultValueWorks() {
        assertThat(TypeUtil.defaultValue(boolean.class)).isFalse();
        assertThat(TypeUtil.defaultValue(char.class)).isEqualTo('\0');
        assertThat(TypeUtil.defaultValue(byte.class)).isZero();
        assertThat(TypeUtil.defaultValue(short.class)).isZero();
        assertThat(TypeUtil.defaultValue(int.class)).isZero();
        assertThat(TypeUtil.defaultValue(long.class)).isZero();
        assertThat(TypeUtil.defaultValue(float.class)).isZero();
        assertThat(TypeUtil.defaultValue(double.class)).isZero();
        assertThat(TypeUtil.defaultValue(String.class)).isNull();
        assertThat(TypeUtil.defaultValue(BigDecimal.class)).isNull();
        assertThat(TypeUtil.defaultValue(Date.class)).isNull();
    }

    @Test
    void boxWorks() {
        assertThat(TypeUtil.box(boolean.class)).isEqualTo(Boolean.class);
        assertThat(TypeUtil.box(char.class)).isEqualTo(Character.class);
        assertThat(TypeUtil.box(byte.class)).isEqualTo(Byte.class);
        assertThat(TypeUtil.box(short.class)).isEqualTo(Short.class);
        assertThat(TypeUtil.box(int.class)).isEqualTo(Integer.class);
        assertThat(TypeUtil.box(long.class)).isEqualTo(Long.class);
        assertThat(TypeUtil.box(float.class)).isEqualTo(Float.class);
        assertThat(TypeUtil.box(double.class)).isEqualTo(Double.class);
        assertThat(TypeUtil.box(String.class)).isEqualTo(String.class);
        assertThat(TypeUtil.box(BigDecimal.class)).isEqualTo(BigDecimal.class);
        assertThat(TypeUtil.box(Date.class)).isEqualTo(Date.class);
    }

    @Test
    void unboxWorks() {
        assertThat(TypeUtil.unbox(boolean.class)).isEqualTo(boolean.class);
        assertThat(TypeUtil.unbox(int.class)).isEqualTo(int.class);
        assertThat(TypeUtil.unbox(double.class)).isEqualTo(double.class);
        assertThat(TypeUtil.unbox(Boolean.class)).isEqualTo(boolean.class);
        assertThat(TypeUtil.unbox(Character.class)).isEqualTo(char.class);
        assertThat(TypeUtil.unbox(Byte.class)).isEqualTo(byte.class);
        assertThat(TypeUtil.unbox(Short.class)).isEqualTo(short.class);
        assertThat(TypeUtil.unbox(Integer.class)).isEqualTo(int.class);
        assertThat(TypeUtil.unbox(Long.class)).isEqualTo(long.class);
        assertThat(TypeUtil.unbox(Float.class)).isEqualTo(float.class);
        assertThat(TypeUtil.unbox(Double.class)).isEqualTo(double.class);
        assertThatThrownBy(() -> TypeUtil.unbox(String.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TypeUtil.unbox(BigDecimal.class))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TypeUtil.unbox(Date.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isAssignableWorks() {
        assertThat(TypeUtil.isAssignable(boolean.class, boolean.class)).isTrue();
        assertThat(TypeUtil.isAssignable(boolean.class, Boolean.class)).isTrue();
        assertThat(TypeUtil.isAssignable(Boolean.class, boolean.class)).isTrue();
        assertThat(TypeUtil.isAssignable(Boolean.class, Boolean.class)).isTrue();
        assertThat(TypeUtil.isAssignable(int.class, int.class)).isTrue();
        assertThat(TypeUtil.isAssignable(int.class, Integer.class)).isTrue();
        assertThat(TypeUtil.isAssignable(Integer.class, int.class)).isTrue();
        assertThat(TypeUtil.isAssignable(Integer.class, Integer.class)).isTrue();
        assertThat(TypeUtil.isAssignable(short.class, int.class)).isTrue();
        assertThat(TypeUtil.isAssignable(short.class, double.class)).isTrue();
    }
}