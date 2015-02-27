package com.shollmann.android.fogon.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.ui.views.controls.TouchImageView;
import com.squareup.picasso.Picasso;

public class PreviewImageFragment extends Fragment {
    private View mView;
    private TouchImageView image;
    public final static String IMAGE = "image";
    public final static String IMAGE_RESOURCE = "imageResource";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_preview_images, container, false);
        image = (TouchImageView) mView.findViewById(R.id.preview_image);

        String url = getArguments().getString(IMAGE);
        Picasso.with(getActivity().getApplicationContext()).load(url).skipMemoryCache().into(image);

        return mView;
    }

}
