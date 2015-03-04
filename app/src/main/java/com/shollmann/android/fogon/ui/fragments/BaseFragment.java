package com.shollmann.android.fogon.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.lookeate.java.api.model.APIResponse;
import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.DialogHelper;
import com.shollmann.android.fogon.interfaces.DialogClickListener;
import com.shollmann.android.fogon.interfaces.IError;
import com.shollmann.android.fogon.interfaces.IFragment;
import com.shollmann.android.fogon.interfaces.IFragmentNavigation;
import com.shollmann.android.fogon.interfaces.INetworkBannerDisplayer;
import com.shollmann.android.fogon.interfaces.IServiceActivity;
import com.shollmann.android.fogon.ui.activities.BaseFragmentActivity;
import com.shollmann.android.fogon.ui.activities.ServiceActivity;
import com.shollmann.android.fogon.util.Constants;
import com.shollmann.android.wood.arguments.ServiceArguments;
import com.shollmann.android.wood.helpers.LogInternal;
import com.shollmann.android.wood.network.NetworkUtilities;

public abstract class BaseFragment extends Fragment implements IFragment, DialogClickListener {

    private static final int ERROR_NETWORK_DIALOG = 4455;
    protected boolean showSearchMenu = true;
    private boolean isShowingServerErrorDialog = false;

    private IServiceActivity activity;
    private IFragmentNavigation navigationActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        LogInternal.logUINavigation("Fragment onAttach", getClass().getSimpleName());

