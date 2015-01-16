package com.lookeate.android.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.lookeate.android.R;
import com.lookeate.android.util.Constants;

@SuppressLint("NewApi")
public class CustomSwitchView extends LinearLayout implements android.widget.Checkable {

    private CompoundButton switchButton;
    private CompoundButton.OnCheckedChangeListener listener;

    public CustomSwitchView(Context context) {
        this(context, null);
    }

    public CustomSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomSwitchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        switchButton.setEnabled(enabled);
    }

    private void initialize() {
        inflate(getContext(), R.layout.view_custom_switch, this);
        switchButton = (CompoundButton) findViewById(R.id.check);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    listener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle savedState = (Bundle) state;
        super.onRestoreInstanceState(savedState.getParcelable(Constants.ExtraKeys.DATA));
        boolean checked = savedState.getBoolean(Constants.ExtraKeys.DATA_2, false);
        if (checked) {
            switchButton.setChecked(checked);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(Constants.ExtraKeys.DATA, super.onSaveInstanceState());
        state.putBoolean(Constants.ExtraKeys.DATA_2, switchButton.isChecked());
        return state;
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isChecked() {
        return switchButton.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        switchButton.setChecked(checked);
        switchButton.requestLayout();
    }

    @Override
    public void toggle() {
        switchButton.toggle();
    }

    public void setTextOn(CharSequence textOn) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            ((Switch) switchButton).setTextOn(textOn);
        }
    }

    public void setTextOff(CharSequence textOff) {
        if (android.os.Build.VERSION.SDK_INT >= 14) {
            ((Switch) switchButton).setTextOff(textOff);
        }
    }
}