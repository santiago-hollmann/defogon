package com.shollmann.android.wood;

import android.content.Context;
import android.content.res.AssetManager;

import com.shollmann.android.wood.db.CachingDbHelper;
import com.lookeate.java.api.contract.LookeateAPI;

public class CoreLibApplication {
    private Context context;
    private LookeateAPI api;
    private static boolean isDebug;
    private CachingDbHelper cachingDbHelper;

    private static CoreLibApplication instance;

    public static CoreLibApplication getInstance() {
        if (instance == null) {
            instance = new CoreLibApplication();
        }
        return instance;
    }

    public void initialize(Context context, String url, String appVersion, String platform, String languageCode, boolean log) {
        initialize(context, url, appVersion, platform, languageCode, false, log);
    }

    public void initialize(Context context, String url, String appVersion, String platform, String languageCode, boolean useMock,
                           boolean log) {
        this.context = context;
        isDebug = log;
        this.api = new LookeateAPI(url, appVersion, platform, languageCode, log);
        this.cachingDbHelper = new CachingDbHelper(context);
    }

    public Context getContext() {
        return context;
    }

    public void setLanguageCode(String languageCode) {
        api.setLanguageCode(languageCode);
    }

    public void setUrl(String url) {
        api.setUrl(url);
    }

    public LookeateAPI getApi() {
        return api;
    }


    public void clearToken() {
        api.setToken(null);
    }

    public AssetManager getAssets() {
        return context.getAssets();
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public CachingDbHelper getCachingDbHelper() {
        return cachingDbHelper;
    }

    public String getLanguage() {
        return api.getLanguageCode();
    }

    public String getAppVersion() {
        return api.getVersion();
    }

}
