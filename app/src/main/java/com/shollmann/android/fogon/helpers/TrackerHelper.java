package com.shollmann.android.fogon.helpers;

import android.content.Context;

import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.wood.helpers.MixPanelHelper;

public class TrackerHelper {

    public static final String ADDED_TO_FAVORITE_SONGS = "added_to_favorite_songs";
    private static final String SEND_NEW_SONG = "send_new_song";
    private static final String REPORT_SONG = "report_song";
    private static final String REMOVED_FROM_FAVORITE_SONGS = "removed_from_favorite_songs";
    private static final String SEARCH_TOUCHED = "search_touched";

    public static void initTrackers(Context context) {
        MixPanelHelper.initMixPanel(context);
    }

    public static void trackScreenName(String screenName) {
        MixPanelHelper.trackPageView(screenName);
    }

    public static void trackAddToFavorites(Song song) {
        MixPanelHelper.trackFavoritedSongEvent(ADDED_TO_FAVORITE_SONGS, song.getName(), song.getAuthor());
    }

    public static void trackRemoveFromFavorites() {
        MixPanelHelper.trackEvent(REMOVED_FROM_FAVORITE_SONGS);
    }

    public static void trackSearchTouched() {
        MixPanelHelper.trackEvent(SEARCH_TOUCHED);
    }

    public static void trackSubmitNewSong() {
        MixPanelHelper.trackEvent(SEND_NEW_SONG);
    }

    public static void trackReportSong() {
        MixPanelHelper.trackEvent(REPORT_SONG);
    }


    public static void flushEvents() {
        MixPanelHelper.flushEvents();
    }
}
