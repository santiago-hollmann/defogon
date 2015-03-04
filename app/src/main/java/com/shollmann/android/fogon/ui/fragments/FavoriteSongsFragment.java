package com.shollmann.android.fogon.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.adapters.SongsFilteredAdapter;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.util.Comparators;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteSongsFragment extends BaseFragment implements TextWatcher, View.OnTouchListener {
    private ListView listviewSongs;
    private EditText edtSearch;
    private ArrayList<Song> arraySongs = new ArrayList<>();
    private View view;
    private SongsFilteredAdapter adapter;
    private String keyword;
    private TextView txtNoFavorites;

    public FavoriteSongsFragment() {
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_songs, container, false);
        return view;
    }

    public static FavoriteSongsFragment newInstance() {
        return new FavoriteSongsFragment();
    }

    @Override
    public void initialize() {
        super.initialize();
        getSupportActionBar().setTitle(ResourcesHelper.getString(R.string.favorite_songs));

        txtNoFavorites = (TextView) view.findViewById(R.id.favorite_songs_no_favs);

        edtSearch = (EditText) view.findViewById(R.id.favorite_songs_search);
        edtSearch.addTextChangedListener(this);
        edtSearch.setOnTouchListener(this);

        listviewSongs = (ListView) view.findViewById(R.id.favorite_songs_listview);
        adapter = new SongsFilteredAdapter(getActivity(), arraySongs);
        listviewSongs.setAdapter(adapter);

        getSongs();

        TrackerHelper.trackScreenName(FavoriteSongsFragment.this.getClass().getSimpleName());
    }

    private void getSongs() {
        if (hasFavoriteSongs()) {
            edtSearch.setVisibility(View.VISIBLE);
            listviewSongs.setVisibility(View.VISIBLE);
            txtNoFavorites.setVisibility(View.GONE);

            arraySongs.clear();
            arraySongs.addAll(PreferencesHelper.getFavoriteSongs().values());
            sort();
        } else {
            edtSearch.setVisibility(View.GONE);
            listviewSongs.setVisibility(View.GONE);
            txtNoFavorites.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasFavoriteSongs() {
        return PreferencesHelper.getFavoriteSongs() != null && PreferencesHelper.getFavoriteSongs().size() > 0;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable text) {
        keyword = text.toString();
        if (keyword.length() >= 3) {
            adapter.setSongArrayList(arraySongs);
            filter(keyword);
        } else {
            getSongs();
        }
    }

    private void filter(String keyword) {
        if (adapter != null && adapter.getFilter() != null) {
            adapter.getFilter().filter(keyword, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int count) {
                    sort();
                }
            });
        }
    }

    private void sort() {
        Collections.sort(arraySongs, Comparators.comparatorSongs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TrackerHelper.trackSearchTouched();
        return false;
    }
}
