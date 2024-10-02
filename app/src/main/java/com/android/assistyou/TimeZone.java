package com.android.assistyou;

import java.time.LocalDateTime;

public class TimeZone {


    // Method Return Greet With Respect To the Current Time
    public static String getCurrentTime()
    {
        int hour = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hour = LocalDateTime.now().getHour();
        }

        if (hour >= 0 && hour < 12) {
            return "Good morning!";
        } else if (hour >= 12 && hour < 18) {
            return "Good afternoon!";
        } else {

            return "Good evening!";
        }
    }
}
