package com.elrain.bashim.receiver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.elrain.bashim.util.BashPreferences;
import com.elrain.bashim.util.Constants;

public class BashBroadcastReceiverTest extends InstrumentationTestCase {

    BashBroadcastReceiver mCancelNotificationReceiver;
    SharedPreferences settings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mCancelNotificationReceiver = new BashBroadcastReceiver();
        settings = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getContext());
    }

    @SmallTest
    public void testOnReceiveCancelIntent() {
        BashPreferences.getInstance(getInstrumentation().getContext()).increaseQuotCounter();
        BashPreferences.getInstance(getInstrumentation().getContext()).increaseQuotCounter();
        BashPreferences.getInstance(getInstrumentation().getContext()).increaseQuotCounter();
        MyContext context = new MyContext();
        Intent cancelIntent = new Intent();
        cancelIntent.setAction(Constants.INTENT_CANCEL);
        mCancelNotificationReceiver.onReceive(context, cancelIntent);
        assertEquals(0, BashPreferences.getInstance(getInstrumentation().getContext()).getQuotesCounter());
    }

    @SmallTest
    public void testOnReceiveCancelNewIntent() {
        BashPreferences.getInstance(getInstrumentation().getContext()).increaseQuotCounter();
        BashPreferences.getInstance(getInstrumentation().getContext()).increaseQuotCounter();
        BashPreferences.getInstance(getInstrumentation().getContext()).increaseQuotCounter();
        MyContext context = new MyContext();
        mCancelNotificationReceiver.onReceive(context, new Intent());
        assertEquals(true, BashPreferences.getInstance(getInstrumentation().getContext()).getQuotesCounter() != 0);
    }

    private class MyContext extends MockContext {

        @Override
        public String getPackageName() {
            return "com.elrain.bashim";
        }

        @Override
        public Object getSystemService(String name) {
            return getInstrumentation().getContext().getSystemService(name);
        }

        @Override
        public SharedPreferences getSharedPreferences(String name, int mode) {
            return getInstrumentation().getContext().getSharedPreferences(name, mode);
        }
    }
}
