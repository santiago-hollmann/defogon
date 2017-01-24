package com.shollmann.android.fogon.helpers;

import android.app.Dialog;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.shollmann.android.fogon.R;
import com.shollmann.android.fogon.ui.fragments.CustomDialogFragment;

public class DialogHelper {
    private static boolean isShowingProgressDialog = false;

    public static void showError(final FragmentActivity activity, String title, String error) {
        showError(activity, title, error, DialogHelper.Dialogs.GENERIC_ERROR);
    }

    public static void showError(final FragmentActivity activity, String title, String error, int dialogId) {
        CustomDialogFragment dialog = CustomDialogFragment.newInstance(dialogId, title, error, R.string.ok, 0, 0, true);
        showDialog(dialog, activity, "error");
    }

    public static void showError(final FragmentActivity activity, int title, int error) {
        showError(activity, title, error, DialogHelper.Dialogs.GENERIC_ERROR);
    }

    public static void show(final FragmentActivity activity, Dialog dialog) {
        CustomDialogFragment dialogFragment = CustomDialogFragment.newInstance(dialog);
        showDialog(dialogFragment, activity, "dialog");
    }

    public static void showError(final FragmentActivity activity, int title, int error, int dialogId) {
        CustomDialogFragment dialog =
                CustomDialogFragment.newInstance(dialogId, activity.getString(title), activity.getString(error), R.string.ok, 0, 0, true);
        showDialog(dialog, activity, "error");
    }

    public static void showProgress(final FragmentActivity activity, String title, String message) {
        showProgress(activity, title, message, false);
    }

    public synchronized static void showProgress(final FragmentActivity activity, String title, String message, boolean cancelable) {
        if (!isShowingProgressDialog) {
            CustomDialogFragment progressFragment =
                    (CustomDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("progress");
            if (progressFragment == null) {
                CustomDialogFragment dialog = CustomDialogFragment.newInstance(DialogHelper.Dialogs.PROGRESS, title, message, cancelable);
                showDialog(dialog, activity, "progress");
                isShowingProgressDialog = true;
            }
        } else {
        }
    }

    public synchronized static void hideProgress(final FragmentActivity activity) {
        if (activity == null) {
            return;
        }
        // Delaying the execution due to issues when trying to close the dialog
        // before it has finished opening
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomDialogFragment progressFragment =
                        (CustomDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("progress");
                if (progressFragment != null) {
                    isShowingProgressDialog = false;
                    progressFragment.dismissAllowingStateLoss();
                } else {
                }
            }
        }, 20);
    }

    public synchronized static void resetProgressPresenceFlag() {
        //This is supposed to be executed on Activity transitions.
        //If there's an active dialog in this situation it will be lost and we should pull down this flag so it can be re-created afterwards
        if (isShowingProgressDialog) {
            isShowingProgressDialog = false;
        }
    }

    public static void show(final FragmentActivity activity, String title, String message, int ok, int cancel, int neutral, int dialogId) {
        CustomDialogFragment dialog = CustomDialogFragment.newInstance(dialogId, title, message, ok, cancel, neutral, true);
        showDialog(dialog, activity, String.valueOf(dialogId));
    }

    public static void show(final FragmentActivity activity, String title, String message, String ok, String cancel, String neutral,
                            int dialogId) {
        CustomDialogFragment dialog = CustomDialogFragment.newInstance(dialogId, title, message, ok, cancel, neutral, true);
        showDialog(dialog, activity, String.valueOf(dialogId));
    }

    public static void show(final FragmentActivity activity, String title, String message, int ok, int cancel, int neutral, int dialogId,
                            boolean cancelable) {
        CustomDialogFragment dialog = CustomDialogFragment.newInstance(dialogId, title, message, ok, cancel, neutral, cancelable);
        showDialog(dialog, activity, String.valueOf(dialogId));
    }

    public static void showWithCustomView(final FragmentActivity activity, String title, int ok, int cancel, int neutral, int dialogId) {
        showWithCustomView(activity, title, ok, cancel, neutral, dialogId, true);
    }

    public static void showWithCustomView(final FragmentActivity activity, String title, int ok, int cancel, int neutral, int dialogId,
                                          boolean cancelable) {
        CustomDialogFragment dialog = CustomDialogFragment.newInstance(dialogId, title, ok, cancel, neutral, cancelable);
        showDialog(dialog, activity, String.valueOf(dialogId));
    }

    public static boolean isShowingProgressDialog() {
        return isShowingProgressDialog;
    }

    private static void showDialog(DialogFragment dialog, FragmentActivity activity, String dialogId) {
        dialog.show(activity.getSupportFragmentManager(), dialogId);
    }

    public interface Dialogs {
        int GENERIC_ERROR = -2001;
        int PROGRESS = 3000;
    }
}
