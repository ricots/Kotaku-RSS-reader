package com.oskalenko.kotakurss.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oskalenko.kotakurss.R;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogResult.CANCEL;
import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogResult.OK;
import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogType.ONE_BUTTON_MODE;
import static com.oskalenko.kotakurss.ui.dialog.InformationDialog.DialogType.TWO_BUTTON_MODE;

/**
 * Created with IntelliJ IDEA.
 * User: oskalenko.v
 * Date: 04.07.2016
 * Time: 20:55
 */

public class InformationDialog extends DialogFragment implements DialogInterface.OnClickListener {

    public enum DialogResult {
        OK,
        CANCEL;
    }

    public enum DialogType {
        ONE_BUTTON_MODE,
        TWO_BUTTON_MODE
    }

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_MESSAGE = "arg_message";
    private static final String ARG_DIALOG_TYPE = "arg_dialog_type";

    private static boolean mIsShown;

    private DialogType mDialogType;
    private OnDialogResult mOnDialogResult;

    public static boolean isShown() {
        return mIsShown;
    }

    public static void setShown(boolean isShown) {
        InformationDialog.mIsShown = isShown;
    }

    public void setOnDialogResult(OnDialogResult onDialogResult) {
        this.mOnDialogResult = onDialogResult;
    }

    public static InformationDialog newInstance(String title, String message, DialogType dialogType) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putSerializable(ARG_DIALOG_TYPE, dialogType);

        InformationDialog dialog = new InformationDialog();
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);
        mDialogType = (DialogType) getArguments().get(ARG_DIALOG_TYPE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string._dialog_ok, this);

        if (mDialogType == TWO_BUTTON_MODE) {
            builder.setNegativeButton(R.string.dialog_cancel, this);
        }

        return builder.create();
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
        mIsShown = false;
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_NEGATIVE:
                break;
            case BUTTON_POSITIVE:
                if (mOnDialogResult != null) {
                    mOnDialogResult.onDialogResult(OK);
                }
                break;
        }
        dialog.dismiss();
    }

    private void setUpDialog() {
        Dialog dialog = getDialog();
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
    }

    public interface OnDialogResult {
        void onDialogResult(DialogResult dialogResult, Object... result);
    }
}
