package android.lookeate.com.core_lib.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.lookeate.com.core_lib.Constants;
import android.lookeate.com.core_lib.helpers.DateHelper;
import android.lookeate.com.core_lib.helpers.LogInternal;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lookeate.java.api.model.APIResponse;

import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class DBHelper {
    protected String tableName;
    protected String columnKey;
    protected String columnData;
    protected String columnDate;

    private SQLiteDatabase db = null;
    private final Gson gson = new Gson();

    public DBHelper(Context context, String dbName, int dbVersion,
                    String tableName, String columnKey, String columnData, String columnDate) {

        this.tableName = tableName;
        this.columnKey = columnKey;
        this.columnData = columnData;
        this.columnDate = columnDate;

        SQLiteOpenHelper helper = new DatabaseHelper(context, dbName, dbVersion);
        this.db = helper.getWritableDatabase();

        purgeCache();
    }

    public void close() {
        db.close();
        db = null;
    }

    public void insert(String key, APIResponse response, long expirationOffset) {
        ContentValues cv = new ContentValues();
        cv.put(columnKey, key);
        cv.put(columnData, gson.toJson(response));
        cv.put(columnDate, System.currentTimeMillis() + (expirationOffset * 1000));

        int rows = db.update(tableName, cv, columnKey + "=?", new String[]{key});
        if (rows == 0) {
            db.insert(tableName, columnKey, cv);
        }
    }

    public void insert(String key, Serializable data) {

        ContentValues cv = new ContentValues();
        cv.put(columnKey, key);
        cv.put(columnData, gson.toJson(data));
        cv.put(columnDate, System.currentTimeMillis() + (60 * 60 * 24 * 365 * 1000));

        int rows = db.update(tableName, cv, columnKey + "=?", new String[]{key});
        if (rows == 0) {
            db.insert(tableName, columnKey, cv);
        }
    }

    public void delete(String key) {
        db.delete(tableName, columnKey + "=?", new String[]{key});
    }

    public void deleteAll(String key) {
        db.delete(tableName, columnKey + " like?", new String[]{key});
    }

    public void clearCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.delete(tableName, null, null);
            }
        }).start();
    }

    public void invalidate(String cacheKey) {
        ContentValues cv = new ContentValues();
        cv.put(columnDate, 0l);

        db.update(tableName, cv, columnKey + "=?", new String[]{cacheKey});

    }

    public void purgeCache() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = (System.currentTimeMillis() - (1000 * 60 * 60 * 6));
                db.delete(tableName, columnDate + "<? AND " + columnDate + "!=0", new String[]{String.valueOf(time)});
            }
        }).start();
    }

    public APIResponse getData(String key, Type type, long ttl) {
        Cursor c =
                db.query(tableName, new String[]{columnKey, columnData, columnDate},
                        columnKey + "=?", new String[]{key}, null, null, null);
        APIResponse response = null;
        if (c.moveToFirst()) {
            String json = Constants.EMPTY_STRING;
            try {
                json = c.getString(c.getColumnIndex(columnData));
                long date = c.getLong(c.getColumnIndex(columnDate));
                response = gson.fromJson(json, type);
                int seconds = DateHelper.diffSeconds(date);
                response.setCache(true);

                if (seconds > ttl) {
                    invalidate(key);
                    response.setExpired(true);
                }
            } catch (JsonSyntaxException e) {
                Crashlytics.logException(e);
                Crashlytics.log(json);
                LogInternal.error("Unable to parse Json: " + json);
            }
        }
        c.close();
        return response;
    }

    public <T> T loadCache(String key, Type clazz) {
        Cursor c = db.query(tableName, new String[]{columnKey, columnData}, columnKey + "=?", new String[]{key}, null, null, null);
        T response = null;
        if (c.moveToFirst()) {
            String json = c.getString(c.getColumnIndex(columnData));
            response = gson.fromJson(json, clazz);
        }
        c.close();
        return response;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String dbName, int dbVersion) {
            super(context, dbName, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s LONG);",
                    tableName, columnKey, columnData, columnDate));
            db.execSQL(String.format("CREATE INDEX %s_%s ON %s(%s);",
                    tableName, columnDate, tableName, columnDate));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(db);
        }
    }
}