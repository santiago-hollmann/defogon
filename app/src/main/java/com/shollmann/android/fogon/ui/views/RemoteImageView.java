package com.shollmann.android.fogon.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.helpers.ResourcesHelper;
import com.shollmann.android.fogon.util.ImageLoader;
import com.shollmann.android.fogon.util.ImageUtils;

public class RemoteImageView extends FrameLayout {

    private static final ScaleType[] sScaleTypeArray =
            {ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER,
                    ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE};
    private int noImage;
    private MyImageView image;
    private ProgressBar progress;
    private boolean showProgressBar = true;
    private Object data;
    public static final int MIN_SIZE = ResourcesHelper.getDimensionPixelSize(R.dimen.finger_size);

    public void setData(Object o) {
        data = o;
    }

    public Object getData() {
        return data;
    }

    public void setShowProgressBar(boolean showProgressBar) {
        this.showProgressBar = showProgressBar;
    }

    public RemoteImageView(Context context) {
        this(context, null, 0);
    }

    public RemoteImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RemoteImageView);
        Drawable d = a.getDrawable(R.styleable.RemoteImageView_src);
        if (!isInEditMode()) {
            int index = a.getInt(R.styleable.RemoteImageView_scaleType, -1);
            if (index >= 0) {
                image.setScaleType(sScaleTypeArray[index]);
            }
            if (d != null) {
                image.setImageDrawable(d);
                progress.setVisibility(View.GONE);
            } else {
                progress.setVisibility(View.VISIBLE);
            }
            image.setFillSpace(a.getBoolean(R.styleable.RemoteImageView_fillSpace, false));
        } else {
            ResourcesHelper.setBackgroundDrawable(this, d);
            // setBackgroundResource(R.drawable.radial_gray);
        }
        a.recycle();
    }

    public MyImageView getImage() {
        return image;
    }

    private void initialize() {
        if (isInEditMode()) {
            ImageView image = new ImageView(this.getContext());
            image.setImageResource(R.drawable.placeholder);
            addView(image);
        } else {
            inflate(getContext(), R.layout.view_remote_image, this);
            image = (MyImageView) findViewById(R.id.image);
            progress = (ProgressBar) findViewById(R.id.progress);
        }
    }

    public void loadImage(String image, int iResourceImage) {
        loadImage(image, iResourceImage, null);
    }

    public PointF getSize() {
        PointF iSize = new PointF(MIN_SIZE, MIN_SIZE);
        try {
            if (this.getLayoutParams().width != -1) {
                iSize = new PointF(this.getLayoutParams().width, this.getLayoutParams().height);
            } else {
                iSize = new PointF(this.image.getDrawable().getIntrinsicWidth(), this.image.getDrawable().getIntrinsicHeight());
            }
            if (iSize.x < MIN_SIZE) {
                iSize.x = MIN_SIZE;
            }
            if (iSize.y < MIN_SIZE) {
                iSize.y = MIN_SIZE;
            }
        } catch (Exception ex) {
            iSize = new PointF(MIN_SIZE, MIN_SIZE);
        }
        return iSize;
    }

    public void loadImage(Uri uri, int iResourceImage, int exifRotation) {

        this.image.setVisibility(View.GONE);
        this.progress.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
        String sPath = ImageUtils.getPathImages();
        noImage = iResourceImage;

        PointF iSize = getSize();

        if (uri != null) {
            this.setTag(uri.toString());
            ImageLoader.get().displayImage(uri, this, sPath, null, iSize, exifRotation);
        } else {
            showStubImage();
        }
    }

    public void loadImage(Uri uri, int iResourceImage) {

        this.image.setVisibility(View.GONE);
        this.progress.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
        noImage = iResourceImage;

        if (uri != null) {
            this.setTag(uri.toString());
            loadImage(uri.toString(), R.drawable.placeholder);
        } else {
            showStubImage();
        }
    }

    public void loadImage(String image, int iResourceImage, String sFileName) {
        if (image != null) {
            this.setTag(image);
        }

        this.image.setVisibility(View.GONE);
        this.progress.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
        String sPath = ImageUtils.getPathImages();
        noImage = iResourceImage;

        PointF iSize = getSize();

        if (image != null) {
            this.setTag(image);
            ImageLoader.get().displayImage(image, this, sPath, sFileName, iSize, 0);
        } else {
            showStubImage();
        }
    }

    public void setImageBitmap(Bitmap bmp) {
        setTag(null);
        image.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        image.setImageBitmap(bmp);
    }

    public void notImageAvailable(int stubId) {
        this.noImage = stubId;
        showStubImage();
    }

    public void reset() {
        setTag(null);
        image.setVisibility(View.GONE);
        // image.setImageResource(noImage);
        progress.setVisibility(showProgressBar ? View.VISIBLE : View.GONE);
    }

    public void setImageDrawable(Drawable icon) {
        setTag(null);
        image.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        image.setImageDrawable(icon);
    }

    public void setImageResource(int drawable) {
        setTag(null);
        image.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        image.setImageResource(drawable);
    }

    public void showStubImage() {
        setTag(null);
        image.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
        this.image.setImageResource(noImage);
    }

    public void hideProgressBar() {
        this.progress.setVisibility(View.GONE);
    }

}
