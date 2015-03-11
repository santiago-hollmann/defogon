package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;

import com.shollmann.android.fogon.ui.fragments.FavoriteSongsFragment;


public class FavoriteSongsActivity extends BaseFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            setInitialFragment(FavoriteSongsFragment.newInstance());
        }
    }
}
