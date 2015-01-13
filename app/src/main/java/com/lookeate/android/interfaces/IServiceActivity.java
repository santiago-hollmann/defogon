package com.lookeate.android.interfaces;

import android.view.Menu;
import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.lookeate.android.AppApplication;
import com.lookeate.android.core_lib.arguments.ServiceArguments;
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

    boolean isShowingDrawer();

    boolean isMyRequest(APIResponse response, String requestId);

    void processError(APIResponse response);

    void requestLocation(LocationListener listener);

    void stopRequestLocation();

    boolean isLocationServiceEnabled();

    void toggleSwitch(Menu menu, boolean show);

    void openDrawerShowCase();

    boolean isSearchBarActive();

    void setSearchBarStatus(boolean status);

    void triggerAnimation(View view, boolean show);

    AppApplication getApp();

    void hideKeyboard();

    void triggerAnimation(View view, boolean show, int showAnimationResource, int hideAnimationResource);

}
