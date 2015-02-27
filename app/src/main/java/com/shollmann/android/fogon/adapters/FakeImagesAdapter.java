package com.shollmann.android.fogon.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class FakeImagesAdapter extends PagerAdapter {

    private final String[] images;
    private final Context context;

    public FakeImagesAdapter(Context context, String[] images) {
        super();
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (images == null) {
            return 0;
        } else {
            return images.length;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FrameLayout itemView = new FrameLayout(context);
        ((ViewPager) container).addView(itemView);
        return itemView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((FrameLayout) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((FrameLayout) object);
    }
}
