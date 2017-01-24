package com.shollmann.android.fogon.helpers;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shollmann.android.fogon.DeFogonApplication;
import com.shollmann.android.fogon.model.Song;

import java.lang.reflect.Type;
import java.util.HashMap;

public class PreferencesHelper {
    private static final String USE_AUTOLOCATION = "useAutolocation";
    private static final String FAVORITE_SONGS = "favoriteSongs";
    private static final String IS_SCREEN_AWAKE = "isScreenAwake";

    private final static Gson gson = new Gson();
    private static SharedPreferences prefs;

    static {
        prefs = PreferenceManager.getDefaultSharedPreferences(DeFogonApplication.getApplication());
    }

    public static void clear() {
        prefs.edit().clear().commit();
    }

    public static void setUseAutolocation(boolean useAutolocation) {
        set(USE_AUTOLOCATION, useAutolocation);
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

    public static HashMap<String, Song> getFavoriteSongs() {
        String json = get(FAVORITE_SONGS, null);
        if (json != null) {
            Type mapType = new TypeToken<HashMap<String, Song>>() {
            }.getType();
            return gson.fromJson(json, mapType);
        }
        return new HashMap<>();
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
