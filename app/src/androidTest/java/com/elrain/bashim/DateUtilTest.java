package com.elrain.bashim;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.elrain.bashim.util.DateUtil;

import java.util.Date;

/**
 * Created by denys.husher on 11.11.2015.
 */
public class DateUtilTest extends InstrumentationTestCase {

    @SmallTest
    public void testParseDateFromXmlOnNull() {
        try {
            DateUtil.parseDateFromXml(null);
        } catch (NullPointerException e) {
            fail();
        }
    }

    @SmallTest
    public void testUnknownFormat() {
        if (!(DateUtil.parseDateFromXml("dasdasdads") instanceof Date))
            fail();
    }

    public void testGetPubDateOnNull() {
        try {
            DateUtil.getItemPubDate(null);
        } catch (NullPointerException e) {
            fail();
        }
    }
}
