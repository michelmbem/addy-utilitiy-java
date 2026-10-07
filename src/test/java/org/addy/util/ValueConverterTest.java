package org.addy.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ValueConverterTest {
    @Test
    void toIntWorks() {
        assertThat(ValueConverter.toInt(1.5f)).isEqualTo(1);
        assertThat(ValueConverter.toInt(true)).isEqualTo(1);
        assertThat(ValueConverter.toInt(2.8)).isEqualTo(2);
        assertThat(ValueConverter.toInt(new BigInteger("314"))).isEqualTo(314);
        assertThat(ValueConverter.toInt('A')).isEqualTo(65);
        assertThat(ValueConverter.toInt("1985")).isEqualTo(1985);
    }

    @Test
    void toTypeWorks() {
        assertThat(ValueConverter.toType(1.5f, int.class)).isEqualTo(1);
        assertThat(ValueConverter.toType(true, float.class)).isEqualTo(1f);
        assertThat(ValueConverter.toType(2.8, BigDecimal.class)).isEqualTo(new BigDecimal("2.8"));
        assertThat(ValueConverter.toType(new BigInteger("314"), Integer.class)).isEqualTo(314);
        assertThat(ValueConverter.toType("A", ABC.class)).isEqualTo(ABC.A);
        assertThat(ValueConverter.toType(2.5, ABC.class)).isEqualTo(ABC.C); // 2 == C.ordinal()
        assertThat(ValueConverter.toType("1985-12-25", Date.class))
                .isEqualTo(DateUtil.date(1985, 12, 25));
        assertThat(ValueConverter.toType(Instant.parse("1985-12-25T00:00:00Z"), Date.class))
                .isInstanceOf(Date.class);
    }

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
        assertThat(f5.denominator()).isPositive();
        assertThat(f1).hasToString("4");
        assertThat(f5).hasToString("-5/3");
    }

    @Test
    void convertByIntrospection_can_construct() {
        Fraction f1 = new Fraction(2);
        Fraction f2 = (Fraction) ValueConverter.toType(2, Fraction.class);
        assertThat(f2).isEqualTo(f1);
    }

    @Test
    void convertByIntrospection_can_factor() {
        long l = 150L;
        BigInteger bi = ValueConverter.toBigInteger(l);
        assertThat(bi).isEqualTo(BigInteger.valueOf(l));
        BigDecimal bd = ValueConverter.toBigDecimal(bi);
        assertThat(bd).isEqualTo(BigDecimal.valueOf(l));
    }

    @Test
    void convertByIntrospection_can_parse() {
        String repr = "2023-04-14T21:56:30+06:00";
        OffsetDateTime odt1 = OffsetDateTime.parse(repr);
        OffsetDateTime odt2 = (OffsetDateTime) ValueConverter.toType(repr, OffsetDateTime.class);
        assertThat(odt2).isEqualTo(odt1);
    }

    @Test
    void convertByIntrospection_can_invoke_converter_method() {
        OffsetDateTime odt = OffsetDateTime.parse("2023-04-14T21:56:30+06:00");
        LocalDateTime ldt = (LocalDateTime) ValueConverter.toType(odt, LocalDateTime.class);
        LocalDate ld = (LocalDate) ValueConverter.toType(odt, LocalDate.class);
        LocalTime lt = (LocalTime) ValueConverter.toType(odt, LocalTime.class);
        assertThat(ldt).isEqualTo(odt.toLocalDateTime());
        assertThat(ld).isEqualTo(odt.toLocalDate());
        assertThat(lt).isEqualTo(odt.toLocalTime());
    }

    @Test
    void convertByIntrospection_works_on_primitive_types() {
        Fraction f = new Fraction(5, 2);
        double d = ValueConverter.toDouble(f);
        int i = ValueConverter.toInt(f);
        assertThat(d).isEqualTo(f.toDouble());
        assertThat(i).isEqualTo(f.asInt());
    }

    enum ABC { A, B, C }

    record Fraction(int numerator, int denominator) {
        Fraction(int numerator, int denominator) {
            if (denominator == 0)
                throw new IllegalArgumentException("denominator cannot be 0");

            int g = gcd(numerator, denominator);
            this.numerator = numerator / g;
            this.denominator = denominator / g;
        }

        public Fraction(int numerator) {
            this(numerator, 1);
        }

        public double toDouble() {
            return (double) numerator / denominator;
        }

        public int asInt() {
            return numerator / denominator;
        }

        @Override
        public String toString() {
            return denominator == 1 ? String.valueOf(numerator) : numerator + "/" + denominator;
        }

        private static int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }
    }
}
