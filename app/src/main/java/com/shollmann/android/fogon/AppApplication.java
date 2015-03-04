package com.shollmann.android.fogon;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.IBinder;
import android.os.StrictMode;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.lookeate.java.api.model.APIResponse;
import com.parse.Parse;
import com.shollmann.android.fogon.helpers.PreferencesHelper;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.helpers.Utilities;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.fogon.util.IntentFactory;
import com.shollmann.android.wood.CoreLibApplication;
import com.shollmann.android.wood.arguments.ServiceArguments;
import com.shollmann.android.wood.helpers.LogInternal;
import com.shollmann.android.wood.network.NetworkUtilities;
import com.shollmann.android.wood.services.DataService;

import java.io.Serializable;
import java.lang.reflect.Type;

import io.fabric.sdk.android.Fabric;

public class AppApplication extends android.app.Application {
    public static final int INITIALIZE_VERSION = 1;
    private static AppApplication instance;

    private DataService dataService;
    private boolean dataServiceStarting = false;
    private Object dataServiceLock = new Object();
    private static final int MAX_SERVICE_WAIT_TIME = 10000;

    private boolean locationChanged = false;

    private boolean hasCheckedForForceUpdate = false;

    private final static String mapKey = Constants.MAPS_PROD_KEY;

    private final ServiceConnection dataServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            dataService = ((DataService.DataBinder) binder).getService();
            synchronized (dataServiceLock) {
                LogInternal.logServiceBinding("notifying dataService is ready", Constants.EMPTY_STRING);
                dataServiceStarting = false;
                dataServiceLock.notifyAll();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            synchronized (dataServiceLock) {
                //Having this working would have allowed us to handle the binding with a simple dataServiceBound flag.
                //    but this event is never received during the process of killing the DataService.
                //    So our assumption of an available service has to be:  dataService != null && !dataService.isOutOfService()
                dataServiceStarting = false;
                LogInternal.logServiceBinding("dataService has disconnected", Constants.EMPTY_STRING);
            }
        }
    };


    public void EnsureDataServiceReady(final Runnable workload) {
        if (isDataServiceAvailable()) {
            LogInternal.logServiceBinding("assume dataService ready", "without waiting");
            workload.run();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (dataServiceLock) {
                        if (isDataServiceAvailable()) {
                            //This is the proper and safe place to check if the service is already bound.
                            //    because both the "if (dataServiceBound)" and the "dataServiceLock.wait(...)" are in the same synchronized
                            //The previous one (at the beginning of this func) is just a performance improvement case
                            //    to avoid generating useless Threads and Locks once everything is up and running.
                            workload.run();
                        } else {
                            if (!dataServiceStarting) {
                                //Nobody has asked for the service to get started. So we should do it
                                LogInternal.logServiceBinding("starting-up dataService", "from EnsureDataServiceReady");
                                initializeServices();
                            }

                            try {
                                LogInternal.logServiceBinding("waiting for dataService", Constants.EMPTY_STRING);
                                dataServiceLock.wait(MAX_SERVICE_WAIT_TIME);
                            } catch (InterruptedException ex) {
                                //This situation would only happen if the app is killed by android during it's startup (highly unlikely)
                                //    and the only reason for this code block is to register that unlikely event on Crashlytics
                                LogInternal.logServiceBinding("error while waiting for dataService", "POSSIBLE FATAL ERROR");
                            }

                            //I'm done waiting and I have to re-check once again if the service is available because maybe the waiting
                            //    ended up because of a timeout-waiting.
                            if (isDataServiceAvailable()) {
                                LogInternal.logServiceBinding("assume dataService ready", "after waiting for it");
                                workload.run();
                            } else {
                                //I can't run the workload because if it's not properly protected it can trigger a NullPointerException.
                                //    and crash the app. I choose not to run it and risk putting the app in a strange state.
                                LogInternal.logServiceBinding("timeout while waiting for dataService", "POSSIBLE FATAL ERROR");
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public static AppApplication getApplication() {
        return instance;
    }

    public static void hideKeyboard(IBinder token) {
        InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    public static void openKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public boolean isDataServiceAvailable() {
        return dataService != null && !dataService.isOutOfService();
    }

    public boolean hasNetworkActivity() {
        if (dataService != null && dataService.getConnections() > 0) {
            return true;
        }
        return false;
    }

    public void makeServiceCallAsync(final ServiceArguments request) {
        EnsureDataServiceReady(new Runnable() {
            @Override
            public void run() {
                dataService.makeServiceCall(request);
            }
        });
    }

    /**
     * This function should only be used after checking for isDataServiceBound() or through the EnsureDataServiceReady(workload) function
     */
    public void saveCache(Serializable data, String key) {
        if (dataService != null) {
            dataService.saveCache(data, key);
        }
    }

    /**
     * This function should only be used after checking for isDataServiceBound() or through the EnsureDataServiceReady(workload) function
     */
    public <T> T loadCache(String key, Type clazz) {
        if (dataService != null) {
            return dataService.loadCache(key, clazz);
        }
        return null;
    }

    /**
     * This function should only be used after checking for isDataServiceBound() or through the EnsureDataServiceReady(workload) function
     */
    public void deleteCache(String key) {
        if (dataService != null) {
            dataService.deleteCache(key);
        }
    }

    public APIResponse removeResponse(String requestId) {
        if (dataService != null) {
            return dataService.removeResponse(requestId);
        }
        return null;
    }

    public void firePendingResponsesAsync() {
        EnsureDataServiceReady(new Runnable() {
            @Override
            public void run() {
                dataService.firePendingResponses();
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!LogInternal.isDebugging()) {
            Fabric.with(this, new Crashlytics());
        }
        instance = this;

        LogInternal.logServiceBinding("starting-up dataService", "from Application.onCreate");
        initializeServices();


        Utilities.setIsNewVersion();

        if (!LogInternal.isDebugging()) {

        }

        CoreLibApplication.getInstance().initialize(this, getUrl(), Utilities.getVersion(), Constants.PLATFORM,
                this.getResources().getConfiguration().locale.getLanguage(), useMock(), LogInternal.isDebugging());

        initializeTypefaces();
        NetworkUtilities.setNetworkStatus();

        if (PreferencesHelper.isNewVersion()) {
            PreferencesHelper.setShouldUpdateCountryTag(true);
        }

        if (LogInternal.isDebugging() && LogInternal.isStrictMode()) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        }

        TrackerHelper.initTrackers(this);

        startParse();

    }

    private void startParse() {
        Parse.initialize(this, "nl7pJ17IaIt2uiPmRyeSLvgVAFyOQvzwRZepRPMa", "Dw7IGglTweWafyTfRUQRbI9NqtOEWlz7bd5sauV0");
    }

    private boolean useMock() {
        return (PreferencesHelper.getApiEndpoint() == Constants.Endpoints.MOCK);
    }

    private String getUrl() {
        if (PreferencesHelper.getApiEndpoint() == Constants.Endpoints.PRODUCTION) {
            return Constants.URL;
        } else if (PreferencesHelper.getApiEndpoint() == Constants.Endpoints.TESTING) {
            return Constants.URL_TESTING;
        }
        return null;
    }


    public void resetApi() {
        CoreLibApplication.getInstance().setUrl(getUrl());
    }

    public void initializeServices() {
        dataServiceStarting = true;
        startService(IntentFactory.getDataServiceIntent());
        bindService(IntentFactory.getDataServiceIntent(), dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        dataServiceStarting = false;
        if (isDataServiceAvailable()) {
            unbindService(dataServiceConnection);
            if (dataService != null) {
                dataService.stopSelf();
            }
        }

        super.onTerminate();
    }

    private void initializeTypefaces() {
        Fonts.HELVETICA_LIGHT = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTStd-Lt.otf");
        Fonts.HELVETICA_MEDIUM = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTStd-Md.otf");
        Fonts.HELVETICA_REGULAR = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLTStd-Roman.otf");
    }

    public static class Fonts {
        public static Typeface HELVETICA_LIGHT;
        public static Typeface HELVETICA_MEDIUM;
        public static Typeface HELVETICA_REGULAR;
    }

    public boolean isLocationChanged() {
        return locationChanged;
    }

    public void setLocationChanged(boolean locationChanged) {
        this.locationChanged = locationChanged;
    }


    public static String getMapKey() {
        return mapKey;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        CoreLibApplication.getInstance().setLanguageCode(PreferencesHelper.getLanguageCode());
    }

    public boolean hasCheckedForForceUpdate() {
        return hasCheckedForForceUpdate;
    }

    public void setHasCheckedForForceUpdate(boolean hasCheckedForForceUpdate) {
        this.hasCheckedForForceUpdate = hasCheckedForForceUpdate;
    }

}
