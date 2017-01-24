package com.shollmann.android.fogon.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.interfaces.IFragment;
import com.shollmann.android.fogon.interfaces.IFragmentNavigation;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseFragmentActivity extends BaseActivity implements IFragmentNavigation, OnClickListener {

    public static boolean disableFragmentAnimations = false;
    private final Set<IFragment> currentFragments = new HashSet<>();

    @Override
    public void registerFragmentforNotifications(IFragment fragment) {
        currentFragments.add(fragment);
    }

    @Override
    public void unregisterFragmentforNotifications(IFragment fragment) {
        currentFragments.remove(fragment);
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
    public void slidePreviousFragment() {
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onPositiveClick(int dialogId) {
        for (IFragment fragment : currentFragments) {
            fragment.onPositiveClick(dialogId);
        }
    }

    @Override
    public void onNegativeClick(int dialogId) {
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
        for (IFragment fragment : currentFragments) {
            fragment.onCancel(dialogId);
        }
    }

    @Override
    public void onDismiss(int dialogId) {
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
    public void onBackPressed() {
        if (this.isNavigationDrawerOpen()) {
            this.closeNavigationDrawer();
            return;
        }
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

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // Do something if menu key is pressed down
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onRefresh() {
        for (IFragment fragment : currentFragments) {
            fragment.onRefresh();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
