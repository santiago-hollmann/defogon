package com.shollmann.android.fogon.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.lookeate.java.api.model.APIResponse;
import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.BundleHelper;
import com.shollmann.android.fogon.helpers.DialogHelper;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.interfaces.DialogClickListener;
import com.shollmann.android.fogon.interfaces.IServiceActivity;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.wood.arguments.ServiceArguments;
import com.shollmann.android.wood.helpers.LogInternal;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public abstract class ServiceActivity extends ActionBarActivity
        implements DialogClickListener, IServiceActivity, Toolbar.OnMenuItemClickListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, OnRefreshListener {
    private static final int DRAWER_CLOSED = 0;
    private static final int DRAWER_OPENED = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int DIALOG_LOGOUT = 8900;
    private static final String REQUEST_IDS = "requestIds";

    protected Bundle requestIds;
    private LocalReceiver receiver;
    protected FrameLayout main;
    private AppApplication app;
    private View updating;

    protected boolean fullScreen = false;
    private boolean hideUpdating = false;
    private boolean receiverRegistered = false;
    private boolean slidingDrawer = false;
    private boolean showingDrawer = false;
    protected boolean isDrawerEnabled = true;

    private LocationClient locationClient;
    private Location gpsLocation;

    private LocationRequest locationRequest;
    private CharSequence mTitle;
    private LocationListener locationListener;
    private boolean requestGPSLocation;
    protected boolean requestLastLocation;
    private boolean showAutolocatingDialog = true;
    private boolean isSearchBarActive;

    public SwipeRefreshLayout swipeLayout;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawer;

    public ServiceActivity() {
        app = AppApplication.getApplication();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogInternal.logUINavigation("Activity onCreate", getClass().getSimpleName());
        DialogHelper.resetProgressPresenceFlag();

        showingDrawer = BundleHelper.fromBundle(savedInstanceState, "showingDrawer", false);

        requestIds = BundleHelper.fromBundle(savedInstanceState, REQUEST_IDS, new Bundle());

        if (fullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DITHER, WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.activity_main);
        main = (FrameLayout) findViewById(R.id.main);

        updating = findViewById(R.id.updating);
        updating.setVisibility(View.GONE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        receiver = new LocalReceiver();
        registerReceiver();

        setupDrawerToggle();
        setUpLocationClient();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(R.color.blue, R.color.light_blue, R.color.dark_blue);
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
        drawer.closeDrawer(R.layout.fragment_drawer);
    }

    public void openDrawer() {
        drawer.openDrawer(R.layout.fragment_drawer);
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
            AppApplication.hideKeyboard(getCurrentFocus().getWindowToken());
        }
    }

    private void setUpLocationClient() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(0);
        locationClient = new LocationClient(this, this, this);
    }

    @Override
    public void changeLocation() {
        chooseManualLocation();
    }

    @Override
    public void cleanupRequestIds() {
        requestIds.clear();
    }

    @Override
    public AppApplication getApp() {
        return app;
    }

    private void getLocationGPS(double latitude, double longitude) {
        if (requestLastLocation && showAutolocatingDialog) {
            DialogHelper.showProgress(this, null, getString(R.string.autolocating));
        }
        // Do something with latitude and longitude
    }

    @Override
    public String getRequestId(String key) {
        return requestIds.getString(key);
    }

    @Override
    public final void hideUpdating() {
        swipeLayout.setRefreshing(false);
    }

    protected boolean isAnimationRunning(View v) {
        return (v.getAnimation() != null && !v.getAnimation().hasEnded());
    }

    @Override
    public boolean isMyRequest(APIResponse response, String requestId) {
        if (!TextUtils.isEmpty(response.getRequestId())) {
            boolean isMyRequest = response.getRequestId().equalsIgnoreCase(getRequestId(requestId));
            if (isMyRequest) {
                removeResponse(response.getRequestId());
                removeRequestId(requestId);
            }
            return isMyRequest;
        }
        return false;
    }

    @Override
    public boolean isRunning(String key) {
        return requestIds.containsKey(key);
    }

    private synchronized boolean isRunningRequestId(String requestId) {
        Set<String> keys = requestIds.keySet();
        for (String key : keys) {
            if (requestId.equalsIgnoreCase(getRequestId(key))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String makeNetworkCall(ServiceArguments args, String key) {
        return makeNetworkCall(args, key, true);
    }

    @Override
    public String makeNetworkCall(final ServiceArguments args, String key, boolean showLoading) {
        String requestId = requestIds.getString(key);
        if (requestId == null) {

            requestId = UUID.randomUUID().toString();
            requestIds.putString(key, requestId);
            args.setRequestId(requestId);

            LogInternal.logServiceCall("Call Requested", args.getLogMessage());
            getApp().makeServiceCallAsync(args);

            if (showLoading) {
                LogInternal.logServiceCall("Open Dialog", args.getLogMessage());
                DialogHelper.showProgress(this, null, getString(R.string.connecting));
            }
        } else {
            args.setRequestId(requestId);
            LogInternal.logServiceCall("Call Skipped", args.getLogMessage());
        }

        return requestId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // WHAT IS THIS GOOD FOR?
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onConnected(Bundle data) {
        if (locationListener != null) {
            if (requestGPSLocation) {
                locationClient.requestLocationUpdates(locationRequest, this);
            } else {
                stopRequestLocation();
            }
        } else if (requestLastLocation) {
            setLastKnownLocation();
        }
    }

    private void setLastKnownLocation() {
        gpsLocation = getLastLocation();
        if (gpsLocation == null) {
            requestLastLocation = false;
            chooseManualLocation();
        } else {
            getLocationGPS(gpsLocation.getLatitude(), gpsLocation.getLongitude());
        }
    }

    private void chooseManualLocation() {
        PreferencesHelper.setUseAutolocation(false);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            if (PreferencesHelper.showGooglePlayService()) {
                PreferencesHelper.setShowGooglePlayService(false);
                Dialog errorDialog =
                        GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                if (errorDialog != null) {
                    DialogHelper.show(this, errorDialog);
                }
            }
        }
    }

    @Override
    public void firePendingResponsesAsync() {
        getApp().firePendingResponsesAsync();
    }

    @Override
    public void onDisconnected() {
    }

    protected void onInternalResultReceived(Intent intent) {
        APIResponse response = (APIResponse) intent.getSerializableExtra(Constants.ExtraKeys.DATA);

        LogInternal.logServiceCall("Results Received", response != null ? response.getLogMessage() : "null");
        onResultReceived(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveClick(int dialogId) {
        if (dialogId == DIALOG_LOGOUT) {
            // Do something
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        if (dialogId == DIALOG_LOGOUT) {
            // Do something
        }
    }

    @Override
    public void onCancel(int dialogId) {
        if (dialogId == DIALOG_LOGOUT) {
            // Do something
        }
    }

    @Override
    public void onDismiss(int dialogId) {
        if (dialogId == DIALOG_LOGOUT) {
            // Do something
        }
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

    public abstract void onResultReceived(Intent intent);

    public abstract void onResultReceivedNoError(Intent intent);

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.disconnect();
        DialogHelper.hideProgress(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationClient.connect();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        receiverRegistered = false;
        locationClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(REQUEST_IDS, requestIds);
        outState.putBoolean("showingDrawer", showingDrawer);
    }

    private void registerReceiver() {
        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.Actions.MESSAGE);
            filter.addAction(Constants.Actions.NETWORK);
            filter.addAction(Constants.Actions.CONNECTIONS);
            filter.addDataScheme(Constants.ExtraKeys.SCHEME);
            LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
        }
    }

    @Override
    public void removeRequestId(String key) {
        requestIds.remove(key);
        if (requestIds.size() == 0) {
            DialogHelper.hideProgress(ServiceActivity.this);
            showUpdating(false);
        }
    }

    @Override
    public APIResponse removeResponse(String requestId) {
        return getApp().removeResponse(requestId);
    }

    private boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            if (PreferencesHelper.showGooglePlayService()) {
                PreferencesHelper.setShowGooglePlayService(false);
                Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

                if (errorDialog != null) {
                    DialogHelper.show(this, errorDialog);
                }
            }
            return false;
        }
    }

    public Location getLastLocation() {
        if (locationClient.isConnected()) {
            return locationClient.getLastLocation();
        }
        return null;
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
        return isDrawerEnabled && isDrawerUnlocked() && !isSearchBarActive();
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

    private class CloseDrawerAnimation implements Runnable {
        @Override
        public void run() {
            closeDrawer();
        }
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
    public void processError(APIResponse response) {

    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (Constants.Actions.NETWORK.equalsIgnoreCase(intent.getAction())) {
                        // TODO SHOW TOAST ERROR ?
                    } else if (Constants.Actions.CONNECTIONS.equalsIgnoreCase(intent.getAction())) {
                        showUpdating(getApp().hasNetworkActivity());
                        return;
                    }
                    APIResponse response = (APIResponse) intent.getSerializableExtra(Constants.ExtraKeys.DATA);
                    if (isRunningRequestId(response.getRequestId())) {
                        onInternalResultReceived(intent);
                    }
                }
            });
        }
    }

    @Override
    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            AppApplication.hideKeyboard(getCurrentFocus().getWindowToken());
        }
    }

    @Override
    public void requestLocation(LocationListener listener) {
        requestGPSLocation = true;
        locationListener = listener;
        if (locationClient.isConnected()) {
            locationClient.requestLocationUpdates(locationRequest, this);
        } else {
            locationClient.connect();
        }
    }

    @Override
    public void stopRequestLocation() {
        requestGPSLocation = false;
        if (locationListener != null && locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
    }

    @Override
    public boolean isLocationServiceEnabled() {
        if (servicesConnected()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            ArrayList<String> providers = new ArrayList<String>(locationManager.getProviders(true));
            if (providers.size() == 0 || (providers.size() == 1 && providers.get(0).equalsIgnoreCase("passive"))) {
                return false;
            }
            return true;
        }
        return false;
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
        //TODO Remove ServiceActivity and put HomeActivity
        return !(this instanceof ServiceActivity);
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
                intent.getAction() == Intent.ACTION_SEND || intent.getAction() == Intent.ACTION_DIAL;
    }

    @Override
    public boolean isSearchBarActive() {
        return isSearchBarActive;
    }

    @Override
    public void setSearchBarStatus(boolean status) {
        isSearchBarActive = status;
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        gpsLocation = location;
        DialogHelper.hideProgress(this);
        getLocationGPS(location.getLatitude(), location.getLongitude());
    }


    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }
}
