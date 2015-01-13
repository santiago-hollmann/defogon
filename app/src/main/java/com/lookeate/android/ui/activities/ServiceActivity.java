package com.lookeate.android.ui.activities;

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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
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
import com.lookeate.android.interfaces.DialogClickListener;
import com.lookeate.android.interfaces.IServiceActivity;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public abstract class ServiceActivity extends ActionBarActivity
        implements DialogClickListener, IServiceActivity, Toolbar.OnMenuItemClickListener, GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, OnLocationChangedListener, OnAuthenticationListener,
        OnCategorySelectedListener, OnRefreshListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int DIALOG_LOGOUT = 8900;
    protected final static String LOCATION_REQUEST_ID = "requestIdLocation";
    private static final String REQUEST_IDS = "requestIds";
    private static final String FORCE_UPDATE_REQUEST_ID = "force_update_request_id";
    private static final int DRAWER_CLOSED = 0;
    private static final int DRAWER_OPENED = 1;
    public static final int DIALOG_ENVIRONMENT = 10001;
    private static final int DIALOG_CHANGE_LOCATION = 78772;
    private static final int MIN_KIM_TO_CHANGE_LOCATION = 20;

    protected Bundle requestIds;
    private LocalReceiver receiver;
    protected FrameLayout main;
    private LeChuckApplication app;
    private View updating;

    protected boolean fullScreen = false;
    private boolean hideUpdating = false;
    private boolean receiverRegistered = false;
    private boolean slidingDrawer = false;
    private boolean showingDrawer = false;
    protected boolean isDrawerEnabled = true;

    private CustomDrawerLayout drawer;
    private LeftMenuView leftMenu;
    private ActionBarDrawerToggle drawerToggle;
    private LocationClient locationClient;
    private com.olx.smaug.api.model.Location location;
    private Location gpsLocation;

    private LocationRequest locationRequest;
    private CharSequence mTitle;
    private LocationListener locationListener;
    private boolean requestGPSLocation;
    protected boolean requestLastLocation;
    private int previousActionbarMode;
    private boolean requestCheckIsInNewLocation;
    private ResolvedLocation previousLocation;
    private boolean showAutolocatingDialog = true;
    private final CloseDrawerAnimation closeDrawerAnimation = new CloseDrawerAnimation();
    private String previousDrawerItem;
    private boolean isSearchBarActive;

    public SwipeRefreshLayout swipeLayout;

    public ServiceActivity() {
        app = LeChuckApplication.getApplication();
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

        drawer = (CustomDrawerLayout) findViewById(R.id.drawer_layout);
        leftMenu = (LeftMenuView) findViewById(R.id.left_drawer);
        leftMenu.setOnMenuItemListener(this);

        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        receiver = new LocalReceiver();
        registerReceiver();

        setUpDrawerToggle();
        setUpLocationClient();

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        Session fbSession = Session.getActiveSession();
        if (fbSession == null) {
            if (fbSession == null) {
                fbSession = new Session(this);
            }
            Session.setActiveSession(fbSession);
        }

        if (showingDrawer) {
            drawer.post(new Runnable() {

                @Override
                public void run() {
                    onMoveDrawer();
                }
            });
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(R.color.ptr_violet, R.color.ptr_violet, R.color.ptr_green, R.color.ptr_orange);

        //Skip for the SplashActivity because it can auto-navigate to HomeActivity allowing checkForForceUpdate to be called twice
        if (!(this instanceof SplashActivity)) {
            checkForForceUpdate(false);
        }
    }

    public void checkForForceUpdate(boolean forceCheck) {
        if (forceCheck || !LeChuckApplication.getApplication().hasCheckedForForceUpdate()) {
            ResolvedLocation resolvedLocation = PreferencesHelper.getResolvedLocation();
            if (resolvedLocation != null && resolvedLocation.getCountry() != null && resolvedLocation.getCountry().getUrl() != null) {
                makeNetworkCall(new ForceInstallArguments(resolvedLocation.getCountry().getUrl()), FORCE_UPDATE_REQUEST_ID, false);
            }
        }
    }

    private void setUpDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (setCallbacksForFiltersDrawer(drawerView, slideOffset)) {
                    return;
                }
                if (slideOffset == DRAWER_CLOSED) {
                    onCloseDrawer();
                } else if (slideOffset == DRAWER_OPENED) {
                    TrackerHelper.openDrawer();
                    slidingDrawer = false;
                } else if (!slidingDrawer) {
                    onMoveDrawer();
                }
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(drawerToggle);
    }

    private boolean setCallbacksForFiltersDrawer(View drawerView, float slideOffset) {
        if (drawerView instanceof RightMenuView) {
            if (slideOffset == DRAWER_OPENED) {
                ((ListingActivity) this).onFilterDrawerOpened();
            } else if (slideOffset == DRAWER_CLOSED) {
                ((ListingActivity) this).onFilterDrawerClose();
            }
            return true;
        }
        return false;
    }

    private void onCloseDrawer() {
        showingDrawer = false;
        slidingDrawer = false;
        String tempTitle = getSupportActionBar().getTitle().toString();
        if (tempTitle.equalsIgnoreCase(getString(R.string.app_name))) {
            getSupportActionBar().setTitle(mTitle);
        }
        getSupportActionBar().setNavigationMode(previousActionbarMode);
        supportInvalidateOptionsMenu();
    }

    private void onMoveDrawer() {
        slidingDrawer = true;
        supportInvalidateOptionsMenu();
        if (!getSupportActionBar().getTitle().equals(getString(R.string.app_name))) {
            mTitle = getSupportActionBar().getTitle();
        }
        if (!showingDrawer) {
            showingDrawer = true;
            mTitle = getSupportActionBar().getTitle();
            previousActionbarMode = getSupportActionBar().getNavigationMode();
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
        getSupportActionBar().setTitle(R.string.app_name);
        leftMenu.refreshMenu();
        if (getCurrentFocus() != null) {
            LeChuckApplication.hideKeyboard(getCurrentFocus().getWindowToken());
        }

    }

    @Override
    public boolean isShowingDrawer() {
        return slidingDrawer;
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
    public LeChuckApplication getApp() {
        return app;
    }

    private void getLocationGPS(double latitude, double longitude) {
        if (requestLastLocation && showAutolocatingDialog) {
            DialogHelper.showProgress(this, null, getString(R.string.auto_locating));
        }
        makeNetworkCall(new LocationArguments(latitude, longitude), LOCATION_REQUEST_ID, false);
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
        } else if (requestCheckIsInNewLocation) {
            requestResolvedLocation();
        }
    }

    private void setLastKnownLocation() {
        gpsLocation = getLastLocation();
        if (gpsLocation == null) {
            onLocationFailed();
            requestLastLocation = false;
            chooseManualLocation();
        } else {
            getLocationGPS(gpsLocation.getLatitude(), gpsLocation.getLongitude());
        }
    }

    private void chooseManualLocation() {
        PreferencesHelper.setUseAutolocation(false);
        if (PreferencesHelper.isFirstStart()) {
            startActivity(IntentFactory.getLocationSettingsIntent());
        }
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

    private void confirmLocation() {
        PreferencesHelper.setCategoriesCounter(new CategoriesCounter());
        LocationHelper.setResolvedLocation(new ResolvedLocation(location.getCountry(), location.getState(), location.getCity()));
        PreferencesHelper.setAutolocationCity(location.getCity());
        PreferencesHelper.setPublishLocation(null);
        onLocationChanged(PreferencesHelper.getResolvedLocation());
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
        if (isMyRequest(response, LOCATION_REQUEST_ID)) {
            handleLocationResponse(response);
        } else if (isMyRequest(response, FORCE_UPDATE_REQUEST_ID)) {
            handleForceUpdateResponse(intent, response);
        } else {
            onResultReceived(intent);
        }
    }

    private void handleForceUpdateResponse(Intent intent, APIResponse response) {
        DialogHelper.hideProgress(this);
        if (response.isSuccess()) {
            LeChuckApplication.getApplication().setHasCheckedForForceUpdate(true);
            ForceInstall forceInstall = (ForceInstall) response;

            if (forceInstall.isHasToUpdate() && this instanceof BaseFragmentActivity) {
                startActivity(IntentFactory.getForceInstallActivityIntent(forceInstall));
                if (forceInstall.isBlocking()) {
                    finish();
                }
            }
        }
    }

    private void handleLocationResponse(APIResponse response) {
        DialogHelper.hideProgress(this);
        if (response.isSuccess()) {
            location = (com.olx.smaug.api.model.Location) response;
            UrbanAirshipHelper.updateCountryTag(location.getCountry().getName());
            if (requestGPSLocation) {
                if (locationListener != null) {
                    Bundle extras = new Bundle();
                    extras.putSerializable(Constants.ExtraKeys.LOCATION, location);
                    gpsLocation.setExtras(extras);
                    locationListener.onLocationChanged(gpsLocation);
                }
            } else if (requestLastLocation) {
                requestLastLocation = false;
                confirmLocation();
                TrackerHelper.trackUserInstallation(true);
            } else if (requestCheckIsInNewLocation) {
                handleCheckForNewLocation();
            }
        } else {
            chooseManualLocation();
        }
    }

    private void handleCheckForNewLocation() {
        requestCheckIsInNewLocation = false;
        ResolvedLocation newLocation = new ResolvedLocation(location.getCountry(), location.getState(), location.getCity());
        if (previousLocation.isSameCountry(newLocation)) {
            if (LocationHelper.calculateDistanceInKM(previousLocation, newLocation) > MIN_KIM_TO_CHANGE_LOCATION) {
                LocationHelper.setResolvedLocation(newLocation);
                onLocationChanged(newLocation);
            }
        } else {
            DialogHelper.show(this, Constants.EMPTY_STRING,
                    String.format(getString(R.string.change_city_dialog), newLocation.getCity().getName(),
                            newLocation.getCountry().getName()), R.string.ok, R.string.cancel, 0, DIALOG_CHANGE_LOCATION);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        gpsLocation = location;
        DialogHelper.hideProgress(this);
        getLocationGPS(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onMenuItemClick(View v, final LeftMenuItem item) {
        drawer.closeDrawer(leftMenu);
        previousDrawerItem = LeftMenuView.getMenuItemId();
        if (!item.getId().equalsIgnoreCase(LeftMenuView.getMenuItemId())) {
            LeftMenuView.setMenuItemId(item.getId());
            if (!item.isCategory()) {
                if (item.getId().equalsIgnoreCase(LeftMenuView.MenuItems.LOGIN)) {
                    if (PreferencesHelper.getUser() != null) {
                        handleLogout();
                        return;
                    }
                }
                startActivity(IntentFactory.getActivityIntent(item.getId()));
            } else {
                TrackerHelper.drawerCategory();
                TrackerHelper.browseCategoryForReply();

                navigateToSubCategories(item.getCategory());
                if (this instanceof ListingActivity) {
                    return;
                }
            }

            if (!(this instanceof HomeActivity)) {
                finish(false);
            }
        }
    }

    protected void navigateToMyAds() {
        startActivity(IntentFactory.getActivityIntent(LeftMenuView.getMenuItemId()));
    }

    protected void navigateToItem(long itemId) {
        Item item = new Item();
        item.setId(itemId);
        LeChuckApplication.getApplication().clearItems();
        LeChuckApplication.getApplication().setItemsArguments(null);
        startActivity(IntentFactory.getItemActivityIntent(item, -1, true));
        overridePendingTransition(0, 0);
    }

    protected void navigateToItemFromUri(long itemId) {
        Item item = new Item();
        item.setId(itemId);

        LeChuckApplication.getApplication().clearItems();
        LeChuckApplication.getApplication().setItemsArguments(null);

        TaskStackBuilder tsb = TaskStackBuilder.create(this);
        tsb.addNextIntent(IntentFactory.getHomeActivityIntent());
        tsb.addNextIntent(IntentFactory.getItemActivityIntent(item, -1, false));
        tsb.startActivities();
        overridePendingTransition(0, 0);
    }

    private void handleLogout() {
        // TODO research why onCancel is not call
        DialogHelper
                .show(this, Constants.EMPTY_STRING, getString(R.string.are_you_sure_you_want_to_logout), R.string.ok, R.string.cancel, 0,
                        DIALOG_LOGOUT, false);
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
            LeftMenuView.setMenuItemId(LeftMenuView.MenuItems.HOME);
            onLogout();
            startActivity(IntentFactory.getHomeActivityIntent());
        } else if (dialogId == FeedbackUtilities.DIALOG_FEEDBACK_INITIAL) {
            startActivity(FeedbackUtilities.sendToSurvey(this));
        } else if (dialogId == DIALOG_CHANGE_LOCATION) {
            confirmLocation();
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        if (dialogId == FeedbackUtilities.DIALOG_FEEDBACK_INITIAL) {
            // do nothing
        } else if (dialogId == DIALOG_LOGOUT) {
            if (previousDrawerItem != null) {
                LeftMenuView.setMenuItemId(previousDrawerItem);
            }
        }
    }

    @Override
    public void onCancel(int dialogId) {
        if (dialogId == DIALOG_LOGOUT) {
            if (previousDrawerItem != null) {
                LeftMenuView.setMenuItemId(previousDrawerItem);
            }
        }
    }

    @Override
    public void onDismiss(int dialogId) {
        if (dialogId == DIALOG_LOGOUT) {
            if (previousDrawerItem != null) {
                LeftMenuView.setMenuItemId(previousDrawerItem);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
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
        TrackerHelper.reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationClient.disconnect();
        DialogHelper.hideProgress(this);
        TrackerHelper.reportActivityStop(this);
    }

    @Override
    protected void onResume() {
        ApptimizeHelper.runExperimentsOnServiceActivityResume();
        super.onResume();
        LeftMenuView.setMenuItemIdByClass(this);
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
            filter.addAction(Constants.Actions.POSTING);
            filter.addAction(Constants.Actions.UPLOADING);
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

    public final void setMenuCategories(ArrayList<Category> categories) {
        leftMenu.setMenuItems(categories);
    }

    public void setHomeLocation(boolean firstStart) {
        showAutolocatingDialog = firstStart;
        if (isLocationServiceEnabled()) {
            requestLastLocation = true;
            if (locationClient.isConnected()) {
                setLastKnownLocation();
            } else {
                locationClient.connect();
            }
        } else {
            chooseManualLocation();
        }
    }

    public void detectNewLocation() {
        previousLocation = PreferencesHelper.getResolvedLocation();
        if (isLocationServiceEnabled()) {
            requestLastLocation = false;
            requestCheckIsInNewLocation = true;
            locationClient.connect();
            if (locationClient.isConnected()) {
                requestResolvedLocation();
            }
        }
    }

    private void requestResolvedLocation() {
        gpsLocation = getLastLocation();
        if (gpsLocation != null) {
            makeNetworkCall(new LocationArguments(gpsLocation.getLatitude(), gpsLocation.getLongitude()), LOCATION_REQUEST_ID, false);
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void unlockMenu() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        drawerToggle.setDrawerIndicatorEnabled(true);
    }

    public void showBackButton() {
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    public boolean isDrawerUnlocked() {
        return drawerToggle.isDrawerIndicatorEnabled();
    }

    @Override
    public void onSelectedItem(int dialogId, int position, String item) {
    }

    @Override
    public void processError(APIResponse response) {

    }

    @Override
    public void onLogout() {
        PreferencesHelper.setUser(null);
        GrogApplication.getInstance().clearToken();
        Session.getActiveSession().closeAndClearTokenInformation();
        getApp().setUser(null);
        ItemsHelper.deleteFailedItemsAsync();

        //ABTEST 4.15 - Add to Favorites from Listing
        LeChuckApplication.getApplication().clearLoggedinLocalFavorites();
    }

    @Override
    public void onLogin() {
        if (LeftMenuView.getMenuItemId().equalsIgnoreCase(LeftMenuView.MenuItems.LOGIN)) {
            LeftMenuView.setMenuItemId(LeftMenuView.MenuItems.HOME);
        }

        PreferencesHelper.setContactName(null);
        PreferencesHelper.setContactPhone(null);
        ItemsHelper.deleteFailedItemsAsync();
    }

    @Override
    public void authenticateWithFacebook() {
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
                    if (Constants.Actions.POSTING.equalsIgnoreCase(intent.getAction())) {
                        intent.putExtra(Constants.ExtraKeys.DATA, new APIResponse());
                        onInternalResultReceived(intent);
                    } else if (Constants.Actions.UPLOADING.equalsIgnoreCase(intent.getAction())) {
                        intent.putExtra(Constants.ExtraKeys.DATA, new APIResponse());
                        onInternalResultReceived(intent);
                    } else if (isRunningRequestId(response.getRequestId())) {
                        onInternalResultReceived(intent);
                    }
                }
            });
        }
    }

    @Override
    public final void toggleSwitch(Menu menu, boolean show) {
        MenuItem postMenuItem = menu.findItem(R.id.menu_posting);
        if (postMenuItem != null) {
            postMenuItem.setVisible(show);
        }
    }

    private void navigateToSubCategories(Category category) {
        //abtest Subcategories on Listing
        if (ApptimizeHelper.isSubcategoriesListingEnabled()) {
            startActivity(IntentFactory.getListItemActivity(null, category));
        } else {
            hideKeyboard();
            startActivity(IntentFactory.getSubcategoriesActivityIntent(category));
        }
    }

    @Override
    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            LeChuckApplication.hideKeyboard(getCurrentFocus().getWindowToken());
        }
    }

    @Override
    public void onCategorySelected(Category category) {
        navigateToSubCategories(category);
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

        if (animate && !isRegularAnimatedView()) {
            overridePendingTransition(R.anim.animation_appears_from_left, R.anim.animation_disappears_to_right);
        }
    }

    private boolean isRegularAnimatedView() {
        return this instanceof HomeActivity || this instanceof SplashActivity || this instanceof ForceInstallActivity;
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
    public void setActionBarToList(ArrayList<Category> subcategories, OnNavigationListener listener, int itemSelected) {
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayList<Value> values = new ArrayList<Value>();
        values.add(new Value(null, subcategories.get(0).getTrName()));
        for (int i = 1; i < subcategories.size(); i++) {
            values.add(new Value(null, subcategories.get(i).getTrName()));
        }

        CategorySpinnerAdapter spinner = new CategorySpinnerAdapter(this, R.layout.view_spinner_category, values);

        getSupportActionBar().setListNavigationCallbacks(spinner, listener);
        getSupportActionBar().setSelectedNavigationItem(itemSelected);
    }

    @Override
    public void openDrawerShowCase() {
        openDrawer();
        leftMenu.postDelayed(closeDrawerAnimation, 1250);
    }

    public void openDrawer() {
        drawer.openDrawer(leftMenu);
    }

    public void closeDrawer() {
        drawer.closeDrawer(leftMenu);
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

    public ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }
}
