package com.lookeate.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class FastListAdapter<T> extends BaseAdapter implements OnScrollListener {

    public abstract interface OnClickListener<T> {
        public abstract void onClick(View v, T item);
    }

    protected List<T> mDataObjects;
    private OnClickListener<T> mListenerClick;
    private final Context context;
    private int firstOne = 0;
    private boolean scrolling;
    private int lastState = 0;
    private OnScrollListener mListener;

    public FastListAdapter(Context context, List<T> objects) {
        this.context = context;
        mDataObjects = objects;
    }

    public void setListItems(List<T> objects) {
        mDataObjects = objects;
    }

    public List<T> getAllItems() {
        return mDataObjects;
    }

    @Override
    public final int getCount() {
        if (mDataObjects != null) {
            return mDataObjects.size();
        }
        return 0;
    }

    @Override
    public final T getItem(int position) {
        if (mDataObjects != null) {
            return mDataObjects.get(position);
        }
        return null;
    }

    public int getPosition(T item) {
        return mDataObjects.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = getNewView(context, parent, position);
        }
        setData(view, getItem(position), position);
        return view;
    }

    protected abstract View getNewView(Context context, ViewGroup parent, int pos);

    protected abstract void setData(View view, T item, int pos);

    protected abstract void updateData(View view, T item, int pos);

    public void onClick(View v, T item) {
        if (mListenerClick != null) {
            mListenerClick.onClick(v, item);
        }
    }

    public void setOnClickListener(OnClickListener<T> listener) {
        mListenerClick = listener;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        mListener = listener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstOne = firstVisibleItem;
        if (mListener != null) {
            mListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView parent, int scrollState) {
        if (mListener != null) {
            mListener.onScrollStateChanged(parent, scrollState);
        }
        if (lastState != 2) {
            scrolling = scrollState != 0;
        } else {
            scrolling = scrollState == 2;
        }
        lastState = scrollState;

        if (!scrolling) {
            int number = firstOne;
            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                updateData(view, getItem(number), number);
                number++;
            }
        }

    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

}
