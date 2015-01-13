package android.lookeate.com.core_lib.helpers;

import android.lookeate.com.core_lib.CoreLibApplication;
import android.text.format.DateFormat;

import java.text.DecimalFormat;
import java.util.Date;

public class DateHelper {

    private static final String DATE_TIME = "%1$s %2$s";

    public static int diffSeconds(long d1, long d2) {
        long diff = Math.abs(d1 - d2);
        diff = diff / (1000);
        return (int) diff;
    }

    public static int diffSeconds(long d1) {
        return diffSeconds(System.currentTimeMillis(), d1);
    }

    public static String formatDate(long milliseconds) {
        if (milliseconds > 0) {
            return DateFormat.getDateFormat(CoreLibApplication.getInstance().getContext()).format(new Date(milliseconds));
        }
        return null;
    }

    public static String formatMediumDate(long milliseconds) {
        if (milliseconds > 0) {
            return DateFormat.getMediumDateFormat(CoreLibApplication.getInstance().getContext()).format(new Date(milliseconds));
        }
        return null;
    }

    public static Object[] formatDate(long milliseconds, boolean time) {
        Object[] date = new String[2];
        if (milliseconds > 0) {
            date[0] = DateFormat.getDateFormat(CoreLibApplication.getInstance().getContext()).format(new Date(milliseconds));
            date[1] = DateFormat.getTimeFormat(CoreLibApplication.getInstance().getContext()).format(new Date(milliseconds));
        }
        return date;
    }

    public static String formatDateTime(long milliseconds) {
        return String.format(DATE_TIME, formatDate(milliseconds, true));
    }

    public static String formatTimeSpan(long seconds) {

        final DecimalFormat f = new DecimalFormat("0.#");

        if (seconds >= 86400) { // 86400 = 60 * 60 * 24 = 1 day
            return f.format(seconds / 86400.) + "d";
        } else if (seconds >= 3600) { // 3600 = 60 * 60 = 1 hour
            return f.format(seconds / 3600.) + "h";
        } else if (seconds >= 60) { // 60 = 1 minute
            return f.format(seconds / 60.) + "m";
        } else {
            return seconds + "s";
        }
    }
}
