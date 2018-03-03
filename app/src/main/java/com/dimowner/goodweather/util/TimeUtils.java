package com.dimowner.goodweather.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

	/** Date format: May 16, 10:30 AM */
	private static SimpleDateFormat messageDateFormat = new SimpleDateFormat("MMM dd, hh:mm aa", Locale.US);

	public static final int INTERVAL_SECOND = 1000; //mills
	public static final int INTERVAL_MINUTE = 60 * INTERVAL_SECOND;
	public static final int INTERVAL_HOUR = 60 * INTERVAL_MINUTE;
	public static final int INTERVAL_DAY = 24 * INTERVAL_HOUR;

	private TimeUtils() {}

	public static String formatTimeGMT(long timeMillsGmt) {
		if (timeMillsGmt <= 0) {
			return "";
		}
		Calendar calendar = Calendar.getInstance();
		long offset = calendar.getTimeZone().getRawOffset();
		long dstSavings = calendar.getTimeZone().getDSTSavings();

		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(timeMillsGmt + offset + dstSavings);

		Calendar dayYesterday = Calendar.getInstance();
		dayYesterday.setTimeInMillis(date.getTimeInMillis());
		dayYesterday.set(Calendar.DAY_OF_YEAR, dayYesterday.get(Calendar.DAY_OF_YEAR) - 1);

		Calendar prevYear = Calendar.getInstance();
		prevYear.setTimeInMillis(date.getTimeInMillis());
		prevYear.set(Calendar.YEAR, prevYear.get(Calendar.YEAR) - 1);

		return messageDateFormat.format(new Date(timeMillsGmt + offset + dstSavings));

	}

	public static String formatTime(long timeMills) {
		if (timeMills <= 0) {
			return "";
		}
		return messageDateFormat.format(new Date(timeMills));
	}
}
