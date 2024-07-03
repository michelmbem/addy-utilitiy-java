package org.addy.util;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
