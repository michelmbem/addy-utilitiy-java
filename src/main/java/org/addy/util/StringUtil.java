package org.addy.util;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class StringUtil {
	private StringUtil() {}
	
	public static final char DEFAULT_QUOTE = '\'';
	public static final char DEFAULT_PAD_CHAR = ' ';
	public static final String DEFAULT_DELIMITER = ",";
	public static final String DEFAULT_SPLIT_REGEX = "[\\W_]+";
	
	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String ACCENTS = "ÀÁÂÃÄÅÇÈÉÊËÌÍÎÏÒÓÔÕÖÙÚÛÜÝàáâãäåçèéêëìíîïðòóôõöùúûüýÿ";
    private static final String NO_ACCENT = "AAAAAACEEEEIIIIOOOOOUUUUYaaaaaaceeeeiiiioooooouuuuyy";
    private static final String ACCEPTED_SPECIAL_CHARS = ".-_";
    
	public static String quote(String value, char quoteChar) {
		String quoteString = String.valueOf(quoteChar);
		String doubleQuote = quoteString + quoteString;
		return value == null
				? doubleQuote
				: quoteString + value.replace(quoteString, doubleQuote) + quoteString;
	}

	public static String quote(String value) {
		return quote(value, DEFAULT_QUOTE);
	}
	
    public static String unquote(String value, char quoteChar) {
        if (null == value) return null;
        
        value = value.trim();
        int length = value.length();
        
        if (length < 2 || value.charAt(0) != quoteChar ||
    		value.charAt(length - 1) != quoteChar) return value;
    
        StringBuilder sb = new StringBuilder();
        boolean allowQuote = false;
        
        for (int i = 1; i < length - 1; ++i) {
            char c = value.charAt(i);
            if (c == quoteChar) {
                if (allowQuote) {
                    sb.append(quoteChar);
                    allowQuote = false;
                }
                else {
                    allowQuote = true;
                }
            }
            else if (allowQuote) {
                sb.append(quoteChar).append(c);
                allowQuote = false;
            }
            else {
                sb.append(c);
            }
        }
        
        if (allowQuote) sb.append(quoteChar);
        
        return sb.toString();
    }

    public static String unquote(String value) {
        return unquote(value, DEFAULT_QUOTE);
    }

	public static boolean isEmpty(String value) {
		return value == null || value.length() == 0;
	}

	public static boolean isBlank(String value) {
		return value == null || value.matches("^\\s*$");
	}

	public static String padLeft(String value, int length, char padChar) {
		StringBuilder sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.insert(0, padChar);
		}

		return sb.toString();
	}

	public static String padLeft(String value, int length) {
		return padLeft(value, length, DEFAULT_PAD_CHAR);
	}

	public static String padRight(String value, int length, char padChar) {
		StringBuilder sb = new StringBuilder(value);

		while (sb.length() < length) {
			sb.append(padChar);
		}

		return sb.toString();
	}

	public static String padRight(String value, int length) {
		return padRight(value, length, DEFAULT_PAD_CHAR);
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
    
    public static <T extends Object> String join(T[] values, CharSequence delimiter) {
        return Arrays.asList(values).stream()
        		.map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }
    
    @SafeVarargs
	public static <T extends Object> String join(T... values) {
        return join(values, DEFAULT_DELIMITER);
    }
    
    @SafeVarargs
	public static <T extends Object> String joinCamelCase(T... values) {
        if (values == null || values.length == 0)  return "";
        
        return camelCase(String.valueOf(values[0])) +
                Arrays.asList(values).stream()
                        .skip(1)
                        .map(o -> pascalCase(String.valueOf(o)))
                        .collect(Collectors.joining());
    }

	public static String joinCamelCase(String value, String regex) {
		return joinCamelCase(value.split(regex));
	}

	public static String joinCamelCase(String value) {
		return joinCamelCase(value, DEFAULT_SPLIT_REGEX);
	}
    
    @SafeVarargs
	public static <T extends Object> String joinPascalCase(T... values) {
        if (values == null || values.length == 0) {
            return "";
        }
        else {
            return Arrays.asList(values).stream()
                    .map(o -> pascalCase(String.valueOf(o)))
                    .collect(Collectors.joining());
        }
    }

	public static String joinPascalCase(String value, String regex) {
		return joinPascalCase(value.split(regex));
	}

	public static String joinPascalCase(String value) {
		return joinPascalCase(value, DEFAULT_SPLIT_REGEX);
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
	
    public static String repeat(String value, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; ++i) {
            sb.append(value);
        }
        return sb.toString();
    }

    public static String noAccent(String value) {
        if (null == value || value.trim().length() == 0) return "";
        
        value = value.replaceAll("\\s*-\\s*", "-");
        
        StringBuilder sb = new StringBuilder();
        int length = value.length();
        boolean whitespace = false;
        
        for (int i = 0; i < length; ++i) {
            char c = value.charAt(i);
            int pos = ACCENTS.indexOf(c);
            
            if (pos >= 0) {
                sb.append(NO_ACCENT.charAt(pos));
                whitespace = false;
            }
            else if (Character.isLetterOrDigit(c) || ACCEPTED_SPECIAL_CHARS.indexOf(c) >= 0) {
                sb.append(c);
                whitespace = false;
            }
            else if (Character.isWhitespace(c) && !whitespace) {
                sb.append('-');
                whitespace = true;
            }
        }
        
        return sb.toString();
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
