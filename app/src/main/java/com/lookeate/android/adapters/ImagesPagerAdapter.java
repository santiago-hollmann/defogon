package com.lookeate.android.adapters;

import android.content.Context;
import android.lookeate.com.lookeate.R;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lookeate.android.AppApplication;
import com.lookeate.android.helpers.ResourcesHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImagesPagerAdapter extends PagerAdapter implements OnClickListener, Callback {

    public interface OnImageListener {
        public void onImageClickListener(View view, int position);

        public void onImageSuccessListener();
    }

    private final String[] images;
    private final LayoutInflater inflater;
    private final int padding;
    private OnImageListener listener;

    public ImagesPagerAdapter(String[] images) {
        super();
        this.images = images;
        this.inflater = (LayoutInflater) AppApplication.getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.padding = ResourcesHelper.getDimensionPixelSize(R.dimen.small_padding);
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
        final FrameLayout itemView = (FrameLayout) inflater.inflate(R.layout.image_view, container, false);
        if (getCount() > 1) {
            if (position == 0) {
                itemView.setPadding(0, 0, padding, 0);
            } else if (position == getCount() - 1) {
                itemView.setPadding(padding, 0, 0, 0);
            } else {
                itemView.setPadding(padding, 0, padding, 0);
            }
        }

        final ImageView image = (ImageView) itemView.findViewById(R.id.item_image);
        image.setTag(position);
        image.setOnClickListener(this);
        if (position == 0) {
            Picasso.with(AppApplication.getApplication()).load(images[position]).fit().centerCrop().skipMemoryCache().noFade()
                    .into(image, this);
        } else {
            Picasso.with(AppApplication.getApplication()).load(images[position]).fit().centerCrop().skipMemoryCache().into(image);
        }
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

    public void setOnImageListener(OnImageListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        listener.onImageClickListener(v, position);
    }

    @Override
    public void onError() {
    }

    @Override
    public void onSuccess() {
        if (listener != null) {
            listener.onImageSuccessListener();
        }
    }
}
