package com.lookeate.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class LocationReceiver extends BroadcastReceiver {

    private static List<ILocationListener> mListeners = new ArrayList<ILocationListener>();

    @Override
    public void onReceive(Context context, Intent intent) {
        notifyListeners();
    }

    public static void registerListener(ILocationListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public static void unRegisterListener(ILocationListener listener) {
        mListeners.remove(listener);
    }

    public static void notifyListeners() {
        try {
            for (ILocationListener listener : mListeners) {
                listener.onLocationServicesChanged();
            }
        } catch (ConcurrentModificationException ex) {
            ex.printStackTrace();
        }
    }

    public interface ILocationListener {
        void onLocationServicesChanged();
    }

}
