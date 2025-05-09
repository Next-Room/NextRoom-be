package com.nextroom.nextRoomServer.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Decoder {
    public static String decode(String base64Data) {
        if (base64Data == null) {
            return null;
        }
        // Base64 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

        return decodedString;
    }
}
