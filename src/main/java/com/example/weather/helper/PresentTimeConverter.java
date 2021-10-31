package com.example.weather.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

public class PresentTimeConverter {
    public static long unixPresentTimeConverter(LocalDate date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date format = formatter.parse(String.valueOf(date));
        long unixTime = format.getTime();
        return unixTime;
    }
}
