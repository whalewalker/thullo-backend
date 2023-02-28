package com.thullo.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Helper {


    private Helper() {
    }

    public static boolean isValidToken(LocalDateTime expiryDate) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiryDate);
        return minutes >= 0;
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static String calculateFileSize(long size) {
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static String extractFileIdFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf("files/") + 6);
    }

}
