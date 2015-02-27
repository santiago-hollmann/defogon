package com.shollmann.android.fogon.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.shollmann.android.wood.network.NetworkUtilities;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtilities.setNetworkStatus();
    }
}
