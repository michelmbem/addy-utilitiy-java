package org.addy.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StringUtil {
	public static final char DEFAULT_PADDING = ' ';
	public static final String DEFAULT_WRAPPER = "'";
	public static final String DEFAULT_DELIMITER = "";
	public static final String DEFAULT_SPLIT_REGEX = "[\\W_]+";
	private static final String DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private StringUtil() {}
	
	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	public static boolean isNumeric(String value) {
		if (value == null) return false;

		try {
			new BigDecimal(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isTemporal(String value, Locale locale) {
		if (value == null) return false;

		try {
			DateUtil.parseDate(value, locale);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static boolean isTemporal(String value) {
		return isTemporal(value, Locale.getDefault());
	}

	public static String padLeft(String value, int length, char padding) {
        var sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.insert(0, padding);
		}

		return sb.toString();
	}

	public static String padLeft(String value, int length) {
		return padLeft(value, length, DEFAULT_PADDING);
	}

	public static String padRight(String value, int length, char padding) {
		var sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.append(padding);
		}

		return sb.toString();
	}

	public static String padRight(String value, int length) {
		return padRight(value, length, DEFAULT_PADDING);
	}

    public static String wrap(String value, String wrapper) {
        return value != null
                ? wrapper + value.replace(wrapper, wrapper + wrapper) + wrapper
                : null;
    }

    public static String wrap(String value) {
        return wrap(value, DEFAULT_WRAPPER);
    }

    public static String unwrap(String value, String wrapper) {
        if (null == value) return null;

        value = value.trim();

        if (!(value.length() >= 2 * wrapper.length() &&
                value.startsWith(wrapper) &&
                value.endsWith(wrapper))) return value;

        return value.substring(wrapper.length(), value.length() - wrapper.length())
                .replace(wrapper + wrapper, wrapper);
    }

    public static String unwrap(String value) {
        return unwrap(value, DEFAULT_WRAPPER);
    }

	public static String camelCase(String value) {
		return isEmpty(value)
				? value
				: value.substring(0, 1).toLowerCase() + value.substring(1);
	}

	public static String pascalCase(String value) {
		return isEmpty(value)
				? value
				: value.substring(0, 1).toUpperCase() + value.substring(1);
	}

	public static String[] splitWords(String value) {
		if (isEmpty(value)) return new String[0];

		String[] words = value.replaceAll("([A-Z])", " $1")
				.split(DEFAULT_SPLIT_REGEX);

		return Stream.of(words)
				.filter(word -> !isBlank(word))
				.map(String::toLowerCase)
				.toArray(String[]::new);
	}
    
    public static <T> String join(T[] values, CharSequence delimiter) {
        return Stream.of(values)
        		.map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }
    
    @SafeVarargs
	public static <T> String join(T... values) {
        return join(values, DEFAULT_DELIMITER);
    }
    
    @SafeVarargs
	public static <T> String joinCamelCase(T... values) {
        if (values == null || values.length == 0)  return "";
        
        return camelCase(String.valueOf(values[0])) + Stream.of(values)
                .skip(1)
                .map(o -> pascalCase(String.valueOf(o)))
                .collect(Collectors.joining());
    }
    
    @SafeVarargs
	public static <T> String joinPascalCase(T... values) {
        if (values == null || values.length == 0) return "";
        
        return Stream.of(values)
                .map(o -> pascalCase(String.valueOf(o)))
                .collect(Collectors.joining());
    }

	public static String splitJoin(String value, String regex, CharSequence delimiter) {
		return value != null ? join(value.split(regex), delimiter) : null;
	}

	public static String splitJoin(String value, CharSequence delimiter) {
		return splitJoin(value, DEFAULT_SPLIT_REGEX, delimiter);
	}

	public static String splitJoinCamelCase(String value, String regex) {
        return value != null ? joinCamelCase(value.split(regex)) : null;
    }

    public static String splitJoinCamelCase(String value) {
        return splitJoinCamelCase(value, DEFAULT_SPLIT_REGEX);
    }

	public static String splitJoinPascalCase(String value, String regex) {
		return value != null ? joinPascalCase(value.split(regex)) : null;
	}

	public static String splitJoinPascalCase(String value) {
		return splitJoinPascalCase(value, DEFAULT_SPLIT_REGEX);
	}

	public static String reverseCase(String value) {
		var sb = new StringBuilder();

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (Character.isUpperCase(c))
                sb.append(Character.toLowerCase(c));
            else if (Character.isLowerCase(c))
                sb.append(Character.toUpperCase(c));
            else
                sb.append(c);
		}

		return sb.toString();
	}

    public static String stripAccents(String value) {
        return value != null
                ? Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}", "")
                : null;
    }
    
    public static String randomString(int length, String alphabet) {
        var sb = new StringBuilder();
        Random random = ThreadLocalRandom.current();
        int max = alphabet.length();
        
        for (int i = 0; i < length; ++i) {
            sb.append(alphabet.charAt(random.nextInt(max)));
        }
        
        return sb.toString();
    }

	public static String randomString(int length) {
		return randomString(length, DEFAULT_ALPHABET);
	}

	public static String requireNonEmpty(String value) {
		if (isEmpty(value)) throw new IllegalArgumentException();
		return value;
	}

	public static String requireNonEmpty(String value, String message) {
		if (isEmpty(value)) throw new IllegalArgumentException(message);
		return value;
	}

	public static String requireNonEmptyElse(String value, String altValue) {
		return isEmpty(value) ? altValue : value;
	}

	public static String requireNonBlank(String value) {
		if (isBlank(value)) throw new IllegalArgumentException();
		return value;
	}

	public static String requireNonBlank(String value, String message) {
		if (isBlank(value)) throw new IllegalArgumentException(message);
		return value;
	}

	public static String requireNonBlankElse(String value, String altValue) {
		return isBlank(value) ? altValue : value;
	}
}
