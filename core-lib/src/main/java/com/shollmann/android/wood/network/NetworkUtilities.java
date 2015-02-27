package com.shollmann.android.wood.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.text.Html;

import com.crashlytics.android.Crashlytics;
import com.shollmann.android.wood.Constants;
import com.shollmann.android.wood.CoreLibApplication;
import com.lookeate.java.api.model.APIResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class NetworkUtilities {
    private static final String NO_NETWORK = "no_network";
    private static final String encoding = "UTF-8";
    private static final String GPRS = "GPRS";
    private static final String EDGE = "EDGE";
    private static final String _3G = "3G";
    private static final String _4G = "4G";
    private static final String WIFI = "Wifi";
    private static final String UNDEFINED_NETWORK = "undefined";

    private static List<IConnectivityListener> mListeners = new ArrayList<IConnectivityListener>();
    private static ConnectivityManager manager;
    private static TelephonyManager telephonyManager;
    private static boolean isOnline;
    private static String currentConnection = UNDEFINED_NETWORK;
    private static String previousConnection = UNDEFINED_NETWORK;

    static {
        manager = (ConnectivityManager) CoreLibApplication.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        telephonyManager = (TelephonyManager) CoreLibApplication.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    private NetworkUtilities() {

    }

    public static String getNetworkType() {
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().getTypeName();
        }
        return Constants.EMPTY_STRING;
    }

    public static String getReadableNetworkType() {
        if (isMobile()) {
            return getReadableMobileNetworkType();
        } else {
            if (isAnyWifi()) {
                return WIFI;
            } else {
                return NO_NETWORK;
            }
        }
    }

    public static boolean isConnected() {
        try {
            if (manager.getActiveNetworkInfo() != null) {
                return manager.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        } catch (Exception e) {
            Crashlytics.log("manager: " + manager);
            Crashlytics.logException(e);
            return false;
        }
        return false;
    }

    public static boolean isOnline() {
        if (isConnected()) {
            return isOnline;
        }
        return false;
    }

    public static boolean isAnyWifi() {
        try {
            if (manager != null) {
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo wimaxInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
                boolean isWifiAvailable = wifiInfo != null && wifiInfo.isConnected();
                boolean isWimaxAvailable = wimaxInfo != null && wimaxInfo.isConnected();
                return (isWifiAvailable || isWimaxAvailable);
            }
        } catch (Exception e) {
            Crashlytics.log("manager: " + manager);
            Crashlytics.logException(e);
            return false;
        }
        return false;
    }

    public static boolean isMobile() {
        try {
            if (manager.getActiveNetworkInfo() != null) {
                Integer networkType = manager.getActiveNetworkInfo().getType();
                if (networkType != null) {
                    return networkType == ConnectivityManager.TYPE_MOBILE || networkType == ConnectivityManager.TYPE_MOBILE_DUN ||
                            networkType == ConnectivityManager.TYPE_MOBILE_HIPRI || networkType == ConnectivityManager.TYPE_MOBILE_MMS ||
                            networkType == ConnectivityManager.TYPE_MOBILE_SUPL;
                }
            }
        } catch (Exception e) {
            Crashlytics.log("manager: " + manager);
            Crashlytics.logException(e);

            return false;
        }

        return false;
    }

    public static boolean isWifiMax() {
        if (manager.getActiveNetworkInfo() != null) {
            return manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIMAX;
        }
        return false;
    }

    public static void setNetworkStatus() {
        if (isConnected()) {
            PingToDetectInternetAccessAsyncTask task = new PingToDetectInternetAccessAsyncTask();
            task.execute();
        } else {
            isOnline = false;
            notifyListeners(isConnected(), isOnline);
        }
    }

    public static String convertStreamToString(InputStream is) {
        InputStreamReader isr;
        try {
            isr = new InputStreamReader(is, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            isr = new InputStreamReader(is);
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(isr);
        {
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    public static String encode(String text) {
        try {
            return URLEncoder.encode(text, encoding);
        } catch (UnsupportedEncodingException e) {
            return text;
        }
    }

    public static String HTMLDecode(String sHTML) {
        return Html.fromHtml(sHTML).toString();
    }

    public static void registerListener(IConnectivityListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public static void unRegisterListener(IConnectivityListener listener) {
        mListeners.remove(listener);
    }

    public static void notifyListeners(boolean isConnected, boolean isOnline) {
        try {
            previousConnection = currentConnection;
            currentConnection = getReadableNetworkType();

            for (IConnectivityListener listener : mListeners) {
                listener.onConnectivityChanged(isConnected, isOnline);
            }
        } catch (ConcurrentModificationException ex) {
            ex.printStackTrace();
        }
    }

    public interface IConnectivityListener {
        void onConnectivityChanged(boolean isConnected, boolean isOnline);
    }

    public static boolean isBackendError(APIResponse response) {
        if (!isOnline) {
            return false;
        }

        if (response.isSuccess()) {
            return false;
        }

        if ((isA500Error(response) || isA400Error(response)) && (response.getStatusCode() != HttpURLConnection.HTTP_UNAUTHORIZED) &&
                (response.getStatusCode() != Constants.Errors.NO_NETWORK) && (response.getStatusCode() != APIResponse.UNEXPECTED) &&
                (response.getStatusCode() != Constants.Errors.UNEXPECTED) && (response.getStatusCode() != Constants.Errors.TIME_OUT)) {
            return true;
        }

        return false;
    }

    private static boolean isA500Error(APIResponse response) {
        return response.getStatusCode() % 500 <= 99;
    }

    private static boolean isA400Error(APIResponse response) {
        return response.getStatusCode() % 400 <= 99;
    }

    public static void setOnline(boolean isOnline) {
        NetworkUtilities.isOnline = isOnline;
    }

    public static NetworkErrorTypes getNetworkError() {
        if (isConnected()) {
            if (isOnline) {
                return NetworkErrorTypes.NO_ERROR;
            } else {
                if (isAnyWifi()) {
                    return NetworkErrorTypes.NO_INTERNET_ACCESS;
                } else {
                    return NetworkErrorTypes.UNSTABLE_NETWORK;
                }
            }
        } else {
            return NetworkErrorTypes.NO_CONNECTION;
        }
    }

    private static String getReadableMobileNetworkType() {
        if (telephonyManager != null) {
            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return _4G;

                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return _3G;

                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return GPRS;

                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return EDGE;

                default:
                    return UNDEFINED_NETWORK;
            }
        }
        return Constants.EMPTY_STRING;

    }

    public static boolean canDeviceHandleCalls() {
        if (telephonyManager != null && telephonyManager.getLine1Number() != null) {
            return true;
        }

        return false;
    }

    public static String getCurrentConnection() {
        return currentConnection;
    }

    public static void setCurrentConnection(String currentConnection) {
        NetworkUtilities.currentConnection = currentConnection;
    }

    public static String getPreviousConnection() {
        return previousConnection;
    }

    public static void setPrevioousConnection(String previoousConnection) {
        NetworkUtilities.previousConnection = previoousConnection;
    }

    private static class PingToDetectInternetAccessAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private static final int SLEEP_TIME = 2000;

        @Override
        protected Boolean doInBackground(Void... params) {
            int statusCode = pingGoogle();

            if (statusCode != HttpURLConnection.HTTP_NO_CONTENT && isMobile()) {

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                statusCode = pingGoogle();
            }

            return statusCode == HttpURLConnection.HTTP_NO_CONTENT;
        }

        private int pingGoogle() {
            int statusCode = 0;
            try {
                /*
                 * Android use this URL
				 * (http://clients3.google.com/generate_204) to detect real
				 * Internet access. If you receive something different than a
				 * 204 (NO CONTENT), it means that the response is not the
				 * original by Google.
				 */
                URL url = new URL("http://clients3.google.com/generate_204");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                statusCode = urlConnection.getResponseCode();
            } catch (Exception e) {
                return -1;
            }
            return statusCode;
        }

        @Override
        protected void onPostExecute(Boolean isOnline) {
            NetworkUtilities.isOnline = isOnline;
            notifyListeners(isConnected(), isOnline);
        }

    }

    public enum NetworkErrorTypes {
        NO_INTERNET_ACCESS, UNSTABLE_NETWORK, NO_CONNECTION, NO_ERROR;
    }

}
