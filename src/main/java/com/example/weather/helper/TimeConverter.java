package com.example.weather.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeConverter {
    public static long unixTimeConverter(String date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date format = formatter.parse(date);
        long unixTime = format.getTime();
        return unixTime;
    }
}
