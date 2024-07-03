package org.addy.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {
    private static final String[] values = {"one", "two", "three", "four"};

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
    void removeAccentsWorks() {
        assertEquals("Eleves du maitre a Noel", StringUtil.removeAccents("Élèves du maître à Noël"));
        assertEquals("jeunesse", StringUtil.removeAccents("jeunesse"));
    }

    @Test
    void randomStringWorks() {
        assertEquals(16, StringUtil.randomString(16).length());
    }
}
