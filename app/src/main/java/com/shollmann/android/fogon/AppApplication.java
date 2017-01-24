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
import com.shollmann.android.fogon.helpers.LogInternal;
import com.shollmann.android.fogon.helpers.TrackerHelper;
import com.shollmann.android.fogon.util.Constants;

import io.fabric.sdk.android.Fabric;

public class AppApplication extends android.app.Application {
    private static AppApplication instance;

    public static AppApplication getApplication() {
        return instance;
    }

    public static void hideKeyboard(IBinder token) {
        InputMethodManager imm = (InputMethodManager) instance.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!LogInternal.isDebugging()) {
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
                .applicationId("nl7pJ17IaIt2uiPmRyeSLvgVAFyOQvzwRZepRPMa")
                .clientKey("Dw7IGglTweWafyTfRUQRbI9NqtOEWlz7bd5sauV0")
                .server("https://pg-app-eo2qa3qreym21d23p7cwhukyg2qeax.scalabl.cloud/1/")
                .build()
        );
    }

}
