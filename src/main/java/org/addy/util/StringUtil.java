package org.addy.util;

public final class StringUtil {
	
	private StringUtil() {
	}
	
	public static final char DEFAULT_QUOTE = '\'';
	public static final char DEFAULT_PADDING = ' ';
	public static final String DEFAULT_SEPARATOR = ",";
	public static final String DEFAULT_CAMEL_CASE_REGEX = "[\\W_]+";

	public static String quotedString(String value, char quote) {
		String quoteAsStr = String.valueOf(quote);
		String doubleQuote = quoteAsStr + quoteAsStr;
		return value == null
				? doubleQuote
				: quoteAsStr + value.replaceAll(quoteAsStr, doubleQuote) + quoteAsStr;
	}

	public static String quotedString(String value) {
		return quotedString(value, DEFAULT_QUOTE);
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public static boolean isNullOrWhitespace(String value) {
		return value == null || value.matches("^\\s*$");
	}

	public static String padLeft(String value, int length, char padding) {
		StringBuilder sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.insert(0, padding);
		}

		return sb.toString();
	}

	public static String padLeft(String value, int length) {
		return padLeft(value, length, DEFAULT_PADDING);
	}

	public static String padRight(String value, int length, char padding) {
		StringBuilder sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.append(padding);
		}

		return sb.toString();
	}

	public static String padRight(String value, int length) {
		return padRight(value, length, DEFAULT_PADDING);
	}

	public static String join(String[] values, String separator) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < values.length; i++) {
			if (i > 0) sb.append(separator);
			sb.append(values[i]);
		}

		return sb.toString();
	}

	public static String join(String[] values) {
		return join(values, DEFAULT_SEPARATOR);
	}

	public static String toCamelCase(String value) {
		return isNullOrEmpty(value)
				? value
				: String.valueOf(Character.toLowerCase(value.charAt(0))) + value.substring(1);
	}

	public static String toPascalCase(String value) {
		return isNullOrEmpty(value)
				? value
				: String.valueOf(Character.toUpperCase(value.charAt(0))) + value.substring(1);
	}

	public static String joinCamelCase(String[] words) {
		if (words == null || words.length <= 0) return "";

		StringBuilder sb = new StringBuilder(toCamelCase(words[0]));
		for (int i = 1; i < words.length; i++)
			sb.append(toPascalCase(words[i]));
		
		return sb.toString();
	}

	public static String joinCamelCase(String value, String regex) {
		return joinCamelCase(value.split(regex));
	}

	public static String joinCamelCase(String value) {
		return joinCamelCase(value, DEFAULT_CAMEL_CASE_REGEX);
	}

	public static String joinPascalCase(String[] words) {
		if (words == null || words.length <= 0) return "";

		StringBuilder sb = new StringBuilder();
		for (String word : words) {
			sb.append(toPascalCase(word));
		}
		
		return sb.toString();
	}

	public static String joinPascalCase(String value, String regex) {
		return joinPascalCase(value.split(regex));
	}

	public static String joinPascalCase(String value) {
		return joinPascalCase(value, DEFAULT_CAMEL_CASE_REGEX);
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
}
