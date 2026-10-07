package org.addy.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {
    private static final String[] values = {"one", "two", "three", "four"};
    private static int count = 0;

    @ParameterizedTest()
    @ValueSource(ints = {2, 3, 4, 5, 6})
    void StringSplitByNTest(int n) {
        String str = "Je me figure ce zouave qui boit du whisky en jouant au xylophone";
        var sb = new StringBuilder();
        count = 0;

        str.chars().filter(c -> !Character.isSpaceChar(c))
                .forEach(c -> {
                    sb.append((char)c);
                    if (++count >= n) {
                        count = 0;
                        sb.append('\n');
                    }
                });

        String[] lines = sb.toString().split("\n");
        long m = Arrays.stream(lines).filter(s -> s.length() != n).count();

        assertTrue(m <= 1);
    }

    @Test
    void isEmptyWorks() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("Hello"));
    }

    @Test
    void isBlankWorks() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" "));
        assertTrue(StringUtil.isBlank("\t\f\r\n"));
        assertFalse(StringUtil.isBlank("Hello"));
    }

    @Test
    void isNumericWorks() {
        assertFalse(StringUtil.isNumeric(null));
        assertFalse(StringUtil.isNumeric(""));
        assertFalse(StringUtil.isNumeric(" "));
        assertTrue(StringUtil.isNumeric("1982"));
        assertTrue(StringUtil.isNumeric("-725.83"));
        assertTrue(StringUtil.isNumeric("17.5e-33"));
        assertTrue(StringUtil.isNumeric(".314E+1"));
    }

    @Test
    void isTemporalWorks() {
        assertFalse(StringUtil.isTemporal(null));
        assertFalse(StringUtil.isTemporal(""));
        assertFalse(StringUtil.isTemporal(" "));
        assertTrue(StringUtil.isTemporal("1982-02-28"));
        assertTrue(StringUtil.isTemporal("2025-11-21T18:30:52Z"));
        assertTrue(StringUtil.isTemporal("14:19:05"));
        assertTrue(StringUtil.isTemporal("20070817"));
        assertTrue(StringUtil.isTemporal("July 3, 2024 at 10:15:30 PM EDT", Locale.US));
        assertTrue(StringUtil.isTemporal("3 juill. 2024, 22 h 15 min 30 s", Locale.CANADA_FRENCH));
        assertTrue(StringUtil.isTemporal("7/3/24, 10:15 PM", Locale.US));
        assertTrue(StringUtil.isTemporal("3 juillet 2024", Locale.FRANCE));
        assertTrue(StringUtil.isTemporal("3 juill. 2024", Locale.CANADA_FRENCH));
        assertTrue(StringUtil.isTemporal("7/3/24", Locale.US));
        assertTrue(StringUtil.isTemporal("22:15:30 EDT", Locale.FRANCE));
        assertTrue(StringUtil.isTemporal("10:15 PM", Locale.US));
    }

    @Test
    void padLeftWorks() {
        assertEquals("xxxxxHello", StringUtil.padLeft("Hello", 10, 'x'));
        assertEquals("Hello", StringUtil.padLeft("Hello", 5, 'x'));
        assertEquals("Hello", StringUtil.padLeft("Hello", 3, 'x'));
    }

    @Test
    void padRightWorks() {
        assertEquals("Helloxxxxx", StringUtil.padRight("Hello", 10, 'x'));
        assertEquals("Hello", StringUtil.padRight("Hello", 5, 'x'));
        assertEquals("Hello", StringUtil.padRight("Hello", 3, 'x'));
    }

    @Test
    void wrapWorks() {
        assertEquals(null, StringUtil.wrap(null));
        assertEquals("$$", StringUtil.wrap("", "$"));
        assertEquals("\"Bonjour\"", StringUtil.wrap("Bonjour", "\""));
        assertEquals("'Salut'", StringUtil.wrap("Salut"));
        assertEquals("'Salut l''ami'", StringUtil.wrap("Salut l'ami"));
        assertEquals("\"Salut l'ami\"", StringUtil.wrap("Salut l'ami", "\""));
        assertEquals("\"\"\"Bonjour l'ami\"\"\"", StringUtil.wrap("Bonjour l'ami", "\"\"\""));
    }

    @Test
    void unwrapWorks() {
        assertEquals(null, StringUtil.unwrap(null));
        assertEquals("", StringUtil.unwrap("", "$"));
        assertEquals("$$Hello", StringUtil.unwrap("$$Hello", "$$"));
        assertEquals("Bonjour", StringUtil.unwrap("\"Bonjour\"", "\""));
        assertEquals("Salut", StringUtil.unwrap("'Salut'"));
        assertEquals("Salut l'ami", StringUtil.unwrap("'Salut l''ami'"));
        assertEquals("Salut l'ami", StringUtil.unwrap("\"Salut l'ami\"", "\""));
        assertEquals("Bonjour l'ami", StringUtil.unwrap("##Bonjour l'ami##", "##"));
    }

    @Test
    void camelCaseWorks() {
        assertEquals(null, StringUtil.camelCase(null));
        assertEquals("", StringUtil.camelCase(""));
        assertEquals("c", StringUtil.camelCase("c"));
        assertEquals("toto", StringUtil.camelCase("toto"));
        assertEquals("helloWorld", StringUtil.camelCase("HelloWorld"));
        assertEquals("eXPECTATION", StringUtil.camelCase("EXPECTATION"));
    }

    @Test
    void pascalCaseWorks() {
        assertEquals(null, StringUtil.pascalCase(null));
        assertEquals("", StringUtil.pascalCase(""));
        assertEquals("C", StringUtil.pascalCase("c"));
        assertEquals("Toto", StringUtil.pascalCase("toto"));
        assertEquals("HelloWorld", StringUtil.pascalCase("helloWorld"));
        assertEquals("EXPECTATION", StringUtil.pascalCase("EXPECTATION"));
    }

    @Test
    void joinWorks() {
        assertEquals("one-two-three-four", StringUtil.join(values, "-"));
        assertEquals("onetwothreefour", StringUtil.join(values));
    }

    @Test
    void joinCamelCaseWorks() {
        assertEquals("oneTwoThreeFour", StringUtil.joinCamelCase(values));
    }

    @Test
    void joinPascalCaseWorks() {
        assertEquals("OneTwoThreeFour", StringUtil.joinPascalCase(values));
    }

    @Test
    void splitJoinCamelCaseWorks() {
        assertEquals("unDeuxTroisQuatre", StringUtil.splitJoinCamelCase("un deux trois quatre"));
        assertEquals("lundiMardiMercredi", StringUtil.splitJoinCamelCase("lundi,mardi,mercredi", ","));
    }

    @Test
    void splitJoinPascalCaseWorks() {
        assertEquals("UnDeuxTroisQuatre", StringUtil.splitJoinPascalCase("un deux trois quatre"));
        assertEquals("LundiMardiMercredi", StringUtil.splitJoinPascalCase("lundi-mardi-mercredi", "-"));
    }

    @Test
    void reverseCaseWorks() {
        assertEquals("hELLO", StringUtil.reverseCase("Hello"));
        assertEquals("THOMAS@JEFFREY.COM", StringUtil.reverseCase("thomas@jeffrey.com"));
    }

    @Test
    void stripAccentsWorks() {
        assertEquals("Eleves du maitre a Noel", StringUtil.stripAccents("Élèves du maître à Noël"));
        assertEquals("jeunesse", StringUtil.stripAccents("jeunesse"));
    }

    @Test
    void randomStringWorks() {
        assertEquals(16, StringUtil.randomString(16).length());
    }
}
