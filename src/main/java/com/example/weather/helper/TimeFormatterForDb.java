package com.example.weather.helper;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class TimeFormatterForDb {
    public static String timeFormatterForDb(long date) {
        Instant instant = Instant.ofEpochSecond(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date myDate = Date.from(instant);
        return formatter.format(myDate);
    }
}
