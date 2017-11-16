package com.gsma.mobileconnect.r2.android.demo.utils;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.util.Calendar;


public class DateUtils {

    /**
     *
     * @param millis - time in milliseconds
     * @return date in sent format
     */
    public static String getDate(final long millis) {
        ISO8601DateFormat formatter = new ISO8601DateFormat();
        return formatter.format(getCalendar(millis).getTime());
    }

    /**
     *
     * @param millis - time in milliseconds
     * @return {@link Calendar} object with date from millis
     */
    private static Calendar getCalendar(final long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }
}
