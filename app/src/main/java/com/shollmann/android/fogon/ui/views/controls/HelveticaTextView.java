package com.shollmann.android.fogon.ui.views.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.shollmann.android.fogon.AppApplication;
import com.shollmann.android.fogon.R;

public class HelveticaTextView extends TextView {

    public HelveticaTextView(Context context) {
        super(context);
        initialize(null);
    }

    public HelveticaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public HelveticaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        Typeface typeface = null;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HelveticaTextView);
            int index = a.getInt(R.styleable.HelveticaTextView_fontType, -1);
            switch (index) {
                case 0:
                    typeface = AppApplication.Fonts.HELVETICA_LIGHT;
                    break;
                case 1:
                    typeface = AppApplication.Fonts.HELVETICA_MEDIUM;
                    break;
                case 2:
                    typeface = AppApplication.Fonts.HELVETICA_REGULAR;
                    break;
            }
        }
        if (typeface != null) {
            this.setTypeface(typeface);
        } else {
            this.setTypeface(AppApplication.Fonts.HELVETICA_LIGHT);
        }
    }

}
