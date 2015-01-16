package com.lookeate.android.ui.fragments;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lookeate.android.AppApplication;
import com.lookeate.android.R;
import com.lookeate.android.interfaces.IOnReload;
import com.lookeate.android.util.Constants;

public class NoDataAvailableFragment extends BaseFragment {

    public static NoDataAvailableFragment newInstance() {
        NoDataAvailableFragment fragment = new NoDataAvailableFragment();
        return fragment;
    }

    @Override
    public View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nodata_available, container, false);

        TextView txtMessage = (TextView) view.findViewById(R.id.nodata_message);
        txtMessage.setTypeface(AppApplication.Fonts.HELVETICA_LIGHT);

        showSearchMenu = false;

        return view;
    }

    @Override
    public void setActionBar(ActionBar bar) {
        super.setActionBar(bar);
        setTitle(bar, Constants.EMPTY_STRING);
    }

    @Override
    public boolean canIGoBack() {
        return true;
    }

    @Override
    public void onReload() {
        ((IOnReload) getActivity()).onReload();
    }

}
