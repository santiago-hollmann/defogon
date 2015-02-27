package com.shollmann.android.wood.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;
import com.shollmann.android.wood.Constants;
import com.shollmann.android.wood.CoreLibApplication;
import com.shollmann.android.wood.arguments.ServiceArguments;
import com.shollmann.android.wood.db.CachingDbHelper;
import com.shollmann.android.wood.helpers.LogInternal;
import com.shollmann.android.wood.network.NetworkUtilities;
import com.lookeate.java.api.contract.LookeateAPI;
import com.lookeate.java.api.model.APIResponse;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataService extends Service {
    private boolean outOfService = false;
    private final IBinder mBinder = new DataBinder();
    private ExecutorService executor;
    private HashMap<String, APIResponse> responses;
    private int connections = 0;
    private CachingDbHelper cachingDb;
    private LookeateAPI api;
    private static final long BASE_WAIT_TIME = 2000;
    private String action;

    public int getConnections() {
        return connections;
    }

    public static void sendBroadCast(Context context, String Action, Serializable data) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Action);
        broadcastIntent
                .setData(Uri.withAppendedPath(Uri.parse(Constants.ExtraKeys.FULL_SCHEME), String.valueOf(System.currentTimeMillis())));
        broadcastIntent.putExtra(Constants.ExtraKeys.DATA, data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogInternal.logServiceBinding("DataService is initializing", Constants.EMPTY_STRING);
        executor = Executors.newCachedThreadPool();
        responses = new HashMap<String, APIResponse>();
        cachingDb = CoreLibApplication.getInstance().getCachingDbHelper();
        return START_NOT_STICKY;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        outOfService = true;
        LogInternal.logServiceBinding("DataService is out of service", "from an OS call");
        executor.shutdown();
    }

    public boolean isOutOfService() {
        return outOfService;
    }

    public void makeServiceCall(ServiceArguments args) {
        executor.execute(new NetworkTask(args));
    }

    public void saveCache(Serializable data, String key) {
        try {
            cachingDb.insert(key, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteCache(String key) {
        try {
            cachingDb.delete(key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T> T loadCache(String key, Type clazz) {
        try {
            return cachingDb.loadCache(key, clazz);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public APIResponse removeResponse(String requestId) {
        return responses.remove(requestId);
    }

    public void firePendingResponses() {
        try {
            for (APIResponse response : responses.values()) {
                sendBroadCast(getApplicationContext(), Constants.Actions.MESSAGE, response);
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DataBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    }

    public class NetworkTask implements Runnable {
        private final ServiceArguments args;

        public NetworkTask(ServiceArguments args) {
            this.args = args;
        }

        @Override
        public void run() {
            APIResponse dataResponse = getSyncApiResponse(args);

            // Save the response
            responses.put(dataResponse.getRequestId(), dataResponse);

            // Broadcast the response
            sendBroadCast(getApplicationContext(), action, dataResponse);
        }

    }

    private APIResponse getData(APIResponse dataResponse, ServiceArguments args) {
        if (args.isAllowShowCache()) {
            dataResponse = handleAllowToShowCache(args);
        } else {
            dataResponse = handleNotAllowToShowCache(dataResponse, args);
        }
        dataResponse.setRequestId(args.getRequestId());
        LogInternal.logServiceCall("answering", dataResponse.getLogMessage());
        return dataResponse;
    }

    private APIResponse handleAllowToShowCache(ServiceArguments args) {
        APIResponse dataResponse;
        dataResponse = cachingDb.getData(args.getCacheKey(), args.getType(), args.getTTL());
        if (dataResponse != null) {
            LogInternal.logServiceCall("Allow Cache As Response, retrieving from DB", args.getLogMessage());
            dataResponse = args.preProcess(dataResponse);
        } else if (args.isOfflineOnly()) {
            LogInternal.logServiceCall("Allow Cache As Response. retrieving from NOWHERE", args.getLogMessage());
            dataResponse = new APIResponse();
        } else {
            LogInternal.logServiceCall("Allow Cache As Response. retrieving from NET", args.getLogMessage());
            dataResponse = getDataFromNetwork(args);
        }
        return dataResponse;
    }

    private APIResponse getDataFromNetwork(ServiceArguments args) {
        APIResponse dataResponse = new APIResponse();
        dataResponse.setStatusCode(Constants.Errors.NO_NETWORK);

        while (canRetryRequest(dataResponse, args)) {
            args.incrementRetries();

            if (NetworkUtilities.isConnected()) {
                connections++;
                broadcastConnectionAmount();
                dataResponse = getAPIResponse(args);
                connections--;
                broadcastConnectionAmount();
            } else {
                action = Constants.Actions.NETWORK;
                dataResponse = new APIResponse();
                dataResponse.setStatusCode(Constants.Errors.NO_NETWORK);
            }

            if (shouldWaitForRetry(dataResponse, args)) {
                try {
                    Thread.sleep(BASE_WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        return dataResponse;
    }

    /*
        This method returns an API Response synchronously.
        Only call it when you are not inside the UI thread.
     */
    public APIResponse getSyncApiResponse(ServiceArguments args) {
        APIResponse dataResponse = new APIResponse();
        action = Constants.Actions.MESSAGE;

        dataResponse = getData(dataResponse, args);

        args.resetRetries();

        if ((dataResponse.getStatusCode() == Constants.Errors.NO_NETWORK || dataResponse.getStatusCode() == Constants.Errors.TIME_OUT ||
                dataResponse.getStatusCode() == Constants.Errors.UNEXPECTED) && !dataResponse.isCache()) {
            // TODO: Change this, as it is directly setting the connection
            // status
            NetworkUtilities.setOnline(false);
        }

        dataResponse.setUpdateDate(System.currentTimeMillis());
        dataResponse.setRequestId(args.getRequestId());
        dataResponse.setAction(action);

        dataResponse = args.postProcess(dataResponse);

        if (saveCache(dataResponse, args)) {
            cachingDb.insert(args.getCacheKey(), dataResponse, args.getTTL());
        }

        return dataResponse;
    }

    private boolean shouldWaitForRetry(APIResponse dataResponse, ServiceArguments args) {
        // Validations Added to log data details to resolve bug ANDROID-161, remove after the issue is resolved
        boolean shoulWaitForRetry = false;
        try {
            shoulWaitForRetry = !dataResponse.isSuccess() && !NetworkUtilities.isBackendError(dataResponse);
        } catch (NullPointerException e) {
            try {
                Crashlytics.log(args.getLogMessage());
                Crashlytics.logException(e);
            } catch (Exception ex) {
            }
        }
        return shoulWaitForRetry;
    }

    private boolean canRetryRequest(APIResponse dataResponse, ServiceArguments args) {
        return NetworkUtilities.isConnected() && args.canRetry() && !dataResponse.isSuccess() &&
                !NetworkUtilities.isBackendError(dataResponse);
    }

    private APIResponse handleNotAllowToShowCache(APIResponse dataResponse, ServiceArguments args) {
        if (args.useCache()) {
            dataResponse = cachingDb.getData(args.getCacheKey(), args.getType(), args.getTTL());
            if (dataResponse != null) {
                LogInternal.logServiceCall("Retrieving from DB", args.getLogMessage());
                dataResponse = args.preProcess(dataResponse);
            } else {
                LogInternal.logServiceCall("Retrieving from NOWHERE", args.getLogMessage());
                dataResponse = new APIResponse();
            }
        }
        if (!dataResponse.isCache() || dataResponse.isExpired()) {
            LogInternal.logServiceCall("Retrieving from NET", args.getLogMessage());
            dataResponse = getDataFromNetwork(args);
        }
        return dataResponse;
    }

    private boolean saveCache(APIResponse dataResponse, ServiceArguments args) {
        return (!dataResponse.isCache() && args.useCache() && dataResponse.isSuccess());
    }

    private void broadcastConnectionAmount() {
        sendBroadCast(getApplicationContext(), Constants.Actions.CONNECTIONS, new APIResponse());
    }

    private APIResponse getAPIResponse(ServiceArguments args) {
        APIResponse apiResponse = new APIResponse();

        api = CoreLibApplication.getInstance().getApi();

        try {
            // Generic argument. Remove before start coding
            if (args instanceof ServiceArguments) {
                // handle call with a method that gives back the server response
                apiResponse = null;
            } else if (true) {
                // Another call to the server
            }
        } catch (Exception ex) {
            apiResponse = setErrorResponse();
            ex.printStackTrace();
        }

        return apiResponse == null ? setErrorResponse() : apiResponse;
    }

    private APIResponse setErrorResponse() {
        APIResponse response = new APIResponse();
        response.setStatusCode(Constants.Errors.SOMETHING_WENT_WRONG);
        return response;
    }

}
