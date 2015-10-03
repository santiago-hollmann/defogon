package com.shollmann.android.fogon.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.shollmann.android.fogon.helpers.BundleHelper;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.util.Comparators;

import java.util.ArrayList;
import java.util.Collections;

public class FavoriteSongsFragment extends BaseFragment implements TextWatcher, View.OnTouchListener {
    private static final String LIST_POSITION = "itemListPosition";
    private static final String SONGS = "songs";
    public static final int MILLIS_IN_FUTURE = 300;
    public static final int COUNT_DOWN_INTERVAL = 300;

    private ListView listviewSongs;
    private EditText edtSearch;
    private ArrayList<Song> arraySongs = new ArrayList<>();
    private ArrayList<Song> arrayOriginalSongs = new ArrayList<>();
    private View view;
    private SongsFilteredAdapter adapter;
    private String keyword;
    private TextView txtNoFavorites;
    private int listScrollPosition;
    private CountDownTimer timerFilterList;


    public FavoriteSongsFragment() {
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite_songs, container, false);

        if (savedInstanceState != null) {
            arraySongs = BundleHelper.fromBundle(savedInstanceState, SONGS, new ArrayList<Song>());
            listScrollPosition = BundleHelper.fromBundle(savedInstanceState, LIST_POSITION);
        }

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

        if (arraySongs.isEmpty()) {
            getSongs();
        } else {
            edtSearch.setVisibility(View.VISIBLE);
            listviewSongs.setVisibility(View.VISIBLE);
            txtNoFavorites.setVisibility(View.GONE);
            listviewSongs.setSelection(listScrollPosition);
        }

        timerFilterList = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                arraySongs = (ArrayList<Song>) arrayOriginalSongs.clone();
                adapter.setSongArrayList(arraySongs);
                filter(keyword);
            }
        };

        TrackerHelper.trackScreenName(FavoriteSongsFragment.this.getClass().getSimpleName());
    }

    private void getSongs() {
        if (hasFavoriteSongs()) {
            edtSearch.setVisibility(View.VISIBLE);
            listviewSongs.setVisibility(View.VISIBLE);
            txtNoFavorites.setVisibility(View.GONE);

            arraySongs.clear();
            arrayOriginalSongs.clear();
            arraySongs.addAll(PreferencesHelper.getFavoriteSongs().values());
            arrayOriginalSongs.addAll(PreferencesHelper.getFavoriteSongs().values());
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
        if (keyword.length() == 0) {
            getSongs();
        } else {
            timerFilterList.cancel();
            timerFilterList.start();
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
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TrackerHelper.trackSearchTouched();
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIST_POSITION, listviewSongs.getFirstVisiblePosition());
        outState.putSerializable(SONGS, arraySongs);
    }
}
