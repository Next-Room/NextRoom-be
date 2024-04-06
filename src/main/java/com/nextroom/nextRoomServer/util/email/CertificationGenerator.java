package com.nextroom.nextRoomServer.util.email;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.nextroom.nextRoomServer.exceptions.CustomException;

@Component
public class CertificationGenerator {

    public String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(BAD_REQUEST);
        }
    }
}
