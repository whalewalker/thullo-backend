package com.thullo.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Helper {


    private Helper() {}

    public static boolean isValidToken(LocalDateTime expiryDate) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiryDate);
        return minutes >= 0;
    }

    public  static boolean isNullOrEmpty(String  value){
        return value == null || value.trim().length() == 0 ;
    }



}
