package com.shollmann.android.fogon.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.util.Comparators;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.wood.helpers.LogInternal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends BaseFragment implements TextWatcher {
    private ListView listviewSongs;
    private EditText edtSearch;
    private ArrayList<Song> arraySongs = new ArrayList<>();
    private View view;
    private SongsFilteredAdapter adapter;
    private String keyword;

    public HomeFragment() {
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
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

        listviewSongs = (ListView) view.findViewById(R.id.home_songs_listview);
        adapter = new SongsFilteredAdapter(getActivity(), arraySongs);
        listviewSongs.setAdapter(adapter);

        getSongs();
    }

    private void getSongs() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.Model.SONGS);
        query.orderByAscending("author");
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
        arraySongs.clear();
        for (ParseObject object : list) {
            arraySongs.add(new Song(object));
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
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void sort() {
        Collections.sort(arraySongs, Comparators.comparatorSongs);
        adapter.notifyDataSetChanged();
    }
}
