package com.elrain.bashim.util;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.Calendar;
import java.util.Date;

public class DateUtilTest extends InstrumentationTestCase {

    private static final String DATE_ONE = "Fri, 13 Nov 2015 11:46:03";
    private static final String DATE_TWO = "25.01.13 10:10";
    private static final String DATE_THREE = "2015-03-14 17:03";

    @SmallTest
    public void testParseDateFromXmlOnNull() {
        try {
            DateUtil.parseDateFromXml(null);
        } catch (NullPointerException e) {
            fail();
        }
    }

    @SmallTest
    public void testParseDateFormatOne() {
        Calendar c = Calendar.getInstance();
        c.set(2015, 10, 13, 11, 46, 3);
        c.set(Calendar.MILLISECOND, 0);
        Date d = DateUtil.parseDateFromXml(DATE_ONE);
        assertEquals(d.getTime(), c.getTime().getTime());
    }

    @SmallTest
    public void testParseDateFormatTwo() {
        Calendar c = Calendar.getInstance();
        c.set(2013, 0, 25, 10, 10, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date d = DateUtil.parseDateFromXml(DATE_TWO);
        assertEquals(d.getTime(), c.getTime().getTime());
    }

    @SmallTest
    public void testParseDateFormatThree() {
        Calendar c = Calendar.getInstance();
        c.set(2015, 2, 14, 17, 3, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date d = DateUtil.parseDateFromXml(DATE_THREE);
        assertEquals(d.getTime(), c.getTime().getTime());
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
