package com.shollmann.android.fogon.helpers;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.shollmann.android.wood.helpers.ConfigurationHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class MixPanelHelper {
    public static final String PROP_FRAGMENT_NAME = "Fragment Name";
    public static final String ANDROID_PAGE_VIEW = "android_page_view";
    private static final String PROP_SONG_FAVORITED = "Favorite Song";
    private static final String PROP_FAVORITED_AUTHOR = "Favorite Author";
    private static final String IS_AWAKE = "Is Awake";
    private static MixpanelAPI mixpanel;

    public static void initMixPanel(Context context) {
        mixpanel = MixpanelAPI.getInstance(context, ConfigurationHelper.MIXPANEL_KEY);
    }

    public static void trackPageView(String screenName) {
        try {
            JSONObject props = new JSONObject();
            props.put(PROP_FRAGMENT_NAME, screenName);
            mixpanel.track(ANDROID_PAGE_VIEW, props);
        } catch (JSONException e) {
        }
    }

    public static void trackFavoritedSongEvent(String eventName, String songName, String songAuthor) {
        try {
            JSONObject props = new JSONObject();
            props.put(PROP_SONG_FAVORITED, songName + "_" + songAuthor);
            props.put(PROP_FAVORITED_AUTHOR, songAuthor);
            mixpanel.track(eventName, props);
        } catch (JSONException e) {
        }
    }

    public static void trackScreenAwakeEvent(String eventName, boolean isAwake) {
        try {
            JSONObject props = new JSONObject();
            props.put(IS_AWAKE, isAwake);
            mixpanel.track(eventName, props);
        } catch (JSONException e) {
        }
    }

    public static void trackEvent(String eventName, JSONObject props) {
        mixpanel.track(eventName, props);
    }

    public static void trackEvent(String eventName) {
        JSONObject props = new JSONObject();
        MixPanelHelper.trackEvent(eventName, props);
    }

    public static void flushEvents() {
        mixpanel.flush();
    }
}
