package com.shollmann.android.fogon.interfaces;

import android.view.View;

import com.shollmann.android.fogon.DeFogonApplication;


public interface IServiceActivity {

    void showUpdating();

    void showUpdating(boolean show);

    void hideUpdating();

    void lockMenu();

    void unlockMenu();

    void triggerAnimation(View view, boolean show);

    DeFogonApplication getApp();

    void hideKeyboard();

    void triggerAnimation(View view, boolean show, int showAnimationResource, int hideAnimationResource);

}
