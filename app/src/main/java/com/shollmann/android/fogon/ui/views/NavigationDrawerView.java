package com.shollmann.android.fogon.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.ui.activities.FavoriteSongsActivity;
import com.shollmann.android.fogon.ui.activities.HomeActivity;
import com.shollmann.android.fogon.ui.activities.RandomSongsActivity;
import com.shollmann.android.fogon.ui.activities.ServiceActivity;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.fogon.util.IntentFactory;

public class NavigationDrawerView extends LinearLayout implements View.OnClickListener {
    private static final int MIN_FAVORITES_TO_SHOW_REVIEW = 2;

    private View view;
    private TextView btnHome;
    private TextView btnFavoriteSongs;
    private TextView btnRandomMode;
    private TextView btnSendSongs;
    private TextView btnReportSongs;
    private TextView btnRateUs;
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
        btnRateUs = (TextView) view.findViewById(R.id.drawer_rate_us);

        btnRateUs.setVisibility(canShowRateUs() ? View.VISIBLE : View.GONE);

        btnHome.setOnClickListener(this);
        btnFavoriteSongs.setOnClickListener(this);
        btnSendSongs.setOnClickListener(this);
        btnReportSongs.setOnClickListener(this);
        btnRandomMode.setOnClickListener(this);
        btnRateUs.setOnClickListener(this);
    }

    private boolean canShowRateUs() {
        return PreferencesHelper.getFavoriteSongs() != null && PreferencesHelper.getFavoriteSongs().size() >= MIN_FAVORITES_TO_SHOW_REVIEW;
    }

    public void update() {
        unselectAllItems();
        markItemAsSelected();
    }

    private void markItemAsSelected() {
        if (activity instanceof HomeActivity) {
            btnHome.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_all_songs, getContext().getTheme()), null, null, null);
            btnHome.setTextColor(ResourcesHelper.getResources().getColor(R.color.primary));
        } else if (activity instanceof FavoriteSongsActivity) {
            btnFavoriteSongs.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_bookmark, getContext().getTheme()), null, null, null);
            btnFavoriteSongs.setTextColor(ResourcesHelper.getResources().getColor(R.color.primary));
        } else if (activity instanceof RandomSongsActivity) {
            btnRandomMode.setSelected(true);
            btnRandomMode.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_shuffle, getContext().getTheme()), null, null, null);
            btnRandomMode.setTextColor(ResourcesHelper.getResources().getColor(R.color.primary));
        }
    }

    private void unselectAllItems() {
        btnHome.setTextColor(ResourcesHelper.getResources().getColor(R.color.dark));
        btnFavoriteSongs.setTextColor(ResourcesHelper.getResources().getColor(R.color.dark));
        btnRandomMode.setTextColor(ResourcesHelper.getResources().getColor(R.color.dark));

        btnRandomMode.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_shuffle_gray, getContext().getTheme()), null, null, null);
        btnHome.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_all_songs_gray, getContext().getTheme()), null, null, null);
        btnFavoriteSongs.setCompoundDrawablesWithIntrinsicBounds(VectorDrawableCompat.create(getResources(), R.drawable.ic_bookmark_gray, getContext().getTheme()), null, null, null);
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
            case R.id.drawer_rate_us:
                TrackerHelper.trackRateUsClicked();
                openPlayStore();
                break;
            case R.id.drawer_random_mode:
                if (!(activity instanceof RandomSongsActivity)) {
                    activity.startActivity(IntentFactory.getRandomSongsActivity());
                }
                break;
        }
    }

    private void openPlayStore() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PLAYSTORE_URL + Constants.APP_PKG_NAME)));
        } catch (Exception e) {

        }
    }
}
