package com.shollmann.android.fogon.interfaces;

import android.view.View;

import com.shollmann.android.fogon.AppApplication;


public interface IServiceActivity {

    void changeLocation();

    void firePendingResponsesAsync();

    void cleanupRequestIds();

    boolean isRunning(String key);

    String getRequestId(String key);

    void removeRequestId(String key);

    void showUpdating();

    void showUpdating(boolean show);

    void hideUpdating();

    void lockMenu();

    void unlockMenu();

    void stopRequestLocation();

    boolean isSearchBarActive();

    void triggerAnimation(View view, boolean show);

    AppApplication getApp();

    void hideKeyboard();

    void triggerAnimation(View view, boolean show, int showAnimationResource, int hideAnimationResource);

}
