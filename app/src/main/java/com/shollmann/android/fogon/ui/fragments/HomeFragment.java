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

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.adapters.SongsFilteredAdapter;
import com.shollmann.android.fogon.helpers.BundleHelper;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.util.Comparators;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.wood.helpers.LogInternal;
import com.shollmann.android.wood.network.NetworkUtilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends BaseFragment implements TextWatcher, View.OnTouchListener {
    public static final String ORDER_CRITERIA = "author";
    private static final String LIST_POSITION = "songPositionOnList";
    private static final String SONGS = "songList";
    public static final int MILLIS_IN_FUTURE = 300;
    public static final int COUNT_DOWN_INTERVAL = 300;
    private ListView listviewSongs;
    private EditText edtSearch;
    private ArrayList<Song> arraySongs = new ArrayList<>();
    private ArrayList<Song> arrayOriginalSongs = new ArrayList<>();
    private View view;
    private SongsFilteredAdapter adapter;
    private CountDownTimer timerFilterList;
    private String keyword;
    private int listScrollPosition;

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            arraySongs = BundleHelper.fromBundle(savedInstanceState, SONGS, new ArrayList<Song>());
            listScrollPosition = BundleHelper.fromBundle(savedInstanceState, LIST_POSITION);
        }
        return view;
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void initialize() {
        super.initialize();
        getSupportActionBar().setTitle(ResourcesHelper.getString(R.string.app_name));

        edtSearch = (EditText) view.findViewById(R.id.home_songs_search);
        edtSearch.addTextChangedListener(this);
        edtSearch.setOnTouchListener(this);

        listviewSongs = (ListView) view.findViewById(R.id.home_songs_listview);
        adapter = new SongsFilteredAdapter(getActivity(), arraySongs);
        listviewSongs.setAdapter(adapter);

        if (arraySongs.isEmpty()) {
            getSongs();
        } else {
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

        TrackerHelper.trackScreenName(HomeFragment.this.getClass().getSimpleName());
    }

    private void getSongs() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.Model.SONGS);
        if (!NetworkUtilities.isConnected()) {
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        } else {
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(Constants.Parse.CACHE_DAYS_TIME));
        }
        query.setLimit(Constants.Parse.MAX_LIST_SIZE);
        query.orderByAscending(ORDER_CRITERIA);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
                    populateSongsList(parseObjects);
                } else {
                    LogInternal.error("Songs Error: " + e.getMessage());
                }
            }
        });
    }

    private void populateSongsList(List<ParseObject> list) {
        arrayOriginalSongs.clear();
        arraySongs.clear();
        for (ParseObject object : list) {
            arraySongs.add(new Song(object));
            arrayOriginalSongs.add(new Song(object));
        }
        adapter.notifyDataSetChanged();
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
                    adapter.notifyDataSetChanged();
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
