package com.shollmann.android.fogon.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.shollmann.android.fogon.DeFogonApplication;
import com.shollmann.android.fogon.interfaces.DialogClickListener;

public class CustomDialogFragment extends DialogFragment {

    private Dialog dialog;

    public static CustomDialogFragment newInstance(int dialogId, String title, int ok, int cancel, int neutral, boolean cancelable) {
        CustomDialogFragment frag = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        if (ok != 0) {
            args.putString("ok", DeFogonApplication.getApplication().getString(ok));
        }
        if (cancel != 0) {
            args.putString("cancel", DeFogonApplication.getApplication().getString(cancel));
        }
        if (neutral != 0) {
            args.putString("neutral", DeFogonApplication.getApplication().getString(neutral));
        }
        args.putInt("dialogId", dialogId);
        args.putBoolean("cancelable", cancelable);
        args.putBoolean("useCustomView", true);
        frag.setArguments(args);
        return frag;
    }

    public static CustomDialogFragment newInstance(int dialogId, String title, String message, int ok, int cancel, int neutral,
                                                   boolean cancelable) {
        CustomDialogFragment frag = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        if (ok > 0) {
            args.putString("ok", DeFogonApplication.getApplication().getString(ok));
        }
        if (cancel > 0) {
            args.putString("cancel", DeFogonApplication.getApplication().getString(cancel));
        }
        if (neutral > 0) {
            args.putString("neutral", DeFogonApplication.getApplication().getString(neutral));
        }
        args.putInt("dialogId", dialogId);
        args.putBoolean("cancelable", cancelable);
        frag.setArguments(args);
        return frag;
    }

    public static CustomDialogFragment newInstance(int dialogId, String title, String message, String ok, String cancel, String neutral,
                                                   boolean cancelable) {
        CustomDialogFragment frag = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("ok", ok);
        args.putString("cancel", cancel);
        args.putString("neutral", neutral);
        args.putInt("dialogId", dialogId);
        args.putBoolean("cancelable", cancelable);
        frag.setArguments(args);
        return frag;
    }

    public static CustomDialogFragment newInstance(int dialogId, String title, String message, boolean cancelable) {
        CustomDialogFragment frag = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putBoolean("progress", true);
        args.putInt("dialogId", dialogId);
        args.putBoolean("cancelable", cancelable);
        frag.setArguments(args);
        return frag;
    }

    public static CustomDialogFragment newInstance(Dialog dialog) {
        CustomDialogFragment frag = new CustomDialogFragment();
        frag.setDialog(dialog);
        return frag;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (dialog == null) {
            boolean useCustomView = getArguments().getBoolean("useCustomView");
            String title = getArguments().getString("title");
            String message = getArguments().getString("message");
            String ok = getArguments().getString("ok");
            String neutral = getArguments().getString("neutral");
            String cancel = getArguments().getString("cancel");
            boolean cancelable = getArguments().getBoolean("cancelable", true);
            final int dialogId = getArguments().getInt("dialogId");
            boolean progress = getArguments().getBoolean("progress");

            if (progress) {
                final ProgressDialog dialog = new ProgressDialog(getActivity());
                if (title != null) {
                    dialog.setTitle(title);
                }
                if (message != null) {
                    dialog.setMessage(message);
                }
                setCancelable(cancelable);
                dialog.setCancelable(cancelable);
                dialog.setIndeterminate(true);
                return dialog;
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                if (title != null) {
                    b.setTitle(title);
                }
                if (useCustomView) {
                    b.setView(((DialogClickListener) getActivity()).getCustomDialogView(dialogId));
                } else {
                    if (message != null) {
                        b.setMessage(message);
                    }
                }
                if (!TextUtils.isEmpty(ok)) {
                    b.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((DialogClickListener) getActivity()).onPositiveClick(dialogId);
                        }
                    });

                }
                if (!TextUtils.isEmpty(neutral)) {
                    b.setNeutralButton(cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((DialogClickListener) getActivity()).onNeutralClick(dialogId);
                        }
                    });
                }
                if (!TextUtils.isEmpty(cancel)) {
                    b.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((DialogClickListener) getActivity()).onNegativeClick(dialogId);
                        }
                    });
                }
                if (cancelable) {
                    b.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                                ((DialogClickListener) getActivity()).onDismiss(dialogId);
                            }
                            return false;
                        }
                    });
                    b.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            ((DialogClickListener) getActivity()).onCancel(dialogId);
                        }
                    });
                }
                b.setCancelable(cancelable);
                setCancelable(cancelable);
                Dialog dialog = b.create();
                dialog.setCancelable(cancelable);
                dialog.setCanceledOnTouchOutside(cancelable);

                return dialog;
            }
        } else {
            return dialog;
        }
    }

}