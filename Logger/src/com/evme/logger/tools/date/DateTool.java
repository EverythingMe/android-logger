package com.evme.logger.tools.date;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

public class DateTool {
	private static DateTool mInstance = null;

	public static final String DEFAULT_FORMAT = "yyyy-MM-dd";
	public static final String FULL_FORMAT = "yyyy-MM-dd HH:mm:ss a zzz";

	public static final class Patterns {
		/**
		 * Like 1984
		 */
		public static final String YEAR_FULL = "yyyy";

		/**
		 * Month number. Like 04
		 */
		public static final String MONTH_NUMBER = "MM";

		/**
		 * Day of month number. Like 25
		 */
		public static final String DAY_NUMBER = "dd";

		/**
		 * Hour of the day. It depends on AM_PM set. <br>
		 * If AM_PM set was used, then HH will be until 12:00, otherwise it will
		 * be until 24:00
		 */
		public static final String HOUR = "HH";

		/**
		 * Minutes. Like 16
		 */
		public static final String MINUTES = "mm";

		/**
		 * Seconds. Like 56
		 */
		public static final String SECONDS = "ss";

		/**
		 * Milliseconds
		 */
		public static final String MILLI_SECONDS = "SSS";

		/**
		 * Add 'AM'/'PM'
		 */
		public static final String AM_PM = "a";

		/**
		 * Day of week in short way. Like: Wed
		 */
		public static final String DAY_OF_WEEK_SHORT = "EEE";

		/**
		 * Day of week in full way. Like: Wednesday
		 */
		public static final String DAY_OF_WEEK_FULL = "EEEE";
	}

	private DateTool() {

	}

	public static class WeekDays {
		public int weeks;
		public int days;

		public WeekDays(int weeks, int days) {
			this.weeks = weeks;
			this.days = days;
		}
	}

	public static DateTool getInstance() {
		if (mInstance == null) {
			mInstance = new DateTool();
		}
		return mInstance;
	}

	/**
	 * Get date in {@value #DEFAULT_FORMAT} format
	 * 
	 * @param date
	 * @return string as date
	 */
	public static String getString(Date date) {
		return getString(date, DEFAULT_FORMAT);
	}

	public static String getString(Date date, String format) {
		Format formatter = new SimpleDateFormat(format, Locale.US);
		String s = formatter.format(date);
		return s;
	}

