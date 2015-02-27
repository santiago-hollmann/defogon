package com.shollmann.android.fogon.interfaces;

import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.wood.arguments.ServiceArguments;
import com.lookeate.java.api.model.APIResponse;


public interface IServiceActivity {

    void changeLocation();

    APIResponse removeResponse(String requestId);

    void firePendingResponsesAsync();

    void cleanupRequestIds();

    String makeNetworkCall(ServiceArguments args, String key);

    String makeNetworkCall(ServiceArguments args, String key, boolean showLoading);

    boolean isRunning(String key);

    String getRequestId(String key);

    void removeRequestId(String key);

    void showUpdating();

    void showUpdating(boolean show);

    void hideUpdating();

    void lockMenu();

    void unlockMenu();

    boolean isMyRequest(APIResponse response, String requestId);

    void processError(APIResponse response);

    void requestLocation(LocationListener listener);

    void stopRequestLocation();

    boolean isLocationServiceEnabled();

    boolean isSearchBarActive();

    void setSearchBarStatus(boolean status);

    void triggerAnimation(View view, boolean show);

    AppApplication getApp();

    void hideKeyboard();

    void triggerAnimation(View view, boolean show, int showAnimationResource, int hideAnimationResource);

}
