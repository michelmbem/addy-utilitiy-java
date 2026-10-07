package org.addy.util;

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

public final class ValueConverter {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();
    private static final ZoneOffset DEFAULT_ZONE_OFFSET = OffsetTime.now(DEFAULT_ZONE_ID).getOffset();
    
    private ValueConverter() {}

    public static boolean toBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean) return (boolean) value;
        if (value instanceof Number number) return number.intValue() != 0;
        if (value instanceof CharSequence) return Boolean.parseBoolean(value.toString());
        return convertByIntrospection(value, boolean.class);
    }

    public static char toChar(Object value) {
        if (value == null) return '\0';
        if (value instanceof Character chr) return chr;
        if (value instanceof Boolean) return (boolean) value ? '1' : '0';
        if (value instanceof Number number) return (char) number.intValue();

        if (value instanceof CharSequence) {
            String str = value.toString();
            if (str.length() == 1) return  str.charAt(0);
            throw new IllegalArgumentException(
                    "The given character sequence is either empty or has more then one character");
        }

        return convertByIntrospection(value, char.class);
    }

    public static byte toByte(Object value) {
        if (value == null) return (byte) 0;
        if (value instanceof Boolean) return (byte) ((boolean) value ? 1 : 0);
        if (value instanceof Character chr) return (byte) chr.charValue();
        if (value instanceof Number number) return number.byteValue();
        if (value instanceof CharSequence) return Byte.parseByte(value.toString());
        if (value instanceof Enum<?> en) return (byte) en.ordinal();
        return convertByIntrospection(value, byte.class);
    }

    public static short toShort(Object value) {
        if (value == null) return (short) 0;
        if (value instanceof Boolean) return (short) ((boolean) value ? 1 : 0);
        if (value instanceof Character chr) return (short) chr.charValue();
        if (value instanceof Number number) return number.shortValue();
        if (value instanceof CharSequence) return Short.parseShort(value.toString());
        if (value instanceof Enum<?> en) return (short) en.ordinal();
        return convertByIntrospection(value, short.class);
    }

    public static int toInt(Object value) {
        if (value == null) return 0;
        if (value instanceof Boolean) return (boolean) value ? 1 : 0;
        if (value instanceof Character chr) return chr;
        if (value instanceof Number number) return number.intValue();
        if (value instanceof CharSequence) return Integer.parseInt(value.toString());
        if (value instanceof Enum<?> en) return en.ordinal();
        return convertByIntrospection(value, int.class);
    }

    public static long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Boolean) return (boolean) value ? 1L : 0L;
        if (value instanceof Character chr) return chr;
        if (value instanceof Number number) return number.longValue();
        if (value instanceof CharSequence) return Long.parseLong(value.toString());
        if (value instanceof Enum<?> en) return en.ordinal();
        return convertByIntrospection(value, long.class);
    }

    public static float toFloat(Object value) {
        if (value == null) return 0F;
        if (value instanceof Boolean) return (boolean) value ? 1F : 0F;
        if (value instanceof Character chr) return chr;
        if (value instanceof Number number) return number.floatValue();
        if (value instanceof CharSequence) return Float.parseFloat(value.toString());
        if (value instanceof Enum<?> en) return en.ordinal();
        return convertByIntrospection(value, float.class);
    }

    public static double toDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Boolean) return (boolean) value ? 1.0 : 0.0;
        if (value instanceof Character chr) return chr;
        if (value instanceof Number number) return number.doubleValue();
        if (value instanceof CharSequence) return Double.parseDouble(value.toString());
        if (value instanceof Enum<?> en) return en.ordinal();
        return convertByIntrospection(value, double.class);
    }

    public static BigInteger toBigInteger(Object value) {
        if (value == null || value instanceof BigInteger) return (BigInteger) value;
        if (value instanceof Boolean) return (boolean) value ? BigInteger.ONE : BigInteger.ZERO;
        if (value instanceof Character chr) return BigInteger.valueOf(chr);
        if (value instanceof Number number) return BigInteger.valueOf(number.longValue());
        if (value instanceof Enum<?> en) return BigInteger.valueOf(en.ordinal());
        return convertByIntrospection(value, BigInteger.class);
    }

    public static BigDecimal toBigDecimal(Object value) {
        if (value == null || value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof BigInteger bi) return new BigDecimal(bi); // to prevent the case of 'value instanceof Number'
        if (value instanceof Boolean) return (boolean) value ? BigDecimal.ONE : BigDecimal.ZERO;
        if (value instanceof Character chr) return BigDecimal.valueOf(chr);
        if (value instanceof Number number) return BigDecimal.valueOf(number.doubleValue());
        if (value instanceof Enum<?> en) return BigDecimal.valueOf(en.ordinal());
        return convertByIntrospection(value, BigDecimal.class);
    }

    public static Date toDate(Object value) {
        if (value == null || value instanceof Date) return (Date) value;
        if (value instanceof ZonedDateTime zdt) return Date.from(zdt.toInstant());
        if (value instanceof OffsetDateTime odt) return Date.from(odt.toInstant());

        if (value instanceof LocalDateTime ldt)
            return Date.from(ldt.atZone(DEFAULT_ZONE_ID).toInstant());

        if (value instanceof LocalDate ld)
            return Date.from(ld.atStartOfDay(DEFAULT_ZONE_ID).toInstant());

        if (value instanceof OffsetTime ot)
            return Date.from(ot.atDate(LocalDate.MIN).toInstant());

        if (value instanceof LocalTime lt)
            return Date.from(lt.atDate(LocalDate.MIN).atZone(DEFAULT_ZONE_ID).toInstant());

        if (value instanceof CharSequence) {
            try {
                return DateUtil.parseDate(value.toString());
            } catch (ParseException e) {
                throw new ClassCastException();
            }
        }

        return convertByIntrospection(value, Date.class);
    }

    public static Instant toInstant(Object value) {
        if (value == null || value instanceof Instant) return (Instant) value;
        if (value instanceof LocalDateTime ldt) return ldt.toInstant(DEFAULT_ZONE_OFFSET);
        if (value instanceof LocalDate ld) return ld.atStartOfDay().toInstant(DEFAULT_ZONE_OFFSET);
        if (value instanceof OffsetTime ot) return ot.atDate(LocalDate.MIN).toInstant();
        if (value instanceof LocalTime lt) return lt.atDate(LocalDate.MIN).toInstant(DEFAULT_ZONE_OFFSET);
        return convertByIntrospection(value, Instant.class);
    }

    public static ZonedDateTime toZonedDateTime(Object value) {
        if (value == null || value instanceof ZonedDateTime) return (ZonedDateTime) value;
        if (value instanceof Instant instant) return instant.atZone(DEFAULT_ZONE_ID);
        if (value instanceof LocalDateTime ldt) return ldt.atZone(DEFAULT_ZONE_ID);
        if (value instanceof LocalDate ld) return ld.atStartOfDay().atZone(DEFAULT_ZONE_ID);
        if (value instanceof OffsetTime ot) return ot.atDate(LocalDate.MIN).toZonedDateTime();
        if (value instanceof LocalTime lt) return lt.atDate(LocalDate.MIN).atZone(DEFAULT_ZONE_ID);
        if (value instanceof Date d) return d.toInstant().atZone(DEFAULT_ZONE_ID);
        return convertByIntrospection(value, ZonedDateTime.class);
    }

    public static OffsetDateTime toOffsetDateTime(Object value) {
        if (value == null || value instanceof OffsetDateTime) return (OffsetDateTime) value;
        if (value instanceof Instant instant) return instant.atOffset(DEFAULT_ZONE_OFFSET);
        if (value instanceof LocalDateTime ldt) return ldt.atOffset(DEFAULT_ZONE_OFFSET);
        if (value instanceof LocalDate ld) return ld.atStartOfDay().atOffset(DEFAULT_ZONE_OFFSET);
        if (value instanceof OffsetTime ot) return ot.atDate(LocalDate.MIN);
        if (value instanceof LocalTime lt) return lt.atDate(LocalDate.MIN).atOffset(DEFAULT_ZONE_OFFSET);
        if (value instanceof Date d) return d.toInstant().atOffset(DEFAULT_ZONE_OFFSET);
        return convertByIntrospection(value, OffsetDateTime.class);
    }

    public static LocalDateTime toLocalDateTime(Object value) {
        if (value == null || value instanceof LocalDateTime) return (LocalDateTime) value;
        if (value instanceof Instant instant) return instant.atZone(DEFAULT_ZONE_ID).toLocalDateTime();
        if (value instanceof LocalDate ld) return ld.atStartOfDay();
        if (value instanceof LocalTime lt) return lt.atDate(LocalDate.MIN);
        if (value instanceof Date d) return d.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
        return convertByIntrospection(value, LocalDateTime.class);
    }

    public static LocalDate toLocalDate(Object value) {
        if (value == null || value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof Instant instant) return instant.atZone(DEFAULT_ZONE_ID).toLocalDate();
        if (value instanceof Date d) return d.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
        return convertByIntrospection(value, LocalDate.class);
    }

    public static OffsetTime toOffsetTime(Object value) {
        if (value == null || value instanceof OffsetTime) return (OffsetTime) value;
        if (value instanceof Instant instant) return instant.atOffset(DEFAULT_ZONE_OFFSET).toOffsetTime();
        if (value instanceof Date d) return d.toInstant().atOffset(DEFAULT_ZONE_OFFSET).toOffsetTime();
        return convertByIntrospection(value, OffsetTime.class);
    }

    public static LocalTime toLocalTime(Object value) {
        if (value == null || value instanceof LocalTime) return (LocalTime) value;
        if (value instanceof Instant instant) return instant.atZone(DEFAULT_ZONE_ID).toLocalTime();
        if (value instanceof Date d) return d.toInstant().atZone(DEFAULT_ZONE_ID).toLocalTime();
        return convertByIntrospection(value, LocalTime.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, Object value) {
        if (value == null || value.getClass() == enumType) return (T) value;
        if (value instanceof Number number) return enumType.getEnumConstants()[number.intValue()];
        if (value instanceof CharSequence) return Enum.valueOf(enumType, value.toString());
        return convertByIntrospection(value, enumType);
    }

    @SuppressWarnings("unchecked")
    public static Object toType(Object value, Class<?> targetType) {
        Class<?> boxedType = TypeUtil.box(targetType);
        if (boxedType == Boolean.class) return toBoolean(value);
        if (boxedType == Character.class) return toChar(value);
        if (boxedType == Byte.class) return toByte(value);
        if (boxedType == Short.class) return toShort(value);
        if (boxedType == Integer.class) return toInt(value);
        if (boxedType == Long.class) return toLong(value);
        if (boxedType == Float.class) return toFloat(value);
        if (boxedType == Double.class) return toDouble(value);
        if (boxedType == BigInteger.class) return toBigInteger(value);
        if (boxedType == BigDecimal.class) return toBigDecimal(value);
        if (boxedType == Date.class) return toDate(value);
        if (boxedType == Instant.class) return toInstant(value);
        if (boxedType == ZonedDateTime.class) return toZonedDateTime(value);
        if (boxedType == OffsetDateTime.class) return toOffsetDateTime(value);
        if (boxedType == LocalDateTime.class) return toLocalDateTime(value);
        if (boxedType == LocalDate.class) return toLocalDate(value);
        if (boxedType == OffsetTime.class) return toOffsetTime(value);
        if (boxedType == LocalTime.class) return toLocalTime(value);
        if (boxedType == String.class) return String.valueOf(value);
        if (boxedType.isEnum()) return toEnum((Class<? extends Enum>) boxedType, value);
        if (value == null || boxedType.isAssignableFrom(value.getClass())) return value;
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

    @SuppressWarnings("unchecked")
    private static <T> boolean constructed(Class<T> targetType, Object value, Reference<T> ref) {
        Constructor<?> constructor = Stream.of(targetType.getConstructors())
                .filter(c -> Modifier.isPublic(c.getModifiers()) &&
                        c.getParameterTypes().length == 1 &&
                        TypeUtil.isAssignable(value.getClass(), c.getParameterTypes()[0]))
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

    @SuppressWarnings("unchecked")
    private static <T> boolean factored(Class<T> targetType, Object value, Reference<T> ref) {
        Method factoryMethod = Stream.of(targetType.getMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) &&
                        Modifier.isStatic(m.getModifiers()) &&
                        TypeUtil.isAssignable(m.getReturnType(), targetType) &&
                        m.getParameterTypes().length == 1 &&
                        TypeUtil.isAssignable(value.getClass(), m.getParameterTypes()[0]))
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

    @SuppressWarnings("unchecked")
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
