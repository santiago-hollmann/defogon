package com.shollmann.android.fogon.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.shollmann.android.fogon.DeFogonApplication;
import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.BundleHelper;
import com.shollmann.android.fogon.helpers.DialogHelper;
import com.shollmann.android.fogon.helpers.LogInternal;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.interfaces.DialogClickListener;
import com.shollmann.android.fogon.interfaces.IServiceActivity;
import com.shollmann.android.fogon.ui.views.NavigationDrawerView;
import com.shollmann.android.fogon.util.Constants;

public abstract class ServiceActivity extends AppCompatActivity
        implements DialogClickListener, IServiceActivity, Toolbar.OnMenuItemClickListener, OnRefreshListener {
    private static final int DRAWER_CLOSED = 0;
    private static final int DRAWER_OPENED = 1;
    public SwipeRefreshLayout swipeLayout;
    protected Bundle requestIds;
    protected FrameLayout main;
    protected boolean fullScreen = false;
    protected boolean isDrawerEnabled = true;
    private DeFogonApplication app;
    private boolean hideUpdating = false;
    private boolean slidingDrawer = false;
    private boolean showingDrawer = false;
    private CharSequence mTitle;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawer;
    private NavigationDrawerView navigationDrawerView;

    public ServiceActivity() {
        app = DeFogonApplication.getApplication();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogInternal.logUINavigation("Activity onCreate", getClass().getSimpleName());
        DialogHelper.resetProgressPresenceFlag();

        showingDrawer = BundleHelper.fromBundle(savedInstanceState, "showingDrawer", false);

        if (fullScreen) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DITHER, WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.activity_main);
        main = (FrameLayout) findViewById(R.id.main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        setupDrawerToggle();

        navigationDrawerView = (NavigationDrawerView) findViewById(R.id.navigation_drawer);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.primary, R.color.accent, R.color.secondary);

        if (!PreferencesHelper.isScreenAwake()) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void setupDrawerToggle() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                if (slideOffset == DRAWER_CLOSED) {
                    onCloseDrawer();
                } else if (slideOffset == DRAWER_OPENED) {
                    slidingDrawer = false;
                } else if (!slidingDrawer) {
                    onMoveDrawer();
                }
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(drawerToggle);
    }

    public void closeDrawer() {
        drawer.closeDrawers();
    }

    public void openDrawer() {
        drawer.openDrawer(navigationDrawerView, true);
    }

    private void onCloseDrawer() {
        showingDrawer = false;
        slidingDrawer = false;
        String tempTitle = getSupportActionBar().getTitle().toString();
        if (tempTitle.equalsIgnoreCase(Constants.EMPTY_STRING)) {
            getSupportActionBar().setTitle(mTitle);
        }
        supportInvalidateOptionsMenu();
    }

    private void onMoveDrawer() {
        slidingDrawer = true;
        supportInvalidateOptionsMenu();
        if (!getSupportActionBar().getTitle().equals(Constants.EMPTY_STRING)) {
            mTitle = getSupportActionBar().getTitle();
        }
        if (!showingDrawer) {
            showingDrawer = true;
            mTitle = getSupportActionBar().getTitle();
        }

        getSupportActionBar().setTitle(Constants.EMPTY_STRING);

        if (getCurrentFocus() != null) {
            DeFogonApplication.hideKeyboard(getCurrentFocus().getWindowToken());
        }
    }

    @Override
    public DeFogonApplication getApp() {
        return app;
    }

    @Override
    public final void hideUpdating() {
        swipeLayout.setRefreshing(false);
    }

    protected boolean isAnimationRunning(View v) {
        return (v.getAnimation() != null && !v.getAnimation().hasEnded());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        TrackerHelper.flushEvents();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isDrawerEnabled) {
            int menuSize = menu.size();
            for (int i = 0; i < menuSize; i++) {
                menu.getItem(i).setVisible(!slidingDrawer);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DialogHelper.hideProgress(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationDrawerView.update();
    }

    @Override
    public final void showUpdating() {
        swipeLayout.setRefreshing(true);
    }

    @Override
    public final void showUpdating(boolean show) {
        swipeLayout.setRefreshing(show);
    }

    @Override
    public final void triggerAnimation(final View view, boolean show) {
        triggerAnimation(view, show, R.anim.animation_updating_show, R.anim.animation_updating_hide);
    }

    @Override
    public final void triggerAnimation(final View view, boolean show, int showAnimationResource, int hideAnimationResource) {
        final boolean isVisible = view.getVisibility() == View.VISIBLE;
        if (isVisible == show) {
            return;
        }

        if (isAnimationRunning(view)) {
            if (!show) {
                hideUpdating = true;
            }
            return;

        }
        if (!isVisible) {
            view.setVisibility(View.VISIBLE);
        }

        Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, isVisible ? hideAnimationResource : showAnimationResource);
        logoMoveAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                if (!isVisible && hideUpdating) {
                    hideUpdating = false;
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            showUpdating(false);
                        }
                    });
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        view.startAnimation(logoMoveAnimation);
    }

    @Override
    public void lockMenu() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void unlockMenu() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle.setDrawerIndicatorEnabled(true);
    }

    public void showBackButton() {
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    public boolean isDrawerUnlocked() {
        return drawerToggle.isDrawerIndicatorEnabled();
    }

    public boolean canToggleDrawer() {
        return isDrawerEnabled && isDrawerUnlocked();
    }

    public boolean toggleDrawer() {
        if (canToggleDrawer()) {
            if (isDrawerOpen()) {
                closeDrawer();
            } else {
                openDrawer();
            }
            return true;
        }
        return false;
    }

    public boolean isDrawerOpen() {
        return drawer.isDrawerVisible(GravityCompat.START);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onSelectedItem(int dialogId, int position, String item) {
    }

    @Override
    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            DeFogonApplication.hideKeyboard(getCurrentFocus().getWindowToken());
        }
    }

    @Override
    public void finish() {
        finish(true);
    }

    public void finish(boolean animate) {
        super.finish();

        if (animate && isRegularAnimatedView()) {
            overridePendingTransition(R.anim.animation_appears_from_left, R.anim.animation_disappears_to_right);
        }
    }

    private boolean isRegularAnimatedView() {
        return !(this instanceof HomeActivity);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

        if (!isExternalIntent(intent)) {
            overridePendingTransition(R.anim.animation_appears_from_right, R.anim.animation_disappears_to_left);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        if (!isExternalIntent(intent)) {
            overridePendingTransition(R.anim.animation_appears_from_right, R.anim.animation_disappears_to_left);
        }
    }

    private boolean isExternalIntent(Intent intent) {
        return intent.getAction() == Intent.ACTION_VIEW || intent.getAction() == android.provider.Settings.ACTION_WIFI_SETTINGS ||
                intent.getAction() == Intent.ACTION_CHOOSER || intent.getAction() == Intent.ACTION_SEND || intent.getAction() == Intent.ACTION_DIAL;
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    public boolean isNavigationDrawerOpen() {
        return drawer.isDrawerOpen(navigationDrawerView);
    }

    public void closeNavigationDrawer() {
        drawer.closeDrawers();
    }

    private class CloseDrawerAnimation implements Runnable {
        @Override
        public void run() {
            closeDrawer();
        }
    }

}