        this.activity = (IServiceActivity) activity;
        this.navigationActivity = (IFragmentNavigation) activity;
        navigationActivity.registerFragmentforNotifications(this);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        disablePTR();
        return onCreateCustomView(inflater, container, savedInstanceState);
    }

    public abstract View onCreateCustomView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void triggerAnimation(View view, boolean show) {
        activity.triggerAnimation(view, show);
    }

    public void triggerAnimation(View view, boolean show, int showAnimationResource, int hideAnimationResource) {
        activity.triggerAnimation(view, show, showAnimationResource, hideAnimationResource);
    }

    @Override
    public void setResponse(APIResponse response, String action) {
        setResponse(response, action, true);
    }

    @Override
    public void setResponse(APIResponse response, String action, boolean handleError) {
        removeResponse(response.getRequestId());
        if (getActivity() != null) {
            ((INetworkBannerDisplayer) getActivity()).setShowNetworkIssueBanner(response);
        }

        if (!response.isSuccess() && hasToShowNoDataFragment(response)) {
            // TODO: Change this, as it is directly setting the connection
            // status
            NetworkUtilities.setOnline(false);
            goToFragmentRemovingCurrent(NoDataAvailableFragment.newInstance());

        } else if (hasToShowErrorResponseMessage(response, handleError)) {
            isShowingServerErrorDialog = true;
            DialogHelper.show(getActivity(), null, getString(R.string.error_loading_content), R.string.ok, 0, 0, ERROR_NETWORK_DIALOG);
        }

    }

    private boolean hasToShowErrorResponseMessage(APIResponse response, boolean handleError) {
        return handleError && NetworkUtilities.isBackendError(response) && !isShowingServerErrorDialog;
    }

    private boolean hasToShowNoDataFragment(APIResponse response) {
        return !isAvoidNoConnectionFragment() &&
                (!NetworkUtilities.isConnected() || response.getStatusCode() == Constants.Errors.TIME_OUT ||
                        response.getStatusCode() == Constants.Errors.UNEXPECTED || response.getStatusCode() == APIResponse.UNEXPECTED ||
                        response.getStatusCode() == Constants.Errors.NO_NETWORK);
    }

    private boolean isAvoidNoConnectionFragment() {
        // should return true or false depending on the instance type of the current fragment
        return false;
    }

    public final APIResponse removeResponse(String requestId) {
        if (getActivity() != null) {
            return activity.removeResponse(requestId);
        }
        return null;
    }

    public boolean isMyRequest(APIResponse response, String requestId) {
        return activity.isMyRequest(response, requestId);
    }

    @Override
    public boolean canIGoBack() {
        return true;
    }

    @Override
    public void setActionBar(ActionBar bar) {
        if (bar != null) {
            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            bar.setDisplayShowCustomEnabled(false);
            bar.setTitle(null);
            bar.setSubtitle(null);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.show();
        }
    }

    protected CharSequence getTitle(ActionBar bar) {
        if (bar != null) {
            return bar.getTitle();
        } else if (getActivity() != null) {
            return getActivity().getTitle();
        }
        return null;
    }

    protected void setTitle(ActionBar bar, int titleId) {
        setTitle(bar, getResources().getString(titleId));
    }

    protected void setTitle(ActionBar bar, CharSequence title) {
        if (bar != null) {
            bar.setTitle(title);
        } else if (getActivity() != null) {
            getActivity().setTitle(title);
        }
    }

    protected void setSubtitle(ActionBar bar, int subtitleId) {
        setSubtitle(bar, getResources().getString(subtitleId));
    }

    protected void setSubtitle(ActionBar bar, CharSequence subtitle) {
        if (bar != null) {
            bar.setSubtitle(subtitle);
        }
    }

    @Override
    public void onPositiveClick(int dialogId) {
    }

    @Override
    public void onSelectedItem(int dialogId, int position, String item) {
    }

    @Override
    public void onDismiss(int dialogId) {
    }

    @Override
    public void onNeutralClick(int dialogId) {
    }

    @Override
    public void onNegativeClick(int dialogId) {
    }

    @Override
    public View getCustomDialogView(int dialogId) {
        return null;
    }

    @Override
    public void onCancel(int dialogId) {
    }

    protected ActionBar getSupportActionBar() {
        return navigationActivity.getSupportActionBar();
    }

    protected void lockMenu() {
        activity.lockMenu();
    }

    protected void unlockMenu() {
        activity.unlockMenu();
    }

    protected void removeCurrentFragment(boolean animate) {
        navigationActivity.removeCurrentFragment(animate);
    }

    protected void goHome() {
        navigationActivity.goHome();
    }

    protected void setInitialFragment(IFragment fragment) {
        navigationActivity.setInitialFragment(fragment);
    }

    protected void slideNextFragment(IFragment fragment) {
        navigationActivity.slideNextFragment(fragment);
    }

    protected void slideNextFragmentRemovingCurrent(IFragment fragment, boolean animate) {
        navigationActivity.slideNextFragmentRemovingCurrent(fragment, animate);
    }

    protected void slidePreviousFragment() {
        navigationActivity.slidePreviousFragment();
    }

    protected void slideBottomFragment(IFragment fragment) {
        navigationActivity.slideBottomFragment(fragment);
    }

    public void slideBottomFragmentRemovingCurrent(IFragment fragment, boolean animate) {
        navigationActivity.slideBottomFragmentRemovingCurrent(fragment, animate);
    }

    public void goToFragment(String fragment, boolean animate) {
        navigationActivity.goToFragment(fragment, animate);
    }

    public void goToFragmentRemovingCurrent(IFragment fragment) {
        navigationActivity.goToFragmentRemovingCurrent(fragment);
    }

    protected void slideTopFragment(IFragment fragment) {
        navigationActivity.slideTopFragment(fragment);
    }

    public void slideTopFragmentRemovingCurrent(IFragment fragment, boolean animate) {
        navigationActivity.slideTopFragmentRemovingCurrent(fragment, animate);
    }

    protected void popFragment(IFragment fragment) {
        navigationActivity.popFragment(fragment);
    }

    protected void changeLocation() {
        activity.changeLocation();
    }

    protected void showError(String title, String message, int dialogId) {
        ((IError) getActivity()).showError(title, message, dialogId);
    }

    protected void processError(APIResponse response) {
        activity.processError(response);
    }

    public final String makeNetworkCall(ServiceArguments args, String key) {
        return makeNetworkCall(args, key, true);
    }

    public final String makeNetworkCall(ServiceArguments args, String key, boolean showLoading) {
        return activity.makeNetworkCall(args, key, showLoading);
    }

    public final boolean isRunning(String key) {
        return activity.isRunning(key);
    }

    public final String getRequestId(String key) {
        return activity.getRequestId(key);
    }

    public final void removeRequestId(String key) {
        activity.removeRequestId(key);
    }

    public final void cleanupRequestIds() {
        activity.cleanupRequestIds();
    }

    public final void hideUpdating() {
        activity.hideUpdating();
    }

    public final void showUpdating() {
        activity.showUpdating();
    }

    public final void showUpdating(boolean show) {
        activity.showUpdating(show);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void onViewPagerSelected() {
    }

    @Override
    public void onViewPagerUnSelected() {
    }

    protected AppApplication getApp() {
        return activity.getApp();
    }

    public void hideKeyboard() {
        activity.hideKeyboard();
    }

    @Override
    public void onLocationFailed() {
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public MenuItem getOptionsMenuButton(Menu menu, int itemIdToFind) {
        int i;
        int currentId;

        int menuSize = menu.size();
        for (i = 0; i < menuSize; i++) {
            currentId = menu.getItem(i).getItemId();
            if (currentId == itemIdToFind) {
                return menu.getItem(i);
            }
        }
        return null;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (BaseFragmentActivity.disableFragmentAnimations) {
            Animation animation = new Animation() {
            };
            animation.setDuration(0);
            return animation;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

        if (!isExternalIntent(intent)) {
            getActivity().overridePendingTransition(R.anim.animation_appears_from_right, R.anim.animation_disappears_to_left);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        if (!isExternalIntent(intent)) {
            getActivity().overridePendingTransition(R.anim.animation_appears_from_right, R.anim.animation_disappears_to_left);
        }
    }

    private boolean isExternalIntent(Intent intent) {
        return intent.getAction() == Intent.ACTION_VIEW || intent.getAction() == android.provider.Settings.ACTION_WIFI_SETTINGS ||
                intent.getAction() == Intent.ACTION_SEND || intent.getAction() == Intent.ACTION_DIAL;
    }

    protected void hideDrawerToggle() {
        lockMenu();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationActivity.registerFragmentforNotifications(this);
        activity.firePendingResponsesAsync();
        setActionBar(((ActionBarActivity) getActivity()).getSupportActionBar());

        initialize();
    }

    @Override
    public void onPause() {
        super.onPause();
        navigationActivity.unregisterFragmentforNotifications(this);
    }

    @Override
    public void onRefresh() {
        stopRefresh();
    }

    private void stopRefresh() {
        ((ServiceActivity) getActivity()).swipeLayout.setRefreshing(false);
    }

    public final void disablePTR() {
        if (getActivity() != null) {
            ((ServiceActivity) getActivity()).swipeLayout.setEnabled(false);
        }
    }

    public final void enablePTR() {
        ((ServiceActivity) getActivity()).swipeLayout.setEnabled(true);
    }

}
