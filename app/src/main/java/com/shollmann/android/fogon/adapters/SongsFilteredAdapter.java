package com.shollmann.android.fogon.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.ui.views.SongItemView;
import com.shollmann.android.fogon.util.Constants;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

public class SongsFilteredAdapter extends FastListAdapter<Song> implements Filterable {

    public static final String SPECIAL_CHARS_REGEX = "\\p{InCombiningDiacriticalMarks}+";
    private final ArrayList<Song> songArrayList = new ArrayList<Song>();

    public SongsFilteredAdapter(Context context, ArrayList<Song> songArrayList) {
        super(context, songArrayList);
    }

    public void setSongArrayList(ArrayList<Song> songArrayList) {
        this.songArrayList.clear();
        this.songArrayList.addAll(songArrayList);
        getAllItems().clear();
        getAllItems().addAll(songArrayList);
        notifyDataSetChanged();
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

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                getAllItems().clear();
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    getAllItems().addAll((ArrayList<Song>) results.values);
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Song> filteredList = getFilteredList(constraint);
                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();

                return results;
            }
        };
    }

    private ArrayList<Song> getFilteredList(CharSequence constraint) {
        ArrayList<Song> results = new ArrayList<>();
        String songWithoutAccents;
        String songNormalized;

        if (TextUtils.isEmpty(constraint)) {
            results.addAll(songArrayList);
            return results;
        } else {
            String keyword = constraint.toString().toLowerCase(Locale.US);
            String normalizedKeyword = Normalizer.normalize(keyword, Normalizer.Form.NFD);
            String keywordWithoutAccents = normalizedKeyword.replaceAll(SPECIAL_CHARS_REGEX, Constants.EMPTY_STRING);

            for (Song song : songArrayList) {
                songNormalized = Normalizer.normalize(song.toString().toLowerCase(Locale.US), Normalizer.Form.NFD);
                songWithoutAccents = songNormalized.replaceAll(SPECIAL_CHARS_REGEX, Constants.EMPTY_STRING);

                if (songWithoutAccents.contains(keywordWithoutAccents)) {
                    results.add(song);
                }
            }

            return results;
        }
    }

}
