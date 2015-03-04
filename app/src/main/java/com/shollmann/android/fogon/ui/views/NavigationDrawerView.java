package com.shollmann.android.fogon.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.ui.activities.FavoriteSongsActivity;
import com.shollmann.android.fogon.ui.activities.HomeActivity;
import com.shollmann.android.fogon.ui.activities.ServiceActivity;
import com.shollmann.android.fogon.util.IntentFactory;

public class NavigationDrawerView extends LinearLayout implements View.OnClickListener {
    private View view;
    private TextView btnHome;
    private TextView btnFavoriteSongs;
    private TextView btnSendSongs;
    private TextView btnReportSongs;
    private ServiceActivity activity;

    public NavigationDrawerView(Context context) {
        super(context);
        initialize();
    }

    public NavigationDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    @SuppressLint("NewApi")
    public NavigationDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        view = inflate(getContext(), R.layout.fragment_drawer, this);

        activity = (ServiceActivity) getContext();

        btnHome = (TextView) view.findViewById(R.id.drawer_home);
        btnFavoriteSongs = (TextView) view.findViewById(R.id.drawer_favorite_songs);
        btnSendSongs = (TextView) view.findViewById(R.id.drawer_send_song);
        btnReportSongs = (TextView) view.findViewById(R.id.drawer_report_song);

        btnHome.setOnClickListener(this);
        btnFavoriteSongs.setOnClickListener(this);
        btnSendSongs.setOnClickListener(this);
        btnReportSongs.setOnClickListener(this);
    }


    public void update() {
        unselectAllItems();
        markItemAsSelected();
    }

    private void markItemAsSelected() {
        if (activity instanceof HomeActivity) {
            btnHome.setTextColor(ResourcesHelper.getResources().getColor(R.color.secondary));
        } else if (activity instanceof FavoriteSongsActivity) {
            btnFavoriteSongs.setTextColor(ResourcesHelper.getResources().getColor(R.color.secondary));
        }
    }

    private void unselectAllItems() {
        btnHome.setTextColor(ResourcesHelper.getResources().getColor(R.color.black));
        btnFavoriteSongs.setTextColor(ResourcesHelper.getResources().getColor(R.color.black));

    }

    @Override
    public void onClick(View v) {
        activity.closeDrawer();

        switch (v.getId()) {
            case R.id.drawer_home:
                if (!(activity instanceof HomeActivity)) {
                    activity.startActivity(IntentFactory.getHomeActivity());
                }
                break;
            case R.id.drawer_favorite_songs:
                if (!(activity instanceof FavoriteSongsActivity)) {
                    activity.startActivity(IntentFactory.getFavoriteSongsActivity());
                }
                break;
        }
    }
}
