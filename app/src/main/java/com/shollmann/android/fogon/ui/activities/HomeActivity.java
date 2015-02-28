package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;
import android.view.View;

import com.shollmann.android.fogon.ui.fragments.HomeFragment;


public class HomeActivity extends BaseFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockMenu();
        setInitialFragment(HomeFragment.newInstance());
    }

    @Override
    public void onClick(View v) {

    }
}
