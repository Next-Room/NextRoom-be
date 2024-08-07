package com.nextroom.nextRoomServer.util;

import java.util.Base64;

public class Base64Decoder {
    public static String decode(String base64Data) {
        // Base64 디코딩
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        String decodedString = new String(decodedBytes);

        return decodedString;
    }
}
