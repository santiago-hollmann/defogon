package com.lookeate.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.olx.grog.network.NetworkUtilities;
import com.olx.grog.network.NetworkUtilities.IConnectivityListener;
import com.olx.olx.Constants;
import com.olx.olx.R;
import com.olx.olx.helpers.BundleHelper;
import com.olx.olx.helpers.TrackerHelper;
import com.olx.olx.interfaces.IFragment;
import com.olx.olx.interfaces.IFragmentNavigation;
import com.olx.olx.interfaces.INetworkBannerDisplayer;
import com.olx.olx.interfaces.IOnReload;
import com.olx.olx.model.ResolvedLocation;
import com.olx.olx.ui.views.NetworkErrorBannerView;
import com.olx.smaug.api.model.APIResponse;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseFragmentActivity extends BaseActivity
        implements IFragmentNavigation, IConnectivityListener, OnClickListener, INetworkBannerDisplayer, IOnReload {

    private static final String BANNER_STATE = "bannerState";
    private final Set<IFragment> currentFragments = new HashSet<IFragment>();
    private NetworkErrorBannerView networkErrorBanner;

    public boolean showNetworkErrors = false;
    public static boolean disableFragmentAnimations = false;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        networkErrorBanner = (NetworkErrorBannerView) findViewById(R.id.main_networkerror_banner);
        networkErrorBanner.setOnClickListener(this);

        if (BundleHelper.bundleContains(savedInstance, BANNER_STATE)) {
            setUIForConnectionIssues();
        }

    }

    @Override
    public void registerFragmentforNotifications(IFragment fragment) {
        currentFragments.add(fragment);
    }

    @Override
    public void unregisterFragmentforNotifications(IFragment fragment) {
        currentFragments.remove(fragment);
    }

    @Override
    public void onResultReceived(Intent intent) {
        APIResponse response = (APIResponse) intent.getSerializableExtra(Constants.ExtraKeys.DATA);
        String action = intent.getAction();
        try {
            for (IFragment fragment : currentFragments) {
                fragment.setResponse(response, action);
            }
        } catch (ConcurrentModificationException ex) {
        }
    }

    @Override
    public void onResultReceivedNoError(Intent intent) {
        APIResponse response = (APIResponse) intent.getSerializableExtra(Constants.ExtraKeys.DATA);
        String action = intent.getAction();
        try {
            for (IFragment fragment : currentFragments) {
                fragment.setResponse(response, action, false);
            }
        } catch (ConcurrentModificationException ex) {
        }
    }

    private void setFragment(IFragment fragment, int direction) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            setFragmentAnimation(direction, transaction);

            if (direction == IFragmentNavigation.Directions.POP) {
                transaction.add(R.id.activity_fragment_layout, (Fragment) fragment, fragment.getClass().getName());
            } else {
                transaction.replace(R.id.activity_fragment_layout, (Fragment) fragment, fragment.getClass().getName());
            }

            transaction.addToBackStack(fragment.getClass().getName());
            transaction.commitAllowingStateLoss();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setFragmentAnimation(int direction, FragmentTransaction transaction) {
        switch (direction) {
            case IFragmentNavigation.Directions.RIGHT:
                transaction.setCustomAnimations(R.anim.animation_appears_from_right, R.anim.animation_disappears_to_left,
                        R.anim.animation_appears_from_left, R.anim.animation_disappears_to_right);
                break;
            case IFragmentNavigation.Directions.BOTTOM:
                transaction.setCustomAnimations(R.anim.animation_appears_from_bottom, R.anim.animation_disappears_to_top,
                        R.anim.animation_appears_from_top, R.anim.animation_disappears_to_bottom);
                break;
            case IFragmentNavigation.Directions.LEFT:
                transaction.setCustomAnimations(R.anim.animation_appears_from_left, R.anim.animation_disappear, 0, 0);
                break;
            case IFragmentNavigation.Directions.TOP:
                transaction.setCustomAnimations(R.anim.animation_appears_from_top, R.anim.animation_disappears_to_bottom,
                        R.anim.animation_appears_from_bottom, R.anim.animation_disappears_to_top);
                break;
            case IFragmentNavigation.Directions.POP:
                transaction.setCustomAnimations(R.anim.grow, 0, 0, R.anim.shrink);
                break;
            default:
                break;
        }
    }

    @Override
    public void setInitialFragment(IFragment fragment) {
        removeAllFragments();
        setFragment(fragment, IFragmentNavigation.Directions.NONE);
    }

    @Override
    public void goToFragment(String name, boolean animate) {
        disableFragmentAnimations = !animate;
        getSupportFragmentManager().popBackStackImmediate(name, 0);
        disableFragmentAnimations = false;
    }

    @Override
    public void goToFragmentRemovingCurrent(IFragment fragment) {
        removeCurrentFragment(false);
        setFragment(fragment, IFragmentNavigation.Directions.NONE);
    }

    @Override
    public void slideNextFragment(IFragment fragment) {
        setFragment(fragment, IFragmentNavigation.Directions.RIGHT);
    }

    @Override
    public void setNextFragment(IFragment fragment) {
        setFragment(fragment, IFragmentNavigation.Directions.NONE);
    }

    @Override
    public void removeCurrentFragment(boolean animate) {
        disableFragmentAnimations = !animate;
        getSupportFragmentManager().popBackStack();
        disableFragmentAnimations = false;
    }

    @Override
    public void slideNextFragmentRemovingCurrent(IFragment fragment, boolean animate) {
        removeCurrentFragment(animate);
        setFragment(fragment, IFragmentNavigation.Directions.RIGHT);
    }

    @Override
    public void slideBottomFragment(IFragment fragment) {
        setFragment(fragment, IFragmentNavigation.Directions.BOTTOM);
    }

    @Override
    public void slideBottomFragmentRemovingCurrent(IFragment fragment, boolean animate) {
        removeCurrentFragment(animate);
        setFragment(fragment, IFragmentNavigation.Directions.BOTTOM);
    }

    @Override
    public void slideTopFragment(IFragment fragment) {
        setFragment(fragment, IFragmentNavigation.Directions.TOP);
    }

    @Override
    public void slideTopFragmentRemovingCurrent(IFragment fragment, boolean animate) {
        removeCurrentFragment(animate);
        setFragment(fragment, IFragmentNavigation.Directions.TOP);
    }

    @Override
    public void popFragment(IFragment fragment) {
        setFragment(fragment, IFragmentNavigation.Directions.POP);
    }

    @Override
    public void goHome() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            disableFragmentAnimations = true;
            getSupportFragmentManager().popBackStackImmediate(getSupportFragmentManager().getBackStackEntryAt(0).getId(), 0);
            disableFragmentAnimations = false;
        }
    }

    private void removeAllFragments() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            disableFragmentAnimations = true;
            getSupportFragmentManager().popBackStackImmediate(getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            disableFragmentAnimations = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkUtilities.registerListener(this);
        // Careful here, we're using isOnline which can be set by hand
        checkForNetworkErrors(NetworkUtilities.isOnline());
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetworkUtilities.unRegisterListener(this);
    }

    @Override
    public void slidePreviousFragment() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onPositiveClick(int dialogId) {
        super.onPositiveClick(dialogId);
        for (IFragment fragment : currentFragments) {
            fragment.onPositiveClick(dialogId);
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
        super.onNegativeClick(dialogId);
        for (IFragment fragment : currentFragments) {
            fragment.onNegativeClick(dialogId);
        }
    }

    @Override
    public void onNeutralClick(int dialogId) {
        super.onNeutralClick(dialogId);
        for (IFragment fragment : currentFragments) {
            fragment.onNeutralClick(dialogId);
        }
    }

    @Override
    public void onSelectedItem(int dialogId, int position, String item) {
        super.onSelectedItem(dialogId, position, item);
        for (IFragment fragment : currentFragments) {
            fragment.onSelectedItem(dialogId, position, item);
        }
    }

    @Override
    public void onCancel(int dialogId) {
        super.onCancel(dialogId);
        for (IFragment fragment : currentFragments) {
            fragment.onCancel(dialogId);
        }
    }

    @Override
    public void onDismiss(int dialogId) {
        super.onDismiss(dialogId);
        for (IFragment fragment : currentFragments) {
            fragment.onDismiss(dialogId);
        }
    }

    @Override
    public View getCustomDialogView(int dialogId) {
        View view = null;
        for (IFragment fragment : currentFragments) {
            view = fragment.getCustomDialogView(dialogId);
            if (view != null) {
                break;
            }
        }
        return view;
    }

    @Override
    public void onLocationChanged(ResolvedLocation location) {
        checkForForceUpdate(true);
        super.onLocationChanged(location);
        for (IFragment fragment : currentFragments) {
            fragment.onLocationChanged(location);
        }
    }

    @Override
    public void onLocationFailed() {
        super.onLocationFailed();
        for (IFragment fragment : currentFragments) {
            fragment.onLocationFailed();
        }
    }

    @Override
    public void onBackPressed() {
        if (canIGoBack()) {
            final FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        }
    }

    public boolean canIGoBack() {
        boolean canI = true;
        try {
            for (IFragment fragment : currentFragments) {
                canI &= fragment.canIGoBack();
            }
        } catch (ConcurrentModificationException ex) {

        }
        return canI;
    }

    @Override
    public void onConnectivityChanged(boolean isConnected, boolean isOnline) {
        TrackerHelper.connectionChange();

        showNetworkErrors = true;
        // Careful here, we're using isOnline which can be set by hand
        checkForNetworkErrors(isOnline);
    }

    private void checkForNetworkErrors(boolean isOnline) {
        if (showNetworkErrors) {
            if (isOnline) {
                setUIForNoConnectionIssues();
            } else {
                setUIForConnectionIssues();
            }
        }
    }

    private void setUIForNoConnectionIssues() {
        networkErrorBanner.setVisibility(View.GONE);
        reloadData();
    }

    private void setUIForConnectionIssues() {
        networkErrorBanner.setVisibility(View.VISIBLE);
        if (!NetworkUtilities.isConnected()) {
            networkErrorBanner.showNoConnectionBanner();
        } else {
            if (NetworkUtilities.isAnyWifi()) {
                networkErrorBanner.showNoInternetAccessBanner();
            } else {
                networkErrorBanner.showUnstableNetworkBanner();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (NetworkUtilities.getNetworkError()) {
            case NO_INTERNET_ACCESS:
                reloadData();
                networkErrorBanner.setVisibility(View.GONE);
                break;
            case NO_CONNECTION:
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                break;

            case UNSTABLE_NETWORK:
                reloadData();
                networkErrorBanner.setVisibility(View.GONE);
                break;
            case NO_ERROR:
                reloadData();
                networkErrorBanner.setVisibility(View.GONE);
                break;
            default:
                reloadData();
                networkErrorBanner.setVisibility(View.GONE);
                break;
        }
    }

    private void reloadData() {
        try {
            for (IFragment fragment : currentFragments) {
                fragment.onReload();
            }
        } catch (ConcurrentModificationException ex) {

        }
    }

    public void setShowNetworkErrors(boolean show) {
        showNetworkErrors = show;
    }

    @Override
    public void setShowNetworkIssueBanner(APIResponse response) {
        if (response.isSuccess()) {
            networkErrorBanner.setVisibility(View.GONE);
            if (!response.isCache()) {
                NetworkUtilities.setOnline(true);
            }
        } else {
            if (!NetworkUtilities.isBackendError(response)) {
                setUIForConnectionIssues();
            }
        }
    }

    @Override
    public void onReload() {
    }

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (networkErrorBanner.getVisibility() != View.GONE) {
            outState.putInt(BANNER_STATE, networkErrorBanner.getVisibility());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return toggleDrawer();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onRefresh() {
        for (IFragment fragment : currentFragments) {
            fragment.onRefresh();
        }
    }
}
