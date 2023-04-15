package org.addy.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class TypeConverterTest {
    @Test
    void Fraction_works() {
        Fraction f1 = new Fraction(8, 2);
        Fraction f2 = new Fraction(16, 4);
        Fraction f3 = new Fraction(4);
        Fraction f4 = new Fraction(-12, -3);
        Fraction f5 = new Fraction(5, -3);
        assertThat(f2).isEqualTo(f1);
        assertThat(f3).isEqualTo(f1);
        assertThat(f4).isEqualTo(f1);
        assertThat(f5.getDenominator()).isPositive();
        assertThat(f1).hasToString("4");
        assertThat(f5).hasToString("-5/3");
    }

    @Test
    void convertByIntrospection_can_construct() {
        Fraction f1 = new Fraction(2);
        Fraction f2 = (Fraction) TypeConverter.toType(2, Fraction.class);
        assertThat(f2).isEqualTo(f1);
    }

    @Test
    void convertByIntrospection_can_factor() {
        long l = 150L;
        BigInteger bi = TypeConverter.toBigInteger(l);
        assertThat(bi).isEqualTo(BigInteger.valueOf(l));
        BigDecimal bd = TypeConverter.toBigDecimal(bi);
        assertThat(bd).isEqualTo(BigDecimal.valueOf(l));
    }

    @Test
    void convertByIntrospection_can_parse() {
        String repr = "2023-04-14T21:56:30+06:00";
        OffsetDateTime odt1 = OffsetDateTime.parse(repr);
        OffsetDateTime odt2 = (OffsetDateTime) TypeConverter.toType(repr, OffsetDateTime.class);
        assertThat(odt2).isEqualTo(odt1);
    }

    @Test
    void convertByIntrospection_can_invoke_converter_method() {
        OffsetDateTime odt = OffsetDateTime.parse("2023-04-14T21:56:30+06:00");
        LocalDateTime ldt = (LocalDateTime) TypeConverter.toType(odt, LocalDateTime.class);
        LocalDate ld = (LocalDate) TypeConverter.toType(odt, LocalDate.class);
        LocalTime lt = (LocalTime) TypeConverter.toType(odt, LocalTime.class);
        assertThat(ldt).isEqualTo(odt.toLocalDateTime());
        assertThat(ld).isEqualTo(odt.toLocalDate());
        assertThat(lt).isEqualTo(odt.toLocalTime());
    }

    @Test
    void convertByIntrospection_works_on_primitive_types() {
        Fraction f = new Fraction(5, 2);
        double d = TypeConverter.toDouble(f);
        int i = TypeConverter.toInt(f);
        assertThat(d).isEqualTo(f.toDouble());
        assertThat(i).isEqualTo(f.asInt());
    }

    static class Fraction {
        private final int numerator;
        private final int denominator;

        public Fraction(int numerator, int denominator) {
            if (denominator == 0)
                throw new IllegalArgumentException("denominator cannot be 0");

            int g = gcd(numerator, denominator);
            this.numerator  = numerator / g;
            this.denominator = denominator / g;
        }

        public Fraction(int numerator) {
            this(numerator, 1);
        }

        public int getNumerator() {
            return numerator;
        }

        public int getDenominator() {
            return denominator;
        }

        public double toDouble() {
            return (double) numerator / denominator;
        }

        public int asInt() {
            return numerator / denominator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Fraction fraction = (Fraction) o;
            return numerator == fraction.numerator && denominator == fraction.denominator;
        }

        @Override
        public int hashCode() {
            return Objects.hash(numerator, denominator);
        }

        @Override
        public String toString() {
            return denominator == 1 ? String.valueOf(numerator) : numerator + "/" + denominator;
        }

        private static int gcd(int a, int b)
        {
            return b == 0 ? a : gcd(b, a % b);
        }
    }
}
