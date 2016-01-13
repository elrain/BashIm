package com.elrain.bashim.activity.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

import com.elrain.bashim.activity.MainActivity;

import java.util.concurrent.CountDownLatch;

/**
 * Created by denys.husher on 13.01.2016.
 */
public class DialogHelperTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public DialogHelperTest() {
        super(MainActivity.class);
    }

    public void testNoInternetDialog() throws Throwable {
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                assertEquals(which, DialogInterface.BUTTON_POSITIVE);
                fail();
            }
        };
        final AlertDialog dialog = DialogsHelper.noInternetDialog(getActivity(), listener);

        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });

        getInstrumentation().waitForIdleSync();
        assertEquals(true, dialog.isShowing());
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            }
        });
        getInstrumentation().waitForIdleSync();
    }
}
