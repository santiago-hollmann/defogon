package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.shollmann.android.fogon.util.IntentFactory;


public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(IntentFactory.getHomeActivity());
    }
}
