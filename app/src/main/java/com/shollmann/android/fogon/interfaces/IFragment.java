package com.shollmann.android.fogon.interfaces;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

public interface IFragment extends DialogClickListener {
    void initialize();

    boolean canIGoBack();

    void setActionBar(ActionBar actionBar);

    void onViewPagerSelected();

    void onViewPagerUnSelected();

    void onLocationFailed();

    MenuItem getOptionsMenuButton(Menu menu, int buttonId);

    void onReload();

    void onRefresh();

}
