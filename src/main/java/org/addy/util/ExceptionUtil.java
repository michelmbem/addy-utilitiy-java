package org.addy.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtil {
    private ExceptionUtil() {}

    public static Throwable findRootCause(Throwable throwable) {
        if (throwable == null) return null;

        while (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        return throwable;
    }

    public static String getMessage(Throwable throwable) {
        if (throwable == null) return null;
        return StringUtil.requireNonBlankElse(throwable.getLocalizedMessage(), throwable.getMessage());
    }

    public static String getRootMessage(Throwable throwable) {
        return getMessage(findRootCause(throwable));
    }

    public static String getStackTrace(Throwable throwable) {
        if (throwable != null) {
            try (var writer = new StringWriter()) {
                try (var printer = new PrintWriter(writer)) {
                    throwable.printStackTrace(printer);
                    return writer.toString();
                }
            } catch (IOException ignored) {
            }
        }

        return "";
    }

    public static String getHumanReadableName(Throwable throwable) {
        if (throwable == null) return null;

        String typeName = throwable.getClass().getSimpleName();
        if (typeName.endsWith("Exception")) {
            typeName = typeName.substring(0, typeName.length() - 9);
        }

        return String.join(" ", StringUtil.splitWords(typeName));
    }
}
