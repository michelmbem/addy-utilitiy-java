package org.addy.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionUtilTest {
    Throwable throwable;

    @BeforeEach
    void setUp() {
        throwable = new IllegalArgumentException(
                "invalid arg",
                new RuntimeException(
                        "not a number",
                        new ClassCastException("failed to cast")));
    }

    @Test
    void findRootCauseWorks() {
        Throwable rootCause = ExceptionUtil.findRootCause(throwable);
        assertThat(rootCause).isInstanceOf(ClassCastException.class);
    }

    @Test
    void getMessageWorks() {
        String message = ExceptionUtil.getMessage(throwable);
        assertThat(message).isEqualTo("invalid arg");
    }

    @Test
    void getRootMessageWorks() {
        String message = ExceptionUtil.getRootMessage(throwable);
        assertThat(message).isEqualTo("failed to cast");
    }

    @Test
    void getStackTraceWorks() {
        String stackTrace;

        try {
            throw throwable;
        } catch (Throwable t) {
            stackTrace = ExceptionUtil.getStackTrace(t);
            System.out.println(stackTrace);
        }

        assertThat(stackTrace).isNotBlank();
    }

    @Test
    void getHumanReadableNameWorks() {
        String readableName = ExceptionUtil.getHumanReadableName(throwable);
        assertThat(readableName).isEqualTo("illegal argument");
    }
}