package com.sample.thesis17.mytimeapp.Static;


import android.graphics.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by kimz on 2017-09-07.
 */

public class MyMath {
    public static long LONG_HOUR_MILLIS = 60*60*1000;
    public static long LONG_MIN_MILLIS = 60*1000;
    public static long LONG_DAY_MILLIS = 60*60*24*1000;
    public static long LONG_WEEK_MILLIS = 60*60*24*7*1000;
    public static long LONG_GROUP_INTERVAL = LONG_MIN_MILLIS * 10;  //10분 내면 같은 group

    public static long LONG_LOCALE_MINUS_KOREA = -1 * LONG_HOUR_MILLIS * 9; //LOCALE_KOREA : GMT + 09:00
    public static long LONG_LOCALE_PLUS_KOREA_MODULO_WEEK = LONG_WEEK_MILLIS + (-1 * LONG_HOUR_MILLIS * 9); //LOCALE_KOREA : GMT + 09:00
    public static long LONG_LOCALE_PLUS_KOREA_MODULO_DAY = LONG_DAY_MILLIS + (-1 * LONG_HOUR_MILLIS * 9); //LOCALE_KOREA : GMT + 09:00

    public static String[] WEEK_STRING = {"일", "월", "화", "수", "목", "금", "토"};
    public static String[] WEEK_STRING_REAL = {"수", "목", "금", "토", "일", "월", "화"};

    public static double OUT_RADIUS_INCDEC = 0.000000003;
    public static double IN_RADIUS_INCDEC = 0.000000002;

    public static long MAX_TIME_INCDEC = 60*1000 * 10;  //10 min
    public static long DIV_TIME_INCDEC = 10;    // 1/10 반영

    public static double CUSTOM_DRADIUS = 0.00000004;  // 0.0.0000000676// 0.000187 공학관 한동, 운동장크기;

    public static double DOUBLE_GROUPING_2POW_RADIUS = 0.000000035;  // groupping 기준
    public static double MINDIST_TO_GROUP_TO_MAKRER = 0.00000025;



    public static int getContrastColor(int color) {
        double y = (299 * Color.red(color) + 587 * Color.green(color) + 114 * Color.blue(color)) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }

    public static double doubleRound(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    // https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
}
