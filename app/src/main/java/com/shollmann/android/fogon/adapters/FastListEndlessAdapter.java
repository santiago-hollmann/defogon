package com.shollmann.android.fogon.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class FastListEndlessAdapter<T> extends FastListAdapter<T> {

    private OnLoadMoreListener loadMoreListener;
    private final int pageSize;
    private boolean keepLoading;
    private boolean loadMore = true;

    public FastListEndlessAdapter(Context context, List<T> objects, int pageSize) {
        super(context, objects);
        this.pageSize = pageSize;
        registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                loadMore = true;
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                loadMore = true;
            }
        });
    }

    public void setKeepLoading(boolean keepLoading) {
        this.keepLoading = keepLoading;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (keepLoading && mDataObjects != null && mDataObjects.size() >= pageSize && (position + (pageSize / 2)) > mDataObjects.size() &&
                loadMoreListener != null && loadMore) {
            loadMore = false;
            loadMoreListener.loadMore();
        }
        return super.getView(position, view, parent);
    }

    public interface OnLoadMoreListener {
        public void loadMore();
    }
}