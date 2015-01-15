package com.lookeate.android.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.lookeate.android.ui.fragments.PreviewImageFragment;

public class PreviewImagesAdapter extends FragmentStatePagerAdapter {
    private String[] images;
    private int[] imagesResources;

    public PreviewImagesAdapter(FragmentManager fm, String[] images) {
        super(fm);
        this.images = images;
    }

    public PreviewImagesAdapter(FragmentManager fm, int[] images) {
        super(fm);
        this.imagesResources = images;
    }

    @Override
    public int getCount() {
        if (images == null) {
            return imagesResources.length;
        } else {
            return images.length;
        }
    }

    @Override
    public Fragment getItem(int position) {
        PreviewImageFragment fragment = new PreviewImageFragment();
        Bundle args = new Bundle();
        if (images == null) {
            args.putInt(PreviewImageFragment.IMAGE_RESOURCE, imagesResources[position]);
        } else {
            args.putString(PreviewImageFragment.IMAGE, images[position]);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

}
