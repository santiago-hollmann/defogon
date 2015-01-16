package com.lookeate.android.ui.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lookeate.android.R;

public class CustomSpinnerList extends CustomPopupWindow {
    private static final int FIX_HEIGHT = 87;
    private final static int PADDING_LIST = -5;
    private final static int DISPLAY_ITEMS = 5;

    private View mRootView;
    private LayoutInflater inflater;
    private ListView mListView;

    private OnItemClickListener mListener;

    private int showItems = -1;
    private int itemHeight = -1;
    private int itemWidth = -1;

    public interface ISpinnerItem {
        boolean isEnabled();
    }

    public CustomSpinnerList(Context context) {
        super(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRootViewId();
    }

    public void setRootViewId() {
        mRootView = (ViewGroup) inflater.inflate(R.layout.custom_spinner_list, null);
        mListView = (ListView) mRootView.findViewById(R.id.spinner_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                if ((!(view instanceof ISpinnerItem) && mListView.getAdapter().isEnabled(position)) || ((ISpinnerItem) view).isEnabled()) {
                    if (mListener != null) {
                        String stringValueOfItemClicked = mListView.getAdapter().getItem(position).toString();

                        mListener.onItemClick(position, view, stringValueOfItemClicked);
                    }
                    dismiss();
                }
            }
        });
        setContentView(mRootView);
    }

    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public ListAdapter getAdapter() {
        return mListView.getAdapter();
    }

    public void show(View anchor) {
        int xPos, yPos;
        int[] location = new int[2];

        mListView.smoothScrollToPosition(0);
        preShow();
        anchor.getLocationOnScreen(location);

        xPos = location[0];
        yPos = location[1] + anchor.getHeight() + PADDING_LIST;

        int size = showItems;
        int height = itemHeight;
        int width = itemWidth;

        if (size == -1) {
            if (mListView.getAdapter() != null) {
                size = mListView.getAdapter().getCount();
                if (size > DISPLAY_ITEMS) {
                    size = DISPLAY_ITEMS;
                }
            } else {
                size = DISPLAY_ITEMS;
            }
        }

        if (itemHeight == -1) {
            height = anchor.getHeight();
        }

        if (itemWidth == -1) {
            width = anchor.getWidth();
        }

        mWindow.setWidth(width);
        // REMIND Commenting this to make the oveflow menu not scrollable
        // remove if this isn't breaking anything else
        // mWindow.setHeight(height * size + FIX_HEIGHT);
        setAnimationStyle();

        int fixAlignment = mContext.getResources().getDimensionPixelSize(R.dimen.big_distance);
        xPos = (xPos - width) + fixAlignment;

        mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private void setAnimationStyle() {
        mWindow.setAnimationStyle(R.style.Animations_PopDownMenu);
    }

    public void setShowItems(int showItems) {
        this.showItems = showItems;
    }

    public int getShowItems() {
        return showItems;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public interface OnItemClickListener {
        public abstract void onItemClick(int pos, View view, String key);
    }

    public interface IStatusChangeListener {
        void onStatusChanged();
    }

    public void hideScrollbars() {
        mListView.setVerticalScrollBarEnabled(false);
    }

    public void setMinWidth(int minWidth) {
        mListView.setMinimumWidth(minWidth);
    }
}
