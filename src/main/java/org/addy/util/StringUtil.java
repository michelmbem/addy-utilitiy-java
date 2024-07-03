package org.addy.util;

import java.text.Normalizer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StringUtil {
	private StringUtil() {}
	
	public static final char DEFAULT_PADDING_CHAR = ' ';
    public static final String DEFAULT_CONTAINER = "'";
    public static final String DEFAULT_DELIMITER = "";
	public static final String DEFAULT_SPLIT_REGEX = "[\\W_]+";
	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public static boolean isEmpty(String value) {
		return value == null || value.isEmpty();
	}

	public static boolean isBlank(String value) {
		return value == null || value.isBlank();
	}

	public static String padLeft(String value, int length, char paddingChar) {
        StringBuilder sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.insert(0, paddingChar);
		}

		return sb.toString();
	}

	public static String padLeft(String value, int length) {
		return padLeft(value, length, DEFAULT_PADDING_CHAR);
	}

	public static String padRight(String value, int length, char paddingChar) {
		StringBuilder sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.append(paddingChar);
		}

		return sb.toString();
	}

	public static String padRight(String value, int length) {
		return padRight(value, length, DEFAULT_PADDING_CHAR);
	}

    public static String wrap(String value, String container) {
        return value == null
                ? null
                : container + value.replace(container, container + container) + container;
    }

    public static String wrap(String value) {
        return wrap(value, DEFAULT_CONTAINER);
    }

    public static String unwrap(String value, String container) {
        if (null == value) return null;

        value = value.trim();

        if (!(value.length() >= 2 * container.length() &&
                value.startsWith(container) &&
                value.endsWith(container))) return value;

        return value.substring(container.length(), value.length() - container.length())
                .replace(container + container, container);
    }

    public static String unwrap(String value) {
        return unwrap(value, DEFAULT_CONTAINER);
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

    public static String splitJoinCamelCase(String value, String regex) {
        return joinCamelCase(value.split(regex));
    }

    public static String splitJoinCamelCase(String value) {
        return splitJoinCamelCase(value, DEFAULT_SPLIT_REGEX);
    }

	public static String splitJoinPascalCase(String value, String regex) {
		return joinPascalCase(value.split(regex));
	}

	public static String splitJoinPascalCase(String value) {
		return splitJoinPascalCase(value, DEFAULT_SPLIT_REGEX);
	}

	public static String reverseCase(String value) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			if (Character.isUpperCase(c)) {
				sb.append(Character.toLowerCase(c));
			} else if (Character.isLowerCase(c)) {
				sb.append(Character.toUpperCase(c));
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

    public static String removeAccents(String value) {
        return value == null
                ? null
                : Normalizer.normalize(value, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }
    
    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = ThreadLocalRandom.current();
        int max = ALPHABET.length();
        
        for (int i = 0; i < length; ++i) {
            sb.append(ALPHABET.charAt(random.nextInt(max)));
        }
        
        return sb.toString();
    }
}
