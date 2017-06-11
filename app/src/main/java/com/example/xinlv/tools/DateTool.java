package com.example.xinlv.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by foot on 2016/4/3.
 * input
 */
public class DateTool {
public static String getMonthDateHourMinuteFromInt(long time){
    String mdhm=null;
    DateFormat format=new SimpleDateFormat("MM/dd/hh/mm");
    long time_millis=time*1000;
    Calendar calendar=Calendar.getInstance();
    calendar.setTimeInMillis(time_millis);
    mdhm=format.format(calendar.getTime());
    return  mdhm;
}

    public static String getMonthDateHourMinuteFromString(String time){
        String mdhm=null;
        DateFormat format=new SimpleDateFormat("MM/dd/hh/mm");
        long time_millis=((long)Integer.parseInt(time))*1000;
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time_millis);
        mdhm=format.format(calendar.getTime());
        return  mdhm;
    }

}
