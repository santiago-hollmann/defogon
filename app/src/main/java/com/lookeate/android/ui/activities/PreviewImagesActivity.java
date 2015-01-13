package com.lookeate.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.lookeate.com.lookeate.R;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lookeate.android.adapters.PreviewImagesAdapter;
import com.lookeate.android.ui.views.controls.ImagesPageIndicator;
import com.lookeate.android.ui.views.controls.ImagesViewPager;

public class PreviewImagesActivity extends BaseActivity {
    private PreviewImagesAdapter mAdapter;
    protected ImagesViewPager mPager;
    private ImagesPageIndicator mTitleIndicator;
    private int startingPosition;
    private String[] images;
    private int[] imagesResources;

    public static final String IMAGES = "images";
    public static final String IMAGES_RESOURCES = "imagesResources";
    public static final String START_POSITION = "startPosition";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_images);
        getSupportActionBar().hide();

        mPager = (ImagesViewPager) findViewById(R.id.libuic_activity_pager_layout);
        mTitleIndicator = (ImagesPageIndicator) findViewById(R.id.indicator);
        Bundle extras = getIntent().getExtras();

        if (extras.containsKey(IMAGES)) {
            setImages(extras.getStringArray(IMAGES));
        }

        if (extras.containsKey(IMAGES_RESOURCES)) {
            setImagesResources(extras.getIntArray(IMAGES_RESOURCES));
        }

        setStartingPosition(extras.getInt(START_POSITION, startingPosition));

        if (getImages() == null) {
            mAdapter = new PreviewImagesAdapter(getSupportFragmentManager(), getImagesResources());
        } else {
            mAdapter = new PreviewImagesAdapter(getSupportFragmentManager(), getImages());
        }

        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mAdapter);

        mTitleIndicator.setViewPager(mPager);
        mTitleIndicator.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
        });

        mPager.setCurrentItem(getStartingPosition());
        setPosition(getStartingPosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public int[] getImagesResources() {
        return imagesResources;
    }

    public void setImagesResources(int[] imagesResources) {
        this.imagesResources = imagesResources;
    }

    public int getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(int startingPosition) {
        this.startingPosition = startingPosition;
    }

    @Override
    public void onResultReceived(Intent intent) {
    }

    @Override
    public void onResultReceivedNoError(Intent intent) {
    }

    private void setPosition(int position) {
        Intent data = new Intent();
        data.putExtra(START_POSITION, position);
        setResult(Activity.RESULT_OK, data);
    }
}
