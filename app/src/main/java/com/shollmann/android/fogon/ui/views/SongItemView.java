package com.shollmann.android.fogon.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.model.Song;

public class SongItemView extends RelativeLayout {
    private TextView txtNameAuthor;
    private TextView txtChords;

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
    }

    public void setData(Song song) {
        if (song != null) {
            txtChords.setText(song.getChords());
            txtNameAuthor.setText(String.format(ResourcesHelper.getString(R.string.word_word), song.getAuthor(), song.getName()));
        }
    }

    public void updateData(Song item) {
        setData(item);
    }
}
