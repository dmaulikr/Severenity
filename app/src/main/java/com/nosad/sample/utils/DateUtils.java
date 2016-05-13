package com.nosad.sample.utils;

import com.nosad.sample.utils.common.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andriy on 4/30/2016.
 */
public class DateUtils {

    private static Calendar _calendar = Calendar.getInstance(Constants.LOCALE);
    private static SimpleDateFormat _dataFormat = new SimpleDateFormat(Constants.TIME_FORMAT);

    public static int getDayFromTimestamp(String timestamp){

        try{
            _calendar.setTime(_dataFormat.parse(timestamp));
            return _calendar.get(Calendar.DAY_OF_MONTH);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static int getMonthFromTimestamp(String timestamp){

        try{
            _calendar.setTime(_dataFormat.parse(timestamp));
            return _calendar.get(Calendar.MONTH);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static int getYearFromTimestamp(String timestamp){

        try{
            _calendar.setTime(_dataFormat.parse(timestamp));
            return _calendar.get(Calendar.YEAR);
        }
        catch (Exception e) {
            return -1;
        }
    }

    public static String getTimeFromTimestamp(String timestamp){

        try{
            _calendar.setTime(_dataFormat.parse(timestamp));
            int mins = _calendar.get(Calendar.MINUTE);
            String minutes = (mins < 10) ? "0" + mins : String.valueOf(mins);

            return _calendar.get(Calendar.HOUR_OF_DAY) + ":" + minutes;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getDateFromTimestamp(String timestamp){

        try{
            _calendar.setTime(_dataFormat.parse(timestamp));
            return _calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Constants.LOCALE) + " " + _calendar.get(Calendar.DAY_OF_MONTH);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static String getTimestamp(){
        return _dataFormat.format(new Date());
    }

    public static boolean isToday(String timestamp){

        try{
            _calendar.setTime(_dataFormat.parse(timestamp));
        }
        catch (Exception e) {
            return false;
        }

        int day   = _calendar.get(Calendar.DAY_OF_MONTH);
        int month = _calendar.get(Calendar.MONTH);
        int year  = _calendar.get(Calendar.YEAR);

        _calendar.setTime(new Date());

        return day == _calendar.get(Calendar.DAY_OF_MONTH) &&
                month == _calendar.get(Calendar.MONTH) &&
                year  == _calendar.get(Calendar.YEAR);
    }
}