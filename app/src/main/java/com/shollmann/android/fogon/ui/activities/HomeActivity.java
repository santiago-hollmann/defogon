package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;

import com.shollmann.android.fogon.ui.fragments.HomeFragment;


public class HomeActivity extends BaseFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            setInitialFragment(HomeFragment.newInstance());
        }
    }
}
