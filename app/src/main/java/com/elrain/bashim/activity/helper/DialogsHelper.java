package com.elrain.bashim.activity.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.elrain.bashim.R;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class DialogsHelper {

    private static final DialogInterface.OnClickListener CLOSE_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public static AlertDialog noInternetDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title_no_internet);
        builder.setMessage(R.string.dialog_text_no_internet);
        builder.setPositiveButton(R.string.dialog_btn_text_ok, CLOSE_LISTENER);
        return builder.create();
    }

    public static AlertDialog noInternetDialog(Context context, @NonNull DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title_no_internet);
        builder.setMessage(R.string.dialog_text_no_internet);
        builder.setPositiveButton(R.string.dialog_btn_text_retry, listener);
        builder.setNegativeButton(R.string.dialog_btn_text_cancel, CLOSE_LISTENER);
        return builder.create();
    }
}
