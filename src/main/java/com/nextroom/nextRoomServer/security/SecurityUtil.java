package com.nextroom.nextRoomServer.security;

import static com.nextroom.nextRoomServer.exceptions.StatusCode.*;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nextroom.nextRoomServer.exceptions.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentShopId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        }

        return Long.parseLong(authentication.getName());
    }
}
