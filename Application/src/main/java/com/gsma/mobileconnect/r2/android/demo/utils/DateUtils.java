package com.gsma.mobileconnect.r2.android.demo.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateUtils {

    /**
     *
     * @param millis - time in milliseconds
     * @param dateFormat - format of date
     * @return date in sent format
     */
    public static String getDate(final long millis, final String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
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
