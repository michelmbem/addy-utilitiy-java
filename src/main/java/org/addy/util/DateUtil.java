package org.addy.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {
	public static final int HOURS_PER_DAY = 24;
	public static final int MINUTES_PER_HOUR = 60;
	public static final int MINUTES_PER_DAY = 1440;
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = 3600;
	public static final int SECONDS_PER_DAY = 86400;

	private DateUtil() {}

	public static Date dateTime(int year, int month, int day, int hour, int minute, int second) {
		if (year < 1) throw new IllegalArgumentException("The year must be greater than or equal to 1");
		if (month < 1 || month > 12) throw new IllegalArgumentException("The month must be between 1 and 12");
		if (day < 1 || day > getLastDayOfMonth(month, year)) throw new IllegalArgumentException("The day must be between 1 and the last day of the month");
		if (hour < 0 || hour > 23) throw new IllegalArgumentException("The hour must be between 0 and 23");
		if (minute < 0 || minute > 59) throw new IllegalArgumentException("The minute must be between 0 and 59");
		if (second < 0 || second > 59) throw new IllegalArgumentException("The second must be between 0 and 59");

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTime();
	}

	public static Date date(int year, int month, int day) {
		return dateTime(year, month, day, 0, 0, 0);
	}

	public static Date time(int hour, int minute, int second) {
		return dateTime(1, 1, 1, hour, minute, second);
	}

	public static Date now() {
		return Calendar.getInstance().getTime();
	}

	public static Date today() {
		return getDate(now());
	}

	public static Date timeOfDay() {
		return getTime(now());
	}

	public static Date getDate(Date date) {
		Calendar calendar = toCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getTime(Date date) {
		Calendar calendar = toCalendar(date);
		calendar.set(Calendar.YEAR, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}

	public static int getYear(Date date) {
		return toCalendar(date).get(Calendar.YEAR);
	}

	public static int getMonth(Date date) {
		return toCalendar(date).get(Calendar.MONTH) + 1;
	}

	public static int getWeekOfYear(Date date) {
		return toCalendar(date).get(Calendar.WEEK_OF_YEAR);
	}

	public static int getWeekOfMonth(Date date) {
		return toCalendar(date).get(Calendar.WEEK_OF_MONTH);
	}

	public static int getDayOfYear(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_YEAR);
	}

	public static int getDayOfMonth(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_MONTH);
	}

	public static int getDayOfWeek(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_WEEK);
	}

	public static int getDayOfWeekInMonth(Date date) {
		return toCalendar(date).get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public static int getHour(Date date) {
		return toCalendar(date).get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(Date date) {
		return toCalendar(date).get(Calendar.MINUTE);
	}

	public static int getSecond(Date date) {
		return toCalendar(date).get(Calendar.SECOND);
	}

	public static int getMillis(Date date) {
		return toCalendar(date).get(Calendar.MILLISECOND);
	}

	public static Date addYears(Date date, int years) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.YEAR, years);
		return calendar.getTime();
	}

	public static Date addMonths(Date date, int months) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTime();
	}

	public static Date addDays(Date date, int days) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public static Date addHours(Date date, int hours) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.HOUR_OF_DAY, hours);
		return calendar.getTime();
	}

	public static Date addMinutes(Date date, int minutes) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	public static Date addSeconds(Date date, int seconds) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.SECOND, seconds);
		return calendar.getTime();
	}

	public static Date addMillis(Date date, int millis) {
		Calendar calendar = toCalendar(date);
		calendar.add(Calendar.MILLISECOND, millis);
		return calendar.getTime();
	}
	
	public static Date combine(Date date, Date time) {
		Calendar dateCal = toCalendar(date);
		Calendar timeCal = toCalendar(time);
		Calendar finalCal = Calendar.getInstance();
		finalCal.clear();
		finalCal.set(Calendar.YEAR, dateCal.get(Calendar.YEAR));
		finalCal.set(Calendar.MONTH, dateCal.get(Calendar.MONTH));
		finalCal.set(Calendar.DATE, dateCal.get(Calendar.DATE));
		finalCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
		finalCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
		finalCal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
		finalCal.set(Calendar.MILLISECOND, timeCal.get(Calendar.MILLISECOND));
		return finalCal.getTime();
	}

	public static int getLastDayOfMonth(int month, int year) {
		return switch (month) {
			case 1, 3, 5, 7, 8, 10, 12 -> 31;
			case 4, 6, 9, 11 -> 30;
			default -> (year >= 1900 && year % 4 == 0) ? 29 : 28;
		};
	}

	public static Date getLastSecondOfDay(Date date) {
		return addSeconds(getDate(date), SECONDS_PER_DAY - 1);
	}

	public static Date getAverrageDate(Date... dates) {
		if (dates.length == 0) return new Date(0L);

		long millis = 0L;
		for (Date date : dates) {
			millis += date.getTime();
		}

		return new Date(millis / dates.length);
	}

	public static int getYearsBetween(Date from, Date to) {
		if (to.before(from)) return -getYearsBetween(to, from);

		Calendar fromCal = toCalendar(from);
		Calendar toCal = toCalendar(to);

		int yearDiff = toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR);
		int monthDiff = toCal.get(Calendar.MONTH) - fromCal.get(Calendar.MONTH);
		int dayDiff = toCal.get(Calendar.DATE) - fromCal.get(Calendar.DATE);
		if (monthDiff < 0 || (monthDiff == 0 && dayDiff < 0)) --yearDiff;

		return yearDiff;
	}

	public static int getMonthsBetween(Date from, Date to) {
		if (to.before(from)) return -getMonthsBetween(to, from);

		Calendar fromCal = toCalendar(from);
		Calendar toCal = toCalendar(to);

		int yearDiff = toCal.get(Calendar.YEAR) - fromCal.get(Calendar.YEAR);
		int monthDiff = toCal.get(Calendar.MONTH) - fromCal.get(Calendar.MONTH);
		int dayDiff = toCal.get(Calendar.DATE) - fromCal.get(Calendar.DATE);
		if (monthDiff < 0) {
			monthDiff += 12;
			--yearDiff;
		}
		if (dayDiff < 0) --monthDiff;

		return 12 * yearDiff + monthDiff;
	}

	public static int getWeeksBetween(Date from, Date to) {
		return getDaysBetween(from, to) / 7;
	}

	public static int getDaysBetween(Date from, Date to) {
		int millisPerDay = SECONDS_PER_DAY * 1000;
		return (int) ((to.getTime() - from.getTime()) / millisPerDay);
	}

	public static long getHoursBetween(Date from, Date to) {
		long millisPerHour = SECONDS_PER_HOUR * 1000L;
		return (to.getTime() - from.getTime()) / millisPerHour;
	}

	public static long getMinutesBetween(Date from, Date to) {
		long millisPerMinute = SECONDS_PER_MINUTE * 1000L;
		return (to.getTime() - from.getTime()) / millisPerMinute;
	}

	public static long getSecondsBetween(Date from, Date to) {
		return (to.getTime() - from.getTime()) / 1000L;
	}

	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}

	public static String toLongDateTimeString(Date date) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		return dateFormat.format(date);
	}

	public static String toShortDateTimeString(Date date) {
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
		return dateFormat.format(date);
	}

	public static String toLongDateString(Date date) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		return dateFormat.format(date);
	}

	public static String toShortDateString(Date date) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		return dateFormat.format(date);
	}

	public static String toTimeString(Date date) {
		DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.LONG);
		return dateFormat.format(date);
	}

	public static String toString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	public static Date parseDate(String value, Locale locale) throws ParseException {
		int[] styles = { DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG };
		DateFormat[] dateFormats = new DateFormat[styles.length * (styles.length + 2)];
		int index = 0, errorOffset = 0;
		
		for (int style1 : styles) {
			dateFormats[index++] = DateFormat.getDateInstance(style1, locale);
			dateFormats[index++] = DateFormat.getTimeInstance(style1, locale);
			for (int style2 : styles) {
				dateFormats[index++] = DateFormat.getDateTimeInstance(style1, style2, locale);
			}
		}

		for (DateFormat dateFormat : dateFormats) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException pex) {
				if (pex.getErrorOffset() > errorOffset) {
					errorOffset = pex.getErrorOffset();
				}
			}
		}

		throw new ParseException("Could not parse " + value + " as a date", errorOffset);
	}

	public static Date parseDate(String value) throws ParseException {
		return parseDate(value, Locale.getDefault());
	}
}
