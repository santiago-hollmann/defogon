package com.shollmann.android.fogon.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.ui.views.SongItemView;

import java.util.ArrayList;

public class SongsAdapter extends FastListAdapter<Song> {

    public SongsAdapter(Context context, ArrayList<Song> songs) {
        super(context, songs);
    }

    @Override
    public View getNewView(Context context, ViewGroup parent, int position) {
        return new SongItemView(context);
    }

    @Override
    protected void setData(View view, Song item, int pos) {
        SongItemView aiv = (SongItemView) view;
        aiv.setData(item);
    }

    @Override
    protected void updateData(View view, Song item, int pos) {
        ((SongItemView) view).updateData(item);
    }

}
