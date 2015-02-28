package com.shollmann.android.fogon.util;

import com.shollmann.android.fogon.model.Song;

import java.util.Comparator;

public class Comparators {

    public static Comparator<Song> comparatorSongs = new Comparator<Song>() {
        @Override
        public int compare(Song song0, Song song1) {
            if (song0 != null && song1 != null) {
                return song0.getName().compareToIgnoreCase(song1.getName());
            } else {
                return 0;
            }
        }
    };

}
