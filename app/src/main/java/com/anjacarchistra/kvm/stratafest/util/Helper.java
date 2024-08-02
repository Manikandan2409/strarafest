package com.anjacarchistra.kvm.stratafest.util;

import android.util.Base64;

public class Helper {
    // Encode method
    public static String encode(String originalValue) {
        String encodedString;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodedString = java.util.Base64.getEncoder().encodeToString(originalValue.getBytes());
        } else {
            encodedString = Base64.encodeToString(originalValue.getBytes(), Base64.DEFAULT);
        }
        return encodedString;
    }

    // Decode method
    public static String decode(String encodedValue) {
        byte[] decodedBytes;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            decodedBytes = java.util.Base64.getDecoder().decode(encodedValue);
        } else {
            decodedBytes = Base64.decode(encodedValue, Base64.DEFAULT);
        }
        return new String(decodedBytes);
    }
}
