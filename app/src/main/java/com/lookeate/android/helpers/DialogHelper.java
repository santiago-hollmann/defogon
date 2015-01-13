package com.lookeate.android.helpers;

import android.app.Dialog;
import android.lookeate.com.lookeate.R;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.lookeate.android.core_lib.helpers.LogInternal;
import com.lookeate.android.ui.fragments.CustomDialogFragment;
import com.lookeate.android.util.Constants;

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
                LogInternal.logProgressDialog("Opening", activity);
                CustomDialogFragment dialog = CustomDialogFragment.newInstance(DialogHelper.Dialogs.PROGRESS, title, message, cancelable);
                showDialog(dialog, activity, "progress");
                isShowingProgressDialog = true;
            }
        } else {
            LogInternal.logProgressDialog("Opening skipped", activity);
        }
    }

    //ABTEST Swap Game
    public synchronized static void showSwapProgress(final FragmentActivity activity, String message) {
        CustomDialogFragment progressFragment =
                (CustomDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("swapprogress");
        if (progressFragment == null) {
            LogInternal.logProgressDialog("Swap Progress Opening", activity);
            CustomDialogFragment dialog = CustomDialogFragment.newInstance(DialogHelper.Dialogs.SWAP_PROGRESS, Constants.EMPTY_STRING, message, false);
            showDialog(dialog, activity, "progress");
        }

    }

    //ABTEST Swap Game
    public synchronized static void hideSwapProgress(final FragmentActivity activity) {
        if (activity == null) {
            LogInternal.logProgressDialog("Closing Skipped", activity);
            return;
        }
        // Delaying the execution due to issues when trying to close the dialog
        // before it has finished opening
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CustomDialogFragment progressFragment =
                        (CustomDialogFragment) activity.getSupportFragmentManager().findFragmentByTag("swapprogress");
                if (progressFragment != null) {
                    LogInternal.logProgressDialog("Swap Closing", activity);
                    progressFragment.dismissAllowingStateLoss();
                } else {
                    LogInternal.logProgressDialog("Swap Closing Skipped (fragment not found)", activity);
                }
            }
        }, 20);
        LogInternal.logProgressDialog("Swap Closing Scheduled", activity);
    }

    public synchronized static void hideProgress(final FragmentActivity activity) {
        if (activity == null) {
            LogInternal.logProgressDialog("Closing Skipped", activity);
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
                    LogInternal.logProgressDialog("Closing", activity);
                    isShowingProgressDialog = false;
                    progressFragment.dismissAllowingStateLoss();
                } else {
                    LogInternal.logProgressDialog("Closing Skipped (fragment not found)", activity);
                }
            }
        }, 20);
        LogInternal.logProgressDialog("Closing Scheduled", activity);
    }

    public synchronized static void resetProgressPresenceFlag() {
        //This is supposed to be executed on Activity transitions.
        //If there's an active dialog in this situation it will be lost and we should pull down this flag so it can be re-created afterwards
        if (isShowingProgressDialog) {
            LogInternal.logProgressDialog("Had to reset presence flag", null);
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

    public interface Dialogs {
        public static final int GENERIC_ERROR = -2001;
        public static final int PROGRESS = 3000;
        public static final int SWAP_PROGRESS = 3010;
    }

    private static void showDialog(DialogFragment dialog, FragmentActivity activity, String dialogId) {
        dialog.show(activity.getSupportFragmentManager(), dialogId);
    }
}
