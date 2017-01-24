package com.shollmann.android.fogon;

import android.content.Context;
import android.os.IBinder;
import android.os.StrictMode;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.parse.interceptors.ParseLogInterceptor;
import com.shollmann.android.fogon.helpers.LogInternal;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.util.Constants;

import io.fabric.sdk.android.Fabric;

public class DeFogonApplication extends android.app.Application {
    private static DeFogonApplication instance;

    public static DeFogonApplication getApplication() {
        return instance;
    }

    public static void hideKeyboard(IBinder token) {
        InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }

        instance = this;

        if (LogInternal.isDebugging() && LogInternal.isStrictMode()) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        }

        TrackerHelper.initTrackers(this);

        startParse();
        registerParsePushNotificationsService();

    }

    private void registerParsePushNotificationsService() {
        ParsePush.subscribeInBackground(Constants.EMPTY_STRING, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    LogInternal.log("Parse - Successfully subscribed to the broadcast channel.");
                } else {
                    LogInternal.error("Parse - Failed to subscribe for push " + e.getMessage());
                }
            }
        });
    }

    private void startParse() {
        Parse.initialize(new Parse.Configuration.Builder(getApplication())
                .applicationId(BuildConfig.APP_ID)
                .clientKey(BuildConfig.CLIENT_KEY)
                .server(BuildConfig.SERVER)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .build()
        );
    }

}
