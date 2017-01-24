package com.shollmann.android.fogon.helpers;

import android.support.compat.BuildConfig;
import android.util.Log;

public class LogInternal {
    public static final String TAG = "DeFogon";
    private static final boolean STRICT_MODE = false;
    private static boolean DEBUG = BuildConfig.DEBUG;
    private static boolean LOG_UI_NAVIGATION = false;

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
