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

    public static interface Directions {
        static final int NONE = 0;
        static final int RIGHT = 1;
        static final int BOTTOM = 2;
        static final int LEFT = 3;
        static final int TOP = 4;
        static final int POP = 5;
    }

    void registerFragmentforNotifications(IFragment baseFragment);

    ActionBar getSupportActionBar();

    void unregisterFragmentforNotifications(IFragment baseFragment);

}
