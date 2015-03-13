package com.shollmann.android.fogon.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.ui.activities.FavoriteSongsActivity;
import com.shollmann.android.fogon.ui.activities.HomeActivity;
import com.shollmann.android.fogon.ui.activities.RandomSongsActivity;
import com.shollmann.android.fogon.ui.activities.ServiceActivity;
import com.shollmann.android.fogon.util.IntentFactory;

public class NavigationDrawerView extends LinearLayout implements View.OnClickListener {
    private View view;
    private TextView btnHome;
    private TextView btnFavoriteSongs;
    private TextView btnRandomMode;
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
        btnRandomMode = (TextView) view.findViewById(R.id.drawer_random_mode);
        btnSendSongs = (TextView) view.findViewById(R.id.drawer_send_song);
        btnReportSongs = (TextView) view.findViewById(R.id.drawer_report_song);

        btnHome.setOnClickListener(this);
        btnFavoriteSongs.setOnClickListener(this);
        btnSendSongs.setOnClickListener(this);
        btnReportSongs.setOnClickListener(this);
        btnRandomMode.setOnClickListener(this);
    }


    public void update() {
        unselectAllItems();
        markItemAsSelected();
    }

    private void markItemAsSelected() {
        if (activity instanceof HomeActivity) {
            btnHome.setCompoundDrawablesWithIntrinsicBounds(ResourcesHelper.getDrawable(R.drawable.ic_drawer_songs_selected), null, null, null);
            btnHome.setTextColor(ResourcesHelper.getResources().getColor(R.color.secondary));
        } else if (activity instanceof FavoriteSongsActivity) {
            btnFavoriteSongs.setCompoundDrawablesWithIntrinsicBounds(ResourcesHelper.getDrawable(R.drawable.ic_drawer_favorites_selected), null, null, null);
            btnFavoriteSongs.setTextColor(ResourcesHelper.getResources().getColor(R.color.secondary));
        } else if (activity instanceof RandomSongsActivity) {
            btnRandomMode.setSelected(true);
            btnRandomMode.setCompoundDrawablesWithIntrinsicBounds(ResourcesHelper.getDrawable(R.drawable.ic_drawer_random_selected), null, null, null);
            btnRandomMode.setTextColor(ResourcesHelper.getResources().getColor(R.color.secondary));
        }
    }

    private void unselectAllItems() {
        btnHome.setTextColor(ResourcesHelper.getResources().getColor(R.color.black));
        btnFavoriteSongs.setTextColor(ResourcesHelper.getResources().getColor(R.color.black));
        btnRandomMode.setTextColor(ResourcesHelper.getResources().getColor(R.color.black));

        btnRandomMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawer_random_unselected, 0, 0, 0);
        btnHome.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawer_songs_unselected, 0, 0, 0);
        btnFavoriteSongs.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_drawer_favorites_unselected, 0, 0, 0);
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
            case R.id.drawer_send_song:
                TrackerHelper.trackSubmitNewSong();
                activity.startActivity(Intent.createChooser(IntentFactory
                                .getSendEmailActivity(ResourcesHelper.getString(R.string.send_new_song_subject)),
                        ResourcesHelper.getString(R.string.send_email)));
                break;
            case R.id.drawer_report_song:
                TrackerHelper.trackReportSong();
                activity.startActivity(Intent.createChooser(IntentFactory
                                .getSendEmailActivity(ResourcesHelper.getString(R.string.report_song_subject)),
                        ResourcesHelper.getString(R.string.send_email)));
                break;
            case R.id.drawer_random_mode:
                if (!(activity instanceof RandomSongsActivity)) {
                    activity.startActivity(IntentFactory.getRandomSongsActivity());
                }
                break;
        }
    }
}
