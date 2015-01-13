package com.lookeate.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.olx.olx.model.LeftMenuItem;
import com.olx.olx.ui.views.LeftMenuItemView;

import java.util.List;

public class MenuAdapter extends FastListAdapter<LeftMenuItem> {

    public MenuAdapter(Context context, List<LeftMenuItem> menu) {
        super(context, menu);
    }

    @Override
    public View getNewView(Context context, ViewGroup parent, int position) {
        return new LeftMenuItemView(context);
    }

    @Override
    protected void setData(View view, LeftMenuItem item, int pos) {
        ((LeftMenuItemView) view).setData(item);
    }

    @Override
    protected void updateData(View view, LeftMenuItem item, int pos) {
    }

}