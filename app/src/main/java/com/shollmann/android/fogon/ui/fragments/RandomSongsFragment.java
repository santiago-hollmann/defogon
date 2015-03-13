package com.shollmann.android.fogon.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.BundleHelper;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.model.Song;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.wood.helpers.LogInternal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSongsFragment extends BaseFragment implements View.OnClickListener {
    public static final String ORDER_CRITERIA = "author";
    private static final String SONGS = "songs";
    private static final String CURRENT_SONG_POSITION = "currentSongPosition";

    private View view;
    private ArrayList<Song> arraySongs = new ArrayList<>();
    private TextView txtSongTitle;
    private TextView txtSongChords;
    private Button btnNextSong;
    private int newSongPosition = -1;

    public static RandomSongsFragment newInstance() {
        return new RandomSongsFragment();
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_random_song, container, false);
        if (savedInstanceState != null) {
            arraySongs = BundleHelper.fromBundle(savedInstanceState, SONGS, new ArrayList<Song>());
            newSongPosition = BundleHelper.fromBundle(savedInstanceState, CURRENT_SONG_POSITION);
        }
        return view;
    }

    @Override
    public void initialize() {
        TrackerHelper.trackScreenName(RandomSongsFragment.this.getClass().getSimpleName());

        setTitle(getSupportActionBar(), ResourcesHelper.getString(R.string.random_mode));

        txtSongTitle = (TextView) view.findViewById(R.id.random_song_title);
        txtSongChords = (TextView) view.findViewById(R.id.random_song_chords);
        btnNextSong = (Button) view.findViewById(R.id.random_btn_next);

        btnNextSong.setOnClickListener(this);

        if (arraySongs.isEmpty()) {
            getSongs();
        } else if (newSongPosition != -1) {
            setSongFields(newSongPosition);
        }
        super.initialize();

    }

    private void displayNewSong() {
        if (!arraySongs.isEmpty()) {
            Random rnd = new Random();
            newSongPosition = rnd.nextInt(arraySongs.size());
            setSongFields(newSongPosition);
        }

    }

    private void getSongs() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.Model.SONGS);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ONLY);
        query.setLimit(Constants.Parse.MAX_LIST_SIZE);
        query.orderByAscending(ORDER_CRITERIA);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
                    populateSongsList(parseObjects);
                    displayNewSong();
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
    }

    private void setSongFields(int newSongPosition) {
        Song newSong = arraySongs.get(newSongPosition);

        txtSongTitle.setText(String.format(ResourcesHelper.getString(R.string.word_word), newSong.getAuthor(), newSong.getName()));
        txtSongChords.setText(newSong.getChords());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.random_btn_next) {
            displayNewSong();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SONGS, arraySongs);
        outState.putInt(CURRENT_SONG_POSITION, newSongPosition);
    }
}
