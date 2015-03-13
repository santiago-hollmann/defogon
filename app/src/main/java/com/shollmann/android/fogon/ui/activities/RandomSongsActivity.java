package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;

import com.shollmann.android.fogon.ui.fragments.RandomSongsFragment;

public class RandomSongsActivity extends BaseFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if (savedInstance == null) {
            setInitialFragment(RandomSongsFragment.newInstance());
        }
    }
}
