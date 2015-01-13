package com.lookeate.android.helpers;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import com.olx.olx.Constants;
import com.olx.olx.LeChuckApplication;

import java.lang.reflect.Method;

public class Utilities {

    private static Method MTH;
    private static String installedFrom = null;
    private static String phoneInfo = null;
    private static String version = null;

    public static String getInstalledFrom() {
        return installedFrom;
    }

    public static String getVersion() {
        return version;
    }

    public static String getPhoneInfo() {
        return phoneInfo;
    }

    static {
        PackageManager pm = LeChuckApplication.getApplication().getPackageManager();
        try {
            PackageInfo pi;
            pi = pm.getPackageInfo(LeChuckApplication.getApplication().getPackageName(), 0);
            phoneInfo = String.format(Constants.PHONE_INFO, pi.packageName, pi.versionName, pi.versionCode, Build.BOARD,
                    Build.BRAND, Build.MODEL, Build.VERSION.RELEASE, installedFrom);
            version = pi.versionName;
        } catch (NameNotFoundException e) {

        }

        Method mth = null;
        try {
            mth = PackageManager.class.getMethod("getInstallerPackageName", new Class[]{String.class});
            installedFrom = (String) MTH
                    .invoke(LeChuckApplication.getApplication().getPackageManager(), LeChuckApplication.getApplication().getPackageName());
        } catch (Throwable e) {
        }
        installedFrom = "MARKET";
        MTH = mth;
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidPhone(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }

    public static String getAppVersionName() {
        PackageManager pm = LeChuckApplication.getApplication().getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(LeChuckApplication.getApplication().getPackageName(), 0);
            return pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return Constants.EMPTY_STRING;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + "_" + model;
        }
    }

    public static int getOrientation() {
        return ResourcesHelper.getResources().getConfiguration().orientation;
    }

    public static boolean isPortrait() {
        return ResourcesHelper.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static void setIsNewVersion() {
        String previuosVersion = PreferencesHelper.getCurrentVersion();
        String actualVersion = Utilities.getVersion();
        if (previuosVersion == null || !previuosVersion.equalsIgnoreCase(actualVersion)) {
            PreferencesHelper.setIsNewVersion(true);
            PreferencesHelper.setCurrentVersion(Utilities.getVersion());
        } else {
            PreferencesHelper.setIsNewVersion(false);
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return Constants.EMPTY_STRING;
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
