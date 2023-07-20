package com.nextroom.oescape.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nextroom.oescape.exceptions.CustomException;
import com.nextroom.oescape.exceptions.StatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(StatusCode.INVALID_REFRESH_TOKEN);
        }

        return Long.parseLong(authentication.getName());
    }

    public static Long getRequestedShopId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getPrincipal().toString());
    }
}
