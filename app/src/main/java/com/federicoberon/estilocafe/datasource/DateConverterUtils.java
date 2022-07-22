package com.federicoberon.estilocafe.datasource;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class DateConverterUtils {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Date toDate(Calendar calendar) {
        return calendar == null ? null : calendar.getTime();
    }

    @TypeConverter
    public static Calendar toCalendar(Date date) {
        if(date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
