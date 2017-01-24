package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.shollmann.android.fogon.R;

public abstract class BaseActivity extends ServiceActivity {
    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        findViewById(R.id.activity_fragment_layout).setVisibility(View.VISIBLE);
        initActionBar();
    }

    @Override
    public void onNeutralClick(int dialogId) {
    }

    @Override
    public void onSelectedItem(int dialogId, int position, String item) {
        super.onSelectedItem(dialogId, position, item);
    }

    @Override
    public View getCustomDialogView(int dialogId) {
        return null;
    }
}
