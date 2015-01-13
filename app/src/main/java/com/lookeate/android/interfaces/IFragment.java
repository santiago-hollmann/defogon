package com.lookeate.android.interfaces;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.lookeate.java.api.model.APIResponse;

public interface IFragment extends DialogClickListener {

    public void setResponse(APIResponse response, String action);

    public void setResponse(APIResponse response, String action, boolean handleError);

    public void initialize();

    public boolean canIGoBack();

    public void setActionBar(ActionBar actionBar);

    public void onViewPagerSelected();

    public void onViewPagerUnSelected();

    public void onLocationFailed();

    public MenuItem getOptionsMenuButton(Menu menu, int buttonId);

    public void onReload();

    void onRefresh();

}
