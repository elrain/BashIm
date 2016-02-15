package com.elrain.bashim.activity.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.elrain.bashim.R;
import com.elrain.bashim.util.Constants;

public class DialogsHelper {

    private static final String YEAR = "year/";
    private static final String MONTH = "month/";
    private static final String DIVIDER = "/";

    private static final DialogInterface.OnClickListener CLOSE_LISTENER = (dialog, which) -> dialog.dismiss();

    public static AlertDialog noInternetDialog(Context context, @NonNull DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title_no_internet);
        builder.setMessage(R.string.dialog_text_no_internet);
        builder.setPositiveButton(R.string.dialog_btn_text_retry, listener);
        builder.setNegativeButton(R.string.dialog_btn_text_cancel, CLOSE_LISTENER);
        return builder.create();
    }

    public static AlertDialog noInternetByPreferencesDialog(Context context, @NonNull DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.dialog_title_no_internet);
        builder.setMessage(R.string.dialog_text_no_internet_by_settings);
        builder.setPositiveButton(R.string.dialog_btn_text_retry, listener);
        builder.setNegativeButton(R.string.dialog_btn_text_cancel, CLOSE_LISTENER);
        return builder.create();
    }

    public static AlertDialog datePickerDialog(Context context, final OnDatePicked listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.date_picker_view, null);
        builder.setView(view);
        builder.setTitle(R.string.dialog_title_date_picker);

        final Spinner spYears = (Spinner) view.findViewById(R.id.spYear);
        final Spinner spMonth = (Spinner) view.findViewById(R.id.spMonth);
        final String[] years = context.getResources().getStringArray(R.array.years);
        final String[] months = context.getResources().getStringArray(R.array.months);

        final ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYears.setAdapter(yearAdapter);

        final ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(monthAdapter);

        builder.setPositiveButton(R.string.dialog_btn_text_done, (dialog, which) -> {
            if (0 == spMonth.getSelectedItemPosition())
                listener.loadUrl(Constants.BEST_URL + YEAR + years[spYears.getSelectedItemPosition()]);
            else
                listener.loadUrl(Constants.BEST_URL + MONTH
                        + years[spYears.getSelectedItemPosition()] + DIVIDER
                        + spMonth.getSelectedItemPosition());

        });
        return builder.create();
    }

    public static AlertDialog abortedDownload(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.action_main));
        builder.setMessage(context.getString(R.string.dialog_text_aborted_download));
        builder.setNegativeButton(context.getString(R.string.dialog_btn_text_ok), CLOSE_LISTENER);
        return builder.create();
    }
}
