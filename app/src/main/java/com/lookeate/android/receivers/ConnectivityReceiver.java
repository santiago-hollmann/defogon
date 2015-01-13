package com.lookeate.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lookeate.android.core_lib.network.NetworkUtilities;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkUtilities.setNetworkStatus();
    }
}
