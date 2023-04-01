package com.thullo.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Helper {

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
        String fileUrl = imageUrl.substring(imageUrl.indexOf("files/") + 6);
        return fileUrl.split("\\.")[0];
    }

    public static boolean isOnServer(String envName) {
        return envName.trim().equals("server");
    }

    public static String formatStatus(String status){
       return status.replaceAll("\\s+", "_").toUpperCase();
    }
}