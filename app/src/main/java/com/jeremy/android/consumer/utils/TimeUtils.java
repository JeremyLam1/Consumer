package com.jeremy.android.consumer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jeremy on 2016/4/6.
 */
public class TimeUtils {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd";

    public static String getFormatByTimeStamp(Long timestamp) {
        return new SimpleDateFormat(TIMESTAMP_FORMAT).format(new Date(
                timestamp));
    }

    public static String getDateFormatByTimeStamp(Long timestamp) {
        return new SimpleDateFormat(TIMESTAMP_DATE_FORMAT).format(new Date(
                timestamp));
    }
}
