package com.lookeate.android.util;

import android.content.Context;
import android.lookeate.com.lookeate.R;
import android.text.format.DateFormat;

import java.util.Date;

public class DateUtils {

    public static final int MILLISECONDS_IN_A_DAY = 1000 * 60 * 60 * 24;
    public static final int MILLISECONDS_IN_A_MINUTE = 1000 * 60;
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static final int HOURS_IN_A_DAY = 24;

    public static String getDaysAgoText(Context context, Date date) {
        long itemDateMillis = getMillisForDate(date);
        long todaysMillis = getMillisForDate(new java.util.Date());
        int daysAgo = getDifferenceInDays(todaysMillis, itemDateMillis);

        switch (daysAgo) {
            case 0:
                return context.getResources().getString(R.string.date_today);
            case 1:
                return context.getResources().getString(R.string.date_yesterday);
            default:
                return String.format(context.getString(R.string.date_days_ago), daysAgo);
        }
    }

    public static String getDaysAgoText(Context context, int unixSecondsDate) {
        long todaysMillis = getMillisForDate(new java.util.Date());
        int daysAgo = getDifferenceInDays(todaysMillis, unixSecondsDate * 1000L);

        java.util.Date date = new java.util.Date(unixSecondsDate * 1000L);
        switch (daysAgo) {
            case 0:
                return DateFormat.getTimeFormat(context).format(date);
            case 1:
                return context.getResources().getString(R.string.date_yesterday);
            default: {
                date = new java.util.Date(unixSecondsDate * 1000L);
                return DateFormat.getMediumDateFormat(context).format(date);
            }
        }
    }

    public static String getTimeAgoText(Context context, Date date) {
        long itemMillis = getMillisForDate(date);
        long todaysMillis = getMillisForDate(new java.util.Date());
        int minutesAgo = getDifferenceInMinutes(todaysMillis, itemMillis);

        if (minutesAgo < 0) {
            return context.getString(R.string.date_just_now);
        } else if (minutesAgo < MINUTES_IN_AN_HOUR) {
            return String.format(context.getString(R.string.date_minutes_ago), minutesAgo);
        } else {
            int hoursAgo = minutesAgo / MINUTES_IN_AN_HOUR;

            if (hoursAgo < HOURS_IN_A_DAY) {
                return String.format(context.getString(R.string.date_hours_ago), hoursAgo);
            } else {
                int daysAgo = hoursAgo / HOURS_IN_A_DAY;
                return String.format(context.getString(R.string.date_days_ago), daysAgo);
            }
        }

    }

    private static long getMillisForDate(java.util.Date date) {
        return date.getTime();
    }

    private static int getDifferenceInDays(long firstDate, long secondDate) {
        return Math.round((firstDate - secondDate) / MILLISECONDS_IN_A_DAY);
    }

    private static int getDifferenceInMinutes(long firstDate, long secondDate) {
        return Math.round((firstDate - secondDate) / MILLISECONDS_IN_A_MINUTE);
    }
}
