package com.shollmann.android.wood.db;

import android.content.Context;

public class CachingDbHelper extends DBHelper {
    private final static int DB_VERSION = 4;
    private final static String DB_NAME = "database";
    private final static String TABLE_NAME = "cache";
    private final static String COLUMN_KEY = "cacheKey";
    private final static String COLUMN_DATA = "cacheData";
    private final static String COLUMN_DATE = "cacheDate";

    public CachingDbHelper(Context context) {
        super(context, DB_NAME, DB_VERSION, TABLE_NAME, COLUMN_KEY, COLUMN_DATA, COLUMN_DATE);
    }
}