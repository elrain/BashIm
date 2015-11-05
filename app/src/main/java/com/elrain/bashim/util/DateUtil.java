package com.elrain.bashim.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by denys.husher on 04.11.2015.
 */
public class DateUtil {
    public static Date parseDateFromXml(String dateString) {
        SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy H:mm:ss");
        Date d = new Date();
        try {
            d = f.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String getItemPubDate(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int hours = date.getHours();
        int minutes = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH) + 1;

        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day)
                + " " + (hours < 10 ? "0" + hours : hours) + ":" + minutes;
    }
}