	public static Date getDate(String string) {
		try {
			return getDate(string, DEFAULT_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public static Date getDate(String string, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
		Date date = formatter.parse(string);
		return date;
	}

	/**
	 * Return date by milliseconds from 1970
	 * 
	 * @param millis
	 * @return
	 */
	public static Date getDate(long millis) {
		Date date = new Date(millis);
		return date;
	}

	/**
	 * Return millis by date
	 * 
	 * @param date
	 * @return millis
	 */
	public static long getLong(Date date) {
		return date.getTime();
	}

	/**
	 * Get current time
	 * 
	 * @return current time
	 */
	public static Date getNowDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * Get current time in millis
	 * 
	 * @return Time in millis
	 */
	public static long getNowDateMillis() {
		return Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * For example: today + plus 140 days
	 * 
	 * @return
	 */
	public static Date getDatePlusDays(Date date, int numOfPlusDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, numOfPlusDays);
		return calendar.getTime();
	}

	/**
	 * For example: today + plus 15 minutes
	 * 
	 * @return
	 */
	public static Date getDatePlusMinutes(Date date, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

	/**
	 * For example: today + plus 15 minutes
	 * 
	 * @return
	 */
	public static long getNowPlusMinutes(int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getNowDate());
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTimeInMillis();
	}

	/**
	 * For example: today - 1 day
	 * 
	 * @param date
	 * @param numOfMinusDays
	 * @return
	 */
	public static Date getDateMinusDays(Date date, int numOfMinusDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, (0 - numOfMinusDays));
		return calendar.getTime();
	}

	/**
	 * This method will return week+days by passed overall number of days.<br>
	 * For example, passed days = 36, the the returned value will be 5+1
	 * 
	 * @param overallDays
	 * @return
	 */
	public static WeekDays getWeekPlusDays(int overallDays) {
		int weeks = (int) Math.floor(overallDays / 7);
		int days = ((int) overallDays) - weeks * 7;
		return new WeekDays(weeks, days);
	}

	/**
	 * Get passed seconds from date till date
	 * 
	 * @param fromDate
	 * @param toDate
	 * @return passed seconds
	 */
	public static int getPassedSeconds(Date fromDate, Date toDate) {
		Calendar fromDateCal = Calendar.getInstance();
		fromDateCal.setTime(fromDate);

		Calendar toDateCal = Calendar.getInstance();
		toDateCal.setTime(toDate);

		float millisDiff = ((toDateCal.getTimeInMillis() - fromDateCal.getTimeInMillis()));
		float diffSeconds = ((millisDiff) / (float) (1000));
		int seconds = Math.round(diffSeconds);

		return seconds;
	}

	/**
	 * This method will retrieve the date in string format that is more readable
	 * for the user.<br>
	 * Supported formats:
	 * <ul>
	 * <li>X seconds ago</li>
	 * <li>X minutes ago</li>
	 * <li>X hours ago</li>
	 * <li>Yesterday at 11:08 am</li>
	 * <li>Thursday at 9:36 pm</li>
	 * <li>May 5 at 5:00 pm</li>
	 * <li>November 20 at 6:30 pm, 2012</li>
	 * </ul>
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getSocialPastPostDate(Date postDate) {
		Date nowDate = getNowDate();

		// check passed seconds
		int passedSeconds = getPassedSeconds(postDate, nowDate);
		if (passedSeconds < 60) {
			return passedSeconds + " seconds ago";
		}

		// check passed minutes
		float passedMinutesFloat = (float) passedSeconds / (float) 60;
		int passedMinutes = Math.round(passedMinutesFloat);
		if (passedMinutes < 60) {
			return passedMinutes + " minutes ago";
		}

		// check passed hours
		float passedHoursFloat = passedMinutesFloat / (float) 60;
		int passedHours = Math.round(passedHoursFloat);
		if (passedHours < 24) {
			return passedHours + " hours ago";
		}

		// check yesterday
		Date startYesterdayDate = getZeroDayBeforeDays(nowDate, 1);
		int compare = compare(startYesterdayDate, postDate);
		if (compare == -1) {
			return "Yesterday at " + getString(postDate, "HH:mm") + getString(postDate, "a").toLowerCase();
		}

		// check weeks
		Date startWeekDate = getZeroDayBeforeDays(nowDate, 6);
		int compareWeek = compare(startWeekDate, postDate);
		if (compareWeek == -1) {
			return getString(postDate, "EEEE") + " at " + getString(postDate, "HH:mm") + getString(postDate, "a").toLowerCase();
		}

		// check year
		Date startYear = getStartYear(nowDate);
		int compareYear = compare(startYear, postDate);
		if (compareYear == -1) {
			return getString(postDate, "MMMM dd") + " at " + getString(postDate, "HH:mm") + getString(postDate, "a").toLowerCase();
		}

		return getString(postDate, "MMMM dd") + " at " + getString(postDate, "HH:mma yyyy");
	}

	/**
	 * 0 = both same date <br>
	 * -1 = date2 after date1 <br>
	 * 1 = date1 after date2
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compare(Date date1, Date date2) {
		Calendar dateCal1 = Calendar.getInstance();
		dateCal1.setTime(date1);

		Calendar dateCal2 = Calendar.getInstance();
		dateCal2.setTime(date2);

		int compare = dateCal1.compareTo(dateCal2);
		return compare;
	}

	/**
	 * Get the start year date. <br>
	 * If passed date is Nov 5, 2013, then returned date will be January 1, 2013
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartYear(Date date) {
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(date);
		yesterday.set(Calendar.MONTH, Calendar.JANUARY);
		yesterday.set(Calendar.DAY_OF_MONTH, 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MILLISECOND, 1);
		return yesterday.getTime();
	}

	/**
	 * Get the day year date. <br>
	 * If passed date is Nov 5 18:36, then returned date will be Nov 5 00:00
	 * 
	 * @param date
	 * @return
	 */
	public static Date getStartDay(Date date) {
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(date);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MILLISECOND, 1);
		return yesterday.getTime();
	}

	/**
	 * Get the day before today at 00:00 am.<br>
	 * Means, if passed day = <b>Nov 5 13:04</b>, then returned date will be =
	 * <b>Nov 4 00:00</b> for days = 1
	 * 
	 * @param date
	 * @param days
	 *            Numner of days to reduce
	 * @return
	 */
	public static Date getZeroDayBeforeDays(Date date, int days) {
		Calendar yesterday = Calendar.getInstance();
		yesterday.setTime(getDateMinusDays(date, days));
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MILLISECOND, 1);
		return yesterday.getTime();
	}
}
