package com.shollmann.android.fogon.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.model.Song;

import java.util.HashMap;

public class SongItemView extends RelativeLayout implements View.OnClickListener {
    private TextView txtNameAuthor;
    private TextView txtChords;
    private ImageView btnFavorite;
    private Song song;

    public SongItemView(Context context) {
        this(context, null);
    }

    public SongItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @SuppressLint("NewApi")
    public SongItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
        inflate(getContext(), R.layout.view_list_song, this);
        txtNameAuthor = (TextView) findViewById(R.id.song_item_author_name);
        txtChords = (TextView) findViewById(R.id.song_item_chords);
        btnFavorite = (ImageView) findViewById(R.id.song_favorite_btn);
        btnFavorite.setOnClickListener(this);
    }

    public void setData(Song song) {
        if (song != null) {
            this.song = song;
            txtChords.setText(song.getChords());
            txtNameAuthor.setText(String.format(ResourcesHelper.getString(R.string.word_word), song.getAuthor(), song.getName()));
            btnFavorite.setImageDrawable(ResourcesHelper.getDrawable(song.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star_empty));
        }
    }

    public void updateData(Song item) {
        setData(item);
    }

    @Override
    public void onClick(View v) {
        HashMap<String, Song> favoriteSongs = PreferencesHelper.getFavoriteSongs();
        if (favoriteSongs == null) {
            favoriteSongs = new HashMap<>();
        }
        if (!song.isFavorite()) {
            addToFavorites(favoriteSongs);
        } else {
            removeFromFavorites(favoriteSongs);
        }

        btnFavorite.setImageDrawable(ResourcesHelper.getDrawable(song.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star_empty));
    }

    private void removeFromFavorites(HashMap<String, Song> favoriteSongs) {
        song.setFavorite(false);
        favoriteSongs.remove(song.getObjectId());
        PreferencesHelper.setFavoriteSongs(favoriteSongs);
        TrackerHelper.trackRemoveFromFavorites();
    }

    private void addToFavorites(HashMap<String, Song> favoriteSongs) {
        song.setFavorite(true);
        favoriteSongs.put(song.getObjectId(), song);
        PreferencesHelper.setFavoriteSongs(favoriteSongs);
        TrackerHelper.trackAddToFavorites(song);
    }
}
