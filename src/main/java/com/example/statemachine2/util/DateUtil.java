package com.example.statemachine2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static boolean isValid(String format,String dateStr) {
        boolean isValid;
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        try {
            Date date=sdf.parse(dateStr);
            //reverse from date back to original
            isValid=dateStr.equals(sdf.format(date))?true:false;
        }catch (ParseException pe) {
            isValid=false;
        }
        return isValid;
    }
    public static String dateToString(Date date,String format) {
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date stringToDate(String date,String format) throws ParseException {
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.parse(date);
    }
}
