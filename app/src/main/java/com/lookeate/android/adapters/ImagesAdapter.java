package com.lookeate.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.olx.grog.model.GalleryImage;
import com.olx.olx.ui.views.GalleryImageItemView;

import java.util.ArrayList;

public class ImagesAdapter extends FastListAdapter<GalleryImage> {

    public ImagesAdapter(Context context, ArrayList<GalleryImage> images) {
        super(context, images);
    }

    @Override
    public View getNewView(Context context, ViewGroup parent, int position) {
        return new GalleryImageItemView(context);
    }

    @Override
    protected void setData(View view, GalleryImage item, int pos) {
        GalleryImageItemView aiv = (GalleryImageItemView) view;
        aiv.setData(item, isScrolling());
    }

    @Override
    protected void updateData(View view, GalleryImage item, int pos) {
        GalleryImageItemView aiv = (GalleryImageItemView) view;
        aiv.updateData(item);
    }

}
