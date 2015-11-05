package com.elrain.bashim.activity.helper;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by denys.husher on 03.11.2015.
 */
public class ProgressBarDialogBuilder {
    private final Context mContext;
    private final String mMessage;

    public ProgressBarDialogBuilder(Context context, String message) {
        mContext = context;
        mMessage = message;
    }

    public ProgressDialog build() {
        ProgressDialog mPbDialog = new ProgressDialog(mContext);
        mPbDialog.setCancelable(false);
        mPbDialog.setMessage(mMessage);
        mPbDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return mPbDialog;
    }
}
