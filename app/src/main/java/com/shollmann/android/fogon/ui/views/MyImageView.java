package com.shollmann.android.fogon.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {
    private boolean fillSpace = false;

    public void setFillSpace(boolean value) {
        fillSpace = value;
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyImageView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fillSpace && getDrawable() != null) {
            int sideSize = MeasureSpec.getSize(widthMeasureSpec);
            setMeasuredDimension(sideSize, sideSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}