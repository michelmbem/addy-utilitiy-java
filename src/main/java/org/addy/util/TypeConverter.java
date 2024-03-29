package org.addy.util;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.*;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class TypeConverter {
    private TypeConverter() {}

    public static boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        if (value instanceof CharSequence) return Boolean.parseBoolean(value.toString());
        return convertByIntrospection(value, boolean.class);
    }

    public static char toChar(Object value) {
        if (value == null) return '\0';
        if (value instanceof Boolean) return (boolean) value ? '1' : '0';
        if (value instanceof Number) return (char) ((Number) value).intValue();

        if (value instanceof CharSequence) {
            String str = value.toString();
            if (str.length() == 1) return  str.charAt(0);
            throw new IllegalArgumentException("The given character sequence is either empty or has more then one character");
        }

        return convertByIntrospection(value, char.class);
    }

    public static byte toByte(Object value) {
        if (value == null) return (byte) 0;
        if (value instanceof Boolean) return (byte) ((boolean) value ? 1 : 0);
        if (value instanceof Number) return ((Number) value).byteValue();
        if (value instanceof CharSequence) return Byte.parseByte(value.toString());
        return convertByIntrospection(value, byte.class);
    }

    public static short toShort(Object value) {
        if (value == null) return (short) 0;
        if (value instanceof Boolean) return (short) ((boolean) value ? 1 : 0);
        if (value instanceof Number) return ((Number) value).shortValue();
        if (value instanceof CharSequence) return Short.parseShort(value.toString());
        return convertByIntrospection(value, short.class);
    }

    public static int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Boolean) return (boolean) value ? 1 : 0;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof CharSequence) return Integer.parseInt(value.toString());
        return convertByIntrospection(value, int.class);
    }

    public static long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Boolean) return (boolean) value ? 1L : 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof CharSequence) return Long.parseLong(value.toString());
        return convertByIntrospection(value, long.class);
    }

    public static float toFloat(Object value) {
        if (value == null) return 0F;
        if (value instanceof Boolean) return (boolean) value ? 1F : 0F;
        if (value instanceof Number) return ((Number) value).floatValue();
        if (value instanceof CharSequence) return Float.parseFloat(value.toString());
        return convertByIntrospection(value, float.class);
    }

    public static double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Boolean) return (boolean) value ? 1.0 : 0.0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof CharSequence) return Double.parseDouble(value.toString());
        return convertByIntrospection(value, double.class);
    }

    public static BigInteger toBigInteger(Object value) {
        if (value == null || value instanceof BigInteger) return (BigInteger) value;
        if (value instanceof Boolean) return (boolean) value ? BigInteger.ONE : BigInteger.ZERO;
        return convertByIntrospection(value, BigInteger.class);
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null || value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Boolean) return (boolean) value ? BigDecimal.ONE : BigDecimal.ZERO;
        return convertByIntrospection(value, BigDecimal.class);
    }

    public static Date toDate(Object value) {
        if (value == null || value instanceof Date) return (Date) value;

        if (value instanceof ZonedDateTime)
            return Date.from(((ZonedDateTime) value).toInstant());

        if (value instanceof OffsetDateTime)
            return Date.from(((OffsetDateTime) value).toInstant());

        if (value instanceof LocalDateTime)
            return Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());

        if (value instanceof LocalDate)
            return Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());

        if (value instanceof OffsetTime)
            return Date.from(((OffsetTime) value).atDate(LocalDate.MIN).toInstant());

        if (value instanceof LocalTime)
            return Date.from(((LocalTime) value).atDate(LocalDate.MIN).atZone(ZoneId.systemDefault()).toInstant());

        if (value instanceof CharSequence) {
            try {
                return DateUtil.parseDate(value.toString());
            } catch (ParseException e) {
                throw new ClassCastException();
            }
        }

        return convertByIntrospection(value, Date.class);
    }

    public static ZonedDateTime toZonedDateTime(Object value) {
        if (value == null || value instanceof ZonedDateTime) return (ZonedDateTime) value;

        if (value instanceof LocalDateTime)
            return ((LocalDateTime) value).atZone(ZoneId.systemDefault());

        if (value instanceof LocalDate)
            return ((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault());

        if (value instanceof OffsetTime)
            return ((OffsetTime) value).atDate(LocalDate.MIN).toZonedDateTime();

        if (value instanceof LocalTime)
            return ((LocalTime) value).atDate(LocalDate.MIN).atZone(ZoneId.systemDefault());

        if (value instanceof Date)
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault());

        return convertByIntrospection(value, ZonedDateTime.class);
    }

    public static OffsetDateTime toOffsetDateTime(Object value) {
        if (value == null || value instanceof OffsetDateTime) return (OffsetDateTime) value;

        if (value instanceof LocalDateTime)
            return ((LocalDateTime) value).atOffset((ZoneOffset) ZoneId.systemDefault());

        if (value instanceof LocalDate)
            return ((LocalDate) value).atStartOfDay().atOffset((ZoneOffset) ZoneId.systemDefault());

        if (value instanceof OffsetTime)
            return ((OffsetTime) value).atDate(LocalDate.MIN);

        if (value instanceof LocalTime)
            return ((LocalTime) value).atDate(LocalDate.MIN).atOffset((ZoneOffset) ZoneId.systemDefault());

        if (value instanceof Date)
            return ((Date) value).toInstant().atOffset((ZoneOffset) ZoneId.systemDefault());

        return convertByIntrospection(value, OffsetDateTime.class);
    }

    public static LocalDateTime toLocalDateTime(Object value) {
        if (value == null || value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof LocalDate) return ((LocalDate) value).atStartOfDay();
        if (value instanceof LocalTime) return ((LocalTime) value).atDate(LocalDate.MIN);

        if (value instanceof Date)
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return convertByIntrospection(value, LocalDateTime.class);
    }

    public static LocalDate toLocalDate(Object value) {
        if (value == null || value instanceof LocalDate) return (LocalDate) value;

        if (value instanceof Date)
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return convertByIntrospection(value, LocalDate.class);
    }

    public static OffsetTime toOffsetTime(Object value) {
        if (value == null || value instanceof OffsetTime) return (OffsetTime) value;

        if (value instanceof Date)
            return ((Date) value).toInstant().atOffset((ZoneOffset) ZoneId.systemDefault()).toOffsetTime();

        return convertByIntrospection(value, OffsetTime.class);
    }

    public static LocalTime toLocalTime(Object value) {
        if (value == null || value instanceof LocalTime) return (LocalTime) value;

        if (value instanceof Date)
            return ((Date) value).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        return convertByIntrospection(value, LocalTime.class);
    }

    public static Object toType(Object value, Class<?> targetType) {
        if (targetType == Boolean.class) return toBoolean(value);
        if (targetType == Byte.class) return toByte(value);
        if (targetType == Short.class) return toShort(value);
        if (targetType == Integer.class) return toInt(value);
        if (targetType == Long.class) return toLong(value);
        if (targetType == Float.class) return toFloat(value);
        if (targetType == Double.class) return toDouble(value);
        if (targetType == BigInteger.class) return toBigInteger(value);
        if (targetType == BigDecimal.class) return toBigDecimal(value);
        if (targetType == Date.class) return toDate(value);
        if (targetType == ZonedDateTime.class) return toZonedDateTime(value);
        if (targetType == OffsetDateTime.class) return toOffsetDateTime(value);
        if (targetType == LocalDateTime.class) return toLocalDateTime(value);
        if (targetType == LocalDate.class) return toLocalDate(value);
        if (targetType == OffsetTime.class) return toOffsetTime(value);
        if (targetType == LocalTime.class) return toLocalTime(value);
        if (targetType == String.class) return String.valueOf(value);
        if (value == null || targetType.isAssignableFrom(value.getClass())) return value;
        return convertByIntrospection(value, targetType);
    }

    private static <T> T convertByIntrospection(Object value, Class<T> targetType) {
        Reference<T> ref = new Reference<>();

        if (constructed(targetType, value, ref) ||
                factored(targetType, value, ref) ||
                parsed(targetType, value, ref) ||
                converted(targetType, value, ref)) return ref.getTarget();

        throw new ClassCastException("Could not cast " + value + " to " + targetType);
    }

    private static <T> boolean constructed(Class<T> targetType, Object value, Reference<T> ref) {
        Constructor<?> constructor = Stream.of(targetType.getConstructors())
                .filter(c -> Modifier.isPublic(c.getModifiers()) &&
                        c.getParameterTypes().length == 1 &&
                        ClassUtils.isAssignable(value.getClass(), c.getParameterTypes()[0], true))
                .findFirst()
                .orElse(null);

        if (constructor != null) {
            try {
                ref.setTarget((T) constructor.newInstance(value));
                return true;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                return false;
            }
        }

        return false;
    }

    private static <T> boolean factored(Class<T> targetType, Object value, Reference<T> ref) {
        Method factoryMethod = Stream.of(targetType.getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) &&
                        Modifier.isStatic(m.getModifiers()) &&
                        ClassUtils.isAssignable(m.getReturnType(), targetType, true) &&
                        m.getParameterTypes().length == 1 &&
                        ClassUtils.isAssignable(value.getClass(), m.getParameterTypes()[0], true))
                .findFirst()
                .orElse(null);

        if (factoryMethod != null) {
            try {
                ref.setTarget((T) factoryMethod.invoke(null, value));
                return true;
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                return false;
            }
        }

        return false;
    }

    private static <T> boolean parsed(Class<T> targetType, Object value, Reference<T> ref) {
        return (value instanceof CharSequence) && factored(targetType, value.toString(), ref);
    }

    private static <T> boolean converted(Class<T> targetType, Object value, Reference<T> ref) {
        Pattern converterMethodName = Pattern.compile(
                String.format("^(to|as|get)%s$", targetType.getSimpleName()),
                Pattern.CASE_INSENSITIVE);

        Method converterMethod = Stream.of(value.getClass().getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) &&
                        !Modifier.isStatic(m.getModifiers()) &&
                        converterMethodName.matcher(m.getName()).find() &&
                        m.getReturnType() == targetType &&
                        m.getParameterTypes().length == 0)
                .findFirst()
                .orElse(null);

        if (converterMethod != null) {
            try {
                ref.setTarget((T) converterMethod.invoke(value));
                return true;
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                return false;
            }
        }

        return false;
    }
}
