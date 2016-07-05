package com.oskalenko.kotakurss.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.oskalenko.kotakurss.ui.dialog.BaseDialog.DialogResult.CANCEL;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 20:55
 */

public abstract class BaseDialog extends DialogFragment {

    public enum DialogResult {
        OK,
        CANCEL;
    }

    private String mDialogTitle;;
    private OnDialogResult mOnDialogResult;

    public void setOnDialogResult(OnDialogResult onDialogResult) {
        this.mOnDialogResult = onDialogResult;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setUpDialog();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnDialogResult != null) {
            mOnDialogResult.onDialogResult(CANCEL);
        }
        super.onDismiss(dialog);
    }

    private void setUpDialog() {
        Dialog dialog = getDialog();
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
    }

    private AlertDialog.Builder getDialogBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mDialogTitle != null ? mDialogTitle : null);
        return  builder;
    }

    public interface OnDialogResult {
        void onDialogResult(DialogResult dialogResult, Object... result);
    }
}
