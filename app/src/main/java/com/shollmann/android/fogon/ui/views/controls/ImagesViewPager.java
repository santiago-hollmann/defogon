package com.shollmann.android.fogon.ui.views.controls;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ImagesViewPager extends ViewPager {

    public ImagesViewPager(Context context) {
        super(context);
    }

    public ImagesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v != this && v instanceof TouchImageView) {
            if (((TouchImageView) v).getSaveScale() > 1) {
                // REMIND Prevents the ViewPager to scroll if the zoom image > 1
                return true;
            }
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

}
