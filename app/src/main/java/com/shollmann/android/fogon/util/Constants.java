package com.shollmann.android.fogon.util;

public class Constants extends com.shollmann.android.wood.Constants {
    public final static String URL = "http://api-prod.com/";
    public final static String URL_TESTING = "http://api-testing.com/";
    public static final String PLATFORM = "android";
    public static final String MAPS_PROD_KEY = "";
    public static final String PHONE_INFO = "phoneInfo";
    public static final String CONTACT_EMAIL = "defogonandroid@gmail.com";
    public static final String APP_PKG_NAME = "com.shollmann.android.fogon";
    public static final String PLAYSTORE_URL = "market://details?id=";

    public static class ExtraKeys extends com.shollmann.android.wood.Constants {
        public static final String SEARCH_STRING = "searchString";
        public static final String DATA = "data";
        public static final String DATA_1 = "data1";
        public static final String DATA_2 = "data2";
        public final static String SCHEME = "";
        public final static String FULL_SCHEME = SCHEME + "://data/";
    }

    public static class Model {
        public static final String SONGS = "Song";

    }

    public class Parse {
        public static final int CACHE_DAYS_TIME = 2;
        public static final int MAX_LIST_SIZE = 1024;
    }
}
