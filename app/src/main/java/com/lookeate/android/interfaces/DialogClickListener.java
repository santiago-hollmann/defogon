package com.lookeate.android.interfaces;

import android.view.View;

public interface DialogClickListener {
    void onPositiveClick(int dialogId);

    void onNegativeClick(int dialogId);

    void onNeutralClick(int dialogId);

    void onCancel(int dialogId);

    void onDismiss(int dialogId);

    void onSelectedItem(int dialogId, int position, String item);

    View getCustomDialogView(int dialogId);
}
