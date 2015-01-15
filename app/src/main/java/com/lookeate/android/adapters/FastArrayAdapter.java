package com.lookeate.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class FastArrayAdapter<T> extends ArrayAdapter<T> {

    private FastListAdapter.OnClickListener<T> mListenerClick;

    public FastArrayAdapter(Context context, T[] objects) {
        super(context, 0);
        setAdapterData(objects);
    }

    public T getItem(int position) {
        return super.getItem(position);
    }

    public void setAdapterData(T[] objects) {
        clear();
        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                add(objects[i]);
            }
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = getNewView(getContext(), parent, position);
        }
        setData(view, getItem(position), position);
        return view;
    }

    protected abstract View getNewView(Context context, ViewGroup parent, int pos);

    protected abstract void setData(View view, T item, int pos);

    public void onClick(View v, T item) {
        if (mListenerClick != null) {
            mListenerClick.onClick(v, item);
        }
    }

    public void setOnClickListener(FastListAdapter.OnClickListener<T> listener) {
        mListenerClick = listener;
    }

}