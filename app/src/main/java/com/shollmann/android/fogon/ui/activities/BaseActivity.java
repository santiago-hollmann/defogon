package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.DialogHelper;
import com.shollmann.android.fogon.interfaces.IError;

public abstract class BaseActivity extends ServiceActivity implements IError {
    private void initActionBar() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    @Override
    public void onCancel(int dialogId) {
        super.onCancel(dialogId);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        findViewById(R.id.activity_fragment_layout).setVisibility(View.VISIBLE);
        initActionBar();
    }

    @Override
    public void onDismiss(int dialogId) {
        super.onDismiss(dialogId);
    }

    @Override
    public void onNegativeClick(int dialogId) {
        super.onNegativeClick(dialogId);
    }

    @Override
    public void onNeutralClick(int dialogId) {
    }

    @Override
    public void onPositiveClick(int dialogId) {
        super.onPositiveClick(dialogId);
    }

    @Override
    public void onSelectedItem(int dialogId, int position, String item) {
        super.onSelectedItem(dialogId, position, item);
    }

    @Override
    public View getCustomDialogView(int dialogId) {
        return null;
    }

    @Override
    public void showError(String title, String message, int dialogId) {
        if (getSupportFragmentManager().findFragmentByTag("error") == null) {
            DialogHelper.showError(this, title, message, dialogId);
        }
    }
}
