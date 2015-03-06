package com.shollmann.android.fogon.helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.util.Constants;

import java.lang.reflect.Type;
import java.util.HashMap;

public class PreferencesHelper {
    private final static String API_ENDPOINT = "apiEndpoint";
    private static final String FIRST_START = "firstStart";
    private static final String SHOULD_UPDATE_COUNTRY_TAG = "shouldUpdateCountryTag";
    private static final String ACTIVITIES_VIEWS = "activitiesViews";
    private static final String APP_VERSION = "appVersion";
    private static final String USE_AUTOLOCATION = "useAutolocation";
    private static final String IS_NEW_VERSION = "isNewVersion";
    private static final String GOOGLE_PLAY_SERVICES_SHOWN = "googlePlayServicesShown";
    private static final String FAVORITE_SONGS = "favoriteSongs";
    private static final String IS_SCREEN_AWAKE = "isScreenAwake";

    private final static Gson gson = new Gson();

    static {
        prefs = PreferenceManager.getDefaultSharedPreferences(AppApplication.getApplication());
    }


    private static SharedPreferences prefs;

    public static void clear() {
        prefs.edit().clear().commit();
    }

    public static String getCurrentVersion() {
        return get(APP_VERSION, null);
    }

    public static void setCurrentVersion(String value) {
        set(APP_VERSION, value);
    }

    public static boolean showGooglePlayService() {
        return get(GOOGLE_PLAY_SERVICES_SHOWN, true);
    }

    public static void setShowGooglePlayService(boolean value) {
        set(GOOGLE_PLAY_SERVICES_SHOWN, value);
    }

    public static int getActivitiesViews() {
        return get(ACTIVITIES_VIEWS, 0);
    }

    public static void setActivitiesViews(int value) {
        set(ACTIVITIES_VIEWS, value);
    }

    public static void setFirstStart(boolean firstStart) {
        set(FIRST_START, firstStart);
    }

    public static boolean isFirstStart() {
        return get(FIRST_START, true);
    }

    public static void setShouldUpdateCountryTag(boolean shouldUpdateCountryTag) {
        set(SHOULD_UPDATE_COUNTRY_TAG, shouldUpdateCountryTag);
    }

    public static int getApiEndpoint() {
        return get(API_ENDPOINT, Constants.Endpoints.PRODUCTION);
    }

    public static void setApiEndpoint(int endpoint) {
        set(API_ENDPOINT, endpoint);
    }

    public static String getLanguageCode() {
        return ResourcesHelper.getString(R.string.language_code);
    }

    public static void setUseAutolocation(boolean useAutolocation) {
        set(USE_AUTOLOCATION, useAutolocation);
    }

    public static boolean getUseAutolocation() {
        return get(USE_AUTOLOCATION, true);
    }

    public static int get(String key, int _default) {
        return prefs.getInt(key, _default);
    }

    public static String get(String key, String _default) {
        return prefs.getString(key, _default);
    }

    public static float get(String key, float _default) {
        return prefs.getFloat(key, _default);
    }

    public static boolean get(String key, boolean _default) {
        return prefs.getBoolean(key, _default);
    }

    public static long get(String key, long _default) {
        return prefs.getLong(key, _default);
    }

    public static void set(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    public static void set(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    public static void set(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public static void set(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    public static void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    public static void remove(String key) {
        prefs.edit().remove(key).commit();
    }

    public static void setIsNewVersion(boolean isNewVersion) {
        set(IS_NEW_VERSION, isNewVersion);
    }

    public static boolean isNewVersion() {
        return get(IS_NEW_VERSION, false);
    }

    public static HashMap<String, Song> getFavoriteSongs() {
        String json = get(FAVORITE_SONGS, null);
        if (json != null) {
            Type mapType = new TypeToken<HashMap<String, Song>>() {
            }.getType();
            return gson.fromJson(json, mapType);
        }
        return null;
    }

    public static void setFavoriteSongs(HashMap<String, Song> favoriteSongs) {
        Type mapType = new TypeToken<HashMap<String, Song>>() {
        }.getType();
        set(FAVORITE_SONGS, gson.toJson(favoriteSongs, mapType));
    }

    public static boolean isScreenAwake() {
        return get(IS_SCREEN_AWAKE, true);
    }

    public static void setScreenAwake(boolean screenAwake) {
        set(IS_SCREEN_AWAKE, screenAwake);
    }
}
