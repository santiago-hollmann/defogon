package com.shollmann.android.wood.helpers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LogInternal {
    private static boolean DEBUG = true;
    private static boolean LOG_PUSH_EVENTS = false;
    private static boolean LOG_SERVICE_CALLS = false;
    private static boolean LOG_UI_NAVIGATION = false;
    private static boolean LOG_SERVICE_BINDING = false;
    private static boolean LOG_PROGRESS_DIALOG = false;
    private static boolean LOG_TRACKERS = false;

    public static final String TAG = "App-Core";
    private static final long MIN_TOAST_INTERVAL = 2500;
    private static final boolean STRICT_MODE = false;
    private static long lastToast;

    public static boolean isDebugging() {
        return DEBUG;
    }

    private static void dualColumnLog(String leftColumnMessage, String rightColumnMessage) {
        log(String.format("%-40s %s", leftColumnMessage, rightColumnMessage), Log.INFO);
    }

    public static void logPushEvent(String eventMessage, String parametersMessage) {
        if (LOG_PUSH_EVENTS) {
            dualColumnLog("Push Event: " + eventMessage, parametersMessage);
        }
    }

    public static void logServiceCall(String eventMessage, String parametersMessage) {
        if (LOG_SERVICE_CALLS) {
            dualColumnLog("DataService: " + eventMessage, parametersMessage);
        }
    }

    public static void logUINavigation(String eventMessage, String parametersMessage) {
        if (LOG_UI_NAVIGATION) {
            dualColumnLog("UI Navigation: " + eventMessage, parametersMessage);
        }
    }

    public static void logServiceBinding(String eventMessage, String contextMessage) {
        if (LOG_SERVICE_BINDING) {
            dualColumnLog("Svc Bind: " + eventMessage, contextMessage);
        }
    }

    public static void logProgressDialog(String eventMessage, Activity activity) {
        if (LOG_PROGRESS_DIALOG) {
            dualColumnLog("Progress Dialog: " + eventMessage, activity != null ? activity.getClass().getSimpleName() : "null");
        }
    }

    public static void logTrackers(String trackerName, String eventMessage, String parametersMessage) {
        if (LOG_TRACKERS) {
            dualColumnLog(String.format("Tracker %s: %s", trackerName, eventMessage), parametersMessage);
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

    public static void toast(Context context, String message) {
        if (System.currentTimeMillis() - lastToast > MIN_TOAST_INTERVAL) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            lastToast = System.currentTimeMillis();
        }
    }

    public static boolean isStrictMode() {
        return STRICT_MODE;
    }
}
