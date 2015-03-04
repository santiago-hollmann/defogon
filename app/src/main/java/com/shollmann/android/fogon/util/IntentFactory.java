package com.shollmann.android.fogon.util;

import android.content.Intent;

import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.fogon.ui.activities.FavoriteSongsActivity;
import com.shollmann.android.fogon.ui.activities.HomeActivity;
import com.shollmann.android.wood.services.DataService;

public class IntentFactory {
    public static Intent getDataServiceIntent() {
        return new Intent(AppApplication.getApplication(), DataService.class);
    }

    public static Intent getHomeActivity() {
        Intent intent = new Intent(AppApplication.getApplication(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    public static Intent getFavoriteSongsActivity() {
        Intent intent = new Intent(AppApplication.getApplication(), FavoriteSongsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }
}
