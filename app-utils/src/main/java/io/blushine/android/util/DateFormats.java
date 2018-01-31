package io.blushine.android.util;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;

import io.blushine.android.AppActivity;

/**
 * Various time and date helper methods
 */
public class DateFormats {
/**
 * Get the short date format for the current locale, such as (2001-01-03)
 * @return short date format for the current locale
 */
public static SimpleDateFormat getShortDateFormat() {
	return (SimpleDateFormat) DateFormat.getDateFormat(AppActivity.getActivity());
}

/**
 * Get the medium date format for the current locale, such as (Jan 3, 2001)
 * @return medium date format for the current locale
 */
public static SimpleDateFormat getMediumDateFormat() {
	return (SimpleDateFormat) DateFormat.getMediumDateFormat(AppActivity.getActivity());
}

/**
 * Get the long date format for the current locale, such as (Monday, January 3, 2001)
 * @return long date format for the current locale
 */
public static SimpleDateFormat getLongDateFormat() {
	return (SimpleDateFormat) DateFormat.getLongDateFormat(AppActivity.getActivity());
}

/**
 * Get the time format for the current locale. I.e. 12 or 24 hours.
 * @return time format for the current locale
 */
public static SimpleDateFormat getTimeFormat() {
	return (SimpleDateFormat) DateFormat.getTimeFormat(AppActivity.getActivity());
}
}
