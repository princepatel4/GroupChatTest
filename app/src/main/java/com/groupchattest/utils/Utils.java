package com.groupchattest.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class Utils {


    public static long getCurrentTimeStamp(){
        return  System.currentTimeMillis()/1000;
    }

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
        return date;
    }
}
