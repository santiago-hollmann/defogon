package com.shollmann.android.fogon.interfaces;

import android.support.v7.app.ActionBar;

public interface IFragmentNavigation {

    void setNextFragment(IFragment fragment);

    void slideNextFragment(IFragment fragment);

    void slideNextFragmentRemovingCurrent(IFragment fragment, boolean animate);

    void slidePreviousFragment();

    void slideBottomFragment(IFragment fragment);

    void slideBottomFragmentRemovingCurrent(IFragment fragment, boolean animate);

    void slideTopFragment(IFragment fragment);

    void slideTopFragmentRemovingCurrent(IFragment fragment, boolean animate);

    void popFragment(IFragment fragment);

    void setInitialFragment(IFragment fragment);

    void goToFragment(String name, boolean animate);

    void removeCurrentFragment(boolean animate);

    void goHome();

    void showUpdating(boolean show);

    void goToFragmentRemovingCurrent(IFragment fragment);

    void registerFragmentforNotifications(IFragment baseFragment);

    ActionBar getSupportActionBar();

    void unregisterFragmentforNotifications(IFragment baseFragment);

    interface Directions {
        int NONE = 0;
        int RIGHT = 1;
        int BOTTOM = 2;
        int LEFT = 3;
        int TOP = 4;
        int POP = 5;
    }

}
