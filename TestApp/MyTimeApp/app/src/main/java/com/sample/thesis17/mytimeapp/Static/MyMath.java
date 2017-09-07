package com.sample.thesis17.mytimeapp.Static;


import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by kimz on 2017-09-07.
 */

public class MyMath {
    public static double doubleRound(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    // https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
}
