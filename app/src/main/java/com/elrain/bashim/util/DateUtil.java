package com.elrain.bashim.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateUtil {

    private static final String[] DATE_FORMATS = new String[]{"EEE, dd MMM yyyy H:mm:ss",
            "dd.MM.yy H:mm", "yyyy-MM-dd H:mm"};

    /**
     * Parses date from <code>String</code> to <code>Date</code>. If ParseException happened will be returned <code>new Date()</code>
     *
     * @param dateString inputted date in <code>String</code>. This <code>String</code> must be like:
     *                   <i>Fri, 13 Nov 2015 11:46:03</i>
     * @return <code>Date</code> object with parsed date
     */
    public static Date parseDateFromXml(String dateString) {
        Date d = new Date();
        if (null == dateString) return d;
        SimpleDateFormat f = new SimpleDateFormat("", Locale.US);
        for (String dateFormat : DATE_FORMATS) {
            f.applyPattern(dateFormat);
            try {
                d = f.parse(dateString);
            } catch (ParseException e) {
                continue;
            }
            break;
        }
        return d;
    }

    /**
     * Parses <code>Date</code> object and return this date in <code>String</code>
     * with specific format.
     *
     * @param date Inputted date
     * @return <code>String</code> object with specific format like <i>2015-11-13 11:13</i>
     */
    public static String getItemPubDate(Date date) {
        if (null == date) return null;
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hours = date.getHours();
        int minutes = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;

        return year + "-" + isZeroNeeded(month) + "-" + isZeroNeeded(day) + " "
                + isZeroNeeded(hours) + ":" + isZeroNeeded(minutes);
    }

    private static String isZeroNeeded(int value) {
        return value < 10 ? "0" + value : String.valueOf(value);
    }

}
