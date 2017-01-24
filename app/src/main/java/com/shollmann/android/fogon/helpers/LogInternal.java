package com.shollmann.android.fogon.helpers;

import android.util.Log;

public class LogInternal {
    private static boolean DEBUG = false;
    private static boolean LOG_UI_NAVIGATION = false;

    public static final String TAG = "DeFogon";
    private static final long MIN_TOAST_INTERVAL = 2500;
    private static final boolean STRICT_MODE = false;
    private static long lastToast;

    public static boolean isDebugging() {
        return DEBUG;
    }

    private static void dualColumnLog(String leftColumnMessage, String rightColumnMessage) {
        log(String.format("%-40s %s", leftColumnMessage, rightColumnMessage), Log.INFO);
    }

    public static void logUINavigation(String eventMessage, String parametersMessage) {
        if (LOG_UI_NAVIGATION) {
            dualColumnLog("UI Navigation: " + eventMessage, parametersMessage);
        }
    }


    public static void log(String sMessage) {
        log(sMessage, Log.INFO);
    }

    public static void error(String sMessage) {
        log(sMessage, Log.ERROR);
    }

    public static void log(String sMessage, int iType) {
        if (!DEBUG) {
            return;
        }
        switch (iType) {
            case Log.ERROR:
                Log.e(LogInternal.TAG, "" + sMessage);
                break;
            case Log.DEBUG:
                Log.d(LogInternal.TAG, "" + sMessage);
                break;
            case Log.INFO:
                Log.i(LogInternal.TAG, "" + sMessage);
                break;
            case Log.VERBOSE:
                Log.v(LogInternal.TAG, "" + sMessage);
                break;
            case Log.WARN:
                Log.w(LogInternal.TAG, "" + sMessage);
                break;
        }

    }

    public static boolean isStrictMode() {
        return STRICT_MODE;
    }
}
