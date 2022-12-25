package com.webmuseum.museum.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateHelper {
    public static Date parseStrToDate(String dateStr){
        if(dateStr == null || dateStr.isEmpty()){
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date;
        try{
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }

    public static String parseDateToStr(Date date){
        if(date == null){
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return formatter.format(date);
    }
}
