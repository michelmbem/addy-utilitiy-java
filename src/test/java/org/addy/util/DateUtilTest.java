package org.addy.util;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HHmmss");

    @Test
    void dateTimeWorks() {
        Date date = DateUtil.dateTime(1998, 7, 13, 14, 50, 33);
        assertEquals("19980713145033", DATE_TIME_FORMAT.format(date));
        assertThrows(IllegalArgumentException.class, () -> DateUtil.dateTime(-1, -1, 0, 14, -7, 33));
    }

    @Test
    void dateWorks() {
        Date date = DateUtil.date(2024, 2, 29);
        assertEquals("20240229", DATE_FORMAT.format(date));
        assertThrows(IllegalArgumentException.class, () -> DateUtil.date(2023, 2, 29));
    }

    @Test
    void timeWorks() {
        Date date = DateUtil.time(14, 27, 40);
        assertEquals("142740", TIME_FORMAT.format(date));
        assertThrows(IllegalArgumentException.class, () -> DateUtil.date(24, 0, 0));
    }

    @Test
    void gettingPartsWorks() {
        Date date = DateUtil.dateTime(1977, 2, 5, 22, 30, 0);
        assertEquals(1977, DateUtil.getYear(date));
        assertEquals(2, DateUtil.getMonth(date));
        assertEquals(5, DateUtil.getDayOfMonth(date));
        assertEquals(22, DateUtil.getHour(date));
        assertEquals(30, DateUtil.getMinute(date));
        assertEquals(0, DateUtil.getSecond(date));
    }

    @Test
    void addingPartsWorks() {
        Date date = DateUtil.dateTime(1977, 2, 5, 22, 30, 0);

        assertEquals("19780205223000", DATE_TIME_FORMAT.format(DateUtil.addYears(date, 1)));
        assertEquals("19750205223000", DATE_TIME_FORMAT.format(DateUtil.addYears(date, -2)));

        assertEquals("19770705223000", DATE_TIME_FORMAT.format(DateUtil.addMonths(date, 5)));
        assertEquals("19760105223000", DATE_TIME_FORMAT.format(DateUtil.addMonths(date, -13)));

        assertEquals("19770317223000", DATE_TIME_FORMAT.format(DateUtil.addDays(date, 40)));
        assertEquals("19770121223000", DATE_TIME_FORMAT.format(DateUtil.addDays(date, -15)));

        assertEquals("19770207103000", DATE_TIME_FORMAT.format(DateUtil.addHours(date, 36)));
        assertEquals("19770204223000", DATE_TIME_FORMAT.format(DateUtil.addHours(date, -DateUtil.HOURS_PER_DAY)));

        assertEquals("19770206043000", DATE_TIME_FORMAT.format(DateUtil.addMinutes(date, 360)));
        assertEquals("19770131223000", DATE_TIME_FORMAT.format(DateUtil.addMinutes(date, -5 * DateUtil.MINUTES_PER_DAY)));

        assertEquals("19770205223052", DATE_TIME_FORMAT.format(DateUtil.addSeconds(date, 52)));
        assertEquals("19770205222908", DATE_TIME_FORMAT.format(DateUtil.addSeconds(date, -52)));
    }

    @Test
    void combineWorks() {
        Date date = DateUtil.date(2024, 2, 29);
        Date time = DateUtil.time(14, 27, 40);
        Date dateTime = DateUtil.combine(date, time);
        assertEquals("20240229142740", DATE_TIME_FORMAT.format(dateTime));
    }

    @Test
    void getLastDayOfMonthWorks() {
        assertEquals(31, DateUtil.getLastDayOfMonth(1, 1990));
        assertEquals(28, DateUtil.getLastDayOfMonth(2, 1995));
        assertEquals(30, DateUtil.getLastDayOfMonth(6, 2010));
        assertEquals(29, DateUtil.getLastDayOfMonth(2, 2024));
    }

    @Test
    void getLastSecondOfDayWorks() {
        Date date = DateUtil.date(2024, 2, 29);
        Date dateTime = DateUtil.getLastSecondOfDay(date);
        assertEquals("20240229235959", DATE_TIME_FORMAT.format(dateTime));
    }

    @Test
    void getAverrageDateWorks() {
        Date avgDate = DateUtil.getAverrageDate(
                DateUtil.date(2020, 3, 2),
                DateUtil.date(2021, 3, 2),
                DateUtil.date(2022, 3, 2),
                DateUtil.date(2023, 3, 2),
                DateUtil.date(2024, 3, 2));
        Date expected = DateUtil.date(2022, 3, 2);

        assertEquals(expected, DateUtil.getDate(avgDate));
    }

    @Test
    void dateDifferenceWorks() {
        Date date1 = DateUtil.date(2020, 3, 2);
        Date date2 = DateUtil.date(2024, 7, 3);
        assertEquals(4, DateUtil.getYearsBetween(date1, date2));
        assertEquals(52, DateUtil.getMonthsBetween(date1, date2));
        assertEquals(1583, DateUtil.getDaysBetween(date1, date2));
    }

    @Test
    void stringConversionWorks() {
        Date date = DateUtil.dateTime(2024, 7, 3, 22, 15, 30);
        assertEquals("July 3, 2024 at 10:15:30 PM EDT", DateUtil.toLongDateTimeString(date, Locale.US));
        assertEquals("3 juill. 2024, 22 h 15 min 30 s", DateUtil.toMediumDateTimeString(date, Locale.CANADA_FRENCH));
        assertEquals("2024-07-03 22 h 15", DateUtil.toShortDateTimeString(date, Locale.US));
        assertEquals("3 juillet 2024", DateUtil.toLongDateString(date, Locale.FRANCE));
        assertEquals("3 juill. 2024", DateUtil.toMediumDateString(date, Locale.CANADA_FRENCH));
        assertEquals("2024-07-03", DateUtil.toShortDateString(date, Locale.US));
        assertEquals("22 h 15 min 30 s HAE", DateUtil.toLongTimeString(date, Locale.FRANCE));
        assertEquals("22 h 15", DateUtil.toShortTimeString(date, Locale.US));
        assertEquals("03/07/2024 22:15:30", DateUtil.toString(date, "dd/MM/yyyy HH:mm:ss"));
    }

    @Test
    void stringParsingWorks() throws ParseException {
        Date date = DateUtil.date(2002, 2, 19);
        assertEquals(date, DateUtil.parseDate("2002-02-19", Locale.CANADA_FRENCH));
        assertEquals(date, DateUtil.parseDate("19/02/2002", Locale.FRANCE));
    }
}
