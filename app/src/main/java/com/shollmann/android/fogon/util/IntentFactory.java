package com.shollmann.android.fogon.util;

import android.content.Intent;

import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.wood.services.DataService;
import com.shollmann.android.fogon.ui.activities.ServiceActivity;

public class IntentFactory {

    public static Intent getSampleActivity(int param) {
        // Sample of how to get intents to show activities
        // You also can send parameters
        Intent intent = new Intent(AppApplication.getApplication(), ServiceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Constants.ExtraKeys.DATA, param);

        return intent;
    }

    public static Intent getDataServiceIntent() {
        return new Intent(AppApplication.getApplication(), DataService.class);
    }
}
